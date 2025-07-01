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
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class Deadmau5EarsEntityModel
extends BipedEntityModel<PlayerEntityRenderState> {
    public Deadmau5EarsEntityModel(ModelPart arg) {
        super(arg);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild("head");
        lv3.addChild("hat");
        lv2.addChild("body");
        lv2.addChild("left_arm");
        lv2.addChild("right_arm");
        lv2.addChild("left_leg");
        lv2.addChild("right_leg");
        ModelPartBuilder lv4 = ModelPartBuilder.create().uv(24, 0).cuboid(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, new Dilation(1.0f));
        lv3.addChild(EntityModelPartNames.LEFT_EAR, lv4, ModelTransform.pivot(-6.0f, -6.0f, 0.0f));
        lv3.addChild(EntityModelPartNames.RIGHT_EAR, lv4, ModelTransform.pivot(6.0f, -6.0f, 0.0f));
        return TexturedModelData.of(lv, 64, 64);
    }
}

