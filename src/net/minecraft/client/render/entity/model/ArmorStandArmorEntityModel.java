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
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ArmorStandArmorEntityModel
extends BipedEntityModel<ArmorStandEntityRenderState> {
    public ArmorStandArmorEntityModel(ModelPart arg) {
        super(arg);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData lv = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.pivot(0.0f, 1.0f, 0.0f));
        lv3.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation.add(0.5f)), ModelTransform.NONE);
        lv2.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(-0.1f)), ModelTransform.pivot(-1.9f, 11.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(-0.1f)), ModelTransform.pivot(1.9f, 11.0f, 0.0f));
        return TexturedModelData.of(lv, 64, 32);
    }

    @Override
    public void setAngles(ArmorStandEntityRenderState arg) {
        super.setAngles(arg);
        this.head.pitch = (float)Math.PI / 180 * arg.headRotation.getPitch();
        this.head.yaw = (float)Math.PI / 180 * arg.headRotation.getYaw();
        this.head.roll = (float)Math.PI / 180 * arg.headRotation.getRoll();
        this.body.pitch = (float)Math.PI / 180 * arg.bodyRotation.getPitch();
        this.body.yaw = (float)Math.PI / 180 * arg.bodyRotation.getYaw();
        this.body.roll = (float)Math.PI / 180 * arg.bodyRotation.getRoll();
        this.leftArm.pitch = (float)Math.PI / 180 * arg.leftArmRotation.getPitch();
        this.leftArm.yaw = (float)Math.PI / 180 * arg.leftArmRotation.getYaw();
        this.leftArm.roll = (float)Math.PI / 180 * arg.leftArmRotation.getRoll();
        this.rightArm.pitch = (float)Math.PI / 180 * arg.rightArmRotation.getPitch();
        this.rightArm.yaw = (float)Math.PI / 180 * arg.rightArmRotation.getYaw();
        this.rightArm.roll = (float)Math.PI / 180 * arg.rightArmRotation.getRoll();
        this.leftLeg.pitch = (float)Math.PI / 180 * arg.leftLegRotation.getPitch();
        this.leftLeg.yaw = (float)Math.PI / 180 * arg.leftLegRotation.getYaw();
        this.leftLeg.roll = (float)Math.PI / 180 * arg.leftLegRotation.getRoll();
        this.rightLeg.pitch = (float)Math.PI / 180 * arg.rightLegRotation.getPitch();
        this.rightLeg.yaw = (float)Math.PI / 180 * arg.rightLegRotation.getYaw();
        this.rightLeg.roll = (float)Math.PI / 180 * arg.rightLegRotation.getRoll();
    }
}

