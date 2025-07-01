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
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(value=EnvType.CLIENT)
public class LeashKnotEntityModel
extends EntityModel<EntityRenderState> {
    private static final String KNOT = "knot";
    private final ModelPart knot;

    public LeashKnotEntityModel(ModelPart arg) {
        super(arg);
        this.knot = arg.getChild(KNOT);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        lv2.addChild(KNOT, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -8.0f, -3.0f, 6.0f, 8.0f, 6.0f), ModelTransform.NONE);
        return TexturedModelData.of(lv, 32, 32);
    }
}

