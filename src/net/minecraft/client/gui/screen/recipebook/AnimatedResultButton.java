/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends ClickableWidget {
    private static final Identifier SLOT_MANY_CRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_many_craftable");
    private static final Identifier SLOT_CRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_craftable");
    private static final Identifier SLOT_MANY_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_many_uncraftable");
    private static final Identifier SLOT_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_uncraftable");
    private static final float field_32414 = 15.0f;
    private static final int field_32415 = 25;
    private static final Text MORE_RECIPES_TEXT = Text.translatable("gui.recipebook.moreRecipes");
    private RecipeResultCollection resultCollection = RecipeResultCollection.EMPTY;
    private List<Result> results = List.of();
    private boolean allResultsEqual;
    private final CurrentIndexProvider currentIndexProvider;
    private float bounce;

    public AnimatedResultButton(CurrentIndexProvider currentIndexProvider) {
        super(0, 0, 25, 25, ScreenTexts.EMPTY);
        this.currentIndexProvider = currentIndexProvider;
    }

    public void showResultCollection(RecipeResultCollection resultCollection, boolean filteringCraftable, RecipeBookResults results, ContextParameterMap context) {
        this.resultCollection = resultCollection;
        List<RecipeDisplayEntry> list = resultCollection.filter(filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY);
        this.results = list.stream().map(entry -> new Result(entry.id(), entry.getStacks(context))).toList();
        this.allResultsEqual = AnimatedResultButton.areAllResultsEqual(this.results);
        List<NetworkRecipeId> list2 = list.stream().map(RecipeDisplayEntry::id).filter(results.getRecipeBook()::isHighlighted).toList();
        if (!list2.isEmpty()) {
            list2.forEach(results::onRecipeDisplayed);
            this.bounce = 15.0f;
        }
    }

    private static boolean areAllResultsEqual(List<Result> results) {
        Iterator iterator = results.stream().flatMap(result -> result.displayItems().stream()).iterator();
        if (!iterator.hasNext()) {
            return true;
        }
        ItemStack lv = (ItemStack)iterator.next();
        while (iterator.hasNext()) {
            ItemStack lv2 = (ItemStack)iterator.next();
            if (ItemStack.areItemsAndComponentsEqual(lv, lv2)) continue;
            return false;
        }
        return true;
    }

    public RecipeResultCollection getResultCollection() {
        return this.resultCollection;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean bl;
        Identifier lv = this.resultCollection.hasCraftableRecipes() ? (this.hasMultipleResults() ? SLOT_MANY_CRAFTABLE_TEXTURE : SLOT_CRAFTABLE_TEXTURE) : (this.hasMultipleResults() ? SLOT_MANY_UNCRAFTABLE_TEXTURE : SLOT_UNCRAFTABLE_TEXTURE);
        boolean bl2 = bl = this.bounce > 0.0f;
        if (bl) {
            float g = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            context.getMatrices().push();
            context.getMatrices().translate(this.getX() + 8, this.getY() + 12, 0.0f);
            context.getMatrices().scale(g, g, 1.0f);
            context.getMatrices().translate(-(this.getX() + 8), -(this.getY() + 12), 0.0f);
            this.bounce -= delta;
        }
        context.drawGuiTexture(RenderLayer::getGuiTextured, lv, this.getX(), this.getY(), this.width, this.height);
        ItemStack lv2 = this.getDisplayStack();
        int k = 4;
        if (this.hasMultipleResults() && this.allResultsEqual) {
            context.drawItem(lv2, this.getX() + k + 1, this.getY() + k + 1, 0, 10);
            --k;
        }
        context.drawItemWithoutEntity(lv2, this.getX() + k, this.getY() + k);
        if (bl) {
            context.getMatrices().pop();
        }
    }

    private boolean hasMultipleResults() {
        return this.results.size() > 1;
    }

    public boolean hasSingleResult() {
        return this.results.size() == 1;
    }

    public NetworkRecipeId getCurrentId() {
        int i = this.currentIndexProvider.currentIndex() % this.results.size();
        return this.results.get((int)i).id;
    }

    public ItemStack getDisplayStack() {
        int i = this.currentIndexProvider.currentIndex();
        int j = this.results.size();
        int k = i / j;
        int l = i - j * k;
        return this.results.get(l).getDisplayStack(k);
    }

    public List<Text> getTooltip(ItemStack stack) {
        ArrayList<Text> list = new ArrayList<Text>(Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack));
        if (this.hasMultipleResults()) {
            list.add(MORE_RECIPES_TEXT);
        }
        return list;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)Text.translatable("narration.recipe", this.getDisplayStack().getName()));
        if (this.hasMultipleResults()) {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"), Text.translatable("narration.recipe.usage.more"));
        } else {
            builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.button.usage.hovered"));
        }
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 || button == 1;
    }

    @Environment(value=EnvType.CLIENT)
    record Result(NetworkRecipeId id, List<ItemStack> displayItems) {
        public ItemStack getDisplayStack(int currentIndex) {
            if (this.displayItems.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int j = currentIndex % this.displayItems.size();
            return this.displayItems.get(j);
        }
    }
}

