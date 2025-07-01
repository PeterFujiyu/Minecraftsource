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
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LlamaSpitEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityRenderer
extends EntityRenderer<LlamaSpitEntity, LlamaSpitEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/llama/spit.png");
    private final LlamaSpitEntityModel model;

    public LlamaSpitEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.model = new LlamaSpitEntityModel(arg.getPart(EntityModelLayers.LLAMA_SPIT));
    }

    @Override
    public void render(LlamaSpitEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.translate(0.0f, 0.15f, 0.0f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arg.yaw - 90.0f));
        arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arg.pitch));
        this.model.setAngles(arg);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public LlamaSpitEntityRenderState createRenderState() {
        return new LlamaSpitEntityRenderState();
    }

    @Override
    public void updateRenderState(LlamaSpitEntity arg, LlamaSpitEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.pitch = arg.getLerpedPitch(f);
        arg2.yaw = arg.getLerpedYaw(f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

