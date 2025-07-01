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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armorstand/wood.png");
    private final ArmorStandArmorEntityModel mainModel = (ArmorStandArmorEntityModel)this.getModel();
    private final ArmorStandArmorEntityModel smallModel;

    public ArmorStandEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new ArmorStandEntityModel(arg.getPart(EntityModelLayers.ARMOR_STAND)), 0.0f);
        this.smallModel = new ArmorStandEntityModel(arg.getPart(EntityModelLayers.ARMOR_STAND_SMALL));
        this.addFeature(new ArmorFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, new ArmorStandArmorEntityModel(arg.getPart(EntityModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorEntityModel(arg.getPart(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR)), new ArmorStandArmorEntityModel(arg.getPart(EntityModelLayers.ARMOR_STAND_SMALL_INNER_ARMOR)), new ArmorStandArmorEntityModel(arg.getPart(EntityModelLayers.ARMOR_STAND_SMALL_OUTER_ARMOR)), arg.getEquipmentRenderer()));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this, arg.getEntityModels(), arg.getEquipmentRenderer()));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this, arg.getEntityModels()));
    }

    @Override
    public Identifier getTexture(ArmorStandEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public ArmorStandEntityRenderState createRenderState() {
        return new ArmorStandEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmorStandEntity arg, ArmorStandEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        BipedEntityRenderer.updateBipedRenderState(arg, arg2, f, this.itemModelResolver);
        arg2.yaw = MathHelper.lerpAngleDegrees(f, arg.prevYaw, arg.getYaw());
        arg2.marker = arg.isMarker();
        arg2.small = arg.isSmall();
        arg2.showArms = arg.shouldShowArms();
        arg2.showBasePlate = arg.shouldShowBasePlate();
        arg2.bodyRotation = arg.getBodyRotation();
        arg2.headRotation = arg.getHeadRotation();
        arg2.leftArmRotation = arg.getLeftArmRotation();
        arg2.rightArmRotation = arg.getRightArmRotation();
        arg2.leftLegRotation = arg.getLeftLegRotation();
        arg2.rightLegRotation = arg.getRightLegRotation();
        arg2.timeSinceLastHit = (float)(arg.getWorld().getTime() - arg.lastHitTime) + f;
    }

    @Override
    public void render(ArmorStandEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.model = arg.small ? this.smallModel : this.mainModel;
        super.render(arg, arg2, arg3, i);
    }

    @Override
    protected void setupTransforms(ArmorStandEntityRenderState arg, MatrixStack arg2, float f, float g) {
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        if (arg.timeSinceLastHit < 5.0f) {
            arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(arg.timeSinceLastHit / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity arg, double d) {
        return arg.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderLayer getRenderLayer(ArmorStandEntityRenderState arg, boolean bl, boolean bl2, boolean bl3) {
        if (!arg.marker) {
            return super.getRenderLayer(arg, bl, bl2, bl3);
        }
        Identifier lv = this.getTexture(arg);
        if (bl2) {
            return RenderLayer.getEntityTranslucent(lv, false);
        }
        if (bl) {
            return RenderLayer.getEntityCutoutNoCull(lv, false);
        }
        return null;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ArmorStandEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

