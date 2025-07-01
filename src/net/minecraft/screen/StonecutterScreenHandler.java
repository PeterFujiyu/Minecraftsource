/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.screen;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterScreenHandler
extends ScreenHandler {
    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 29;
    private static final int OUTPUT_START = 29;
    private static final int OUTPUT_END = 38;
    private final ScreenHandlerContext context;
    final Property selectedRecipe = Property.create();
    private final World world;
    private CuttingRecipeDisplay.Grouping<StonecuttingRecipe> availableRecipes = CuttingRecipeDisplay.Grouping.empty();
    private ItemStack inputStack = ItemStack.EMPTY;
    long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    Runnable contentsChangedListener = () -> {};
    public final Inventory input = new SimpleInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            StonecutterScreenHandler.this.onContentChanged(this);
            StonecutterScreenHandler.this.contentsChangedListener.run();
        }
    };
    final CraftingResultInventory output = new CraftingResultInventory();

    public StonecutterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public StonecutterScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(ScreenHandlerType.STONECUTTER, syncId);
        this.context = context;
        this.world = playerInventory.player.getWorld();
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
                StonecutterScreenHandler.this.output.unlockLastRecipe(player, this.getInputStacks());
                ItemStack lv = StonecutterScreenHandler.this.inputSlot.takeStack(1);
                if (!lv.isEmpty()) {
                    StonecutterScreenHandler.this.populateResult(StonecutterScreenHandler.this.selectedRecipe.get());
                }
                context.run((world, pos) -> {
                    long l = world.getTime();
                    if (StonecutterScreenHandler.this.lastTakeTime != l) {
                        world.playSound(null, (BlockPos)pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        StonecutterScreenHandler.this.lastTakeTime = l;
                    }
                });
                super.onTakeItem(player, stack);
            }

            private List<ItemStack> getInputStacks() {
                return List.of(StonecutterScreenHandler.this.inputSlot.getStack());
            }
        });
        this.addPlayerSlots(playerInventory, 8, 84);
        this.addProperty(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return StonecutterScreenHandler.canUse(this.context, player, Blocks.STONECUTTER);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.selectedRecipe.get() == id) {
            return false;
        }
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult(id);
        }
        return true;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack lv = this.inputSlot.getStack();
        if (!lv.isOf(this.inputStack.getItem())) {
            this.inputStack = lv.copy();
            this.updateInput(lv);
        }
    }

    private void updateInput(ItemStack stack) {
        this.selectedRecipe.set(-1);
        this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        this.availableRecipes = !stack.isEmpty() ? this.world.getRecipeManager().getStonecutterRecipes().filter(stack) : CuttingRecipeDisplay.Grouping.empty();
    }

    void populateResult(int selectedId) {
        Optional<RecipeEntry<Object>> optional;
        if (!this.availableRecipes.isEmpty() && this.isInBounds(selectedId)) {
            CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe> lv = this.availableRecipes.entries().get(selectedId);
            optional = lv.recipe().recipe();
        } else {
            optional = Optional.empty();
        }
        optional.ifPresentOrElse(recipe -> {
            this.output.setLastRecipe((RecipeEntry<?>)recipe);
            this.outputSlot.setStackNoCallbacks(((StonecuttingRecipe)recipe.value()).craft(new SingleStackRecipeInput(this.input.getStack(0)), (RegistryWrapper.WrapperLookup)this.world.getRegistryManager()));
        }, () -> {
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
            this.output.setLastRecipe(null);
        });
        this.sendContentUpdates();
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return ScreenHandlerType.STONECUTTER;
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(slot);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            Item lv4 = lv3.getItem();
            lv = lv3.copy();
            if (slot == 1) {
                lv4.onCraftByPlayer(lv3, player.getWorld(), player);
                if (!this.insertItem(lv3, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onQuickTransfer(lv3, lv);
            } else if (slot == 0 ? !this.insertItem(lv3, 2, 38, false) : (this.world.getRecipeManager().getStonecutterRecipes().contains(lv3) ? !this.insertItem(lv3, 0, 1, false) : (slot >= 2 && slot < 29 ? !this.insertItem(lv3, 29, 38, false) : slot >= 29 && slot < 38 && !this.insertItem(lv3, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            }
            lv2.markDirty();
            if (lv3.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
            lv2.onTakeItem(player, lv3);
            if (slot == 1) {
                player.dropItem(lv3, false);
            }
            this.sendContentUpdates();
        }
        return lv;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.output.removeStack(1);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }
}

