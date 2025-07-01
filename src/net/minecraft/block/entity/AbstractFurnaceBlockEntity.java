/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFurnaceBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory,
RecipeUnlocker,
RecipeInputProvider {
    protected static final int INPUT_SLOT_INDEX = 0;
    protected static final int FUEL_SLOT_INDEX = 1;
    protected static final int OUTPUT_SLOT_INDEX = 2;
    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;
    public static final int COOK_TIME_PROPERTY_INDEX = 2;
    public static final int COOK_TIME_TOTAL_PROPERTY_INDEX = 3;
    public static final int PROPERTY_COUNT = 4;
    public static final int DEFAULT_COOK_TIME = 200;
    public static final int field_31295 = 2;
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    int litTimeRemaining;
    int litTotalTime;
    int cookingTimeSpent;
    int cookingTotalTime;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return AbstractFurnaceBlockEntity.this.litTimeRemaining;
                }
                case 1: {
                    return AbstractFurnaceBlockEntity.this.litTotalTime;
                }
                case 2: {
                    return AbstractFurnaceBlockEntity.this.cookingTimeSpent;
                }
                case 3: {
                    return AbstractFurnaceBlockEntity.this.cookingTotalTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    AbstractFurnaceBlockEntity.this.litTimeRemaining = value;
                    break;
                }
                case 1: {
                    AbstractFurnaceBlockEntity.this.litTotalTime = value;
                    break;
                }
                case 2: {
                    AbstractFurnaceBlockEntity.this.cookingTimeSpent = value;
                    break;
                }
                case 3: {
                    AbstractFurnaceBlockEntity.this.cookingTotalTime = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };
    private final Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap();
    private final ServerRecipeManager.MatchGetter<SingleStackRecipeInput, ? extends AbstractCookingRecipe> matchGetter;

    protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType, pos, state);
        this.matchGetter = ServerRecipeManager.createCachedMatchGetter(recipeType);
    }

    private boolean isBurning() {
        return this.litTimeRemaining > 0;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registries);
        this.cookingTimeSpent = nbt.getShort("cooking_time_spent");
        this.cookingTotalTime = nbt.getShort("cooking_total_time");
        this.litTimeRemaining = nbt.getShort("lit_time_remaining");
        this.litTotalTime = nbt.getShort("lit_total_time");
        NbtCompound lv = nbt.getCompound("RecipesUsed");
        for (String string : lv.getKeys()) {
            this.recipesUsed.put(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(string)), lv.getInt(string));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putShort("cooking_time_spent", (short)this.cookingTimeSpent);
        nbt.putShort("cooking_total_time", (short)this.cookingTotalTime);
        nbt.putShort("lit_time_remaining", (short)this.litTimeRemaining);
        nbt.putShort("lit_total_time", (short)this.litTotalTime);
        Inventories.writeNbt(nbt, this.inventory, registries);
        NbtCompound lv = new NbtCompound();
        this.recipesUsed.forEach((BiConsumer<RegistryKey<Recipe<?>>, Integer>)((BiConsumer<RegistryKey, Integer>)(recipeKey, count) -> lv.putInt(recipeKey.getValue().toString(), (int)count)));
        nbt.put("RecipesUsed", lv);
    }

    public static void tick(ServerWorld world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity) {
        boolean bl4;
        boolean bl = blockEntity.isBurning();
        boolean bl2 = false;
        if (blockEntity.isBurning()) {
            --blockEntity.litTimeRemaining;
        }
        ItemStack lv = blockEntity.inventory.get(1);
        ItemStack lv2 = blockEntity.inventory.get(0);
        boolean bl3 = !lv2.isEmpty();
        boolean bl5 = bl4 = !lv.isEmpty();
        if (blockEntity.isBurning() || bl4 && bl3) {
            SingleStackRecipeInput lv3 = new SingleStackRecipeInput(lv2);
            RecipeEntry lv4 = bl3 ? (RecipeEntry)blockEntity.matchGetter.getFirstMatch(lv3, world).orElse(null) : null;
            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isBurning() && AbstractFurnaceBlockEntity.canAcceptRecipeOutput(world.getRegistryManager(), lv4, lv3, blockEntity.inventory, i)) {
                blockEntity.litTotalTime = blockEntity.litTimeRemaining = blockEntity.getFuelTime(world.getFuelRegistry(), lv);
                if (blockEntity.isBurning()) {
                    bl2 = true;
                    if (bl4) {
                        Item lv5 = lv.getItem();
                        lv.decrement(1);
                        if (lv.isEmpty()) {
                            blockEntity.inventory.set(1, lv5.getRecipeRemainder());
                        }
                    }
                }
            }
            if (blockEntity.isBurning() && AbstractFurnaceBlockEntity.canAcceptRecipeOutput(world.getRegistryManager(), lv4, lv3, blockEntity.inventory, i)) {
                ++blockEntity.cookingTimeSpent;
                if (blockEntity.cookingTimeSpent == blockEntity.cookingTotalTime) {
                    blockEntity.cookingTimeSpent = 0;
                    blockEntity.cookingTotalTime = AbstractFurnaceBlockEntity.getCookTime(world, blockEntity);
                    if (AbstractFurnaceBlockEntity.craftRecipe(world.getRegistryManager(), lv4, lv3, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(lv4);
                    }
                    bl2 = true;
                }
            } else {
                blockEntity.cookingTimeSpent = 0;
            }
        } else if (!blockEntity.isBurning() && blockEntity.cookingTimeSpent > 0) {
            blockEntity.cookingTimeSpent = MathHelper.clamp(blockEntity.cookingTimeSpent - 2, 0, blockEntity.cookingTotalTime);
        }
        if (bl != blockEntity.isBurning()) {
            bl2 = true;
            state = (BlockState)state.with(AbstractFurnaceBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (bl2) {
            AbstractFurnaceBlockEntity.markDirty(world, pos, state);
        }
    }

    private static boolean canAcceptRecipeOutput(DynamicRegistryManager dynamicRegistryManager, @Nullable RecipeEntry<? extends AbstractCookingRecipe> recipe, SingleStackRecipeInput input, DefaultedList<ItemStack> inventory, int maxCount) {
        if (inventory.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack lv = recipe.value().craft(input, (RegistryWrapper.WrapperLookup)dynamicRegistryManager);
        if (lv.isEmpty()) {
            return false;
        }
        ItemStack lv2 = inventory.get(2);
        if (lv2.isEmpty()) {
            return true;
        }
        if (!ItemStack.areItemsAndComponentsEqual(lv2, lv)) {
            return false;
        }
        if (lv2.getCount() < maxCount && lv2.getCount() < lv2.getMaxCount()) {
            return true;
        }
        return lv2.getCount() < lv.getMaxCount();
    }

    private static boolean craftRecipe(DynamicRegistryManager dynamicRegistryManager, @Nullable RecipeEntry<? extends AbstractCookingRecipe> recipe, SingleStackRecipeInput input, DefaultedList<ItemStack> inventory, int maxCount) {
        if (recipe == null || !AbstractFurnaceBlockEntity.canAcceptRecipeOutput(dynamicRegistryManager, recipe, input, inventory, maxCount)) {
            return false;
        }
        ItemStack lv = inventory.get(0);
        ItemStack lv2 = recipe.value().craft(input, (RegistryWrapper.WrapperLookup)dynamicRegistryManager);
        ItemStack lv3 = inventory.get(2);
        if (lv3.isEmpty()) {
            inventory.set(2, lv2.copy());
        } else if (ItemStack.areItemsAndComponentsEqual(lv3, lv2)) {
            lv3.increment(1);
        }
        if (lv.isOf(Blocks.WET_SPONGE.asItem()) && !inventory.get(1).isEmpty() && inventory.get(1).isOf(Items.BUCKET)) {
            inventory.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        lv.decrement(1);
        return true;
    }

    protected int getFuelTime(FuelRegistry fuelRegistry, ItemStack stack) {
        return fuelRegistry.getFuelTicks(stack);
    }

    private static int getCookTime(ServerWorld world, AbstractFurnaceBlockEntity furnace) {
        SingleStackRecipeInput lv = new SingleStackRecipeInput(furnace.getStack(0));
        return furnace.matchGetter.getFirstMatch(lv, world).map(recipe -> ((AbstractCookingRecipe)recipe.value()).getCookingTime()).orElse(200);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 1) {
            return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET);
        }
        return true;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        World world;
        ItemStack lv = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(lv, stack);
        this.inventory.set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
        if (slot == 0 && !bl && (world = this.world) instanceof ServerWorld) {
            ServerWorld lv2 = (ServerWorld)world;
            this.cookingTotalTime = AbstractFurnaceBlockEntity.getCookTime(lv2, this);
            this.cookingTimeSpent = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 2) {
            return false;
        }
        if (slot == 1) {
            ItemStack lv = this.inventory.get(1);
            return this.world.getFuelRegistry().isFuel(stack) || stack.isOf(Items.BUCKET) && !lv.isOf(Items.BUCKET);
        }
        return true;
    }

    @Override
    public void setLastRecipe(@Nullable RecipeEntry<?> recipe) {
        if (recipe != null) {
            RegistryKey<Recipe<?>> lv = recipe.id();
            this.recipesUsed.addTo(lv, 1);
        }
    }

    @Override
    @Nullable
    public RecipeEntry<?> getLastRecipe() {
        return null;
    }

    @Override
    public void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {
    }

    public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
        List<RecipeEntry<?>> list = this.getRecipesUsedAndDropExperience(player.getServerWorld(), player.getPos());
        player.unlockRecipes((Collection<RecipeEntry<?>>)list);
        for (RecipeEntry<?> lv : list) {
            if (lv == null) continue;
            player.onRecipeCrafted(lv, this.inventory);
        }
        this.recipesUsed.clear();
    }

    public List<RecipeEntry<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        ArrayList<RecipeEntry<?>> list = Lists.newArrayList();
        for (Reference2IntMap.Entry entry : this.recipesUsed.reference2IntEntrySet()) {
            world.getRecipeManager().get((RegistryKey)entry.getKey()).ifPresent(recipe -> {
                list.add((RecipeEntry<?>)recipe);
                AbstractFurnaceBlockEntity.dropExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe.value()).getExperience());
            });
        }
        return list;
    }

    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
        int j = MathHelper.floor((float)multiplier * experience);
        float g = MathHelper.fractionalPart((float)multiplier * experience);
        if (g != 0.0f && Math.random() < (double)g) {
            ++j;
        }
        ExperienceOrbEntity.spawn(world, pos, j);
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack lv : this.inventory) {
            finder.addInput(lv);
        }
    }
}

