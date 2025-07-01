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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.BatAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.BatEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BatEntityModel
extends EntityModel<BatEntityRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;
    private final ModelPart feet;

    public BatEntityModel(ModelPart arg) {
        super(arg, RenderLayer::getEntityCutout);
        this.body = arg.getChild(EntityModelPartNames.BODY);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
        this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
        this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
        this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
        this.feet = this.body.getChild(EntityModelPartNames.FEET);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, 0.0f, -1.0f, 3.0f, 5.0f, 2.0f), ModelTransform.pivot(0.0f, 17.0f, 0.0f));
        ModelPartData lv4 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 7).cuboid(-2.0f, -3.0f, -1.0f, 4.0f, 3.0f, 2.0f), ModelTransform.pivot(0.0f, 17.0f, 0.0f));
        lv4.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(1, 15).cuboid(-2.5f, -4.0f, 0.0f, 3.0f, 5.0f, 0.0f), ModelTransform.pivot(-1.5f, -2.0f, 0.0f));
        lv4.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(8, 15).cuboid(-0.1f, -3.0f, 0.0f, 3.0f, 5.0f, 0.0f), ModelTransform.pivot(1.1f, -3.0f, 0.0f));
        ModelPartData lv5 = lv3.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(12, 0).cuboid(-2.0f, -2.0f, 0.0f, 2.0f, 7.0f, 0.0f), ModelTransform.pivot(-1.5f, 0.0f, 0.0f));
        lv5.addChild(EntityModelPartNames.RIGHT_WING_TIP, ModelPartBuilder.create().uv(16, 0).cuboid(-6.0f, -2.0f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.pivot(-2.0f, 0.0f, 0.0f));
        ModelPartData lv6 = lv3.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(12, 7).cuboid(0.0f, -2.0f, 0.0f, 2.0f, 7.0f, 0.0f), ModelTransform.pivot(1.5f, 0.0f, 0.0f));
        lv6.addChild(EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().uv(16, 8).cuboid(0.0f, -2.0f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.pivot(2.0f, 0.0f, 0.0f));
        lv3.addChild(EntityModelPartNames.FEET, ModelPartBuilder.create().uv(16, 16).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 2.0f, 0.0f), ModelTransform.pivot(0.0f, 5.0f, 0.0f));
        return TexturedModelData.of(lv, 32, 32);
    }

    @Override
    public void setAngles(BatEntityRenderState arg) {
        super.setAngles(arg);
        if (arg.roosting) {
            this.setRoostingHeadAngles(arg.yawDegrees);
        }
        this.animate(arg.flyingAnimationState, BatAnimations.FLYING, arg.age, 1.0f);
        this.animate(arg.roostingAnimationState, BatAnimations.ROOSTING, arg.age, 1.0f);
    }

    private void setRoostingHeadAngles(float yaw) {
        this.head.yaw = yaw * ((float)Math.PI / 180);
    }
}

