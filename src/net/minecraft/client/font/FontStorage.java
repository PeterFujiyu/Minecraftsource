/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random RANDOM = Random.create();
    private static final float MAX_ADVANCE = 32.0f;
    private final TextureManager textureManager;
    private final Identifier id;
    private BakedGlyph blankBakedGlyph;
    private BakedGlyph whiteRectangleBakedGlyph;
    private List<Font.FontFilterPair> allFonts = List.of();
    private List<Font> availableFonts = List.of();
    private final GlyphContainer<BakedGlyph> bakedGlyphCache = new GlyphContainer(BakedGlyph[]::new, rowCount -> new BakedGlyph[rowCount][]);
    private final GlyphContainer<GlyphPair> glyphCache = new GlyphContainer(GlyphPair[]::new, rowCount -> new GlyphPair[rowCount][]);
    private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<IntList>();
    private final List<GlyphAtlasTexture> glyphAtlases = Lists.newArrayList();
    private final IntFunction<GlyphPair> glyphFinder = this::findGlyph;
    private final IntFunction<BakedGlyph> glyphBaker = this::bake;

    public FontStorage(TextureManager textureManager, Identifier id) {
        this.textureManager = textureManager;
        this.id = id;
    }

    public void setFonts(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
        this.allFonts = allFonts;
        this.setActiveFilters(activeFilters);
    }

    public void setActiveFilters(Set<FontFilterType> activeFilters) {
        this.availableFonts = List.of();
        this.clear();
        this.availableFonts = this.applyFilters(this.allFonts, activeFilters);
    }

    private void clear() {
        this.closeGlyphAtlases();
        this.bakedGlyphCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankBakedGlyph = BuiltinEmptyGlyph.MISSING.bake(this::bake);
        this.whiteRectangleBakedGlyph = BuiltinEmptyGlyph.WHITE.bake(this::bake);
    }

    private List<Font> applyFilters(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
        IntOpenHashSet intSet = new IntOpenHashSet();
        ArrayList<Font> list2 = new ArrayList<Font>();
        for (Font.FontFilterPair lv : allFonts) {
            if (!lv.filter().isAllowed(activeFilters)) continue;
            list2.add(lv.provider());
            intSet.addAll(lv.provider().getProvidedGlyphs());
        }
        HashSet set2 = Sets.newHashSet();
        intSet.forEach(codePoint -> {
            for (Font lv : list2) {
                Glyph lv2 = lv.getGlyph(codePoint);
                if (lv2 == null) continue;
                set2.add(lv);
                if (lv2 == BuiltinEmptyGlyph.MISSING) break;
                this.charactersByWidth.computeIfAbsent(MathHelper.ceil(lv2.getAdvance(false)), i -> new IntArrayList()).add(codePoint);
                break;
            }
        });
        return list2.stream().filter(set2::contains).toList();
    }

    @Override
    public void close() {
        this.closeGlyphAtlases();
    }

    private void closeGlyphAtlases() {
        for (GlyphAtlasTexture lv : this.glyphAtlases) {
            lv.close();
        }
        this.glyphAtlases.clear();
    }

    private static boolean isAdvanceInvalid(Glyph glyph) {
        float f = glyph.getAdvance(false);
        if (f < 0.0f || f > 32.0f) {
            return true;
        }
        float g = glyph.getAdvance(true);
        return g < 0.0f || g > 32.0f;
    }

    private GlyphPair findGlyph(int codePoint) {
        Glyph lv = null;
        for (Font lv2 : this.availableFonts) {
            Glyph lv3 = lv2.getGlyph(codePoint);
            if (lv3 == null) continue;
            if (lv == null) {
                lv = lv3;
            }
            if (FontStorage.isAdvanceInvalid(lv3)) continue;
            return new GlyphPair(lv, lv3);
        }
        if (lv != null) {
            return new GlyphPair(lv, BuiltinEmptyGlyph.MISSING);
        }
        return GlyphPair.MISSING;
    }

    public Glyph getGlyph(int codePoint, boolean validateAdvance) {
        return this.glyphCache.computeIfAbsent(codePoint, this.glyphFinder).getGlyph(validateAdvance);
    }

    private BakedGlyph bake(int codePoint) {
        for (Font lv : this.availableFonts) {
            Glyph lv2 = lv.getGlyph(codePoint);
            if (lv2 == null) continue;
            return lv2.bake(this::bake);
        }
        LOGGER.warn("Couldn't find glyph for character {} (\\u{})", (Object)Character.toString(codePoint), (Object)String.format("%04x", codePoint));
        return this.blankBakedGlyph;
    }

    public BakedGlyph getBaked(int codePoint) {
        return this.bakedGlyphCache.computeIfAbsent(codePoint, this.glyphBaker);
    }

    private BakedGlyph bake(RenderableGlyph c) {
        for (GlyphAtlasTexture lv : this.glyphAtlases) {
            BakedGlyph lv2 = lv.bake(c);
            if (lv2 == null) continue;
            return lv2;
        }
        Identifier lv3 = this.id.withSuffixedPath("/" + this.glyphAtlases.size());
        boolean bl = c.hasColor();
        TextRenderLayerSet lv4 = bl ? TextRenderLayerSet.of(lv3) : TextRenderLayerSet.ofIntensity(lv3);
        GlyphAtlasTexture lv5 = new GlyphAtlasTexture(lv4, bl);
        this.glyphAtlases.add(lv5);
        this.textureManager.registerTexture(lv3, lv5);
        BakedGlyph lv6 = lv5.bake(c);
        return lv6 == null ? this.blankBakedGlyph : lv6;
    }

    public BakedGlyph getObfuscatedBakedGlyph(Glyph glyph) {
        IntList intList = (IntList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
        if (intList != null && !intList.isEmpty()) {
            return this.getBaked(intList.getInt(RANDOM.nextInt(intList.size())));
        }
        return this.blankBakedGlyph;
    }

    public Identifier getId() {
        return this.id;
    }

    public BakedGlyph getRectangleBakedGlyph() {
        return this.whiteRectangleBakedGlyph;
    }

    @Environment(value=EnvType.CLIENT)
    record GlyphPair(Glyph glyph, Glyph advanceValidatedGlyph) {
        static final GlyphPair MISSING = new GlyphPair(BuiltinEmptyGlyph.MISSING, BuiltinEmptyGlyph.MISSING);

        Glyph getGlyph(boolean validateAdvance) {
            return validateAdvance ? this.advanceValidatedGlyph : this.glyph;
        }
    }
}

