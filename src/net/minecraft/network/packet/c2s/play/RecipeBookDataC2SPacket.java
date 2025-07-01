/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.NetworkRecipeId;

public record RecipeBookDataC2SPacket(NetworkRecipeId recipeId) implements Packet<ServerPlayPacketListener>
{
    public static final PacketCodec<PacketByteBuf, RecipeBookDataC2SPacket> CODEC = PacketCodec.tuple(NetworkRecipeId.PACKET_CODEC, RecipeBookDataC2SPacket::recipeId, RecipeBookDataC2SPacket::new);

    @Override
    public PacketType<RecipeBookDataC2SPacket> getPacketType() {
        return PlayPackets.RECIPE_BOOK_SEEN_RECIPE;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onRecipeBookData(this);
    }
}

