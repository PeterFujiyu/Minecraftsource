/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(CraftingRecipeCategory category) {
        super("", category, RawShapedRecipe.create(Map.of(Character.valueOf('#'), Ingredient.ofItem(Items.PAPER), Character.valueOf('x'), Ingredient.ofItem(Items.FILLED_MAP)), "###", "#x#", "###"), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingRecipeInput arg, World arg2) {
        if (!super.matches(arg, arg2)) {
            return false;
        }
        ItemStack lv = MapExtendingRecipe.findFilledMap(arg);
        if (lv.isEmpty()) {
            return false;
        }
        MapState lv2 = FilledMapItem.getMapState(lv, arg2);
        if (lv2 == null) {
            return false;
        }
        if (lv2.hasExplorationMapDecoration()) {
            return false;
        }
        return lv2.scale < 4;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        ItemStack lv = MapExtendingRecipe.findFilledMap(arg).copyWithCount(1);
        lv.set(DataComponentTypes.MAP_POST_PROCESSING, MapPostProcessingComponent.SCALE);
        return lv;
    }

    private static ItemStack findFilledMap(CraftingRecipeInput input) {
        for (int i = 0; i < input.size(); ++i) {
            ItemStack lv = input.getStackInSlot(i);
            if (!lv.contains(DataComponentTypes.MAP_ID)) continue;
            return lv;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<MapExtendingRecipe> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}

