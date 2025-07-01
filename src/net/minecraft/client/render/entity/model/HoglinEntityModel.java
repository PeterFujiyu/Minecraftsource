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
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityModel
extends EntityModel<HoglinEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f, Set.of("head"));
    private static final float HEAD_PITCH_START = 0.87266463f;
    private static final float HEAD_PITCH_END = -0.34906584f;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart mane;

    public HoglinEntityModel(ModelPart arg) {
        super(arg);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.mane = this.body.getChild(EntityModelPartNames.MANE);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.rightEar = this.head.getChild(EntityModelPartNames.RIGHT_EAR);
        this.leftEar = this.head.getChild(EntityModelPartNames.LEFT_EAR);
        this.rightFrontLeg = arg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = arg.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.rightHindLeg = arg.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = arg.getChild(EntityModelPartNames.LEFT_HIND_LEG);
    }

    private static ModelData getModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(1, 1).cuboid(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f), ModelTransform.pivot(0.0f, 7.0f, 0.0f));
        lv3.addChild(EntityModelPartNames.MANE, ModelPartBuilder.create().uv(90, 33).cuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new Dilation(0.001f)), ModelTransform.pivot(0.0f, -14.0f, -7.0f));
        ModelPartData lv4 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(61, 1).cuboid(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f), ModelTransform.of(0.0f, 2.0f, -12.0f, 0.87266463f, 0.0f, 0.0f));
        lv4.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(1, 1).cuboid(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(-6.0f, -2.0f, -3.0f, 0.0f, 0.0f, -0.6981317f));
        lv4.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(1, 6).cuboid(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(6.0f, -2.0f, -3.0f, 0.0f, 0.0f, 0.6981317f));
        lv4.addChild(EntityModelPartNames.RIGHT_HORN, ModelPartBuilder.create().uv(10, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(-7.0f, 2.0f, -12.0f));
        lv4.addChild(EntityModelPartNames.LEFT_HORN, ModelPartBuilder.create().uv(1, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(7.0f, 2.0f, -12.0f));
        int i = 14;
        int j = 11;
        lv2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().uv(66, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.pivot(-4.0f, 10.0f, -8.5f));
        lv2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().uv(41, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.pivot(4.0f, 10.0f, -8.5f));
        lv2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().uv(21, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.pivot(-5.0f, 13.0f, 10.0f));
        lv2.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().uv(0, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.pivot(5.0f, 13.0f, 10.0f));
        return lv;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = HoglinEntityModel.getModelData();
        return TexturedModelData.of(lv, 128, 64);
    }

    public static TexturedModelData getBabyTexturedModelData() {
        ModelData lv = HoglinEntityModel.getModelData();
        ModelPartData lv2 = lv.getRoot().getChild(EntityModelPartNames.BODY);
        lv2.addChild(EntityModelPartNames.MANE, ModelPartBuilder.create().uv(90, 33).cuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new Dilation(0.001f)), ModelTransform.pivot(0.0f, -14.0f, -3.0f));
        return TexturedModelData.of(lv, 128, 64).transform(BABY_TRANSFORMER);
    }

    @Override
    public void setAngles(HoglinEntityRenderState arg) {
        super.setAngles(arg);
        float f = arg.limbAmplitudeMultiplier;
        float g = arg.limbFrequency;
        this.rightEar.roll = -0.6981317f - f * MathHelper.sin(g);
        this.leftEar.roll = 0.6981317f + f * MathHelper.sin(g);
        this.head.yaw = arg.yawDegrees * ((float)Math.PI / 180);
        float h = 1.0f - (float)MathHelper.abs(10 - 2 * arg.movementCooldownTicks) / 10.0f;
        this.head.pitch = MathHelper.lerp(h, 0.87266463f, -0.34906584f);
        if (arg.baby) {
            this.head.pivotY += h * 2.5f;
        }
        float i = 1.2f;
        this.rightFrontLeg.pitch = MathHelper.cos(g) * 1.2f * f;
        this.rightHindLeg.pitch = this.leftFrontLeg.pitch = MathHelper.cos(g + (float)Math.PI) * 1.2f * f;
        this.leftHindLeg.pitch = this.rightFrontLeg.pitch;
    }
}

