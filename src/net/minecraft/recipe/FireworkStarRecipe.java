/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class FireworkStarRecipe
extends SpecialCraftingRecipe {
    private static final Map<Item, FireworkExplosionComponent.Type> TYPE_MODIFIER_MAP = Map.of(Items.FIRE_CHARGE, FireworkExplosionComponent.Type.LARGE_BALL, Items.FEATHER, FireworkExplosionComponent.Type.BURST, Items.GOLD_NUGGET, FireworkExplosionComponent.Type.STAR, Items.SKELETON_SKULL, FireworkExplosionComponent.Type.CREEPER, Items.WITHER_SKELETON_SKULL, FireworkExplosionComponent.Type.CREEPER, Items.CREEPER_HEAD, FireworkExplosionComponent.Type.CREEPER, Items.PLAYER_HEAD, FireworkExplosionComponent.Type.CREEPER, Items.DRAGON_HEAD, FireworkExplosionComponent.Type.CREEPER, Items.ZOMBIE_HEAD, FireworkExplosionComponent.Type.CREEPER, Items.PIGLIN_HEAD, FireworkExplosionComponent.Type.CREEPER);
    private static final Ingredient TRAIL_MODIFIER = Ingredient.ofItem(Items.DIAMOND);
    private static final Ingredient FLICKER_MODIFIER = Ingredient.ofItem(Items.GLOWSTONE_DUST);
    private static final Ingredient GUNPOWDER = Ingredient.ofItem(Items.GUNPOWDER);

    public FireworkStarRecipe(CraftingRecipeCategory arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingRecipeInput arg, World arg2) {
        if (arg.getStackCount() < 2) {
            return false;
        }
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStackInSlot(i);
            if (lv.isEmpty()) continue;
            if (TYPE_MODIFIER_MAP.containsKey(lv.getItem())) {
                if (bl3) {
                    return false;
                }
                bl3 = true;
                continue;
            }
            if (FLICKER_MODIFIER.test(lv)) {
                if (bl5) {
                    return false;
                }
                bl5 = true;
                continue;
            }
            if (TRAIL_MODIFIER.test(lv)) {
                if (bl4) {
                    return false;
                }
                bl4 = true;
                continue;
            }
            if (GUNPOWDER.test(lv)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (lv.getItem() instanceof DyeItem) {
                bl2 = true;
                continue;
            }
            return false;
        }
        return bl && bl2;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        FireworkExplosionComponent.Type lv = FireworkExplosionComponent.Type.SMALL_BALL;
        boolean bl = false;
        boolean bl2 = false;
        IntArrayList intList = new IntArrayList();
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStackInSlot(i);
            if (lv2.isEmpty()) continue;
            FireworkExplosionComponent.Type lv3 = TYPE_MODIFIER_MAP.get(lv2.getItem());
            if (lv3 != null) {
                lv = lv3;
                continue;
            }
            if (FLICKER_MODIFIER.test(lv2)) {
                bl = true;
                continue;
            }
            if (TRAIL_MODIFIER.test(lv2)) {
                bl2 = true;
                continue;
            }
            Item item = lv2.getItem();
            if (!(item instanceof DyeItem)) continue;
            DyeItem lv4 = (DyeItem)item;
            intList.add(lv4.getColor().getFireworkColor());
        }
        ItemStack lv5 = new ItemStack(Items.FIREWORK_STAR);
        lv5.set(DataComponentTypes.FIREWORK_EXPLOSION, new FireworkExplosionComponent(lv, intList, IntList.of(), bl2, bl));
        return lv5;
    }

    @Override
    public RecipeSerializer<FireworkStarRecipe> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
}

