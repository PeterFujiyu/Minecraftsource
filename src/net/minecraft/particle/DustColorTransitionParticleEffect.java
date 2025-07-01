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
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;

public class DustColorTransitionParticleEffect
extends AbstractDustParticleEffect {
    public static final int SCULK_BLUE = 3790560;
    public static final DustColorTransitionParticleEffect DEFAULT = new DustColorTransitionParticleEffect(3790560, 0xFF0000, 1.0f);
    public static final MapCodec<DustColorTransitionParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codecs.RGB.fieldOf("from_color")).forGetter(particle -> particle.fromColor), ((MapCodec)Codecs.RGB.fieldOf("to_color")).forGetter(particle -> particle.toColor), ((MapCodec)SCALE_CODEC.fieldOf("scale")).forGetter(AbstractDustParticleEffect::getScale)).apply((Applicative<DustColorTransitionParticleEffect, ?>)instance, DustColorTransitionParticleEffect::new));
    public static final PacketCodec<RegistryByteBuf, DustColorTransitionParticleEffect> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, particle -> particle.fromColor, PacketCodecs.INTEGER, particle -> particle.toColor, PacketCodecs.FLOAT, AbstractDustParticleEffect::getScale, DustColorTransitionParticleEffect::new);
    private final int fromColor;
    private final int toColor;

    public DustColorTransitionParticleEffect(int fromColor, int toColor, float scale) {
        super(scale);
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    public Vector3f getFromColor() {
        return ColorHelper.toVector(this.fromColor);
    }

    public Vector3f getToColor() {
        return ColorHelper.toVector(this.toColor);
    }

    public ParticleType<DustColorTransitionParticleEffect> getType() {
        return ParticleTypes.DUST_COLOR_TRANSITION;
    }
}

