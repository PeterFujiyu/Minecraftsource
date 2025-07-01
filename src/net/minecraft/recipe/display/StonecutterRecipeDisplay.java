/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe.display;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;

public record StonecutterRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay
{
    public static final MapCodec<StonecutterRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)SlotDisplay.CODEC.fieldOf("input")).forGetter(StonecutterRecipeDisplay::input), ((MapCodec)SlotDisplay.CODEC.fieldOf("result")).forGetter(StonecutterRecipeDisplay::result), ((MapCodec)SlotDisplay.CODEC.fieldOf("crafting_station")).forGetter(StonecutterRecipeDisplay::craftingStation)).apply((Applicative<StonecutterRecipeDisplay, ?>)instance, StonecutterRecipeDisplay::new));
    public static final PacketCodec<RegistryByteBuf, StonecutterRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, StonecutterRecipeDisplay::input, SlotDisplay.PACKET_CODEC, StonecutterRecipeDisplay::result, SlotDisplay.PACKET_CODEC, StonecutterRecipeDisplay::craftingStation, StonecutterRecipeDisplay::new);
    public static final RecipeDisplay.Serializer<StonecutterRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<StonecutterRecipeDisplay>(CODEC, PACKET_CODEC);

    public RecipeDisplay.Serializer<StonecutterRecipeDisplay> serializer() {
        return SERIALIZER;
    }
}

