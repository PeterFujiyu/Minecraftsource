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
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityModel
extends EntityModel<EndCrystalEntityRenderState> {
    private static final String OUTER_GLASS = "outer_glass";
    private static final String INNER_GLASS = "inner_glass";
    private static final String BASE = "base";
    private static final float field_52906 = (float)Math.sin(0.7853981633974483);
    public final ModelPart base;
    public final ModelPart outerGlass;
    public final ModelPart innerGlass;
    public final ModelPart cube;

    public EndCrystalEntityModel(ModelPart arg) {
        super(arg);
        this.base = arg.getChild(BASE);
        this.outerGlass = arg.getChild(OUTER_GLASS);
        this.innerGlass = this.outerGlass.getChild(INNER_GLASS);
        this.cube = this.innerGlass.getChild(EntityModelPartNames.CUBE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        float f = 0.875f;
        ModelPartBuilder lv3 = ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        ModelPartData lv4 = lv2.addChild(OUTER_GLASS, lv3, ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        ModelPartData lv5 = lv4.addChild(INNER_GLASS, lv3, ModelTransform.NONE.withScale(0.875f));
        lv5.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE.withScale(0.765625f));
        lv2.addChild(BASE, ModelPartBuilder.create().uv(0, 16).cuboid(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f), ModelTransform.NONE);
        return TexturedModelData.of(lv, 64, 32);
    }

    @Override
    public void setAngles(EndCrystalEntityRenderState arg) {
        super.setAngles(arg);
        this.base.visible = arg.baseVisible;
        float f = arg.age * 3.0f;
        float g = EndCrystalEntityRenderer.getYOffset(arg.age) * 16.0f;
        this.outerGlass.pivotY += g / 2.0f;
        this.outerGlass.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(f).rotateAxis(1.0471976f, field_52906, 0.0f, field_52906));
        this.innerGlass.rotate(new Quaternionf().setAngleAxis(1.0471976f, field_52906, 0.0f, field_52906).rotateY(f * ((float)Math.PI / 180)));
        this.cube.rotate(new Quaternionf().setAngleAxis(1.0471976f, field_52906, 0.0f, field_52906).rotateY(f * ((float)Math.PI / 180)));
    }
}

