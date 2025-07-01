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
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityRenderer
extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new ZombieVillagerEntityModel(arg.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), new ZombieVillagerEntityModel(arg.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5f, VillagerEntityRenderer.HEAD_TRANSFORMATION);
        this.addFeature(new ArmorFeatureRenderer(this, new ZombieVillagerEntityModel(arg.getPart(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerEntityModel(arg.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), new ZombieVillagerEntityModel(arg.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_INNER_ARMOR)), new ZombieVillagerEntityModel(arg.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_OUTER_ARMOR)), arg.getEquipmentRenderer()));
        this.addFeature(new VillagerClothingFeatureRenderer<ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>>(this, arg.getResourceManager(), "zombie_villager"));
    }

    @Override
    public Identifier getTexture(ZombieVillagerRenderState arg) {
        return TEXTURE;
    }

    @Override
    public ZombieVillagerRenderState createRenderState() {
        return new ZombieVillagerRenderState();
    }

    @Override
    public void updateRenderState(ZombieVillagerEntity arg, ZombieVillagerRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.convertingInWater = arg.isConverting();
        arg2.villagerData = arg.getVillagerData();
        arg2.attacking = arg.isAttacking();
    }

    @Override
    protected boolean isShaking(ZombieVillagerRenderState arg) {
        return super.isShaking(arg) || arg.convertingInWater;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((ZombieVillagerRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieVillagerRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

