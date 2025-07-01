/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.util.collection.DataPool;

@Environment(value=EnvType.CLIENT)
public record WeightedUnbakedModel(List<ModelVariant> variants) implements GroupableModel
{
    public WeightedUnbakedModel {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Variant list must contain at least one element");
        }
    }

    @Override
    public Object getEqualityGroup(BlockState state) {
        return this;
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.variants.forEach(variant -> resolver.resolve(variant.location()));
    }

    @Override
    public BakedModel bake(Baker baker) {
        if (this.variants.size() == 1) {
            ModelVariant lv = this.variants.getFirst();
            return baker.bake(lv.location(), lv);
        }
        DataPool.Builder<BakedModel> lv2 = DataPool.builder();
        for (ModelVariant lv3 : this.variants) {
            BakedModel lv4 = baker.bake(lv3.location(), lv3);
            lv2.add(lv4, lv3.weight());
        }
        return new WeightedBakedModel(lv2.build());
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<WeightedUnbakedModel> {
        @Override
        public WeightedUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            ArrayList<ModelVariant> list = Lists.newArrayList();
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.isEmpty()) {
                    throw new JsonParseException("Empty variant array");
                }
                for (JsonElement jsonElement2 : jsonArray) {
                    list.add((ModelVariant)jsonDeserializationContext.deserialize(jsonElement2, (Type)((Object)ModelVariant.class)));
                }
            } else {
                list.add((ModelVariant)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)ModelVariant.class)));
            }
            return new WeightedUnbakedModel(list);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

