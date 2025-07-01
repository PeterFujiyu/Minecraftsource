/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.state.StateManager;

@Environment(value=EnvType.CLIENT)
public class MultipartUnbakedModel
implements GroupableModel {
    private final List<Selector> selectors;

    MultipartUnbakedModel(List<Selector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public Object getEqualityGroup(BlockState state) {
        IntArrayList intList = new IntArrayList();
        for (int i = 0; i < this.selectors.size(); ++i) {
            if (!this.selectors.get((int)i).predicate.test(state)) continue;
            intList.add(i);
        }
        @Environment(value=EnvType.CLIENT)
        record EqualityGroup(MultipartUnbakedModel model, IntList selectors) {
        }
        return new EqualityGroup(this, intList);
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.selectors.forEach(selector -> selector.variant.resolve(resolver));
    }

    @Override
    public BakedModel bake(Baker baker) {
        ArrayList<MultipartBakedModel.Selector> list = new ArrayList<MultipartBakedModel.Selector>(this.selectors.size());
        for (Selector lv : this.selectors) {
            BakedModel lv2 = lv.variant.bake(baker);
            list.add(new MultipartBakedModel.Selector(lv.predicate, lv2));
        }
        return new MultipartBakedModel(list);
    }

    @Environment(value=EnvType.CLIENT)
    record Selector(Predicate<BlockState> predicate, WeightedUnbakedModel variant) {
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<Serialized> {
        @Override
        public Serialized deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Serialized(this.deserializeComponents(jsonDeserializationContext, jsonElement.getAsJsonArray()));
        }

        private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext context, JsonArray array) {
            ArrayList<MultipartModelComponent> list = new ArrayList<MultipartModelComponent>();
            if (array.isEmpty()) {
                throw new JsonSyntaxException("Empty selector array");
            }
            for (JsonElement jsonElement : array) {
                list.add((MultipartModelComponent)context.deserialize(jsonElement, (Type)((Object)MultipartModelComponent.class)));
            }
            return list;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, type, context);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Serialized(List<MultipartModelComponent> selectors) {
        public MultipartUnbakedModel toModel(StateManager<Block, BlockState> stateManager) {
            List<Selector> list = this.selectors.stream().map(selector -> new Selector(selector.getPredicate(stateManager), selector.getModel())).toList();
            return new MultipartUnbakedModel(list);
        }

        public Set<WeightedUnbakedModel> getBackingModels() {
            return this.selectors.stream().map(MultipartModelComponent::getModel).collect(Collectors.toSet());
        }
    }
}

