/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.recipe.Ingredient;

public class IngredientPlacement {
    public static final int field_55495 = -1;
    public static final IngredientPlacement NONE = new IngredientPlacement(List.of(), IntList.of());
    private final List<Ingredient> ingredients;
    private final IntList placementSlots;

    private IngredientPlacement(List<Ingredient> ingredients, IntList placementSlots) {
        this.ingredients = ingredients;
        this.placementSlots = placementSlots;
    }

    public static IngredientPlacement forSingleSlot(Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return NONE;
        }
        return new IngredientPlacement(List.of(ingredient), IntList.of(0));
    }

    public static IngredientPlacement forMultipleSlots(List<Optional<Ingredient>> ingredients) {
        int i = ingredients.size();
        ArrayList<Ingredient> list2 = new ArrayList<Ingredient>(i);
        IntArrayList intList = new IntArrayList(i);
        int j = 0;
        for (Optional<Ingredient> optional : ingredients) {
            if (optional.isPresent()) {
                Ingredient lv = optional.get();
                if (lv.isEmpty()) {
                    return NONE;
                }
                list2.add(lv);
                intList.add(j++);
                continue;
            }
            intList.add(-1);
        }
        return new IngredientPlacement(list2, intList);
    }

    public static IngredientPlacement forShapeless(List<Ingredient> ingredients) {
        int i = ingredients.size();
        IntArrayList intList = new IntArrayList(i);
        for (int j = 0; j < i; ++j) {
            Ingredient lv = ingredients.get(j);
            if (lv.isEmpty()) {
                return NONE;
            }
            intList.add(j);
        }
        return new IngredientPlacement(ingredients, intList);
    }

    public IntList getPlacementSlots() {
        return this.placementSlots;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean hasNoPlacement() {
        return this.placementSlots.isEmpty();
    }
}

