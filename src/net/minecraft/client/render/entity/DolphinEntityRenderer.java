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
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityRenderer
extends AgeableMobEntityRenderer<DolphinEntity, DolphinEntityRenderState, DolphinEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/dolphin.png");

    public DolphinEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new DolphinEntityModel(arg.getPart(EntityModelLayers.DOLPHIN)), new DolphinEntityModel(arg.getPart(EntityModelLayers.DOLPHIN_BABY)), 0.7f);
        this.addFeature(new DolphinHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(DolphinEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public DolphinEntityRenderState createRenderState() {
        return new DolphinEntityRenderState();
    }

    @Override
    public void updateRenderState(DolphinEntity arg, DolphinEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ItemHolderEntityRenderState.update(arg, arg2, this.itemModelResolver);
        arg2.moving = arg.getVelocity().horizontalLengthSquared() > 1.0E-7;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((DolphinEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

