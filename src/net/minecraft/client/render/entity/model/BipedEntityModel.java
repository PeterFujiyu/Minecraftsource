/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BipedEntityModel<T extends BipedEntityRenderState>
extends EntityModel<T>
implements ModelWithArms,
ModelWithHead {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f, Set.of("head"));
    public static final float field_32505 = 0.25f;
    public static final float field_32506 = 0.5f;
    public static final float field_42513 = -0.1f;
    private static final float field_42512 = 0.005f;
    private static final float SPYGLASS_ARM_YAW_OFFSET = 0.2617994f;
    private static final float SPYGLASS_ARM_PITCH_OFFSET = 1.9198622f;
    private static final float SPYGLASS_SNEAKING_ARM_PITCH_OFFSET = 0.2617994f;
    private static final float field_46576 = -1.3962634f;
    private static final float field_46577 = 0.43633232f;
    private static final float field_46724 = 0.5235988f;
    public static final float field_39069 = 1.4835298f;
    public static final float field_39070 = 0.5235988f;
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public BipedEntityModel(ModelPart arg) {
        this(arg, RenderLayer::getEntityCutoutNoCull);
    }

    public BipedEntityModel(ModelPart arg, Function<Identifier, RenderLayer> function) {
        super(arg, function);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.hat = this.head.getChild(EntityModelPartNames.HAT);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.rightArm = arg.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = arg.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightLeg = arg.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = arg.getChild(EntityModelPartNames.LEFT_LEG);
    }

    public static ModelData getModelData(Dilation dilation, float pivotOffsetY) {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.pivot(0.0f, 0.0f + pivotOffsetY, 0.0f));
        lv3.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation.add(0.5f)), ModelTransform.NONE);
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(0.0f, 0.0f + pivotOffsetY, 0.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(40, 16).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(-5.0f, 2.0f + pivotOffsetY, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(5.0f, 2.0f + pivotOffsetY, 0.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(-1.9f, 12.0f + pivotOffsetY, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(1.9f, 12.0f + pivotOffsetY, 0.0f));
        return lv;
    }

    @Override
    public void setAngles(T arg) {
        boolean bl2;
        super.setAngles(arg);
        ArmPose lv = ((BipedEntityRenderState)arg).leftArmPose;
        ArmPose lv2 = ((BipedEntityRenderState)arg).rightArmPose;
        float f = ((BipedEntityRenderState)arg).leaningPitch;
        boolean bl = ((BipedEntityRenderState)arg).isGliding;
        this.head.pitch = ((BipedEntityRenderState)arg).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((BipedEntityRenderState)arg).yawDegrees * ((float)Math.PI / 180);
        if (bl) {
            this.head.pitch = -0.7853982f;
        } else if (f > 0.0f) {
            this.head.pitch = MathHelper.lerpAngleRadians(f, this.head.pitch, -0.7853982f);
        }
        float g = ((BipedEntityRenderState)arg).limbFrequency;
        float h = ((BipedEntityRenderState)arg).limbAmplitudeMultiplier;
        this.rightArm.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 2.0f * h * 0.5f / ((BipedEntityRenderState)arg).limbAmplitudeInverse;
        this.leftArm.pitch = MathHelper.cos(g * 0.6662f) * 2.0f * h * 0.5f / ((BipedEntityRenderState)arg).limbAmplitudeInverse;
        this.rightLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * h / ((BipedEntityRenderState)arg).limbAmplitudeInverse;
        this.leftLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * h / ((BipedEntityRenderState)arg).limbAmplitudeInverse;
        this.rightLeg.yaw = 0.005f;
        this.leftLeg.yaw = -0.005f;
        this.rightLeg.roll = 0.005f;
        this.leftLeg.roll = -0.005f;
        if (((BipedEntityRenderState)arg).hasVehicle) {
            this.rightArm.pitch += -0.62831855f;
            this.leftArm.pitch += -0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        boolean bl3 = bl2 = ((BipedEntityRenderState)arg).mainArm == Arm.RIGHT;
        if (((BipedEntityRenderState)arg).isUsingItem) {
            boolean bl4 = bl3 = ((BipedEntityRenderState)arg).activeHand == Hand.MAIN_HAND;
            if (bl3 == bl2) {
                this.positionRightArm(arg, lv2);
            } else {
                this.positionLeftArm(arg, lv);
            }
        } else {
            boolean bl5 = bl3 = bl2 ? lv.isTwoHanded() : lv2.isTwoHanded();
            if (bl2 != bl3) {
                this.positionLeftArm(arg, lv);
                this.positionRightArm(arg, lv2);
            } else {
                this.positionRightArm(arg, lv2);
                this.positionLeftArm(arg, lv);
            }
        }
        this.animateArms(arg, ((BipedEntityRenderState)arg).age);
        if (((BipedEntityRenderState)arg).isInSneakingPose) {
            this.body.pitch = 0.5f;
            this.rightArm.pitch += 0.4f;
            this.leftArm.pitch += 0.4f;
            this.rightLeg.pivotZ += 4.0f;
            this.leftLeg.pivotZ += 4.0f;
            this.head.pivotY += 4.2f;
            this.body.pivotY += 3.2f;
            this.leftArm.pivotY += 3.2f;
            this.rightArm.pivotY += 3.2f;
        }
        if (lv2 != ArmPose.SPYGLASS) {
            CrossbowPosing.swingArm(this.rightArm, ((BipedEntityRenderState)arg).age, 1.0f);
        }
        if (lv != ArmPose.SPYGLASS) {
            CrossbowPosing.swingArm(this.leftArm, ((BipedEntityRenderState)arg).age, -1.0f);
        }
        if (f > 0.0f) {
            float l;
            float k;
            float i = g % 26.0f;
            Arm lv3 = ((BipedEntityRenderState)arg).preferredArm;
            float j = lv3 == Arm.RIGHT && ((BipedEntityRenderState)arg).handSwingProgress > 0.0f ? 0.0f : f;
            float f2 = k = lv3 == Arm.LEFT && ((BipedEntityRenderState)arg).handSwingProgress > 0.0f ? 0.0f : f;
            if (!((BipedEntityRenderState)arg).isUsingItem) {
                if (i < 14.0f) {
                    this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 0.0f);
                    this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 0.0f);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float)Math.PI);
                    this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float)Math.PI);
                    this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, (float)Math.PI + 1.8707964f * this.method_2807(i) / this.method_2807(14.0f));
                    this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, (float)Math.PI - 1.8707964f * this.method_2807(i) / this.method_2807(14.0f));
                } else if (i >= 14.0f && i < 22.0f) {
                    l = (i - 14.0f) / 8.0f;
                    this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 1.5707964f * l);
                    this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 1.5707964f * l);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float)Math.PI);
                    this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float)Math.PI);
                    this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, 5.012389f - 1.8707964f * l);
                    this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, 1.2707963f + 1.8707964f * l);
                } else if (i >= 22.0f && i < 26.0f) {
                    l = (i - 22.0f) / 4.0f;
                    this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 1.5707964f - 1.5707964f * l);
                    this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 1.5707964f - 1.5707964f * l);
                    this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float)Math.PI);
                    this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float)Math.PI);
                    this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, (float)Math.PI);
                    this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, (float)Math.PI);
                }
            }
            l = 0.3f;
            float m = 0.33333334f;
            this.leftLeg.pitch = MathHelper.lerp(f, this.leftLeg.pitch, 0.3f * MathHelper.cos(g * 0.33333334f + (float)Math.PI));
            this.rightLeg.pitch = MathHelper.lerp(f, this.rightLeg.pitch, 0.3f * MathHelper.cos(g * 0.33333334f));
        }
    }

    private void positionRightArm(T state, ArmPose armPose) {
        switch (armPose.ordinal()) {
            case 0: {
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 2: {
                this.positionBlockingArm(this.rightArm, true);
                break;
            }
            case 1: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 4: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 3: {
                this.rightArm.yaw = -0.1f + this.head.yaw;
                this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case 5: {
                CrossbowPosing.charge(this.rightArm, this.leftArm, ((BipedEntityRenderState)state).crossbowPullTime, ((BipedEntityRenderState)state).itemUseTime, true);
                break;
            }
            case 6: {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
                break;
            }
            case 9: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.62831855f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case 7: {
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622f - (((BipedEntityRenderState)state).isInSneakingPose ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.rightArm.yaw = this.head.yaw - 0.2617994f;
                break;
            }
            case 8: {
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2f, 1.2f) - 1.4835298f;
                this.rightArm.yaw = this.head.yaw - 0.5235988f;
            }
        }
    }

    private void positionLeftArm(T state, ArmPose armPose) {
        switch (armPose.ordinal()) {
            case 0: {
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 2: {
                this.positionBlockingArm(this.leftArm, false);
                break;
            }
            case 1: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 4: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 3: {
                this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
                this.leftArm.yaw = 0.1f + this.head.yaw;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case 5: {
                CrossbowPosing.charge(this.rightArm, this.leftArm, ((BipedEntityRenderState)state).crossbowPullTime, ((BipedEntityRenderState)state).itemUseTime, false);
                break;
            }
            case 6: {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
                break;
            }
            case 9: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.62831855f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case 7: {
                this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622f - (((BipedEntityRenderState)state).isInSneakingPose ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.leftArm.yaw = this.head.yaw + 0.2617994f;
                break;
            }
            case 8: {
                this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2f, 1.2f) - 1.4835298f;
                this.leftArm.yaw = this.head.yaw + 0.5235988f;
            }
        }
    }

    private void positionBlockingArm(ModelPart arm, boolean rightArm) {
        arm.pitch = arm.pitch * 0.5f - 0.9424779f + MathHelper.clamp(this.head.pitch, -1.3962634f, 0.43633232f);
        arm.yaw = (rightArm ? -30.0f : 30.0f) * ((float)Math.PI / 180) + MathHelper.clamp(this.head.yaw, -0.5235988f, 0.5235988f);
    }

    protected void animateArms(T state, float animationProgress) {
        float g = ((BipedEntityRenderState)state).handSwingProgress;
        if (g <= 0.0f) {
            return;
        }
        Arm lv = ((BipedEntityRenderState)state).preferredArm;
        ModelPart lv2 = this.getArm(lv);
        float h = g;
        this.body.yaw = MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2)) * 0.2f;
        if (lv == Arm.LEFT) {
            this.body.yaw *= -1.0f;
        }
        float i = ((BipedEntityRenderState)state).ageScale;
        this.rightArm.pivotZ = MathHelper.sin(this.body.yaw) * 5.0f * i;
        this.rightArm.pivotX = -MathHelper.cos(this.body.yaw) * 5.0f * i;
        this.leftArm.pivotZ = -MathHelper.sin(this.body.yaw) * 5.0f * i;
        this.leftArm.pivotX = MathHelper.cos(this.body.yaw) * 5.0f * i;
        this.rightArm.yaw += this.body.yaw;
        this.leftArm.yaw += this.body.yaw;
        this.leftArm.pitch += this.body.yaw;
        h = 1.0f - g;
        h *= h;
        h *= h;
        h = 1.0f - h;
        float j = MathHelper.sin(h * (float)Math.PI);
        float k = MathHelper.sin(g * (float)Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
        lv2.pitch -= j * 1.2f + k;
        lv2.yaw += this.body.yaw * 2.0f;
        lv2.roll += MathHelper.sin(g * (float)Math.PI) * -0.4f;
    }

    private float method_2807(float f) {
        return -65.0f * f + f * f;
    }

    public void copyTransforms(BipedEntityModel<T> model) {
        model.head.copyTransform(this.head);
        model.body.copyTransform(this.body);
        model.rightArm.copyTransform(this.rightArm);
        model.leftArm.copyTransform(this.leftArm);
        model.rightLeg.copyTransform(this.rightLeg);
        model.leftLeg.copyTransform(this.leftLeg);
    }

    public void setVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.body.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.root.rotate(matrices);
        this.getArm(arm).rotate(matrices);
    }

    protected ModelPart getArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ArmPose {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true),
        SPYGLASS(false),
        TOOT_HORN(false),
        BRUSH(false);

        private final boolean twoHanded;

        private ArmPose(boolean twoHanded) {
            this.twoHanded = twoHanded;
        }

        public boolean isTwoHanded() {
            return this.twoHanded;
        }
    }
}

