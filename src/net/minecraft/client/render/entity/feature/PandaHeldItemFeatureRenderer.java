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
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntityRenderState, PandaEntityModel> {
    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntityRenderState, PandaEntityModel> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, PandaEntityRenderState arg3, float f, float g) {
        ItemRenderState lv = arg3.itemRenderState;
        if (lv.isEmpty() || !arg3.sitting || arg3.scaredByThunderstorm) {
            return;
        }
        float h = -0.6f;
        float j = 1.4f;
        if (arg3.eating) {
            h -= 0.2f * MathHelper.sin(arg3.age * 0.6f) + 0.2f;
            j -= 0.09f * MathHelper.sin(arg3.age * 0.6f);
        }
        arg.push();
        arg.translate(0.1f, j, h);
        lv.render(arg, arg2, i, OverlayTexture.DEFAULT_UV);
        arg.pop();
    }
}

