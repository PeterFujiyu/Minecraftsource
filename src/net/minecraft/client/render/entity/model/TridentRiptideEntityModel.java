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
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TridentRiptideEntityModel
extends EntityModel<PlayerEntityRenderState> {
    private static final int field_54016 = 2;
    private final ModelPart[] parts = new ModelPart[2];

    public TridentRiptideEntityModel(ModelPart arg) {
        super(arg);
        for (int i = 0; i < 2; ++i) {
            this.parts[i] = arg.getChild(TridentRiptideEntityModel.getPartName(i));
        }
    }

    private static String getPartName(int index) {
        return "box" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        for (int i = 0; i < 2; ++i) {
            float f = -3.2f + 9.6f * (float)(i + 1);
            float g = 0.75f * (float)(i + 1);
            lv2.addChild(TridentRiptideEntityModel.getPartName(i), ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -16.0f + f, -8.0f, 16.0f, 32.0f, 16.0f), ModelTransform.NONE.withScale(g));
        }
        return TexturedModelData.of(lv, 64, 64);
    }

    @Override
    public void setAngles(PlayerEntityRenderState arg) {
        super.setAngles(arg);
        for (int i = 0; i < this.parts.length; ++i) {
            float f = arg.age * (float)(-(45 + (i + 1) * 5));
            this.parts[i].yaw = MathHelper.wrapDegrees(f) * ((float)Math.PI / 180);
        }
    }
}

