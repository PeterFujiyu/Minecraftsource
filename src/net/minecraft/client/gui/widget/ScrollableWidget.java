/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class ScrollableWidget
extends ClickableWidget {
    public static final int SCROLLBAR_WIDTH = 6;
    private double scrollY;
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("widget/scroller");
    private static final Identifier SCROLLER_BACKGROUND_TEXTURE = Identifier.ofVanilla("widget/scroller_background");
    private boolean scrollbarDragged;

    public ScrollableWidget(int i, int j, int k, int l, Text arg) {
        super(i, j, k, l, arg);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.visible) {
            return false;
        }
        this.setScrollY(this.getScrollY() - verticalAmount * this.getDeltaYPerScroll());
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollbarDragged) {
            if (mouseY < (double)this.getY()) {
                this.setScrollY(0.0);
            } else if (mouseY > (double)this.getBottom()) {
                this.setScrollY(this.getMaxScrollY());
            } else {
                double h = Math.max(1, this.getMaxScrollY());
                int j = this.getScrollbarThumbHeight();
                double k = Math.max(1.0, h / (double)(this.height - j));
                this.setScrollY(this.getScrollY() + deltaY * k);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.scrollbarDragged = false;
    }

    public double getScrollY() {
        return this.scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = MathHelper.clamp(scrollY, 0.0, (double)this.getMaxScrollY());
    }

    public boolean checkScrollbarDragged(double mouseX, double mouseY, int button) {
        this.scrollbarDragged = this.overflows() && this.isValidClickButton(button) && mouseX >= (double)this.getScrollbarX() && mouseX <= (double)(this.getScrollbarX() + 6) && mouseY >= (double)this.getY() && mouseY < (double)this.getBottom();
        return this.scrollbarDragged;
    }

    public void refreshScroll() {
        this.setScrollY(this.scrollY);
    }

    public int getMaxScrollY() {
        return Math.max(0, this.getContentsHeightWithPadding() - this.height);
    }

    protected boolean overflows() {
        return this.getMaxScrollY() > 0;
    }

    protected int getScrollbarThumbHeight() {
        return MathHelper.clamp((int)((float)(this.height * this.height) / (float)this.getContentsHeightWithPadding()), 32, this.height - 8);
    }

    protected int getScrollbarX() {
        return this.getRight() - 6;
    }

    protected int getScrollbarThumbY() {
        return Math.max(this.getY(), (int)this.scrollY * (this.height - this.getScrollbarThumbHeight()) / this.getMaxScrollY() + this.getY());
    }

    protected void drawScrollbar(DrawContext context) {
        if (this.overflows()) {
            int i = this.getScrollbarX();
            int j = this.getScrollbarThumbHeight();
            int k = this.getScrollbarThumbY();
            context.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_BACKGROUND_TEXTURE, i, this.getY(), 6, this.getHeight());
            context.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_TEXTURE, i, k, 6, j);
        }
    }

    protected abstract int getContentsHeightWithPadding();

    protected abstract double getDeltaYPerScroll();
}

