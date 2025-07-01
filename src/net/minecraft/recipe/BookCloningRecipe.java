/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BookCloningRecipe
extends SpecialCraftingRecipe {
    public BookCloningRecipe(CraftingRecipeCategory arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingRecipeInput arg, World arg2) {
        if (arg.getStackCount() < 2) {
            return false;
        }
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStackInSlot(i);
            if (lv.isEmpty()) continue;
            if (lv.isOf(Items.WRITTEN_BOOK)) {
                if (bl2) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            if (lv.isOf(Items.WRITABLE_BOOK)) {
                bl = true;
                continue;
            }
            return false;
        }
        return bl2 && bl;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        int i = 0;
        ItemStack lv = ItemStack.EMPTY;
        for (int j = 0; j < arg.size(); ++j) {
            ItemStack lv2 = arg.getStackInSlot(j);
            if (lv2.isEmpty()) continue;
            if (lv2.isOf(Items.WRITTEN_BOOK)) {
                if (!lv.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                lv = lv2;
                continue;
            }
            if (lv2.isOf(Items.WRITABLE_BOOK)) {
                ++i;
                continue;
            }
            return ItemStack.EMPTY;
        }
        WrittenBookContentComponent lv3 = lv.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (lv.isEmpty() || i < 1 || lv3 == null) {
            return ItemStack.EMPTY;
        }
        WrittenBookContentComponent lv4 = lv3.copy();
        if (lv4 == null) {
            return ItemStack.EMPTY;
        }
        ItemStack lv5 = lv.copyWithCount(i);
        lv5.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, lv4);
        return lv5;
    }

    @Override
    public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        DefaultedList<ItemStack> lv = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < lv.size(); ++i) {
            ItemStack lv2 = input.getStackInSlot(i);
            ItemStack lv3 = lv2.getItem().getRecipeRemainder();
            if (!lv3.isEmpty()) {
                lv.set(i, lv3);
                continue;
            }
            if (!(lv2.getItem() instanceof WrittenBookItem)) continue;
            lv.set(i, lv2.copyWithCount(1));
            break;
        }
        return lv;
    }

    @Override
    public RecipeSerializer<BookCloningRecipe> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }
}

