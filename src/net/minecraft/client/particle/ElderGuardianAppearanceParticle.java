/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianAppearanceParticle
extends Particle {
    private final Model model;
    private final RenderLayer layer = RenderLayer.getEntityTranslucent(ElderGuardianEntityRenderer.TEXTURE);

    ElderGuardianAppearanceParticle(ClientWorld arg, double d, double e, double f) {
        super(arg, d, e, f);
        this.model = new GuardianEntityModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.ELDER_GUARDIAN));
        this.gravityStrength = 0.0f;
        this.maxAge = 30;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void renderCustom(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta) {
        float g = ((float)this.age + tickDelta) / (float)this.maxAge;
        float h = 0.05f + 0.5f * MathHelper.sin(g * (float)Math.PI);
        int i = ColorHelper.fromFloats(h, 1.0f, 1.0f, 1.0f);
        matrices.push();
        matrices.multiply(camera.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(60.0f - 150.0f * g));
        float j = 0.42553192f;
        matrices.scale(0.42553192f, -0.42553192f, -0.42553192f);
        matrices.translate(0.0f, -0.56f, 3.5f);
        VertexConsumer lv = vertexConsumers.getBuffer(this.layer);
        this.model.render(matrices, lv, 0xF000F0, OverlayTexture.DEFAULT_UV, i);
        matrices.pop();
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            return new ElderGuardianAppearanceParticle(arg2, d, e, f);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return this.createParticle((SimpleParticleType)parameters, world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}

