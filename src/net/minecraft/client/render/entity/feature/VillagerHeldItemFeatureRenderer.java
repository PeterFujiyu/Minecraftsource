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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<S extends ItemHolderEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    public VillagerHeldItemFeatureRenderer(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, S arg3, float f, float g) {
        ItemRenderState lv = ((ItemHolderEntityRenderState)arg3).itemRenderState;
        if (lv.isEmpty()) {
            return;
        }
        arg.push();
        this.applyTransforms(arg3, arg);
        lv.render(arg, arg2, i, OverlayTexture.DEFAULT_UV);
        arg.pop();
    }

    protected void applyTransforms(S state, MatrixStack matrices) {
        ((ModelWithHat)this.getContextModel()).rotateArms(matrices);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(0.75f));
        matrices.scale(1.07f, 1.07f, 1.07f);
        matrices.translate(0.0f, 0.13f, -0.34f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float)Math.PI));
    }
}

