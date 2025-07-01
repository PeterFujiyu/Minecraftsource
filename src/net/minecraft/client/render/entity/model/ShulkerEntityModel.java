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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel
extends EntityModel<ShulkerEntityRenderState> {
    public static final String LID = "lid";
    private static final String BASE = "base";
    private final ModelPart lid;
    private final ModelPart head;

    public ShulkerEntityModel(ModelPart arg) {
        super(arg, RenderLayer::getEntityCutoutNoCullZOffset);
        this.lid = arg.getChild(LID);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
    }

    private static ModelData getModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        lv2.addChild(LID, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        lv2.addChild(BASE, ModelPartBuilder.create().uv(0, 28).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        return lv;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = ShulkerEntityModel.getModelData();
        lv.getRoot().addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 52).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.pivot(0.0f, 12.0f, 0.0f));
        return TexturedModelData.of(lv, 64, 64);
    }

    public static TexturedModelData getShulkerBoxTexturedModelData() {
        ModelData lv = ShulkerEntityModel.getModelData();
        return TexturedModelData.of(lv, 64, 64);
    }

    @Override
    public void setAngles(ShulkerEntityRenderState arg) {
        super.setAngles(arg);
        float f = (0.5f + arg.openProgress) * (float)Math.PI;
        float g = -1.0f + MathHelper.sin(f);
        float h = 0.0f;
        if (f > (float)Math.PI) {
            h = MathHelper.sin(arg.age * 0.1f) * 0.7f;
        }
        this.lid.setPivot(0.0f, 16.0f + MathHelper.sin(f) * 8.0f + h, 0.0f);
        this.lid.yaw = arg.openProgress > 0.3f ? g * g * g * g * (float)Math.PI * 0.125f : 0.0f;
        this.head.pitch = arg.pitch * ((float)Math.PI / 180);
        this.head.yaw = (arg.headYaw - 180.0f - arg.shellYaw) * ((float)Math.PI / 180);
    }
}

