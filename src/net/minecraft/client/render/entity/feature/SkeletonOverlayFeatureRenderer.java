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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SkeletonOverlayFeatureRenderer<S extends SkeletonEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    private final SkeletonEntityModel<S> model;
    private final Identifier texture;

    public SkeletonOverlayFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels loader, EntityModelLayer layer, Identifier texture) {
        super(context);
        this.texture = texture;
        this.model = new SkeletonEntityModel(loader.getModelPart(layer));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, S arg3, float f, float g) {
        SkeletonOverlayFeatureRenderer.render(this.model, this.texture, arg, arg2, i, arg3, -1);
    }
}

