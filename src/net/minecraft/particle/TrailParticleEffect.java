/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.particle;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record TrailParticleEffect(Vec3d target, int color, int duration) implements ParticleEffect
{
    public static final MapCodec<TrailParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Vec3d.CODEC.fieldOf("target")).forGetter(TrailParticleEffect::target), ((MapCodec)Codecs.RGB.fieldOf("color")).forGetter(TrailParticleEffect::color), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("duration")).forGetter(TrailParticleEffect::duration)).apply((Applicative<TrailParticleEffect, ?>)instance, TrailParticleEffect::new));
    public static final PacketCodec<RegistryByteBuf, TrailParticleEffect> PACKET_CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, TrailParticleEffect::target, PacketCodecs.INTEGER, TrailParticleEffect::color, PacketCodecs.VAR_INT, TrailParticleEffect::duration, TrailParticleEffect::new);

    public ParticleType<TrailParticleEffect> getType() {
        return ParticleTypes.TRAIL;
    }
}

