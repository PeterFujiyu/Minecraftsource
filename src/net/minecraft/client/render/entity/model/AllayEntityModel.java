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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class AllayEntityModel
extends EntityModel<AllayEntityRenderState>
implements ModelWithArms {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private static final float field_38999 = 0.7853982f;
    private static final float field_39000 = -1.134464f;
    private static final float field_39001 = -1.0471976f;

    public AllayEntityModel(ModelPart arg) {
        super(arg.getChild(EntityModelPartNames.ROOT), RenderLayer::getEntityTranslucent);
        this.head = this.root.getChild(EntityModelPartNames.HEAD);
        this.body = this.root.getChild(EntityModelPartNames.BODY);
        this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 23.5f, 0.0f));
        lv3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, -3.99f, 0.0f));
        ModelPartData lv4 = lv3.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 10).cuboid(-1.5f, 0.0f, -1.0f, 3.0f, 4.0f, 2.0f, new Dilation(0.0f)).uv(0, 16).cuboid(-1.5f, 0.0f, -1.0f, 3.0f, 5.0f, 2.0f, new Dilation(-0.2f)), ModelTransform.pivot(0.0f, -4.0f, 0.0f));
        lv4.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(23, 0).cuboid(-0.75f, -0.5f, -1.0f, 1.0f, 4.0f, 2.0f, new Dilation(-0.01f)), ModelTransform.pivot(-1.75f, 0.5f, 0.0f));
        lv4.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(23, 6).cuboid(-0.25f, -0.5f, -1.0f, 1.0f, 4.0f, 2.0f, new Dilation(-0.01f)), ModelTransform.pivot(1.75f, 0.5f, 0.0f));
        lv4.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(16, 14).cuboid(0.0f, 1.0f, 0.0f, 0.0f, 5.0f, 8.0f, new Dilation(0.0f)), ModelTransform.pivot(-0.5f, 0.0f, 0.6f));
        lv4.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(16, 14).cuboid(0.0f, 1.0f, 0.0f, 0.0f, 5.0f, 8.0f, new Dilation(0.0f)), ModelTransform.pivot(0.5f, 0.0f, 0.6f));
        return TexturedModelData.of(lv, 32, 32);
    }

    @Override
    public void setAngles(AllayEntityRenderState arg) {
        float p;
        float o;
        float n;
        super.setAngles(arg);
        float f = arg.limbAmplitudeMultiplier;
        float g = arg.limbFrequency;
        float h = arg.age * 20.0f * ((float)Math.PI / 180) + g;
        float i = MathHelper.cos(h) * (float)Math.PI * 0.15f + f;
        float j = arg.age * 9.0f * ((float)Math.PI / 180);
        float k = Math.min(f / 0.3f, 1.0f);
        float l = 1.0f - k;
        float m = arg.itemHoldAnimationTicks;
        if (arg.dancing) {
            n = arg.age * 8.0f * ((float)Math.PI / 180) + f;
            o = MathHelper.cos(n) * 16.0f * ((float)Math.PI / 180);
            p = arg.spinningAnimationTicks;
            float q = MathHelper.cos(n) * 14.0f * ((float)Math.PI / 180);
            float r = MathHelper.cos(n) * 30.0f * ((float)Math.PI / 180);
            this.root.yaw = arg.spinning ? (float)Math.PI * 4 * p : this.root.yaw;
            this.root.roll = o * (1.0f - p);
            this.head.yaw = r * (1.0f - p);
            this.head.roll = q * (1.0f - p);
        } else {
            this.head.pitch = arg.pitch * ((float)Math.PI / 180);
            this.head.yaw = arg.yawDegrees * ((float)Math.PI / 180);
        }
        this.rightWing.pitch = 0.43633232f * (1.0f - k);
        this.rightWing.yaw = -0.7853982f + i;
        this.leftWing.pitch = 0.43633232f * (1.0f - k);
        this.leftWing.yaw = 0.7853982f - i;
        this.body.pitch = k * 0.7853982f;
        n = m * MathHelper.lerp(k, -1.0471976f, -1.134464f);
        this.root.pivotY += (float)Math.cos(j) * 0.25f * l;
        this.rightArm.pitch = n;
        this.leftArm.pitch = n;
        o = l * (1.0f - m);
        p = 0.43633232f - MathHelper.cos(j + 4.712389f) * (float)Math.PI * 0.075f * o;
        this.leftArm.roll = -p;
        this.rightArm.roll = p;
        this.rightArm.yaw = 0.27925268f * m;
        this.leftArm.yaw = -0.27925268f * m;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        float f = 1.0f;
        float g = 3.0f;
        this.root.rotate(matrices);
        this.body.rotate(matrices);
        matrices.translate(0.0f, 0.0625f, 0.1875f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(this.rightArm.pitch));
        matrices.scale(0.7f, 0.7f, 0.7f);
        matrices.translate(0.0625f, 0.0f, 0.0f);
    }
}

