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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractHorseEntityRenderer<T extends AbstractHorseEntity, S extends LivingHorseEntityRenderState, M extends EntityModel<? super S>>
extends AgeableMobEntityRenderer<T, S, M> {
    public AbstractHorseEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel) {
        super(context, model, babyModel, 0.75f);
    }

    @Override
    public void updateRenderState(T arg, S arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ((LivingHorseEntityRenderState)arg2).saddled = ((AbstractHorseEntity)arg).isSaddled();
        ((LivingHorseEntityRenderState)arg2).hasPassengers = ((Entity)arg).hasPassengers();
        ((LivingHorseEntityRenderState)arg2).eatingGrassAnimationProgress = ((AbstractHorseEntity)arg).getEatingGrassAnimationProgress(f);
        ((LivingHorseEntityRenderState)arg2).angryAnimationProgress = ((AbstractHorseEntity)arg).getAngryAnimationProgress(f);
        ((LivingHorseEntityRenderState)arg2).eatingAnimationProgress = ((AbstractHorseEntity)arg).getEatingAnimationProgress(f);
        ((LivingHorseEntityRenderState)arg2).waggingTail = ((AbstractHorseEntity)arg).tailWagTicks > 0;
    }
}

