/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.VertexSorter;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.ChunkRenderTaskScheduler;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.thread.NameableExecutor;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder {
    private final ChunkRenderTaskScheduler scheduler = new ChunkRenderTaskScheduler();
    private final Queue<Runnable> uploadQueue = Queues.newConcurrentLinkedQueue();
    final BlockBufferAllocatorStorage buffers;
    private final BlockBufferBuilderPool buffersPool;
    private volatile int queuedTaskCount;
    private volatile boolean stopped;
    private final SimpleConsecutiveExecutor consecutiveExecutor;
    private final NameableExecutor executor;
    ClientWorld world;
    final WorldRenderer worldRenderer;
    private Vec3d cameraPosition = Vec3d.ZERO;
    final SectionBuilder sectionBuilder;

    public ChunkBuilder(ClientWorld world, WorldRenderer worldRenderer, NameableExecutor executor, BufferBuilderStorage bufferBuilderStorage, BlockRenderManager blockRenderManager, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        this.buffers = bufferBuilderStorage.getBlockBufferBuilders();
        this.buffersPool = bufferBuilderStorage.getBlockBufferBuildersPool();
        this.executor = executor;
        this.consecutiveExecutor = new SimpleConsecutiveExecutor(executor, "Section Renderer");
        this.consecutiveExecutor.send(this::scheduleRunTasks);
        this.sectionBuilder = new SectionBuilder(blockRenderManager, blockEntityRenderDispatcher);
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }

    private void scheduleRunTasks() {
        if (this.stopped || this.buffersPool.hasNoAvailableBuilder()) {
            return;
        }
        BuiltChunk.Task lv = this.scheduler.dequeueNearest(this.getCameraPosition());
        if (lv == null) {
            return;
        }
        BlockBufferAllocatorStorage lv2 = Objects.requireNonNull(this.buffersPool.acquire());
        this.queuedTaskCount = this.scheduler.size();
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> lv.run(lv2), this.executor.named(lv.getName())).thenCompose(future -> future)).whenComplete((result, throwable) -> {
            if (throwable != null) {
                MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Batching sections"));
                return;
            }
            arg.finished.set(true);
            this.consecutiveExecutor.send(() -> {
                if (result == Result.SUCCESSFUL) {
                    lv2.clear();
                } else {
                    lv2.reset();
                }
                this.buffersPool.release(lv2);
                this.scheduleRunTasks();
            });
        });
    }

    public String getDebugString() {
        return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.buffersPool.getAvailableBuilderCount());
    }

    public int getToBatchCount() {
        return this.queuedTaskCount;
    }

    public int getChunksToUpload() {
        return this.uploadQueue.size();
    }

    public int getFreeBufferCount() {
        return this.buffersPool.getAvailableBuilderCount();
    }

    public void setCameraPosition(Vec3d cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    public void upload() {
        Runnable runnable;
        while ((runnable = this.uploadQueue.poll()) != null) {
            runnable.run();
        }
    }

    public void rebuild(BuiltChunk chunk, ChunkRendererRegionBuilder builder) {
        chunk.rebuild(builder);
    }

    public void reset() {
        this.clear();
    }

    public void send(BuiltChunk.Task task) {
        if (this.stopped) {
            return;
        }
        this.consecutiveExecutor.send(() -> {
            if (this.stopped) {
                return;
            }
            this.scheduler.enqueue(task);
            this.queuedTaskCount = this.scheduler.size();
            this.scheduleRunTasks();
        });
    }

    public CompletableFuture<Void> scheduleUpload(BuiltBuffer builtBuffer, VertexBuffer glBuffer) {
        if (this.stopped) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            if (glBuffer.isClosed()) {
                builtBuffer.close();
                return;
            }
            try (ScopedProfiler lv = Profilers.get().scoped("Upload Section Layer");){
                glBuffer.bind();
                glBuffer.upload(builtBuffer);
                VertexBuffer.unbind();
            }
        }, this.uploadQueue::add);
    }

    public CompletableFuture<Void> scheduleIndexBufferUpload(BufferAllocator.CloseableBuffer indexBuffer, VertexBuffer vertexBuffer) {
        if (this.stopped) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            if (vertexBuffer.isClosed()) {
                indexBuffer.close();
                return;
            }
            try (ScopedProfiler lv = Profilers.get().scoped("Upload Section Indices");){
                vertexBuffer.bind();
                vertexBuffer.uploadIndexBuffer(indexBuffer);
                VertexBuffer.unbind();
            }
        }, this.uploadQueue::add);
    }

    private void clear() {
        this.scheduler.cancelAll();
        this.queuedTaskCount = 0;
    }

    public boolean isEmpty() {
        return this.queuedTaskCount == 0 && this.uploadQueue.isEmpty();
    }

    public void stop() {
        this.stopped = true;
        this.clear();
        this.upload();
    }

    @Environment(value=EnvType.CLIENT)
    public class BuiltChunk {
        public static final int field_32832 = 16;
        public final int index;
        public final AtomicReference<ChunkData> data = new AtomicReference<ChunkData>(ChunkData.UNPROCESSED);
        public final AtomicReference<NormalizedRelativePos> relativePos = new AtomicReference<Object>(null);
        @Nullable
        private RebuildTask rebuildTask;
        @Nullable
        private SortTask sortTask;
        private final Set<BlockEntity> blockEntities = Sets.newHashSet();
        private final Map<RenderLayer, VertexBuffer> buffers = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(layer -> layer, layer -> new VertexBuffer(GlUsage.STATIC_WRITE)));
        private Box boundingBox;
        private boolean needsRebuild = true;
        long sectionPos = ChunkSectionPos.asLong(-1, -1, -1);
        final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
        private boolean needsImportantRebuild;

        public BuiltChunk(int index, long sectionPos) {
            this.index = index;
            this.setSectionPos(sectionPos);
        }

        private boolean isChunkNonEmpty(long sectionPos) {
            Chunk lv = ChunkBuilder.this.world.getChunk(ChunkSectionPos.unpackX(sectionPos), ChunkSectionPos.unpackZ(sectionPos), ChunkStatus.FULL, false);
            return lv != null && ChunkBuilder.this.world.getLightingProvider().isLightingEnabled(ChunkSectionPos.withZeroY(sectionPos));
        }

        public boolean shouldBuild() {
            int i = 24;
            if (this.getSquaredCameraDistance() > 576.0) {
                return this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.WEST)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.NORTH)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.EAST)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.SOUTH)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, -1, 0, -1)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, -1, 0, 1)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, 1, 0, -1)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, 1, 0, 1));
            }
            return true;
        }

        public Box getBoundingBox() {
            return this.boundingBox;
        }

        public VertexBuffer getBuffer(RenderLayer layer) {
            return this.buffers.get(layer);
        }

        public void setSectionPos(long sectionPos) {
            this.clear();
            this.sectionPos = sectionPos;
            int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(sectionPos));
            int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(sectionPos));
            int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(sectionPos));
            this.origin.set(i, j, k);
            this.boundingBox = new Box(i, j, k, i + 16, j + 16, k + 16);
        }

        protected double getSquaredCameraDistance() {
            Camera lv = MinecraftClient.getInstance().gameRenderer.getCamera();
            double d = this.boundingBox.minX + 8.0 - lv.getPos().x;
            double e = this.boundingBox.minY + 8.0 - lv.getPos().y;
            double f = this.boundingBox.minZ + 8.0 - lv.getPos().z;
            return d * d + e * e + f * f;
        }

        public ChunkData getData() {
            return this.data.get();
        }

        private void clear() {
            this.cancel();
            this.data.set(ChunkData.UNPROCESSED);
            this.relativePos.set(null);
            this.needsRebuild = true;
        }

        public void delete() {
            this.clear();
            this.buffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public long getSectionPos() {
            return this.sectionPos;
        }

        public void scheduleRebuild(boolean important) {
            boolean bl2 = this.needsRebuild;
            this.needsRebuild = true;
            this.needsImportantRebuild = important | (bl2 && this.needsImportantRebuild);
        }

        public void cancelRebuild() {
            this.needsRebuild = false;
            this.needsImportantRebuild = false;
        }

        public boolean needsRebuild() {
            return this.needsRebuild;
        }

        public boolean needsImportantRebuild() {
            return this.needsRebuild && this.needsImportantRebuild;
        }

        public long getOffsetSectionPos(Direction direction) {
            return ChunkSectionPos.offset(this.sectionPos, direction);
        }

        public void scheduleSort(ChunkBuilder builder) {
            this.sortTask = new SortTask(this.getData());
            builder.send(this.sortTask);
        }

        public boolean hasTranslucentLayer() {
            return this.getData().nonEmptyLayers.contains(RenderLayer.getTranslucent());
        }

        public boolean isCurrentlySorting() {
            return this.sortTask != null && !this.sortTask.finished.get();
        }

        protected void cancel() {
            if (this.rebuildTask != null) {
                this.rebuildTask.cancel();
                this.rebuildTask = null;
            }
            if (this.sortTask != null) {
                this.sortTask.cancel();
                this.sortTask = null;
            }
        }

        public Task createRebuildTask(ChunkRendererRegionBuilder builder) {
            this.cancel();
            ChunkRendererRegion lv = builder.build(ChunkBuilder.this.world, ChunkSectionPos.from(this.sectionPos));
            boolean bl = this.data.get() != ChunkData.UNPROCESSED;
            this.rebuildTask = new RebuildTask(lv, bl);
            return this.rebuildTask;
        }

        public void scheduleRebuild(ChunkBuilder chunkRenderer, ChunkRendererRegionBuilder builder) {
            Task lv = this.createRebuildTask(builder);
            chunkRenderer.send(lv);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void setNoCullingBlockEntities(Collection<BlockEntity> blockEntities) {
            HashSet<BlockEntity> set2;
            HashSet<BlockEntity> set = Sets.newHashSet(blockEntities);
            Set<BlockEntity> set3 = this.blockEntities;
            synchronized (set3) {
                set2 = Sets.newHashSet(this.blockEntities);
                set.removeAll(this.blockEntities);
                set2.removeAll(blockEntities);
                this.blockEntities.clear();
                this.blockEntities.addAll(blockEntities);
            }
            ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set2, set);
        }

        public void rebuild(ChunkRendererRegionBuilder builder) {
            Task lv = this.createRebuildTask(builder);
            lv.run(ChunkBuilder.this.buffers);
        }

        void setData(ChunkData chunkData) {
            this.data.set(chunkData);
            ChunkBuilder.this.worldRenderer.addBuiltChunk(this);
        }

        VertexSorter getVertexSorter() {
            Vec3d lv = ChunkBuilder.this.getCameraPosition();
            return VertexSorter.byDistance((float)(lv.x - (double)this.origin.getX()), (float)(lv.y - (double)this.origin.getY()), (float)(lv.z - (double)this.origin.getZ()));
        }

        @Environment(value=EnvType.CLIENT)
        class SortTask
        extends Task {
            private final ChunkData data;

            public SortTask(ChunkData data) {
                super(true);
                this.data = data;
            }

            @Override
            protected String getName() {
                return "rend_chk_sort";
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferAllocatorStorage buffers) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                BuiltBuffer.SortState lv = this.data.transparentSortingData;
                if (lv == null || this.data.isEmpty(RenderLayer.getTranslucent())) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                VertexSorter lv2 = BuiltChunk.this.getVertexSorter();
                NormalizedRelativePos lv3 = NormalizedRelativePos.of(ChunkBuilder.this.getCameraPosition(), BuiltChunk.this.sectionPos);
                if (lv3.equals(BuiltChunk.this.relativePos.get()) && !lv3.isOnCameraAxis()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                BufferAllocator.CloseableBuffer lv4 = lv.sortAndStore(buffers.get(RenderLayer.getTranslucent()), lv2);
                if (lv4 == null) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    lv4.close();
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                CompletionStage completableFuture = ChunkBuilder.this.scheduleIndexBufferUpload(lv4, BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())).thenApply(v -> Result.CANCELLED);
                return ((CompletableFuture)completableFuture).handle((result, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
                    }
                    if (this.cancelled.get()) {
                        return Result.CANCELLED;
                    }
                    BuiltChunk.this.relativePos.set(lv3);
                    return Result.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.cancelled.set(true);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public abstract class Task {
            protected final AtomicBoolean cancelled = new AtomicBoolean(false);
            protected final AtomicBoolean finished = new AtomicBoolean(false);
            protected final boolean prioritized;

            public Task(boolean prioritized) {
                this.prioritized = prioritized;
            }

            public abstract CompletableFuture<Result> run(BlockBufferAllocatorStorage var1);

            public abstract void cancel();

            protected abstract String getName();

            public boolean isPrioritized() {
                return this.prioritized;
            }

            public BlockPos getOrigin() {
                return BuiltChunk.this.origin;
            }
        }

        @Environment(value=EnvType.CLIENT)
        class RebuildTask
        extends Task {
            @Nullable
            protected volatile ChunkRendererRegion region;

            public RebuildTask(ChunkRendererRegion region, boolean prioritized) {
                super(prioritized);
                this.region = region;
            }

            @Override
            protected String getName() {
                return "rend_chk_rebuild";
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferAllocatorStorage buffers) {
                SectionBuilder.RenderData lv4;
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                ChunkRendererRegion lv = this.region;
                this.region = null;
                if (lv == null) {
                    BuiltChunk.this.setData(ChunkData.EMPTY);
                    return CompletableFuture.completedFuture(Result.SUCCESSFUL);
                }
                ChunkSectionPos lv2 = ChunkSectionPos.from(BuiltChunk.this.origin);
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                try (ScopedProfiler lv3 = Profilers.get().scoped("Compile Section");){
                    lv4 = ChunkBuilder.this.sectionBuilder.build(lv2, lv, BuiltChunk.this.getVertexSorter(), buffers);
                }
                NormalizedRelativePos lv5 = NormalizedRelativePos.of(ChunkBuilder.this.getCameraPosition(), BuiltChunk.this.sectionPos);
                BuiltChunk.this.setNoCullingBlockEntities(lv4.noCullingBlockEntities);
                if (this.cancelled.get()) {
                    lv4.close();
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                ChunkData lv6 = new ChunkData();
                lv6.occlusionGraph = lv4.chunkOcclusionData;
                lv6.blockEntities.addAll(lv4.blockEntities);
                lv6.transparentSortingData = lv4.translucencySortingData;
                ArrayList list = new ArrayList(lv4.buffers.size());
                lv4.buffers.forEach((renderLayer, buffer) -> {
                    list.add(ChunkBuilder.this.scheduleUpload((BuiltBuffer)buffer, BuiltChunk.this.getBuffer((RenderLayer)renderLayer)));
                    arg.nonEmptyLayers.add((RenderLayer)renderLayer);
                });
                return Util.combine(list).handle((v, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
                    }
                    if (this.cancelled.get()) {
                        return Result.CANCELLED;
                    }
                    BuiltChunk.this.setData(lv6);
                    BuiltChunk.this.relativePos.set(lv5);
                    return Result.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.cancelled.compareAndSet(false, true)) {
                    BuiltChunk.this.scheduleRebuild(false);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        SUCCESSFUL,
        CANCELLED;

    }

    @Environment(value=EnvType.CLIENT)
    public static class ChunkData {
        public static final ChunkData UNPROCESSED = new ChunkData(){

            @Override
            public boolean isVisibleThrough(Direction from, Direction to) {
                return false;
            }
        };
        public static final ChunkData EMPTY = new ChunkData(){

            @Override
            public boolean isVisibleThrough(Direction from, Direction to) {
                return true;
            }
        };
        final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet<RenderLayer>(RenderLayer.getBlockLayers().size());
        final List<BlockEntity> blockEntities = Lists.newArrayList();
        ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
        @Nullable
        BuiltBuffer.SortState transparentSortingData;

        public boolean hasNonEmptyLayers() {
            return !this.nonEmptyLayers.isEmpty();
        }

        public boolean isEmpty(RenderLayer layer) {
            return !this.nonEmptyLayers.contains(layer);
        }

        public List<BlockEntity> getBlockEntities() {
            return this.blockEntities;
        }

        public boolean isVisibleThrough(Direction from, Direction to) {
            return this.occlusionGraph.isVisibleThrough(from, to);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class NormalizedRelativePos {
        private int x;
        private int y;
        private int z;

        public static NormalizedRelativePos of(Vec3d cameraPos, long sectionPos) {
            return new NormalizedRelativePos().with(cameraPos, sectionPos);
        }

        public NormalizedRelativePos with(Vec3d cameraPos, long sectionPos) {
            this.x = NormalizedRelativePos.normalize(cameraPos.getX(), ChunkSectionPos.unpackX(sectionPos));
            this.y = NormalizedRelativePos.normalize(cameraPos.getY(), ChunkSectionPos.unpackY(sectionPos));
            this.z = NormalizedRelativePos.normalize(cameraPos.getZ(), ChunkSectionPos.unpackZ(sectionPos));
            return this;
        }

        private static int normalize(double cameraCoord, int sectionCoord) {
            int j = ChunkSectionPos.getSectionCoordFloored(cameraCoord) - sectionCoord;
            return MathHelper.clamp(j, -1, 1);
        }

        public boolean isOnCameraAxis() {
            return this.x == 0 || this.y == 0 || this.z == 0;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof NormalizedRelativePos) {
                NormalizedRelativePos lv = (NormalizedRelativePos)o;
                return this.x == lv.x && this.y == lv.y && this.z == lv.z;
            }
            return false;
        }
    }
}

