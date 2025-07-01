/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item.consume;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public record PlaySoundConsumeEffect(RegistryEntry<SoundEvent> sound) implements ConsumeEffect
{
    public static final MapCodec<PlaySoundConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)SoundEvent.ENTRY_CODEC.fieldOf("sound")).forGetter(PlaySoundConsumeEffect::sound)).apply((Applicative<PlaySoundConsumeEffect, ?>)instance, PlaySoundConsumeEffect::new));
    public static final PacketCodec<RegistryByteBuf, PlaySoundConsumeEffect> PACKET_CODEC = PacketCodec.tuple(SoundEvent.ENTRY_PACKET_CODEC, PlaySoundConsumeEffect::sound, PlaySoundConsumeEffect::new);

    public ConsumeEffect.Type<PlaySoundConsumeEffect> getType() {
        return ConsumeEffect.Type.PLAY_SOUND;
    }

    @Override
    public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
        world.playSound(null, user.getBlockPos(), this.sound.value(), user.getSoundCategory(), 1.0f, 1.0f);
        return true;
    }
}

