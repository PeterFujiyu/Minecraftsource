/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public record RepairableComponent(RegistryEntryList<Item> items) {
    public static final Codec<RepairableComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("items")).forGetter(RepairableComponent::items)).apply((Applicative<RepairableComponent, ?>)instance, RepairableComponent::new));
    public static final PacketCodec<RegistryByteBuf, RepairableComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntryList(RegistryKeys.ITEM), RepairableComponent::items, RepairableComponent::new);

    public boolean matches(ItemStack stack) {
        return stack.isIn(this.items);
    }
}

