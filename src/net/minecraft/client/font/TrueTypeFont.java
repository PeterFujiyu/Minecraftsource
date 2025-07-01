/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFont
implements Font {
    @Nullable
    private ByteBuffer buffer;
    @Nullable
    private FT_Face face;
    final float oversample;
    private final GlyphContainer<LazyGlyph> container = new GlyphContainer(LazyGlyph[]::new, i -> new LazyGlyph[i][]);

    public TrueTypeFont(ByteBuffer buffer, FT_Face face, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
        this.buffer = buffer;
        this.face = face;
        this.oversample = oversample;
        IntArraySet intSet = new IntArraySet();
        excludedCharacters.codePoints().forEach(intSet::add);
        int j = Math.round(size * oversample);
        FreeType.FT_Set_Pixel_Sizes(face, j, j);
        float k = shiftX * oversample;
        float l = -shiftY * oversample;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int n;
            FT_Vector fT_Vector = FreeTypeUtil.set(FT_Vector.malloc(memoryStack), k, l);
            FreeType.FT_Set_Transform(face, null, fT_Vector);
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            int m = (int)FreeType.FT_Get_First_Char(face, intBuffer);
            while ((n = intBuffer.get(0)) != 0) {
                if (!intSet.contains(m)) {
                    this.container.put(m, new LazyGlyph(n));
                }
                m = (int)FreeType.FT_Get_Next_Char(face, m, intBuffer);
            }
        }
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        LazyGlyph lv = this.container.get(codePoint);
        return lv != null ? this.getOrLoadGlyph(codePoint, lv) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Glyph getOrLoadGlyph(int codePoint, LazyGlyph glyph) {
        Glyph lv = glyph.glyph;
        if (lv == null) {
            FT_Face fT_Face;
            FT_Face fT_Face2 = fT_Face = this.getInfo();
            synchronized (fT_Face2) {
                lv = glyph.glyph;
                if (lv == null) {
                    glyph.glyph = lv = this.loadGlyph(codePoint, fT_Face, glyph.index);
                }
            }
        }
        return lv;
    }

    private Glyph loadGlyph(int codePoint, FT_Face face, int index) {
        FT_GlyphSlot fT_GlyphSlot;
        int k = FreeType.FT_Load_Glyph(face, index, 0x400008);
        if (k != 0) {
            FreeTypeUtil.checkFatalError(k, String.format(Locale.ROOT, "Loading glyph U+%06X", codePoint));
        }
        if ((fT_GlyphSlot = face.glyph()) == null) {
            throw new NullPointerException(String.format(Locale.ROOT, "Glyph U+%06X not initialized", codePoint));
        }
        float f = FreeTypeUtil.getX(fT_GlyphSlot.advance());
        FT_Bitmap fT_Bitmap = fT_GlyphSlot.bitmap();
        int l = fT_GlyphSlot.bitmap_left();
        int m = fT_GlyphSlot.bitmap_top();
        int n = fT_Bitmap.width();
        int o = fT_Bitmap.rows();
        if (n <= 0 || o <= 0) {
            return () -> f / this.oversample;
        }
        return new TtfGlyph(l, m, n, o, f, index);
    }

    FT_Face getInfo() {
        if (this.buffer == null || this.face == null) {
            throw new IllegalStateException("Provider already closed");
        }
        return this.face;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.face != null) {
            Object object = FreeTypeUtil.LOCK;
            synchronized (object) {
                FreeTypeUtil.checkError(FreeType.FT_Done_Face(this.face), "Deleting face");
            }
            this.face = null;
        }
        MemoryUtil.memFree(this.buffer);
        this.buffer = null;
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return this.container.getProvidedGlyphs();
    }

    @Environment(value=EnvType.CLIENT)
    static class LazyGlyph {
        final int index;
        @Nullable
        volatile Glyph glyph;

        LazyGlyph(int index) {
            this.index = index;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class TtfGlyph
    implements Glyph {
        final int width;
        final int height;
        final float bearingX;
        final float ascent;
        private final float advance;
        final int glyphIndex;

        TtfGlyph(float bearingX, float ascent, int width, int height, float advance, int glyphIndex) {
            this.width = width;
            this.height = height;
            this.advance = advance / TrueTypeFont.this.oversample;
            this.bearingX = bearingX / TrueTypeFont.this.oversample;
            this.ascent = ascent / TrueTypeFont.this.oversample;
            this.glyphIndex = glyphIndex;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<RenderableGlyph, BakedGlyph> function) {
            return function.apply(new RenderableGlyph(){

                @Override
                public int getWidth() {
                    return TtfGlyph.this.width;
                }

                @Override
                public int getHeight() {
                    return TtfGlyph.this.height;
                }

                @Override
                public float getOversample() {
                    return TrueTypeFont.this.oversample;
                }

                @Override
                public float getBearingX() {
                    return TtfGlyph.this.bearingX;
                }

                @Override
                public float getAscent() {
                    return TtfGlyph.this.ascent;
                }

                @Override
                public void upload(int x, int y) {
                    NativeImage lv = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);
                    FT_Face fT_Face = TrueTypeFont.this.getInfo();
                    if (lv.makeGlyphBitmapSubpixel(fT_Face, TtfGlyph.this.glyphIndex)) {
                        lv.upload(0, x, y, 0, 0, TtfGlyph.this.width, TtfGlyph.this.height, true);
                    } else {
                        lv.close();
                    }
                }

                @Override
                public boolean hasColor() {
                    return false;
                }
            });
        }
    }
}

