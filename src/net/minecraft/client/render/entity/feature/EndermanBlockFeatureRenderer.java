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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class EndermanBlockFeatureRenderer
extends FeatureRenderer<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
    private final BlockRenderManager blockRenderManager;

    public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, EndermanEntityRenderState arg3, float f, float g) {
        BlockState lv = arg3.carriedBlock;
        if (lv == null) {
            return;
        }
        arg.push();
        arg.translate(0.0f, 0.6875f, -0.75f);
        arg.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20.0f));
        arg.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));
        arg.translate(0.25f, 0.1875f, 0.25f);
        float h = 0.5f;
        arg.scale(-0.5f, -0.5f, 0.5f);
        arg.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        this.blockRenderManager.renderBlockAsEntity(lv, arg, arg2, i, OverlayTexture.DEFAULT_UV);
        arg.pop();
    }
}

