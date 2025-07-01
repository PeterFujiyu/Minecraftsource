/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TexturedRenderLayers {
    public static final Identifier SHULKER_BOXES_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/shulker_boxes.png");
    public static final Identifier BEDS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/beds.png");
    public static final Identifier BANNER_PATTERNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/banner_patterns.png");
    public static final Identifier SHIELD_PATTERNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/shield_patterns.png");
    public static final Identifier SIGNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/signs.png");
    public static final Identifier CHEST_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/chest.png");
    public static final Identifier ARMOR_TRIMS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/armor_trims.png");
    public static final Identifier DECORATED_POT_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/decorated_pot.png");
    private static final RenderLayer SHULKER_BOXES_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SHULKER_BOXES_ATLAS_TEXTURE);
    private static final RenderLayer BEDS_RENDER_LAYER = RenderLayer.getEntitySolid(BEDS_ATLAS_TEXTURE);
    private static final RenderLayer BANNER_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(BANNER_PATTERNS_ATLAS_TEXTURE);
    private static final RenderLayer SHIELD_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(SHIELD_PATTERNS_ATLAS_TEXTURE);
    private static final RenderLayer SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SIGNS_ATLAS_TEXTURE);
    private static final RenderLayer CHEST_RENDER_LAYER = RenderLayer.getEntityCutout(CHEST_ATLAS_TEXTURE);
    private static final RenderLayer ARMOR_TRIMS_RENDER_LAYER = RenderLayer.getArmorCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
    private static final RenderLayer ARMOR_TRIMS_DECAL_RENDER_LAYER = RenderLayer.createArmorDecalCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
    private static final RenderLayer ENTITY_SOLID = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    private static final RenderLayer ENTITY_CUTOUT = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    private static final RenderLayer ITEM_ENTITY_TRANSLUCENT_CULL = RenderLayer.getItemEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    public static final SpriteIdentifier SHULKER_TEXTURE_ID = TexturedRenderLayers.createShulkerBoxTextureId(Identifier.ofVanilla("shulker"));
    public static final List<SpriteIdentifier> COLORED_SHULKER_BOXES_TEXTURES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(TexturedRenderLayers::createShulkerBoxTextureId).collect(ImmutableList.toImmutableList());
    public static final Map<WoodType, SpriteIdentifier> SIGN_TYPE_TEXTURES = WoodType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createSignTextureId));
    public static final Map<WoodType, SpriteIdentifier> HANGING_SIGN_TYPE_TEXTURES = WoodType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createHangingSignTextureId));
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/banner/base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield/base"));
    private static final Map<Identifier, SpriteIdentifier> BANNER_PATTERN_TEXTURES = new HashMap<Identifier, SpriteIdentifier>();
    private static final Map<Identifier, SpriteIdentifier> SHIELD_PATTERN_TEXTURES = new HashMap<Identifier, SpriteIdentifier>();
    public static final Map<RegistryKey<DecoratedPotPattern>, SpriteIdentifier> DECORATED_POT_PATTERN_TEXTURES = Registries.DECORATED_POT_PATTERN.streamEntries().collect(Collectors.toMap(RegistryEntry.Reference::registryKey, pattern -> TexturedRenderLayers.createDecoratedPotPatternTextureId(((DecoratedPotPattern)pattern.value()).assetId())));
    public static final SpriteIdentifier DECORATED_POT_BASE = TexturedRenderLayers.createDecoratedPotPatternTextureId(Identifier.ofVanilla("decorated_pot_base"));
    public static final SpriteIdentifier DECORATED_POT_SIDE = TexturedRenderLayers.createDecoratedPotPatternTextureId(Identifier.ofVanilla("decorated_pot_side"));
    private static final SpriteIdentifier[] BED_TEXTURES = (SpriteIdentifier[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(TexturedRenderLayers::createBedTextureId).toArray(SpriteIdentifier[]::new);
    public static final SpriteIdentifier TRAPPED = TexturedRenderLayers.createChestTextureId("trapped");
    public static final SpriteIdentifier TRAPPED_LEFT = TexturedRenderLayers.createChestTextureId("trapped_left");
    public static final SpriteIdentifier TRAPPED_RIGHT = TexturedRenderLayers.createChestTextureId("trapped_right");
    public static final SpriteIdentifier CHRISTMAS = TexturedRenderLayers.createChestTextureId("christmas");
    public static final SpriteIdentifier CHRISTMAS_LEFT = TexturedRenderLayers.createChestTextureId("christmas_left");
    public static final SpriteIdentifier CHRISTMAS_RIGHT = TexturedRenderLayers.createChestTextureId("christmas_right");
    public static final SpriteIdentifier NORMAL = TexturedRenderLayers.createChestTextureId("normal");
    public static final SpriteIdentifier NORMAL_LEFT = TexturedRenderLayers.createChestTextureId("normal_left");
    public static final SpriteIdentifier NORMAL_RIGHT = TexturedRenderLayers.createChestTextureId("normal_right");
    public static final SpriteIdentifier ENDER = TexturedRenderLayers.createChestTextureId("ender");

    public static RenderLayer getBannerPatterns() {
        return BANNER_PATTERNS_RENDER_LAYER;
    }

    public static RenderLayer getShieldPatterns() {
        return SHIELD_PATTERNS_RENDER_LAYER;
    }

    public static RenderLayer getBeds() {
        return BEDS_RENDER_LAYER;
    }

    public static RenderLayer getShulkerBoxes() {
        return SHULKER_BOXES_RENDER_LAYER;
    }

    public static RenderLayer getSign() {
        return SIGN_RENDER_LAYER;
    }

    public static RenderLayer getHangingSign() {
        return SIGN_RENDER_LAYER;
    }

    public static RenderLayer getChest() {
        return CHEST_RENDER_LAYER;
    }

    public static RenderLayer getArmorTrims(boolean decal) {
        return decal ? ARMOR_TRIMS_DECAL_RENDER_LAYER : ARMOR_TRIMS_RENDER_LAYER;
    }

    public static RenderLayer getEntitySolid() {
        return ENTITY_SOLID;
    }

    public static RenderLayer getEntityCutout() {
        return ENTITY_CUTOUT;
    }

    public static RenderLayer getItemEntityTranslucentCull() {
        return ITEM_ENTITY_TRANSLUCENT_CULL;
    }

    public static SpriteIdentifier getBedTextureId(DyeColor color) {
        return BED_TEXTURES[color.getId()];
    }

    public static Identifier createColorId(DyeColor color) {
        return Identifier.ofVanilla(color.getName());
    }

    public static SpriteIdentifier createBedTextureId(DyeColor color) {
        return TexturedRenderLayers.createBedTextureId(TexturedRenderLayers.createColorId(color));
    }

    public static SpriteIdentifier createBedTextureId(Identifier id) {
        return new SpriteIdentifier(BEDS_ATLAS_TEXTURE, id.withPrefixedPath("entity/bed/"));
    }

    public static SpriteIdentifier getShulkerBoxTextureId(DyeColor color) {
        return COLORED_SHULKER_BOXES_TEXTURES.get(color.getId());
    }

    public static Identifier createShulkerId(DyeColor color) {
        return Identifier.ofVanilla("shulker_" + color.getName());
    }

    public static SpriteIdentifier createShulkerBoxTextureId(DyeColor color) {
        return TexturedRenderLayers.createShulkerBoxTextureId(TexturedRenderLayers.createShulkerId(color));
    }

    public static SpriteIdentifier createShulkerBoxTextureId(Identifier id) {
        return new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, id.withPrefixedPath("entity/shulker/"));
    }

    private static SpriteIdentifier createSignTextureId(WoodType type) {
        return TexturedRenderLayers.createSignTextureId(Identifier.ofVanilla(type.name()));
    }

    public static SpriteIdentifier createSignTextureId(Identifier id) {
        return new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, id.withPrefixedPath("entity/signs/"));
    }

    private static SpriteIdentifier createHangingSignTextureId(WoodType type) {
        return TexturedRenderLayers.createHangingSignTextureId(Identifier.ofVanilla(type.name()));
    }

    public static SpriteIdentifier createHangingSignTextureId(Identifier id) {
        return new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, id.withPrefixedPath("entity/signs/hanging/"));
    }

    public static SpriteIdentifier getSignTextureId(WoodType signType) {
        return SIGN_TYPE_TEXTURES.get(signType);
    }

    public static SpriteIdentifier getHangingSignTextureId(WoodType signType) {
        return HANGING_SIGN_TYPE_TEXTURES.get(signType);
    }

    public static SpriteIdentifier getBannerPatternTextureId(RegistryEntry<BannerPattern> pattern) {
        return BANNER_PATTERN_TEXTURES.computeIfAbsent(pattern.value().assetId(), id -> {
            Identifier lv = id.withPrefixedPath("entity/banner/");
            return new SpriteIdentifier(BANNER_PATTERNS_ATLAS_TEXTURE, lv);
        });
    }

    public static SpriteIdentifier getShieldPatternTextureId(RegistryEntry<BannerPattern> pattern) {
        return SHIELD_PATTERN_TEXTURES.computeIfAbsent(pattern.value().assetId(), id -> {
            Identifier lv = id.withPrefixedPath("entity/shield/");
            return new SpriteIdentifier(SHIELD_PATTERNS_ATLAS_TEXTURE, lv);
        });
    }

    private static SpriteIdentifier createChestTextureId(String variant) {
        return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Identifier.ofVanilla("entity/chest/" + variant));
    }

    public static SpriteIdentifier createChestTextureId(Identifier id) {
        return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, id.withPrefixedPath("entity/chest/"));
    }

    private static SpriteIdentifier createDecoratedPotPatternTextureId(Identifier patternId) {
        return new SpriteIdentifier(DECORATED_POT_ATLAS_TEXTURE, patternId.withPrefixedPath("entity/decorated_pot/"));
    }

    @Nullable
    public static SpriteIdentifier getDecoratedPotPatternTextureId(@Nullable RegistryKey<DecoratedPotPattern> potPatternKey) {
        if (potPatternKey == null) {
            return null;
        }
        return DECORATED_POT_PATTERN_TEXTURES.get(potPatternKey);
    }

    public static SpriteIdentifier getChestTextureId(BlockEntity blockEntity, ChestType type, boolean christmas) {
        if (blockEntity instanceof EnderChestBlockEntity) {
            return ENDER;
        }
        if (christmas) {
            return TexturedRenderLayers.getChestTextureId(type, CHRISTMAS, CHRISTMAS_LEFT, CHRISTMAS_RIGHT);
        }
        if (blockEntity instanceof TrappedChestBlockEntity) {
            return TexturedRenderLayers.getChestTextureId(type, TRAPPED, TRAPPED_LEFT, TRAPPED_RIGHT);
        }
        return TexturedRenderLayers.getChestTextureId(type, NORMAL, NORMAL_LEFT, NORMAL_RIGHT);
    }

    private static SpriteIdentifier getChestTextureId(ChestType type, SpriteIdentifier single, SpriteIdentifier left, SpriteIdentifier right) {
        switch (type) {
            case LEFT: {
                return left;
            }
            case RIGHT: {
                return right;
            }
        }
        return single;
    }
}

