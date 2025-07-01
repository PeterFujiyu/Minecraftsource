/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

@Environment(value=EnvType.CLIENT)
public class DrawContext {
    public static final float field_44931 = 10000.0f;
    public static final float field_44932 = -10000.0f;
    private static final int field_44655 = 2;
    private final MinecraftClient client;
    private final MatrixStack matrices;
    private final VertexConsumerProvider.Immediate vertexConsumers;
    private final ScissorStack scissorStack = new ScissorStack();
    private final GuiAtlasManager guiAtlasManager;
    private final ItemRenderState itemRenderState = new ItemRenderState();

    private DrawContext(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers) {
        this.client = client;
        this.matrices = matrices;
        this.vertexConsumers = vertexConsumers;
        this.guiAtlasManager = client.getGuiAtlasManager();
    }

    public DrawContext(MinecraftClient client, VertexConsumerProvider.Immediate vertexConsumers) {
        this(client, new MatrixStack(), vertexConsumers);
    }

    public int getScaledWindowWidth() {
        return this.client.getWindow().getScaledWidth();
    }

    public int getScaledWindowHeight() {
        return this.client.getWindow().getScaledHeight();
    }

    public MatrixStack getMatrices() {
        return this.matrices;
    }

    public void draw() {
        this.vertexConsumers.draw();
    }

    public void drawHorizontalLine(int x1, int x2, int y, int color) {
        this.drawHorizontalLine(RenderLayer.getGui(), x1, x2, y, color);
    }

