/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.context.ContextParameterMap;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GhostRecipe {
    private final Reference2ObjectMap<Slot, CyclingItem> items = new Reference2ObjectArrayMap<Slot, CyclingItem>();
    private final CurrentIndexProvider currentIndexProvider;

    public GhostRecipe(CurrentIndexProvider currentIndexProvider) {
        this.currentIndexProvider = currentIndexProvider;
    }

    public void clear() {
        this.items.clear();
    }

    private void addItems(Slot slot, ContextParameterMap context, SlotDisplay display, boolean resultSlot) {
        List<ItemStack> list = display.getStacks(context);
        if (!list.isEmpty()) {
            this.items.put(slot, new CyclingItem(list, resultSlot));
        }
    }

    protected void addInputs(Slot slot, ContextParameterMap context, SlotDisplay display) {
        this.addItems(slot, context, display, false);
    }

    protected void addResults(Slot slot, ContextParameterMap context, SlotDisplay display) {
        this.addItems(slot, context, display, true);
    }

    public void draw(DrawContext context, MinecraftClient client, boolean resultHasPadding) {
        this.items.forEach((slot, item) -> {
            int i = slot.x;
            int j = slot.y;
            if (item.isResultSlot && resultHasPadding) {
                context.fill(i - 4, j - 4, i + 20, j + 20, 0x30FF0000);
            } else {
                context.fill(i, j, i + 16, j + 16, 0x30FF0000);
            }
            ItemStack lv = item.get(this.currentIndexProvider.currentIndex());
            context.drawItemWithoutEntity(lv, i, j);
            context.fill(RenderLayer.getGuiGhostRecipeOverlay(), i, j, i + 16, j + 16, 0x30FFFFFF);
            if (item.isResultSlot) {
                context.drawStackOverlay(arg2.textRenderer, lv, i, j);
            }
        });
    }

    public void drawTooltip(DrawContext context, MinecraftClient client, int x, int y, @Nullable Slot slot) {
        if (slot == null) {
            return;
        }
        CyclingItem lv = (CyclingItem)this.items.get(slot);
        if (lv != null) {
            ItemStack lv2 = lv.get(this.currentIndexProvider.currentIndex());
            context.drawTooltip(client.textRenderer, Screen.getTooltipFromItem(client, lv2), x, y, lv2.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }

    @Environment(value=EnvType.CLIENT)
    record CyclingItem(List<ItemStack> items, boolean isResultSlot) {
        public ItemStack get(int index) {
            int j = this.items.size();
            if (j == 0) {
                return ItemStack.EMPTY;
            }
            return this.items.get(index % j);
        }
    }
}

