/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementWidget {
    private static final Identifier TITLE_BOX_TEXTURE = Identifier.ofVanilla("advancements/title_box");
    private static final int field_32286 = 26;
    private static final int field_32287 = 0;
    private static final int field_32288 = 200;
    private static final int field_32289 = 26;
    private static final int ICON_OFFSET_X = 8;
    private static final int ICON_OFFSET_Y = 5;
    private static final int ICON_SIZE = 26;
    private static final int field_32293 = 3;
    private static final int field_32294 = 5;
    private static final int TITLE_OFFSET_X = 32;
    private static final int TITLE_OFFSET_Y = 9;
    private static final int field_55103 = 8;
    private static final int TITLE_MAX_WIDTH = 163;
    private static final int field_55104 = 80;
    private static final int[] SPLIT_OFFSET_CANDIDATES = new int[]{0, 10, -10, 25, -25};
    private final AdvancementTab tab;
    private final PlacedAdvancement advancement;
    private final AdvancementDisplay display;
    private final List<OrderedText> title;
    private final int width;
    private final List<OrderedText> description;
    private final MinecraftClient client;
    @Nullable
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    @Nullable
    private AdvancementProgress progress;
    private final int x;
    private final int y;

    public AdvancementWidget(AdvancementTab tab, MinecraftClient client, PlacedAdvancement advancement, AdvancementDisplay display) {
        this.tab = tab;
        this.advancement = advancement;
        this.display = display;
        this.client = client;
        this.title = client.textRenderer.wrapLines(display.getTitle(), 163);
        this.x = MathHelper.floor(display.getX() * 28.0f);
        this.y = MathHelper.floor(display.getY() * 27.0f);
        int i = Math.max(this.title.stream().mapToInt(client.textRenderer::getWidth).max().orElse(0), 80);
        int j = this.getProgressWidth();
        int k = 29 + i + j;
        this.description = Language.getInstance().reorder(this.wrapDescription(Texts.setStyleIfAbsent(display.getDescription().copy(), Style.EMPTY.withColor(display.getFrame().getTitleFormat())), k));
        for (OrderedText lv : this.description) {
            k = Math.max(k, client.textRenderer.getWidth(lv));
        }
        this.width = k + 3 + 5;
    }

    private int getProgressWidth() {
        int i = this.advancement.getAdvancement().requirements().getLength();
        if (i <= 1) {
            return 0;
        }
        int j = 8;
        MutableText lv = Text.translatable("advancements.progress", i, i);
        return this.client.textRenderer.getWidth(lv) + 8;
    }

    private static float getMaxWidth(TextHandler textHandler, List<StringVisitable> lines) {
        return (float)lines.stream().mapToDouble(textHandler::getWidth).max().orElse(0.0);
    }

    private List<StringVisitable> wrapDescription(Text text, int width) {
        TextHandler lv = this.client.textRenderer.getTextHandler();
        List<StringVisitable> list = null;
        float f = Float.MAX_VALUE;
        for (int j : SPLIT_OFFSET_CANDIDATES) {
            List<StringVisitable> list2 = lv.wrapLines(text, width - j, Style.EMPTY);
            float g = Math.abs(AdvancementWidget.getMaxWidth(lv, list2) - (float)width);
            if (g <= 10.0f) {
                return list2;
            }
            if (!(g < f)) continue;
            f = g;
            list = list2;
        }
        return list;
    }

    @Nullable
    private AdvancementWidget getParent(PlacedAdvancement advancement) {
        while ((advancement = advancement.getParent()) != null && advancement.getAdvancement().display().isEmpty()) {
        }
        if (advancement == null || advancement.getAdvancement().display().isEmpty()) {
            return null;
        }
        return this.tab.getWidget(advancement.getAdvancementEntry());
    }

    public void renderLines(DrawContext context, int x, int y, boolean border) {
        if (this.parent != null) {
            int p;
            int k = x + this.parent.x + 13;
            int l = x + this.parent.x + 26 + 4;
            int m = y + this.parent.y + 13;
            int n = x + this.x + 13;
            int o = y + this.y + 13;
            int n2 = p = border ? Colors.BLACK : Colors.WHITE;
            if (border) {
                context.drawHorizontalLine(l, k, m - 1, p);
                context.drawHorizontalLine(l + 1, k, m, p);
                context.drawHorizontalLine(l, k, m + 1, p);
                context.drawHorizontalLine(n, l - 1, o - 1, p);
                context.drawHorizontalLine(n, l - 1, o, p);
                context.drawHorizontalLine(n, l - 1, o + 1, p);
                context.drawVerticalLine(l - 1, o, m, p);
                context.drawVerticalLine(l + 1, o, m, p);
            } else {
                context.drawHorizontalLine(l, k, m, p);
                context.drawHorizontalLine(n, l, o, p);
                context.drawVerticalLine(l, o, m, p);
            }
        }
        for (AdvancementWidget lv : this.children) {
            lv.renderLines(context, x, y, border);
        }
    }

    public void renderWidgets(DrawContext context, int x, int y) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus lv = f >= 1.0f ? AdvancementObtainedStatus.OBTAINED : AdvancementObtainedStatus.UNOBTAINED;
            context.drawGuiTexture(RenderLayer::getGuiTextured, lv.getFrameTexture(this.display.getFrame()), x + this.x + 3, y + this.y, 26, 26);
            context.drawItemWithoutEntity(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
        }
        for (AdvancementWidget lv2 : this.children) {
            lv2.renderWidgets(context, x, y);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public void setProgress(AdvancementProgress progress) {
        this.progress = progress;
    }

    public void addChild(AdvancementWidget widget) {
        this.children.add(widget);
    }

    public void drawTooltip(DrawContext context, int originX, int originY, float alpha, int x, int y) {
        AdvancementObtainedStatus lv5;
        AdvancementObtainedStatus lv4;
        AdvancementObtainedStatus lv3;
        TextRenderer lv = this.client.textRenderer;
        int m = lv.fontHeight * this.title.size() + 9 + 8;
        int n = originY + this.y + (26 - m) / 2;
        int o = n + m;
        int p = this.description.size() * lv.fontHeight;
        int q = 6 + p;
        boolean bl = x + originX + this.x + this.width + 26 >= this.tab.getScreen().width;
        Text lv2 = this.progress == null ? null : this.progress.getProgressBarFraction();
        int r = lv2 == null ? 0 : lv.getWidth(lv2);
        boolean bl2 = o + q >= 113;
        float g = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
        int s = MathHelper.floor(g * (float)this.width);
        if (g >= 1.0f) {
            s = this.width / 2;
            lv3 = AdvancementObtainedStatus.OBTAINED;
            lv4 = AdvancementObtainedStatus.OBTAINED;
            lv5 = AdvancementObtainedStatus.OBTAINED;
        } else if (s < 2) {
            s = this.width / 2;
            lv3 = AdvancementObtainedStatus.UNOBTAINED;
            lv4 = AdvancementObtainedStatus.UNOBTAINED;
            lv5 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (s > this.width - 2) {
            s = this.width / 2;
            lv3 = AdvancementObtainedStatus.OBTAINED;
            lv4 = AdvancementObtainedStatus.OBTAINED;
            lv5 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            lv3 = AdvancementObtainedStatus.OBTAINED;
            lv4 = AdvancementObtainedStatus.UNOBTAINED;
            lv5 = AdvancementObtainedStatus.UNOBTAINED;
        }
        int t = this.width - s;
        int u = bl ? originX + this.x - this.width + 26 + 6 : originX + this.x;
        int v = m + q;
        if (!this.description.isEmpty()) {
            if (bl2) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, TITLE_BOX_TEXTURE, u, o - v, this.width, v);
            } else {
                context.drawGuiTexture(RenderLayer::getGuiTextured, TITLE_BOX_TEXTURE, u, n, this.width, v);
            }
        }
        if (lv3 != lv4) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, lv3.getBoxTexture(), 200, m, 0, 0, u, n, s, m);
            context.drawGuiTexture(RenderLayer::getGuiTextured, lv4.getBoxTexture(), 200, m, 200 - t, 0, u + s, n, t, m);
        } else {
            context.drawGuiTexture(RenderLayer::getGuiTextured, lv3.getBoxTexture(), u, n, this.width, m);
        }
        context.drawGuiTexture(RenderLayer::getGuiTextured, lv5.getFrameTexture(this.display.getFrame()), originX + this.x + 3, originY + this.y, 26, 26);
        int w = u + 5;
        if (bl) {
            this.drawText(context, this.title, w, n + 9, -1);
            if (lv2 != null) {
                context.drawTextWithShadow(lv, lv2, originX + this.x - r, n + 9, Colors.WHITE);
            }
        } else {
            this.drawText(context, this.title, originX + this.x + 32, n + 9, -1);
            if (lv2 != null) {
                context.drawTextWithShadow(lv, lv2, originX + this.x + this.width - r - 5, n + 9, Colors.WHITE);
            }
        }
        if (bl2) {
            this.drawText(context, this.description, w, n - p + 1, -16711936);
        } else {
            this.drawText(context, this.description, w, o, -16711936);
        }
        context.drawItemWithoutEntity(this.display.getIcon(), originX + this.x + 8, originY + this.y + 5);
    }

    private void drawText(DrawContext context, List<OrderedText> text, int x, int y, int color) {
        TextRenderer lv = this.client.textRenderer;
        for (int l = 0; l < text.size(); ++l) {
            context.drawTextWithShadow(lv, text.get(l), x, y + l * lv.fontHeight, color);
        }
    }

    public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int m = originX + this.x;
        int n = m + 26;
        int o = originY + this.y;
        int p = o + 26;
        return mouseX >= m && mouseX <= n && mouseY >= o && mouseY <= p;
    }

    public void addToTree() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}

