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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeEntityRenderer
extends MobEntityRenderer<BreezeEntity, BreezeEntityRenderState, BreezeEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/breeze/breeze.png");

    public BreezeEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new BreezeEntityModel(arg.getPart(EntityModelLayers.BREEZE)), 0.5f);
        this.addFeature(new BreezeWindFeatureRenderer(arg, this));
        this.addFeature(new BreezeEyesFeatureRenderer(this));
    }

    @Override
    public void render(BreezeEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        BreezeEntityModel lv = (BreezeEntityModel)this.getModel();
        BreezeEntityRenderer.updatePartVisibility(lv, lv.getHead(), lv.getRods());
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(BreezeEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public BreezeEntityRenderState createRenderState() {
        return new BreezeEntityRenderState();
    }

    @Override
    public void updateRenderState(BreezeEntity arg, BreezeEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.idleAnimationState.copyFrom(arg.idleAnimationState);
        arg2.shootingAnimationState.copyFrom(arg.shootingAnimationState);
        arg2.slidingAnimationState.copyFrom(arg.slidingAnimationState);
        arg2.slidingBackAnimationState.copyFrom(arg.slidingBackAnimationState);
        arg2.inhalingAnimationState.copyFrom(arg.inhalingAnimationState);
        arg2.longJumpingAnimationState.copyFrom(arg.longJumpingAnimationState);
    }

    public static BreezeEntityModel updatePartVisibility(BreezeEntityModel model, ModelPart ... modelParts) {
        model.getHead().visible = false;
        model.getEyes().visible = false;
        model.getRods().visible = false;
        model.getWindBody().visible = false;
        for (ModelPart lv : modelParts) {
            lv.visible = true;
        }
        return model;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BreezeEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

