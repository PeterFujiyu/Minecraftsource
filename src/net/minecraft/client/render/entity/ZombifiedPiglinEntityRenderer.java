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
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ZombifiedPiglinEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombifiedPiglinEntityRenderer
extends BipedEntityRenderer<ZombifiedPiglinEntity, ZombifiedPiglinEntityRenderState, ZombifiedPiglinEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/piglin/zombified_piglin.png");

    public ZombifiedPiglinEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer mainLayer, EntityModelLayer babyMainLayer, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer, EntityModelLayer babyArmorInnerLayer, EntityModelLayer babyArmorOuterLayer) {
        super(context, new ZombifiedPiglinEntityModel(context.getPart(mainLayer)), new ZombifiedPiglinEntityModel(context.getPart(babyMainLayer)), 0.5f, PiglinEntityRenderer.HEAD_TRANSFORMATION);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(context.getPart(armorInnerLayer)), new ArmorEntityModel(context.getPart(armorOuterLayer)), new ArmorEntityModel(context.getPart(babyArmorInnerLayer)), new ArmorEntityModel(context.getPart(babyArmorInnerLayer)), context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(ZombifiedPiglinEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public ZombifiedPiglinEntityRenderState createRenderState() {
        return new ZombifiedPiglinEntityRenderState();
    }

    @Override
    public void updateRenderState(ZombifiedPiglinEntity arg, ZombifiedPiglinEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.attacking = arg.isAttacking();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombifiedPiglinEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

