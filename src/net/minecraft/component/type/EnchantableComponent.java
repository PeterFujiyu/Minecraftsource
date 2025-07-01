/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record EnchantableComponent(int value) {
    public static final Codec<EnchantableComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.POSITIVE_INT.fieldOf("value")).forGetter(EnchantableComponent::value)).apply((Applicative<EnchantableComponent, ?>)instance, EnchantableComponent::new));
    public static final PacketCodec<ByteBuf, EnchantableComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EnchantableComponent::value, EnchantableComponent::new);

    public EnchantableComponent {
        if (i <= 0) {
            throw new IllegalArgumentException("Enchantment value must be positive, but was " + i);
        }
    }
}

