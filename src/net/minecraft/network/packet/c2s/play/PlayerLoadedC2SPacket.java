/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network.packet.c2s.play;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerLoadedC2SPacket() implements Packet<ServerPlayPacketListener>
{
    public static final PacketCodec<ByteBuf, PlayerLoadedC2SPacket> CODEC = PacketCodec.unit(new PlayerLoadedC2SPacket());

    @Override
    public PacketType<PlayerLoadedC2SPacket> getPacketType() {
        return PlayPackets.PLAYER_LOADED;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerLoaded(this);
    }
}

