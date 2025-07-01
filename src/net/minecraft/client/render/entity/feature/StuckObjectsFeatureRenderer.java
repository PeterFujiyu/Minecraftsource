/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public abstract class StuckObjectsFeatureRenderer<M extends PlayerEntityModel>
extends FeatureRenderer<PlayerEntityRenderState, M> {
    private final Model model;
    private final Identifier texture;
    private final RenderPosition renderPosition;

    public StuckObjectsFeatureRenderer(LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer, Model model, Identifier texture, RenderPosition renderPosition) {
        super(entityRenderer);
        this.model = model;
        this.texture = texture;
        this.renderPosition = renderPosition;
    }

    protected abstract int getObjectCount(PlayerEntityRenderState var1);

    private void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float f, float directionX, float directionY) {
        float j = MathHelper.sqrt(f * f + directionY * directionY);
        float k = (float)(Math.atan2(f, directionY) * 57.2957763671875);
        float l = (float)(Math.atan2(directionX, j) * 57.2957763671875);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k - 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(l));
        this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(this.texture)), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, PlayerEntityRenderState arg3, float f, float g) {
        int j = this.getObjectCount(arg3);
        if (j <= 0) {
            return;
        }
        Random lv = Random.create(arg3.id);
        for (int k = 0; k < j; ++k) {
            arg.push();
            ModelPart lv2 = ((PlayerEntityModel)this.getContextModel()).getRandomPart(lv);
            ModelPart.Cuboid lv3 = lv2.getRandomCuboid(lv);
            lv2.rotate(arg);
            float h = lv.nextFloat();
            float l = lv.nextFloat();
            float m = lv.nextFloat();
            if (this.renderPosition == RenderPosition.ON_SURFACE) {
                int n = lv.nextInt(3);
                switch (n) {
                    case 0: {
                        h = StuckObjectsFeatureRenderer.method_62597(h);
                        break;
                    }
                    case 1: {
                        l = StuckObjectsFeatureRenderer.method_62597(l);
                        break;
                    }
                    default: {
                        m = StuckObjectsFeatureRenderer.method_62597(m);
                    }
                }
            }
            arg.translate(MathHelper.lerp(h, lv3.minX, lv3.maxX) / 16.0f, MathHelper.lerp(l, lv3.minY, lv3.maxY) / 16.0f, MathHelper.lerp(m, lv3.minZ, lv3.maxZ) / 16.0f);
            this.renderObject(arg, arg2, i, -(h * 2.0f - 1.0f), -(l * 2.0f - 1.0f), -(m * 2.0f - 1.0f));
            arg.pop();
        }
    }

    private static float method_62597(float f) {
        return f > 0.5f ? 1.0f : 0.5f;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum RenderPosition {
        IN_CUBE,
        ON_SURFACE;

    }
}

