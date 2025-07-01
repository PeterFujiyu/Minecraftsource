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
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FelineEntityModel<T extends FelineEntityRenderState>
extends EntityModel<T> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 10.0f, 4.0f, Set.of("head"));
    private static final float field_32527 = 0.0f;
    private static final float BODY_SIZE_Y = 16.0f;
    private static final float field_32529 = -9.0f;
    protected static final float HIND_LEG_PIVOT_Y = 18.0f;
    protected static final float HIND_LEG_PIVOT_Z = 5.0f;
    protected static final float FRONT_LEG_PIVOT_Y = 14.1f;
    private static final float FRONT_LEG_PIVOT_Z = -5.0f;
    private static final String TAIL1 = "tail1";
    private static final String TAIL2 = "tail2";
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftFrontLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;
    protected final ModelPart head;
    protected final ModelPart body;

    public FelineEntityModel(ModelPart arg) {
        super(arg);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.upperTail = arg.getChild(TAIL1);
        this.lowerTail = arg.getChild(TAIL2);
        this.leftHindLeg = arg.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightHindLeg = arg.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftFrontLeg = arg.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.rightFrontLeg = arg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        Dilation lv3 = new Dilation(-0.02f);
        lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().cuboid("main", -2.5f, -2.0f, -3.0f, 5.0f, 4.0f, 5.0f, dilation).cuboid(EntityModelPartNames.NOSE, -1.5f, -0.001f, -4.0f, 3, 2, 2, dilation, 0, 24).cuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 0, 10).cuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 6, 10), ModelTransform.pivot(0.0f, 15.0f, -9.0f));
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(20, 0).cuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, dilation), ModelTransform.of(0.0f, 12.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.addChild(TAIL1, ModelPartBuilder.create().uv(0, 15).cuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, dilation), ModelTransform.of(0.0f, 15.0f, 8.0f, 0.9f, 0.0f, 0.0f));
        lv2.addChild(TAIL2, ModelPartBuilder.create().uv(4, 15).cuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, lv3), ModelTransform.pivot(0.0f, 20.0f, 14.0f));
        ModelPartBuilder lv4 = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, dilation);
        lv2.addChild(EntityModelPartNames.LEFT_HIND_LEG, lv4, ModelTransform.pivot(1.1f, 18.0f, 5.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, lv4, ModelTransform.pivot(-1.1f, 18.0f, 5.0f));
        ModelPartBuilder lv5 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, dilation);
        lv2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, lv5, ModelTransform.pivot(1.2f, 14.1f, -5.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, lv5, ModelTransform.pivot(-1.2f, 14.1f, -5.0f));
        return lv;
    }

    @Override
    public void setAngles(T arg) {
        super.setAngles(arg);
        float f = ((FelineEntityRenderState)arg).ageScale;
        if (((FelineEntityRenderState)arg).inSneakingPose) {
            this.body.pivotY += 1.0f * f;
            this.head.pivotY += 2.0f * f;
            this.upperTail.pivotY += 1.0f * f;
            this.lowerTail.pivotY += -4.0f * f;
            this.lowerTail.pivotZ += 2.0f * f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
        } else if (((FelineEntityRenderState)arg).sprinting) {
            this.lowerTail.pivotY = this.upperTail.pivotY;
            this.lowerTail.pivotZ += 2.0f * f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
        }
        this.head.pitch = ((FelineEntityRenderState)arg).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((FelineEntityRenderState)arg).yawDegrees * ((float)Math.PI / 180);
        if (!((FelineEntityRenderState)arg).inSittingPose) {
            this.body.pitch = 1.5707964f;
            float g = ((FelineEntityRenderState)arg).limbAmplitudeMultiplier;
            float h = ((FelineEntityRenderState)arg).limbFrequency;
            if (((FelineEntityRenderState)arg).sprinting) {
                this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662f) * g;
                this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662f + 0.3f) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI + 0.3f) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * g;
                this.lowerTail.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(h) * g;
            } else {
                this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662f) * g;
                this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662f) * g;
                this.lowerTail.pitch = !((FelineEntityRenderState)arg).inSneakingPose ? 1.7278761f + 0.7853982f * MathHelper.cos(h) * g : 1.7278761f + 0.47123894f * MathHelper.cos(h) * g;
            }
        }
        if (((FelineEntityRenderState)arg).inSittingPose) {
            this.body.pitch = 0.7853982f;
            this.body.pivotY += -4.0f * f;
            this.body.pivotZ += 5.0f * f;
            this.head.pivotY += -3.3f * f;
            this.head.pivotZ += 1.0f * f;
            this.upperTail.pivotY += 8.0f * f;
            this.upperTail.pivotZ += -2.0f * f;
            this.lowerTail.pivotY += 2.0f * f;
            this.lowerTail.pivotZ += -0.8f * f;
            this.upperTail.pitch = 1.7278761f;
            this.lowerTail.pitch = 2.670354f;
            this.leftFrontLeg.pitch = -0.15707964f;
            this.leftFrontLeg.pivotY += 2.0f * f;
            this.leftFrontLeg.pivotZ -= 2.0f * f;
            this.rightFrontLeg.pitch = -0.15707964f;
            this.rightFrontLeg.pivotY += 2.0f * f;
            this.rightFrontLeg.pivotZ -= 2.0f * f;
            this.leftHindLeg.pitch = -1.5707964f;
            this.leftHindLeg.pivotY += 3.0f * f;
            this.leftHindLeg.pivotZ -= 4.0f * f;
            this.rightHindLeg.pitch = -1.5707964f;
            this.rightHindLeg.pivotY += 3.0f * f;
            this.rightHindLeg.pivotZ -= 4.0f * f;
        }
        if (((FelineEntityRenderState)arg).sleepAnimationProgress > 0.0f) {
            this.head.roll = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)arg).sleepAnimationProgress, this.head.roll, -1.2707963f);
            this.head.yaw = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)arg).sleepAnimationProgress, this.head.yaw, 1.2707963f);
            this.leftFrontLeg.pitch = -1.2707963f;
            this.rightFrontLeg.pitch = -0.47079635f;
            this.rightFrontLeg.roll = -0.2f;
            this.rightFrontLeg.pivotX += f;
            this.leftHindLeg.pitch = -0.4f;
            this.rightHindLeg.pitch = 0.5f;
            this.rightHindLeg.roll = -0.5f;
            this.rightHindLeg.pivotX += 0.8f * f;
            this.rightHindLeg.pivotY += 2.0f * f;
            this.upperTail.pitch = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)arg).tailCurlAnimationProgress, this.upperTail.pitch, 0.8f);
            this.lowerTail.pitch = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)arg).tailCurlAnimationProgress, this.lowerTail.pitch, -0.4f);
        }
        if (((FelineEntityRenderState)arg).headDownAnimationProgress > 0.0f) {
            this.head.pitch = MathHelper.lerpAngleDegrees(((FelineEntityRenderState)arg).headDownAnimationProgress, this.head.pitch, -0.58177644f);
        }
    }
}