    public void drawHorizontalLine(RenderLayer layer, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int m = x1;
            x1 = x2;
            x2 = m;
        }
        this.fill(layer, x1, y, x2 + 1, y + 1, color);
    }

    public void drawVerticalLine(int x, int y1, int y2, int color) {
        this.drawVerticalLine(RenderLayer.getGui(), x, y1, y2, color);
    }

    public void drawVerticalLine(RenderLayer layer, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int m = y1;
            y1 = y2;
            y2 = m;
        }
        this.fill(layer, x, y1 + 1, x + 1, y2, color);
    }

    public void enableScissor(int x1, int y1, int x2, int y2) {
        ScreenRect lv = new ScreenRect(x1, y1, x2 - x1, y2 - y1).transform(this.matrices.peek().getPositionMatrix());
        this.setScissor(this.scissorStack.push(lv));
    }

    public void disableScissor() {
        this.setScissor(this.scissorStack.pop());
    }

    public boolean scissorContains(int x, int y) {
        return this.scissorStack.contains(x, y);
    }

    private void setScissor(@Nullable ScreenRect rect) {
        this.draw();
        if (rect != null) {
            Window lv = MinecraftClient.getInstance().getWindow();
            int i = lv.getFramebufferHeight();
            double d = lv.getScaleFactor();
            double e = (double)rect.getLeft() * d;
            double f = (double)i - (double)rect.getBottom() * d;
            double g = (double)rect.width() * d;
            double h = (double)rect.height() * d;
            RenderSystem.enableScissor((int)e, (int)f, Math.max(0, (int)g), Math.max(0, (int)h));
        } else {
            RenderSystem.disableScissor();
        }
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        this.fill(x1, y1, x2, y2, 0, color);
    }

    public void fill(int x1, int y1, int x2, int y2, int z, int color) {
        this.fill(RenderLayer.getGui(), x1, y1, x2, y2, z, color);
    }

    public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color) {
        this.fill(layer, x1, y1, x2, y2, 0, color);
    }

    public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int z, int color) {
        int o;
        Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
        if (x1 < x2) {
            o = x1;
            x1 = x2;
            x2 = o;
        }
        if (y1 < y2) {
            o = y1;
            y1 = y2;
            y2 = o;
        }
        VertexConsumer lv = this.vertexConsumers.getBuffer(layer);
        lv.vertex(matrix4f, (float)x1, (float)y1, (float)z).color(color);
        lv.vertex(matrix4f, (float)x1, (float)y2, (float)z).color(color);
        lv.vertex(matrix4f, (float)x2, (float)y2, (float)z).color(color);
        lv.vertex(matrix4f, (float)x2, (float)y1, (float)z).color(color);
    }

    public void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        this.fillGradient(startX, startY, endX, endY, 0, colorStart, colorEnd);
    }

    public void fillGradient(int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        this.fillGradient(RenderLayer.getGui(), startX, startY, endX, endY, colorStart, colorEnd, z);
    }

    public void fillGradient(RenderLayer layer, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        VertexConsumer lv = this.vertexConsumers.getBuffer(layer);
        this.fillGradient(lv, startX, startY, endX, endY, z, colorStart, colorEnd);
    }

    private void fillGradient(VertexConsumer vertexConsumer, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).color(colorEnd);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).color(colorEnd);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).color(colorStart);
    }

    public void fillWithLayer(RenderLayer layer, int startX, int startY, int endX, int endY, int z) {
        Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
        VertexConsumer lv = this.vertexConsumers.getBuffer(layer);
        lv.vertex(matrix4f, (float)startX, (float)startY, (float)z);
        lv.vertex(matrix4f, (float)startX, (float)endY, (float)z);
        lv.vertex(matrix4f, (float)endX, (float)endY, (float)z);
        lv.vertex(matrix4f, (float)endX, (float)startY, (float)z);
    }

    public void drawCenteredTextWithShadow(TextRenderer textRenderer, String text, int centerX, int y, int color) {
        this.drawTextWithShadow(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color);
    }

    public void drawCenteredTextWithShadow(TextRenderer textRenderer, Text text, int centerX, int y, int color) {
        OrderedText lv = text.asOrderedText();
        this.drawTextWithShadow(textRenderer, lv, centerX - textRenderer.getWidth(lv) / 2, y, color);
    }

    public void drawCenteredTextWithShadow(TextRenderer textRenderer, OrderedText text, int centerX, int y, int color) {
        this.drawTextWithShadow(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color);
    }

    public int drawTextWithShadow(TextRenderer textRenderer, @Nullable String text, int x, int y, int color) {
        return this.drawText(textRenderer, text, x, y, color, true);
    }

    public int drawText(TextRenderer textRenderer, @Nullable String text, int x, int y, int color, boolean shadow) {
        if (text == null) {
            return 0;
        }
        int l = textRenderer.draw(text, (float)x, (float)y, color, shadow, this.matrices.peek().getPositionMatrix(), (VertexConsumerProvider)this.vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        return l;
    }

    public int drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
        return this.drawText(textRenderer, text, x, y, color, true);
    }

    public int drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
        int l = textRenderer.draw(text, (float)x, (float)y, color, shadow, this.matrices.peek().getPositionMatrix(), (VertexConsumerProvider)this.vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        return l;
    }

    public int drawTextWithShadow(TextRenderer textRenderer, Text text, int x, int y, int color) {
        return this.drawText(textRenderer, text, x, y, color, true);
    }

    public int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
        return this.drawText(textRenderer, text.asOrderedText(), x, y, color, shadow);
    }

    public void drawWrappedTextWithShadow(TextRenderer textRenderer, StringVisitable text, int x, int y, int width, int color) {
        this.drawWrappedText(textRenderer, text, x, y, width, color, true);
    }

    public void drawWrappedText(TextRenderer textRenderer, StringVisitable text, int x, int y, int width, int color, boolean shadow) {
        for (OrderedText lv : textRenderer.wrapLines(text, width)) {
            this.drawText(textRenderer, lv, x, y, color, shadow);
            y += textRenderer.fontHeight;
        }
    }

    public int drawTextWithBackground(TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
        int m = this.client.options.getTextBackgroundColor(0.0f);
        if (m != 0) {
            int n = 2;
            this.fill(x - 2, y - 2, x + width + 2, y + textRenderer.fontHeight + 2, ColorHelper.mix(m, color));
        }
        return this.drawText(textRenderer, text, x, y, color, true);
    }

    public void drawBorder(int x, int y, int width, int height, int color) {
        this.fill(x, y, x + width, y + 1, color);
        this.fill(x, y + height - 1, x + width, y + height, color);
        this.fill(x, y + 1, x + 1, y + height - 1, color);
        this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public void drawGuiTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        this.drawGuiTexture(renderLayers, sprite, x, y, width, height, -1);
    }

    public void drawGuiTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height, int color) {
        Sprite lv = this.guiAtlasManager.getSprite(sprite);
        Scaling lv2 = this.guiAtlasManager.getScaling(lv);
        if (lv2 instanceof Scaling.Stretch) {
            this.drawSpriteStretched(renderLayers, lv, x, y, width, height, color);
        } else if (lv2 instanceof Scaling.Tile) {
            Scaling.Tile lv3 = (Scaling.Tile)lv2;
            this.drawSpriteTiled(renderLayers, lv, x, y, width, height, 0, 0, lv3.width(), lv3.height(), lv3.width(), lv3.height(), color);
        } else if (lv2 instanceof Scaling.NineSlice) {
            Scaling.NineSlice lv4 = (Scaling.NineSlice)lv2;
            this.drawSpriteNineSliced(renderLayers, lv, lv4, x, y, width, height, color);
        }
    }

    public void drawGuiTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height) {
        Sprite lv = this.guiAtlasManager.getSprite(sprite);
        Scaling lv2 = this.guiAtlasManager.getScaling(lv);
        if (lv2 instanceof Scaling.Stretch) {
            this.drawSpriteRegion(renderLayers, lv, textureWidth, textureHeight, u, v, x, y, width, height, -1);
        } else {
            this.enableScissor(x, y, x + width, y + height);
            this.drawGuiTexture(renderLayers, sprite, x - u, y - v, textureWidth, textureHeight, -1);
            this.disableScissor();
        }
    }

    public void drawSpriteStretched(Function<Identifier, RenderLayer> renderLayers, Sprite sprite, int x, int y, int width, int height) {
        this.drawSpriteStretched(renderLayers, sprite, x, y, width, height, -1);
    }

    public void drawSpriteStretched(Function<Identifier, RenderLayer> renderLayers, Sprite sprite, int x, int y, int width, int height, int color) {
        if (width == 0 || height == 0) {
            return;
        }
        this.drawTexturedQuad(renderLayers, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), color);
    }

    private void drawSpriteRegion(Function<Identifier, RenderLayer> renderLayers, Sprite sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, int color) {
        if (width == 0 || height == 0) {
            return;
        }
        this.drawTexturedQuad(renderLayers, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getFrameU((float)u / (float)textureWidth), sprite.getFrameU((float)(u + width) / (float)textureWidth), sprite.getFrameV((float)v / (float)textureHeight), sprite.getFrameV((float)(v + height) / (float)textureHeight), color);
    }

    private void drawSpriteNineSliced(Function<Identifier, RenderLayer> renderLayers, Sprite sprite, Scaling.NineSlice nineSlice, int x, int y, int width, int height, int color) {
        Scaling.NineSlice.Border lv = nineSlice.border();
        int n = Math.min(lv.left(), width / 2);
        int o = Math.min(lv.right(), width / 2);
        int p = Math.min(lv.top(), height / 2);
        int q = Math.min(lv.bottom(), height / 2);
        if (width == nineSlice.width() && height == nineSlice.height()) {
            this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, height, color);
            return;
        }
        if (height == nineSlice.height()) {
            this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, n, height, color);
            this.drawInnerSprite(renderLayers, nineSlice, sprite, x + n, y, width - o - n, height, n, 0, nineSlice.width() - o - n, nineSlice.height(), nineSlice.width(), nineSlice.height(), color);
            this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, 0, x + width - o, y, o, height, color);
            return;
        }
        if (width == nineSlice.width()) {
            this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, p, color);
            this.drawInnerSprite(renderLayers, nineSlice, sprite, x, y + p, width, height - q - p, 0, p, nineSlice.width(), nineSlice.height() - q - p, nineSlice.width(), nineSlice.height(), color);
            this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - q, x, y + height - q, width, q, color);
            return;
        }
        this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, n, p, color);
        this.drawInnerSprite(renderLayers, nineSlice, sprite, x + n, y, width - o - n, p, n, 0, nineSlice.width() - o - n, p, nineSlice.width(), nineSlice.height(), color);
        this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, 0, x + width - o, y, o, p, color);
        this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - q, x, y + height - q, n, q, color);
        this.drawInnerSprite(renderLayers, nineSlice, sprite, x + n, y + height - q, width - o - n, q, n, nineSlice.height() - q, nineSlice.width() - o - n, q, nineSlice.width(), nineSlice.height(), color);
        this.drawSpriteRegion(renderLayers, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, nineSlice.height() - q, x + width - o, y + height - q, o, q, color);
        this.drawInnerSprite(renderLayers, nineSlice, sprite, x, y + p, n, height - q - p, 0, p, n, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height(), color);
        this.drawInnerSprite(renderLayers, nineSlice, sprite, x + n, y + p, width - o - n, height - q - p, n, p, nineSlice.width() - o - n, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height(), color);
        this.drawInnerSprite(renderLayers, nineSlice, sprite, x + width - o, y + p, o, height - q - p, nineSlice.width() - o, p, o, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height(), color);
    }

    private void drawInnerSprite(Function<Identifier, RenderLayer> renderLayers, Scaling.NineSlice nineSlice, Sprite sprite, int x, int y, int width, int height, int u, int v, int tileWidth, int tileHeight, int textureWidth, int textureHeight, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (nineSlice.stretchInner()) {
            this.drawTexturedQuad(renderLayers, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getFrameU((float)u / (float)textureWidth), sprite.getFrameU((float)(u + tileWidth) / (float)textureWidth), sprite.getFrameV((float)v / (float)textureHeight), sprite.getFrameV((float)(v + tileHeight) / (float)textureHeight), color);
        } else {
            this.drawSpriteTiled(renderLayers, sprite, x, y, width, height, u, v, tileWidth, tileHeight, textureWidth, textureHeight, color);
        }
    }

    private void drawSpriteTiled(Function<Identifier, RenderLayer> renderLayers, Sprite sprite, int x, int y, int width, int height, int u, int v, int tileWidth, int tileHeight, int textureWidth, int textureHeight, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (tileWidth <= 0 || tileHeight <= 0) {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight);
        }
        for (int t = 0; t < width; t += tileWidth) {
            int u2 = Math.min(tileWidth, width - t);
            for (int v2 = 0; v2 < height; v2 += tileHeight) {
                int w = Math.min(tileHeight, height - v2);
                this.drawSpriteRegion(renderLayers, sprite, textureWidth, textureHeight, u, v, x + t, y + v2, u2, w, color);
            }
        }
    }

    public void drawTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
        this.drawTexture(renderLayers, sprite, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color);
    }

    public void drawTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        this.drawTexture(renderLayers, sprite, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }

    public void drawTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWith, int regionHeight, int textureWidth, int textureHeight) {
        this.drawTexture(renderLayers, sprite, x, y, u, v, width, height, regionWith, regionHeight, textureWidth, textureHeight, -1);
    }

    public void drawTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
        this.drawTexturedQuad(renderLayers, sprite, x, x + width, y, y + height, (u + 0.0f) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0f) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight, color);
    }

    private void drawTexturedQuad(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color) {
        RenderLayer lv = renderLayers.apply(sprite);
        Matrix4f matrix4f = this.matrices.peek().getPositionMatrix();
        VertexConsumer lv2 = this.vertexConsumers.getBuffer(lv);
        lv2.vertex(matrix4f, (float)x1, (float)y1, 0.0f).texture(u1, v1).color(color);
        lv2.vertex(matrix4f, (float)x1, (float)y2, 0.0f).texture(u1, v2).color(color);
        lv2.vertex(matrix4f, (float)x2, (float)y2, 0.0f).texture(u2, v2).color(color);
        lv2.vertex(matrix4f, (float)x2, (float)y1, 0.0f).texture(u2, v1).color(color);
    }

    public void drawItem(ItemStack item, int x, int y) {
        this.drawItem(this.client.player, this.client.world, item, x, y, 0);
    }

    public void drawItem(ItemStack stack, int x, int y, int seed) {
        this.drawItem(this.client.player, this.client.world, stack, x, y, seed);
    }

    public void drawItem(ItemStack stack, int x, int y, int seed, int z) {
        this.drawItem(this.client.player, this.client.world, stack, x, y, seed, z);
    }

    public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
        this.drawItemWithoutEntity(stack, x, y, 0);
    }

    public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
        this.drawItem(null, this.client.world, stack, x, y, seed);
    }

    public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
        this.drawItem(entity, entity.getWorld(), stack, x, y, seed);
    }

    private void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed) {
        this.drawItem(entity, world, stack, x, y, seed, 0);
    }

    private void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int z) {
        if (stack.isEmpty()) {
            return;
        }
        this.client.getItemModelManager().update(this.itemRenderState, stack, ModelTransformationMode.GUI, false, world, entity, seed);
        this.matrices.push();
        this.matrices.translate(x + 8, y + 8, 150 + (this.itemRenderState.hasDepth() ? z : 0));
        try {
            boolean bl;
            this.matrices.scale(16.0f, -16.0f, 16.0f);
            boolean bl2 = bl = !this.itemRenderState.isSideLit();
            if (bl) {
                this.draw();
                DiffuseLighting.disableGuiDepthLighting();
            }
            this.itemRenderState.render(this.matrices, this.vertexConsumers, 0xF000F0, OverlayTexture.DEFAULT_UV);
            this.draw();
            if (bl) {
                DiffuseLighting.enableGuiDepthLighting();
            }
        } catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Rendering item");
            CrashReportSection lv2 = lv.addElement("Item being rendered");
            lv2.add("Item Type", () -> String.valueOf(stack.getItem()));
            lv2.add("Item Components", () -> String.valueOf(stack.getComponents()));
            lv2.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
            throw new CrashException(lv);
        }
        this.matrices.pop();
    }

    public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y) {
        this.drawStackOverlay(textRenderer, stack, x, y, null);
    }

    public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String stackCountText) {
        if (stack.isEmpty()) {
            return;
        }
        this.matrices.push();
        this.drawItemBar(stack, x, y);
        this.drawStackCount(textRenderer, stack, x, y, stackCountText);
        this.drawCooldownProgress(stack, x, y);
        this.matrices.pop();
    }

    public void drawItemTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y) {
        this.drawTooltip(textRenderer, Screen.getTooltipFromItem(this.client, stack), stack.getTooltipData(), x, y, stack.get(DataComponentTypes.TOOLTIP_STYLE));
    }

    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y) {
        this.drawTooltip(textRenderer, text, data, x, y, null);
    }

    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data2, int x, int y, @Nullable Identifier texture) {
        List<TooltipComponent> list2 = text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Util.toArrayList());
        data2.ifPresent(data -> list2.add(list2.isEmpty() ? 0 : 1, TooltipComponent.of(data)));
        this.drawTooltip(textRenderer, list2, x, y, HoveredTooltipPositioner.INSTANCE, texture);
    }

    public void drawTooltip(TextRenderer textRenderer, Text text, int x, int y) {
        this.drawTooltip(textRenderer, text, x, y, null);
    }

    public void drawTooltip(TextRenderer textRenderer, Text text, int x, int y, @Nullable Identifier texture) {
        this.drawOrderedTooltip(textRenderer, List.of(text.asOrderedText()), x, y, texture);
    }

    public void drawTooltip(TextRenderer textRenderer, List<Text> text, int x, int y) {
        this.drawTooltip(textRenderer, text, x, y, null);
    }

    public void drawTooltip(TextRenderer textRenderer, List<Text> text, int x, int y, @Nullable Identifier texture) {
        this.drawTooltip(textRenderer, text.stream().map(Text::asOrderedText).map(TooltipComponent::of).toList(), x, y, HoveredTooltipPositioner.INSTANCE, texture);
    }

    public void drawOrderedTooltip(TextRenderer textRenderer, List<? extends OrderedText> text, int x, int y) {
        this.drawOrderedTooltip(textRenderer, text, x, y, null);
    }

    public void drawOrderedTooltip(TextRenderer textRenderer, List<? extends OrderedText> text, int x, int y, @Nullable Identifier texture) {
        this.drawTooltip(textRenderer, text.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, HoveredTooltipPositioner.INSTANCE, texture);
    }

    public void drawTooltip(TextRenderer textRenderer, List<OrderedText> text, TooltipPositioner positioner, int x, int y) {
        this.drawTooltip(textRenderer, text.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, positioner, null);
    }

    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture) {
        TooltipComponent lv2;
        int t;
        if (components.isEmpty()) {
            return;
        }
        int k = 0;
        int l = components.size() == 1 ? -2 : 0;
        for (TooltipComponent lv : components) {
            int m = lv.getWidth(textRenderer);
            if (m > k) {
                k = m;
            }
            l += lv.getHeight(textRenderer);
        }
        int n = k;
        int o = l;
        Vector2ic vector2ic = positioner.getPosition(this.getScaledWindowWidth(), this.getScaledWindowHeight(), x, y, n, o);
        int p = vector2ic.x();
        int q = vector2ic.y();
        this.matrices.push();
        int r = 400;
        TooltipBackgroundRenderer.render(this, p, q, n, o, 400, texture);
        this.matrices.translate(0.0f, 0.0f, 400.0f);
        int s = q;
        for (t = 0; t < components.size(); ++t) {
            lv2 = components.get(t);
            lv2.drawText(textRenderer, p, s, this.matrices.peek().getPositionMatrix(), this.vertexConsumers);
            s += lv2.getHeight(textRenderer) + (t == 0 ? 2 : 0);
        }
        s = q;
        for (t = 0; t < components.size(); ++t) {
            lv2 = components.get(t);
            lv2.drawItems(textRenderer, p, s, n, o, this);
            s += lv2.getHeight(textRenderer) + (t == 0 ? 2 : 0);
        }
        this.matrices.pop();
    }

    private void drawItemBar(ItemStack stack, int x, int y) {
        if (stack.isItemBarVisible()) {
            int k = x + 2;
            int l = y + 13;
            this.fill(RenderLayer.getGui(), k, l, k + 13, l + 2, 200, Colors.BLACK);
            this.fill(RenderLayer.getGui(), k, l, k + stack.getItemBarStep(), l + 1, 200, ColorHelper.fullAlpha(stack.getItemBarColor()));
        }
    }

    private void drawStackCount(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String stackCountText) {
        if (stack.getCount() != 1 || stackCountText != null) {
            String string2 = stackCountText == null ? String.valueOf(stack.getCount()) : stackCountText;
            this.matrices.push();
            this.matrices.translate(0.0f, 0.0f, 200.0f);
            this.drawText(textRenderer, string2, x + 19 - 2 - textRenderer.getWidth(string2), y + 6 + 3, Colors.WHITE, true);
            this.matrices.pop();
        }
    }

    private void drawCooldownProgress(ItemStack stack, int x, int y) {
        float f;
        ClientPlayerEntity lv = this.client.player;
        float f2 = f = lv == null ? 0.0f : lv.getItemCooldownManager().getCooldownProgress(stack, this.client.getRenderTickCounter().getTickDelta(true));
        if (f > 0.0f) {
            int k = y + MathHelper.floor(16.0f * (1.0f - f));
            int l = k + MathHelper.ceil(16.0f * f);
            this.fill(RenderLayer.getGui(), x, k, x + 16, l, 200, Integer.MAX_VALUE);
        }
    }

    public void drawHoverEvent(TextRenderer textRenderer, @Nullable Style style, int x, int y) {
        if (style == null || style.getHoverEvent() == null) {
            return;
        }
        HoverEvent lv = style.getHoverEvent();
        HoverEvent.ItemStackContent lv2 = lv.getValue(HoverEvent.Action.SHOW_ITEM);
        if (lv2 != null) {
            this.drawItemTooltip(textRenderer, lv2.asStack(), x, y);
        } else {
            HoverEvent.EntityContent lv3 = lv.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (lv3 != null) {
                if (this.client.options.advancedItemTooltips) {
                    this.drawTooltip(textRenderer, lv3.asTooltip(), x, y);
                }
            } else {
                Text lv4 = lv.getValue(HoverEvent.Action.SHOW_TEXT);
                if (lv4 != null) {
                    this.drawOrderedTooltip(textRenderer, textRenderer.wrapLines(lv4, Math.max(this.getScaledWindowWidth() / 2, 200)), x, y);
                }
            }
        }
    }

    public void draw(Consumer<VertexConsumerProvider> drawer) {
        drawer.accept(this.vertexConsumers);
        this.vertexConsumers.draw();
    }

    @Environment(value=EnvType.CLIENT)
    static class ScissorStack {
        private final Deque<ScreenRect> stack = new ArrayDeque<ScreenRect>();

        ScissorStack() {
        }

        public ScreenRect push(ScreenRect rect) {
            ScreenRect lv = this.stack.peekLast();
            if (lv != null) {
                ScreenRect lv2 = Objects.requireNonNullElse(rect.intersection(lv), ScreenRect.empty());
                this.stack.addLast(lv2);
                return lv2;
            }
            this.stack.addLast(rect);
            return rect;
        }

        @Nullable
        public ScreenRect pop() {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException("Scissor stack underflow");
            }
            this.stack.removeLast();
            return this.stack.peekLast();
        }

        public boolean contains(int x, int y) {
            if (this.stack.isEmpty()) {
                return true;
            }
            return this.stack.peek().contains(x, y);
        }
    }
}

