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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FrogEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FrogEntityRenderer
extends MobEntityRenderer<FrogEntity, FrogEntityRenderState, FrogEntityModel> {
    public FrogEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new FrogEntityModel(arg.getPart(EntityModelLayers.FROG)), 0.3f);
    }

    @Override
    public Identifier getTexture(FrogEntityRenderState arg) {
        return arg.texture;
    }

    @Override
    public FrogEntityRenderState createRenderState() {
        return new FrogEntityRenderState();
    }

    @Override
    public void updateRenderState(FrogEntity arg, FrogEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.insideWaterOrBubbleColumn = arg.isInsideWaterOrBubbleColumn();
        arg2.longJumpingAnimationState.copyFrom(arg.longJumpingAnimationState);
        arg2.croakingAnimationState.copyFrom(arg.croakingAnimationState);
        arg2.usingTongueAnimationState.copyFrom(arg.usingTongueAnimationState);
        arg2.idlingInWaterAnimationState.copyFrom(arg.idlingInWaterAnimationState);
        arg2.texture = ((FrogVariant)arg.getVariant().value()).texture();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FrogEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

