/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.item.ItemAssetsLoader;
import net.minecraft.client.model.ModelNameSupplier;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.ModelGrouper;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
implements ResourceReloader,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder MODELS_FINDER = ResourceFinder.json("models");
    private static final Map<Identifier, Identifier> LAYERS_TO_LOADERS = Map.of(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("banner_patterns"), TexturedRenderLayers.BEDS_ATLAS_TEXTURE, Identifier.ofVanilla("beds"), TexturedRenderLayers.CHEST_ATLAS_TEXTURE, Identifier.ofVanilla("chests"), TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("shield_patterns"), TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, Identifier.ofVanilla("signs"), TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, Identifier.ofVanilla("shulker_boxes"), TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE, Identifier.ofVanilla("armor_trims"), TexturedRenderLayers.DECORATED_POT_ATLAS_TEXTURE, Identifier.ofVanilla("decorated_pot"), SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("blocks"));
    private Map<ModelIdentifier, BakedModel> bakedBlockModels = Map.of();
    private Map<Identifier, ItemModel> bakedItemModels = Map.of();
    private Map<Identifier, ItemAsset.Properties> itemProperties = Map.of();
    private final SpriteAtlasManager atlasManager;
    private final BlockModels blockModelCache;
    private final BlockColors colorMap;
    private LoadedEntityModels entityModels = LoadedEntityModels.EMPTY;
    private LoadedBlockEntityModels blockEntityModels = LoadedBlockEntityModels.EMPTY;
    private int mipmapLevels;
    private BakedModel missingBlockModel;
    private ItemModel missingItemModel;
    private Object2IntMap<BlockState> modelGroups = Object2IntMaps.emptyMap();

    public BakedModelManager(TextureManager textureManager, BlockColors colorMap, int mipmap) {
        this.colorMap = colorMap;
        this.mipmapLevels = mipmap;
        this.blockModelCache = new BlockModels(this);
        this.atlasManager = new SpriteAtlasManager(LAYERS_TO_LOADERS, textureManager);
    }

    public BakedModel getModel(ModelIdentifier id) {
        return this.bakedBlockModels.getOrDefault(id, this.missingBlockModel);
    }

    public BakedModel getMissingBlockModel() {
        return this.missingBlockModel;
    }

    public ItemModel getItemModel(Identifier id) {
        return this.bakedItemModels.getOrDefault(id, this.missingItemModel);
    }

    public ItemAsset.Properties getItemProperties(Identifier id) {
        return this.itemProperties.getOrDefault(id, ItemAsset.Properties.DEFAULT);
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    public final CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
        UnbakedModel lv = MissingModel.create();
        CompletableFuture<LoadedEntityModels> completableFuture = CompletableFuture.supplyAsync(LoadedEntityModels::copy, prepareExecutor);
        CompletionStage completableFuture2 = completableFuture.thenApplyAsync(LoadedBlockEntityModels::fromModels, prepareExecutor);
        CompletableFuture<Map<Identifier, UnbakedModel>> completableFuture3 = BakedModelManager.reloadModels(manager, prepareExecutor);
        CompletableFuture<BlockStatesLoader.BlockStateDefinition> completableFuture4 = BlockStatesLoader.load(lv, manager, prepareExecutor);
        CompletableFuture<ItemAssetsLoader.Result> completableFuture5 = ItemAssetsLoader.load(manager, prepareExecutor);
        CompletionStage completableFuture6 = CompletableFuture.allOf(completableFuture3, completableFuture4, completableFuture5).thenApplyAsync(v -> BakedModelManager.collect(lv, (Map)completableFuture3.join(), (BlockStatesLoader.BlockStateDefinition)completableFuture4.join(), (ItemAssetsLoader.Result)completableFuture5.join()), prepareExecutor);
        CompletionStage completableFuture7 = completableFuture4.thenApplyAsync(definition -> BakedModelManager.group(this.colorMap, definition), prepareExecutor);
        Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>> map = this.atlasManager.reload(manager, this.mipmapLevels, prepareExecutor);
        return ((CompletableFuture)((CompletableFuture)((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])Stream.concat(map.values().stream(), Stream.of(completableFuture6, completableFuture7, completableFuture4, completableFuture5, completableFuture, completableFuture2)).toArray(CompletableFuture[]::new)).thenApplyAsync(v -> {
            Map<Identifier, SpriteAtlasManager.AtlasPreparation> map2 = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> (SpriteAtlasManager.AtlasPreparation)((CompletableFuture)entry.getValue()).join()));
            ReferencedModelsCollector lv = (ReferencedModelsCollector)((CompletableFuture)completableFuture6).join();
            Object2IntMap object2IntMap = (Object2IntMap)((CompletableFuture)completableFuture7).join();
            Set<Identifier> set = lv.getUnresolved();
            if (!set.isEmpty()) {
                LOGGER.debug("Unreferenced models: \n{}", (Object)set.stream().sorted().map(id -> "\t" + String.valueOf(id) + "\n").collect(Collectors.joining()));
            }
            ModelBaker lv2 = new ModelBaker((LoadedEntityModels)completableFuture.join(), ((BlockStatesLoader.BlockStateDefinition)completableFuture4.join()).getModels(), ((ItemAssetsLoader.Result)completableFuture5.join()).contents(), lv.getResolvedModels(), lv);
            return BakedModelManager.bake(Profilers.get(), map2, lv2, object2IntMap, (LoadedEntityModels)completableFuture.join(), (LoadedBlockEntityModels)((CompletableFuture)completableFuture2).join());
        }, prepareExecutor)).thenCompose(result -> result.readyForUpload.thenApply(void_ -> result))).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(bakingResult -> this.upload((BakingResult)bakingResult, Profilers.get()), applyExecutor);
    }

    private static CompletableFuture<Map<Identifier, UnbakedModel>> reloadModels(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> MODELS_FINDER.findResources(resourceManager), executor).thenCompose(models2 -> {
            ArrayList<CompletableFuture<Pair>> list = new ArrayList<CompletableFuture<Pair>>(models2.size());
            for (Map.Entry entry : models2.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    Pair<Identifier, JsonUnbakedModel> pair;
                    block8: {
                        Identifier lv = MODELS_FINDER.toResourceId((Identifier)entry.getKey());
                        BufferedReader reader = ((Resource)entry.getValue()).getReader();
                        try {
                            pair = Pair.of(lv, JsonUnbakedModel.deserialize(reader));
                            if (reader == null) break block8;
                        } catch (Throwable throwable) {
                            try {
                                if (reader != null) {
                                    try {
                                        ((Reader)reader).close();
                                    } catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            } catch (Exception exception) {
                                LOGGER.error("Failed to load model {}", entry.getKey(), (Object)exception);
                                return null;
                            }
                        }
                        ((Reader)reader).close();
                    }
                    return pair;
                }, executor));
            }
            return Util.combineSafe(list).thenApply(models -> models.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private static ReferencedModelsCollector collect(UnbakedModel missingModel, Map<Identifier, UnbakedModel> models, BlockStatesLoader.BlockStateDefinition blockStates, ItemAssetsLoader.Result itemAssets) {
        ReferencedModelsCollector lv = new ReferencedModelsCollector(models, missingModel);
        blockStates.streamModels().forEach(lv::add);
        itemAssets.contents().values().forEach(asset -> lv.add(asset.model()));
        lv.addGenerated();
        lv.resolveAll();
        return lv;
    }

    private static BakingResult bake(Profiler profiler, final Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlases, ModelBaker baker, Object2IntMap<BlockState> groups, LoadedEntityModels entityModels, LoadedBlockEntityModels blockEntityModels) {
        profiler.push("baking");
        final HashMultimap multimap = HashMultimap.create();
        final HashMultimap multimap2 = HashMultimap.create();
        final Sprite lv = atlases.get(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).getMissingSprite();
        ModelBaker.BakedModels lv2 = baker.bake(new ModelBaker.ErrorCollectingSpriteGetter(){

            @Override
            public Sprite get(ModelNameSupplier modelNameSupplier, SpriteIdentifier spriteId) {
                SpriteAtlasManager.AtlasPreparation lv3 = (SpriteAtlasManager.AtlasPreparation)atlases.get(spriteId.getAtlasId());
                Sprite lv2 = lv3.getSprite(spriteId.getTextureId());
                if (lv2 != null) {
                    return lv2;
                }
                multimap.put((String)modelNameSupplier.get(), spriteId);
                return lv3.getMissingSprite();
            }

            @Override
            public Sprite getMissing(ModelNameSupplier modelNameSupplier, String textureId) {
                multimap2.put((String)modelNameSupplier.get(), textureId);
                return lv;
            }
        });
        multimap.asMap().forEach((modelName, sprites) -> LOGGER.warn("Missing textures in model {}:\n{}", modelName, (Object)sprites.stream().sorted(SpriteIdentifier.COMPARATOR).map(spriteId -> "    " + String.valueOf(spriteId.getAtlasId()) + ":" + String.valueOf(spriteId.getTextureId())).collect(Collectors.joining("\n"))));
        multimap2.asMap().forEach((modelName, textureIds) -> LOGGER.warn("Missing texture references in model {}:\n{}", modelName, (Object)textureIds.stream().sorted().map(string -> "    " + string).collect(Collectors.joining("\n"))));
        profiler.swap("dispatch");
        Map<BlockState, BakedModel> map2 = BakedModelManager.toStateMap(lv2.blockStateModels(), lv2.missingModel());
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf((CompletableFuture[])atlases.values().stream().map(SpriteAtlasManager.AtlasPreparation::whenComplete).toArray(CompletableFuture[]::new));
        profiler.pop();
        return new BakingResult(lv2, groups, map2, atlases, entityModels, blockEntityModels, completableFuture);
    }

    private static Map<BlockState, BakedModel> toStateMap(Map<ModelIdentifier, BakedModel> blockStateModels, BakedModel missingModel) {
        IdentityHashMap<BlockState, BakedModel> map2 = new IdentityHashMap<BlockState, BakedModel>();
        for (Block lv : Registries.BLOCK) {
            lv.getStateManager().getStates().forEach(state -> {
                Identifier lv = state.getBlock().getRegistryEntry().registryKey().getValue();
                ModelIdentifier lv2 = BlockModels.getModelId(lv, state);
                BakedModel lv3 = (BakedModel)blockStateModels.get(lv2);
                if (lv3 == null) {
                    LOGGER.warn("Missing model for variant: '{}'", (Object)lv2);
                    map2.putIfAbsent((BlockState)state, missingModel);
                } else {
                    map2.put((BlockState)state, lv3);
                }
            });
        }
        return map2;
    }

    private static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.BlockStateDefinition definition) {
        return ModelGrouper.group(colors, definition);
    }

    private void upload(BakingResult bakingResult, Profiler profiler) {
        profiler.push("upload");
        bakingResult.atlasPreparations.values().forEach(SpriteAtlasManager.AtlasPreparation::upload);
        ModelBaker.BakedModels lv = bakingResult.bakedModels;
        this.bakedBlockModels = lv.blockStateModels();
        this.bakedItemModels = lv.itemStackModels();
        this.itemProperties = lv.itemProperties();
        this.modelGroups = bakingResult.modelGroups;
        this.missingBlockModel = lv.missingModel();
        this.missingItemModel = lv.missingItemModel();
        profiler.swap("cache");
        this.blockModelCache.setModels(bakingResult.modelCache);
        this.blockEntityModels = bakingResult.specialBlockModelRenderer;
        this.entityModels = bakingResult.entityModelSet;
        profiler.pop();
    }

    public boolean shouldRerender(BlockState from, BlockState to) {
        int j;
        if (from == to) {
            return false;
        }
        int i = this.modelGroups.getInt(from);
        if (i != -1 && i == (j = this.modelGroups.getInt(to))) {
            FluidState lv2;
            FluidState lv = from.getFluidState();
            return lv != (lv2 = to.getFluidState());
        }
        return true;
    }

    public SpriteAtlasTexture getAtlas(Identifier id) {
        return this.atlasManager.getAtlas(id);
    }

    @Override
    public void close() {
        this.atlasManager.close();
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.mipmapLevels = mipmapLevels;
    }

    public Supplier<LoadedBlockEntityModels> getBlockEntityModelsSupplier() {
        return () -> this.blockEntityModels;
    }

    public Supplier<LoadedEntityModels> getEntityModelsSupplier() {
        return () -> this.entityModels;
    }

    @Environment(value=EnvType.CLIENT)
    record BakingResult(ModelBaker.BakedModels bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BakedModel> modelCache, Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasPreparations, LoadedEntityModels entityModelSet, LoadedBlockEntityModels specialBlockModelRenderer, CompletableFuture<Void> readyForUpload) {
    }
}

