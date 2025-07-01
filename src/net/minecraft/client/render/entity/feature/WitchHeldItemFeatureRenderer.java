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
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer
extends VillagerHeldItemFeatureRenderer<WitchEntityRenderState, WitchEntityModel> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<WitchEntityRenderState, WitchEntityModel> arg) {
        super(arg);
    }

    @Override
    protected void applyTransforms(WitchEntityRenderState arg, MatrixStack arg2) {
        if (arg.holdingPotion) {
            ((WitchEntityModel)this.getContextModel()).getRootPart().rotate(arg2);
            ((WitchEntityModel)this.getContextModel()).getHead().rotate(arg2);
            ((WitchEntityModel)this.getContextModel()).getNose().rotate(arg2);
            arg2.translate(0.0625f, 0.25f, 0.0f);
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(140.0f));
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(10.0f));
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
            return;
        }
        super.applyTransforms(arg, arg2);
    }
}

