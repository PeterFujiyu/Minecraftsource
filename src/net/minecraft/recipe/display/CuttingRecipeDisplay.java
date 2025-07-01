/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe.display;

import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.SlotDisplay;

public record CuttingRecipeDisplay<T extends Recipe<?>>(SlotDisplay optionDisplay, Optional<RecipeEntry<T>> recipe) {
    public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, CuttingRecipeDisplay<T>> codec() {
        return PacketCodec.tuple(SlotDisplay.PACKET_CODEC, CuttingRecipeDisplay::optionDisplay, display -> new CuttingRecipeDisplay((SlotDisplay)display, Optional.empty()));
    }

    public record Grouping<T extends Recipe<?>>(List<GroupEntry<T>> entries) {
        public static <T extends Recipe<?>> Grouping<T> empty() {
            return new Grouping<T>(List.of());
        }

        public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, Grouping<T>> codec() {
            return PacketCodec.tuple(GroupEntry.codec().collect(PacketCodecs.toList()), Grouping::entries, Grouping::new);
        }

        public boolean contains(ItemStack stack) {
            return this.entries.stream().anyMatch(entry -> entry.input.test(stack));
        }

        public Grouping<T> filter(ItemStack stack) {
            return new Grouping<T>(this.entries.stream().filter((? super T entry) -> entry.input.test(stack)).toList());
        }

        public boolean isEmpty() {
            return this.entries.isEmpty();
        }

        public int size() {
            return this.entries.size();
        }
    }

    public record GroupEntry<T extends Recipe<?>>(Ingredient input, CuttingRecipeDisplay<T> recipe) {
        public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, GroupEntry<T>> codec() {
            return PacketCodec.tuple(Ingredient.PACKET_CODEC, GroupEntry::input, CuttingRecipeDisplay.codec(), GroupEntry::recipe, GroupEntry::new);
        }
    }
}

