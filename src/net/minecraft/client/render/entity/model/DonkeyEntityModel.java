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
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel
extends AbstractHorseEntityModel<DonkeyEntityRenderState> {
    public static final float field_55113 = 0.87f;
    public static final float field_55114 = 0.92f;
    private final ModelPart leftChest;
    private final ModelPart rightChest;

    public DonkeyEntityModel(ModelPart arg) {
        super(arg);
        this.leftChest = this.body.getChild(EntityModelPartNames.LEFT_CHEST);
        this.rightChest = this.body.getChild(EntityModelPartNames.RIGHT_CHEST);
    }

    public static TexturedModelData getTexturedModelData(float scale) {
        ModelData lv = AbstractHorseEntityModel.getModelData(Dilation.NONE);
        DonkeyEntityModel.addDonkeyParts(lv.getRoot());
        return TexturedModelData.of(lv, 64, 64).transform(ModelTransformer.scaling(scale));
    }

    public static TexturedModelData getBabyTexturedModelData(float scale) {
        ModelData lv = AbstractHorseEntityModel.getBabyModelData(Dilation.NONE);
        DonkeyEntityModel.addDonkeyParts(lv.getRoot());
        return TexturedModelData.of(AbstractHorseEntityModel.BABY_TRANSFORMER.apply(lv), 64, 64).transform(ModelTransformer.scaling(scale));
    }

    private static void addDonkeyParts(ModelPartData root) {
        ModelPartData lv = root.getChild(EntityModelPartNames.BODY);
        ModelPartBuilder lv2 = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        lv.addChild(EntityModelPartNames.LEFT_CHEST, lv2, ModelTransform.of(6.0f, -8.0f, 0.0f, 0.0f, -1.5707964f, 0.0f));
        lv.addChild(EntityModelPartNames.RIGHT_CHEST, lv2, ModelTransform.of(-6.0f, -8.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        ModelPartData lv3 = root.getChild("head_parts").getChild(EntityModelPartNames.HEAD);
        ModelPartBuilder lv4 = ModelPartBuilder.create().uv(0, 12).cuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        lv3.addChild(EntityModelPartNames.LEFT_EAR, lv4, ModelTransform.of(1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, 0.2617994f));
        lv3.addChild(EntityModelPartNames.RIGHT_EAR, lv4, ModelTransform.of(-1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, -0.2617994f));
    }

    @Override
    public void setAngles(DonkeyEntityRenderState arg) {
        super.setAngles(arg);
        this.leftChest.visible = arg.hasChest;
        this.rightChest.visible = arg.hasChest;
    }
}

