/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GameRenderer
implements AutoCloseable {
    private static final Identifier field_53899 = Identifier.ofVanilla("blur");
    public static final int field_49904 = 10;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean field_32688 = false;
    public static final float CAMERA_DEPTH = 0.05f;
    private static final float field_44940 = 1000.0f;
    private final MinecraftClient client;
    private final ResourceManager resourceManager;
    private final Random random = Random.create();
    private float viewDistance;
    public final HeldItemRenderer firstPersonRenderer;
    private final BufferBuilderStorage buffers;
    private int ticks;
    private float fovMultiplier;
    private float lastFovMultiplier;
    private float skyDarkness;
    private float lastSkyDarkness;
    private boolean renderHand = true;
    private boolean blockOutlineEnabled = true;
    private long lastWorldIconUpdate;
    private boolean hasWorldIcon;
    private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
    private final LightmapTextureManager lightmapTextureManager;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean renderingPanorama;
    private float zoom = 1.0f;
    private float zoomX;
    private float zoomY;
    public static final int field_32687 = 40;
    @Nullable
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;
    private final Pool pool = new Pool(3);
    @Nullable
    private Identifier postProcessorId;
    private boolean postProcessorEnabled;
    private final Camera camera = new Camera();

    public GameRenderer(MinecraftClient client, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, BufferBuilderStorage buffers) {
        this.client = client;
        this.resourceManager = resourceManager;
        this.firstPersonRenderer = heldItemRenderer;
        this.lightmapTextureManager = new LightmapTextureManager(this, client);
        this.buffers = buffers;
    }

    @Override
    public void close() {
        this.lightmapTextureManager.close();
        this.overlayTexture.close();
        this.pool.close();
    }

    public void setRenderHand(boolean renderHand) {
        this.renderHand = renderHand;
    }

    public void setBlockOutlineEnabled(boolean blockOutlineEnabled) {
        this.blockOutlineEnabled = blockOutlineEnabled;
    }

    public void setRenderingPanorama(boolean renderingPanorama) {
        this.renderingPanorama = renderingPanorama;
    }

    public boolean isRenderingPanorama() {
        return this.renderingPanorama;
    }

    public void clearPostProcessor() {
        this.postProcessorId = null;
    }

    public void togglePostProcessorEnabled() {
        this.postProcessorEnabled = !this.postProcessorEnabled;
    }

    public void onCameraEntitySet(@Nullable Entity entity) {
        this.postProcessorId = null;
        if (entity instanceof CreeperEntity) {
            this.setPostProcessor(Identifier.ofVanilla("creeper"));
        } else if (entity instanceof SpiderEntity) {
            this.setPostProcessor(Identifier.ofVanilla("spider"));
        } else if (entity instanceof EndermanEntity) {
            this.setPostProcessor(Identifier.ofVanilla("invert"));
        }
    }

    private void setPostProcessor(Identifier id) {
        this.postProcessorId = id;
        this.postProcessorEnabled = true;
    }

    public void renderBlur() {
        float f = this.client.options.getMenuBackgroundBlurrinessValue();
        if (f < 1.0f) {
            return;
        }
        PostEffectProcessor lv = this.client.getShaderLoader().loadPostEffect(field_53899, DefaultFramebufferSet.MAIN_ONLY);
        if (lv != null) {
            lv.setUniforms("Radius", f);
            lv.render(this.client.getFramebuffer(), this.pool);
        }
    }

    public void preloadPrograms(ResourceFactory factory) {
        try {
            this.client.getShaderLoader().preload(factory, ShaderProgramKeys.RENDERTYPE_GUI, ShaderProgramKeys.RENDERTYPE_GUI_OVERLAY, ShaderProgramKeys.POSITION_TEX_COLOR);
        } catch (IOException | ShaderLoader.LoadException exception) {
            throw new RuntimeException("Could not preload shaders for loading UI", exception);
        }
    }

    public void tick() {
        this.updateFovMultiplier();
        this.lightmapTextureManager.tick();
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.camera.updateEyeHeight();
        this.firstPersonRenderer.updateHeldItems();
        ++this.ticks;
        if (!this.client.world.getTickManager().shouldTick()) {
            return;
        }
        this.client.worldRenderer.addWeatherParticlesAndSound(this.camera);
        this.lastSkyDarkness = this.skyDarkness;
        if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05f;
            if (this.skyDarkness > 1.0f) {
                this.skyDarkness = 1.0f;
            }
        } else if (this.skyDarkness > 0.0f) {
            this.skyDarkness -= 0.0125f;
        }
        if (this.floatingItemTimeLeft > 0) {
            --this.floatingItemTimeLeft;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }
    }

    @Nullable
    public Identifier getPostProcessorId() {
        return this.postProcessorId;
    }

    public void onResized(int width, int height) {
        this.pool.clear();
        this.client.worldRenderer.onResized(width, height);
    }

    public void updateCrosshairTarget(float tickDelta) {
        Entity entity;
        HitResult lv2;
        Entity lv = this.client.getCameraEntity();
        if (lv == null) {
            return;
        }
        if (this.client.world == null || this.client.player == null) {
            return;
        }
        Profilers.get().push("pick");
        double d = this.client.player.getBlockInteractionRange();
        double e = this.client.player.getEntityInteractionRange();
        this.client.crosshairTarget = lv2 = this.findCrosshairTarget(lv, d, e, tickDelta);
        if (lv2 instanceof EntityHitResult) {
            EntityHitResult lv3 = (EntityHitResult)lv2;
            entity = lv3.getEntity();
        } else {
            entity = null;
        }
        this.client.targetedEntity = entity;
        Profilers.get().pop();
    }

    private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        double g = Math.max(blockInteractionRange, entityInteractionRange);
        double h = MathHelper.square(g);
        Vec3d lv = camera.getCameraPosVec(tickDelta);
        HitResult lv2 = camera.raycast(g, tickDelta, false);
        double i = lv2.getPos().squaredDistanceTo(lv);
        if (lv2.getType() != HitResult.Type.MISS) {
            h = i;
            g = Math.sqrt(h);
        }
        Vec3d lv3 = camera.getRotationVec(tickDelta);
        Vec3d lv4 = lv.add(lv3.x * g, lv3.y * g, lv3.z * g);
        float j = 1.0f;
        Box lv5 = camera.getBoundingBox().stretch(lv3.multiply(g)).expand(1.0, 1.0, 1.0);
        EntityHitResult lv6 = ProjectileUtil.raycast(camera, lv, lv4, lv5, EntityPredicates.CAN_HIT, h);
        if (lv6 != null && lv6.getPos().squaredDistanceTo(lv) < i) {
            return GameRenderer.ensureTargetInRange(lv6, lv, entityInteractionRange);
        }
        return GameRenderer.ensureTargetInRange(lv2, lv, blockInteractionRange);
    }

    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d lv = hitResult.getPos();
        if (!lv.isInRange(cameraPos, interactionRange)) {
            Vec3d lv2 = hitResult.getPos();
            Direction lv3 = Direction.getFacing(lv2.x - cameraPos.x, lv2.y - cameraPos.y, lv2.z - cameraPos.z);
            return BlockHitResult.createMissed(lv2, lv3, BlockPos.ofFloored(lv2));
        }
        return hitResult;
    }

    private void updateFovMultiplier() {
        float g;
        Entity entity = this.client.getCameraEntity();
        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity lv = (AbstractClientPlayerEntity)entity;
            GameOptions lv2 = this.client.options;
            boolean bl = lv2.getPerspective().isFirstPerson();
            float f = lv2.getFovEffectScale().getValue().floatValue();
            g = lv.getFovMultiplier(bl, f);
        } else {
            g = 1.0f;
        }
        this.lastFovMultiplier = this.fovMultiplier;
        this.fovMultiplier += (g - this.fovMultiplier) * 0.5f;
        this.fovMultiplier = MathHelper.clamp(this.fovMultiplier, 0.1f, 1.5f);
    }

    private float getFov(Camera arg, float tickDelta, boolean changingFov) {
        CameraSubmersionType lv2;
        LivingEntity lv;
        Entity entity;
        if (this.renderingPanorama) {
            return 90.0f;
        }
        float g = 70.0f;
        if (changingFov) {
            g = this.client.options.getFov().getValue().intValue();
            g *= MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
        }
        if ((entity = arg.getFocusedEntity()) instanceof LivingEntity && (lv = (LivingEntity)entity).isDead()) {
            float h = Math.min((float)lv.deathTime + tickDelta, 20.0f);
            g /= (1.0f - 500.0f / (h + 500.0f)) * 2.0f + 1.0f;
        }
        if ((lv2 = arg.getSubmersionType()) == CameraSubmersionType.LAVA || lv2 == CameraSubmersionType.WATER) {
            float h = this.client.options.getFovEffectScale().getValue().floatValue();
            g *= MathHelper.lerp(h, 1.0f, 0.85714287f);
        }
        return g;
    }

    private void tiltViewWhenHurt(MatrixStack matrices, float tickDelta) {
        Entity entity = this.client.getCameraEntity();
        if (entity instanceof LivingEntity) {
            float h;
            LivingEntity lv = (LivingEntity)entity;
            float g = (float)lv.hurtTime - tickDelta;
            if (lv.isDead()) {
                h = Math.min((float)lv.deathTime + tickDelta, 20.0f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0f - 8000.0f / (h + 200.0f)));
            }
            if (g < 0.0f) {
                return;
            }
            g /= (float)lv.maxHurtTime;
            g = MathHelper.sin(g * g * g * g * (float)Math.PI);
            h = lv.getDamageTiltYaw();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-h));
            float i = (float)((double)(-g) * 14.0 * this.client.options.getDamageTiltStrength().getValue());
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
        }
    }

    private void bobView(MatrixStack matrices, float tickDelta) {
        Entity entity = this.client.getCameraEntity();
        if (!(entity instanceof AbstractClientPlayerEntity)) {
            return;
        }
        AbstractClientPlayerEntity lv = (AbstractClientPlayerEntity)entity;
        float g = lv.distanceMoved - lv.lastDistanceMoved;
        float h = -(lv.distanceMoved + g * tickDelta);
        float i = MathHelper.lerp(tickDelta, lv.prevStrideDistance, lv.strideDistance);
        matrices.translate(MathHelper.sin(h * (float)Math.PI) * i * 0.5f, -Math.abs(MathHelper.cos(h * (float)Math.PI) * i), 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(h * (float)Math.PI) * i * 3.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(h * (float)Math.PI - 0.2f) * i) * 5.0f));
    }

    public void renderWithZoom(float zoom, float zoomX, float zoomY) {
        this.zoom = zoom;
        this.zoomX = zoomX;
        this.zoomY = zoomY;
        this.setBlockOutlineEnabled(false);
        this.setRenderHand(false);
        this.renderWorld(RenderTickCounter.ZERO);
        this.zoom = 1.0f;
    }

    private void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f) {
        boolean bl;
        if (this.renderingPanorama) {
            return;
        }
        Matrix4f matrix4f2 = this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false));
        RenderSystem.setProjectionMatrix(matrix4f2, ProjectionType.PERSPECTIVE);
        MatrixStack lv = new MatrixStack();
        lv.push();
        lv.multiplyPositionMatrix(matrix4f.invert(new Matrix4f()));
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix().mul(matrix4f);
        this.tiltViewWhenHurt(lv, tickDelta);
        if (this.client.options.getBobView().getValue().booleanValue()) {
            this.bobView(lv, tickDelta);
        }
        boolean bl2 = bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
        if (this.client.options.getPerspective().isFirstPerson() && !bl && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.lightmapTextureManager.enable();
            this.firstPersonRenderer.renderItem(tickDelta, lv, this.buffers.getEntityVertexConsumers(), this.client.player, this.client.getEntityRenderDispatcher().getLight(this.client.player, tickDelta));
            this.lightmapTextureManager.disable();
        }
        matrix4fStack.popMatrix();
        lv.pop();
        if (this.client.options.getPerspective().isFirstPerson() && !bl) {
            VertexConsumerProvider.Immediate lv2 = this.buffers.getEntityVertexConsumers();
            InGameOverlayRenderer.renderOverlays(this.client, lv, lv2);
            lv2.draw();
        }
    }

    public Matrix4f getBasicProjectionMatrix(float fovDegrees) {
        Matrix4f matrix4f = new Matrix4f();
        if (this.zoom != 1.0f) {
            matrix4f.translate(this.zoomX, -this.zoomY, 0.0f);
            matrix4f.scale(this.zoom, this.zoom, 1.0f);
        }
        return matrix4f.perspective(fovDegrees * ((float)Math.PI / 180), (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05f, this.getFarPlaneDistance());
    }

    public float getFarPlaneDistance() {
        return this.viewDistance * 4.0f;
    }

    public static float getNightVisionStrength(LivingEntity entity, float tickDelta) {
        StatusEffectInstance lv = entity.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (!lv.isDurationBelow(200)) {
            return 1.0f;
        }
        return 0.7f + MathHelper.sin(((float)lv.getDuration() - tickDelta) * (float)Math.PI * 0.2f) * 0.3f;
    }

    public void render(RenderTickCounter tickCounter, boolean tick) {
        if (this.client.isWindowFocused() || !this.client.options.pauseOnLostFocus || this.client.options.getTouchscreen().getValue().booleanValue() && this.client.mouse.wasRightButtonClicked()) {
            this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
        } else if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openGameMenu(false);
        }
        if (this.client.skipGameRender) {
            return;
        }
        Profiler lv = Profilers.get();
        boolean bl2 = this.client.isFinishedLoading();
        int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
        int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        if (bl2 && tick && this.client.world != null) {
            lv.push("level");
            this.renderWorld(tickCounter);
            this.updateWorldIcon();
            this.client.worldRenderer.drawEntityOutlinesFramebuffer();
            if (this.postProcessorId != null && this.postProcessorEnabled) {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                RenderSystem.resetTextureMatrix();
                PostEffectProcessor lv2 = this.client.getShaderLoader().loadPostEffect(this.postProcessorId, DefaultFramebufferSet.MAIN_ONLY);
                if (lv2 != null) {
                    lv2.render(this.client.getFramebuffer(), this.pool);
                }
            }
            this.client.getFramebuffer().beginWrite(true);
        }
        Window lv3 = this.client.getWindow();
        RenderSystem.clear(256);
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float)((double)lv3.getFramebufferWidth() / lv3.getScaleFactor()), (float)((double)lv3.getFramebufferHeight() / lv3.getScaleFactor()), 0.0f, 1000.0f, 21000.0f);
        RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.translation(0.0f, 0.0f, -11000.0f);
        DiffuseLighting.enableGuiDepthLighting();
        DrawContext lv4 = new DrawContext(this.client, this.buffers.getEntityVertexConsumers());
        if (bl2 && tick && this.client.world != null) {
            lv.swap("gui");
            if (!this.client.options.hudHidden) {
                this.renderFloatingItem(lv4, tickCounter.getTickDelta(false));
            }
            this.client.inGameHud.render(lv4, tickCounter);
            lv4.draw();
            RenderSystem.clear(256);
            lv.pop();
        }
        if (this.client.getOverlay() != null) {
            try {
                this.client.getOverlay().render(lv4, i, j, tickCounter.getLastFrameDuration());
            } catch (Throwable throwable) {
                CrashReport lv5 = CrashReport.create(throwable, "Rendering overlay");
                CrashReportSection lv6 = lv5.addElement("Overlay render details");
                lv6.add("Overlay name", () -> this.client.getOverlay().getClass().getCanonicalName());
                throw new CrashException(lv5);
            }
        }
        if (bl2 && this.client.currentScreen != null) {
            try {
                this.client.currentScreen.renderWithTooltip(lv4, i, j, tickCounter.getLastFrameDuration());
            } catch (Throwable throwable) {
                CrashReport lv5 = CrashReport.create(throwable, "Rendering screen");
                CrashReportSection lv6 = lv5.addElement("Screen render details");
                lv6.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                lv6.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()));
                lv6.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getWindow().getScaleFactor()));
                throw new CrashException(lv5);
            }
            try {
                if (this.client.currentScreen != null) {
                    this.client.currentScreen.updateNarrator();
                }
            } catch (Throwable throwable) {
                CrashReport lv5 = CrashReport.create(throwable, "Narrating screen");
                CrashReportSection lv6 = lv5.addElement("Screen details");
                lv6.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                throw new CrashException(lv5);
            }
        }
        if (bl2 && tick && this.client.world != null) {
            this.client.inGameHud.renderAutosaveIndicator(lv4, tickCounter);
        }
        if (bl2) {
            try (ScopedProfiler lv7 = lv.scoped("toasts");){
                this.client.getToastManager().draw(lv4);
            }
        }
        lv4.draw();
        matrix4fStack.popMatrix();
        this.pool.decrementLifespan();
    }

    private void updateWorldIcon() {
        if (this.hasWorldIcon || !this.client.isInSingleplayer()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastWorldIconUpdate < 1000L) {
            return;
        }
        this.lastWorldIconUpdate = l;
        IntegratedServer lv = this.client.getServer();
        if (lv == null || lv.isStopped()) {
            return;
        }
        lv.getIconFile().ifPresent(path -> {
            if (Files.isRegularFile(path, new LinkOption[0])) {
                this.hasWorldIcon = true;
            } else {
                this.updateWorldIcon((Path)path);
            }
        });
    }

    private void updateWorldIcon(Path path) {
        if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete()) {
            NativeImage lv = ScreenshotRecorder.takeScreenshot(this.client.getFramebuffer());
            Util.getIoWorkerExecutor().execute(() -> {
                int i = lv.getWidth();
                int j = lv.getHeight();
                int k = 0;
                int l = 0;
                if (i > j) {
                    k = (i - j) / 2;
                    i = j;
                } else {
                    l = (j - i) / 2;
                    j = i;
                }
                try (NativeImage lv = new NativeImage(64, 64, false);){
                    lv.resizeSubRectTo(k, l, i, j, lv);
                    lv.writeTo(path);
                } catch (IOException iOException) {
                    LOGGER.warn("Couldn't save auto screenshot", iOException);
                } finally {
                    lv.close();
                }
            });
        }
    }

    private boolean shouldRenderBlockOutline() {
        boolean bl;
        if (!this.blockOutlineEnabled) {
            return false;
        }
        Entity lv = this.client.getCameraEntity();
        boolean bl2 = bl = lv instanceof PlayerEntity && !this.client.options.hudHidden;
        if (bl && !((PlayerEntity)lv).getAbilities().allowModifyWorld) {
            ItemStack lv2 = ((LivingEntity)lv).getMainHandStack();
            HitResult lv3 = this.client.crosshairTarget;
            if (lv3 != null && lv3.getType() == HitResult.Type.BLOCK) {
                BlockPos lv4 = ((BlockHitResult)lv3).getBlockPos();
                BlockState lv5 = this.client.world.getBlockState(lv4);
                if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                    bl = lv5.createScreenHandlerFactory(this.client.world, lv4) != null;
                } else {
                    CachedBlockPosition lv6 = new CachedBlockPosition(this.client.world, lv4, false);
                    RegistryWrapper.Impl lv7 = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
                    bl = !lv2.isEmpty() && (lv2.canBreak(lv6) || lv2.canPlaceOn(lv6));
                }
            }
        }
        return bl;
    }

    public void renderWorld(RenderTickCounter renderTickCounter) {
        float f = renderTickCounter.getTickDelta(true);
        this.lightmapTextureManager.update(f);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.updateCrosshairTarget(f);
        Profiler lv = Profilers.get();
        lv.push("center");
        boolean bl = this.shouldRenderBlockOutline();
        lv.swap("camera");
        Camera lv2 = this.camera;
        Entity lv3 = this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity();
        float g = this.client.world.getTickManager().shouldSkipTick(lv3) ? 1.0f : f;
        lv2.update(this.client.world, lv3, !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), g);
        this.viewDistance = this.client.options.getClampedViewDistance() * 16;
        float h = this.getFov(lv2, f, true);
        Matrix4f matrix4f = this.getBasicProjectionMatrix(h);
        MatrixStack lv4 = new MatrixStack();
        this.tiltViewWhenHurt(lv4, lv2.getLastTickDelta());
        if (this.client.options.getBobView().getValue().booleanValue()) {
            this.bobView(lv4, lv2.getLastTickDelta());
        }
        matrix4f.mul(lv4.peek().getPositionMatrix());
        float i = this.client.options.getDistortionEffectScale().getValue().floatValue();
        float j = MathHelper.lerp(f, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity) * (i * i);
        if (j > 0.0f) {
            int k = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
            float l = 5.0f / (j * j + 5.0f) - j * 0.04f;
            l *= l;
            Vector3f vector3f = new Vector3f(0.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f);
            float m = ((float)this.ticks + f) * (float)k * ((float)Math.PI / 180);
            matrix4f.rotate(m, vector3f);
            matrix4f.scale(1.0f / l, 1.0f, 1.0f);
            matrix4f.rotate(-m, vector3f);
        }
        float n = Math.max(h, (float)this.client.options.getFov().getValue().intValue());
        Matrix4f matrix4f2 = this.getBasicProjectionMatrix(n);
        RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.PERSPECTIVE);
        Quaternionf quaternionf = lv2.getRotation().conjugate(new Quaternionf());
        Matrix4f matrix4f3 = new Matrix4f().rotation(quaternionf);
        this.client.worldRenderer.setupFrustum(lv2.getPos(), matrix4f3, matrix4f2);
        this.client.getFramebuffer().beginWrite(true);
        this.client.worldRenderer.render(this.pool, renderTickCounter, bl, lv2, this, matrix4f3, matrix4f);
        lv.swap("hand");
        if (this.renderHand) {
            RenderSystem.clear(256);
            this.renderHand(lv2, f, matrix4f3);
        }
        lv.pop();
    }

    public void reset() {
        this.floatingItem = null;
        this.client.getMapTextureManager().clear();
        this.camera.reset();
        this.hasWorldIcon = false;
    }

    public void showFloatingItem(ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        this.floatingItemTimeLeft = 40;
        this.floatingItemWidth = this.random.nextFloat() * 2.0f - 1.0f;
        this.floatingItemHeight = this.random.nextFloat() * 2.0f - 1.0f;
    }

    private void renderFloatingItem(DrawContext context, float tickDelta) {
        if (this.floatingItem == null || this.floatingItemTimeLeft <= 0) {
            return;
        }
        int i = 40 - this.floatingItemTimeLeft;
        float g = ((float)i + tickDelta) / 40.0f;
        float h = g * g;
        float j = g * h;
        float k = 10.25f * j * h - 24.95f * h * h + 25.5f * j - 13.8f * h + 4.0f * g;
        float l = k * (float)Math.PI;
        float m = this.floatingItemWidth * (float)(context.getScaledWindowWidth() / 4);
        float n = this.floatingItemHeight * (float)(context.getScaledWindowHeight() / 4);
        MatrixStack lv = context.getMatrices();
        lv.push();
        lv.translate((float)(context.getScaledWindowWidth() / 2) + m * MathHelper.abs(MathHelper.sin(l * 2.0f)), (float)(context.getScaledWindowHeight() / 2) + n * MathHelper.abs(MathHelper.sin(l * 2.0f)), -50.0f);
        float o = 50.0f + 175.0f * MathHelper.sin(l);
        lv.scale(o, -o, o);
        lv.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0f * MathHelper.abs(MathHelper.sin(l))));
        lv.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f * MathHelper.cos(g * 8.0f)));
        lv.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0f * MathHelper.cos(g * 8.0f)));
        context.draw(vertexConsumers -> this.client.getItemRenderer().renderItem(this.floatingItem, ModelTransformationMode.FIXED, 0xF000F0, OverlayTexture.DEFAULT_UV, lv, (VertexConsumerProvider)vertexConsumers, this.client.world, 0));
        lv.pop();
    }

    public MinecraftClient getClient() {
        return this.client;
    }

    public float getSkyDarkness(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastSkyDarkness, this.skyDarkness);
    }

    public float getViewDistance() {
        return this.viewDistance;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public LightmapTextureManager getLightmapTextureManager() {
        return this.lightmapTextureManager;
    }

    public OverlayTexture getOverlayTexture() {
        return this.overlayTexture;
    }
}

