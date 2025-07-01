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
import net.minecraft.client.render.entity.model.ArmadilloEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmadilloEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ArmadilloEntityRenderer
extends AgeableMobEntityRenderer<ArmadilloEntity, ArmadilloEntityRenderState, ArmadilloEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armadillo.png");

    public ArmadilloEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new ArmadilloEntityModel(arg.getPart(EntityModelLayers.ARMADILLO)), new ArmadilloEntityModel(arg.getPart(EntityModelLayers.ARMADILLO_BABY)), 0.4f);
    }

    @Override
    public Identifier getTexture(ArmadilloEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public ArmadilloEntityRenderState createRenderState() {
        return new ArmadilloEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmadilloEntity arg, ArmadilloEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.rolledUp = arg.isRolledUp();
        arg2.scaredAnimationState.copyFrom(arg.scaredAnimationState);
        arg2.unrollingAnimationState.copyFrom(arg.unrollingAnimationState);
        arg2.rollingAnimationState.copyFrom(arg.rollingAnimationState);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ArmadilloEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

