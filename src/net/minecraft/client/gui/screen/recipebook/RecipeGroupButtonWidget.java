/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends ToggleButtonWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/tab"), Identifier.ofVanilla("recipe_book/tab_selected"));
    private final RecipeBookWidget.Tab tab;
    private static final float field_32412 = 15.0f;
    private float bounce;

    public RecipeGroupButtonWidget(RecipeBookWidget.Tab tab) {
        super(0, 0, 35, 27, false);
        this.tab = tab;
        this.setTextures(TEXTURES);
    }

    public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
        RecipeResultCollection.RecipeFilterMode lv = filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY;
        List<RecipeResultCollection> list = recipeBook.getResultsForCategory(this.tab.category());
        for (RecipeResultCollection lv2 : list) {
            for (RecipeDisplayEntry lv3 : lv2.filter(lv)) {
                if (!recipeBook.isHighlighted(lv3.id())) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.textures == null) {
            return;
        }
        if (this.bounce > 0.0f) {
            float g = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            context.getMatrices().push();
            context.getMatrices().translate(this.getX() + 8, this.getY() + 12, 0.0f);
            context.getMatrices().scale(1.0f, g, 1.0f);
            context.getMatrices().translate(-(this.getX() + 8), -(this.getY() + 12), 0.0f);
        }
        Identifier lv = this.textures.get(true, this.toggled);
        int k = this.getX();
        if (this.toggled) {
            k -= 2;
        }
        context.drawGuiTexture(RenderLayer::getGuiTextured, lv, k, this.getY(), this.width, this.height);
        this.renderIcons(context);
        if (this.bounce > 0.0f) {
            context.getMatrices().pop();
            this.bounce -= delta;
        }
    }

    private void renderIcons(DrawContext context) {
        int i;
        int n = i = this.toggled ? -2 : 0;
        if (this.tab.secondaryIcon().isPresent()) {
            context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 3 + i, this.getY() + 5);
            context.drawItemWithoutEntity(this.tab.secondaryIcon().get(), this.getX() + 14 + i, this.getY() + 5);
        } else {
            context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 9 + i, this.getY() + 5);
        }
    }

    public RecipeBookGroup getCategory() {
        return this.tab.category();
    }

    public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
        List<RecipeResultCollection> list = recipeBook.getResultsForCategory(this.tab.category());
        this.visible = false;
        for (RecipeResultCollection lv : list) {
            if (!lv.hasDisplayableRecipes()) continue;
            this.visible = true;
            break;
        }
        return this.visible;
    }
}

