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
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;

@Environment(value=EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity, S extends IllagerEntityRenderState>
extends MobEntityRenderer<T, S, IllagerEntityModel<S>> {
    protected IllagerEntityRenderer(EntityRendererFactory.Context ctx, IllagerEntityModel<S> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addFeature(new HeadFeatureRenderer(this, ctx.getEntityModels()));
    }

    @Override
    public void updateRenderState(T arg, S arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ArmedEntityRenderState.updateRenderState(arg, arg2, this.itemModelResolver);
        ((IllagerEntityRenderState)arg2).hasVehicle = ((Entity)arg).hasVehicle();
        ((IllagerEntityRenderState)arg2).illagerMainArm = ((MobEntity)arg).getMainArm();
        ((IllagerEntityRenderState)arg2).illagerState = ((IllagerEntity)arg).getState();
        ((IllagerEntityRenderState)arg2).crossbowPullTime = ((IllagerEntityRenderState)arg2).illagerState == IllagerEntity.State.CROSSBOW_CHARGE ? CrossbowItem.getPullTime(((LivingEntity)arg).getActiveItem(), arg) : 0;
        ((IllagerEntityRenderState)arg2).itemUseTime = ((LivingEntity)arg).getItemUseTime();
        ((IllagerEntityRenderState)arg2).handSwingProgress = ((LivingEntity)arg).getHandSwingProgress(f);
        ((IllagerEntityRenderState)arg2).attacking = ((MobEntity)arg).isAttacking();
    }
}

