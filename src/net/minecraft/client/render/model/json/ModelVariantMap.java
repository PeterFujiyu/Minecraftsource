/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.BlockPropertiesPredicate;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelVariantMap {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)ModelVariantMap.class), new Deserializer()).registerTypeAdapter((Type)((Object)ModelVariant.class), new ModelVariant.Deserializer()).registerTypeAdapter((Type)((Object)WeightedUnbakedModel.class), new WeightedUnbakedModel.Deserializer()).registerTypeAdapter((Type)((Object)MultipartUnbakedModel.Serialized.class), new MultipartUnbakedModel.Deserializer()).registerTypeAdapter((Type)((Object)MultipartModelComponent.class), new MultipartModelComponent.Deserializer()).create();
    private final Map<String, WeightedUnbakedModel> variantMap;
    @Nullable
    private final MultipartUnbakedModel.Serialized multipartModel;

    public static ModelVariantMap fromJson(Reader reader) {
        return JsonHelper.deserialize(GSON, reader, ModelVariantMap.class);
    }

    public static ModelVariantMap fromJson(JsonElement json) {
        return GSON.fromJson(json, ModelVariantMap.class);
    }

    public ModelVariantMap(Map<String, WeightedUnbakedModel> variantMap, @Nullable MultipartUnbakedModel.Serialized multipartModel) {
        this.multipartModel = multipartModel;
        this.variantMap = variantMap;
    }

    @VisibleForTesting
    public WeightedUnbakedModel getVariant(String key) {
        WeightedUnbakedModel lv = this.variantMap.get(key);
        if (lv == null) {
            throw new VariantAbsentException();
        }
        return lv;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ModelVariantMap) {
            ModelVariantMap lv = (ModelVariantMap)o;
            return this.variantMap.equals(lv.variantMap) && Objects.equals(this.multipartModel, lv.multipartModel);
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.variantMap.hashCode() + (this.multipartModel != null ? this.multipartModel.hashCode() : 0);
    }

    @VisibleForTesting
    public Set<WeightedUnbakedModel> getAllModels() {
        HashSet<WeightedUnbakedModel> set = Sets.newHashSet(this.variantMap.values());
        if (this.multipartModel != null) {
            set.addAll(this.multipartModel.getBackingModels());
        }
        return set;
    }

    @Nullable
    public MultipartUnbakedModel.Serialized getMultipartModel() {
        return this.multipartModel;
    }

    public Map<BlockState, GroupableModel> parse(StateManager<Block, BlockState> stateManager, String path) {
        MultipartUnbakedModel lv;
        IdentityHashMap<BlockState, GroupableModel> map = new IdentityHashMap<BlockState, GroupableModel>();
        ImmutableList<BlockState> list = stateManager.getStates();
        if (this.multipartModel != null) {
            lv = this.multipartModel.toModel(stateManager);
            list.forEach(state -> map.put((BlockState)state, lv));
        } else {
            lv = null;
        }
        this.variantMap.forEach((key, model) -> {
            try {
                list.stream().filter(BlockPropertiesPredicate.parse(stateManager, key)).forEach(state -> {
                    GroupableModel lv = map.put((BlockState)state, (GroupableModel)model);
                    if (lv != null && lv != lv) {
                        String string = (String)this.variantMap.entrySet().stream().filter(entry -> entry.getValue() == lv).findFirst().get().getKey();
                        throw new RuntimeException("Overlapping definition with: " + string);
                    }
                });
            } catch (Exception exception) {
                LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", path, key, exception.getMessage());
            }
        });
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    protected static class VariantAbsentException
    extends RuntimeException {
        protected VariantAbsentException() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelVariantMap> {
        @Override
        public ModelVariantMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, WeightedUnbakedModel> map = this.variantsFromJson(jsonDeserializationContext, jsonObject);
            MultipartUnbakedModel.Serialized lv = this.multipartFromJson(jsonDeserializationContext, jsonObject);
            if (map.isEmpty() && lv == null) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new ModelVariantMap(map, lv);
        }

        protected Map<String, WeightedUnbakedModel> variantsFromJson(JsonDeserializationContext context, JsonObject object) {
            HashMap<String, WeightedUnbakedModel> map = Maps.newHashMap();
            if (object.has("variants")) {
                JsonObject jsonObject2 = JsonHelper.getObject(object, "variants");
                for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                    map.put(entry.getKey(), (WeightedUnbakedModel)context.deserialize(entry.getValue(), (Type)((Object)WeightedUnbakedModel.class)));
                }
            }
            return map;
        }

        @Nullable
        protected MultipartUnbakedModel.Serialized multipartFromJson(JsonDeserializationContext context, JsonObject object) {
            if (!object.has("multipart")) {
                return null;
            }
            JsonArray jsonArray = JsonHelper.getArray(object, "multipart");
            return (MultipartUnbakedModel.Serialized)context.deserialize(jsonArray, (Type)((Object)MultipartUnbakedModel.Serialized.class));
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

