/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BlockStatesLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder FINDER = ResourceFinder.json("blockstates");
    private static final String MAP_KEY = "map";
    private static final String MAP_TRUE_VARIANT = "map=true";
    private static final String MAP_FALSE_VARIANT = "map=false";
    private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_MANAGER = new StateManager.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(Block::getDefaultState, BlockState::new);
    private static final Identifier GLOW_ITEM_FRAME_ID = Identifier.ofVanilla("glow_item_frame");
    private static final Identifier ITEM_FRAME_ID = Identifier.ofVanilla("item_frame");
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = Map.of(ITEM_FRAME_ID, ITEM_FRAME_STATE_MANAGER, GLOW_ITEM_FRAME_ID, ITEM_FRAME_STATE_MANAGER);
    public static final ModelIdentifier MAP_GLOW_ITEM_FRAME_MODEL_ID = new ModelIdentifier(GLOW_ITEM_FRAME_ID, "map=true");
    public static final ModelIdentifier GLOW_ITEM_FRAME_MODEL_ID = new ModelIdentifier(GLOW_ITEM_FRAME_ID, "map=false");
    public static final ModelIdentifier MAP_ITEM_FRAME_MODEL_ID = new ModelIdentifier(ITEM_FRAME_ID, "map=true");
    public static final ModelIdentifier ITEM_FRAME_MODEL_ID = new ModelIdentifier(ITEM_FRAME_ID, "map=false");

    private static Function<Identifier, StateManager<Block, BlockState>> getIdToStatesConverter() {
        HashMap<Identifier, StateManager<Block, BlockState>> map = new HashMap<Identifier, StateManager<Block, BlockState>>(STATIC_DEFINITIONS);
        for (Block lv : Registries.BLOCK) {
            map.put(lv.getRegistryEntry().registryKey().getValue(), lv.getStateManager());
        }
        return map::get;
    }

    public static CompletableFuture<BlockStateDefinition> load(UnbakedModel missingModel, ResourceManager resourceManager, Executor executor) {
        Function<Identifier, StateManager<Block, BlockState>> function = BlockStatesLoader.getIdToStatesConverter();
        return CompletableFuture.supplyAsync(() -> FINDER.findAllResources(resourceManager), executor).thenCompose(resources -> {
            ArrayList<CompletableFuture<BlockStateDefinition>> list = new ArrayList<CompletableFuture<BlockStateDefinition>>(resources.size());
            for (Map.Entry entry : resources.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    Identifier lv = FINDER.toResourceId((Identifier)entry.getKey());
                    StateManager lv2 = (StateManager)function.apply(lv);
                    if (lv2 == null) {
                        LOGGER.debug("Discovered unknown block state definition {}, ignoring", (Object)lv);
                        return null;
                    }
                    List list = (List)entry.getValue();
                    ArrayList<PackBlockStateDefinition> list2 = new ArrayList<PackBlockStateDefinition>(list.size());
                    for (Resource lv3 : list) {
                        try {
                            BufferedReader reader = lv3.getReader();
                            try {
                                JsonObject jsonObject = JsonHelper.deserialize(reader);
                                ModelVariantMap lv4 = ModelVariantMap.fromJson(jsonObject);
                                list2.add(new PackBlockStateDefinition(lv3.getPackId(), lv4));
                            } finally {
                                if (reader == null) continue;
                                ((Reader)reader).close();
                            }
                        } catch (Exception exception) {
                            LOGGER.error("Failed to load blockstate definition {} from pack {}", lv, lv3.getPackId(), exception);
                        }
                    }
                    try {
                        return BlockStatesLoader.combine(lv, lv2, list2, missingModel);
                    } catch (Exception exception2) {
                        LOGGER.error("Failed to load blockstate definition {}", (Object)lv, (Object)exception2);
                        return null;
                    }
                }, executor));
            }
            return Util.combineSafe(list).thenApply(definitions -> {
                HashMap<ModelIdentifier, BlockModel> map = new HashMap<ModelIdentifier, BlockModel>();
                for (BlockStateDefinition lv : definitions) {
                    if (lv == null) continue;
                    map.putAll(lv.models());
                }
                return new BlockStateDefinition(map);
            });
        });
    }

    private static BlockStateDefinition combine(Identifier id, StateManager<Block, BlockState> stateManager, List<PackBlockStateDefinition> definitions, UnbakedModel missingModel) {
        HashMap<ModelIdentifier, BlockModel> map = new HashMap<ModelIdentifier, BlockModel>();
        for (PackBlockStateDefinition lv : definitions) {
            lv.contents.parse(stateManager, String.valueOf(id) + "/" + lv.source).forEach((state, model) -> {
                ModelIdentifier lv = BlockModels.getModelId(id, state);
                map.put(lv, new BlockModel((BlockState)state, (GroupableModel)model));
            });
        }
        return new BlockStateDefinition(map);
    }

    @Environment(value=EnvType.CLIENT)
    record PackBlockStateDefinition(String source, ModelVariantMap contents) {
    }

    @Environment(value=EnvType.CLIENT)
    public record BlockStateDefinition(Map<ModelIdentifier, BlockModel> models) {
        public Stream<ResolvableModel> streamModels() {
            return this.models.values().stream().map(BlockModel::model);
        }

        public Map<ModelIdentifier, GroupableModel> getModels() {
            return Maps.transformValues(this.models, BlockModel::model);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BlockModel(BlockState state, GroupableModel model) {
    }
}

