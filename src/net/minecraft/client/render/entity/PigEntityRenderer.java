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
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PigEntityRenderer
extends AgeableMobEntityRenderer<PigEntity, PigEntityRenderState, PigEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/pig/pig.png");

    public PigEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new PigEntityModel(arg.getPart(EntityModelLayers.PIG)), new PigEntityModel(arg.getPart(EntityModelLayers.PIG_BABY)), 0.7f);
        this.addFeature(new SaddleFeatureRenderer<PigEntityRenderState, PigEntityModel>(this, new PigEntityModel(arg.getPart(EntityModelLayers.PIG_SADDLE)), new PigEntityModel(arg.getPart(EntityModelLayers.PIG_BABY_SADDLE)), Identifier.ofVanilla("textures/entity/pig/pig_saddle.png")));
    }

    @Override
    public Identifier getTexture(PigEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public PigEntityRenderState createRenderState() {
        return new PigEntityRenderState();
    }

    @Override
    public void updateRenderState(PigEntity arg, PigEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.saddled = arg.isSaddled();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PigEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

