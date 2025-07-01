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
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel
extends EntityModel<ChickenEntityRenderState> {
    public static final String RED_THING = "red_thing";
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(Set.of("head", "beak", "red_thing"));
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ChickenEntityModel(ModelPart arg) {
        super(arg);
        this.head = arg.getChild(EntityModelPartNames.HEAD);
        this.rightLeg = arg.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = arg.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightWing = arg.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = arg.getChild(EntityModelPartNames.LEFT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        int i = 16;
        ModelPartData lv3 = lv2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), ModelTransform.pivot(0.0f, 15.0f, -4.0f));
        lv3.addChild(EntityModelPartNames.BEAK, ModelPartBuilder.create().uv(14, 0).cuboid(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), ModelTransform.NONE);
        lv3.addChild(RED_THING, ModelPartBuilder.create().uv(14, 4).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 9).cuboid(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder lv4 = ModelPartBuilder.create().uv(26, 0).cuboid(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        lv2.addChild(EntityModelPartNames.RIGHT_LEG, lv4, ModelTransform.pivot(-2.0f, 19.0f, 1.0f));
        lv2.addChild(EntityModelPartNames.LEFT_LEG, lv4, ModelTransform.pivot(1.0f, 19.0f, 1.0f));
        lv2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(24, 13).cuboid(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.pivot(-4.0f, 13.0f, 0.0f));
        lv2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(24, 13).cuboid(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.pivot(4.0f, 13.0f, 0.0f));
        return TexturedModelData.of(lv, 64, 32);
    }

    @Override
    public void setAngles(ChickenEntityRenderState arg) {
        super.setAngles(arg);
        float f = (MathHelper.sin(arg.flapProgress) + 1.0f) * arg.maxWingDeviation;
        this.head.pitch = arg.pitch * ((float)Math.PI / 180);
        this.head.yaw = arg.yawDegrees * ((float)Math.PI / 180);
        float g = arg.limbAmplitudeMultiplier;
        float h = arg.limbFrequency;
        this.rightLeg.pitch = MathHelper.cos(h * 0.6662f) * 1.4f * g;
        this.leftLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightWing.roll = f;
        this.leftWing.roll = -f;
    }
}

