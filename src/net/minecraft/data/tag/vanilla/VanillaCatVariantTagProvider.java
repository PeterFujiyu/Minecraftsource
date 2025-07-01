/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.TagProvider;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.CatVariantTags;

public class VanillaCatVariantTagProvider
extends TagProvider<CatVariant> {
    public VanillaCatVariantTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.CAT_VARIANT, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        this.getOrCreateTagBuilder(CatVariantTags.DEFAULT_SPAWNS).add(CatVariant.TABBY, CatVariant.BLACK, CatVariant.RED, CatVariant.SIAMESE, CatVariant.BRITISH_SHORTHAIR, CatVariant.CALICO, CatVariant.PERSIAN, CatVariant.RAGDOLL, CatVariant.WHITE, CatVariant.JELLIE);
        this.getOrCreateTagBuilder(CatVariantTags.FULL_MOON_SPAWNS).addTag(CatVariantTags.DEFAULT_SPAWNS).add(CatVariant.ALL_BLACK);
    }
}

