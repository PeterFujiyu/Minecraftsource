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
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FireworkRocketEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class FireworkRocketEntityRenderer
extends EntityRenderer<FireworkRocketEntity, FireworkRocketEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public FireworkRocketEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.itemModelManager = arg.getItemModelManager();
    }

    @Override
    public void render(FireworkRocketEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.multiply(this.dispatcher.getRotation());
        if (arg.shotAtAngle) {
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        }
        arg.stack.render(arg2, arg3, i, OverlayTexture.DEFAULT_UV);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public FireworkRocketEntityRenderState createRenderState() {
        return new FireworkRocketEntityRenderState();
    }

    @Override
    public void updateRenderState(FireworkRocketEntity arg, FireworkRocketEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.shotAtAngle = arg.wasShotAtAngle();
        this.itemModelManager.updateForNonLivingEntity(arg2.stack, arg.getStack(), ModelTransformationMode.GROUND, arg);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

