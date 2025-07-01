/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record UseCooldownComponent(float seconds, Optional<Identifier> cooldownGroup) {
    public static final Codec<UseCooldownComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.POSITIVE_FLOAT.fieldOf("seconds")).forGetter(UseCooldownComponent::seconds), Identifier.CODEC.optionalFieldOf("cooldown_group").forGetter(UseCooldownComponent::cooldownGroup)).apply((Applicative<UseCooldownComponent, ?>)instance, UseCooldownComponent::new));
    public static final PacketCodec<RegistryByteBuf, UseCooldownComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, UseCooldownComponent::seconds, Identifier.PACKET_CODEC.collect(PacketCodecs::optional), UseCooldownComponent::cooldownGroup, UseCooldownComponent::new);

    public UseCooldownComponent(float seconds) {
        this(seconds, Optional.empty());
    }

    public int getCooldownTicks() {
        return (int)(this.seconds * 20.0f);
    }

    public void set(ItemStack stack, LivingEntity user) {
        if (user instanceof PlayerEntity) {
            PlayerEntity lv = (PlayerEntity)user;
            lv.getItemCooldownManager().set(stack, this.getCooldownTicks());
        }
    }
}

