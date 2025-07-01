/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.CloudRenderer;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.WorldBorderRendering;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldRenderer
implements SynchronousResourceReloader,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier TRANSPARENCY = Identifier.ofVanilla("transparency");
    private static final Identifier ENTITY_OUTLINE = Identifier.ofVanilla("entity_outline");
    public static final int field_32759 = 16;
    public static final int field_34812 = 8;
    public static final int field_54162 = 32;
    private static final int field_54163 = 15;
    private final MinecraftClient client;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final BufferBuilderStorage bufferBuilders;
    private final SkyRendering skyRendering = new SkyRendering();
    private final CloudRenderer cloudRenderer = new CloudRenderer();
    private final WorldBorderRendering worldBorderRendering = new WorldBorderRendering();
    private final WeatherRendering weatherRendering = new WeatherRendering();
    @Nullable
    private ClientWorld world;
    private final ChunkRenderingDataPreparer chunkRenderingDataPreparer = new ChunkRenderingDataPreparer();
    private final ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks = new ObjectArrayList(10000);
    private final ObjectArrayList<ChunkBuilder.BuiltChunk> nearbyChunks = new ObjectArrayList(50);
    private final Set<BlockEntity> noCullingBlockEntities = Sets.newHashSet();
    @Nullable
    private BuiltChunkStorage chunks;
    private int ticks;
    private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap<BlockBreakingInfo>();
    private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap<SortedSet<BlockBreakingInfo>>();
    @Nullable
    private Framebuffer entityOutlineFramebuffer;
    private final DefaultFramebufferSet framebufferSet = new DefaultFramebufferSet();
    private int cameraChunkX = Integer.MIN_VALUE;
    private int cameraChunkY = Integer.MIN_VALUE;
    private int cameraChunkZ = Integer.MIN_VALUE;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private double lastCameraPitch = Double.MIN_VALUE;
    private double lastCameraYaw = Double.MIN_VALUE;
    @Nullable
    private ChunkBuilder chunkBuilder;
    private int viewDistance = -1;
    private final List<Entity> renderedEntities = new ArrayList<Entity>();
    private int renderedEntitiesCount;
    private Frustum frustum;
    private boolean shouldCaptureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    @Nullable
    private BlockPos prevTranslucencySortCameraPos;
    private int chunkIndex;

    public WorldRenderer(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders) {
        this.client = client;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.bufferBuilders = bufferBuilders;
    }

    public void addWeatherParticlesAndSound(Camera camera) {
        this.weatherRendering.addParticlesAndSound(this.client.world, camera, this.ticks, this.client.options.getParticles().getValue());
    }

    @Override
    public void close() {
        if (this.entityOutlineFramebuffer != null) {
            this.entityOutlineFramebuffer.delete();
        }
        this.skyRendering.close();
        this.cloudRenderer.close();
    }

    @Override
    public void reload(ResourceManager manager) {
        this.loadEntityOutlinePostProcessor();
    }

    public void loadEntityOutlinePostProcessor() {
        if (this.entityOutlineFramebuffer != null) {
            this.entityOutlineFramebuffer.delete();
        }
        this.entityOutlineFramebuffer = new SimpleFramebuffer(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), true);
        this.entityOutlineFramebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Nullable
    private PostEffectProcessor getTransparencyPostEffectProcessor() {
        if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
            return null;
        }
        PostEffectProcessor lv = this.client.getShaderLoader().loadPostEffect(TRANSPARENCY, DefaultFramebufferSet.STAGES);
        if (lv == null) {
            this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
            this.client.options.write();
        }
        return lv;
    }

    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            this.entityOutlineFramebuffer.drawInternal(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    protected boolean canDrawEntityOutlines() {
        return !this.client.gameRenderer.isRenderingPanorama() && this.entityOutlineFramebuffer != null && this.client.player != null;
    }

    public void setWorld(@Nullable ClientWorld world) {
        this.cameraChunkX = Integer.MIN_VALUE;
        this.cameraChunkY = Integer.MIN_VALUE;
        this.cameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setWorld(world);
        this.world = world;
        if (world != null) {
            this.reload();
        } else {
            if (this.chunks != null) {
                this.chunks.clear();
                this.chunks = null;
            }
            if (this.chunkBuilder != null) {
                this.chunkBuilder.stop();
            }
            this.chunkBuilder = null;
            this.noCullingBlockEntities.clear();
            this.chunkRenderingDataPreparer.setStorage(null);
            this.clear();
        }
    }

    private void clear() {
        this.builtChunks.clear();
        this.nearbyChunks.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        if (this.world == null) {
            return;
        }
        this.world.reloadColor();
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.world, this, Util.getMainWorkerExecutor(), this.bufferBuilders, this.client.getBlockRenderManager(), this.client.getBlockEntityRenderDispatcher());
        } else {
            this.chunkBuilder.setWorld(this.world);
        }
        this.cloudRenderer.scheduleTerrainUpdate();
        RenderLayers.setFancyGraphicsOrBetter(MinecraftClient.isFancyGraphicsOrBetter());
        this.viewDistance = this.client.options.getClampedViewDistance();
        if (this.chunks != null) {
            this.chunks.clear();
        }
        this.chunkBuilder.reset();
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.clear();
        }
        this.chunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.getClampedViewDistance(), this);
        this.chunkRenderingDataPreparer.setStorage(this.chunks);
        this.clear();
        Camera lv = this.client.gameRenderer.getCamera();
        this.chunks.updateCameraPosition(ChunkSectionPos.from(lv.getPos()));
    }

    public void onResized(int width, int height) {
        this.scheduleTerrainUpdate();
        if (this.entityOutlineFramebuffer != null) {
            this.entityOutlineFramebuffer.resize(width, height);
        }
    }

    public String getChunksDebugString() {
        int i = this.chunks.chunks.length;
        int j = this.getCompletedChunkCount();
        return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", j, i, this.client.chunkCullingEnabled ? "(s) " : "", this.viewDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    public ChunkBuilder getChunkBuilder() {
        return this.chunkBuilder;
    }

    public double getChunkCount() {
        return this.chunks.chunks.length;
    }

    public double getViewDistance() {
        return this.viewDistance;
    }

    public int getCompletedChunkCount() {
        int i = 0;
        for (ChunkBuilder.BuiltChunk lv : this.builtChunks) {
            if (!lv.getData().hasNonEmptyLayers()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.renderedEntitiesCount + "/" + this.world.getRegularEntityCount() + ", SD: " + this.world.getSimulationDistance();
    }

    private void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {
        Vec3d lv = camera.getPos();
        if (this.client.options.getClampedViewDistance() != this.viewDistance) {
            this.reload();
        }
        Profiler lv2 = Profilers.get();
        lv2.push("camera");
        int i = ChunkSectionPos.getSectionCoord(lv.getX());
        int j = ChunkSectionPos.getSectionCoord(lv.getY());
        int k = ChunkSectionPos.getSectionCoord(lv.getZ());
        if (this.cameraChunkX != i || this.cameraChunkY != j || this.cameraChunkZ != k) {
            this.cameraChunkX = i;
            this.cameraChunkY = j;
            this.cameraChunkZ = k;
            this.chunks.updateCameraPosition(ChunkSectionPos.from(lv));
        }
        this.chunkBuilder.setCameraPosition(lv);
        lv2.swap("cull");
        double d = Math.floor(lv.x / 8.0);
        double e = Math.floor(lv.y / 8.0);
        double f = Math.floor(lv.z / 8.0);
        if (d != this.lastCameraX || e != this.lastCameraY || f != this.lastCameraZ) {
            this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
        }
        this.lastCameraX = d;
        this.lastCameraY = e;
        this.lastCameraZ = f;
        lv2.swap("update");
        if (!hasForcedFrustum) {
            boolean bl3 = this.client.chunkCullingEnabled;
            if (spectator && this.world.getBlockState(camera.getBlockPos()).isOpaqueFullCube()) {
                bl3 = false;
            }
            lv2.push("section_occlusion_graph");
            this.chunkRenderingDataPreparer.updateSectionOcclusionGraph(bl3, camera, frustum, this.builtChunks, this.world.getChunkManager().getActiveSections());
            lv2.pop();
            double g = Math.floor(camera.getPitch() / 2.0f);
            double h = Math.floor(camera.getYaw() / 2.0f);
            if (this.chunkRenderingDataPreparer.method_52836() || g != this.lastCameraPitch || h != this.lastCameraYaw) {
                this.applyFrustum(WorldRenderer.offsetFrustum(frustum));
                this.lastCameraPitch = g;
                this.lastCameraYaw = h;
            }
        }
        lv2.pop();
    }

    public static Frustum offsetFrustum(Frustum frustum) {
        return new Frustum(frustum).coverBoxAroundSetPosition(8);
    }

    private void applyFrustum(Frustum frustum) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
        }
        Profilers.get().push("apply_frustum");
        this.clear();
        this.chunkRenderingDataPreparer.collectChunks(frustum, this.builtChunks, this.nearbyChunks);
        Profilers.get().pop();
    }

    public void addBuiltChunk(ChunkBuilder.BuiltChunk chunk) {
        this.chunkRenderingDataPreparer.schedulePropagationFrom(chunk);
    }

    public void setupFrustum(Vec3d pos, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        this.frustum = new Frustum(positionMatrix, projectionMatrix);
        this.frustum.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        float k;
        float f = tickCounter.getTickDelta(false);
        RenderSystem.setShaderGameTime(this.world.getTime(), f);
        this.blockEntityRenderDispatcher.configure(this.world, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        final Profiler lv = Profilers.get();
        lv.swap("light_update_queue");
        this.world.runQueuedChunkUpdates();
        lv.swap("light_updates");
        this.world.getChunkManager().getLightingProvider().doLightUpdates();
        Vec3d lv2 = camera.getPos();
        double d = lv2.getX();
        double e = lv2.getY();
        double g = lv2.getZ();
        lv.swap("culling");
        boolean bl2 = this.capturedFrustum != null;
        Frustum lv3 = bl2 ? this.capturedFrustum : this.frustum;
        Profilers.get().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.capturedFrustum = bl2 ? new Frustum(positionMatrix, projectionMatrix) : lv3;
            this.capturedFrustum.setPosition(d, e, g);
            this.shouldCaptureFrustum = false;
        }
        lv.swap("fog");
        float h = gameRenderer.getViewDistance();
        boolean bl3 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        Vector4f vector4f = BackgroundRenderer.getFogColor(camera, f, this.client.world, this.client.options.getClampedViewDistance(), gameRenderer.getSkyDarkness(f));
        Fog lv4 = BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, vector4f, h, bl3, f);
        Fog lv5 = BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, vector4f, h, bl3, f);
        lv.swap("cullEntities");
        boolean bl4 = this.getEntitiesToRender(camera, lv3, this.renderedEntities);
        this.renderedEntitiesCount = this.renderedEntities.size();
        lv.swap("terrain_setup");
        this.setupTerrain(camera, lv3, bl2, this.client.player.isSpectator());
        lv.swap("compile_sections");
        this.updateChunks(camera);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(positionMatrix);
        FrameGraphBuilder lv6 = new FrameGraphBuilder();
        this.framebufferSet.mainFramebuffer = lv6.createObjectNode("main", this.client.getFramebuffer());
        int i = this.client.getFramebuffer().textureWidth;
        int j = this.client.getFramebuffer().textureHeight;
        SimpleFramebufferFactory lv7 = new SimpleFramebufferFactory(i, j, true);
        PostEffectProcessor lv8 = this.getTransparencyPostEffectProcessor();
        if (lv8 != null) {
            this.framebufferSet.translucentFramebuffer = lv6.createResourceHandle("translucent", lv7);
            this.framebufferSet.itemEntityFramebuffer = lv6.createResourceHandle("item_entity", lv7);
            this.framebufferSet.particlesFramebuffer = lv6.createResourceHandle("particles", lv7);
            this.framebufferSet.weatherFramebuffer = lv6.createResourceHandle("weather", lv7);
            this.framebufferSet.cloudsFramebuffer = lv6.createResourceHandle("clouds", lv7);
        }
        if (this.entityOutlineFramebuffer != null) {
            this.framebufferSet.entityOutlineFramebuffer = lv6.createObjectNode("entity_outline", this.entityOutlineFramebuffer);
        }
        RenderPass lv9 = lv6.createPass("clear");
        this.framebufferSet.mainFramebuffer = lv9.transfer(this.framebufferSet.mainFramebuffer);
        lv9.setRenderer(() -> {
            RenderSystem.clearColor(vector4f.x, vector4f.y, vector4f.z, 0.0f);
            RenderSystem.clear(16640);
        });
        if (!bl3) {
            this.renderSky(lv6, camera, f, lv5);
        }
        this.renderMain(lv6, lv3, camera, positionMatrix, projectionMatrix, lv4, renderBlockOutline, bl4, tickCounter, lv);
        PostEffectProcessor lv10 = this.client.getShaderLoader().loadPostEffect(ENTITY_OUTLINE, DefaultFramebufferSet.MAIN_AND_ENTITY_OUTLINE);
        if (bl4 && lv10 != null) {
            lv10.render(lv6, i, j, this.framebufferSet);
        }
        this.renderParticles(lv6, camera, f, lv4);
        CloudRenderMode lv11 = this.client.options.getCloudRenderModeValue();
        if (lv11 != CloudRenderMode.OFF && !Float.isNaN(k = this.world.getDimensionEffects().getCloudsHeight())) {
            float l = (float)this.ticks + f;
            int m = this.world.getCloudsColor(f);
            this.renderClouds(lv6, positionMatrix, projectionMatrix, lv11, camera.getPos(), l, m, k + 0.33f);
        }
        this.renderWeather(lv6, camera.getPos(), f, lv4);
        if (lv8 != null) {
            lv8.render(lv6, i, j, this.framebufferSet);
        }
        this.renderLateDebug(lv6, lv2, lv4);
        lv.swap("framegraph");
        lv6.run(allocator, new FrameGraphBuilder.Profiler(){

            @Override
            public void push(String location) {
                lv.push(location);
            }

            @Override
            public void pop(String location) {
                lv.pop();
            }
        });
        this.client.getFramebuffer().beginWrite(false);
        this.renderedEntities.clear();
        this.framebufferSet.clear();
        matrix4fStack.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderFog(Fog.DUMMY);
    }

    private void renderMain(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Fog fog, boolean renderBlockOutline, boolean renderEntityOutlines, RenderTickCounter renderTickCounter, Profiler profiler) {
        RenderPass lv = frameGraphBuilder.createPass("main");
        this.framebufferSet.mainFramebuffer = lv.transfer(this.framebufferSet.mainFramebuffer);
        if (this.framebufferSet.translucentFramebuffer != null) {
            this.framebufferSet.translucentFramebuffer = lv.transfer(this.framebufferSet.translucentFramebuffer);
        }
        if (this.framebufferSet.itemEntityFramebuffer != null) {
            this.framebufferSet.itemEntityFramebuffer = lv.transfer(this.framebufferSet.itemEntityFramebuffer);
        }
        if (this.framebufferSet.weatherFramebuffer != null) {
            this.framebufferSet.weatherFramebuffer = lv.transfer(this.framebufferSet.weatherFramebuffer);
        }
        if (renderEntityOutlines && this.framebufferSet.entityOutlineFramebuffer != null) {
            this.framebufferSet.entityOutlineFramebuffer = lv.transfer(this.framebufferSet.entityOutlineFramebuffer);
        }
        Handle<Framebuffer> lv2 = this.framebufferSet.mainFramebuffer;
        Handle<Framebuffer> lv3 = this.framebufferSet.translucentFramebuffer;
        Handle<Framebuffer> lv4 = this.framebufferSet.itemEntityFramebuffer;
        Handle<Framebuffer> lv5 = this.framebufferSet.weatherFramebuffer;
        Handle<Framebuffer> lv6 = this.framebufferSet.entityOutlineFramebuffer;
        lv.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            float f = renderTickCounter.getTickDelta(false);
            Vec3d lv = camera.getPos();
            double d = lv.getX();
            double e = lv.getY();
            double g = lv.getZ();
            profiler.push("terrain");
            this.renderLayer(RenderLayer.getSolid(), d, e, g, positionMatrix, projectionMatrix);
            this.renderLayer(RenderLayer.getCutoutMipped(), d, e, g, positionMatrix, projectionMatrix);
            this.renderLayer(RenderLayer.getCutout(), d, e, g, positionMatrix, projectionMatrix);
            if (this.world.getDimensionEffects().isDarkened()) {
                DiffuseLighting.enableForLevel();
            } else {
                DiffuseLighting.disableForLevel();
            }
            if (lv4 != null) {
                ((Framebuffer)lv4.get()).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                ((Framebuffer)lv4.get()).clear();
                ((Framebuffer)lv4.get()).copyDepthFrom(this.client.getFramebuffer());
                ((Framebuffer)lv2.get()).beginWrite(false);
            }
            if (lv5 != null) {
                ((Framebuffer)lv5.get()).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                ((Framebuffer)lv5.get()).clear();
            }
            if (this.canDrawEntityOutlines() && lv6 != null) {
                ((Framebuffer)lv6.get()).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                ((Framebuffer)lv6.get()).clear();
                ((Framebuffer)lv2.get()).beginWrite(false);
            }
            MatrixStack lv2 = new MatrixStack();
            VertexConsumerProvider.Immediate lv3 = this.bufferBuilders.getEntityVertexConsumers();
            VertexConsumerProvider.Immediate lv4 = this.bufferBuilders.getEffectVertexConsumers();
            profiler.swap("entities");
            this.renderEntities(lv2, lv3, camera, renderTickCounter, this.renderedEntities);
            lv3.drawCurrentLayer();
            this.checkEmpty(lv2);
            profiler.swap("blockentities");
            this.renderBlockEntities(lv2, lv3, lv4, camera, f);
            lv3.drawCurrentLayer();
            this.checkEmpty(lv2);
            lv3.draw(RenderLayer.getSolid());
            lv3.draw(RenderLayer.getEndPortal());
            lv3.draw(RenderLayer.getEndGateway());
            lv3.draw(TexturedRenderLayers.getEntitySolid());
            lv3.draw(TexturedRenderLayers.getEntityCutout());
            lv3.draw(TexturedRenderLayers.getBeds());
            lv3.draw(TexturedRenderLayers.getShulkerBoxes());
            lv3.draw(TexturedRenderLayers.getSign());
            lv3.draw(TexturedRenderLayers.getHangingSign());
            lv3.draw(TexturedRenderLayers.getChest());
            this.bufferBuilders.getOutlineVertexConsumers().draw();
            if (renderBlockOutline) {
                this.renderTargetBlockOutline(camera, lv3, lv2, false);
            }
            profiler.swap("debug");
            this.client.debugRenderer.render(lv2, frustum, lv3, d, e, g);
            lv3.drawCurrentLayer();
            this.checkEmpty(lv2);
            lv3.draw(TexturedRenderLayers.getItemEntityTranslucentCull());
            lv3.draw(TexturedRenderLayers.getBannerPatterns());
            lv3.draw(TexturedRenderLayers.getShieldPatterns());
            lv3.draw(RenderLayer.getArmorEntityGlint());
            lv3.draw(RenderLayer.getGlint());
            lv3.draw(RenderLayer.getGlintTranslucent());
            lv3.draw(RenderLayer.getEntityGlint());
            profiler.swap("destroyProgress");
            this.renderBlockDamage(lv2, camera, lv4);
            lv4.draw();
            this.checkEmpty(lv2);
            lv3.draw(RenderLayer.getWaterMask());
            lv3.draw();
            if (lv3 != null) {
                ((Framebuffer)lv3.get()).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                ((Framebuffer)lv3.get()).clear();
                ((Framebuffer)lv3.get()).copyDepthFrom((Framebuffer)lv2.get());
            }
            profiler.swap("translucent");
            this.renderLayer(RenderLayer.getTranslucent(), d, e, g, positionMatrix, projectionMatrix);
            profiler.swap("string");
            this.renderLayer(RenderLayer.getTripwire(), d, e, g, positionMatrix, projectionMatrix);
            if (renderBlockOutline) {
                this.renderTargetBlockOutline(camera, lv3, lv2, true);
            }
            lv3.draw();
            profiler.pop();
        });
    }

    private void renderParticles(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, Fog fog) {
        RenderPass lv = frameGraphBuilder.createPass("particles");
        if (this.framebufferSet.particlesFramebuffer != null) {
            this.framebufferSet.particlesFramebuffer = lv.transfer(this.framebufferSet.particlesFramebuffer);
            lv.dependsOn(this.framebufferSet.mainFramebuffer);
        } else {
            this.framebufferSet.mainFramebuffer = lv.transfer(this.framebufferSet.mainFramebuffer);
        }
        Handle<Framebuffer> lv2 = this.framebufferSet.mainFramebuffer;
        Handle<Framebuffer> lv3 = this.framebufferSet.particlesFramebuffer;
        lv.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            if (lv3 != null) {
                ((Framebuffer)lv3.get()).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                ((Framebuffer)lv3.get()).clear();
                ((Framebuffer)lv3.get()).copyDepthFrom((Framebuffer)lv2.get());
            }
            this.client.particleManager.renderParticles(camera, tickDelta, this.bufferBuilders.getEntityVertexConsumers());
        });
    }

    private void renderClouds(FrameGraphBuilder frameGraphBuilder, Matrix4f positionMatrix, Matrix4f projectionMatrix, CloudRenderMode renderMode, Vec3d cameraPos, float ticks, int color, float cloudHeight) {
        RenderPass lv = frameGraphBuilder.createPass("clouds");
        if (this.framebufferSet.cloudsFramebuffer != null) {
            this.framebufferSet.cloudsFramebuffer = lv.transfer(this.framebufferSet.cloudsFramebuffer);
        } else {
            this.framebufferSet.mainFramebuffer = lv.transfer(this.framebufferSet.mainFramebuffer);
        }
        Handle<Framebuffer> lv2 = this.framebufferSet.cloudsFramebuffer;
        lv.setRenderer(() -> {
            if (lv2 != null) {
                ((Framebuffer)lv2.get()).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                ((Framebuffer)lv2.get()).clear();
            }
            this.cloudRenderer.renderClouds(color, renderMode, cloudHeight, positionMatrix, projectionMatrix, cameraPos, ticks);
        });
    }

    private void renderWeather(FrameGraphBuilder frameGraphBuilder, Vec3d pos, float tickDelta, Fog fog) {
        int i = this.client.options.getClampedViewDistance() * 16;
        float g = this.client.gameRenderer.getFarPlaneDistance();
        RenderPass lv = frameGraphBuilder.createPass("weather");
        if (this.framebufferSet.weatherFramebuffer != null) {
            this.framebufferSet.weatherFramebuffer = lv.transfer(this.framebufferSet.weatherFramebuffer);
        } else {
            this.framebufferSet.mainFramebuffer = lv.transfer(this.framebufferSet.mainFramebuffer);
        }
        lv.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            VertexConsumerProvider.Immediate lv = this.bufferBuilders.getEntityVertexConsumers();
            this.weatherRendering.renderPrecipitation(this.client.world, lv, this.ticks, tickDelta, pos);
            this.worldBorderRendering.render(this.world.getWorldBorder(), pos, i, g);
            lv.draw();
        });
    }

    private void renderLateDebug(FrameGraphBuilder frameGraphBuilder, Vec3d pos, Fog fog) {
        RenderPass lv = frameGraphBuilder.createPass("late_debug");
        this.framebufferSet.mainFramebuffer = lv.transfer(this.framebufferSet.mainFramebuffer);
        if (this.framebufferSet.itemEntityFramebuffer != null) {
            this.framebufferSet.itemEntityFramebuffer = lv.transfer(this.framebufferSet.itemEntityFramebuffer);
        }
        Handle<Framebuffer> lv2 = this.framebufferSet.mainFramebuffer;
        lv.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            ((Framebuffer)lv2.get()).beginWrite(false);
            MatrixStack lv = new MatrixStack();
            VertexConsumerProvider.Immediate lv2 = this.bufferBuilders.getEntityVertexConsumers();
            this.client.debugRenderer.renderLate(lv, lv2, arg3.x, arg3.y, arg3.z);
            lv2.drawCurrentLayer();
            this.checkEmpty(lv);
        });
    }

    private boolean getEntitiesToRender(Camera camera, Frustum frustum, List<Entity> output) {
        Vec3d lv = camera.getPos();
        double d = lv.getX();
        double e = lv.getY();
        double f = lv.getZ();
        boolean bl = false;
        boolean bl2 = this.canDrawEntityOutlines();
        Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.getClampedViewDistance() / 8.0, 1.0, 2.5) * this.client.options.getEntityDistanceScaling().getValue());
        for (Entity lv2 : this.world.getEntities()) {
            BlockPos lv3;
            if (!this.entityRenderDispatcher.shouldRender(lv2, frustum, d, e, f) && !lv2.hasPassengerDeep(this.client.player) || !this.world.isOutOfHeightLimit((lv3 = lv2.getBlockPos()).getY()) && !this.isRenderingReady(lv3) || lv2 == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()) || lv2 instanceof ClientPlayerEntity && camera.getFocusedEntity() != lv2) continue;
            output.add(lv2);
            if (!bl2 || !this.client.hasOutline(lv2)) continue;
            bl = true;
        }
        return bl;
    }

    private void renderEntities(MatrixStack matrices, VertexConsumerProvider.Immediate arg2, Camera camera, RenderTickCounter tickCounter, List<Entity> entities) {
        Vec3d lv = camera.getPos();
        double d = lv.getX();
        double e = lv.getY();
        double f = lv.getZ();
        TickManager lv2 = this.client.world.getTickManager();
        boolean bl = this.canDrawEntityOutlines();
        for (Entity lv3 : entities) {
            VertexConsumerProvider lv5;
            if (lv3.age == 0) {
                lv3.lastRenderX = lv3.getX();
                lv3.lastRenderY = lv3.getY();
                lv3.lastRenderZ = lv3.getZ();
            }
            if (bl && this.client.hasOutline(lv3)) {
                OutlineVertexConsumerProvider lv4 = this.bufferBuilders.getOutlineVertexConsumers();
                lv5 = lv4;
                int i = lv3.getTeamColorValue();
                lv4.setColor(ColorHelper.getRed(i), ColorHelper.getGreen(i), ColorHelper.getBlue(i), 255);
            } else {
                lv5 = arg2;
            }
            float g = tickCounter.getTickDelta(!lv2.shouldSkipTick(lv3));
            this.renderEntity(lv3, d, e, f, g, matrices, lv5);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderBlockEntities(MatrixStack matrices, VertexConsumerProvider.Immediate arg2, VertexConsumerProvider.Immediate arg32, Camera camera, float tickDelta) {
        Vec3d lv = camera.getPos();
        double d = lv.getX();
        double e = lv.getY();
        double g = lv.getZ();
        for (ChunkBuilder.BuiltChunk lv2 : this.builtChunks) {
            List<BlockEntity> list = lv2.getData().getBlockEntities();
            if (list.isEmpty()) continue;
            for (BlockEntity lv3 : list) {
                int i;
                BlockPos lv4 = lv3.getPos();
                VertexConsumerProvider lv5 = arg2;
                matrices.push();
                matrices.translate((double)lv4.getX() - d, (double)lv4.getY() - e, (double)lv4.getZ() - g);
                SortedSet sortedSet = (SortedSet)this.blockBreakingProgressions.get(lv4.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (i = ((BlockBreakingInfo)sortedSet.last()).getStage()) >= 0) {
                    MatrixStack.Entry lv6 = matrices.peek();
                    OverlayVertexConsumer lv7 = new OverlayVertexConsumer(arg32.getBuffer(ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(i)), lv6, 1.0f);
                    lv5 = arg3 -> {
                        VertexConsumer lv = arg2.getBuffer(arg3);
                        if (arg3.hasCrumbling()) {
                            return VertexConsumers.union(lv7, lv);
                        }
                        return lv;
                    };
                }
                this.blockEntityRenderDispatcher.render(lv3, tickDelta, matrices, lv5);
                matrices.pop();
            }
        }
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            for (BlockEntity lv8 : this.noCullingBlockEntities) {
                BlockPos lv9 = lv8.getPos();
                matrices.push();
                matrices.translate((double)lv9.getX() - d, (double)lv9.getY() - e, (double)lv9.getZ() - g);
                this.blockEntityRenderDispatcher.render(lv8, tickDelta, matrices, arg2);
                matrices.pop();
            }
        }
    }

    private void renderBlockDamage(MatrixStack matrices, Camera camera, VertexConsumerProvider.Immediate vertexConsumers) {
        Vec3d lv = camera.getPos();
        double d = lv.getX();
        double e = lv.getY();
        double f = lv.getZ();
        for (Long2ObjectMap.Entry entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
            SortedSet sortedSet;
            BlockPos lv2 = BlockPos.fromLong(entry.getLongKey());
            if (lv2.getSquaredDistanceFromCenter(d, e, f) > 1024.0 || (sortedSet = (SortedSet)entry.getValue()) == null || sortedSet.isEmpty()) continue;
            int i = ((BlockBreakingInfo)sortedSet.last()).getStage();
            matrices.push();
            matrices.translate((double)lv2.getX() - d, (double)lv2.getY() - e, (double)lv2.getZ() - f);
            MatrixStack.Entry lv3 = matrices.peek();
            OverlayVertexConsumer lv4 = new OverlayVertexConsumer(vertexConsumers.getBuffer(ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(i)), lv3, 1.0f);
            this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(lv2), lv2, this.world, matrices, lv4);
            matrices.pop();
        }
    }

    private void renderTargetBlockOutline(Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices, boolean translucent) {
        HitResult hitResult = this.client.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult lv = (BlockHitResult)hitResult;
        if (lv.getType() == HitResult.Type.MISS) {
            return;
        }
        BlockPos lv2 = lv.getBlockPos();
        BlockState lv3 = this.world.getBlockState(lv2);
        if (!lv3.isAir() && this.world.getWorldBorder().contains(lv2)) {
            VertexConsumer lv5;
            boolean bl2 = RenderLayers.getBlockLayer(lv3).isTranslucent();
            if (bl2 != translucent) {
                return;
            }
            Vec3d lv4 = camera.getPos();
            Boolean boolean_ = this.client.options.getHighContrastBlockOutline().getValue();
            if (boolean_.booleanValue()) {
                lv5 = vertexConsumers.getBuffer(RenderLayer.getSecondaryBlockOutline());
                this.drawBlockOutline(matrices, lv5, camera.getFocusedEntity(), lv4.x, lv4.y, lv4.z, lv2, lv3, -16777216);
            }
            lv5 = vertexConsumers.getBuffer(RenderLayer.getLines());
            int i = boolean_ != false ? Colors.CYAN : ColorHelper.withAlpha(102, Colors.BLACK);
            this.drawBlockOutline(matrices, lv5, camera.getFocusedEntity(), lv4.x, lv4.y, lv4.z, lv2, lv3, i);
            vertexConsumers.drawCurrentLayer();
        }
    }

    private void checkEmpty(MatrixStack matrices) {
        if (!matrices.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        double h = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double i = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double j = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        this.entityRenderDispatcher.render(entity, h - cameraX, i - cameraY, j - cameraZ, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
    }

    private void translucencySort(Vec3d cameraPos) {
        if (this.builtChunks.isEmpty()) {
            return;
        }
        BlockPos lv = BlockPos.ofFloored(cameraPos);
        boolean bl = !lv.equals(this.prevTranslucencySortCameraPos);
        Profilers.get().push("translucent_sort");
        ChunkBuilder.NormalizedRelativePos lv2 = new ChunkBuilder.NormalizedRelativePos();
        for (ChunkBuilder.BuiltChunk lv3 : this.nearbyChunks) {
            this.scheduleChunkTranslucencySort(lv3, lv2, cameraPos, bl, true);
        }
        this.chunkIndex %= this.builtChunks.size();
        int i = Math.max(this.builtChunks.size() / 8, 15);
        while (i-- > 0) {
            int j = this.chunkIndex++ % this.builtChunks.size();
            this.scheduleChunkTranslucencySort(this.builtChunks.get(j), lv2, cameraPos, bl, false);
        }
        this.prevTranslucencySortCameraPos = lv;
        Profilers.get().pop();
    }

    private void scheduleChunkTranslucencySort(ChunkBuilder.BuiltChunk chunk, ChunkBuilder.NormalizedRelativePos relativePos, Vec3d cameraPos, boolean needsUpdate, boolean ignoreCameraAlignment) {
        boolean bl4;
        relativePos.with(cameraPos, chunk.getSectionPos());
        boolean bl3 = !relativePos.equals(chunk.relativePos.get());
        boolean bl = bl4 = needsUpdate && (relativePos.isOnCameraAxis() || ignoreCameraAlignment);
        if ((bl4 || bl3) && !chunk.isCurrentlySorting() && chunk.hasTranslucentLayer()) {
            chunk.scheduleSort(this.chunkBuilder);
        }
    }

    private void renderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f positionMatrix) {
        RenderSystem.assertOnRenderThread();
        ScopedProfiler lv = Profilers.get().scoped(() -> "render_" + arg.name);
        lv.addLabel(renderLayer::toString);
        boolean bl = renderLayer != RenderLayer.getTranslucent();
        ListIterator objectListIterator = this.builtChunks.listIterator(bl ? 0 : this.builtChunks.size());
        renderLayer.startDrawing();
        ShaderProgram lv2 = RenderSystem.getShader();
        if (lv2 == null) {
            renderLayer.endDrawing();
            lv.close();
            return;
        }
        lv2.initializeUniforms(VertexFormat.DrawMode.QUADS, viewMatrix, positionMatrix, this.client.getWindow());
        lv2.bind();
        GlUniform lv3 = lv2.modelOffset;
        while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
            ChunkBuilder.BuiltChunk lv4;
            ChunkBuilder.BuiltChunk builtChunk = lv4 = bl ? (ChunkBuilder.BuiltChunk)objectListIterator.next() : (ChunkBuilder.BuiltChunk)objectListIterator.previous();
            if (lv4.getData().isEmpty(renderLayer)) continue;
            VertexBuffer lv5 = lv4.getBuffer(renderLayer);
            BlockPos lv6 = lv4.getOrigin();
            if (lv3 != null) {
                lv3.set((float)((double)lv6.getX() - x), (float)((double)lv6.getY() - y), (float)((double)lv6.getZ() - z));
                lv3.upload();
            }
            lv5.bind();
            lv5.draw();
        }
        if (lv3 != null) {
            lv3.set(0.0f, 0.0f, 0.0f);
        }
        lv2.unbind();
        VertexBuffer.unbind();
        lv.close();
        renderLayer.endDrawing();
    }

    public void captureFrustum() {
        this.shouldCaptureFrustum = true;
    }

    public void killFrustum() {
        this.capturedFrustum = null;
    }

    public void tick() {
        if (this.world.getTickManager().shouldTick()) {
            ++this.ticks;
        }
        if (this.ticks % 20 != 0) {
            return;
        }
        Iterator iterator = this.blockBreakingInfos.values().iterator();
        while (iterator.hasNext()) {
            BlockBreakingInfo lv = (BlockBreakingInfo)iterator.next();
            int i = lv.getLastUpdateTick();
            if (this.ticks - i <= 400) continue;
            iterator.remove();
            this.removeBlockBreakingInfo(lv);
        }
    }

    private void removeBlockBreakingInfo(BlockBreakingInfo info) {
        long l = info.getPos().asLong();
        Set set = (Set)this.blockBreakingProgressions.get(l);
        set.remove(info);
        if (set.isEmpty()) {
            this.blockBreakingProgressions.remove(l);
        }
    }

    private void renderSky(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, Fog fog) {
        CameraSubmersionType lv = camera.getSubmersionType();
        if (lv == CameraSubmersionType.POWDER_SNOW || lv == CameraSubmersionType.LAVA || this.hasBlindnessOrDarkness(camera)) {
            return;
        }
        DimensionEffects lv2 = this.world.getDimensionEffects();
        DimensionEffects.SkyType lv3 = lv2.getSkyType();
        if (lv3 == DimensionEffects.SkyType.NONE) {
            return;
        }
        RenderPass lv4 = frameGraphBuilder.createPass("sky");
        this.framebufferSet.mainFramebuffer = lv4.transfer(this.framebufferSet.mainFramebuffer);
        lv4.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            if (lv3 == DimensionEffects.SkyType.END) {
                this.skyRendering.renderEndSky();
                return;
            }
            MatrixStack lv = new MatrixStack();
            float g = this.world.getSkyAngleRadians(tickDelta);
            float h = this.world.getSkyAngle(tickDelta);
            float i = 1.0f - this.world.getRainGradient(tickDelta);
            float j = this.world.getStarBrightness(tickDelta) * i;
            int k = lv2.getSkyColor(h);
            int l = this.world.getMoonPhase();
            int m = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
            float n = ColorHelper.getRedFloat(m);
            float o = ColorHelper.getGreenFloat(m);
            float p = ColorHelper.getBlueFloat(m);
            this.skyRendering.renderSky(n, o, p);
            VertexConsumerProvider.Immediate lv2 = this.bufferBuilders.getEntityVertexConsumers();
            if (lv2.isSunRisingOrSetting(h)) {
                this.skyRendering.renderGlowingSky(lv, lv2, g, k);
            }
            this.skyRendering.renderCelestialBodies(lv, lv2, h, l, i, j, fog);
            lv2.draw();
            if (this.isSkyDark(tickDelta)) {
                this.skyRendering.renderSkyDark(lv);
            }
        });
    }

    private boolean isSkyDark(float tickDelta) {
        return this.client.player.getCameraPosVec((float)tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world) < 0.0;
    }

    private boolean hasBlindnessOrDarkness(Camera camera) {
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)entity;
            return lv.hasStatusEffect(StatusEffects.BLINDNESS) || lv.hasStatusEffect(StatusEffects.DARKNESS);
        }
        return false;
    }

    private void updateChunks(Camera camera) {
        Profiler lv = Profilers.get();
        lv.push("populate_sections_to_compile");
        ChunkRendererRegionBuilder lv2 = new ChunkRendererRegionBuilder();
        BlockPos lv3 = camera.getBlockPos();
        ArrayList<ChunkBuilder.BuiltChunk> list = Lists.newArrayList();
        for (ChunkBuilder.BuiltChunk lv4 : this.builtChunks) {
            if (!lv4.needsRebuild() || !lv4.shouldBuild()) continue;
            boolean bl = false;
            if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.NEARBY) {
                BlockPos lv5 = lv4.getOrigin().add(8, 8, 8);
                bl = lv5.getSquaredDistance(lv3) < 768.0 || lv4.needsImportantRebuild();
            } else if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.PLAYER_AFFECTED) {
                bl = lv4.needsImportantRebuild();
            }
            if (bl) {
                lv.push("build_near_sync");
                this.chunkBuilder.rebuild(lv4, lv2);
                lv4.cancelRebuild();
                lv.pop();
                continue;
            }
            list.add(lv4);
        }
        lv.swap("upload");
        this.chunkBuilder.upload();
        lv.swap("schedule_async_compile");
        for (ChunkBuilder.BuiltChunk lv4 : list) {
            lv4.scheduleRebuild(this.chunkBuilder, lv2);
            lv4.cancelRebuild();
        }
        lv.pop();
        this.translucencySort(camera.getPos());
    }

    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, int color) {
        VertexRendering.drawOutline(matrices, vertexConsumer, state.getOutlineShape(this.world, pos, ShapeContext.of(entity)), (double)pos.getX() - cameraX, (double)pos.getY() - cameraY, (double)pos.getZ() - cameraZ, color);
    }

    public void updateBlock(BlockView world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.scheduleSectionRender(pos, (flags & 8) != 0);
    }

    private void scheduleSectionRender(BlockPos pos, boolean important) {
        for (int i = pos.getZ() - 1; i <= pos.getZ() + 1; ++i) {
            for (int j = pos.getX() - 1; j <= pos.getX() + 1; ++j) {
                for (int k = pos.getY() - 1; k <= pos.getY() + 1; ++k) {
                    this.scheduleChunkRender(ChunkSectionPos.getSectionCoord(j), ChunkSectionPos.getSectionCoord(k), ChunkSectionPos.getSectionCoord(i), important);
                }
            }
        }
    }

    public void scheduleBlockRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int o = minZ - 1; o <= maxZ + 1; ++o) {
            for (int p = minX - 1; p <= maxX + 1; ++p) {
                for (int q = minY - 1; q <= maxY + 1; ++q) {
                    this.scheduleChunkRender(ChunkSectionPos.getSectionCoord(p), ChunkSectionPos.getSectionCoord(q), ChunkSectionPos.getSectionCoord(o));
                }
            }
        }
    }

    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
        if (this.client.getBakedModelManager().shouldRerender(old, updated)) {
            this.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void scheduleChunkRenders3x3x3(int x, int y, int z) {
        this.scheduleChunkRenders(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    public void scheduleChunkRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int o = minZ; o <= maxZ; ++o) {
            for (int p = minX; p <= maxX; ++p) {
                for (int q = minY; q <= maxY; ++q) {
                    this.scheduleChunkRender(p, q, o);
                }
            }
        }
    }

    public void scheduleChunkRender(int chunkX, int chunkY, int chunkZ) {
        this.scheduleChunkRender(chunkX, chunkY, chunkZ, false);
    }

    private void scheduleChunkRender(int x, int y, int z, boolean important) {
        this.chunks.scheduleRebuild(x, y, z, important);
    }

    public void onChunkUnload(long sectionPos) {
        ChunkBuilder.BuiltChunk lv = this.chunks.getRenderedChunk(sectionPos);
        if (lv != null) {
            this.chunkRenderingDataPreparer.schedulePropagationFrom(lv);
        }
    }

    public void addParticle(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, force, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    public void addParticle(ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        try {
            this.spawnParticle(parameters, force, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
        } catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection lv2 = lv.addElement("Particle being added");
            lv2.add("ID", Registries.PARTICLE_TYPE.getId(parameters.getType()));
            lv2.add("Parameters", () -> ParticleTypes.TYPE_CODEC.encodeStart(this.world.getRegistryManager().getOps(NbtOps.INSTANCE), parameters).toString());
            lv2.add("Position", () -> CrashReportSection.createPositionString((HeightLimitView)this.world, x, y, z));
            throw new CrashException(lv);
        }
    }

    public <T extends ParticleEffect> void addParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    Particle spawnParticle(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return this.spawnParticle(parameters, force, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Camera lv = this.client.gameRenderer.getCamera();
        ParticlesMode lv2 = this.getRandomParticleSpawnChance(canSpawnOnMinimal);
        if (force) {
            return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
        if (lv.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
            return null;
        }
        if (lv2 == ParticlesMode.MINIMAL) {
            return null;
        }
        return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    private ParticlesMode getRandomParticleSpawnChance(boolean canSpawnOnMinimal) {
        ParticlesMode lv = this.client.options.getParticles().getValue();
        if (canSpawnOnMinimal && lv == ParticlesMode.MINIMAL && this.world.random.nextInt(10) == 0) {
            lv = ParticlesMode.DECREASED;
        }
        if (lv == ParticlesMode.DECREASED && this.world.random.nextInt(3) == 0) {
            lv = ParticlesMode.MINIMAL;
        }
        return lv;
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int stage) {
        if (stage < 0 || stage >= 10) {
            BlockBreakingInfo lv = (BlockBreakingInfo)this.blockBreakingInfos.remove(entityId);
            if (lv != null) {
                this.removeBlockBreakingInfo(lv);
            }
        } else {
            BlockBreakingInfo lv = (BlockBreakingInfo)this.blockBreakingInfos.get(entityId);
            if (lv != null) {
                this.removeBlockBreakingInfo(lv);
            }
            if (lv == null || lv.getPos().getX() != pos.getX() || lv.getPos().getY() != pos.getY() || lv.getPos().getZ() != pos.getZ()) {
                lv = new BlockBreakingInfo(entityId, pos);
                this.blockBreakingInfos.put(entityId, lv);
            }
            lv.setStage(stage);
            lv.setLastUpdateTick(this.ticks);
            this.blockBreakingProgressions.computeIfAbsent(lv.getPos().asLong(), l -> Sets.newTreeSet()).add(lv);
        }
    }

    public boolean isTerrainRenderComplete() {
        return this.chunkBuilder.isEmpty();
    }

    public void scheduleNeighborUpdates(ChunkPos chunkPos) {
        this.chunkRenderingDataPreparer.addNeighbors(chunkPos);
    }

    public void scheduleTerrainUpdate() {
        this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
        this.cloudRenderer.scheduleTerrainUpdate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateNoCullingBlockEntities(Collection<BlockEntity> removed, Collection<BlockEntity> added) {
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.removeAll(removed);
            this.noCullingBlockEntities.addAll(added);
        }
    }

    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return WorldRenderer.getLightmapCoordinates(world, world.getBlockState(pos), pos);
    }

    public static int getLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos pos) {
        int k;
        if (state.hasEmissiveLighting(world, pos)) {
            return 0xF000F0;
        }
        int i = world.getLightLevel(LightType.SKY, pos);
        int j = world.getLightLevel(LightType.BLOCK, pos);
        if (j < (k = state.getLuminance())) {
            j = k;
        }
        return i << 20 | j << 4;
    }

    public boolean isRenderingReady(BlockPos pos) {
        ChunkBuilder.BuiltChunk lv = this.chunks.getRenderedChunk(pos);
        return lv != null && lv.data.get() != ChunkBuilder.ChunkData.UNPROCESSED;
    }

    @Nullable
    public Framebuffer getEntityOutlinesFramebuffer() {
        return this.framebufferSet.entityOutlineFramebuffer != null ? this.framebufferSet.entityOutlineFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getTranslucentFramebuffer() {
        return this.framebufferSet.translucentFramebuffer != null ? this.framebufferSet.translucentFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getEntityFramebuffer() {
        return this.framebufferSet.itemEntityFramebuffer != null ? this.framebufferSet.itemEntityFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getParticlesFramebuffer() {
        return this.framebufferSet.particlesFramebuffer != null ? this.framebufferSet.particlesFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getWeatherFramebuffer() {
        return this.framebufferSet.weatherFramebuffer != null ? this.framebufferSet.weatherFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getCloudsFramebuffer() {
        return this.framebufferSet.cloudsFramebuffer != null ? this.framebufferSet.cloudsFramebuffer.get() : null;
    }

    @Debug
    public ObjectArrayList<ChunkBuilder.BuiltChunk> getBuiltChunks() {
        return this.builtChunks;
    }

    @Debug
    public ChunkRenderingDataPreparer getChunkRenderingDataPreparer() {
        return this.chunkRenderingDataPreparer;
    }

    @Nullable
    public Frustum getCapturedFrustum() {
        return this.capturedFrustum;
    }

    public CloudRenderer getCloudRenderer() {
        return this.cloudRenderer;
    }
}

