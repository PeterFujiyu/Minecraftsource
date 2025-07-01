/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerRotationS2CPacket(float yRot, float xRot) implements Packet<ClientPlayPacketListener>
{
    public static final PacketCodec<PacketByteBuf, PlayerRotationS2CPacket> CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, PlayerRotationS2CPacket::yRot, PacketCodecs.FLOAT, PlayerRotationS2CPacket::xRot, PlayerRotationS2CPacket::new);

    @Override
    public PacketType<PlayerRotationS2CPacket> getPacketType() {
        return PlayPackets.PLAYER_ROTATION;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerRotation(this);
    }
}

