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
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Environment(value=EnvType.CLIENT)
public abstract class BipedEntityRenderer<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>>
extends AgeableMobEntityRenderer<T, S, M> {
    public BipedEntityRenderer(EntityRendererFactory.Context context, M model, float shadowRadius) {
        this(context, model, model, shadowRadius);
    }

    public BipedEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale) {
        this(context, model, babyModel, scale, HeadFeatureRenderer.HeadTransformation.DEFAULT);
    }

    public BipedEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale, HeadFeatureRenderer.HeadTransformation headTransformation) {
        super(context, model, babyModel, scale);
        this.addFeature(new HeadFeatureRenderer(this, context.getEntityModels(), headTransformation));
        this.addFeature(new ElytraFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    protected BipedEntityModel.ArmPose getArmPose(T entity, Arm arm) {
        return BipedEntityModel.ArmPose.EMPTY;
    }

    @Override
    public void updateRenderState(T arg, S arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        BipedEntityRenderer.updateBipedRenderState(arg, arg2, f, this.itemModelResolver);
        ((BipedEntityRenderState)arg2).leftArmPose = this.getArmPose(arg, Arm.LEFT);
        ((BipedEntityRenderState)arg2).rightArmPose = this.getArmPose(arg, Arm.RIGHT);
    }

    public static void updateBipedRenderState(LivingEntity entity, BipedEntityRenderState state, float tickDelta, ItemModelManager itemModelResolver) {
        ArmedEntityRenderState.updateRenderState(entity, state, itemModelResolver);
        state.isInSneakingPose = entity.isInSneakingPose();
        state.isGliding = entity.isGliding();
        state.isSwimming = entity.isInSwimmingPose();
        state.hasVehicle = entity.hasVehicle();
        state.limbAmplitudeInverse = 1.0f;
        if (state.isGliding) {
            state.limbAmplitudeInverse = (float)entity.getVelocity().lengthSquared();
            state.limbAmplitudeInverse /= 0.2f;
            state.limbAmplitudeInverse *= state.limbAmplitudeInverse * state.limbAmplitudeInverse;
        }
        if (state.limbAmplitudeInverse < 1.0f) {
            state.limbAmplitudeInverse = 1.0f;
        }
        state.handSwingProgress = entity.getHandSwingProgress(tickDelta);
        state.leaningPitch = entity.getLeaningPitch(tickDelta);
        state.preferredArm = BipedEntityRenderer.getPreferredArm(entity);
        state.activeHand = entity.getActiveHand();
        state.crossbowPullTime = CrossbowItem.getPullTime(entity.getActiveItem(), entity);
        state.itemUseTime = entity.getItemUseTime();
        state.isUsingItem = entity.isUsingItem();
        state.leftWingPitch = entity.elytraFlightController.leftWingPitch(tickDelta);
        state.leftWingYaw = entity.elytraFlightController.leftWingYaw(tickDelta);
        state.leftWingRoll = entity.elytraFlightController.leftWingRoll(tickDelta);
        state.equippedHeadStack = BipedEntityRenderer.getEquippedStack(entity, EquipmentSlot.HEAD);
        state.equippedChestStack = BipedEntityRenderer.getEquippedStack(entity, EquipmentSlot.CHEST);
        state.equippedLegsStack = BipedEntityRenderer.getEquippedStack(entity, EquipmentSlot.LEGS);
        state.equippedFeetStack = BipedEntityRenderer.getEquippedStack(entity, EquipmentSlot.FEET);
    }

    private static ItemStack getEquippedStack(LivingEntity entity, EquipmentSlot slot) {
        ItemStack lv = entity.getEquippedStack(slot);
        return ArmorFeatureRenderer.hasModel(lv, slot) ? lv.copy() : ItemStack.EMPTY;
    }

    private static Arm getPreferredArm(LivingEntity entity) {
        Arm lv = entity.getMainArm();
        return entity.preferredHand == Hand.MAIN_HAND ? lv : lv.getOpposite();
    }
}

