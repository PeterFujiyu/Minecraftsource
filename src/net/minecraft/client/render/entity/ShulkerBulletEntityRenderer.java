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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityRenderer
extends EntityRenderer<ShulkerBulletEntity, ShulkerBulletEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/shulker/spark.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
    private final ShulkerBulletEntityModel model;

    public ShulkerBulletEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.model = new ShulkerBulletEntityModel(arg.getPart(EntityModelLayers.SHULKER_BULLET));
    }

    @Override
    protected int getBlockLight(ShulkerBulletEntity arg, BlockPos arg2) {
        return 15;
    }

    @Override
    public void render(ShulkerBulletEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        float f = arg.age;
        arg2.translate(0.0f, 0.15f, 0.0f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(f * 0.1f) * 180.0f));
        arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.cos(f * 0.1f) * 180.0f));
        arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(f * 0.15f) * 360.0f));
        arg2.scale(-0.5f, -0.5f, 0.5f);
        this.model.setAngles(arg);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV);
        arg2.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer lv2 = arg3.getBuffer(LAYER);
        this.model.render(arg2, lv2, i, OverlayTexture.DEFAULT_UV, 0x26FFFFFF);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public ShulkerBulletEntityRenderState createRenderState() {
        return new ShulkerBulletEntityRenderState();
    }

    @Override
    public void updateRenderState(ShulkerBulletEntity arg, ShulkerBulletEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.yaw = arg.getLerpedYaw(f);
        arg2.pitch = arg.getLerpedPitch(f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

