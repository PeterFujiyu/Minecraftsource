/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class EntityRenderDispatcher
implements SynchronousResourceReloader {
    private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(Identifier.ofVanilla("textures/misc/shadow.png"));
    private static final float field_43377 = 32.0f;
    private static final float field_43378 = 0.5f;
    private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers = Map.of();
    public final TextureManager textureManager;
    private World world;
    public Camera camera;
    private Quaternionf rotation;
    public Entity targetedEntity;
    private final ItemModelManager itemModelManager;
    private final MapRenderer mapRenderer;
    private final BlockRenderManager blockRenderManager;
    private final HeldItemRenderer heldItemRenderer;
    private final TextRenderer textRenderer;
    public final GameOptions gameOptions;
    private final Supplier<LoadedEntityModels> entityModelsGetter;
    private final EquipmentModelLoader equipmentModelLoader;
    private boolean renderShadows = true;
    private boolean renderHitboxes;

    public <E extends Entity> int getLight(E entity, float tickDelta) {
        return this.getRenderer(entity).getLight(entity, tickDelta);
    }

    public EntityRenderDispatcher(MinecraftClient client, TextureManager textureManager, ItemModelManager itemModelManager, ItemRenderer itemRenderer, MapRenderer mapRenderer, BlockRenderManager blockRenderManager, TextRenderer textRenderer, GameOptions gameOptions, Supplier<LoadedEntityModels> entityModelsGetter, EquipmentModelLoader equipmentModelLoader) {
        this.textureManager = textureManager;
        this.itemModelManager = itemModelManager;
        this.mapRenderer = mapRenderer;
        this.heldItemRenderer = new HeldItemRenderer(client, this, itemRenderer, itemModelManager);
        this.blockRenderManager = blockRenderManager;
        this.textRenderer = textRenderer;
        this.gameOptions = gameOptions;
        this.entityModelsGetter = entityModelsGetter;
        this.equipmentModelLoader = equipmentModelLoader;
    }

    public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity lv = (AbstractClientPlayerEntity)entity;
            SkinTextures.Model lv2 = lv.getSkinTextures().model();
            EntityRenderer<? extends PlayerEntity, ?> lv3 = this.modelRenderers.get((Object)lv2);
            if (lv3 != null) {
                return lv3;
            }
            return this.modelRenderers.get((Object)SkinTextures.Model.WIDE);
        }
        return this.renderers.get(entity.getType());
    }

    public void configure(World world, Camera camera, Entity target) {
        this.world = world;
        this.camera = camera;
        this.rotation = camera.getRotation();
        this.targetedEntity = target;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setRenderHitboxes(boolean renderHitboxes) {
        this.renderHitboxes = renderHitboxes;
    }

    public boolean shouldRenderHitboxes() {
        return this.renderHitboxes;
    }

    public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double x, double y, double z) {
        EntityRenderer<E, ?> lv = this.getRenderer(entity);
        return lv.shouldRender(entity, frustum, x, y, z);
    }

    public <E extends Entity> void render(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderer<E, ?> lv = this.getRenderer(entity);
        this.render(entity, x, y, z, tickDelta, matrices, vertexConsumers, light, lv);
    }

    private <E extends Entity, S extends EntityRenderState> void render(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer) {
        try {
            double m;
            float n;
            float l;
            S lv = renderer.getAndUpdateRenderState(entity, tickDelta);
            Vec3d lv2 = renderer.getPositionOffset(lv);
            double h = x + lv2.getX();
            double j = y + lv2.getY();
            double k = z + lv2.getZ();
            matrices.push();
            matrices.translate(h, j, k);
            renderer.render(lv, matrices, vertexConsumers, light);
            if (((EntityRenderState)lv).onFire) {
                this.renderFire(matrices, vertexConsumers, (EntityRenderState)lv, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
            }
            if (entity instanceof PlayerEntity) {
                matrices.translate(-lv2.getX(), -lv2.getY(), -lv2.getZ());
            }
            if (this.gameOptions.getEntityShadows().getValue().booleanValue() && this.renderShadows && !((EntityRenderState)lv).invisible && (l = renderer.getShadowRadius(lv)) > 0.0f && (n = (float)((1.0 - (m = ((EntityRenderState)lv).squaredDistanceToCamera) / 256.0) * (double)renderer.getShadowOpacity(lv))) > 0.0f) {
                EntityRenderDispatcher.renderShadow(matrices, vertexConsumers, lv, n, tickDelta, this.world, Math.min(l, 32.0f));
            }
            if (!(entity instanceof PlayerEntity)) {
                matrices.translate(-lv2.getX(), -lv2.getY(), -lv2.getZ());
            }
            if (this.renderHitboxes && !((EntityRenderState)lv).invisible && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
                EntityRenderDispatcher.renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta, 1.0f, 1.0f, 1.0f);
            }
            matrices.pop();
        } catch (Throwable throwable) {
            CrashReport lv3 = CrashReport.create(throwable, "Rendering entity in world");
            CrashReportSection lv4 = lv3.addElement("Entity being rendered");
            entity.populateCrashReport(lv4);
            CrashReportSection lv5 = lv3.addElement("Renderer details");
            lv5.add("Assigned renderer", renderer);
            lv5.add("Location", CrashReportSection.createPositionString((HeightLimitView)this.world, x, y, z));
            lv5.add("Delta", Float.valueOf(tickDelta));
            throw new CrashException(lv3);
        }
    }

    private static void renderServerSideHitbox(MatrixStack matrices, Entity entity, VertexConsumerProvider vertexConsumers) {
        Entity lv = EntityRenderDispatcher.getIntegratedServerEntity(entity);
        if (lv == null) {
            DebugRenderer.drawString(matrices, vertexConsumers, "Missing", entity.getX(), entity.getBoundingBox().maxY + 1.5, entity.getZ(), Colors.RED);
            return;
        }
        matrices.push();
        matrices.translate(lv.getX() - entity.getX(), lv.getY() - entity.getY(), lv.getZ() - entity.getZ());
        EntityRenderDispatcher.renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), lv, 1.0f, 0.0f, 1.0f, 0.0f);
        VertexRendering.drawVector(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), new Vector3f(), lv.getVelocity(), -256);
        matrices.pop();
    }

    @Nullable
    private static Entity getIntegratedServerEntity(Entity entity) {
        ServerWorld lv2;
        IntegratedServer lv = MinecraftClient.getInstance().getServer();
        if (lv != null && (lv2 = lv.getWorld(entity.getWorld().getRegistryKey())) != null) {
            return lv2.getEntityById(entity.getId());
        }
        return null;
    }

    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue) {
        Entity lv3;
        Box lv = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        VertexRendering.drawBox(matrices, vertices, lv, red, green, blue, 1.0f);
        if (entity instanceof EnderDragonEntity) {
            double d = -MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
            double e = -MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
            double j = -MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
            for (EnderDragonPart lv2 : ((EnderDragonEntity)entity).getBodyParts()) {
                matrices.push();
                double k = d + MathHelper.lerp((double)tickDelta, lv2.lastRenderX, lv2.getX());
                double l = e + MathHelper.lerp((double)tickDelta, lv2.lastRenderY, lv2.getY());
                double m = j + MathHelper.lerp((double)tickDelta, lv2.lastRenderZ, lv2.getZ());
                matrices.translate(k, l, m);
                VertexRendering.drawBox(matrices, vertices, lv2.getBoundingBox().offset(-lv2.getX(), -lv2.getY(), -lv2.getZ()), 0.25f, 1.0f, 0.0f, 1.0f);
                matrices.pop();
            }
        }
        if (entity instanceof LivingEntity) {
            float n = 0.01f;
            VertexRendering.drawBox(matrices, vertices, lv.minX, entity.getStandingEyeHeight() - 0.01f, lv.minZ, lv.maxX, entity.getStandingEyeHeight() + 0.01f, lv.maxZ, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        if ((lv3 = entity.getVehicle()) != null) {
            float o = Math.min(lv3.getWidth(), entity.getWidth()) / 2.0f;
            float p = 0.0625f;
            Vec3d lv4 = lv3.getPassengerRidingPos(entity).subtract(entity.getPos());
            VertexRendering.drawBox(matrices, vertices, lv4.x - (double)o, lv4.y, lv4.z - (double)o, lv4.x + (double)o, lv4.y + 0.0625, lv4.z + (double)o, 1.0f, 1.0f, 0.0f, 1.0f);
        }
        VertexRendering.drawVector(matrices, vertices, new Vector3f(0.0f, entity.getStandingEyeHeight(), 0.0f), entity.getRotationVec(tickDelta).multiply(2.0), -16776961);
    }

    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation) {
        Sprite lv = ModelBaker.FIRE_0.getSprite();
        Sprite lv2 = ModelBaker.FIRE_1.getSprite();
        matrices.push();
        float f = renderState.width * 1.4f;
        matrices.scale(f, f, f);
        float g = 0.5f;
        float h = 0.0f;
        float i = renderState.height / f;
        float j = 0.0f;
        matrices.multiply(rotation);
        matrices.translate(0.0f, 0.0f, 0.3f - (float)((int)i) * 0.02f);
        float k = 0.0f;
        int l = 0;
        VertexConsumer lv3 = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
        MatrixStack.Entry lv4 = matrices.peek();
        while (i > 0.0f) {
            Sprite lv5 = l % 2 == 0 ? lv : lv2;
            float m = lv5.getMinU();
            float n = lv5.getMinV();
            float o = lv5.getMaxU();
            float p = lv5.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, -g - 0.0f, 0.0f - j, k, o, p);
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, g - 0.0f, 0.0f - j, k, m, p);
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, g - 0.0f, 1.4f - j, k, m, n);
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, -g - 0.0f, 1.4f - j, k, o, n);
            i -= 0.45f;
            j -= 0.45f;
            g *= 0.9f;
            k -= 0.03f;
            ++l;
        }
        matrices.pop();
    }

    private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(entry, x, y, z).color(Colors.WHITE).texture(u, v).overlay(0, 10).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(entry, 0.0f, 1.0f, 0.0f);
    }

    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, WorldView world, float radius) {
        float i = Math.min(opacity / 0.5f, radius);
        int j = MathHelper.floor(renderState.x - (double)radius);
        int k = MathHelper.floor(renderState.x + (double)radius);
        int l = MathHelper.floor(renderState.y - (double)i);
        int m = MathHelper.floor(renderState.y);
        int n = MathHelper.floor(renderState.z - (double)radius);
        int o = MathHelper.floor(renderState.z + (double)radius);
        MatrixStack.Entry lv = matrices.peek();
        VertexConsumer lv2 = vertexConsumers.getBuffer(SHADOW_LAYER);
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int p = n; p <= o; ++p) {
            for (int q = j; q <= k; ++q) {
                lv3.set(q, 0, p);
                Chunk lv4 = world.getChunk(lv3);
                for (int r = l; r <= m; ++r) {
                    lv3.setY(r);
                    float s = opacity - (float)(renderState.y - (double)lv3.getY()) * 0.5f;
                    EntityRenderDispatcher.renderShadowPart(lv, lv2, lv4, world, lv3, renderState.x, renderState.y, renderState.z, radius, s);
                }
            }
        }
    }

    private static void renderShadowPart(MatrixStack.Entry entry, VertexConsumer vertices, Chunk chunk, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity) {
        BlockPos lv = pos.down();
        BlockState lv2 = chunk.getBlockState(lv);
        if (lv2.getRenderType() == BlockRenderType.INVISIBLE || world.getLightLevel(pos) <= 3) {
            return;
        }
        if (!lv2.isFullCube(chunk, lv)) {
            return;
        }
        VoxelShape lv3 = lv2.getOutlineShape(chunk, lv);
        if (lv3.isEmpty()) {
            return;
        }
        float i = LightmapTextureManager.getBrightness(world.getDimension(), world.getLightLevel(pos));
        float j = opacity * 0.5f * i;
        if (j >= 0.0f) {
            if (j > 1.0f) {
                j = 1.0f;
            }
            int k = ColorHelper.getArgb(MathHelper.floor(j * 255.0f), 255, 255, 255);
            Box lv4 = lv3.getBoundingBox();
            double l = (double)pos.getX() + lv4.minX;
            double m = (double)pos.getX() + lv4.maxX;
            double n = (double)pos.getY() + lv4.minY;
            double o = (double)pos.getZ() + lv4.minZ;
            double p = (double)pos.getZ() + lv4.maxZ;
            float q = (float)(l - x);
            float r = (float)(m - x);
            float s = (float)(n - y);
            float t = (float)(o - z);
            float u = (float)(p - z);
            float v = -q / 2.0f / radius + 0.5f;
            float w = -r / 2.0f / radius + 0.5f;
            float x2 = -t / 2.0f / radius + 0.5f;
            float y2 = -u / 2.0f / radius + 0.5f;
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, k, q, s, t, v, x2);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, k, q, s, u, v, y2);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, k, r, s, u, w, y2);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, k, r, s, t, w, x2);
        }
    }

    private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, int color, float x, float y, float z, float u, float v) {
        Vector3f vector3f = entry.getPositionMatrix().transformPosition(x, y, z, new Vector3f());
        vertices.vertex(vector3f.x(), vector3f.y(), vector3f.z(), color, u, v, OverlayTexture.DEFAULT_UV, 0xF000F0, 0.0f, 1.0f, 0.0f);
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    public double getSquaredDistanceToCamera(Entity entity) {
        return this.camera.getPos().squaredDistanceTo(entity.getPos());
    }

    public double getSquaredDistanceToCamera(double x, double y, double z) {
        return this.camera.getPos().squaredDistanceTo(x, y, z);
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public HeldItemRenderer getHeldItemRenderer() {
        return this.heldItemRenderer;
    }

    @Override
    public void reload(ResourceManager manager) {
        EntityRendererFactory.Context lv = new EntityRendererFactory.Context(this, this.itemModelManager, this.mapRenderer, this.blockRenderManager, manager, this.entityModelsGetter.get(), this.equipmentModelLoader, this.textRenderer);
        this.renderers = EntityRenderers.reloadEntityRenderers(lv);
        this.modelRenderers = EntityRenderers.reloadPlayerRenderers(lv);
    }
}

