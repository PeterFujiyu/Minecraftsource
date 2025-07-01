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
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends MobEntityRenderer<SlimeEntity, SlimeEntityRenderState, SlimeEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/slime.png");

    public SlimeEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new SlimeEntityModel(arg.getPart(EntityModelLayers.SLIME)), 0.25f);
        this.addFeature(new SlimeOverlayFeatureRenderer(this, arg.getEntityModels()));
    }

    @Override
    protected float getShadowRadius(SlimeEntityRenderState arg) {
        return (float)arg.size * 0.25f;
    }

    @Override
    protected void scale(SlimeEntityRenderState arg, MatrixStack arg2) {
        float f = 0.999f;
        arg2.scale(0.999f, 0.999f, 0.999f);
        arg2.translate(0.0f, 0.001f, 0.0f);
        float g = arg.size;
        float h = arg.stretch / (g * 0.5f + 1.0f);
        float i = 1.0f / (h + 1.0f);
        arg2.scale(i * g, 1.0f / i * g, i * g);
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public SlimeEntityRenderState createRenderState() {
        return new SlimeEntityRenderState();
    }

    @Override
    public void updateRenderState(SlimeEntity arg, SlimeEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.stretch = MathHelper.lerp(f, arg.lastStretch, arg.stretch);
        arg2.size = arg.getSize();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState arg) {
        return this.getShadowRadius((SlimeEntityRenderState)arg);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((SlimeEntityRenderState)state);
    }
}

