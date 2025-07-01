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
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ElytraFeatureRenderer<S extends BipedEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    private final ElytraEntityModel model;
    private final ElytraEntityModel babyModel;
    private final EquipmentRenderer equipmentRenderer;

    public ElytraFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.model = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
        this.babyModel = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA_BABY));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, S arg3, float f, float g) {
        ItemStack lv = ((BipedEntityRenderState)arg3).equippedChestStack;
        EquippableComponent lv2 = lv.get(DataComponentTypes.EQUIPPABLE);
        if (lv2 == null || lv2.assetId().isEmpty()) {
            return;
        }
        Identifier lv3 = ElytraFeatureRenderer.getTexture(arg3);
        ElytraEntityModel lv4 = ((BipedEntityRenderState)arg3).baby ? this.babyModel : this.model;
        arg.push();
        arg.translate(0.0f, 0.0f, 0.125f);
        lv4.setAngles((BipedEntityRenderState)arg3);
        this.equipmentRenderer.render(EquipmentModel.LayerType.WINGS, lv2.assetId().get(), lv4, lv, arg, arg2, i, lv3);
        arg.pop();
    }

    @Nullable
    private static Identifier getTexture(BipedEntityRenderState state) {
        if (state instanceof PlayerEntityRenderState) {
            PlayerEntityRenderState lv = (PlayerEntityRenderState)state;
            SkinTextures lv2 = lv.skinTextures;
            if (lv2.elytraTexture() != null) {
                return lv2.elytraTexture();
            }
            if (lv2.capeTexture() != null && lv.capeVisible) {
                return lv2.capeTexture();
            }
        }
        return null;
    }
}

