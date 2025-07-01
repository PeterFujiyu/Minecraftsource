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
import net.minecraft.client.render.entity.model.CamelEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CamelEntityRenderer
extends AgeableMobEntityRenderer<CamelEntity, CamelEntityRenderState, CamelEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/camel/camel.png");

    public CamelEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CamelEntityModel(ctx.getPart(EntityModelLayers.CAMEL)), new CamelEntityModel(ctx.getPart(EntityModelLayers.CAMEL_BABY)), 0.7f);
    }

    @Override
    public Identifier getTexture(CamelEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public CamelEntityRenderState createRenderState() {
        return new CamelEntityRenderState();
    }

    @Override
    public void updateRenderState(CamelEntity arg, CamelEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.saddled = arg.isSaddled();
        arg2.hasPassengers = arg.hasPassengers();
        arg2.jumpCooldown = Math.max((float)arg.getJumpCooldown() - f, 0.0f);
        arg2.sittingTransitionAnimationState.copyFrom(arg.sittingTransitionAnimationState);
        arg2.sittingAnimationState.copyFrom(arg.sittingAnimationState);
        arg2.standingTransitionAnimationState.copyFrom(arg.standingTransitionAnimationState);
        arg2.idlingAnimationState.copyFrom(arg.idlingAnimationState);
        arg2.dashingAnimationState.copyFrom(arg.dashingAnimationState);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CamelEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

