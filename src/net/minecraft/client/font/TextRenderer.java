/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EmptyBakedGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class TextRenderer {
    private static final float Z_INDEX = 0.01f;
    public static final float FORWARD_SHIFT = 0.03f;
    public static final int field_55090 = 0;
    public static final int ARABIC_SHAPING_LETTERS_SHAPE = 8;
    public final int fontHeight = 9;
    public final Random random = Random.create();
    private final Function<Identifier, FontStorage> fontStorageAccessor;
    final boolean validateAdvance;
    private final TextHandler handler;

    public TextRenderer(Function<Identifier, FontStorage> fontStorageAccessor, boolean validateAdvance) {
        this.fontStorageAccessor = fontStorageAccessor;
        this.validateAdvance = validateAdvance;
        this.handler = new TextHandler((codePoint, style) -> this.getFontStorage(style.getFont()).getGlyph(codePoint, this.validateAdvance).getAdvance(style.isBold()));
    }

    FontStorage getFontStorage(Identifier id) {
        return this.fontStorageAccessor.apply(id);
    }

    public String mirror(String text) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException arabicShapingException) {
            return text;
        }
    }

    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        if (this.isRightToLeft()) {
            text = this.mirror(text);
        }
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, true);
    }

    public int draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        return this.draw(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, true);
    }

    public int draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light, boolean swapZIndex) {
        return this.drawInternal(text.asOrderedText(), x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, swapZIndex);
    }

    public int draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, true);
    }

    public void drawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        int l = TextRenderer.tweakTransparency(outlineColor);
        Drawer lv = new Drawer(vertexConsumers, 0.0f, 0.0f, l, false, matrix, TextLayerType.NORMAL, light);
        for (int m = -1; m <= 1; ++m) {
            for (int n = -1; n <= 1; ++n) {
                if (m == 0 && n == 0) continue;
                float[] fs = new float[]{x};
                int o = m;
                int p = n;
                text.accept((index, style, codePoint) -> {
                    boolean bl = style.isBold();
                    FontStorage lv = this.getFontStorage(style.getFont());
                    Glyph lv2 = lv.getGlyph(codePoint, this.validateAdvance);
                    arg.x = fs[0] + (float)o * lv2.getShadowOffset();
                    arg.y = y + (float)p * lv2.getShadowOffset();
                    fs[0] = fs[0] + lv2.getAdvance(bl);
                    return lv.accept(index, style.withColor(l), codePoint);
                });
            }
        }
        lv.drawGlyphs();
        Drawer lv2 = new Drawer(vertexConsumers, x, y, TextRenderer.tweakTransparency(color), false, matrix, TextLayerType.POLYGON_OFFSET, light);
        text.accept(lv2);
        lv2.drawLayer(x);
    }

    private static int tweakTransparency(int argb) {
        if ((argb & 0xFC000000) == 0) {
            return ColorHelper.fullAlpha(argb);
        }
        return argb;
    }

    private int drawInternal(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light, boolean mirror) {
        color = TextRenderer.tweakTransparency(color);
        x = this.drawLayer(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, mirror);
        return (int)x + (shadow ? 1 : 0);
    }

    private int drawInternal(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColor, int light, boolean swapZIndex) {
        color = TextRenderer.tweakTransparency(color);
        x = this.drawLayer(text, x, y, color, shadow, matrix, vertexConsumerProvider, layerType, backgroundColor, light, swapZIndex);
        return (int)x + (shadow ? 1 : 0);
    }

    private float drawLayer(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColor, int light, boolean swapZIndex) {
        Drawer lv = new Drawer(vertexConsumerProvider, x, y, color, backgroundColor, shadow, matrix, layerType, light, swapZIndex);
        TextVisitFactory.visitFormatted(text, Style.EMPTY, (CharacterVisitor)lv);
        return lv.drawLayer(x);
    }

    private float drawLayer(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColor, int light, boolean swapZIndex) {
        Drawer lv = new Drawer(vertexConsumerProvider, x, y, color, backgroundColor, shadow, matrix, layerType, light, swapZIndex);
        text.accept(lv);
        return lv.drawLayer(x);
    }

    public int getWidth(String text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(StringVisitable text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(OrderedText text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? this.handler.trimToWidthBackwards(text, maxWidth, Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public String trimToWidth(String text, int maxWidth) {
        return this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public StringVisitable trimToWidth(StringVisitable text, int width) {
        return this.handler.trimToWidth(text, width, Style.EMPTY);
    }

    public int getWrappedLinesHeight(String text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public List<OrderedText> wrapLines(StringVisitable text, int width) {
        return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
    }

    public boolean isRightToLeft() {
        return Language.getInstance().isRightToLeft();
    }

    public TextHandler getTextHandler() {
        return this.handler;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum TextLayerType {
        NORMAL,
        SEE_THROUGH,
        POLYGON_OFFSET;

    }

    @Environment(value=EnvType.CLIENT)
    class Drawer
    implements CharacterVisitor {
        final VertexConsumerProvider vertexConsumers;
        private final boolean shadow;
        private final int color;
        private final int backgroundColor;
        private final Matrix4f matrix;
        private final TextLayerType layerType;
        private final int light;
        private final boolean swapZIndex;
        float x;
        float y;
        private final List<BakedGlyph.DrawnGlyph> glyphs = new ArrayList<BakedGlyph.DrawnGlyph>();
        @Nullable
        private List<BakedGlyph.Rectangle> rectangles;

        private void addRectangle(BakedGlyph.Rectangle rectangle) {
            if (this.rectangles == null) {
                this.rectangles = Lists.newArrayList();
            }
            this.rectangles.add(rectangle);
        }

        public Drawer(VertexConsumerProvider vertexConsumers, float x, float y, int color, boolean shadow, Matrix4f matrix, TextLayerType layerType, int light) {
            this(vertexConsumers, x, y, color, 0, shadow, matrix, layerType, light, true);
        }

        public Drawer(VertexConsumerProvider vertexConsumers, float x, float y, int color, int backgroundColor, boolean shadow, Matrix4f matrix, TextLayerType layerType, int light, boolean swapZIndex) {
            this.vertexConsumers = vertexConsumers;
            this.x = x;
            this.y = y;
            this.shadow = shadow;
            this.color = color;
            this.backgroundColor = backgroundColor;
            this.matrix = matrix;
            this.layerType = layerType;
            this.light = light;
            this.swapZIndex = swapZIndex;
        }

        @Override
        public boolean accept(int i, Style arg, int j) {
            FontStorage lv = TextRenderer.this.getFontStorage(arg.getFont());
            Glyph lv2 = lv.getGlyph(j, TextRenderer.this.validateAdvance);
            BakedGlyph lv3 = arg.isObfuscated() && j != 32 ? lv.getObfuscatedBakedGlyph(lv2) : lv.getBaked(j);
            boolean bl = arg.isBold();
            TextColor lv4 = arg.getColor();
            int k = this.getRenderColor(lv4);
            int l = this.getShadowColor(arg, k);
            float f = lv2.getAdvance(bl);
            float g = i == 0 ? this.x - 1.0f : this.x;
            float h = lv2.getShadowOffset();
            if (!(lv3 instanceof EmptyBakedGlyph)) {
                float m = bl ? lv2.getBoldOffset() : 0.0f;
                this.glyphs.add(new BakedGlyph.DrawnGlyph(this.x, this.y, k, l, lv3, arg, m, h));
            }
            if (arg.isStrikethrough()) {
                this.addRectangle(new BakedGlyph.Rectangle(g, this.y + 4.5f, this.x + f, this.y + 4.5f - 1.0f, this.getForegroundZIndex(), k, l, h));
            }
            if (arg.isUnderlined()) {
                this.addRectangle(new BakedGlyph.Rectangle(g, this.y + 9.0f, this.x + f, this.y + 9.0f - 1.0f, this.getForegroundZIndex(), k, l, h));
            }
            this.x += f;
            return true;
        }

        float drawLayer(float x) {
            BakedGlyph lv = null;
            if (this.backgroundColor != 0) {
                BakedGlyph.Rectangle lv2 = new BakedGlyph.Rectangle(x - 1.0f, this.y + 9.0f, this.x, this.y - 1.0f, this.getBackgroundZIndex(), this.backgroundColor);
                lv = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
                VertexConsumer lv3 = this.vertexConsumers.getBuffer(lv.getLayer(this.layerType));
                lv.drawRectangle(lv2, this.matrix, lv3, this.light);
            }
            this.drawGlyphs();
            if (this.rectangles != null) {
                if (lv == null) {
                    lv = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
                }
                VertexConsumer lv4 = this.vertexConsumers.getBuffer(lv.getLayer(this.layerType));
                for (BakedGlyph.Rectangle lv5 : this.rectangles) {
                    lv.drawRectangle(lv5, this.matrix, lv4, this.light);
                }
            }
            return this.x;
        }

        private int getRenderColor(@Nullable TextColor override) {
            if (override != null) {
                int i = ColorHelper.getAlpha(this.color);
                int j = override.getRgb();
                return ColorHelper.withAlpha(i, j);
            }
            return this.color;
        }

        private int getShadowColor(Style style, int textColor) {
            Integer integer = style.getShadowColor();
            if (integer != null) {
                float f = ColorHelper.getAlphaFloat(textColor);
                float g = ColorHelper.getAlphaFloat(integer);
                if (f != 1.0f) {
                    return ColorHelper.withAlpha(ColorHelper.channelFromFloat(f * g), integer);
                }
                return integer;
            }
            if (this.shadow) {
                return ColorHelper.scaleRgb(textColor, 0.25f);
            }
            return 0;
        }

        void drawGlyphs() {
            for (BakedGlyph.DrawnGlyph lv : this.glyphs) {
                BakedGlyph lv2 = lv.glyph();
                VertexConsumer lv3 = this.vertexConsumers.getBuffer(lv2.getLayer(this.layerType));
                lv2.draw(lv, this.matrix, lv3, this.light);
            }
        }

        private float getForegroundZIndex() {
            return this.swapZIndex ? 0.01f : -0.01f;
        }

        private float getBackgroundZIndex() {
            return this.swapZIndex ? -0.01f : 0.01f;
        }
    }
}

