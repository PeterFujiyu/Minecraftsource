/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityModel
extends EntityModel<MinecartEntityRenderState> {
    public MinecartEntityModel(ModelPart arg) {
        super(arg);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        lv2.addChild(EntityModelPartNames.BOTTOM, ModelPartBuilder.create().uv(0, 10).cuboid(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.addChild("front", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of(-9.0f, 4.0f, 0.0f, 0.0f, 4.712389f, 0.0f));
        lv2.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of(9.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        lv2.addChild("left", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, -7.0f, 0.0f, (float)Math.PI, 0.0f));
        lv2.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.pivot(0.0f, 4.0f, 7.0f));
        return TexturedModelData.of(lv, 64, 32);
    }
}

