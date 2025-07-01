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
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer<T extends SquidEntity>
extends AgeableMobEntityRenderer<T, SquidEntityRenderState, SquidEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/squid/squid.png");

    public SquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel model, SquidEntityModel babyModel) {
        super(context, model, babyModel, 0.7f);
    }

    @Override
    public Identifier getTexture(SquidEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public SquidEntityRenderState createRenderState() {
        return new SquidEntityRenderState();
    }

    @Override
    public void updateRenderState(T arg, SquidEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.tentacleAngle = MathHelper.lerp(f, ((SquidEntity)arg).prevTentacleAngle, ((SquidEntity)arg).tentacleAngle);
        arg2.tiltAngle = MathHelper.lerp(f, ((SquidEntity)arg).prevTiltAngle, ((SquidEntity)arg).tiltAngle);
        arg2.rollAngle = MathHelper.lerp(f, ((SquidEntity)arg).prevRollAngle, ((SquidEntity)arg).rollAngle);
    }

    @Override
    protected void setupTransforms(SquidEntityRenderState arg, MatrixStack arg2, float f, float g) {
        arg2.translate(0.0f, arg.baby ? 0.25f : 0.5f, 0.0f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(arg.tiltAngle));
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arg.rollAngle));
        arg2.translate(0.0f, arg.baby ? -0.6f : -1.2f, 0.0f);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SquidEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

