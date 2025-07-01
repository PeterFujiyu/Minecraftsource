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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityRenderer
extends AgeableMobEntityRenderer<PolarBearEntity, PolarBearEntityRenderState, PolarBearEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bear/polarbear.png");

    public PolarBearEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new PolarBearEntityModel(arg.getPart(EntityModelLayers.POLAR_BEAR)), new PolarBearEntityModel(arg.getPart(EntityModelLayers.POLAR_BEAR_BABY)), 0.9f);
    }

    @Override
    public Identifier getTexture(PolarBearEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public PolarBearEntityRenderState createRenderState() {
        return new PolarBearEntityRenderState();
    }

    @Override
    public void updateRenderState(PolarBearEntity arg, PolarBearEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.warningAnimationProgress = arg.getWarningAnimationProgress(f);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PolarBearEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

