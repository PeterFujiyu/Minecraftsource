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
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArrowEntityModel
extends EntityModel<ProjectileEntityRenderState> {
    public ArrowEntityModel(ModelPart arg) {
        super(arg, RenderLayer::getEntityCutout);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        lv2.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -2.5f, -2.5f, 0.0f, 5.0f, 5.0f), ModelTransform.of(-11.0f, 0.0f, 0.0f, 0.7853982f, 0.0f, 0.0f).withScale(0.8f));
        ModelPartBuilder lv3 = ModelPartBuilder.create().uv(0, 0).cuboid(-12.0f, -2.0f, 0.0f, 16.0f, 4.0f, 0.0f, Dilation.NONE, 1.0f, 0.8f);
        lv2.addChild("cross_1", lv3, ModelTransform.rotation(0.7853982f, 0.0f, 0.0f));
        lv2.addChild("cross_2", lv3, ModelTransform.rotation(2.3561945f, 0.0f, 0.0f));
        return TexturedModelData.of(lv.transform(arg -> arg.scaled(0.9f)), 32, 32);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState arg) {
        super.setAngles(arg);
        if (arg.shake > 0.0f) {
            float f = -MathHelper.sin(arg.shake * 3.0f) * arg.shake;
            this.root.roll += f * ((float)Math.PI / 180);
        }
    }
}

