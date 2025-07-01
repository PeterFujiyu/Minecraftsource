/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe.input;

import net.minecraft.item.ItemStack;

public interface RecipeInput {
    public ItemStack getStackInSlot(int var1);

    public int size();

    default public boolean isEmpty() {
        for (int i = 0; i < this.size(); ++i) {
            if (this.getStackInSlot(i).isEmpty()) continue;
            return false;
        }
        return true;
    }
}

