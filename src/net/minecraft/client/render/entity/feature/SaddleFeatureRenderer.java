/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SaddleableRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SaddleFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends FeatureRenderer<S, M> {
    private final Identifier texture;
    private final M model;
    private final M babyModel;

    public SaddleFeatureRenderer(FeatureRendererContext<S, M> context, M model, M babyModel, Identifier texture) {
        super(context);
        this.model = model;
        this.babyModel = babyModel;
        this.texture = texture;
    }

    public SaddleFeatureRenderer(FeatureRendererContext<S, M> context, M model, Identifier texture) {
        this(context, model, model, texture);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, S arg3, float f, float g) {
        if (!((SaddleableRenderState)arg3).isSaddled()) {
            return;
        }
        M lv = ((LivingEntityRenderState)arg3).baby ? this.babyModel : this.model;
        ((EntityModel)lv).setAngles(arg3);
        VertexConsumer lv2 = arg2.getBuffer(RenderLayer.getEntityCutoutNoCull(this.texture));
        ((Model)lv).render(arg, lv2, i, OverlayTexture.DEFAULT_UV);
    }
}

