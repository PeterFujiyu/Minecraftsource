/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.slf4j.Logger;

public class FurnaceSmeltLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<FurnaceSmeltLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> FurnaceSmeltLootFunction.addConditionsField(instance).apply(instance, FurnaceSmeltLootFunction::new));

    private FurnaceSmeltLootFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    public LootFunctionType<FurnaceSmeltLootFunction> getType() {
        return LootFunctionTypes.FURNACE_SMELT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ItemStack lv2;
        if (stack.isEmpty()) {
            return stack;
        }
        SingleStackRecipeInput lv = new SingleStackRecipeInput(stack);
        Optional<RecipeEntry<SmeltingRecipe>> optional = context.getWorld().getRecipeManager().getFirstMatch(RecipeType.SMELTING, lv, context.getWorld());
        if (optional.isPresent() && !(lv2 = optional.get().value().craft(lv, (RegistryWrapper.WrapperLookup)context.getWorld().getRegistryManager())).isEmpty()) {
            return lv2.copyWithCount(stack.getCount());
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)stack);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return FurnaceSmeltLootFunction.builder(FurnaceSmeltLootFunction::new);
    }
}

