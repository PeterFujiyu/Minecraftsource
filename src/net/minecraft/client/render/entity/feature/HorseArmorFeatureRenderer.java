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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class HorseArmorFeatureRenderer
extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
    private final HorseEntityModel model;
    private final HorseEntityModel babyModel;
    private final EquipmentRenderer equipmentRenderer;

    public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.equipmentRenderer = equipmentRenderer;
        this.model = new HorseEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR));
        this.babyModel = new HorseEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR_BABY));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, HorseEntityRenderState arg3, float f, float g) {
        ItemStack lv = arg3.armor;
        EquippableComponent lv2 = lv.get(DataComponentTypes.EQUIPPABLE);
        if (lv2 == null || lv2.assetId().isEmpty()) {
            return;
        }
        HorseEntityModel lv3 = arg3.baby ? this.babyModel : this.model;
        lv3.setAngles(arg3);
        this.equipmentRenderer.render(EquipmentModel.LayerType.HORSE_BODY, lv2.assetId().get(), lv3, lv, arg, arg2, i);
    }
}

