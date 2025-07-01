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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityRenderer
extends MobEntityRenderer<EndermanEntity, EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderman/enderman.png");
    private final Random random = Random.create();

    public EndermanEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new EndermanEntityModel(arg.getPart(EntityModelLayers.ENDERMAN)), 0.5f);
        this.addFeature(new EndermanEyesFeatureRenderer(this));
        this.addFeature(new EndermanBlockFeatureRenderer(this, arg.getBlockRenderManager()));
    }

    @Override
    public Vec3d getPositionOffset(EndermanEntityRenderState arg) {
        Vec3d lv = super.getPositionOffset(arg);
        if (arg.angry) {
            double d = 0.02 * (double)arg.baseScale;
            return lv.add(this.random.nextGaussian() * d, 0.0, this.random.nextGaussian() * d);
        }
        return lv;
    }

    @Override
    public Identifier getTexture(EndermanEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public EndermanEntityRenderState createRenderState() {
        return new EndermanEntityRenderState();
    }

    @Override
    public void updateRenderState(EndermanEntity arg, EndermanEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        BipedEntityRenderer.updateBipedRenderState(arg, arg2, f, this.itemModelResolver);
        arg2.angry = arg.isAngry();
        arg2.carriedBlock = arg.getCarriedBlock();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

