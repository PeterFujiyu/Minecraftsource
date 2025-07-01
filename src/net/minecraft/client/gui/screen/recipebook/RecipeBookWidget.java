/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class RecipeBookWidget<T extends AbstractRecipeScreenHandler>
implements Drawable,
Element,
Selectable {
    public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/button"), Identifier.ofVanilla("recipe_book/button_highlighted"));
    protected static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/recipe_book.png");
    private static final int field_52839 = 256;
    private static final int field_52840 = 256;
    private static final Text SEARCH_HINT_TEXT = Text.translatable("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    public static final int field_32408 = 147;
    public static final int field_32409 = 166;
    private static final int field_32410 = 86;
    private static final int field_54389 = 8;
    private static final Text TOGGLE_ALL_RECIPES_TEXT = Text.translatable("gui.recipebook.toggleRecipes.all");
    private static final int field_52841 = 30;
    private int leftOffset;
    private int parentWidth;
    private int parentHeight;
    private float displayTime;
    @Nullable
    private NetworkRecipeId selectedRecipeId;
    private final GhostRecipe ghostRecipe;
    private final List<RecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
    @Nullable
    private RecipeGroupButtonWidget currentTab;
    protected ToggleButtonWidget toggleCraftableButton;
    protected final T craftingScreenHandler;
    protected MinecraftClient client;
    @Nullable
    private TextFieldWidget searchField;
    private String searchText = "";
    private final List<Tab> tabs;
    private ClientRecipeBook recipeBook;
    private final RecipeBookResults recipesArea;
    @Nullable
    private NetworkRecipeId selectedRecipe;
    @Nullable
    private RecipeResultCollection selectedRecipeResults;
    private final RecipeFinder recipeFinder = new RecipeFinder();
    private int cachedInvChangeCount;
    private boolean searching;
    private boolean open;
    private boolean narrow;
    @Nullable
    private ScreenRect searchFieldRect;

    public RecipeBookWidget(T craftingScreenHandler, List<Tab> tabs) {
        this.craftingScreenHandler = craftingScreenHandler;
        this.tabs = tabs;
        CurrentIndexProvider lv = () -> MathHelper.floor(this.displayTime / 30.0f);
        this.ghostRecipe = new GhostRecipe(lv);
        this.recipesArea = new RecipeBookResults(this, lv, craftingScreenHandler instanceof AbstractFurnaceScreenHandler);
    }

    public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow) {
        this.client = client;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.narrow = narrow;
        this.recipeBook = client.player.getRecipeBook();
        this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
        this.open = this.isGuiOpen();
        if (this.open) {
            this.reset();
        }
    }

    private void reset() {
        boolean bl = this.isFilteringCraftable();
        this.leftOffset = this.narrow ? 0 : 86;
        int i = this.getLeft();
        int j = this.getTop();
        this.recipeFinder.clear();
        this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
        ((AbstractRecipeScreenHandler)this.craftingScreenHandler).populateRecipeFinder(this.recipeFinder);
        String string = this.searchField != null ? this.searchField.getText() : "";
        this.searchField = new TextFieldWidget(this.client.textRenderer, i + 25, j + 13, 81, this.client.textRenderer.fontHeight + 5, Text.translatable("itemGroup.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setVisible(true);
        this.searchField.setEditableColor(0xFFFFFF);
        this.searchField.setText(string);
        this.searchField.setPlaceholder(SEARCH_HINT_TEXT);
        this.searchFieldRect = ScreenRect.of(NavigationAxis.HORIZONTAL, i + 8, this.searchField.getY(), this.searchField.getX() - this.getLeft(), this.searchField.getHeight());
        this.recipesArea.initialize(this.client, i, j);
        this.toggleCraftableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, bl);
        this.updateTooltip();
        this.setBookButtonTexture();
        this.tabButtons.clear();
        for (Tab lv : this.tabs) {
            this.tabButtons.add(new RecipeGroupButtonWidget(lv));
        }
        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter(button -> button.getCategory().equals(this.currentTab.getCategory())).findFirst().orElse(null);
        }
        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }
        this.currentTab.setToggled(true);
        this.populateAllRecipes();
        this.refreshTabButtons(bl);
        this.refreshResults(false, bl);
    }

    private int getTop() {
        return (this.parentHeight - 166) / 2;
    }

    private int getLeft() {
        return (this.parentWidth - 147) / 2 - this.leftOffset;
    }

    private void updateTooltip() {
        this.toggleCraftableButton.setTooltip(this.toggleCraftableButton.isToggled() ? Tooltip.of(this.getToggleCraftableButtonText()) : Tooltip.of(TOGGLE_ALL_RECIPES_TEXT));
    }

    protected abstract void setBookButtonTexture();

    public int findLeftEdge(int width, int backgroundWidth) {
        int k = this.isOpen() && !this.narrow ? 177 + (width - backgroundWidth - 200) / 2 : (width - backgroundWidth) / 2;
        return k;
    }

    public void toggleOpen() {
        this.setOpen(!this.isOpen());
    }

    public boolean isOpen() {
        return this.open;
    }

    private boolean isGuiOpen() {
        return this.recipeBook.isGuiOpen(((AbstractRecipeScreenHandler)this.craftingScreenHandler).getCategory());
    }

    protected void setOpen(boolean opened) {
        if (opened) {
            this.reset();
        }
        this.open = opened;
        this.recipeBook.setGuiOpen(((AbstractRecipeScreenHandler)this.craftingScreenHandler).getCategory(), opened);
        if (!opened) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }

    protected abstract boolean isValid(Slot var1);

    public void onMouseClick(@Nullable Slot slot) {
        if (slot != null && this.isValid(slot)) {
            this.selectedRecipeId = null;
            this.ghostRecipe.clear();
            if (this.isOpen()) {
                this.refreshInputs();
            }
        }
    }

    private void populateAllRecipes() {
        for (Tab lv : this.tabs) {
            for (RecipeResultCollection lv2 : this.recipeBook.getResultsForCategory(lv.category())) {
                this.populateRecipes(lv2, this.recipeFinder);
            }
        }
    }

    protected abstract void populateRecipes(RecipeResultCollection var1, RecipeFinder var2);

    private void refreshResults(boolean resetCurrentPage, boolean filteringCraftable) {
        ClientPlayNetworkHandler lv;
        List<RecipeResultCollection> list = this.recipeBook.getResultsForCategory(this.currentTab.getCategory());
        ArrayList<RecipeResultCollection> list2 = Lists.newArrayList(list);
        list2.removeIf(resultCollection -> !resultCollection.hasDisplayableRecipes());
        String string = this.searchField.getText();
        if (!string.isEmpty() && (lv = this.client.getNetworkHandler()) != null) {
            ObjectLinkedOpenHashSet<RecipeResultCollection> objectSet = new ObjectLinkedOpenHashSet<RecipeResultCollection>(lv.getSearchManager().getRecipeOutputReloadFuture().findAll(string.toLowerCase(Locale.ROOT)));
            list2.removeIf(resultCollection -> !objectSet.contains(resultCollection));
        }
        if (filteringCraftable) {
            list2.removeIf(resultCollection -> !resultCollection.hasCraftableRecipes());
        }
        this.recipesArea.setResults(list2, resetCurrentPage, filteringCraftable);
    }

    private void refreshTabButtons(boolean filteringCraftable) {
        int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int j = (this.parentHeight - 166) / 2 + 3;
        int k = 27;
        int l = 0;
        for (RecipeGroupButtonWidget lv : this.tabButtons) {
            RecipeBookGroup lv2 = lv.getCategory();
            if (lv2 instanceof net.minecraft.client.recipebook.RecipeBookType) {
                lv.visible = true;
                lv.setPosition(i, j + 27 * l++);
                continue;
            }
            if (!lv.hasKnownRecipes(this.recipeBook)) continue;
            lv.setPosition(i, j + 27 * l++);
            lv.checkForNewRecipes(this.recipeBook, filteringCraftable);
        }
    }

    public void update() {
        boolean bl = this.isGuiOpen();
        if (this.isOpen() != bl) {
            this.setOpen(bl);
        }
        if (!this.isOpen()) {
            return;
        }
        if (this.cachedInvChangeCount != this.client.player.getInventory().getChangeCount()) {
            this.refreshInputs();
            this.cachedInvChangeCount = this.client.player.getInventory().getChangeCount();
        }
    }

    private void refreshInputs() {
        this.recipeFinder.clear();
        this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
        ((AbstractRecipeScreenHandler)this.craftingScreenHandler).populateRecipeFinder(this.recipeFinder);
        this.populateAllRecipes();
        this.refreshResults(false, this.isFilteringCraftable());
    }

    private boolean isFilteringCraftable() {
        return this.recipeBook.isFilteringCraftable(((AbstractRecipeScreenHandler)this.craftingScreenHandler).getCategory());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isOpen()) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.displayTime += delta;
        }
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 100.0f);
        int k = this.getLeft();
        int l = this.getTop();
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, k, l, 1.0f, 1.0f, 147, 166, 256, 256);
        this.searchField.render(context, mouseX, mouseY, delta);
        for (RecipeGroupButtonWidget lv : this.tabButtons) {
            lv.render(context, mouseX, mouseY, delta);
        }
        this.toggleCraftableButton.render(context, mouseX, mouseY, delta);
        this.recipesArea.draw(context, k, l, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    public void drawTooltip(DrawContext context, int x, int y, @Nullable Slot slot) {
        if (!this.isOpen()) {
            return;
        }
        this.recipesArea.drawTooltip(context, x, y);
        this.ghostRecipe.drawTooltip(context, this.client, x, y, slot);
    }

    protected abstract Text getToggleCraftableButtonText();

    public void drawGhostSlots(DrawContext context, boolean resultHasPadding) {
        this.ghostRecipe.draw(context, this.client, resultHasPadding);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl;
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (this.recipesArea.mouseClicked(mouseX, mouseY, button, this.getLeft(), this.getTop(), 147, 166)) {
            NetworkRecipeId lv = this.recipesArea.getLastClickedRecipe();
            RecipeResultCollection lv2 = this.recipesArea.getLastClickedResults();
            if (lv != null && lv2 != null) {
                if (!this.select(lv2, lv)) {
                    return false;
                }
                this.selectedRecipeResults = lv2;
                this.selectedRecipe = lv;
                if (!this.isWide()) {
                    this.setOpen(false);
                }
            }
            return true;
        }
        if (this.searchField != null) {
            boolean bl2 = bl = this.searchFieldRect != null && this.searchFieldRect.contains(MathHelper.floor(mouseX), MathHelper.floor(mouseY));
            if (bl || this.searchField.mouseClicked(mouseX, mouseY, button)) {
                this.searchField.setFocused(true);
                return true;
            }
            this.searchField.setFocused(false);
        }
        if (this.toggleCraftableButton.mouseClicked(mouseX, mouseY, button)) {
            bl = this.toggleFilteringCraftable();
            this.toggleCraftableButton.setToggled(bl);
            this.updateTooltip();
            this.sendBookDataPacket();
            this.refreshResults(false, bl);
            return true;
        }
        for (RecipeGroupButtonWidget lv3 : this.tabButtons) {
            if (!lv3.mouseClicked(mouseX, mouseY, button)) continue;
            if (this.currentTab != lv3) {
                if (this.currentTab != null) {
                    this.currentTab.setToggled(false);
                }
                this.currentTab = lv3;
                this.currentTab.setToggled(true);
                this.refreshResults(true, this.isFilteringCraftable());
            }
            return true;
        }
        return false;
    }

    private boolean select(RecipeResultCollection results, NetworkRecipeId recipeId) {
        if (!results.isCraftable(recipeId) && recipeId.equals(this.selectedRecipeId)) {
            return false;
        }
        this.selectedRecipeId = recipeId;
        this.ghostRecipe.clear();
        this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, recipeId, Screen.hasShiftDown());
        return true;
    }

    private boolean toggleFilteringCraftable() {
        RecipeBookType lv = ((AbstractRecipeScreenHandler)this.craftingScreenHandler).getCategory();
        boolean bl = !this.recipeBook.isFilteringCraftable(lv);
        this.recipeBook.setFilteringCraftable(lv, bl);
        return bl;
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int backgroundWidth, int backgroundHeight, int button) {
        if (!this.isOpen()) {
            return true;
        }
        boolean bl = mouseX < (double)x || mouseY < (double)y || mouseX >= (double)(x + backgroundWidth) || mouseY >= (double)(y + backgroundHeight);
        boolean bl2 = (double)(x - 147) < mouseX && mouseX < (double)x && (double)y < mouseY && mouseY < (double)(y + backgroundHeight);
        return bl && !bl2 && !this.currentTab.isSelected();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.searching = false;
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !this.isWide()) {
            this.setOpen(false);
            return true;
        }
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            this.refreshSearchResults();
            return true;
        }
        if (this.searchField.isFocused() && this.searchField.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        if (this.client.options.chatKey.matchesKey(keyCode, scanCode) && !this.searchField.isFocused()) {
            this.searching = true;
            this.searchField.setFocused(true);
            return true;
        }
        if (KeyCodes.isToggle(keyCode) && this.selectedRecipeResults != null && this.selectedRecipe != null) {
            ClickableWidget.playClickSound(MinecraftClient.getInstance().getSoundManager());
            return this.select(this.selectedRecipeResults, this.selectedRecipe);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.searching = false;
        return Element.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.searching) {
            return false;
        }
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (this.searchField.charTyped(chr, modifiers)) {
            this.refreshSearchResults();
            return true;
        }
        return Element.super.charTyped(chr, modifiers);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    private void refreshSearchResults() {
        String string = this.searchField.getText().toLowerCase(Locale.ROOT);
        this.triggerPirateSpeakEasterEgg(string);
        if (!string.equals(this.searchText)) {
            this.refreshResults(false, this.isFilteringCraftable());
            this.searchText = string;
        }
    }

    private void triggerPirateSpeakEasterEgg(String search) {
        if ("excitedze".equals(search)) {
            LanguageManager lv = this.client.getLanguageManager();
            String string2 = "en_pt";
            LanguageDefinition lv2 = lv.getLanguage("en_pt");
            if (lv2 == null || lv.getLanguage().equals("en_pt")) {
                return;
            }
            lv.setLanguage("en_pt");
            this.client.options.language = "en_pt";
            this.client.reloadResources();
            this.client.options.write();
        }
    }

    private boolean isWide() {
        return this.leftOffset == 86;
    }

    public void refresh() {
        this.populateAllRecipes();
        this.refreshTabButtons(this.isFilteringCraftable());
        if (this.isOpen()) {
            this.refreshResults(false, this.isFilteringCraftable());
        }
    }

    public void onRecipeDisplayed(NetworkRecipeId recipeId) {
        this.client.player.onRecipeDisplayed(recipeId);
    }

    public void onCraftFailed(RecipeDisplay display) {
        this.ghostRecipe.clear();
        ContextParameterMap lv = SlotDisplayContexts.createParameters(Objects.requireNonNull(this.client.world));
        this.showGhostRecipe(this.ghostRecipe, display, lv);
    }

    protected abstract void showGhostRecipe(GhostRecipe var1, RecipeDisplay var2, ContextParameterMap var3);

    protected void sendBookDataPacket() {
        if (this.client.getNetworkHandler() != null) {
            RecipeBookType lv = ((AbstractRecipeScreenHandler)this.craftingScreenHandler).getCategory();
            boolean bl = this.recipeBook.getOptions().isGuiOpen(lv);
            boolean bl2 = this.recipeBook.getOptions().isFilteringCraftable(lv);
            this.client.getNetworkHandler().sendPacket(new RecipeCategoryOptionsC2SPacket(lv, bl, bl2));
        }
    }

    @Override
    public Selectable.SelectionType getType() {
        return this.open ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        ArrayList<ClickableWidget> list = Lists.newArrayList();
        this.recipesArea.forEachButton(button -> {
            if (button.isNarratable()) {
                list.add((ClickableWidget)button);
            }
        });
        list.add(this.searchField);
        list.add(this.toggleCraftableButton);
        list.addAll(this.tabButtons);
        Screen.SelectedElementNarrationData lv = Screen.findSelectedElementData(list, null);
        if (lv != null) {
            lv.selectable.appendNarrations(builder.nextMessage());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Tab(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon, RecipeBookGroup category) {
        public Tab(net.minecraft.client.recipebook.RecipeBookType type) {
            this(new ItemStack(Items.COMPASS), Optional.empty(), type);
        }

        public Tab(Item primaryIcon, RecipeBookCategory category) {
            this(new ItemStack(primaryIcon), Optional.empty(), (RecipeBookGroup)category);
        }

        public Tab(Item primaryIcon, Item secondaryIcon, RecipeBookCategory category) {
            this(new ItemStack(primaryIcon), Optional.of(new ItemStack(secondaryIcon)), (RecipeBookGroup)category);
        }
    }
}

