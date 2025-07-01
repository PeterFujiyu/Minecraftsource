/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

public class CraftingDecoratedPotRecipe
extends SpecialCraftingRecipe {
    public CraftingDecoratedPotRecipe(CraftingRecipeCategory arg) {
        super(arg);
    }

    private static ItemStack getBack(CraftingRecipeInput input) {
        return input.getStackInSlot(1, 0);
    }

    private static ItemStack getLeft(CraftingRecipeInput input) {
        return input.getStackInSlot(0, 1);
    }

    private static ItemStack getRight(CraftingRecipeInput input) {
        return input.getStackInSlot(2, 1);
    }

    private static ItemStack getFront(CraftingRecipeInput input) {
        return input.getStackInSlot(1, 2);
    }

    @Override
    public boolean matches(CraftingRecipeInput arg, World arg2) {
        if (arg.getWidth() != 3 || arg.getHeight() != 3 || arg.getStackCount() != 4) {
            return false;
        }
        return CraftingDecoratedPotRecipe.getBack(arg).isIn(ItemTags.DECORATED_POT_INGREDIENTS) && CraftingDecoratedPotRecipe.getLeft(arg).isIn(ItemTags.DECORATED_POT_INGREDIENTS) && CraftingDecoratedPotRecipe.getRight(arg).isIn(ItemTags.DECORATED_POT_INGREDIENTS) && CraftingDecoratedPotRecipe.getFront(arg).isIn(ItemTags.DECORATED_POT_INGREDIENTS);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        Sherds lv = new Sherds(CraftingDecoratedPotRecipe.getBack(arg).getItem(), CraftingDecoratedPotRecipe.getLeft(arg).getItem(), CraftingDecoratedPotRecipe.getRight(arg).getItem(), CraftingDecoratedPotRecipe.getFront(arg).getItem());
        return DecoratedPotBlockEntity.getStackWith(lv);
    }

    @Override
    public RecipeSerializer<CraftingDecoratedPotRecipe> getSerializer() {
        return RecipeSerializer.CRAFTING_DECORATED_POT;
    }
}

