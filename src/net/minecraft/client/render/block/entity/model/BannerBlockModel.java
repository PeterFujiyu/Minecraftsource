/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;

@Environment(value=EnvType.CLIENT)
public class BannerBlockModel
extends Model {
    public static final int field_55108 = 20;
    public static final int field_55109 = 40;
    public static final String FLAG = "flag";
    private static final String POLE = "pole";
    private static final String BAR = "bar";

    public BannerBlockModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
    }

    public static TexturedModelData getTexturedModelData(boolean standing) {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        if (standing) {
            lv2.addChild(POLE, ModelPartBuilder.create().uv(44, 0).cuboid(-1.0f, -42.0f, -1.0f, 2.0f, 42.0f, 2.0f), ModelTransform.NONE);
        }
        lv2.addChild(BAR, ModelPartBuilder.create().uv(0, 42).cuboid(-10.0f, standing ? -44.0f : -20.5f, standing ? -1.0f : 9.5f, 20.0f, 2.0f, 2.0f), ModelTransform.NONE);
        return TexturedModelData.of(lv, 64, 64);
    }
}

