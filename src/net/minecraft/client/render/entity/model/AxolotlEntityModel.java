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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityModel
extends EntityModel<AxolotlEntityRenderState> {
    public static final float MOVING_IN_WATER_LEG_PITCH = 1.8849558f;
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    private final ModelPart tail;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart topGills;
    private final ModelPart leftGills;
    private final ModelPart rightGills;

    public AxolotlEntityModel(ModelPart arg) {
        super(arg);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
        this.topGills = this.head.getChild(EntityModelPartNames.TOP_GILLS);
        this.leftGills = this.head.getChild(EntityModelPartNames.LEFT_GILLS);
        this.rightGills = this.head.getChild(EntityModelPartNames.RIGHT_GILLS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 11).cuboid(-4.0f, -2.0f, -9.0f, 8.0f, 4.0f, 10.0f).uv(2, 17).cuboid(0.0f, -3.0f, -8.0f, 0.0f, 5.0f, 9.0f), ModelTransform.pivot(0.0f, 20.0f, 5.0f));
        Dilation lv4 = new Dilation(0.001f);
        ModelPartData lv5 = lv3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 1).cuboid(-4.0f, -3.0f, -5.0f, 8.0f, 5.0f, 5.0f, lv4), ModelTransform.pivot(0.0f, 0.0f, -9.0f));
        ModelPartBuilder lv6 = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0f, -3.0f, 0.0f, 8.0f, 3.0f, 0.0f, lv4);
        ModelPartBuilder lv7 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, lv4);
        ModelPartBuilder lv8 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, lv4);
        lv5.addChild(EntityModelPartNames.TOP_GILLS, lv6, ModelTransform.pivot(0.0f, -3.0f, -1.0f));
        lv5.addChild(EntityModelPartNames.LEFT_GILLS, lv7, ModelTransform.pivot(-4.0f, 0.0f, -1.0f));
        lv5.addChild(EntityModelPartNames.RIGHT_GILLS, lv8, ModelTransform.pivot(4.0f, 0.0f, -1.0f));
        ModelPartBuilder lv9 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, lv4);
        ModelPartBuilder lv10 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, lv4);
        lv3.addChild(EntityModelPartNames.RIGHT_HIND_LEG, lv10, ModelTransform.pivot(-3.5f, 1.0f, -1.0f));
        lv3.addChild(EntityModelPartNames.LEFT_HIND_LEG, lv9, ModelTransform.pivot(3.5f, 1.0f, -1.0f));
        lv3.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, lv10, ModelTransform.pivot(-3.5f, 1.0f, -8.0f));
        lv3.addChild(EntityModelPartNames.LEFT_FRONT_LEG, lv9, ModelTransform.pivot(3.5f, 1.0f, -8.0f));
        lv3.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(2, 19).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 5.0f, 12.0f), ModelTransform.pivot(0.0f, 0.0f, 1.0f));
        return TexturedModelData.of(lv, 64, 64);
    }

    @Override
    public void setAngles(AxolotlEntityRenderState arg) {
        super.setAngles(arg);
        float f = arg.playingDeadValue;
        float g = arg.inWaterValue;
        float h = arg.onGroundValue;
        float i = arg.isMovingValue;
        float j = 1.0f - i;
        float k = 1.0f - Math.min(h, i);
        this.body.yaw += arg.yawDegrees * ((float)Math.PI / 180);
        this.setMovingInWaterAngles(arg.age, arg.pitch, Math.min(i, g));
        this.setStandingInWaterAngles(arg.age, Math.min(j, g));
        this.setMovingOnGroundAngles(arg.age, Math.min(i, h));
        this.setStandingOnGroundAngles(arg.age, Math.min(j, h));
        this.setPlayingDeadAngles(f);
        this.copyLegAngles(k);
    }

    private void setStandingOnGroundAngles(float animationProgress, float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        float h = animationProgress * 0.09f;
        float i = MathHelper.sin(h);
        float j = MathHelper.cos(h);
        float k = i * i - 2.0f * i;
        float l = j * j - 3.0f * i;
        this.head.pitch += -0.09f * k * headYaw;
        this.head.roll += -0.2f * headYaw;
        this.tail.yaw += (-0.1f + 0.1f * k) * headYaw;
        float m = (0.6f + 0.05f * l) * headYaw;
        this.topGills.pitch += m;
        this.leftGills.yaw -= m;
        this.rightGills.yaw += m;
        this.leftHindLeg.pitch += 1.1f * headYaw;
        this.leftHindLeg.yaw += 1.0f * headYaw;
        this.leftFrontLeg.pitch += 0.8f * headYaw;
        this.leftFrontLeg.yaw += 2.3f * headYaw;
        this.leftFrontLeg.roll -= 0.5f * headYaw;
    }

    private void setMovingOnGroundAngles(float animationProgress, float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        float h = animationProgress * 0.11f;
        float i = MathHelper.cos(h);
        float j = (i * i - 2.0f * i) / 5.0f;
        float k = 0.7f * i;
        float l = 0.09f * i * headYaw;
        this.head.yaw += l;
        this.tail.yaw += l;
        float m = (0.6f - 0.08f * (i * i + 2.0f * MathHelper.sin(h))) * headYaw;
        this.topGills.pitch += m;
        this.leftGills.yaw -= m;
        this.rightGills.yaw += m;
        float n = 0.9424779f * headYaw;
        float o = 1.0995574f * headYaw;
        this.leftHindLeg.pitch += n;
        this.leftHindLeg.yaw += (1.5f - j) * headYaw;
        this.leftHindLeg.roll += -0.1f * headYaw;
        this.leftFrontLeg.pitch += o;
        this.leftFrontLeg.yaw += (1.5707964f - k) * headYaw;
        this.rightHindLeg.pitch += n;
        this.rightHindLeg.yaw += (-1.0f - j) * headYaw;
        this.rightFrontLeg.pitch += o;
        this.rightFrontLeg.yaw += (-1.5707964f - k) * headYaw;
    }

    private void setStandingInWaterAngles(float f, float g) {
        if (g <= 1.0E-5f) {
            return;
        }
        float h = f * 0.075f;
        float i = MathHelper.cos(h);
        float j = MathHelper.sin(h) * 0.15f;
        float k = (-0.15f + 0.075f * i) * g;
        this.body.pitch += k;
        this.body.pivotY -= j * g;
        this.head.pitch -= k;
        this.topGills.pitch += 0.2f * i * g;
        float l = (-0.3f * i - 0.19f) * g;
        this.leftGills.yaw += l;
        this.rightGills.yaw -= l;
        this.leftHindLeg.pitch += (2.3561945f - i * 0.11f) * g;
        this.leftHindLeg.yaw += 0.47123894f * g;
        this.leftHindLeg.roll += 1.7278761f * g;
        this.leftFrontLeg.pitch += (0.7853982f - i * 0.2f) * g;
        this.leftFrontLeg.yaw += 2.042035f * g;
        this.tail.yaw += 0.5f * i * g;
    }

    private void setMovingInWaterAngles(float f, float headPitch, float h) {
        if (h <= 1.0E-5f) {
            return;
        }
        float i = f * 0.33f;
        float j = MathHelper.sin(i);
        float k = MathHelper.cos(i);
        float l = 0.13f * j;
        this.body.pitch += (headPitch * ((float)Math.PI / 180) + l) * h;
        this.head.pitch -= l * 1.8f * h;
        this.body.pivotY -= 0.45f * k * h;
        this.topGills.pitch += (-0.5f * j - 0.8f) * h;
        float m = (0.3f * j + 0.9f) * h;
        this.leftGills.yaw += m;
        this.rightGills.yaw -= m;
        this.tail.yaw += 0.3f * MathHelper.cos(i * 0.9f) * h;
        this.leftHindLeg.pitch += 1.8849558f * h;
        this.leftHindLeg.yaw += -0.4f * j * h;
        this.leftHindLeg.roll += 1.5707964f * h;
        this.leftFrontLeg.pitch += 1.8849558f * h;
        this.leftFrontLeg.yaw += (-0.2f * k - 0.1f) * h;
        this.leftFrontLeg.roll += 1.5707964f * h;
    }

    private void setPlayingDeadAngles(float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        this.leftHindLeg.pitch += 1.4137167f * headYaw;
        this.leftHindLeg.yaw += 1.0995574f * headYaw;
        this.leftHindLeg.roll += 0.7853982f * headYaw;
        this.leftFrontLeg.pitch += 0.7853982f * headYaw;
        this.leftFrontLeg.yaw += 2.042035f * headYaw;
        this.body.pitch += -0.15f * headYaw;
        this.body.roll += 0.35f * headYaw;
    }

    private void copyLegAngles(float f) {
        if (f <= 1.0E-5f) {
            return;
        }
        this.rightHindLeg.pitch += this.leftHindLeg.pitch * f;
        ModelPart modelPart = this.rightHindLeg;
        modelPart.yaw = modelPart.yaw + -this.leftHindLeg.yaw * f;
        modelPart = this.rightHindLeg;
        modelPart.roll = modelPart.roll + -this.leftHindLeg.roll * f;
        this.rightFrontLeg.pitch += this.leftFrontLeg.pitch * f;
        modelPart = this.rightFrontLeg;
        modelPart.yaw = modelPart.yaw + -this.leftFrontLeg.yaw * f;
        modelPart = this.rightFrontLeg;
        modelPart.roll = modelPart.roll + -this.leftFrontLeg.roll * f;
    }
}

