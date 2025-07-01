/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TransmuteRecipe
implements CraftingRecipe {
    final String group;
    final CraftingRecipeCategory category;
    final Ingredient input;
    final Ingredient material;
    final RegistryEntry<Item> result;
    @Nullable
    private IngredientPlacement ingredientPlacement;

    public TransmuteRecipe(String group, CraftingRecipeCategory category, Ingredient input, Ingredient material, RegistryEntry<Item> result) {
        this.group = group;
        this.category = category;
        this.input = input;
        this.material = material;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingRecipeInput arg, World arg2) {
        if (arg.getStackCount() != 2) {
            return false;
        }
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStackInSlot(i);
            if (lv.isEmpty()) continue;
            if (!bl && this.input.test(lv) && lv.getItem() != this.result.value()) {
                bl = true;
                continue;
            }
            if (!bl2 && this.material.test(lv)) {
                bl2 = true;
                continue;
            }
            return false;
        }
        return bl && bl2;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        ItemStack lv = ItemStack.EMPTY;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStackInSlot(i);
            if (lv2.isEmpty() || !this.input.test(lv2) || lv2.getItem() == this.result.value()) continue;
            lv = lv2;
        }
        return lv.copyComponentsToNewStack(this.result.value(), 1);
    }

    @Override
    public List<RecipeDisplay> getDisplays() {
        return List.of(new ShapelessCraftingRecipeDisplay(List.of(this.input.toDisplay(), this.material.toDisplay()), new SlotDisplay.ItemSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    @Override
    public RecipeSerializer<TransmuteRecipe> getSerializer() {
        return RecipeSerializer.CRAFTING_TRANSMUTE;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = IngredientPlacement.forShapeless(List.of(this.input, this.material));
        }
        return this.ingredientPlacement;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    public static class Serializer
    implements RecipeSerializer<TransmuteRecipe> {
        private static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group), ((MapCodec)CraftingRecipeCategory.CODEC.fieldOf("category")).orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category), ((MapCodec)Ingredient.CODEC.fieldOf("input")).forGetter(recipe -> recipe.input), ((MapCodec)Ingredient.CODEC.fieldOf("material")).forGetter(recipe -> recipe.material), ((MapCodec)Item.ENTRY_CODEC.fieldOf("result")).forGetter(recipe -> recipe.result)).apply((Applicative<TransmuteRecipe, ?>)instance, TransmuteRecipe::new));
        public static final PacketCodec<RegistryByteBuf, TransmuteRecipe> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, recipe -> recipe.group, CraftingRecipeCategory.PACKET_CODEC, recipe -> recipe.category, Ingredient.PACKET_CODEC, recipe -> recipe.input, Ingredient.PACKET_CODEC, recipe -> recipe.material, PacketCodecs.registryEntry(RegistryKeys.ITEM), recipe -> recipe.result, TransmuteRecipe::new);

        @Override
        public MapCodec<TransmuteRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, TransmuteRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}

