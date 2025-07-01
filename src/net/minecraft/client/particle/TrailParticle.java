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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class TrailParticle
extends SpriteBillboardParticle {
    private final Vec3d target;

    TrailParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Vec3d target, int color) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        color = ColorHelper.scaleRgb(color, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f);
        this.red = (float)ColorHelper.getRed(color) / 255.0f;
        this.green = (float)ColorHelper.getGreen(color) / 255.0f;
        this.blue = (float)ColorHelper.getBlue(color) / 255.0f;
        this.scale = 0.26f;
        this.target = target;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        int i = this.maxAge - this.age;
        double d = 1.0 / (double)i;
        this.x = MathHelper.lerp(d, this.x, this.target.getX());
        this.y = MathHelper.lerp(d, this.y, this.target.getY());
        this.z = MathHelper.lerp(d, this.z, this.target.getZ());
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<TrailParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(TrailParticleEffect arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            TrailParticle lv = new TrailParticle(arg2, d, e, f, g, h, i, arg.target(), arg.color());
            lv.setSprite(this.spriteProvider);
            lv.setMaxAge(arg.duration());
            return lv;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return this.createParticle((TrailParticleEffect)parameters, world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}

