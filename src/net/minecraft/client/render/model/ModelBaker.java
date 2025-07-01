/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.model.ModelNameSupplier;
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelBaker {
    public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_0"));
    public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_1"));
    public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/lava_flow"));
    public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_flow"));
    public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_overlay"));
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/banner_base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base"));
    public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base_nopattern"));
    public static final int field_32983 = 10;
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(stage -> Identifier.ofVanilla("block/destroy_stage_" + stage)).collect(Collectors.toList());
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(id -> id.withPath(path -> "textures/" + path + ".png")).collect(Collectors.toList());
    public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getBlockBreaking).collect(Collectors.toList());
    static final Logger LOGGER = LogUtils.getLogger();
    private final LoadedEntityModels entityModels;
    final Map<BakedModelCacheKey, BakedModel> bakedModelCache = new HashMap<BakedModelCacheKey, BakedModel>();
    private final Map<ModelIdentifier, GroupableModel> blockModels;
    private final Map<Identifier, ItemAsset> itemAssets;
    final Map<Identifier, UnbakedModel> allModels;
    final UnbakedModel missingModel;

    public ModelBaker(LoadedEntityModels entityModels, Map<ModelIdentifier, GroupableModel> blockModels, Map<Identifier, ItemAsset> itemModels, Map<Identifier, UnbakedModel> allModels, UnbakedModel missingModel) {
        this.entityModels = entityModels;
        this.blockModels = blockModels;
        this.itemAssets = itemModels;
        this.allModels = allModels;
        this.missingModel = missingModel;
    }

    public BakedModels bake(ErrorCollectingSpriteGetter spriteGetter) {
        BakedModel lv = UnbakedModel.bake(this.missingModel, new BakerImpl(spriteGetter, () -> "missing"), ModelRotation.X0_Y0);
        HashMap<ModelIdentifier, BakedModel> map = new HashMap<ModelIdentifier, BakedModel>(this.blockModels.size());
        this.blockModels.forEach((id, model) -> {
            try {
                BakedModel lv = model.bake(new BakerImpl(spriteGetter, id::toString));
                map.put((ModelIdentifier)id, lv);
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", id, (Object)exception);
            }
        });
        MissingItemModel lv2 = new MissingItemModel(lv);
        HashMap<Identifier, ItemModel> map2 = new HashMap<Identifier, ItemModel>(this.itemAssets.size());
        HashMap<Identifier, ItemAsset.Properties> map3 = new HashMap<Identifier, ItemAsset.Properties>(this.itemAssets.size());
        this.itemAssets.forEach((id, item) -> {
            ModelNameSupplier lv = () -> String.valueOf(id) + "#inventory";
            BakerImpl lv2 = new BakerImpl(spriteGetter, lv);
            ItemModel.BakeContext lv3 = new ItemModel.BakeContext(lv2, this.entityModels, lv2);
            try {
                ItemModel lv4 = item.model().bake(lv3);
                map2.put((Identifier)id, lv4);
                if (!item.properties().equals(ItemAsset.Properties.DEFAULT)) {
                    map3.put((Identifier)id, item.properties());
                }
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake item model: '{}'", id, (Object)exception);
            }
        });
        return new BakedModels(lv, map, lv2, map2, map3);
    }

    @Environment(value=EnvType.CLIENT)
    class BakerImpl
    implements Baker {
        private final ModelNameSupplier modelNameSupplier;
        private final SpriteGetter spriteGetter;

        BakerImpl(ErrorCollectingSpriteGetter spriteGetter, ModelNameSupplier modelNameSupplier) {
            this.spriteGetter = spriteGetter.toSpriteGetter(modelNameSupplier);
            this.modelNameSupplier = modelNameSupplier;
        }

        @Override
        public SpriteGetter getSpriteGetter() {
            return this.spriteGetter;
        }

        private UnbakedModel getModel(Identifier id) {
            UnbakedModel lv = ModelBaker.this.allModels.get(id);
            if (lv == null) {
                LOGGER.warn("Requested a model that was not discovered previously: {}", (Object)id);
                return ModelBaker.this.missingModel;
            }
            return lv;
        }

        @Override
        public BakedModel bake(Identifier id, ModelBakeSettings settings) {
            BakedModelCacheKey lv = new BakedModelCacheKey(id, settings.getRotation(), settings.isUvLocked());
            BakedModel lv2 = ModelBaker.this.bakedModelCache.get(lv);
            if (lv2 != null) {
                return lv2;
            }
            UnbakedModel lv3 = this.getModel(id);
            BakedModel lv4 = UnbakedModel.bake(lv3, this, settings);
            ModelBaker.this.bakedModelCache.put(lv, lv4);
            return lv4;
        }

        @Override
        public ModelNameSupplier getModelNameSupplier() {
            return this.modelNameSupplier;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ErrorCollectingSpriteGetter {
        public Sprite get(ModelNameSupplier var1, SpriteIdentifier var2);

        public Sprite getMissing(ModelNameSupplier var1, String var2);

        default public SpriteGetter toSpriteGetter(final ModelNameSupplier modelNameSupplier) {
            return new SpriteGetter(){

                @Override
                public Sprite get(SpriteIdentifier spriteId) {
                    return this.get(modelNameSupplier, spriteId);
                }

                @Override
                public Sprite getMissing(String textureId) {
                    return this.getMissing(modelNameSupplier, textureId);
                }
            };
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BakedModels(BakedModel missingModel, Map<ModelIdentifier, BakedModel> blockStateModels, ItemModel missingItemModel, Map<Identifier, ItemModel> itemStackModels, Map<Identifier, ItemAsset.Properties> itemProperties) {
    }

    @Environment(value=EnvType.CLIENT)
    record BakedModelCacheKey(Identifier id, AffineTransformation transformation, boolean isUvLocked) {
    }
}

