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
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CreeperEntityRenderer
extends MobEntityRenderer<CreeperEntity, CreeperEntityRenderState, CreeperEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creeper/creeper.png");

    public CreeperEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new CreeperEntityModel(arg.getPart(EntityModelLayers.CREEPER)), 0.5f);
        this.addFeature(new CreeperChargeFeatureRenderer(this, arg.getEntityModels()));
    }

    @Override
    protected void scale(CreeperEntityRenderState arg, MatrixStack arg2) {
        float f = arg.fuseTime;
        float g = 1.0f + MathHelper.sin(f * 100.0f) * f * 0.01f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        f *= f;
        f *= f;
        float h = (1.0f + f * 0.4f) * g;
        float i = (1.0f + f * 0.1f) / g;
        arg2.scale(h, i, h);
    }

    @Override
    protected float getAnimationCounter(CreeperEntityRenderState arg) {
        float f = arg.fuseTime;
        if ((int)(f * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return MathHelper.clamp(f, 0.5f, 1.0f);
    }

    @Override
    public Identifier getTexture(CreeperEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public CreeperEntityRenderState createRenderState() {
        return new CreeperEntityRenderState();
    }

    @Override
    public void updateRenderState(CreeperEntity arg, CreeperEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.fuseTime = arg.getClientFuseTime(f);
        arg2.charged = arg.isCharged();
    }

    @Override
    protected /* synthetic */ float getAnimationCounter(LivingEntityRenderState state) {
        return this.getAnimationCounter((CreeperEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

