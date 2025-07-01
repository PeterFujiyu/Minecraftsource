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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PiglinBaseEntityModel;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel
extends PiglinBaseEntityModel<PiglinEntityRenderState> {
    public PiglinEntityModel(ModelPart arg) {
        super(arg);
    }

    @Override
    public void setAngles(PiglinEntityRenderState arg) {
        super.setAngles(arg);
        float f = 0.5235988f;
        float g = arg.handSwingProgress;
        PiglinActivity lv = arg.activity;
        if (lv == PiglinActivity.DANCING) {
            float h = arg.age / 60.0f;
            this.rightEar.roll = 0.5235988f + (float)Math.PI / 180 * MathHelper.sin(h * 30.0f) * 10.0f;
            this.leftEar.roll = -0.5235988f - (float)Math.PI / 180 * MathHelper.cos(h * 30.0f) * 10.0f;
            this.head.pivotX += MathHelper.sin(h * 10.0f);
            this.head.pivotY += MathHelper.sin(h * 40.0f) + 0.4f;
            this.rightArm.roll = (float)Math.PI / 180 * (70.0f + MathHelper.cos(h * 40.0f) * 10.0f);
            this.leftArm.roll = this.rightArm.roll * -1.0f;
            this.rightArm.pivotY += MathHelper.sin(h * 40.0f) * 0.5f - 0.5f;
            this.leftArm.pivotY += MathHelper.sin(h * 40.0f) * 0.5f + 0.5f;
            this.body.pivotY += MathHelper.sin(h * 40.0f) * 0.35f;
        } else if (lv == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON && g == 0.0f) {
            this.rotateMainArm(arg);
        } else if (lv == PiglinActivity.CROSSBOW_HOLD) {
            CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, arg.mainArm == Arm.RIGHT);
        } else if (lv == PiglinActivity.CROSSBOW_CHARGE) {
            CrossbowPosing.charge(this.rightArm, this.leftArm, arg.piglinCrossbowPullTime, arg.itemUseTime, arg.mainArm == Arm.RIGHT);
        } else if (lv == PiglinActivity.ADMIRING_ITEM) {
            this.head.pitch = 0.5f;
            this.head.yaw = 0.0f;
            if (arg.mainArm == Arm.LEFT) {
                this.rightArm.yaw = -0.5f;
                this.rightArm.pitch = -0.9f;
            } else {
                this.leftArm.yaw = 0.5f;
                this.leftArm.pitch = -0.9f;
            }
        }
    }

    @Override
    protected void animateArms(PiglinEntityRenderState arg, float f) {
        float g = arg.handSwingProgress;
        if (g > 0.0f && arg.activity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON) {
            CrossbowPosing.meleeAttack(this.rightArm, this.leftArm, arg.mainArm, g, arg.age);
            return;
        }
        super.animateArms(arg, f);
    }

    private void rotateMainArm(PiglinEntityRenderState state) {
        if (state.mainArm == Arm.LEFT) {
            this.leftArm.pitch = -1.8f;
        } else {
            this.rightArm.pitch = -1.8f;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }
}

