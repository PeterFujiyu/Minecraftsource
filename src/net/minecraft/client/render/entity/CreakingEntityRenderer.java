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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.model.CreakingEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreakingEntityRenderer<T extends CreakingEntity>
extends MobEntityRenderer<T, CreakingEntityRenderState, CreakingEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking.png");
    private static final Identifier EYES_TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking_eyes.png");

    public CreakingEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new CreakingEntityModel(arg.getPart(EntityModelLayers.CREAKING)), 0.6f);
        this.addFeature(new EmissiveFeatureRenderer<CreakingEntityRenderState, CreakingEntityModel>(this, EYES_TEXTURE, (state, tickDelta) -> 1.0f, CreakingEntityModel::getEmissiveParts, RenderLayer::getEyes, true));
    }

    @Override
    public Identifier getTexture(CreakingEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public CreakingEntityRenderState createRenderState() {
        return new CreakingEntityRenderState();
    }

    @Override
    public void updateRenderState(T arg, CreakingEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.attackAnimationState.copyFrom(((CreakingEntity)arg).attackAnimationState);
        arg2.invulnerableAnimationState.copyFrom(((CreakingEntity)arg).invulnerableAnimationState);
        arg2.crumblingAnimationState.copyFrom(((CreakingEntity)arg).crumblingAnimationState);
        if (((CreakingEntity)arg).isCrumbling()) {
            arg2.deathTime = 0.0f;
            arg2.hurt = false;
            arg2.glowingEyes = ((CreakingEntity)arg).hasGlowingEyesWhileCrumbling();
        } else {
            arg2.glowingEyes = ((CreakingEntity)arg).isActive();
        }
        arg2.unrooted = ((CreakingEntity)arg).isUnrooted();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CreakingEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

