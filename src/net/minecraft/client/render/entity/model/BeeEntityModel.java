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
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BeeEntityModel
extends EntityModel<BeeEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    private static final String BONE = "bone";
    private static final String STINGER = "stinger";
    private static final String LEFT_ANTENNA = "left_antenna";
    private static final String RIGHT_ANTENNA = "right_antenna";
    private static final String FRONT_LEGS = "front_legs";
    private static final String MIDDLE_LEGS = "middle_legs";
    private static final String BACK_LEGS = "back_legs";
    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    public BeeEntityModel(ModelPart arg) {
        super(arg);
        this.bone = arg.getChild(EntityModelPartNames.BONE);
        ModelPart lv = this.bone.getChild(EntityModelPartNames.BODY);
        this.stinger = lv.getChild(STINGER);
        this.leftAntenna = lv.getChild(LEFT_ANTENNA);
        this.rightAntenna = lv.getChild(RIGHT_ANTENNA);
        this.rightWing = this.bone.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = this.bone.getChild(EntityModelPartNames.LEFT_WING);
        this.frontLegs = this.bone.getChild(FRONT_LEGS);
        this.middleLegs = this.bone.getChild(MIDDLE_LEGS);
        this.backLegs = this.bone.getChild(BACK_LEGS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 19.0f, 0.0f));
        ModelPartData lv4 = lv3.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), ModelTransform.NONE);
        lv4.addChild(STINGER, ModelPartBuilder.create().uv(26, 7).cuboid(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), ModelTransform.NONE);
        lv4.addChild(LEFT_ANTENNA, ModelPartBuilder.create().uv(2, 0).cuboid(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -2.0f, -5.0f));
        lv4.addChild(RIGHT_ANTENNA, ModelPartBuilder.create().uv(2, 3).cuboid(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -2.0f, -5.0f));
        Dilation lv5 = new Dilation(0.001f);
        lv3.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, 18).cuboid(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, lv5), ModelTransform.of(-1.5f, -4.0f, -3.0f, 0.0f, -0.2618f, 0.0f));
        lv3.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, 18).mirrored().cuboid(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, lv5), ModelTransform.of(1.5f, -4.0f, -3.0f, 0.0f, 0.2618f, 0.0f));
        lv3.addChild(FRONT_LEGS, ModelPartBuilder.create().cuboid(FRONT_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), ModelTransform.pivot(1.5f, 3.0f, -2.0f));
        lv3.addChild(MIDDLE_LEGS, ModelPartBuilder.create().cuboid(MIDDLE_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), ModelTransform.pivot(1.5f, 3.0f, 0.0f));
        lv3.addChild(BACK_LEGS, ModelPartBuilder.create().cuboid(BACK_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), ModelTransform.pivot(1.5f, 3.0f, 2.0f));
        return TexturedModelData.of(lv, 64, 64);
    }

    @Override
    public void setAngles(BeeEntityRenderState arg) {
        float f;
        super.setAngles(arg);
        this.bodyPitch = arg.bodyPitch;
        this.stinger.visible = arg.hasStinger;
        if (!arg.stoppedOnGround) {
            f = arg.age * 120.32113f * ((float)Math.PI / 180);
            this.rightWing.yaw = 0.0f;
            this.rightWing.roll = MathHelper.cos(f) * (float)Math.PI * 0.15f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = this.rightWing.yaw;
            this.leftWing.roll = -this.rightWing.roll;
            this.frontLegs.pitch = 0.7853982f;
            this.middleLegs.pitch = 0.7853982f;
            this.backLegs.pitch = 0.7853982f;
        }
        if (!arg.angry && !arg.stoppedOnGround) {
            f = MathHelper.cos(arg.age * 0.18f);
            this.bone.pitch = 0.1f + f * (float)Math.PI * 0.025f;
            this.leftAntenna.pitch = f * (float)Math.PI * 0.03f;
            this.rightAntenna.pitch = f * (float)Math.PI * 0.03f;
            this.frontLegs.pitch = -f * (float)Math.PI * 0.1f + 0.3926991f;
            this.backLegs.pitch = -f * (float)Math.PI * 0.05f + 0.7853982f;
            this.bone.pivotY -= MathHelper.cos(arg.age * 0.18f) * 0.9f;
        }
        if (this.bodyPitch > 0.0f) {
            this.bone.pitch = MathHelper.lerpAngleRadians(this.bodyPitch, this.bone.pitch, 3.0915928f);
        }
    }
}

