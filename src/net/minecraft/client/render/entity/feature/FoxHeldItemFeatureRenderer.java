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
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer
extends FeatureRenderer<FoxEntityRenderState, FoxEntityModel> {
    public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntityRenderState, FoxEntityModel> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, FoxEntityRenderState arg3, float f, float g) {
        ItemRenderState lv = arg3.itemRenderState;
        if (lv.isEmpty()) {
            return;
        }
        boolean bl = arg3.sleeping;
        boolean bl2 = arg3.baby;
        arg.push();
        arg.translate(((FoxEntityModel)this.getContextModel()).head.pivotX / 16.0f, ((FoxEntityModel)this.getContextModel()).head.pivotY / 16.0f, ((FoxEntityModel)this.getContextModel()).head.pivotZ / 16.0f);
        if (bl2) {
            float h = 0.75f;
            arg.scale(0.75f, 0.75f, 0.75f);
        }
        arg.multiply(RotationAxis.POSITIVE_Z.rotation(arg3.headRoll));
        arg.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f));
        arg.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g));
        if (arg3.baby) {
            if (bl) {
                arg.translate(0.4f, 0.26f, 0.15f);
            } else {
                arg.translate(0.06f, 0.26f, -0.5f);
            }
        } else if (bl) {
            arg.translate(0.46f, 0.26f, 0.22f);
        } else {
            arg.translate(0.06f, 0.27f, -0.5f);
        }
        arg.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        if (bl) {
            arg.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
        lv.render(arg, arg2, i, OverlayTexture.DEFAULT_UV);
        arg.pop();
    }
}

