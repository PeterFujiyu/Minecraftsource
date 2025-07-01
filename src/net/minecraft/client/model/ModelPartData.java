/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;

@Environment(value=EnvType.CLIENT)
public class ModelPartData {
    private final List<ModelCuboidData> cuboidData;
    private final ModelTransform rotationData;
    private final Map<String, ModelPartData> children = Maps.newHashMap();

    ModelPartData(List<ModelCuboidData> cuboidData, ModelTransform rotationData) {
        this.cuboidData = cuboidData;
        this.rotationData = rotationData;
    }

    public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform rotationData) {
        ModelPartData lv = new ModelPartData(builder.build(), rotationData);
        return this.addChild(name, lv);
    }

    public ModelPartData addChild(String name, ModelPartData data) {
        ModelPartData lv = this.children.put(name, data);
        if (lv != null) {
            data.children.putAll(lv.children);
        }
        return data;
    }

    public ModelPartData addChild(String name) {
        return this.addChild(name, ModelPartBuilder.create(), ModelTransform.NONE);
    }

    public ModelPart createPart(int textureWidth, int textureHeight) {
        Object2ObjectArrayMap object2ObjectArrayMap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight), (arg, arg2) -> arg, Object2ObjectArrayMap::new));
        List<ModelPart.Cuboid> list = this.cuboidData.stream().map(arg -> arg.createCuboid(textureWidth, textureHeight)).toList();
        ModelPart lv = new ModelPart(list, object2ObjectArrayMap);
        lv.setDefaultTransform(this.rotationData);
        lv.setTransform(this.rotationData);
        return lv;
    }

    public ModelPartData getChild(String name) {
        return this.children.get(name);
    }

    public Set<Map.Entry<String, ModelPartData>> getChildren() {
        return this.children.entrySet();
    }

    public ModelPartData applyTransformer(UnaryOperator<ModelTransform> transformer) {
        ModelPartData lv = new ModelPartData(this.cuboidData, (ModelTransform)transformer.apply(this.rotationData));
        lv.children.putAll(this.children);
        return lv;
    }
}

