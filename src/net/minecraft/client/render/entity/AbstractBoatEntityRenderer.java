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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBoatEntityRenderer
extends EntityRenderer<AbstractBoatEntity, BoatEntityRenderState> {
    public AbstractBoatEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(BoatEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.translate(0.0f, 0.375f, 0.0f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - arg.yaw));
        float f = arg.damageWobbleTicks;
        if (f > 0.0f) {
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(f) * f * arg.damageWobbleStrength / 10.0f * (float)arg.damageWobbleSide));
        }
        if (!MathHelper.approximatelyEquals(arg.bubbleWobble, 0.0f)) {
            arg2.multiply(new Quaternionf().setAngleAxis(arg.bubbleWobble * ((float)Math.PI / 180), 1.0f, 0.0f, 1.0f));
        }
        arg2.scale(-1.0f, -1.0f, 1.0f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        EntityModel<BoatEntityRenderState> lv = this.getModel();
        lv.setAngles(arg);
        VertexConsumer lv2 = arg3.getBuffer(this.getRenderLayer());
        lv.render(arg2, lv2, i, OverlayTexture.DEFAULT_UV);
        this.renderWaterMask(arg, arg2, arg3, i);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    protected void renderWaterMask(BoatEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }

    protected abstract EntityModel<BoatEntityRenderState> getModel();

    protected abstract RenderLayer getRenderLayer();

    @Override
    public BoatEntityRenderState createRenderState() {
        return new BoatEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractBoatEntity arg, BoatEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.yaw = arg.getLerpedYaw(f);
        arg2.damageWobbleTicks = (float)arg.getDamageWobbleTicks() - f;
        arg2.damageWobbleSide = arg.getDamageWobbleSide();
        arg2.damageWobbleStrength = Math.max(arg.getDamageWobbleStrength() - f, 0.0f);
        arg2.bubbleWobble = arg.lerpBubbleWobble(f);
        arg2.submergedInWater = arg.isSubmergedInWater();
        arg2.leftPaddleAngle = arg.lerpPaddlePhase(0, f);
        arg2.rightPaddleAngle = arg.lerpPaddlePhase(1, f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

