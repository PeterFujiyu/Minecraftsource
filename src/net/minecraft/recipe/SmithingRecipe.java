/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.world.World;

public interface SmithingRecipe
extends Recipe<SmithingRecipeInput> {
    @Override
    default public RecipeType<SmithingRecipe> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer();

    @Override
    default public boolean matches(SmithingRecipeInput arg, World arg2) {
        return Ingredient.matches(this.template(), arg.template()) && Ingredient.matches(this.base(), arg.base()) && Ingredient.matches(this.addition(), arg.addition());
    }

    public Optional<Ingredient> template();

    public Optional<Ingredient> base();

    public Optional<Ingredient> addition();

    @Override
    default public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.SMITHING;
    }
}

