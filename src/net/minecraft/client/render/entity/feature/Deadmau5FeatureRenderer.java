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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.Deadmau5EarsEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class Deadmau5FeatureRenderer
extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final BipedEntityModel<PlayerEntityRenderState> model;

    public Deadmau5FeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels entityModels) {
        super(context);
        this.model = new Deadmau5EarsEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER_EARS));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, PlayerEntityRenderState arg3, float f, float g) {
        if (!"deadmau5".equals(arg3.name) || arg3.invisible) {
            return;
        }
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getEntitySolid(arg3.skinTextures.texture()));
        int j = LivingEntityRenderer.getOverlay(arg3, 0.0f);
        ((PlayerEntityModel)this.getContextModel()).copyTransforms(this.model);
        this.model.setAngles(arg3);
        this.model.render(arg, lv, i, j);
    }
}

