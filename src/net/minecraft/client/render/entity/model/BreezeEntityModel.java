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
import net.minecraft.client.render.entity.animation.BreezeAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BreezeEntityModel
extends EntityModel<BreezeEntityRenderState> {
    private static final float field_47431 = 0.6f;
    private static final float field_47432 = 0.8f;
    private static final float field_47433 = 1.0f;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart windBody;
    private final ModelPart windTop;
    private final ModelPart windMid;
    private final ModelPart windBottom;
    private final ModelPart rods;

    public BreezeEntityModel(ModelPart arg) {
        super(arg, RenderLayer::getEntityTranslucent);
        this.windBody = arg.getChild(EntityModelPartNames.WIND_BODY);
        this.windBottom = this.windBody.getChild(EntityModelPartNames.WIND_BOTTOM);
        this.windMid = this.windBottom.getChild(EntityModelPartNames.WIND_MID);
        this.windTop = this.windMid.getChild(EntityModelPartNames.WIND_TOP);
        this.head = arg.getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.HEAD);
        this.eyes = this.head.getChild(EntityModelPartNames.EYES);
        this.rods = arg.getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.RODS);
    }

    public static TexturedModelData getTexturedModelData(int textureWidth, int textureHeight) {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        ModelPartData lv4 = lv3.addChild(EntityModelPartNames.RODS, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 8.0f, 0.0f));
        lv4.addChild("rod_1", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of(2.5981f, -3.0f, 1.5f, -2.7489f, -1.0472f, 3.1416f));
        lv4.addChild("rod_2", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of(-2.5981f, -3.0f, 1.5f, -2.7489f, 1.0472f, 3.1416f));
        lv4.addChild("rod_3", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of(0.0f, -3.0f, -3.0f, 0.3927f, 0.0f, 0.0f));
        ModelPartData lv5 = lv3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(4, 24).cuboid(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new Dilation(0.0f)).uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 4.0f, 0.0f));
        lv5.addChild(EntityModelPartNames.EYES, ModelPartBuilder.create().uv(4, 24).cuboid(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new Dilation(0.0f)).uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        ModelPartData lv6 = lv2.addChild(EntityModelPartNames.WIND_BODY, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        ModelPartData lv7 = lv6.addChild(EntityModelPartNames.WIND_BOTTOM, ModelPartBuilder.create().uv(1, 83).cuboid(-2.5f, -7.0f, -2.5f, 5.0f, 7.0f, 5.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        ModelPartData lv8 = lv7.addChild(EntityModelPartNames.WIND_MID, ModelPartBuilder.create().uv(74, 28).cuboid(-6.0f, -6.0f, -6.0f, 12.0f, 6.0f, 12.0f, new Dilation(0.0f)).uv(78, 32).cuboid(-4.0f, -6.0f, -4.0f, 8.0f, 6.0f, 8.0f, new Dilation(0.0f)).uv(49, 71).cuboid(-2.5f, -6.0f, -2.5f, 5.0f, 6.0f, 5.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, -7.0f, 0.0f));
        lv8.addChild(EntityModelPartNames.WIND_TOP, ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -8.0f, -9.0f, 18.0f, 8.0f, 18.0f, new Dilation(0.0f)).uv(6, 6).cuboid(-6.0f, -8.0f, -6.0f, 12.0f, 8.0f, 12.0f, new Dilation(0.0f)).uv(105, 57).cuboid(-2.5f, -8.0f, -2.5f, 5.0f, 8.0f, 5.0f, new Dilation(0.0f)), ModelTransform.pivot(0.0f, -6.0f, 0.0f));
        return TexturedModelData.of(lv, textureWidth, textureHeight);
    }

    @Override
    public void setAngles(BreezeEntityRenderState arg) {
        super.setAngles(arg);
        this.animate(arg.idleAnimationState, BreezeAnimations.IDLE, arg.age);
        this.animate(arg.shootingAnimationState, BreezeAnimations.SHOOTING, arg.age);
        this.animate(arg.slidingAnimationState, BreezeAnimations.SLIDING, arg.age);
        this.animate(arg.slidingBackAnimationState, BreezeAnimations.SLIDING_BACK, arg.age);
        this.animate(arg.inhalingAnimationState, BreezeAnimations.INHALING, arg.age);
        this.animate(arg.longJumpingAnimationState, BreezeAnimations.LONG_JUMPING, arg.age);
    }

    public ModelPart getHead() {
        return this.head;
    }

    public ModelPart getEyes() {
        return this.eyes;
    }

    public ModelPart getRods() {
        return this.rods;
    }

    public ModelPart getWindBody() {
        return this.windBody;
    }
}

