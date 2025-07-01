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
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.SnowGolemEntityRenderState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class SnowGolemPumpkinFeatureRenderer
extends FeatureRenderer<SnowGolemEntityRenderState, SnowGolemEntityModel> {
    private final BlockRenderManager blockRenderManager;

    public SnowGolemPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntityRenderState, SnowGolemEntityModel> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, SnowGolemEntityRenderState arg3, float f, float g) {
        if (!arg3.hasPumpkin) {
            return;
        }
        if (arg3.invisible && !arg3.hasOutline) {
            return;
        }
        arg.push();
        ((SnowGolemEntityModel)this.getContextModel()).getHead().rotate(arg);
        float h = 0.625f;
        arg.translate(0.0f, -0.34375f, 0.0f);
        arg.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        arg.scale(0.625f, -0.625f, -0.625f);
        BlockState lv = Blocks.CARVED_PUMPKIN.getDefaultState();
        BakedModel lv2 = this.blockRenderManager.getModel(lv);
        int j = LivingEntityRenderer.getOverlay(arg3, 0.0f);
        arg.translate(-0.5f, -0.5f, -0.5f);
        VertexConsumer lv3 = arg3.hasOutline && arg3.invisible ? arg2.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) : arg2.getBuffer(RenderLayers.getEntityBlockLayer(lv));
        this.blockRenderManager.getModelRenderer().render(arg.peek(), lv3, lv, lv2, 0.0f, 0.0f, 0.0f, i, j);
        arg.pop();
    }
}

