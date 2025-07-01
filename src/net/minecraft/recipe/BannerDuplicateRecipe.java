/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BannerDuplicateRecipe
extends SpecialCraftingRecipe {
    public BannerDuplicateRecipe(CraftingRecipeCategory arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingRecipeInput arg, World arg2) {
        if (arg.getStackCount() != 2) {
            return false;
        }
        DyeColor lv = null;
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStackInSlot(i);
            if (lv2.isEmpty()) continue;
            Item lv3 = lv2.getItem();
            if (lv3 instanceof BannerItem) {
                BannerItem lv4 = (BannerItem)lv3;
                if (lv == null) {
                    lv = lv4.getColor();
                } else if (lv != lv4.getColor()) {
                    return false;
                }
            } else {
                return false;
            }
            int j = lv2.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().size();
            if (j > 6) {
                return false;
            }
            if (j > 0) {
                if (bl2) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            if (bl) {
                return false;
            }
            bl = true;
        }
        return bl2 && bl;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        for (int i = 0; i < arg.size(); ++i) {
            int j;
            ItemStack lv = arg.getStackInSlot(i);
            if (lv.isEmpty() || (j = lv.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().size()) <= 0 || j > 6) continue;
            return lv.copyWithCount(1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        DefaultedList<ItemStack> lv = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < lv.size(); ++i) {
            ItemStack lv2 = input.getStackInSlot(i);
            if (lv2.isEmpty()) continue;
            ItemStack lv3 = lv2.getItem().getRecipeRemainder();
            if (!lv3.isEmpty()) {
                lv.set(i, lv3);
                continue;
            }
            if (lv2.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().isEmpty()) continue;
            lv.set(i, lv2.copyWithCount(1));
        }
        return lv;
    }

    @Override
    public RecipeSerializer<BannerDuplicateRecipe> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }
}

