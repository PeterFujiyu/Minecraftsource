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
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MediumPufferfishEntityModel
extends EntityModel<EntityRenderState> {
    private final ModelPart leftBlueFin;
    private final ModelPart rightBlueFin;

    public MediumPufferfishEntityModel(ModelPart arg) {
        super(arg);
        this.leftBlueFin = arg.getChild(EntityModelPartNames.LEFT_BLUE_FIN);
        this.rightBlueFin = arg.getChild(EntityModelPartNames.RIGHT_BLUE_FIN);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        int i = 22;
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(12, 22).cuboid(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f), ModelTransform.pivot(0.0f, 22.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_BLUE_FIN, ModelPartBuilder.create().uv(24, 0).cuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.pivot(-2.5f, 18.0f, -1.5f));
        lv2.addChild(EntityModelPartNames.LEFT_BLUE_FIN, ModelPartBuilder.create().uv(24, 3).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.pivot(2.5f, 18.0f, -1.5f));
        lv2.addChild("top_front_fin", ModelPartBuilder.create().uv(19, 17).cuboid(-2.5f, -1.0f, 0.0f, 5.0f, 1.0f, 0.0f), ModelTransform.of(0.0f, 17.0f, -2.5f, 0.7853982f, 0.0f, 0.0f));
        lv2.addChild("top_back_fin", ModelPartBuilder.create().uv(11, 17).cuboid(-2.5f, -1.0f, 0.0f, 5.0f, 1.0f, 0.0f), ModelTransform.of(0.0f, 17.0f, 2.5f, -0.7853982f, 0.0f, 0.0f));
        lv2.addChild("right_front_fin", ModelPartBuilder.create().uv(5, 17).cuboid(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), ModelTransform.of(-2.5f, 22.0f, -2.5f, 0.0f, -0.7853982f, 0.0f));
        lv2.addChild("right_back_fin", ModelPartBuilder.create().uv(9, 17).cuboid(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), ModelTransform.of(-2.5f, 22.0f, 2.5f, 0.0f, 0.7853982f, 0.0f));
        lv2.addChild("left_back_fin", ModelPartBuilder.create().uv(1, 17).cuboid(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), ModelTransform.of(2.5f, 22.0f, 2.5f, 0.0f, -0.7853982f, 0.0f));
        lv2.addChild("left_front_fin", ModelPartBuilder.create().uv(1, 17).cuboid(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), ModelTransform.of(2.5f, 22.0f, -2.5f, 0.0f, 0.7853982f, 0.0f));
        lv2.addChild("bottom_back_fin", ModelPartBuilder.create().uv(18, 20).cuboid(0.0f, 0.0f, 0.0f, 5.0f, 1.0f, 0.0f), ModelTransform.of(-2.5f, 22.0f, 2.5f, 0.7853982f, 0.0f, 0.0f));
        lv2.addChild("bottom_front_fin", ModelPartBuilder.create().uv(17, 19).cuboid(-2.5f, 0.0f, 0.0f, 5.0f, 1.0f, 1.0f), ModelTransform.of(0.0f, 22.0f, -2.5f, -0.7853982f, 0.0f, 0.0f));
        return TexturedModelData.of(lv, 32, 32);
    }

    @Override
    public void setAngles(EntityRenderState state) {
        super.setAngles(state);
        this.rightBlueFin.roll = -0.2f + 0.4f * MathHelper.sin(state.age * 0.2f);
        this.leftBlueFin.roll = 0.2f - 0.4f * MathHelper.sin(state.age * 0.2f);
    }
}

