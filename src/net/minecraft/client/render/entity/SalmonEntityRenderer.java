/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityRenderer
extends MobEntityRenderer<SalmonEntity, SalmonEntityRenderState, SalmonEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/salmon.png");
    private final SalmonEntityModel smallModel;
    private final SalmonEntityModel mediumModel;
    private final SalmonEntityModel largeModel;

    public SalmonEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new SalmonEntityModel(arg.getPart(EntityModelLayers.SALMON)), 0.4f);
        this.smallModel = new SalmonEntityModel(arg.getPart(EntityModelLayers.SALMON_SMALL));
        this.mediumModel = new SalmonEntityModel(arg.getPart(EntityModelLayers.SALMON));
        this.largeModel = new SalmonEntityModel(arg.getPart(EntityModelLayers.SALMON_LARGE));
    }

    @Override
    public void updateRenderState(SalmonEntity arg, SalmonEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.variant = arg.getVariant();
    }

    @Override
    public Identifier getTexture(SalmonEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public SalmonEntityRenderState createRenderState() {
        return new SalmonEntityRenderState();
    }

    @Override
    protected void setupTransforms(SalmonEntityRenderState arg, MatrixStack arg2, float f, float g) {
        super.setupTransforms(arg, arg2, f, g);
        float h = 1.0f;
        float i = 1.0f;
        if (!arg.touchingWater) {
            h = 1.3f;
            i = 1.7f;
        }
        float j = h * 4.3f * MathHelper.sin(i * 0.6f * arg.age);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        if (!arg.touchingWater) {
            arg2.translate(0.2f, 0.1f, 0.0f);
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    @Override
    public void render(SalmonEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.model = arg.variant == SalmonEntity.Variant.SMALL ? this.smallModel : (arg.variant == SalmonEntity.Variant.LARGE ? this.largeModel : this.mediumModel);
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SalmonEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

