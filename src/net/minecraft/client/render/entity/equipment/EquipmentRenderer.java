/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EquipmentRenderer {
    private static final int field_54178 = 0;
    private final EquipmentModelLoader equipmentModelLoader;
    private final Function<LayerTextureKey, Identifier> layerTextures;
    private final Function<TrimSpriteKey, Sprite> trimSprites;

    public EquipmentRenderer(EquipmentModelLoader equipmentModelLoader, SpriteAtlasTexture armorTrimsAtlas) {
        this.equipmentModelLoader = equipmentModelLoader;
        this.layerTextures = Util.memoize(key -> key.layer.getFullTextureId(key.layerType));
        this.trimSprites = Util.memoize(key -> armorTrimsAtlas.getSprite(key.getTexture()));
    }

    public void render(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        this.render(layerType, assetKey, model, stack, matrices, vertexConsumers, light, null);
    }

    public void render(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture) {
        List<EquipmentModel.Layer> list = this.equipmentModelLoader.get(assetKey).getLayers(layerType);
        if (list.isEmpty()) {
            return;
        }
        int j = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, 0) : 0;
        boolean bl = stack.hasGlint();
        for (EquipmentModel.Layer lv : list) {
            int k = EquipmentRenderer.getDyeColor(lv, j);
            if (k == 0) continue;
            Identifier lv2 = lv.usePlayerTexture() && texture != null ? texture : this.layerTextures.apply(new LayerTextureKey(layerType, lv));
            VertexConsumer lv3 = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(lv2), bl);
            model.render(matrices, lv3, light, OverlayTexture.DEFAULT_UV, k);
            bl = false;
        }
        ArmorTrim lv4 = stack.get(DataComponentTypes.TRIM);
        if (lv4 != null) {
            Sprite lv5 = this.trimSprites.apply(new TrimSpriteKey(lv4, layerType, assetKey));
            VertexConsumer lv6 = lv5.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(lv4.pattern().value().decal())));
            model.render(matrices, lv6, light, OverlayTexture.DEFAULT_UV);
        }
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int j = optional.get().colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != 0 ? dyeColor : j;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
    }

    @Environment(value=EnvType.CLIENT)
    record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
        private static String getAssetName(RegistryEntry<ArmorTrimMaterial> material, RegistryKey<EquipmentAsset> assetKey) {
            String string = material.value().overrideArmorAssets().get(assetKey);
            if (string != null) {
                return string;
            }
            return material.value().assetName();
        }

        public Identifier getTexture() {
            Identifier lv = this.trim.pattern().value().assetId();
            String string = TrimSpriteKey.getAssetName(this.trim.material(), this.equipmentAssetId);
            return lv.withPath(path -> "trims/entity/" + this.layerType.asString() + "/" + path + "_" + string);
        }
    }
}

