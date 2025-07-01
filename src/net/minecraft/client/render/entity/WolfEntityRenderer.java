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
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderer
extends AgeableMobEntityRenderer<WolfEntity, WolfEntityRenderState, WolfEntityModel> {
    public WolfEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new WolfEntityModel(arg.getPart(EntityModelLayers.WOLF)), new WolfEntityModel(arg.getPart(EntityModelLayers.WOLF_BABY)), 0.5f);
        this.addFeature(new WolfArmorFeatureRenderer(this, arg.getEntityModels(), arg.getEquipmentRenderer()));
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }

    @Override
    protected int getMixColor(WolfEntityRenderState arg) {
        float f = arg.furWetBrightnessMultiplier;
        if (f == 1.0f) {
            return -1;
        }
        return ColorHelper.fromFloats(1.0f, f, f, f);
    }

    @Override
    public Identifier getTexture(WolfEntityRenderState arg) {
        return arg.texture;
    }

    @Override
    public WolfEntityRenderState createRenderState() {
        return new WolfEntityRenderState();
    }

    @Override
    public void updateRenderState(WolfEntity arg, WolfEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.angerTime = arg.hasAngerTime();
        arg2.inSittingPose = arg.isInSittingPose();
        arg2.tailAngle = arg.getTailAngle();
        arg2.begAnimationProgress = arg.getBegAnimationProgress(f);
        arg2.shakeProgress = arg.getShakeProgress(f);
        arg2.texture = arg.getTextureId();
        arg2.furWetBrightnessMultiplier = arg.getFurWetBrightnessMultiplier(f);
        arg2.collarColor = arg.isTamed() ? arg.getCollarColor() : null;
        arg2.bodyArmor = arg.getBodyArmor().copy();
    }

    @Override
    protected /* synthetic */ int getMixColor(LivingEntityRenderState state) {
        return this.getMixColor((WolfEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

