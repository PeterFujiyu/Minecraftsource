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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class GhastEntityModel
extends EntityModel<GhastEntityRenderState> {
    private final ModelPart[] tentacles = new ModelPart[9];

    public GhastEntityModel(ModelPart arg) {
        super(arg);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = arg.getChild(GhastEntityModel.getTentacleName(i));
        }
    }

    private static String getTentacleName(int index) {
        return "tentacle" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        lv2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), ModelTransform.pivot(0.0f, 17.6f, 0.0f));
        Random lv3 = Random.create(1660L);
        for (int i = 0; i < 9; ++i) {
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float g = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int j = lv3.nextInt(7) + 8;
            lv2.addChild(GhastEntityModel.getTentacleName(i), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, j, 2.0f), ModelTransform.pivot(f, 24.6f, g));
        }
        return TexturedModelData.of(lv, 64, 32).transform(ModelTransformer.scaling(4.5f));
    }

    @Override
    public void setAngles(GhastEntityRenderState arg) {
        super.setAngles(arg);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].pitch = 0.2f * MathHelper.sin(arg.age * 0.3f + (float)i) + 0.4f;
        }
    }
}

