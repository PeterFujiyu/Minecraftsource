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
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.StriderEntityRenderState;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderer
extends AgeableMobEntityRenderer<StriderEntity, StriderEntityRenderState, StriderEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider.png");
    private static final Identifier COLD_TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider_cold.png");
    private static final float BABY_SHADOW_RADIUS_SCALE = 0.5f;

    public StriderEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new StriderEntityModel(arg.getPart(EntityModelLayers.STRIDER)), new StriderEntityModel(arg.getPart(EntityModelLayers.STRIDER_BABY)), 0.5f);
        this.addFeature(new SaddleFeatureRenderer<StriderEntityRenderState, StriderEntityModel>(this, new StriderEntityModel(arg.getPart(EntityModelLayers.STRIDER_SADDLE)), new StriderEntityModel(arg.getPart(EntityModelLayers.STRIDER_BABY_SADDLE)), Identifier.ofVanilla("textures/entity/strider/strider_saddle.png")));
    }

    @Override
    public Identifier getTexture(StriderEntityRenderState arg) {
        return arg.cold ? COLD_TEXTURE : TEXTURE;
    }

    @Override
    protected float getShadowRadius(StriderEntityRenderState arg) {
        float f = super.getShadowRadius(arg);
        if (arg.baby) {
            return f * 0.5f;
        }
        return f;
    }

    @Override
    public StriderEntityRenderState createRenderState() {
        return new StriderEntityRenderState();
    }

    @Override
    public void updateRenderState(StriderEntity arg, StriderEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.saddled = arg.isSaddled();
        arg2.cold = arg.isCold();
        arg2.hasPassengers = arg.hasPassengers();
    }

    @Override
    protected boolean isShaking(StriderEntityRenderState arg) {
        return super.isShaking(arg) || arg.cold;
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState arg) {
        return this.getShadowRadius((StriderEntityRenderState)arg);
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((StriderEntityRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((StriderEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((StriderEntityRenderState)state);
    }
}

