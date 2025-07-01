/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class ArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>>
extends FeatureRenderer<S, M> {
    private final A innerModel;
    private final A outerModel;
    private final A babyInnerModel;
    private final A babyOuterModel;
    private final EquipmentRenderer equipmentRenderer;

    public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, A innerModel, A outerModel, EquipmentRenderer equipmentRenderer) {
        this(context, innerModel, outerModel, innerModel, outerModel, equipmentRenderer);
    }

    public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, A innerModel, A outerModel, A babyInnerModel, A babyOuterModel, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
        this.babyInnerModel = babyInnerModel;
        this.babyOuterModel = babyOuterModel;
        this.equipmentRenderer = equipmentRenderer;
    }

    public static boolean hasModel(ItemStack stack, EquipmentSlot slot) {
        EquippableComponent lv = stack.get(DataComponentTypes.EQUIPPABLE);
        return lv != null && ArmorFeatureRenderer.hasModel(lv, slot);
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.assetId().isPresent() && component.slot() == slot;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, S arg3, float f, float g) {
        this.renderArmor(arg, arg2, ((BipedEntityRenderState)arg3).equippedChestStack, EquipmentSlot.CHEST, i, this.getModel(arg3, EquipmentSlot.CHEST));
        this.renderArmor(arg, arg2, ((BipedEntityRenderState)arg3).equippedLegsStack, EquipmentSlot.LEGS, i, this.getModel(arg3, EquipmentSlot.LEGS));
        this.renderArmor(arg, arg2, ((BipedEntityRenderState)arg3).equippedFeetStack, EquipmentSlot.FEET, i, this.getModel(arg3, EquipmentSlot.FEET));
        this.renderArmor(arg, arg2, ((BipedEntityRenderState)arg3).equippedHeadStack, EquipmentSlot.HEAD, i, this.getModel(arg3, EquipmentSlot.HEAD));
    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
        EquippableComponent lv = stack.get(DataComponentTypes.EQUIPPABLE);
        if (lv == null || !ArmorFeatureRenderer.hasModel(lv, slot)) {
            return;
        }
        ((BipedEntityModel)this.getContextModel()).copyTransforms(armorModel);
        this.setVisible(armorModel, slot);
        EquipmentModel.LayerType lv2 = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
        this.equipmentRenderer.render(lv2, lv.assetId().orElseThrow(), (Model)armorModel, stack, matrices, vertexConsumers, light);
    }

    protected void setVisible(A bipedModel, EquipmentSlot slot) {
        ((BipedEntityModel)bipedModel).setVisible(false);
        switch (slot) {
            case HEAD: {
                ((BipedEntityModel)bipedModel).head.visible = true;
                ((BipedEntityModel)bipedModel).hat.visible = true;
                break;
            }
            case CHEST: {
                ((BipedEntityModel)bipedModel).body.visible = true;
                ((BipedEntityModel)bipedModel).rightArm.visible = true;
                ((BipedEntityModel)bipedModel).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((BipedEntityModel)bipedModel).body.visible = true;
                ((BipedEntityModel)bipedModel).rightLeg.visible = true;
                ((BipedEntityModel)bipedModel).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((BipedEntityModel)bipedModel).rightLeg.visible = true;
                ((BipedEntityModel)bipedModel).leftLeg.visible = true;
            }
        }
    }

    private A getModel(S state, EquipmentSlot slot) {
        if (this.usesInnerModel(slot)) {
            return ((BipedEntityRenderState)state).baby ? this.babyInnerModel : this.innerModel;
        }
        return ((BipedEntityRenderState)state).baby ? this.babyOuterModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }
}

