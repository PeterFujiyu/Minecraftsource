/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeAlternativesWidget
implements Drawable,
Element {
    private static final Identifier OVERLAY_RECIPE_TEXTURE = Identifier.ofVanilla("recipe_book/overlay_recipe");
    private static final int field_32406 = 4;
    private static final int field_32407 = 5;
    private static final float field_33739 = 0.375f;
    public static final int field_42162 = 25;
    private final List<AlternativeButtonWidget> alternativeButtons = Lists.newArrayList();
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private RecipeResultCollection resultCollection = RecipeResultCollection.EMPTY;
    @Nullable
    private NetworkRecipeId lastClickedRecipe;
    final CurrentIndexProvider currentIndexProvider;
    private final boolean furnace;

    public RecipeAlternativesWidget(CurrentIndexProvider currentIndexProvider, boolean furnace) {
        this.currentIndexProvider = currentIndexProvider;
        this.furnace = furnace;
    }

    public void showAlternativesForResult(RecipeResultCollection resultCollection, ContextParameterMap context, boolean filteringCraftable, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
        float t;
        float s;
        float r;
        float q;
        float h;
        this.resultCollection = resultCollection;
        List<RecipeDisplayEntry> list = resultCollection.filter(RecipeResultCollection.RecipeFilterMode.CRAFTABLE);
        List list2 = filteringCraftable ? Collections.emptyList() : resultCollection.filter(RecipeResultCollection.RecipeFilterMode.NOT_CRAFTABLE);
        int m = list.size();
        int n = m + list2.size();
        int o = n <= 16 ? 4 : 5;
        int p = (int)Math.ceil((float)n / (float)o);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        float g = this.buttonX + Math.min(n, o) * 25;
        if (g > (h = (float)(areaCenterX + 50))) {
            this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((g - h) / delta)));
        }
        if ((q = (float)(this.buttonY + p * 25)) > (r = (float)(areaCenterY + 50))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((q - r) / delta));
        }
        if ((s = (float)this.buttonY) < (t = (float)(areaCenterY - 100))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((s - t) / delta));
        }
        this.visible = true;
        this.alternativeButtons.clear();
        for (int u = 0; u < n; ++u) {
            boolean bl2 = u < m;
            RecipeDisplayEntry lv = bl2 ? list.get(u) : (RecipeDisplayEntry)list2.get(u - m);
            int v = this.buttonX + 4 + 25 * (u % o);
            int w = this.buttonY + 5 + 25 * (u / o);
            if (this.furnace) {
                this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(this, v, w, lv.id(), lv.display(), context, bl2));
                continue;
            }
            this.alternativeButtons.add(new CraftingAlternativeButtonWidget(this, v, w, lv.id(), lv.display(), context, bl2));
        }
        this.lastClickedRecipe = null;
    }

    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }

    @Nullable
    public NetworkRecipeId getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (AlternativeButtonWidget lv : this.alternativeButtons) {
            if (!lv.mouseClicked(mouseX, mouseY, button)) continue;
            this.lastClickedRecipe = lv.recipeId;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 1000.0f);
        int k = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int l = Math.min(this.alternativeButtons.size(), k);
        int m = MathHelper.ceil((float)this.alternativeButtons.size() / (float)k);
        int n = 4;
        context.drawGuiTexture(RenderLayer::getGuiTextured, OVERLAY_RECIPE_TEXTURE, this.buttonX, this.buttonY, l * 25 + 8, m * 25 + 8);
        for (AlternativeButtonWidget lv : this.alternativeButtons) {
            lv.render(context, mouseX, mouseY, delta);
        }
        context.getMatrices().pop();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    class FurnaceAlternativeButtonWidget
    extends AlternativeButtonWidget {
        private static final Identifier FURNACE_OVERLAY = Identifier.ofVanilla("recipe_book/furnace_overlay");
        private static final Identifier FURNACE_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_highlighted");
        private static final Identifier FURNACE_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled");
        private static final Identifier FURNACE_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled_highlighted");

        public FurnaceAlternativeButtonWidget(RecipeAlternativesWidget arg, int x, int y, NetworkRecipeId recipeId, RecipeDisplay display, ContextParameterMap context, boolean craftable) {
            super(x, y, recipeId, craftable, FurnaceAlternativeButtonWidget.alignRecipe(display, context));
        }

        private static List<AlternativeButtonWidget.InputSlot> alignRecipe(RecipeDisplay display, ContextParameterMap context) {
            FurnaceRecipeDisplay lv;
            List<ItemStack> list;
            if (display instanceof FurnaceRecipeDisplay && !(list = (lv = (FurnaceRecipeDisplay)display).ingredient().getStacks(context)).isEmpty()) {
                return List.of(FurnaceAlternativeButtonWidget.slot(1, 1, list));
            }
            return List.of();
        }

        @Override
        protected Identifier getOverlayTexture(boolean enabled) {
            if (enabled) {
                return this.isSelected() ? FURNACE_OVERLAY_HIGHLIGHTED : FURNACE_OVERLAY;
            }
            return this.isSelected() ? FURNACE_OVERLAY_DISABLED_HIGHLIGHTED : FURNACE_OVERLAY_DISABLED;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CraftingAlternativeButtonWidget
    extends AlternativeButtonWidget {
        private static final Identifier CRAFTING_OVERLAY = Identifier.ofVanilla("recipe_book/crafting_overlay");
        private static final Identifier CRAFTING_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_highlighted");
        private static final Identifier CRAFTING_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled");
        private static final Identifier CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled_highlighted");
        private static final int field_54828 = 3;
        private static final int field_54829 = 3;

        public CraftingAlternativeButtonWidget(RecipeAlternativesWidget arg, int x, int y, NetworkRecipeId recipeId, RecipeDisplay display, ContextParameterMap context, boolean craftable) {
            super(x, y, recipeId, craftable, CraftingAlternativeButtonWidget.collectInputSlots(display, context));
        }

        private static List<AlternativeButtonWidget.InputSlot> collectInputSlots(RecipeDisplay display, ContextParameterMap context) {
            ArrayList<AlternativeButtonWidget.InputSlot> list = new ArrayList<AlternativeButtonWidget.InputSlot>();
            RecipeDisplay recipeDisplay = display;
            Objects.requireNonNull(recipeDisplay);
            RecipeDisplay recipeDisplay2 = recipeDisplay;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, n)) {
                case 0: {
                    ShapedCraftingRecipeDisplay lv = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                    RecipeGridAligner.alignRecipeToGrid(3, 3, lv.width(), lv.height(), lv.ingredients(), (slot, index, x, y) -> {
                        List<ItemStack> list2 = slot.getStacks(context);
                        if (!list2.isEmpty()) {
                            list.add(CraftingAlternativeButtonWidget.slot(x, y, list2));
                        }
                    });
                    break;
                }
                case 1: {
                    ShapelessCraftingRecipeDisplay lv2 = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                    List<SlotDisplay> list2 = lv2.ingredients();
                    for (int i = 0; i < list2.size(); ++i) {
                        List<ItemStack> list3 = list2.get(i).getStacks(context);
                        if (list3.isEmpty()) continue;
                        list.add(CraftingAlternativeButtonWidget.slot(i % 3, i / 3, list3));
                    }
                    break;
                }
            }
            return list;
        }

        @Override
        protected Identifier getOverlayTexture(boolean enabled) {
            if (enabled) {
                return this.isSelected() ? CRAFTING_OVERLAY_HIGHLIGHTED : CRAFTING_OVERLAY;
            }
            return this.isSelected() ? CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED : CRAFTING_OVERLAY_DISABLED;
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class AlternativeButtonWidget
    extends ClickableWidget {
        final NetworkRecipeId recipeId;
        private final boolean craftable;
        private final List<InputSlot> inputSlots;

        public AlternativeButtonWidget(int x, int y, NetworkRecipeId recipeId, boolean craftable, List<InputSlot> inputSlots) {
            super(x, y, 24, 24, ScreenTexts.EMPTY);
            this.inputSlots = inputSlots;
            this.recipeId = recipeId;
            this.craftable = craftable;
        }

        protected static InputSlot slot(int x, int y, List<ItemStack> stacks) {
            return new InputSlot(3 + x * 7, 3 + y * 7, stacks);
        }

        protected abstract Identifier getOverlayTexture(boolean var1);

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, this.getOverlayTexture(this.craftable), this.getX(), this.getY(), this.width, this.height);
            float g = this.getX() + 2;
            float h = this.getY() + 2;
            float k = 150.0f;
            for (InputSlot lv : this.inputSlots) {
                context.getMatrices().push();
                context.getMatrices().translate(g + (float)lv.y, h + (float)lv.x, 150.0f);
                context.getMatrices().scale(0.375f, 0.375f, 1.0f);
                context.getMatrices().translate(-8.0f, -8.0f, 0.0f);
                context.drawItem(lv.get(RecipeAlternativesWidget.this.currentIndexProvider.currentIndex()), 0, 0);
                context.getMatrices().pop();
            }
        }

        @Environment(value=EnvType.CLIENT)
        protected record InputSlot(int y, int x, List<ItemStack> stacks) {
            public InputSlot {
                if (list.isEmpty()) {
                    throw new IllegalArgumentException("Ingredient list must be non-empty");
                }
            }

            public ItemStack get(int index) {
                return this.stacks.get(index % this.stacks.size());
            }
        }
    }
}

