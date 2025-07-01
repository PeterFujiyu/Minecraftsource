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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, S extends ZombieEntityRenderState, M extends ZombieEntityModel<S>>
extends BipedEntityRenderer<T, S, M> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

    protected ZombieBaseEntityRenderer(EntityRendererFactory.Context context, M mainModel, M babyMainModel, M armorInnerModel, M armorOuterModel, M babyArmorInnerModel, M babyArmorOuterModel) {
        super(context, mainModel, babyMainModel, 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, armorInnerModel, armorOuterModel, babyArmorInnerModel, babyArmorOuterModel, context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(S arg) {
        return TEXTURE;
    }

    @Override
    public void updateRenderState(T arg, S arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ((ZombieEntityRenderState)arg2).attacking = ((MobEntity)arg).isAttacking();
        ((ZombieEntityRenderState)arg2).convertingInWater = ((ZombieEntity)arg).isConvertingInWater();
    }

    @Override
    protected boolean isShaking(S arg) {
        return super.isShaking(arg) || ((ZombieEntityRenderState)arg).convertingInWater;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((S)((ZombieEntityRenderState)state));
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((S)((ZombieEntityRenderState)state));
    }
}

