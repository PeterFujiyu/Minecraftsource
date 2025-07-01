/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;

@Environment(value=EnvType.CLIENT)
public class LoadedEntityModels {
    public static final LoadedEntityModels EMPTY = new LoadedEntityModels(Map.of());
    private final Map<EntityModelLayer, TexturedModelData> modelParts;

    public LoadedEntityModels(Map<EntityModelLayer, TexturedModelData> modelParts) {
        this.modelParts = modelParts;
    }

    public ModelPart getModelPart(EntityModelLayer layer) {
        TexturedModelData lv = this.modelParts.get(layer);
        if (lv == null) {
            throw new IllegalArgumentException("No model for layer " + String.valueOf(layer));
        }
        return lv.createModel();
    }

    public static LoadedEntityModels copy() {
        return new LoadedEntityModels(ImmutableMap.copyOf(EntityModels.getModels()));
    }
}

