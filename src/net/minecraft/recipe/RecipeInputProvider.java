/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import net.minecraft.recipe.RecipeFinder;

@FunctionalInterface
public interface RecipeInputProvider {
    public void provideRecipeInputs(RecipeFinder var1);
}

