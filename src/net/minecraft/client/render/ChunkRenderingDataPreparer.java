/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HeightLimitView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderingDataPreparer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int field_45619 = 60;
    private static final double CHUNK_INNER_DIAGONAL_LENGTH = Math.ceil(Math.sqrt(3.0) * 16.0);
    private boolean terrainUpdateScheduled = true;
    @Nullable
    private Future<?> terrainUpdateFuture;
    @Nullable
    private BuiltChunkStorage builtChunkStorage;
    private final AtomicReference<PreparerState> state = new AtomicReference();
    private final AtomicReference<Events> events = new AtomicReference();
    private final AtomicBoolean field_45626 = new AtomicBoolean(false);

    public void setStorage(@Nullable BuiltChunkStorage storage) {
        if (this.terrainUpdateFuture != null) {
            try {
                this.terrainUpdateFuture.get();
                this.terrainUpdateFuture = null;
            } catch (Exception exception) {
                LOGGER.warn("Full update failed", exception);
            }
        }
        this.builtChunkStorage = storage;
        if (storage != null) {
            this.state.set(new PreparerState(storage));
            this.scheduleTerrainUpdate();
        } else {
            this.state.set(null);
        }
    }

    public void scheduleTerrainUpdate() {
        this.terrainUpdateScheduled = true;
    }

    public void collectChunks(Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, List<ChunkBuilder.BuiltChunk> nearbyChunks) {
        this.state.get().storage().octree.visit((node, skipVisibilityCheck, depth, nearCenter) -> {
            ChunkBuilder.BuiltChunk lv = node.getBuiltChunk();
            if (lv != null) {
                builtChunks.add(lv);
                if (nearCenter) {
                    nearbyChunks.add(lv);
                }
            }
        }, frustum, 32);
    }

    public boolean method_52836() {
        return this.field_45626.compareAndSet(true, false);
    }

    public void addNeighbors(ChunkPos chunkPos) {
        Events lv2;
        Events lv = this.events.get();
        if (lv != null) {
            this.addNeighbors(lv, chunkPos);
        }
        if ((lv2 = this.state.get().events) != lv) {
            this.addNeighbors(lv2, chunkPos);
        }
    }

    public void schedulePropagationFrom(ChunkBuilder.BuiltChunk builtChunk) {
        Events lv2;
        Events lv = this.events.get();
        if (lv != null) {
            lv.sectionsToPropagateFrom.add(builtChunk);
        }
        if ((lv2 = this.state.get().events) != lv) {
            lv2.sectionsToPropagateFrom.add(builtChunk);
        }
    }

    public void updateSectionOcclusionGraph(boolean cullChunks, Camera camera, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunk, LongOpenHashSet activeSections) {
        Vec3d lv = camera.getPos();
        if (this.terrainUpdateScheduled && (this.terrainUpdateFuture == null || this.terrainUpdateFuture.isDone())) {
            this.updateTerrain(cullChunks, camera, lv, activeSections);
        }
        this.method_52835(cullChunks, frustum, builtChunk, lv, activeSections);
    }

    private void updateTerrain(boolean cullChunks, Camera camera, Vec3d cameraPos, LongOpenHashSet activeSections) {
        this.terrainUpdateScheduled = false;
        LongOpenHashSet longOpenHashSet2 = activeSections.clone();
        this.terrainUpdateFuture = CompletableFuture.runAsync(() -> {
            PreparerState lv = new PreparerState(this.builtChunkStorage);
            this.events.set(lv.events);
            ArrayDeque<ChunkInfo> queue = Queues.newArrayDeque();
            this.method_52821(camera, queue);
            queue.forEach(info -> arg.storage.infoList.setInfo(info.chunk, (ChunkInfo)info));
            this.method_52825(lv.storage, cameraPos, queue, cullChunks, arg -> {}, longOpenHashSet2);
            this.state.set(lv);
            this.events.set(null);
            this.field_45626.set(true);
        }, Util.getMainWorkerExecutor());
    }

    private void method_52835(boolean bl, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, Vec3d cameraPos, LongOpenHashSet activeSections) {
        PreparerState lv = this.state.get();
        this.method_52823(lv);
        if (!lv.events.sectionsToPropagateFrom.isEmpty()) {
            ArrayDeque<ChunkInfo> queue = Queues.newArrayDeque();
            while (!lv.events.sectionsToPropagateFrom.isEmpty()) {
                ChunkBuilder.BuiltChunk lv2 = (ChunkBuilder.BuiltChunk)lv.events.sectionsToPropagateFrom.poll();
                ChunkInfo lv3 = lv.storage.infoList.getInfo(lv2);
                if (lv3 == null || lv3.chunk != lv2) continue;
                queue.add(lv3);
            }
            Frustum lv4 = WorldRenderer.offsetFrustum(frustum);
            Consumer<ChunkBuilder.BuiltChunk> consumer = arg2 -> {
                if (lv4.isVisible(arg2.getBoundingBox())) {
                    this.field_45626.set(true);
                }
            };
            this.method_52825(lv.storage, cameraPos, queue, bl, consumer, activeSections);
        }
    }

    private void method_52823(PreparerState arg) {
        LongIterator longIterator = arg.events.chunksWhichReceivedNeighbors.iterator();
        while (longIterator.hasNext()) {
            long l = longIterator.nextLong();
            List list = (List)arg.storage.field_45628.get(l);
            if (list == null || !((ChunkBuilder.BuiltChunk)list.get(0)).shouldBuild()) continue;
            arg.events.sectionsToPropagateFrom.addAll(list);
            arg.storage.field_45628.remove(l);
        }
        arg.events.chunksWhichReceivedNeighbors.clear();
    }

    private void addNeighbors(Events events, ChunkPos chunkPos) {
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z - 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z + 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z - 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z + 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z - 1));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z + 1));
    }

    private void method_52821(Camera camera, Queue<ChunkInfo> queue) {
        BlockPos lv = camera.getBlockPos();
        long l = ChunkSectionPos.toLong(lv);
        int i = ChunkSectionPos.unpackY(l);
        ChunkBuilder.BuiltChunk lv2 = this.builtChunkStorage.getRenderedChunk(l);
        if (lv2 == null) {
            HeightLimitView lv3 = this.builtChunkStorage.getWorld();
            boolean bl = i < lv3.getBottomSectionCoord();
            int j = bl ? lv3.getBottomSectionCoord() : lv3.getTopSectionCoord();
            int k = this.builtChunkStorage.getViewDistance();
            ArrayList<ChunkInfo> list = Lists.newArrayList();
            int m = ChunkSectionPos.unpackX(l);
            int n = ChunkSectionPos.unpackZ(l);
            for (int o = -k; o <= k; ++o) {
                for (int p = -k; p <= k; ++p) {
                    ChunkBuilder.BuiltChunk lv4 = this.builtChunkStorage.getRenderedChunk(ChunkSectionPos.asLong(o + m, j, p + n));
                    if (lv4 == null || !this.isWithinViewDistance(l, lv4.getSectionPos())) continue;
                    Direction lv5 = bl ? Direction.UP : Direction.DOWN;
                    ChunkInfo lv6 = new ChunkInfo(lv4, lv5, 0);
                    lv6.updateCullingState(lv6.cullingState, lv5);
                    if (o > 0) {
                        lv6.updateCullingState(lv6.cullingState, Direction.EAST);
                    } else if (o < 0) {
                        lv6.updateCullingState(lv6.cullingState, Direction.WEST);
                    }
                    if (p > 0) {
                        lv6.updateCullingState(lv6.cullingState, Direction.SOUTH);
                    } else if (p < 0) {
                        lv6.updateCullingState(lv6.cullingState, Direction.NORTH);
                    }
                    list.add(lv6);
                }
            }
            list.sort(Comparator.comparingDouble(arg2 -> lv.getSquaredDistance(arg2.chunk.getOrigin().add(8, 8, 8))));
            queue.addAll(list);
        } else {
            queue.add(new ChunkInfo(lv2, null, 0));
        }
    }

    private void method_52825(RenderableChunks arg, Vec3d pos, Queue<ChunkInfo> queue, boolean cullChunks, Consumer<ChunkBuilder.BuiltChunk> consumer, LongOpenHashSet longOpenHashSet) {
        int i = 16;
        BlockPos lv = new BlockPos(MathHelper.floor(pos.x / 16.0) * 16, MathHelper.floor(pos.y / 16.0) * 16, MathHelper.floor(pos.z / 16.0) * 16);
        long l2 = ChunkSectionPos.toLong(lv);
        BlockPos lv2 = lv.add(8, 8, 8);
        while (!queue.isEmpty()) {
            ChunkInfo lv3 = queue.poll();
            ChunkBuilder.BuiltChunk lv4 = lv3.chunk;
            if (!longOpenHashSet.contains(lv3.chunk.getSectionPos())) {
                if (arg.octree.add(lv3.chunk)) {
                    consumer.accept(lv3.chunk);
                }
            } else {
                lv3.chunk.data.compareAndSet(ChunkBuilder.ChunkData.UNPROCESSED, ChunkBuilder.ChunkData.EMPTY);
            }
            boolean bl2 = Math.abs(lv4.getOrigin().getX() - lv.getX()) > 60 || Math.abs(lv4.getOrigin().getY() - lv.getY()) > 60 || Math.abs(lv4.getOrigin().getZ() - lv.getZ()) > 60;
            for (Direction lv5 : DIRECTIONS) {
                ChunkInfo lv14;
                ChunkBuilder.BuiltChunk lv6 = this.getRenderedChunk(l2, lv4, lv5);
                if (lv6 == null || cullChunks && lv3.canCull(lv5.getOpposite())) continue;
                if (cullChunks && lv3.hasAnyDirection()) {
                    ChunkBuilder.ChunkData lv7 = lv4.getData();
                    boolean bl3 = false;
                    for (int j = 0; j < DIRECTIONS.length; ++j) {
                        if (!lv3.hasDirection(j) || !lv7.isVisibleThrough(DIRECTIONS[j].getOpposite(), lv5)) continue;
                        bl3 = true;
                        break;
                    }
                    if (!bl3) continue;
                }
                if (cullChunks && bl2) {
                    BlockPos lv8 = lv6.getOrigin();
                    BlockPos lv9 = lv8.add((lv5.getAxis() == Direction.Axis.X ? lv2.getX() > lv8.getX() : lv2.getX() < lv8.getX()) ? 16 : 0, (lv5.getAxis() == Direction.Axis.Y ? lv2.getY() > lv8.getY() : lv2.getY() < lv8.getY()) ? 16 : 0, (lv5.getAxis() == Direction.Axis.Z ? lv2.getZ() > lv8.getZ() : lv2.getZ() < lv8.getZ()) ? 16 : 0);
                    Vec3d lv10 = new Vec3d(lv9.getX(), lv9.getY(), lv9.getZ());
                    Vec3d lv11 = pos.subtract(lv10).normalize().multiply(CHUNK_INNER_DIAGONAL_LENGTH);
                    boolean bl4 = true;
                    while (pos.subtract(lv10).lengthSquared() > 3600.0) {
                        lv10 = lv10.add(lv11);
                        HeightLimitView lv12 = this.builtChunkStorage.getWorld();
                        if (lv10.y > (double)lv12.getTopYInclusive() || lv10.y < (double)lv12.getBottomY()) break;
                        ChunkBuilder.BuiltChunk lv13 = this.builtChunkStorage.getRenderedChunk(BlockPos.ofFloored(lv10.x, lv10.y, lv10.z));
                        if (lv13 != null && arg.infoList.getInfo(lv13) != null) continue;
                        bl4 = false;
                        break;
                    }
                    if (!bl4) continue;
                }
                if ((lv14 = arg.infoList.getInfo(lv6)) != null) {
                    lv14.addDirection(lv5);
                    continue;
                }
                ChunkInfo lv15 = new ChunkInfo(lv6, lv5, lv3.propagationLevel + 1);
                lv15.updateCullingState(lv3.cullingState, lv5);
                if (lv6.shouldBuild()) {
                    queue.add(lv15);
                    arg.infoList.setInfo(lv6, lv15);
                    continue;
                }
                if (!this.isWithinViewDistance(l2, lv6.getSectionPos())) continue;
                arg.infoList.setInfo(lv6, lv15);
                arg.field_45628.computeIfAbsent(ChunkPos.toLong(lv6.getOrigin()), l -> new ArrayList()).add(lv6);
            }
        }
    }

    private boolean isWithinViewDistance(long centerSectionPos, long otherSectionPos) {
        return ChunkFilter.isWithinDistanceExcludingEdge(ChunkSectionPos.unpackX(centerSectionPos), ChunkSectionPos.unpackZ(centerSectionPos), this.builtChunkStorage.getViewDistance(), ChunkSectionPos.unpackX(otherSectionPos), ChunkSectionPos.unpackZ(otherSectionPos));
    }

    @Nullable
    private ChunkBuilder.BuiltChunk getRenderedChunk(long sectionPos, ChunkBuilder.BuiltChunk chunk, Direction direction) {
        long m = chunk.getOffsetSectionPos(direction);
        if (!this.isWithinViewDistance(sectionPos, m)) {
            return null;
        }
        if (MathHelper.abs(ChunkSectionPos.unpackY(sectionPos) - ChunkSectionPos.unpackY(m)) > this.builtChunkStorage.getViewDistance()) {
            return null;
        }
        return this.builtChunkStorage.getRenderedChunk(m);
    }

    @Nullable
    @Debug
    public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
        return this.state.get().storage.infoList.getInfo(chunk);
    }

    public Octree getOctree() {
        return this.state.get().storage.octree;
    }

    @Environment(value=EnvType.CLIENT)
    record PreparerState(RenderableChunks storage, Events events) {
        PreparerState(BuiltChunkStorage storage) {
            this(new RenderableChunks(storage), new Events());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class RenderableChunks {
        public final ChunkInfoList infoList;
        public final Octree octree;
        public final Long2ObjectMap<List<ChunkBuilder.BuiltChunk>> field_45628;

        public RenderableChunks(BuiltChunkStorage storage) {
            this.infoList = new ChunkInfoList(storage.chunks.length);
            this.octree = new Octree(storage.getSectionPos(), storage.getViewDistance(), storage.sizeY, storage.world.getBottomY());
            this.field_45628 = new Long2ObjectOpenHashMap<List<ChunkBuilder.BuiltChunk>>();
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Events(LongSet chunksWhichReceivedNeighbors, BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom) {
        Events() {
            this(new LongOpenHashSet(), new LinkedBlockingQueue<ChunkBuilder.BuiltChunk>());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChunkInfoList {
        private final ChunkInfo[] current;

        ChunkInfoList(int size) {
            this.current = new ChunkInfo[size];
        }

        public void setInfo(ChunkBuilder.BuiltChunk chunk, ChunkInfo info) {
            this.current[chunk.index] = info;
        }

        @Nullable
        public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
            int i = chunk.index;
            if (i < 0 || i >= this.current.length) {
                return null;
            }
            return this.current[i];
        }
    }

    @Environment(value=EnvType.CLIENT)
    @Debug
    public static class ChunkInfo {
        @Debug
        protected final ChunkBuilder.BuiltChunk chunk;
        private byte direction;
        byte cullingState;
        @Debug
        public final int propagationLevel;

        ChunkInfo(ChunkBuilder.BuiltChunk chunk, @Nullable Direction direction, int propagationLevel) {
            this.chunk = chunk;
            if (direction != null) {
                this.addDirection(direction);
            }
            this.propagationLevel = propagationLevel;
        }

        void updateCullingState(byte parentCullingState, Direction from) {
            this.cullingState = (byte)(this.cullingState | (parentCullingState | 1 << from.ordinal()));
        }

        boolean canCull(Direction from) {
            return (this.cullingState & 1 << from.ordinal()) > 0;
        }

        void addDirection(Direction direction) {
            this.direction = (byte)(this.direction | (this.direction | 1 << direction.ordinal()));
        }

        @Debug
        public boolean hasDirection(int ordinal) {
            return (this.direction & 1 << ordinal) > 0;
        }

        boolean hasAnyDirection() {
            return this.direction != 0;
        }

        public int hashCode() {
            return Long.hashCode(this.chunk.getSectionPos());
        }

        public boolean equals(Object o) {
            if (!(o instanceof ChunkInfo)) {
                return false;
            }
            ChunkInfo lv = (ChunkInfo)o;
            return this.chunk.getSectionPos() == lv.chunk.getSectionPos();
        }
    }
}

