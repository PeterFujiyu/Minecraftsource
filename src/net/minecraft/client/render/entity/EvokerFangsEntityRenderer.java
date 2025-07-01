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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerFangsEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity, EvokerFangsEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel model;

    public EvokerFangsEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.model = new EvokerFangsEntityModel(arg.getPart(EntityModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(EvokerFangsEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float f = arg.animationProgress;
        if (f == 0.0f) {
            return;
        }
        arg2.push();
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f - arg.yaw));
        arg2.scale(-1.0f, -1.0f, 1.0f);
        arg2.translate(0.0f, -1.501f, 0.0f);
        this.model.setAngles(arg);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public EvokerFangsEntityRenderState createRenderState() {
        return new EvokerFangsEntityRenderState();
    }

    @Override
    public void updateRenderState(EvokerFangsEntity arg, EvokerFangsEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.yaw = arg.getYaw();
        arg2.animationProgress = arg.getAnimationProgress(f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

