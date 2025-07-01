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
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityRenderState, IronGolemEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/iron_golem/iron_golem.png");

    public IronGolemEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new IronGolemEntityModel(arg.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature(new IronGolemCrackFeatureRenderer(this));
        this.addFeature(new IronGolemFlowerFeatureRenderer(this, arg.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(IronGolemEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public IronGolemEntityRenderState createRenderState() {
        return new IronGolemEntityRenderState();
    }

    @Override
    public void updateRenderState(IronGolemEntity arg, IronGolemEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.attackTicksLeft = (float)arg.getAttackTicksLeft() > 0.0f ? (float)arg.getAttackTicksLeft() - f : 0.0f;
        arg2.lookingAtVillagerTicks = arg.getLookingAtVillagerTicks();
        arg2.crackLevel = arg.getCrackLevel();
    }

    @Override
    protected void setupTransforms(IronGolemEntityRenderState arg, MatrixStack arg2, float f, float g) {
        super.setupTransforms(arg, arg2, f, g);
        if ((double)arg.limbAmplitudeMultiplier < 0.01) {
            return;
        }
        float h = 13.0f;
        float i = arg.limbFrequency + 6.0f;
        float j = (Math.abs(i % 13.0f - 6.5f) - 3.25f) / 3.25f;
        arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * j));
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IronGolemEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

