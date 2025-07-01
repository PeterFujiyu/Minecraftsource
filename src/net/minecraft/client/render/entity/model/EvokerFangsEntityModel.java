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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EvokerFangsEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityModel
extends EntityModel<EvokerFangsEntityRenderState> {
    private static final String BASE = "base";
    private static final String UPPER_JAW = "upper_jaw";
    private static final String LOWER_JAW = "lower_jaw";
    private final ModelPart base;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;

    public EvokerFangsEntityModel(ModelPart arg) {
        super(arg);
        this.base = arg.getChild(BASE);
        this.upperJaw = this.base.getChild(UPPER_JAW);
        this.lowerJaw = this.base.getChild(LOWER_JAW);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(BASE, ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f), ModelTransform.pivot(-5.0f, 24.0f, -5.0f));
        ModelPartBuilder lv4 = ModelPartBuilder.create().uv(40, 0).cuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        lv3.addChild(UPPER_JAW, lv4, ModelTransform.of(6.5f, 0.0f, 1.0f, 0.0f, 0.0f, 2.042035f));
        lv3.addChild(LOWER_JAW, lv4, ModelTransform.of(3.5f, 0.0f, 9.0f, 0.0f, (float)Math.PI, 4.2411504f));
        return TexturedModelData.of(lv, 64, 32);
    }

    @Override
    public void setAngles(EvokerFangsEntityRenderState arg) {
        super.setAngles(arg);
        float f = arg.animationProgress;
        float g = Math.min(f * 2.0f, 1.0f);
        g = 1.0f - g * g * g;
        this.upperJaw.roll = (float)Math.PI - g * 0.35f * (float)Math.PI;
        this.lowerJaw.roll = (float)Math.PI + g * 0.35f * (float)Math.PI;
        this.base.pivotY -= (f + MathHelper.sin(f * 2.7f)) * 7.2f;
        float h = 1.0f;
        if (f > 0.9f) {
            h *= (1.0f - f) / 0.1f;
        }
        this.root.pivotY = 24.0f - 20.0f * h;
        this.root.xScale = h;
        this.root.yScale = h;
        this.root.zScale = h;
    }
}

