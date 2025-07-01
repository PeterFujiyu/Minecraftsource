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
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitchEntityRenderer
extends MobEntityRenderer<WitchEntity, WitchEntityRenderState, WitchEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/witch.png");

    public WitchEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new WitchEntityModel(arg.getPart(EntityModelLayers.WITCH)), 0.5f);
        this.addFeature(new WitchHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(WitchEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public WitchEntityRenderState createRenderState() {
        return new WitchEntityRenderState();
    }

    @Override
    public void updateRenderState(WitchEntity arg, WitchEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ItemHolderEntityRenderState.update(arg, arg2, this.itemModelResolver);
        arg2.id = arg.getId();
        ItemStack lv = arg.getMainHandStack();
        arg2.holdingItem = !lv.isEmpty();
        arg2.holdingPotion = lv.isOf(Items.POTION);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WitchEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

