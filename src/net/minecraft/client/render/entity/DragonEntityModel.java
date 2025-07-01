/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

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
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.entity.boss.dragon.EnderDragonFrameTracker;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DragonEntityModel
extends EntityModel<EnderDragonEntityRenderState> {
    private static final int NUM_NECK_PARTS = 5;
    private static final int NUM_TAIL_PARTS = 12;
    private final ModelPart head;
    private final ModelPart[] neckParts = new ModelPart[5];
    private final ModelPart[] tailParts = new ModelPart[12];
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftFrontLegTip;
    private final ModelPart leftFrontFoot;
    private final ModelPart leftHindLeg;
    private final ModelPart leftHindLegTip;
    private final ModelPart leftHindFoot;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightFrontLegTip;
    private final ModelPart rightFrontFoot;
    private final ModelPart rightHindLeg;
    private final ModelPart rightHindLegTip;
    private final ModelPart rightHindFoot;

    private static String neck(int id) {
        return "neck" + id;
    }

    private static String tail(int id) {
        return "tail" + id;
    }

    public DragonEntityModel(ModelPart arg) {
        super(arg);
        int i;
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.jaw = this.head.getChild(EntityModelPartNames.JAW);
        for (i = 0; i < this.neckParts.length; ++i) {
            this.neckParts[i] = arg.getChild(DragonEntityModel.neck(i));
        }
        for (i = 0; i < this.tailParts.length; ++i) {
            this.tailParts[i] = arg.getChild(DragonEntityModel.tail(i));
        }
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
        this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
        this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.leftFrontLegTip = this.leftFrontLeg.getChild(EntityModelPartNames.LEFT_FRONT_LEG_TIP);
        this.leftFrontFoot = this.leftFrontLegTip.getChild(EntityModelPartNames.LEFT_FRONT_FOOT);
        this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.leftHindLegTip = this.leftHindLeg.getChild(EntityModelPartNames.LEFT_HIND_LEG_TIP);
        this.leftHindFoot = this.leftHindLegTip.getChild(EntityModelPartNames.LEFT_HIND_FOOT);
        this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
        this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
        this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.rightFrontLegTip = this.rightFrontLeg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG_TIP);
        this.rightFrontFoot = this.rightFrontLegTip.getChild(EntityModelPartNames.RIGHT_FRONT_FOOT);
        this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.rightHindLegTip = this.rightHindLeg.getChild(EntityModelPartNames.RIGHT_HIND_LEG_TIP);
        this.rightHindFoot = this.rightHindLegTip.getChild(EntityModelPartNames.RIGHT_HIND_FOOT);
    }

    public static TexturedModelData createTexturedModelData() {
        int i;
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        float f = -16.0f;
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().cuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).cuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).mirrored().cuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).mirrored().cuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), ModelTransform.pivot(0.0f, 20.0f, -62.0f));
        lv3.addChild(EntityModelPartNames.JAW, ModelPartBuilder.create().cuboid(EntityModelPartNames.JAW, -6.0f, 0.0f, -16.0f, 12, 4, 16, 176, 65), ModelTransform.pivot(0.0f, 4.0f, -8.0f));
        ModelPartBuilder lv4 = ModelPartBuilder.create().cuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 192, 104).cuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 48, 0);
        for (i = 0; i < 5; ++i) {
            lv2.addChild(DragonEntityModel.neck(i), lv4, ModelTransform.pivot(0.0f, 20.0f, -12.0f - (float)i * 10.0f));
        }
        for (i = 0; i < 12; ++i) {
            lv2.addChild(DragonEntityModel.tail(i), lv4, ModelTransform.pivot(0.0f, 10.0f, 60.0f + (float)i * 10.0f));
        }
        ModelPartData lv5 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().cuboid(EntityModelPartNames.BODY, -12.0f, 1.0f, -16.0f, 24, 24, 64, 0, 0).cuboid("scale", -1.0f, -5.0f, -10.0f, 2, 6, 12, 220, 53).cuboid("scale", -1.0f, -5.0f, 10.0f, 2, 6, 12, 220, 53).cuboid("scale", -1.0f, -5.0f, 30.0f, 2, 6, 12, 220, 53), ModelTransform.pivot(0.0f, 3.0f, 8.0f));
        ModelPartData lv6 = lv5.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().mirrored().cuboid(EntityModelPartNames.BONE, 0.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).cuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), ModelTransform.pivot(12.0f, 2.0f, -6.0f));
        lv6.addChild(EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().mirrored().cuboid(EntityModelPartNames.BONE, 0.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).cuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), ModelTransform.pivot(56.0f, 0.0f, 0.0f));
        ModelPartData lv7 = lv5.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().cuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), ModelTransform.of(12.0f, 17.0f, -6.0f, 1.3f, 0.0f, 0.0f));
        ModelPartData lv8 = lv7.addChild(EntityModelPartNames.LEFT_FRONT_LEG_TIP, ModelPartBuilder.create().cuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), ModelTransform.of(0.0f, 20.0f, -1.0f, -0.5f, 0.0f, 0.0f));
        lv8.addChild(EntityModelPartNames.LEFT_FRONT_FOOT, ModelPartBuilder.create().cuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), ModelTransform.of(0.0f, 23.0f, 0.0f, 0.75f, 0.0f, 0.0f));
        ModelPartData lv9 = lv5.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().cuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), ModelTransform.of(16.0f, 13.0f, 34.0f, 1.0f, 0.0f, 0.0f));
        ModelPartData lv10 = lv9.addChild(EntityModelPartNames.LEFT_HIND_LEG_TIP, ModelPartBuilder.create().cuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), ModelTransform.of(0.0f, 32.0f, -4.0f, 0.5f, 0.0f, 0.0f));
        lv10.addChild(EntityModelPartNames.LEFT_HIND_FOOT, ModelPartBuilder.create().cuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), ModelTransform.of(0.0f, 31.0f, 4.0f, 0.75f, 0.0f, 0.0f));
        ModelPartData lv11 = lv5.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().cuboid(EntityModelPartNames.BONE, -56.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).cuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), ModelTransform.pivot(-12.0f, 2.0f, -6.0f));
        lv11.addChild(EntityModelPartNames.RIGHT_WING_TIP, ModelPartBuilder.create().cuboid(EntityModelPartNames.BONE, -56.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).cuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), ModelTransform.pivot(-56.0f, 0.0f, 0.0f));
        ModelPartData lv12 = lv5.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().cuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), ModelTransform.of(-12.0f, 17.0f, -6.0f, 1.3f, 0.0f, 0.0f));
        ModelPartData lv13 = lv12.addChild(EntityModelPartNames.RIGHT_FRONT_LEG_TIP, ModelPartBuilder.create().cuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), ModelTransform.of(0.0f, 20.0f, -1.0f, -0.5f, 0.0f, 0.0f));
        lv13.addChild(EntityModelPartNames.RIGHT_FRONT_FOOT, ModelPartBuilder.create().cuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), ModelTransform.of(0.0f, 23.0f, 0.0f, 0.75f, 0.0f, 0.0f));
        ModelPartData lv14 = lv5.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().cuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), ModelTransform.of(-16.0f, 13.0f, 34.0f, 1.0f, 0.0f, 0.0f));
        ModelPartData lv15 = lv14.addChild(EntityModelPartNames.RIGHT_HIND_LEG_TIP, ModelPartBuilder.create().cuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), ModelTransform.of(0.0f, 32.0f, -4.0f, 0.5f, 0.0f, 0.0f));
        lv15.addChild(EntityModelPartNames.RIGHT_HIND_FOOT, ModelPartBuilder.create().cuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), ModelTransform.of(0.0f, 31.0f, 4.0f, 0.75f, 0.0f, 0.0f));
        return TexturedModelData.of(lv, 256, 256);
    }

    @Override
    public void setAngles(EnderDragonEntityRenderState arg) {
        super.setAngles(arg);
        float f = arg.wingPosition * ((float)Math.PI * 2);
        this.jaw.pitch = (MathHelper.sin(f) + 1.0f) * 0.2f;
        float g = MathHelper.sin(f - 1.0f) + 1.0f;
        g = (g * g + g * 2.0f) * 0.05f;
        this.root.pivotY = (g - 2.0f) * 16.0f;
        this.root.pivotZ = -48.0f;
        this.root.pitch = g * 2.0f * ((float)Math.PI / 180);
        float h = this.neckParts[0].pivotX;
        float i = this.neckParts[0].pivotY;
        float j = this.neckParts[0].pivotZ;
        float k = 1.5f;
        EnderDragonFrameTracker.Frame lv = arg.getLerpedFrame(6);
        float l = MathHelper.wrapDegrees(arg.getLerpedFrame(5).yRot() - arg.getLerpedFrame(10).yRot());
        float m = MathHelper.wrapDegrees(arg.getLerpedFrame(5).yRot() + l / 2.0f);
        for (int n = 0; n < 5; ++n) {
            ModelPart lv2 = this.neckParts[n];
            EnderDragonFrameTracker.Frame lv3 = arg.getLerpedFrame(5 - n);
            float o = MathHelper.cos((float)n * 0.45f + f) * 0.15f;
            lv2.yaw = MathHelper.wrapDegrees(lv3.yRot() - lv.yRot()) * ((float)Math.PI / 180) * 1.5f;
            lv2.pitch = o + arg.getNeckPartPitchOffset(n, lv, lv3) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            lv2.roll = -MathHelper.wrapDegrees(lv3.yRot() - m) * ((float)Math.PI / 180) * 1.5f;
            lv2.pivotY = i;
            lv2.pivotZ = j;
            lv2.pivotX = h;
            h -= MathHelper.sin(lv2.yaw) * MathHelper.cos(lv2.pitch) * 10.0f;
            i += MathHelper.sin(lv2.pitch) * 10.0f;
            j -= MathHelper.cos(lv2.yaw) * MathHelper.cos(lv2.pitch) * 10.0f;
        }
        this.head.pivotY = i;
        this.head.pivotZ = j;
        this.head.pivotX = h;
        EnderDragonFrameTracker.Frame lv4 = arg.getLerpedFrame(0);
        this.head.yaw = MathHelper.wrapDegrees(lv4.yRot() - lv.yRot()) * ((float)Math.PI / 180);
        this.head.pitch = MathHelper.wrapDegrees(arg.getNeckPartPitchOffset(6, lv, lv4)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
        this.head.roll = -MathHelper.wrapDegrees(lv4.yRot() - m) * ((float)Math.PI / 180);
        this.body.roll = -l * 1.5f * ((float)Math.PI / 180);
        this.leftWing.pitch = 0.125f - MathHelper.cos(f) * 0.2f;
        this.leftWing.yaw = -0.25f;
        this.leftWing.roll = -(MathHelper.sin(f) + 0.125f) * 0.8f;
        this.leftWingTip.roll = (MathHelper.sin(f + 2.0f) + 0.5f) * 0.75f;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.roll = -this.leftWing.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.setLegAngles(g, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftHindLeg, this.leftHindLegTip, this.leftHindFoot);
        this.setLegAngles(g, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightHindLeg, this.rightHindLegTip, this.rightHindFoot);
        float p = 0.0f;
        i = this.tailParts[0].pivotY;
        j = this.tailParts[0].pivotZ;
        h = this.tailParts[0].pivotX;
        lv = arg.getLerpedFrame(11);
        for (int q = 0; q < 12; ++q) {
            EnderDragonFrameTracker.Frame lv5 = arg.getLerpedFrame(12 + q);
            ModelPart lv6 = this.tailParts[q];
            lv6.yaw = (MathHelper.wrapDegrees(lv5.yRot() - lv.yRot()) * 1.5f + 180.0f) * ((float)Math.PI / 180);
            lv6.pitch = (p += MathHelper.sin((float)q * 0.45f + f) * 0.05f) + (float)(lv5.y() - lv.y()) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            lv6.roll = MathHelper.wrapDegrees(lv5.yRot() - m) * ((float)Math.PI / 180) * 1.5f;
            lv6.pivotY = i;
            lv6.pivotZ = j;
            lv6.pivotX = h;
            i += MathHelper.sin(lv6.pitch) * 10.0f;
            j -= MathHelper.cos(lv6.yaw) * MathHelper.cos(lv6.pitch) * 10.0f;
            h -= MathHelper.sin(lv6.yaw) * MathHelper.cos(lv6.pitch) * 10.0f;
        }
    }

    private void setLegAngles(float offset, ModelPart frontLeg, ModelPart frontLegTip, ModelPart frontFoot, ModelPart hindLeg, ModelPart hindLegTip, ModelPart hindFoot) {
        hindLeg.pitch = 1.0f + offset * 0.1f;
        hindLegTip.pitch = 0.5f + offset * 0.1f;
        hindFoot.pitch = 0.75f + offset * 0.1f;
        frontLeg.pitch = 1.3f + offset * 0.1f;
        frontLegTip.pitch = -0.5f - offset * 0.1f;
        frontFoot.pitch = 0.75f + offset * 0.1f;
    }
}

