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
import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnowGolemEntityRenderState;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityRenderer
extends MobEntityRenderer<SnowGolemEntity, SnowGolemEntityRenderState, SnowGolemEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/snow_golem.png");

    public SnowGolemEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new SnowGolemEntityModel(arg.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5f);
        this.addFeature(new SnowGolemPumpkinFeatureRenderer(this, arg.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(SnowGolemEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public SnowGolemEntityRenderState createRenderState() {
        return new SnowGolemEntityRenderState();
    }

    @Override
    public void updateRenderState(SnowGolemEntity arg, SnowGolemEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.hasPumpkin = arg.hasPumpkin();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SnowGolemEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

