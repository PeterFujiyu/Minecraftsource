/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ShieldDecorationRecipe
extends SpecialCraftingRecipe {
    public ShieldDecorationRecipe(CraftingRecipeCategory arg) {
        super(arg);
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
            if (lv.getItem() instanceof BannerItem) {
                if (bl2) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            if (lv.isOf(Items.SHIELD)) {
                if (bl) {
                    return false;
                }
                BannerPatternsComponent lv2 = lv.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
                if (!lv2.layers().isEmpty()) {
                    return false;
                }
                bl = true;
                continue;
            }
            return false;
        }
        return bl && bl2;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        ItemStack lv = ItemStack.EMPTY;
        ItemStack lv2 = ItemStack.EMPTY;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv3 = arg.getStackInSlot(i);
            if (lv3.isEmpty()) continue;
            if (lv3.getItem() instanceof BannerItem) {
                lv = lv3;
                continue;
            }
            if (!lv3.isOf(Items.SHIELD)) continue;
            lv2 = lv3.copy();
        }
        if (lv2.isEmpty()) {
            return lv2;
        }
        lv2.set(DataComponentTypes.BANNER_PATTERNS, lv.get(DataComponentTypes.BANNER_PATTERNS));
        lv2.set(DataComponentTypes.BASE_COLOR, ((BannerItem)lv.getItem()).getColor());
        return lv2;
    }

    @Override
    public RecipeSerializer<ShieldDecorationRecipe> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}

