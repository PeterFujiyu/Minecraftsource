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
public class SmallPufferfishEntityModel
extends EntityModel<EntityRenderState> {
    private final ModelPart leftFin;
    private final ModelPart rightFin;

    public SmallPufferfishEntityModel(ModelPart arg) {
        super(arg);
        this.leftFin = arg.getChild(EntityModelPartNames.LEFT_FIN);
        this.rightFin = arg.getChild(EntityModelPartNames.RIGHT_FIN);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        int i = 23;
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 27).cuboid(-1.5f, -2.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, 23.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_EYE, ModelPartBuilder.create().uv(24, 6).cuboid(-1.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), ModelTransform.pivot(0.0f, 20.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_EYE, ModelPartBuilder.create().uv(28, 6).cuboid(0.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), ModelTransform.pivot(0.0f, 20.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.BACK_FIN, ModelPartBuilder.create().uv(-3, 0).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 0.0f, 3.0f), ModelTransform.pivot(0.0f, 22.0f, 1.5f));
        lv2.addChild(EntityModelPartNames.RIGHT_FIN, ModelPartBuilder.create().uv(25, 0).cuboid(-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), ModelTransform.pivot(-1.5f, 22.0f, -1.5f));
        lv2.addChild(EntityModelPartNames.LEFT_FIN, ModelPartBuilder.create().uv(25, 0).cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), ModelTransform.pivot(1.5f, 22.0f, -1.5f));
        return TexturedModelData.of(lv, 32, 32);
    }

    @Override
    public void setAngles(EntityRenderState state) {
        super.setAngles(state);
        this.rightFin.roll = -0.2f + 0.4f * MathHelper.sin(state.age * 0.2f);
        this.leftFin.roll = 0.2f - 0.4f * MathHelper.sin(state.age * 0.2f);
    }
}

