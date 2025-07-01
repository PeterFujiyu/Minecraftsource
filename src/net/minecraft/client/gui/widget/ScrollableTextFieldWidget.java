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
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class ScrollableTextFieldWidget
extends ScrollableWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted"));
    private static final int field_55261 = 4;

    public ScrollableTextFieldWidget(int i, int j, int k, int l, Text arg) {
        super(i, j, k, l, arg);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = this.checkScrollbarDragged(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button) || bl;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl2;
        boolean bl = keyCode == GLFW.GLFW_KEY_UP;
        boolean bl3 = bl2 = keyCode == GLFW.GLFW_KEY_DOWN;
        if (bl || bl2) {
            double d = this.getScrollY();
            this.setScrollY(this.getScrollY() + (double)(bl ? -1 : 1) * this.getDeltaYPerScroll());
            if (d != this.getScrollY()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        this.drawBox(context);
        context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
        context.getMatrices().push();
        context.getMatrices().translate(0.0, -this.getScrollY(), 0.0);
        this.renderContents(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
        context.disableScissor();
        this.renderOverlay(context);
    }

    protected void renderOverlay(DrawContext context) {
        this.drawScrollbar(context);
    }

    protected int getTextMargin() {
        return 4;
    }

    protected int getPadding() {
        return this.getTextMargin() * 2;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getRight() + 6) && mouseY < (double)this.getBottom();
    }

    @Override
    protected int getScrollbarX() {
        return this.getRight();
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return this.getContentsHeight() + this.getPadding();
    }

    protected void drawBox(DrawContext context) {
        this.draw(context, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    protected void draw(DrawContext context, int x, int y, int width, int height) {
        Identifier lv = TEXTURES.get(this.isNarratable(), this.isFocused());
        context.drawGuiTexture(RenderLayer::getGuiTextured, lv, x, y, width, height);
    }

    protected boolean isVisible(int textTop, int textBottom) {
        return (double)textBottom - this.getScrollY() >= (double)this.getY() && (double)textTop - this.getScrollY() <= (double)(this.getY() + this.height);
    }

    protected abstract int getContentsHeight();

    protected abstract void renderContents(DrawContext var1, int var2, int var3, float var4);

    protected int getTextX() {
        return this.getX() + this.getTextMargin();
    }

    protected int getTextY() {
        return this.getY() + this.getTextMargin();
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }
}

