/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
    protected static final float field_32921 = 0.025f;
    public static final int field_52257 = 24;
    protected final EntityRenderDispatcher dispatcher;
    private final TextRenderer textRenderer;
    protected float shadowRadius;
    protected float shadowOpacity = 1.0f;
    private final S state = this.createRenderState();

    protected EntityRenderer(EntityRendererFactory.Context context) {
        this.dispatcher = context.getRenderDispatcher();
        this.textRenderer = context.getTextRenderer();
    }

    public final int getLight(T entity, float tickDelta) {
        BlockPos lv = BlockPos.ofFloored(((Entity)entity).getClientCameraPosVec(tickDelta));
        return LightmapTextureManager.pack(this.getBlockLight(entity, lv), this.getSkyLight(entity, lv));
    }

    protected int getSkyLight(T entity, BlockPos pos) {
        return ((Entity)entity).getWorld().getLightLevel(LightType.SKY, pos);
    }

    protected int getBlockLight(T entity, BlockPos pos) {
        if (((Entity)entity).isOnFire()) {
            return 15;
        }
        return ((Entity)entity).getWorld().getLightLevel(LightType.BLOCK, pos);
    }

    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        Leashable lv2;
        Entity lv3;
        if (!((Entity)entity).shouldRender(x, y, z)) {
            return false;
        }
        if (!this.canBeCulled(entity)) {
            return true;
        }
        Box lv = this.getBoundingBox(entity).expand(0.5);
        if (lv.isNaN() || lv.getAverageSideLength() == 0.0) {
            lv = new Box(((Entity)entity).getX() - 2.0, ((Entity)entity).getY() - 2.0, ((Entity)entity).getZ() - 2.0, ((Entity)entity).getX() + 2.0, ((Entity)entity).getY() + 2.0, ((Entity)entity).getZ() + 2.0);
        }
        if (frustum.isVisible(lv)) {
            return true;
        }
        if (entity instanceof Leashable && (lv3 = (lv2 = (Leashable)entity).getLeashHolder()) != null) {
            return frustum.isVisible(this.dispatcher.getRenderer(lv3).getBoundingBox(lv3));
        }
        return false;
    }

    protected Box getBoundingBox(T entity) {
        return ((Entity)entity).getBoundingBox();
    }

    protected boolean canBeCulled(T entity) {
        return true;
    }

    public Vec3d getPositionOffset(S state) {
        if (((EntityRenderState)state).positionOffset != null) {
            return ((EntityRenderState)state).positionOffset;
        }
        return Vec3d.ZERO;
    }

    public void render(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderState.LeashData lv = ((EntityRenderState)state).leashData;
        if (lv != null) {
            EntityRenderer.renderLeash(matrices, vertexConsumers, lv);
        }
        if (((EntityRenderState)state).displayName != null) {
            this.renderLabelIfPresent(state, ((EntityRenderState)state).displayName, matrices, vertexConsumers, light);
        }
    }

    private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState.LeashData leashData) {
        int m;
        float f = 0.025f;
        float g = (float)(leashData.endPos.x - leashData.startPos.x);
        float h = (float)(leashData.endPos.y - leashData.startPos.y);
        float i = (float)(leashData.endPos.z - leashData.startPos.z);
        float j = MathHelper.inverseSqrt(g * g + i * i) * 0.025f / 2.0f;
        float k = i * j;
        float l = g * j;
        matrices.push();
        matrices.translate(leashData.offset);
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (m = 0; m <= 24; ++m) {
            EntityRenderer.renderLeashSegment(lv, matrix4f, g, h, i, leashData.leashedEntityBlockLight, leashData.leashHolderBlockLight, leashData.leashedEntitySkyLight, leashData.leashHolderSkyLight, 0.025f, 0.025f, k, l, m, false);
        }
        for (m = 24; m >= 0; --m) {
            EntityRenderer.renderLeashSegment(lv, matrix4f, g, h, i, leashData.leashedEntityBlockLight, leashData.leashHolderBlockLight, leashData.leashedEntitySkyLight, leashData.leashHolderSkyLight, 0.025f, 0.0f, k, l, m, true);
        }
        matrices.pop();
    }

    private static void renderLeashSegment(VertexConsumer vertexConsumer, Matrix4f matrix, float leashedEntityX, float leashedEntityY, float leashedEntityZ, int leashedEntityBlockLight, int leashHolderBlockLight, int leashedEntitySkyLight, int leashHolderSkyLight, float m, float n, float o, float p, int segmentIndex, boolean isLeashKnot) {
        float r = (float)segmentIndex / 24.0f;
        int s = (int)MathHelper.lerp(r, (float)leashedEntityBlockLight, (float)leashHolderBlockLight);
        int t = (int)MathHelper.lerp(r, (float)leashedEntitySkyLight, (float)leashHolderSkyLight);
        int u = LightmapTextureManager.pack(s, t);
        float v = segmentIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1.0f;
        float w = 0.5f * v;
        float x = 0.4f * v;
        float y = 0.3f * v;
        float z = leashedEntityX * r;
        float aa = leashedEntityY > 0.0f ? leashedEntityY * r * r : leashedEntityY - leashedEntityY * (1.0f - r) * (1.0f - r);
        float ab = leashedEntityZ * r;
        vertexConsumer.vertex(matrix, z - o, aa + n, ab + p).color(w, x, y, 1.0f).light(u);
        vertexConsumer.vertex(matrix, z + o, aa + m - n, ab - p).color(w, x, y, 1.0f).light(u);
    }

    protected boolean hasLabel(T entity, double squaredDistanceToCamera) {
        return ((Entity)entity).shouldRenderName() || ((Entity)entity).hasCustomName() && entity == this.dispatcher.targetedEntity;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    protected void renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d lv = ((EntityRenderState)state).nameLabelPos;
        if (lv == null) {
            return;
        }
        boolean bl = !((EntityRenderState)state).sneaking;
        int j = "deadmau5".equals(text.getString()) ? -10 : 0;
        matrices.push();
        matrices.translate(lv.x, lv.y + 0.5, lv.z);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer lv2 = this.getTextRenderer();
        float f = (float)(-lv2.getWidth(text)) / 2.0f;
        int k = (int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255.0f) << 24;
        lv2.draw(text, f, (float)j, -2130706433, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, k, light);
        if (bl) {
            lv2.draw(text, f, (float)j, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.applyEmission(light, 2));
        }
        matrices.pop();
    }

    @Nullable
    protected Text getDisplayName(T entity) {
        return ((Entity)entity).getDisplayName();
    }

    protected float getShadowRadius(S state) {
        return this.shadowRadius;
    }

    protected float getShadowOpacity(S state) {
        return this.shadowOpacity;
    }

    public abstract S createRenderState();

    public final S getAndUpdateRenderState(T entity, float tickDelta) {
        S lv = this.state;
        this.updateRenderState(entity, lv, tickDelta);
        return lv;
    }

    public void updateRenderState(T entity, S state, float tickDelta) {
        Entity lv4;
        boolean bl;
        ExperimentalMinecartController lv2;
        AbstractMinecartEntity lv;
        Object object;
        ((EntityRenderState)state).x = MathHelper.lerp((double)tickDelta, ((Entity)entity).lastRenderX, ((Entity)entity).getX());
        ((EntityRenderState)state).y = MathHelper.lerp((double)tickDelta, ((Entity)entity).lastRenderY, ((Entity)entity).getY());
        ((EntityRenderState)state).z = MathHelper.lerp((double)tickDelta, ((Entity)entity).lastRenderZ, ((Entity)entity).getZ());
        ((EntityRenderState)state).invisible = ((Entity)entity).isInvisible();
        ((EntityRenderState)state).age = (float)((Entity)entity).age + tickDelta;
        ((EntityRenderState)state).width = ((Entity)entity).getWidth();
        ((EntityRenderState)state).height = ((Entity)entity).getHeight();
        ((EntityRenderState)state).standingEyeHeight = ((Entity)entity).getStandingEyeHeight();
        if (((Entity)entity).hasVehicle() && (object = ((Entity)entity).getVehicle()) instanceof AbstractMinecartEntity && (object = (lv = (AbstractMinecartEntity)object).getController()) instanceof ExperimentalMinecartController && (lv2 = (ExperimentalMinecartController)object).hasCurrentLerpSteps()) {
            double d = MathHelper.lerp((double)tickDelta, lv.lastRenderX, lv.getX());
            double e = MathHelper.lerp((double)tickDelta, lv.lastRenderY, lv.getY());
            double g = MathHelper.lerp((double)tickDelta, lv.lastRenderZ, lv.getZ());
            ((EntityRenderState)state).positionOffset = lv2.getLerpedPosition(tickDelta).subtract(new Vec3d(d, e, g));
        } else {
            ((EntityRenderState)state).positionOffset = null;
        }
        ((EntityRenderState)state).squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera((Entity)entity);
        boolean bl2 = bl = ((EntityRenderState)state).squaredDistanceToCamera < 4096.0 && this.hasLabel(entity, ((EntityRenderState)state).squaredDistanceToCamera);
        if (bl) {
            ((EntityRenderState)state).displayName = this.getDisplayName(entity);
            ((EntityRenderState)state).nameLabelPos = ((Entity)entity).getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, ((Entity)entity).getLerpedYaw(tickDelta));
        } else {
            ((EntityRenderState)state).displayName = null;
        }
        ((EntityRenderState)state).sneaking = ((Entity)entity).isSneaky();
        if (entity instanceof Leashable) {
            Leashable lv3 = (Leashable)entity;
            v1 = lv3.getLeashHolder();
        } else {
            v1 = lv4 = null;
        }
        if (lv4 != null) {
            float h = ((Entity)entity).lerpYaw(tickDelta) * ((float)Math.PI / 180);
            Vec3d lv5 = ((Entity)entity).getLeashOffset(tickDelta).rotateY(-h);
            BlockPos lv6 = BlockPos.ofFloored(((Entity)entity).getCameraPosVec(tickDelta));
            BlockPos lv7 = BlockPos.ofFloored(lv4.getCameraPosVec(tickDelta));
            if (((EntityRenderState)state).leashData == null) {
                ((EntityRenderState)state).leashData = new EntityRenderState.LeashData();
            }
            EntityRenderState.LeashData lv8 = ((EntityRenderState)state).leashData;
            lv8.offset = lv5;
            lv8.startPos = ((Entity)entity).getLerpedPos(tickDelta).add(lv5);
            lv8.endPos = lv4.getLeashPos(tickDelta);
            lv8.leashedEntityBlockLight = this.getBlockLight(entity, lv6);
            lv8.leashHolderBlockLight = this.dispatcher.getRenderer(lv4).getBlockLight(lv4, lv7);
            lv8.leashedEntitySkyLight = ((Entity)entity).getWorld().getLightLevel(LightType.SKY, lv6);
            lv8.leashHolderSkyLight = ((Entity)entity).getWorld().getLightLevel(LightType.SKY, lv7);
        } else {
            ((EntityRenderState)state).leashData = null;
        }
        ((EntityRenderState)state).onFire = ((Entity)entity).doesRenderOnFire();
    }
}

