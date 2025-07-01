/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.screen;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;

public abstract class AbstractCraftingScreenHandler
extends AbstractRecipeScreenHandler {
    private final int width;
    private final int height;
    protected final RecipeInputInventory craftingInventory;
    protected final CraftingResultInventory craftingResultInventory = new CraftingResultInventory();

    public AbstractCraftingScreenHandler(ScreenHandlerType<?> type, int syncId, int width, int height) {
        super(type, syncId);
        this.width = width;
        this.height = height;
        this.craftingInventory = new CraftingInventory(this, width, height);
    }

    protected Slot addResultSlot(PlayerEntity player, int x, int y) {
        return this.addSlot(new CraftingResultSlot(player, this.craftingInventory, this.craftingResultInventory, 0, x, y));
    }

    protected void addInputSlots(int x, int y) {
        for (int k = 0; k < this.width; ++k) {
            for (int l = 0; l < this.height; ++l) {
                this.addSlot(new Slot(this.craftingInventory, l + k * this.width, x + l * 18, y + k * 18));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AbstractRecipeScreenHandler.PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory) {
        RecipeEntry<CraftingRecipe> lv = recipe;
        this.onInputSlotFillStart();
        try {
            List<Slot> list = this.getInputSlots();
            AbstractRecipeScreenHandler.PostFillAction postFillAction = InputSlotFiller.fill(new InputSlotFiller.Handler<CraftingRecipe>(){

                @Override
                public void populateRecipeFinder(RecipeFinder finder) {
                    AbstractCraftingScreenHandler.this.populateRecipeFinder(finder);
                }

                @Override
                public void clear() {
                    AbstractCraftingScreenHandler.this.craftingResultInventory.clear();
                    AbstractCraftingScreenHandler.this.craftingInventory.clear();
                }

                @Override
                public boolean matches(RecipeEntry<CraftingRecipe> entry) {
                    return entry.value().matches(AbstractCraftingScreenHandler.this.craftingInventory.createRecipeInput(), AbstractCraftingScreenHandler.this.getPlayer().getWorld());
                }
            }, this.width, this.height, list, list, inventory, lv, craftAll, creative);
            return postFillAction;
        } finally {
            this.onInputSlotFillFinish(world, lv);
        }
    }

    protected void onInputSlotFillStart() {
    }

    protected void onInputSlotFillFinish(ServerWorld world, RecipeEntry<CraftingRecipe> recipe) {
    }

    public abstract Slot getOutputSlot();

    public abstract List<Slot> getInputSlots();

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    protected abstract PlayerEntity getPlayer();

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        this.craftingInventory.provideRecipeInputs(finder);
    }
}

