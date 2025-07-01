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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BreezeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeWindFeatureRenderer
extends FeatureRenderer<BreezeEntityRenderState, BreezeEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/breeze/breeze_wind.png");
    private final BreezeEntityModel model;

    public BreezeWindFeatureRenderer(EntityRendererFactory.Context entityRendererContext, FeatureRendererContext<BreezeEntityRenderState, BreezeEntityModel> featureContext) {
        super(featureContext);
        this.model = new BreezeEntityModel(entityRendererContext.getPart(EntityModelLayers.BREEZE_WIND));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, BreezeEntityRenderState arg3, float f, float g) {
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getBreezeWind(TEXTURE, this.getXOffset(arg3.age) % 1.0f, 0.0f));
        this.model.setAngles(arg3);
        BreezeEntityRenderer.updatePartVisibility(this.model, this.model.getWindBody()).render(arg, lv, i, OverlayTexture.DEFAULT_UV);
    }

    private float getXOffset(float tickDelta) {
        return tickDelta * 0.02f;
    }
}

