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
import net.minecraft.client.render.entity.animation.CamelAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CamelEntityModel
extends EntityModel<CamelEntityRenderState> {
    private static final float LIMB_ANGLE_SCALE = 2.0f;
    private static final float LIMB_DISTANCE_SCALE = 2.5f;
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.45f);
    private static final String SADDLE = "saddle";
    private static final String BRIDLE = "bridle";
    private static final String REINS = "reins";
    private final ModelPart head;
    private final ModelPart[] saddleAndBridle;
    private final ModelPart[] reins;

    public CamelEntityModel(ModelPart arg) {
        super(arg);
        ModelPart lv = arg.getChild(EntityModelPartNames.BODY);
        this.head = lv.getChild(EntityModelPartNames.HEAD);
        this.saddleAndBridle = new ModelPart[]{lv.getChild(SADDLE), this.head.getChild(BRIDLE)};
        this.reins = new ModelPart[]{this.head.getChild(REINS)};
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        Dilation lv3 = new Dilation(0.05f);
        ModelPartData lv4 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 25).cuboid(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f), ModelTransform.pivot(0.0f, 4.0f, 9.5f));
        lv4.addChild("hump", ModelPartBuilder.create().uv(74, 0).cuboid(-4.5f, -5.0f, -5.5f, 9.0f, 5.0f, 11.0f), ModelTransform.pivot(0.0f, -12.0f, -10.0f));
        lv4.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(122, 0).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 0.0f), ModelTransform.pivot(0.0f, -9.0f, 3.5f));
        ModelPartData lv5 = lv4.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(60, 24).cuboid(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f).uv(21, 0).cuboid(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f).uv(50, 0).cuboid(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f), ModelTransform.pivot(0.0f, -3.0f, -19.5f));
        lv5.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(45, 0).cuboid(-0.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), ModelTransform.pivot(2.5f, -21.0f, -9.5f));
        lv5.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(67, 0).cuboid(-2.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), ModelTransform.pivot(-2.5f, -21.0f, -9.5f));
        lv2.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().uv(58, 16).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(4.9f, 1.0f, 9.5f));
        lv2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().uv(94, 16).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(-4.9f, 1.0f, 9.5f));
        lv2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(4.9f, 1.0f, -10.5f));
        lv2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().uv(0, 26).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.pivot(-4.9f, 1.0f, -10.5f));
        lv4.addChild(SADDLE, ModelPartBuilder.create().uv(74, 64).cuboid(-4.5f, -17.0f, -15.5f, 9.0f, 5.0f, 11.0f, lv3).uv(92, 114).cuboid(-3.5f, -20.0f, -15.5f, 7.0f, 3.0f, 11.0f, lv3).uv(0, 89).cuboid(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f, lv3), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        lv5.addChild(REINS, ModelPartBuilder.create().uv(98, 42).cuboid(3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f).uv(84, 57).cuboid(-3.5f, -18.0f, -2.0f, 7.0f, 7.0f, 0.0f).uv(98, 42).cuboid(-3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        lv5.addChild(BRIDLE, ModelPartBuilder.create().uv(60, 87).cuboid(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f, lv3).uv(21, 64).cuboid(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f, lv3).uv(50, 64).cuboid(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f, lv3).uv(74, 70).cuboid(2.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f).uv(74, 70).mirrored().cuboid(-3.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(lv, 128, 128);
    }

    @Override
    public void setAngles(CamelEntityRenderState arg) {
        super.setAngles(arg);
        this.setHeadAngles(arg, arg.yawDegrees, arg.pitch);
        this.updateVisibleParts(arg);
        this.animateWalking(CamelAnimations.WALKING, arg.limbFrequency, arg.limbAmplitudeMultiplier, 2.0f, 2.5f);
        this.animate(arg.sittingTransitionAnimationState, CamelAnimations.SITTING_TRANSITION, arg.age, 1.0f);
        this.animate(arg.sittingAnimationState, CamelAnimations.SITTING, arg.age, 1.0f);
        this.animate(arg.standingTransitionAnimationState, CamelAnimations.STANDING_TRANSITION, arg.age, 1.0f);
        this.animate(arg.idlingAnimationState, CamelAnimations.IDLING, arg.age, 1.0f);
        this.animate(arg.dashingAnimationState, CamelAnimations.DASHING, arg.age, 1.0f);
    }

    private void setHeadAngles(CamelEntityRenderState state, float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
        headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);
        if (state.jumpCooldown > 0.0f) {
            float h = 45.0f * state.jumpCooldown / 55.0f;
            headPitch = MathHelper.clamp(headPitch + h, -25.0f, 70.0f);
        }
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
    }

    private void updateVisibleParts(CamelEntityRenderState state) {
        boolean bl = state.saddled;
        boolean bl2 = state.hasPassengers;
        for (ModelPart lv : this.saddleAndBridle) {
            lv.visible = bl;
        }
        for (ModelPart lv : this.reins) {
            lv.visible = bl2 && bl;
        }
    }
}

