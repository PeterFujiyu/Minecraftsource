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
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends EntityModel<ParrotEntityRenderState> {
    private static final String FEATHER = "feather";
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ParrotEntityModel(ModelPart arg) {
        super(arg);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.tail = arg.getChild(EntityModelPartNames.TAIL);
        this.leftWing = arg.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = arg.getChild(EntityModelPartNames.RIGHT_WING);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.leftLeg = arg.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = arg.getChild(EntityModelPartNames.RIGHT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(2, 8).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), ModelTransform.of(0.0f, 16.5f, -3.0f, 0.4937f, 0.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(22, 1).cuboid(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), ModelTransform.of(0.0f, 21.07f, 1.16f, 1.015f, 0.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.of(1.5f, 16.94f, -2.76f, -0.6981f, (float)(-Math.PI), 0.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.of(-1.5f, 16.94f, -2.76f, -0.6981f, (float)(-Math.PI), 0.0f));
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), ModelTransform.pivot(0.0f, 15.69f, -2.76f));
        lv3.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), ModelTransform.pivot(0.0f, -2.0f, -1.0f));
        lv3.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, -0.5f, -1.5f));
        lv3.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, -1.75f, -2.45f));
        lv3.addChild(FEATHER, ModelPartBuilder.create().uv(2, 18).cuboid(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), ModelTransform.of(0.0f, -2.15f, 0.15f, -0.2214f, 0.0f, 0.0f));
        ModelPartBuilder lv4 = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        lv2.addChild(EntityModelPartNames.LEFT_LEG, lv4, ModelTransform.of(1.0f, 22.0f, -1.05f, -0.0299f, 0.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_LEG, lv4, ModelTransform.of(-1.0f, 22.0f, -1.05f, -0.0299f, 0.0f, 0.0f));
        return TexturedModelData.of(lv, 32, 32);
    }

    @Override
    public void setAngles(ParrotEntityRenderState arg) {
        super.setAngles(arg);
        this.animateModel(arg.parrotPose);
        this.head.pitch = arg.pitch * ((float)Math.PI / 180);
        this.head.yaw = arg.yawDegrees * ((float)Math.PI / 180);
        switch (arg.parrotPose.ordinal()) {
            case 2: {
                break;
            }
            case 3: {
                float f = MathHelper.cos(arg.age);
                float g = MathHelper.sin(arg.age);
                this.head.pivotX += f;
                this.head.pivotY += g;
                this.head.pitch = 0.0f;
                this.head.yaw = 0.0f;
                this.head.roll = MathHelper.sin(arg.age) * 0.4f;
                this.body.pivotX += f;
                this.body.pivotY += g;
                this.leftWing.roll = -0.0873f - arg.flapAngle;
                this.leftWing.pivotX += f;
                this.leftWing.pivotY += g;
                this.rightWing.roll = 0.0873f + arg.flapAngle;
                this.rightWing.pivotX += f;
                this.rightWing.pivotY += g;
                this.tail.pivotX += f;
                this.tail.pivotY += g;
                break;
            }
            case 1: {
                this.leftLeg.pitch += MathHelper.cos(arg.limbFrequency * 0.6662f) * 1.4f * arg.limbAmplitudeMultiplier;
                this.rightLeg.pitch += MathHelper.cos(arg.limbFrequency * 0.6662f + (float)Math.PI) * 1.4f * arg.limbAmplitudeMultiplier;
            }
            default: {
                float h = arg.flapAngle * 0.3f;
                this.head.pivotY += h;
                this.tail.pitch += MathHelper.cos(arg.limbFrequency * 0.6662f) * 0.3f * arg.limbAmplitudeMultiplier;
                this.tail.pivotY += h;
                this.body.pivotY += h;
                this.leftWing.roll = -0.0873f - arg.flapAngle;
                this.leftWing.pivotY += h;
                this.rightWing.roll = 0.0873f + arg.flapAngle;
                this.rightWing.pivotY += h;
                this.leftLeg.pivotY += h;
                this.rightLeg.pivotY += h;
            }
        }
    }

    private void animateModel(Pose pose) {
        switch (pose.ordinal()) {
            case 0: {
                this.leftLeg.pitch += 0.6981317f;
                this.rightLeg.pitch += 0.6981317f;
                break;
            }
            case 2: {
                float f = 1.9f;
                this.head.pivotY += 1.9f;
                this.tail.pitch += 0.5235988f;
                this.tail.pivotY += 1.9f;
                this.body.pivotY += 1.9f;
                this.leftWing.roll = -0.0873f;
                this.leftWing.pivotY += 1.9f;
                this.rightWing.roll = 0.0873f;
                this.rightWing.pivotY += 1.9f;
                this.leftLeg.pivotY += 1.9f;
                this.rightLeg.pivotY += 1.9f;
                this.leftLeg.pitch += 1.5707964f;
                this.rightLeg.pitch += 1.5707964f;
                break;
            }
            case 3: {
                this.leftLeg.roll = -0.34906584f;
                this.rightLeg.roll = 0.34906584f;
                break;
            }
        }
    }

    public static Pose getPose(ParrotEntity parrot) {
        if (parrot.isSongPlaying()) {
            return Pose.PARTY;
        }
        if (parrot.isInSittingPose()) {
            return Pose.SITTING;
        }
        if (parrot.isInAir()) {
            return Pose.FLYING;
        }
        return Pose.STANDING;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Pose {
        FLYING,
        STANDING,
        SITTING,
        PARTY,
        ON_SHOULDER;

    }
}

