/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ButtonClickC2SPacket(int syncId, int buttonId) implements Packet<ServerPlayPacketListener>
{
    public static final PacketCodec<PacketByteBuf, ButtonClickC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, ButtonClickC2SPacket::syncId, PacketCodecs.VAR_INT, ButtonClickC2SPacket::buttonId, ButtonClickC2SPacket::new);

    @Override
    public PacketType<ButtonClickC2SPacket> getPacketType() {
        return PlayPackets.CONTAINER_BUTTON_CLICK;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onButtonClick(this);
    }
}

