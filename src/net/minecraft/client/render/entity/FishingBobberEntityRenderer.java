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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer<FishingBobberEntity, FishingBobberEntityState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private static final double field_33632 = 960.0;

    public FishingBobberEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
    }

    @Override
    public boolean shouldRender(FishingBobberEntity arg, Frustum arg2, double d, double e, double f) {
        return super.shouldRender(arg, arg2, d, e, f) && arg.getPlayerOwner() != null;
    }

    @Override
    public void render(FishingBobberEntityState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.push();
        arg2.scale(0.5f, 0.5f, 0.5f);
        arg2.multiply(this.dispatcher.getRotation());
        MatrixStack.Entry lv = arg2.peek();
        VertexConsumer lv2 = arg3.getBuffer(LAYER);
        FishingBobberEntityRenderer.vertex(lv2, lv, i, 0.0f, 0, 0, 1);
        FishingBobberEntityRenderer.vertex(lv2, lv, i, 1.0f, 0, 1, 1);
        FishingBobberEntityRenderer.vertex(lv2, lv, i, 1.0f, 1, 1, 0);
        FishingBobberEntityRenderer.vertex(lv2, lv, i, 0.0f, 1, 0, 0);
        arg2.pop();
        float f = (float)arg.pos.x;
        float g = (float)arg.pos.y;
        float h = (float)arg.pos.z;
        VertexConsumer lv3 = arg3.getBuffer(RenderLayer.getLineStrip());
        MatrixStack.Entry lv4 = arg2.peek();
        int j = 16;
        for (int k = 0; k <= 16; ++k) {
            FishingBobberEntityRenderer.renderFishingLine(f, g, h, lv3, lv4, FishingBobberEntityRenderer.percentage(k, 16), FishingBobberEntityRenderer.percentage(k + 1, 16));
        }
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    public static Arm getArmHoldingRod(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof FishingRodItem ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    private Vec3d getHandPos(PlayerEntity player, float f, float tickDelta) {
        int i;
        int n = i = FishingBobberEntityRenderer.getArmHoldingRod(player) == Arm.RIGHT ? 1 : -1;
        if (!this.dispatcher.gameOptions.getPerspective().isFirstPerson() || player != MinecraftClient.getInstance().player) {
            float h = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * ((float)Math.PI / 180);
            double d = MathHelper.sin(h);
            double e = MathHelper.cos(h);
            float j = player.getScale();
            double k = (double)i * 0.35 * (double)j;
            double l = 0.8 * (double)j;
            float m = player.isInSneakingPose() ? -0.1875f : 0.0f;
            return player.getCameraPosVec(tickDelta).add(-e * k - d * l, (double)m - 0.45 * (double)j, -d * k + e * l);
        }
        double n2 = 960.0 / (double)this.dispatcher.gameOptions.getFov().getValue().intValue();
        Vec3d lv = this.dispatcher.camera.getProjection().getPosition((float)i * 0.525f, -0.1f).multiply(n2).rotateY(f * 0.5f).rotateX(-f * 0.7f);
        return player.getCameraPosVec(tickDelta).add(lv);
    }

    private static float percentage(int value, int denominator) {
        return (float)value / (float)denominator;
    }

    private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(Colors.WHITE).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float k = x * segmentStart;
        float l = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f;
        float m = z * segmentStart;
        float n = x * segmentEnd - k;
        float o = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - l;
        float p = z * segmentEnd - m;
        float q = MathHelper.sqrt(n * n + o * o + p * p);
        buffer.vertex(matrices, k, l, m).color(Colors.BLACK).normal(matrices, n /= q, o /= q, p /= q);
    }

    @Override
    public FishingBobberEntityState createRenderState() {
        return new FishingBobberEntityState();
    }

    @Override
    public void updateRenderState(FishingBobberEntity arg, FishingBobberEntityState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        PlayerEntity lv = arg.getPlayerOwner();
        if (lv == null) {
            arg2.pos = Vec3d.ZERO;
            return;
        }
        float g = lv.getHandSwingProgress(f);
        float h = MathHelper.sin(MathHelper.sqrt(g) * (float)Math.PI);
        Vec3d lv2 = this.getHandPos(lv, h, f);
        Vec3d lv3 = arg.getLerpedPos(f).add(0.0, 0.25, 0.0);
        arg2.pos = lv2.subtract(lv3);
    }

    @Override
    protected boolean canBeCulled(FishingBobberEntity arg) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean canBeCulled(Entity entity) {
        return this.canBeCulled((FishingBobberEntity)entity);
    }
}

