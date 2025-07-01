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
import net.minecraft.recipe.NetworkRecipeId;

public record CraftRequestC2SPacket(int syncId, NetworkRecipeId recipeId, boolean craftAll) implements Packet<ServerPlayPacketListener>
{
    public static final PacketCodec<PacketByteBuf, CraftRequestC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, CraftRequestC2SPacket::syncId, NetworkRecipeId.PACKET_CODEC, CraftRequestC2SPacket::recipeId, PacketCodecs.BOOLEAN, CraftRequestC2SPacket::craftAll, CraftRequestC2SPacket::new);

    @Override
    public PacketType<CraftRequestC2SPacket> getPacketType() {
        return PlayPackets.PLACE_RECIPE;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onCraftRequest(this);
    }
}

