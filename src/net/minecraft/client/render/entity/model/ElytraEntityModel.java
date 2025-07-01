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
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel
extends EntityModel<BipedEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5f);
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ElytraEntityModel(ModelPart arg) {
        super(arg);
        this.leftWing = arg.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = arg.getChild(EntityModelPartNames.RIGHT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        Dilation lv3 = new Dilation(1.0f);
        lv2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(22, 0).cuboid(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, lv3), ModelTransform.of(5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, -0.2617994f));
        lv2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(22, 0).mirrored().cuboid(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, lv3), ModelTransform.of(-5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, 0.2617994f));
        return TexturedModelData.of(lv, 64, 32);
    }

    @Override
    public void setAngles(BipedEntityRenderState arg) {
        super.setAngles(arg);
        this.leftWing.pivotY = arg.isInSneakingPose ? 3.0f : 0.0f;
        this.leftWing.pitch = arg.leftWingPitch;
        this.leftWing.roll = arg.leftWingRoll;
        this.leftWing.yaw = arg.leftWingYaw;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.pivotY = this.leftWing.pivotY;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.roll = -this.leftWing.roll;
    }
}

