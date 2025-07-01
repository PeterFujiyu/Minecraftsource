/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;

public interface CraftingRecipe
extends Recipe<CraftingRecipeInput> {
    @Override
    default public RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer();

    public CraftingRecipeCategory getCategory();

    default public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        return CraftingRecipe.collectRecipeRemainders(input);
    }

    public static DefaultedList<ItemStack> collectRecipeRemainders(CraftingRecipeInput input) {
        DefaultedList<ItemStack> lv = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < lv.size(); ++i) {
            Item lv2 = input.getStackInSlot(i).getItem();
            lv.set(i, lv2.getRecipeRemainder());
        }
        return lv;
    }

    @Override
    default public RecipeBookCategory getRecipeBookCategory() {
        return switch (this.getCategory()) {
            default -> throw new MatchException(null, null);
            case CraftingRecipeCategory.BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case CraftingRecipeCategory.EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case CraftingRecipeCategory.REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case CraftingRecipeCategory.MISC -> RecipeBookCategories.CRAFTING_MISC;
        };
    }
}

