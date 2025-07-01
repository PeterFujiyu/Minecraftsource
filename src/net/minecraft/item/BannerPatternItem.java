/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public class BannerPatternItem
extends Item {
    private final TagKey<BannerPattern> patternItemTag;

    public BannerPatternItem(TagKey<BannerPattern> patternItemTag, Item.Settings settings) {
        super(settings);
        this.patternItemTag = patternItemTag;
    }

    public TagKey<BannerPattern> getPattern() {
        return this.patternItemTag;
    }
}

