/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner {
    public static <T> void alignRecipeToGrid(int width, int height, Recipe<?> recipe, Iterable<T> slots, Filler<T> filter) {
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe lv = (ShapedRecipe)recipe;
            RecipeGridAligner.alignRecipeToGrid(width, height, lv.getWidth(), lv.getHeight(), slots, filter);
        } else {
            RecipeGridAligner.alignRecipeToGrid(width, height, width, height, slots, filter);
        }
    }

    public static <T> void alignRecipeToGrid(int width, int height, int recipeWidth, int recipeHeight, Iterable<T> slots, Filler<T> filter) {
        Iterator<T> iterator = slots.iterator();
        int m = 0;
        block0: for (int n = 0; n < height; ++n) {
            boolean bl = (float)recipeHeight < (float)height / 2.0f;
            int o = MathHelper.floor((float)height / 2.0f - (float)recipeHeight / 2.0f);
            if (bl && o > n) {
                m += width;
                ++n;
            }
            for (int p = 0; p < width; ++p) {
                boolean bl2;
                if (!iterator.hasNext()) {
                    return;
                }
                bl = (float)recipeWidth < (float)width / 2.0f;
                o = MathHelper.floor((float)width / 2.0f - (float)recipeWidth / 2.0f);
                int q = recipeWidth;
                boolean bl3 = bl2 = p < recipeWidth;
                if (bl) {
                    q = o + recipeWidth;
                    boolean bl4 = bl2 = o <= p && p < o + recipeWidth;
                }
                if (bl2) {
                    filter.addItemToSlot(iterator.next(), m, p, n);
                } else if (q == p) {
                    m += width - p;
                    continue block0;
                }
                ++m;
            }
        }
    }

    @FunctionalInterface
    public static interface Filler<T> {
        public void addItemToSlot(T var1, int var2, int var3, int var4);
    }
}

