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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityRenderer
extends ZombieBaseEntityRenderer<DrownedEntity, ZombieEntityRenderState, DrownedEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/drowned.png");

    public DrownedEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new DrownedEntityModel(arg.getPart(EntityModelLayers.DROWNED)), new DrownedEntityModel(arg.getPart(EntityModelLayers.DROWNED_BABY)), new DrownedEntityModel(arg.getPart(EntityModelLayers.DROWNED_INNER_ARMOR)), new DrownedEntityModel(arg.getPart(EntityModelLayers.DROWNED_OUTER_ARMOR)), new DrownedEntityModel(arg.getPart(EntityModelLayers.DROWNED_BABY_INNER_ARMOR)), new DrownedEntityModel(arg.getPart(EntityModelLayers.DROWNED_BABY_OUTER_ARMOR)));
        this.addFeature(new DrownedOverlayFeatureRenderer(this, arg.getEntityModels()));
    }

    @Override
    public ZombieEntityRenderState createRenderState() {
        return new ZombieEntityRenderState();
    }

    @Override
    public Identifier getTexture(ZombieEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(ZombieEntityRenderState arg, MatrixStack arg2, float f, float g) {
        super.setupTransforms(arg, arg2, f, g);
        float h = arg.leaningPitch;
        if (h > 0.0f) {
            float i = -10.0f - arg.pitch;
            float j = MathHelper.lerp(h, 0.0f, i);
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j), 0.0f, arg.height / 2.0f / g, 0.0f);
        }
    }

    @Override
    protected BipedEntityModel.ArmPose getArmPose(DrownedEntity arg, Arm arg2) {
        ItemStack lv = arg.getStackInArm(arg2);
        if (arg.getMainArm() == arg2 && arg.isAttacking() && lv.isOf(Items.TRIDENT)) {
            return BipedEntityModel.ArmPose.THROW_SPEAR;
        }
        return BipedEntityModel.ArmPose.EMPTY;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

