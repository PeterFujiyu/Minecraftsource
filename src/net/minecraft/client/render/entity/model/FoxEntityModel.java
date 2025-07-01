/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityModel
extends EntityModel<FoxEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0f, 3.35f, Set.of("head"));
    public final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private static final int field_32477 = 6;
    private static final float HEAD_Y_PIVOT = 16.5f;
    private static final float LEG_Y_PIVOT = 17.5f;
    private float legPitchModifier;

    public FoxEntityModel(ModelPart arg) {
        super(arg);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.rightHindLeg = arg.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = arg.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = arg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = arg.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(1, 5).cuboid(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f), ModelTransform.pivot(-1.0f, 16.5f, -3.0f));
        lv3.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(8, 1).cuboid(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        lv3.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(15, 1).cuboid(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        lv3.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(6, 18).cuboid(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f), ModelTransform.NONE);
        ModelPartData lv4 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(24, 15).cuboid(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, -6.0f, 1.5707964f, 0.0f, 0.0f));
        Dilation lv5 = new Dilation(0.001f);
        ModelPartBuilder lv6 = ModelPartBuilder.create().uv(4, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, lv5);
        ModelPartBuilder lv7 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, lv5);
        lv2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, lv7, ModelTransform.pivot(-5.0f, 17.5f, 7.0f));
        lv2.addChild(EntityModelPartNames.LEFT_HIND_LEG, lv6, ModelTransform.pivot(-1.0f, 17.5f, 7.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, lv7, ModelTransform.pivot(-5.0f, 17.5f, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, lv6, ModelTransform.pivot(-1.0f, 17.5f, 0.0f));
        lv4.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(30, 0).cuboid(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f), ModelTransform.of(-4.0f, 15.0f, -1.0f, -0.05235988f, 0.0f, 0.0f));
        return TexturedModelData.of(lv, 48, 32);
    }

    @Override
    public void setAngles(FoxEntityRenderState arg) {
        float i;
        super.setAngles(arg);
        float f = arg.limbAmplitudeMultiplier;
        float g = arg.limbFrequency;
        this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.head.roll = arg.headRoll;
        this.rightHindLeg.visible = true;
        this.leftHindLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        float h = arg.ageScale;
        if (arg.inSneakingPose) {
            this.body.pitch += 0.10471976f;
            i = arg.bodyRotationHeightOffset;
            this.body.pivotY += i * h;
            this.head.pivotY += i * h;
        } else if (arg.sleeping) {
            this.body.roll = -1.5707964f;
            this.body.pivotY += 5.0f * h;
            this.tail.pitch = -2.6179938f;
            if (arg.baby) {
                this.tail.pitch = -2.1816616f;
                this.body.pivotZ += 2.0f;
            }
            this.head.pivotX += 2.0f * h;
            this.head.pivotY += 2.99f * h;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.rightHindLeg.visible = false;
            this.leftHindLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if (arg.sitting) {
            this.body.pitch = 0.5235988f;
            this.body.pivotY -= 7.0f * h;
            this.body.pivotZ += 3.0f * h;
            this.tail.pitch = 0.7853982f;
            this.tail.pivotZ -= 1.0f * h;
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (arg.baby) {
                this.head.pivotY -= 1.75f;
                this.head.pivotZ -= 0.375f;
            } else {
                this.head.pivotY -= 6.5f;
                this.head.pivotZ += 2.75f;
            }
            this.rightHindLeg.pitch = -1.3089969f;
            this.rightHindLeg.pivotY += 4.0f * h;
            this.rightHindLeg.pivotZ -= 0.25f * h;
            this.leftHindLeg.pitch = -1.3089969f;
            this.leftHindLeg.pivotY += 4.0f * h;
            this.leftHindLeg.pivotZ -= 0.25f * h;
            this.rightFrontLeg.pitch = -0.2617994f;
            this.leftFrontLeg.pitch = -0.2617994f;
        }
        if (!(arg.sleeping || arg.walking || arg.inSneakingPose)) {
            this.head.pitch = arg.pitch * ((float)Math.PI / 180);
            this.head.yaw = arg.yawDegrees * ((float)Math.PI / 180);
        }
        if (arg.sleeping) {
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(arg.age * 0.027f) / 22.0f;
        }
        if (arg.inSneakingPose) {
            this.body.yaw = i = MathHelper.cos(arg.age) * 0.01f;
            this.rightHindLeg.roll = i;
            this.leftHindLeg.roll = i;
            this.rightFrontLeg.roll = i / 2.0f;
            this.leftFrontLeg.roll = i / 2.0f;
        }
        if (arg.walking) {
            i = 0.1f;
            this.legPitchModifier += 0.67f;
            this.rightHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
            this.leftHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.rightFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.leftFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
        }
    }
}

