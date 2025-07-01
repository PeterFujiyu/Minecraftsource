/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.Set;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.PositionFlag;

public record EntityPositionS2CPacket(int entityId, PlayerPosition change, Set<PositionFlag> relatives, boolean onGround) implements Packet<ClientPlayPacketListener>
{
    public static final PacketCodec<PacketByteBuf, EntityPositionS2CPacket> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EntityPositionS2CPacket::entityId, PlayerPosition.PACKET_CODEC, EntityPositionS2CPacket::change, PositionFlag.PACKET_CODEC, EntityPositionS2CPacket::relatives, PacketCodecs.BOOLEAN, EntityPositionS2CPacket::onGround, EntityPositionS2CPacket::new);

    public static EntityPositionS2CPacket create(int entityId, PlayerPosition change, Set<PositionFlag> relatives, boolean onGround) {
        return new EntityPositionS2CPacket(entityId, change, relatives, onGround);
    }

    @Override
    public PacketType<EntityPositionS2CPacket> getPacketType() {
        return PlayPackets.TELEPORT_ENTITY;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityPosition(this);
    }
}

