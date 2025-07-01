/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BoggedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.BoggedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BoggedEntityRenderer
extends AbstractSkeletonEntityRenderer<BoggedEntity, BoggedEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/bogged.png");
    private static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/bogged_overlay.png");

    public BoggedEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, EntityModelLayers.BOGGED_INNER_ARMOR, EntityModelLayers.BOGGED_OUTER_ARMOR, new BoggedEntityModel(arg.getPart(EntityModelLayers.BOGGED)));
        this.addFeature(new SkeletonOverlayFeatureRenderer<BoggedEntityRenderState, SkeletonEntityModel<BoggedEntityRenderState>>(this, arg.getEntityModels(), EntityModelLayers.BOGGED_OUTER, OVERLAY_TEXTURE));
    }

    @Override
    public Identifier getTexture(BoggedEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public BoggedEntityRenderState createRenderState() {
        return new BoggedEntityRenderState();
    }

    @Override
    public void updateRenderState(BoggedEntity arg, BoggedEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.sheared = arg.isSheared();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BoggedEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

