/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;

public class InputSlotFiller<R extends Recipe<?>> {
    private static final int field_51523 = -1;
    private final PlayerInventory inventory;
    private final Handler<R> handler;
    private final boolean craftAll;
    private final int width;
    private final int height;
    private final List<Slot> inputSlots;
    private final List<Slot> slotsToReturn;

    public static <I extends RecipeInput, R extends Recipe<I>> AbstractRecipeScreenHandler.PostFillAction fill(Handler<R> handler, int width, int height, List<Slot> inputSlots, List<Slot> slotsToReturn, PlayerInventory inventory, RecipeEntry<R> recipe, boolean craftAll, boolean creative) {
        InputSlotFiller<R> lv = new InputSlotFiller<R>(handler, inventory, craftAll, width, height, inputSlots, slotsToReturn);
        if (!creative && !lv.canReturnInputs()) {
            return AbstractRecipeScreenHandler.PostFillAction.NOTHING;
        }
        RecipeFinder lv2 = new RecipeFinder();
        inventory.populateRecipeFinder(lv2);
        handler.populateRecipeFinder(lv2);
        return lv.tryFill(recipe, lv2);
    }

    private InputSlotFiller(Handler<R> handler, PlayerInventory inventory, boolean craftAll, int width, int height, List<Slot> inputSlots, List<Slot> slotsToReturn) {
        this.handler = handler;
        this.inventory = inventory;
        this.craftAll = craftAll;
        this.width = width;
        this.height = height;
        this.inputSlots = inputSlots;
        this.slotsToReturn = slotsToReturn;
    }

    private AbstractRecipeScreenHandler.PostFillAction tryFill(RecipeEntry<R> recipe, RecipeFinder finder) {
        if (finder.isCraftable((Recipe<?>)recipe.value(), null)) {
            this.fill(recipe, finder);
            this.inventory.markDirty();
            return AbstractRecipeScreenHandler.PostFillAction.NOTHING;
        }
        this.returnInputs();
        this.inventory.markDirty();
        return AbstractRecipeScreenHandler.PostFillAction.PLACE_GHOST_RECIPE;
    }

    private void returnInputs() {
        for (Slot lv : this.slotsToReturn) {
            ItemStack lv2 = lv.getStack().copy();
            this.inventory.offer(lv2, false);
            lv.setStackNoCallbacks(lv2);
        }
        this.handler.clear();
    }

    private void fill(RecipeEntry<R> recipe, RecipeFinder finder) {
        boolean bl = this.handler.matches(recipe);
        int i = finder.countCrafts((Recipe<?>)recipe.value(), null);
        if (bl) {
            for (Slot lv : this.inputSlots) {
                ItemStack lv2 = lv.getStack();
                if (lv2.isEmpty() || Math.min(i, lv2.getMaxCount()) >= lv2.getCount() + 1) continue;
                return;
            }
        }
        int j = this.calculateCraftAmount(i, bl);
        ArrayList<RegistryEntry<Item>> list = new ArrayList<RegistryEntry<Item>>();
        if (!finder.isCraftable((Recipe<?>)recipe.value(), j, list::add)) {
            return;
        }
        int k = InputSlotFiller.clampToMaxCount(j, list);
        if (k != j) {
            list.clear();
            if (!finder.isCraftable((Recipe<?>)recipe.value(), k, list::add)) {
                return;
            }
        }
        this.returnInputs();
        RecipeGridAligner.alignRecipeToGrid(this.width, this.height, recipe.value(), recipe.value().getIngredientPlacement().getPlacementSlots(), (slot, index, x, y) -> {
            if (slot == -1) {
                return;
            }
            Slot lv = this.inputSlots.get(index);
            RegistryEntry lv2 = (RegistryEntry)list.get((int)slot);
            int m = k;
            while (m > 0) {
                if ((m = this.fillInputSlot(lv, lv2, m)) != -1) continue;
                return;
            }
        });
    }

    private static int clampToMaxCount(int count, List<RegistryEntry<Item>> entries) {
        for (RegistryEntry<Item> lv : entries) {
            count = Math.min(count, lv.value().getMaxCount());
        }
        return count;
    }

    private int calculateCraftAmount(int forCraftAll, boolean match) {
        if (this.craftAll) {
            return forCraftAll;
        }
        if (match) {
            int j = Integer.MAX_VALUE;
            for (Slot lv : this.inputSlots) {
                ItemStack lv2 = lv.getStack();
                if (lv2.isEmpty() || j <= lv2.getCount()) continue;
                j = lv2.getCount();
            }
            if (j != Integer.MAX_VALUE) {
                ++j;
            }
            return j;
        }
        return 1;
    }

    private int fillInputSlot(Slot slot, RegistryEntry<Item> item, int count) {
        ItemStack lv = slot.getStack();
        int j = this.inventory.getMatchingSlot(item, lv);
        if (j == -1) {
            return -1;
        }
        ItemStack lv2 = this.inventory.getStack(j);
        ItemStack lv3 = count < lv2.getCount() ? this.inventory.removeStack(j, count) : this.inventory.removeStack(j);
        int k = lv3.getCount();
        if (lv.isEmpty()) {
            slot.setStackNoCallbacks(lv3);
        } else {
            lv.increment(k);
        }
        return count - k;
    }

    private boolean canReturnInputs() {
        ArrayList<ItemStack> list = Lists.newArrayList();
        int i = this.getFreeInventorySlots();
        for (Slot lv : this.inputSlots) {
            ItemStack lv2 = lv.getStack().copy();
            if (lv2.isEmpty()) continue;
            int j = this.inventory.getOccupiedSlotWithRoomForStack(lv2);
            if (j == -1 && list.size() <= i) {
                for (ItemStack lv3 : list) {
                    if (!ItemStack.areItemsEqual(lv3, lv2) || lv3.getCount() == lv3.getMaxCount() || lv3.getCount() + lv2.getCount() > lv3.getMaxCount()) continue;
                    lv3.increment(lv2.getCount());
                    lv2.setCount(0);
                    break;
                }
                if (lv2.isEmpty()) continue;
                if (list.size() < i) {
                    list.add(lv2);
                    continue;
                }
                return false;
            }
            if (j != -1) continue;
            return false;
        }
        return true;
    }

    private int getFreeInventorySlots() {
        int i = 0;
        for (ItemStack lv : this.inventory.main) {
            if (!lv.isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public static interface Handler<T extends Recipe<?>> {
        public void populateRecipeFinder(RecipeFinder var1);

        public void clear();

        public boolean matches(RecipeEntry<T> var1);
    }
}

