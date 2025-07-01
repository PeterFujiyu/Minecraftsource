/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class PandaEntityRenderer
extends AgeableMobEntityRenderer<PandaEntity, PandaEntityRenderState, PandaEntityModel> {
    private static final Map<PandaEntity.Gene, Identifier> TEXTURES = Util.make(Maps.newEnumMap(PandaEntity.Gene.class), map -> {
        map.put(PandaEntity.Gene.NORMAL, Identifier.ofVanilla("textures/entity/panda/panda.png"));
        map.put(PandaEntity.Gene.LAZY, Identifier.ofVanilla("textures/entity/panda/lazy_panda.png"));
        map.put(PandaEntity.Gene.WORRIED, Identifier.ofVanilla("textures/entity/panda/worried_panda.png"));
        map.put(PandaEntity.Gene.PLAYFUL, Identifier.ofVanilla("textures/entity/panda/playful_panda.png"));
        map.put(PandaEntity.Gene.BROWN, Identifier.ofVanilla("textures/entity/panda/brown_panda.png"));
        map.put(PandaEntity.Gene.WEAK, Identifier.ofVanilla("textures/entity/panda/weak_panda.png"));
        map.put(PandaEntity.Gene.AGGRESSIVE, Identifier.ofVanilla("textures/entity/panda/aggressive_panda.png"));
    });

    public PandaEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new PandaEntityModel(arg.getPart(EntityModelLayers.PANDA)), new PandaEntityModel(arg.getPart(EntityModelLayers.PANDA_BABY)), 0.9f);
        this.addFeature(new PandaHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(PandaEntityRenderState arg) {
        return TEXTURES.getOrDefault(arg.gene, TEXTURES.get(PandaEntity.Gene.NORMAL));
    }

    @Override
    public PandaEntityRenderState createRenderState() {
        return new PandaEntityRenderState();
    }

    @Override
    public void updateRenderState(PandaEntity arg, PandaEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ItemHolderEntityRenderState.update(arg, arg2, this.itemModelResolver);
        arg2.gene = arg.getProductGene();
        arg2.askingForBamboo = arg.getAskForBambooTicks() > 0;
        arg2.sneezing = arg.isSneezing();
        arg2.sneezeProgress = arg.getSneezeProgress();
        arg2.eating = arg.isEating();
        arg2.scaredByThunderstorm = arg.isScaredByThunderstorm();
        arg2.sitting = arg.isSitting();
        arg2.sittingAnimationProgress = arg.getSittingAnimationProgress(f);
        arg2.lieOnBackAnimationProgress = arg.getLieOnBackAnimationProgress(f);
        arg2.rollOverAnimationProgress = arg.isBaby() ? 0.0f : arg.getRollOverAnimationProgress(f);
        arg2.playingTicks = arg.playingTicks > 0 ? (float)arg.playingTicks + f : 0.0f;
    }

    @Override
    protected void setupTransforms(PandaEntityRenderState arg, MatrixStack arg2, float f, float g) {
        float q;
        float h;
        super.setupTransforms(arg, arg2, f, g);
        if (arg.playingTicks > 0.0f) {
            float l;
            h = MathHelper.fractionalPart(arg.playingTicks);
            int i = MathHelper.floor(arg.playingTicks);
            int j = i + 1;
            float k = 7.0f;
            float f2 = l = arg.baby ? 0.3f : 0.8f;
            if ((float)i < 8.0f) {
                float m = 90.0f * (float)i / 7.0f;
                float n = 90.0f * (float)j / 7.0f;
                float o = this.getAngle(m, n, j, h, 8.0f);
                arg2.translate(0.0f, (l + 0.2f) * (o / 90.0f), 0.0f);
                arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
            } else if ((float)i < 16.0f) {
                float m = ((float)i - 8.0f) / 7.0f;
                float n = 90.0f + 90.0f * m;
                float p = 90.0f + 90.0f * ((float)j - 8.0f) / 7.0f;
                float o = this.getAngle(n, p, j, h, 16.0f);
                arg2.translate(0.0f, l + 0.2f + (l - 0.2f) * (o - 90.0f) / 90.0f, 0.0f);
                arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
            } else if ((float)i < 24.0f) {
                float m = ((float)i - 16.0f) / 7.0f;
                float n = 180.0f + 90.0f * m;
                float p = 180.0f + 90.0f * ((float)j - 16.0f) / 7.0f;
                float o = this.getAngle(n, p, j, h, 24.0f);
                arg2.translate(0.0f, l + l * (270.0f - o) / 90.0f, 0.0f);
                arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
            } else if (i < 32) {
                float m = ((float)i - 24.0f) / 7.0f;
                float n = 270.0f + 90.0f * m;
                float p = 270.0f + 90.0f * ((float)j - 24.0f) / 7.0f;
                float o = this.getAngle(n, p, j, h, 32.0f);
                arg2.translate(0.0f, l * ((360.0f - o) / 90.0f), 0.0f);
                arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
            }
        }
        if ((h = arg.sittingAnimationProgress) > 0.0f) {
            arg2.translate(0.0f, 0.8f * h, 0.0f);
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(h, arg.pitch, arg.pitch + 90.0f)));
            arg2.translate(0.0f, -1.0f * h, 0.0f);
            if (arg.scaredByThunderstorm) {
                float q2 = (float)(Math.cos(arg.age * 1.25f) * Math.PI * (double)0.05f);
                arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(q2));
                if (arg.baby) {
                    arg2.translate(0.0f, 0.8f, 0.55f);
                }
            }
        }
        if ((q = arg.lieOnBackAnimationProgress) > 0.0f) {
            float r = arg.baby ? 0.5f : 1.3f;
            arg2.translate(0.0f, r * q, 0.0f);
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(q, arg.pitch, arg.pitch + 180.0f)));
        }
    }

    private float getAngle(float f, float g, int i, float h, float j) {
        if ((float)i < j) {
            return MathHelper.lerp(h, f, g);
        }
        return f;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PandaEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

