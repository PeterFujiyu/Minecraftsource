/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SetCursorItemS2CPacket(ItemStack contents) implements Packet<ClientPlayPacketListener>
{
    public static final PacketCodec<RegistryByteBuf, SetCursorItemS2CPacket> CODEC = PacketCodec.tuple(ItemStack.OPTIONAL_PACKET_CODEC, SetCursorItemS2CPacket::contents, SetCursorItemS2CPacket::new);

    @Override
    public PacketType<SetCursorItemS2CPacket> getPacketType() {
        return PlayPackets.SET_CURSOR_ITEM;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSetCursorItem(this);
    }
}

