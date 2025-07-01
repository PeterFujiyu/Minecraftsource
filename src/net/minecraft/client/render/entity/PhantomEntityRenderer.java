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
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PhantomEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityRenderer
extends MobEntityRenderer<PhantomEntity, PhantomEntityRenderState, PhantomEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/phantom.png");

    public PhantomEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new PhantomEntityModel(arg.getPart(EntityModelLayers.PHANTOM)), 0.75f);
        this.addFeature(new PhantomEyesFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(PhantomEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public PhantomEntityRenderState createRenderState() {
        return new PhantomEntityRenderState();
    }

    @Override
    public void updateRenderState(PhantomEntity arg, PhantomEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.wingFlapProgress = (float)arg.getWingFlapTickOffset() + arg2.age;
        arg2.size = arg.getPhantomSize();
    }

    @Override
    protected void scale(PhantomEntityRenderState arg, MatrixStack arg2) {
        float f = 1.0f + 0.15f * (float)arg.size;
        arg2.scale(f, f, f);
        arg2.translate(0.0f, 1.3125f, 0.1875f);
    }

    @Override
    protected void setupTransforms(PhantomEntityRenderState arg, MatrixStack arg2, float f, float g) {
        super.setupTransforms(arg, arg2, f, g);
        arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(arg.pitch));
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PhantomEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

