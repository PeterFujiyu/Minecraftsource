/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Style;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class BakedGlyph {
    public static final float field_55098 = 0.001f;
    private final TextRenderLayerSet textRenderLayers;
    private final float minU;
    private final float maxU;
    private final float minV;
    private final float maxV;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;

    public BakedGlyph(TextRenderLayerSet textRenderLayers, float minU, float maxU, float minV, float maxV, float minX, float maxX, float minY, float maxY) {
        this.textRenderLayers = textRenderLayers;
        this.minU = minU;
        this.maxU = maxU;
        this.minV = minV;
        this.maxV = maxV;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void draw(DrawnGlyph glyph, Matrix4f matrix, VertexConsumer vertexConsumer, int light) {
        Style lv = glyph.style();
        boolean bl = lv.isItalic();
        float f = glyph.x();
        float g = glyph.y();
        int j = glyph.color();
        int k = glyph.shadowColor();
        boolean bl2 = lv.isBold();
        if (glyph.hasShadow()) {
            this.draw(bl, f + glyph.shadowOffset(), g + glyph.shadowOffset(), matrix, vertexConsumer, k, bl2, light);
            this.draw(bl, f, g, 0.03f, matrix, vertexConsumer, j, bl2, light);
        } else {
            this.draw(bl, f, g, matrix, vertexConsumer, j, bl2, light);
        }
        if (bl2) {
            if (glyph.hasShadow()) {
                this.draw(bl, f + glyph.boldOffset() + glyph.shadowOffset(), g + glyph.shadowOffset(), 0.001f, matrix, vertexConsumer, k, true, light);
                this.draw(bl, f + glyph.boldOffset(), g, 0.03f, matrix, vertexConsumer, j, true, light);
            } else {
                this.draw(bl, f + glyph.boldOffset(), g, matrix, vertexConsumer, j, true, light);
            }
        }
    }

    private void draw(boolean italic, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, int color, boolean bold, int light) {
        this.draw(italic, x, y, 0.0f, matrix, vertexConsumer, color, bold, light);
    }

    private void draw(boolean italic, float x, float y, float z, Matrix4f matrix, VertexConsumer vertexConsumer, int color, boolean bold, int light) {
        float k = x + this.minX;
        float l = x + this.maxX;
        float m = y + this.minY;
        float n = y + this.maxY;
        float o = italic ? 1.0f - 0.25f * this.minY : 0.0f;
        float p = italic ? 1.0f - 0.25f * this.maxY : 0.0f;
        float q = bold ? 0.1f : 0.0f;
        vertexConsumer.vertex(matrix, k + o - q, m - q, z).color(color).texture(this.minU, this.minV).light(light);
        vertexConsumer.vertex(matrix, k + p - q, n + q, z).color(color).texture(this.minU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, l + p + q, n + q, z).color(color).texture(this.maxU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, l + o + q, m - q, z).color(color).texture(this.maxU, this.minV).light(light);
    }

    public void drawRectangle(Rectangle rectangle, Matrix4f matrix, VertexConsumer vertexConsumer, int light) {
        if (rectangle.hasShadow()) {
            this.drawRectangle(rectangle, rectangle.shadowOffset(), 0.0f, rectangle.shadowColor(), vertexConsumer, light, matrix);
            this.drawRectangle(rectangle, 0.0f, 0.03f, rectangle.color, vertexConsumer, light, matrix);
        } else {
            this.drawRectangle(rectangle, 0.0f, 0.0f, rectangle.color, vertexConsumer, light, matrix);
        }
    }

    private void drawRectangle(Rectangle rectangle, float shadowOffset, float zOffset, int color, VertexConsumer vertexConsumer, int light, Matrix4f matrix) {
        vertexConsumer.vertex(matrix, rectangle.minX + shadowOffset, rectangle.minY + shadowOffset, rectangle.zIndex + zOffset).color(color).texture(this.minU, this.minV).light(light);
        vertexConsumer.vertex(matrix, rectangle.maxX + shadowOffset, rectangle.minY + shadowOffset, rectangle.zIndex + zOffset).color(color).texture(this.minU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, rectangle.maxX + shadowOffset, rectangle.maxY + shadowOffset, rectangle.zIndex + zOffset).color(color).texture(this.maxU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, rectangle.minX + shadowOffset, rectangle.maxY + shadowOffset, rectangle.zIndex + zOffset).color(color).texture(this.maxU, this.minV).light(light);
    }

    public RenderLayer getLayer(TextRenderer.TextLayerType layerType) {
        return this.textRenderLayers.getRenderLayer(layerType);
    }

    @Environment(value=EnvType.CLIENT)
    public record DrawnGlyph(float x, float y, int color, int shadowColor, BakedGlyph glyph, Style style, float boldOffset, float shadowOffset) {
        boolean hasShadow() {
            return this.shadowColor() != 0;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Rectangle(float minX, float minY, float maxX, float maxY, float zIndex, int color, int shadowColor, float shadowOffset) {
        public Rectangle(float minX, float minY, float maxX, float maxY, float zIndex, int color) {
            this(minX, minY, maxX, maxY, zIndex, color, 0, 0.0f);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }
    }
}

