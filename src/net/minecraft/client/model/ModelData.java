/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

@Environment(value=EnvType.CLIENT)
public class ModelData {
    private final ModelPartData data;

    public ModelData() {
        this(new ModelPartData(ImmutableList.of(), ModelTransform.NONE));
    }

    private ModelData(ModelPartData data) {
        this.data = data;
    }

    public ModelPartData getRoot() {
        return this.data;
    }

    public ModelData transform(UnaryOperator<ModelTransform> transformer) {
        return new ModelData(this.data.applyTransformer(transformer));
    }
}

