/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;

public abstract class AbstractRecipeScreenHandler
extends ScreenHandler {
    public AbstractRecipeScreenHandler(ScreenHandlerType<?> arg, int i) {
        super(arg, i);
    }

    public abstract PostFillAction fillInputSlots(boolean var1, boolean var2, RecipeEntry<?> var3, ServerWorld var4, PlayerInventory var5);

    public abstract void populateRecipeFinder(RecipeFinder var1);

    public abstract RecipeBookType getCategory();

    public static enum PostFillAction {
        NOTHING,
        PLACE_GHOST_RECIPE;

    }
}

