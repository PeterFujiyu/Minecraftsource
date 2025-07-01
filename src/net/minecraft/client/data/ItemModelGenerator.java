/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.render.item.model.BundleSelectedItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.render.item.model.special.TridentModelRenderer;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.bool.BrokenProperty;
import net.minecraft.client.render.item.property.bool.BundleHasSelectedItemProperty;
import net.minecraft.client.render.item.property.bool.FishingRodCastProperty;
import net.minecraft.client.render.item.property.numeric.CompassProperty;
import net.minecraft.client.render.item.property.numeric.CompassState;
import net.minecraft.client.render.item.property.numeric.CrossbowPullProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.TimeProperty;
import net.minecraft.client.render.item.property.numeric.UseCycleProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.render.item.property.select.ChargeTypeProperty;
import net.minecraft.client.render.item.property.select.DisplayContextProperty;
import net.minecraft.client.render.item.property.select.TrimMaterialProperty;
import net.minecraft.client.render.item.tint.DyeTintSource;
import net.minecraft.client.render.item.tint.FireworkTintSource;
import net.minecraft.client.render.item.tint.MapColorTintSource;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ItemModelGenerator {
    private static final TintSource UNTINTED = ItemModels.constantTintSource(-1);
    private static final String HELMET = "helmet";
    private static final String CHESTPLATE = "chestplate";
    private static final String LEGGINGS = "leggings";
    private static final String BOOTS = "boots";
    private static final List<TrimMaterial> TRIM_MATERIALS = List.of(new TrimMaterial("quartz", ArmorTrimMaterials.QUARTZ, Map.of()), new TrimMaterial("iron", ArmorTrimMaterials.IRON, Map.of(EquipmentAssetKeys.IRON, "iron_darker")), new TrimMaterial("netherite", ArmorTrimMaterials.NETHERITE, Map.of(EquipmentAssetKeys.NETHERITE, "netherite_darker")), new TrimMaterial("redstone", ArmorTrimMaterials.REDSTONE, Map.of()), new TrimMaterial("copper", ArmorTrimMaterials.COPPER, Map.of()), new TrimMaterial("gold", ArmorTrimMaterials.GOLD, Map.of(EquipmentAssetKeys.GOLD, "gold_darker")), new TrimMaterial("emerald", ArmorTrimMaterials.EMERALD, Map.of()), new TrimMaterial("diamond", ArmorTrimMaterials.DIAMOND, Map.of(EquipmentAssetKeys.DIAMOND, "diamond_darker")), new TrimMaterial("lapis", ArmorTrimMaterials.LAPIS, Map.of()), new TrimMaterial("amethyst", ArmorTrimMaterials.AMETHYST, Map.of()), new TrimMaterial("resin", ArmorTrimMaterials.RESIN, Map.of()));
    private final ItemModelOutput output;
    private final BiConsumer<Identifier, ModelSupplier> modelCollector;

    public ItemModelGenerator(ItemModelOutput output, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        this.output = output;
        this.modelCollector = modelCollector;
    }

    private void register(Item item) {
        this.output.accept(item, ItemModels.basic(ModelIds.getItemModelId(item)));
    }

    private Identifier upload(Item item, Model model) {
        return model.upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), this.modelCollector);
    }

    private void register(Item item, Model model) {
        this.output.accept(item, ItemModels.basic(this.upload(item, model)));
    }

    private Identifier registerSubModel(Item item, String suffix, Model model) {
        return model.upload(ModelIds.getItemSubModelId(item, suffix), TextureMap.layer0(TextureMap.getSubId(item, suffix)), this.modelCollector);
    }

    private Identifier uploadWithTextureSource(Item item, Item textureSourceItem, Model model) {
        return model.upload(ModelIds.getItemModelId(item), TextureMap.layer0(textureSourceItem), this.modelCollector);
    }

    private void registerWithTextureSource(Item item, Item textureSourceItem, Model model) {
        this.output.accept(item, ItemModels.basic(this.uploadWithTextureSource(item, textureSourceItem, model)));
    }

    private void registerWithTintedOverlay(Item item, TintSource tint) {
        this.registerWithTintedLayer(item, "_overlay", tint);
    }

    private void registerWithTintedLayer(Item item, String layer1Suffix, TintSource tint) {
        Identifier lv = this.uploadTwoLayers(item, TextureMap.getId(item), TextureMap.getSubId(item, layer1Suffix));
        this.output.accept(item, ItemModels.tinted(lv, UNTINTED, tint));
    }

    private List<RangeDispatchItemModel.Entry> createCompassRangeDispatchEntries(Item item) {
        ArrayList<RangeDispatchItemModel.Entry> list = new ArrayList<RangeDispatchItemModel.Entry>();
        ItemModel.Unbaked lv = ItemModels.basic(this.registerSubModel(item, "_16", Models.GENERATED));
        list.add(ItemModels.rangeDispatchEntry(lv, 0.0f));
        for (int i = 1; i < 32; ++i) {
            int j = MathHelper.floorMod(i - 16, 32);
            ItemModel.Unbaked lv2 = ItemModels.basic(this.registerSubModel(item, String.format(Locale.ROOT, "_%02d", j), Models.GENERATED));
            list.add(ItemModels.rangeDispatchEntry(lv2, (float)i - 0.5f));
        }
        list.add(ItemModels.rangeDispatchEntry(lv, 31.5f));
        return list;
    }

    private void registerCompass(Item item) {
        List<RangeDispatchItemModel.Entry> list = this.createCompassRangeDispatchEntries(item);
        this.output.accept(item, ItemModels.condition(ItemModels.hasComponentProperty(DataComponentTypes.LODESTONE_TRACKER), ItemModels.rangeDispatch((NumericProperty)new CompassProperty(true, CompassState.Target.LODESTONE), 32.0f, list), ItemModels.overworldSelect(ItemModels.rangeDispatch((NumericProperty)new CompassProperty(true, CompassState.Target.SPAWN), 32.0f, list), ItemModels.rangeDispatch((NumericProperty)new CompassProperty(true, CompassState.Target.NONE), 32.0f, list))));
    }

    private void registerRecoveryCompass(Item item) {
        this.output.accept(item, ItemModels.rangeDispatch((NumericProperty)new CompassProperty(true, CompassState.Target.RECOVERY), 32.0f, this.createCompassRangeDispatchEntries(item)));
    }

    private void registerClock(Item clock) {
        ArrayList<RangeDispatchItemModel.Entry> list = new ArrayList<RangeDispatchItemModel.Entry>();
        ItemModel.Unbaked lv = ItemModels.basic(this.registerSubModel(clock, "_00", Models.GENERATED));
        list.add(ItemModels.rangeDispatchEntry(lv, 0.0f));
        for (int i = 1; i < 64; ++i) {
            ItemModel.Unbaked lv2 = ItemModels.basic(this.registerSubModel(clock, String.format(Locale.ROOT, "_%02d", i), Models.GENERATED));
            list.add(ItemModels.rangeDispatchEntry(lv2, (float)i - 0.5f));
        }
        list.add(ItemModels.rangeDispatchEntry(lv, 63.5f));
        this.output.accept(clock, ItemModels.overworldSelect(ItemModels.rangeDispatch((NumericProperty)new TimeProperty(true, TimeProperty.Source.DAYTIME), 64.0f, list), ItemModels.rangeDispatch((NumericProperty)new TimeProperty(true, TimeProperty.Source.RANDOM), 64.0f, list)));
    }

    private Identifier uploadTwoLayers(Item item, Identifier layer0, Identifier layer1) {
        return Models.GENERATED_TWO_LAYERS.upload(item, TextureMap.layered(layer0, layer1), this.modelCollector);
    }

    private Identifier uploadArmor(Identifier id, Identifier layer0, Identifier layer1) {
        return Models.GENERATED_TWO_LAYERS.upload(id, TextureMap.layered(layer0, layer1), this.modelCollector);
    }

    private void uploadArmor(Identifier id, Identifier layer0, Identifier layer1, Identifier layer2) {
        Models.GENERATED_THREE_LAYERS.upload(id, TextureMap.layered(layer0, layer1, layer2), this.modelCollector);
    }

    private void registerArmor(Item item, RegistryKey<EquipmentAsset> equipmentKey, String type, boolean dyeable) {
        ItemModel.Unbaked lv8;
        Identifier lv = ModelIds.getItemModelId(item);
        Identifier lv2 = TextureMap.getId(item);
        Identifier lv3 = TextureMap.getSubId(item, "_overlay");
        ArrayList list = new ArrayList(TRIM_MATERIALS.size());
        for (TrimMaterial lv4 : TRIM_MATERIALS) {
            ItemModel.Unbaked lv7;
            Identifier lv5 = lv.withSuffixedPath("_" + lv4.name() + "_trim");
            Identifier lv6 = Identifier.ofVanilla("trims/items/" + type + "_trim_" + lv4.texture(equipmentKey));
            if (dyeable) {
                this.uploadArmor(lv5, lv2, lv3, lv6);
                lv7 = ItemModels.tinted(lv5, new DyeTintSource(-6265536));
            } else {
                this.uploadArmor(lv5, lv2, lv6);
                lv7 = ItemModels.basic(lv5);
            }
            list.add(ItemModels.switchCase(lv4.materialKey, lv7));
        }
        if (dyeable) {
            Models.GENERATED_TWO_LAYERS.upload(lv, TextureMap.layered(lv2, lv3), this.modelCollector);
            lv8 = ItemModels.tinted(lv, new DyeTintSource(-6265536));
        } else {
            Models.GENERATED.upload(lv, TextureMap.layer0(lv2), this.modelCollector);
            lv8 = ItemModels.basic(lv);
        }
        this.output.accept(item, ItemModels.select(new TrimMaterialProperty(), lv8, list));
    }

    private void registerBundle(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(this.upload(item, Models.GENERATED));
        Identifier lv2 = this.uploadOpenBundleModel(item, Models.TEMPLATE_BUNDLE_OPEN_BACK, "_open_back");
        Identifier lv3 = this.uploadOpenBundleModel(item, Models.TEMPLATE_BUNDLE_OPEN_FRONT, "_open_front");
        ItemModel.Unbaked lv4 = ItemModels.composite(ItemModels.basic(lv2), new BundleSelectedItemModel.Unbaked(), ItemModels.basic(lv3));
        ItemModel.Unbaked lv5 = ItemModels.condition(new BundleHasSelectedItemProperty(), lv4, lv);
        this.output.accept(item, ItemModels.select(new DisplayContextProperty(), lv, ItemModels.switchCase(ModelTransformationMode.GUI, lv5)));
    }

    private Identifier uploadOpenBundleModel(Item item, Model model, String textureSuffix) {
        Identifier lv = TextureMap.getSubId(item, textureSuffix);
        return model.upload(item, TextureMap.layer0(lv), this.modelCollector);
    }

    private void registerBow(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked lv2 = ItemModels.basic(this.registerSubModel(item, "_pulling_0", Models.BOW));
        ItemModel.Unbaked lv3 = ItemModels.basic(this.registerSubModel(item, "_pulling_1", Models.BOW));
        ItemModel.Unbaked lv4 = ItemModels.basic(this.registerSubModel(item, "_pulling_2", Models.BOW));
        this.output.accept(item, ItemModels.condition(ItemModels.usingItemProperty(), ItemModels.rangeDispatch((NumericProperty)new UseDurationProperty(false), 0.05f, lv2, ItemModels.rangeDispatchEntry(lv3, 0.65f), ItemModels.rangeDispatchEntry(lv4, 0.9f)), lv));
    }

    private void registerCrossbow(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked lv2 = ItemModels.basic(this.registerSubModel(item, "_pulling_0", Models.CROSSBOW));
        ItemModel.Unbaked lv3 = ItemModels.basic(this.registerSubModel(item, "_pulling_1", Models.CROSSBOW));
        ItemModel.Unbaked lv4 = ItemModels.basic(this.registerSubModel(item, "_pulling_2", Models.CROSSBOW));
        ItemModel.Unbaked lv5 = ItemModels.basic(this.registerSubModel(item, "_arrow", Models.CROSSBOW));
        ItemModel.Unbaked lv6 = ItemModels.basic(this.registerSubModel(item, "_firework", Models.CROSSBOW));
        this.output.accept(item, ItemModels.condition(ItemModels.usingItemProperty(), ItemModels.rangeDispatch((NumericProperty)new CrossbowPullProperty(), lv2, ItemModels.rangeDispatchEntry(lv3, 0.58f), ItemModels.rangeDispatchEntry(lv4, 1.0f)), ItemModels.select(new ChargeTypeProperty(), lv, ItemModels.switchCase(CrossbowItem.ChargeType.ARROW, lv5), ItemModels.switchCase(CrossbowItem.ChargeType.ROCKET, lv6))));
    }

    private void registerCondition(Item item, BooleanProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) {
        this.output.accept(item, ItemModels.condition(property, onTrue, onFalse));
    }

    private void registerWithBrokenCondition(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(this.upload(item, Models.GENERATED));
        ItemModel.Unbaked lv2 = ItemModels.basic(this.registerSubModel(item, "_broken", Models.GENERATED));
        this.registerCondition(item, new BrokenProperty(), lv2, lv);
    }

    private void registerBrush(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked lv2 = ItemModels.basic(ModelIds.getItemSubModelId(item, "_brushing_0"));
        ItemModel.Unbaked lv3 = ItemModels.basic(ModelIds.getItemSubModelId(item, "_brushing_1"));
        ItemModel.Unbaked lv4 = ItemModels.basic(ModelIds.getItemSubModelId(item, "_brushing_2"));
        this.output.accept(item, ItemModels.rangeDispatch((NumericProperty)new UseCycleProperty(10.0f), 0.1f, lv, ItemModels.rangeDispatchEntry(lv2, 0.25f), ItemModels.rangeDispatchEntry(lv3, 0.5f), ItemModels.rangeDispatchEntry(lv4, 0.75f)));
    }

    private void registerFishingRod(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(this.upload(item, Models.HANDHELD_ROD));
        ItemModel.Unbaked lv2 = ItemModels.basic(this.registerSubModel(item, "_cast", Models.HANDHELD_ROD));
        this.registerCondition(item, new FishingRodCastProperty(), lv2, lv);
    }

    private void registerGoatHorn(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked lv2 = ItemModels.basic(ModelIds.getMinecraftNamespacedItem("tooting_goat_horn"));
        this.registerCondition(item, ItemModels.usingItemProperty(), lv2, lv);
    }

    private void registerShield(Item item) {
        ItemModel.Unbaked lv = ItemModels.special(ModelIds.getItemModelId(item), new ShieldModelRenderer.Unbaked());
        ItemModel.Unbaked lv2 = ItemModels.special(ModelIds.getItemSubModelId(item, "_blocking"), new ShieldModelRenderer.Unbaked());
        this.registerCondition(item, ItemModels.usingItemProperty(), lv2, lv);
    }

    private static ItemModel.Unbaked createModelWithInHandVariant(ItemModel.Unbaked model, ItemModel.Unbaked inHandModel) {
        return ItemModels.select(new DisplayContextProperty(), inHandModel, ItemModels.switchCase(List.of(ModelTransformationMode.GUI, ModelTransformationMode.GROUND, ModelTransformationMode.FIXED), model));
    }

    private void registerWithInHandModel(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(this.upload(item, Models.GENERATED));
        ItemModel.Unbaked lv2 = ItemModels.basic(ModelIds.getItemSubModelId(item, "_in_hand"));
        this.output.accept(item, ItemModelGenerator.createModelWithInHandVariant(lv, lv2));
    }

    private void registerTrident(Item item) {
        ItemModel.Unbaked lv = ItemModels.basic(this.upload(item, Models.GENERATED));
        ItemModel.Unbaked lv2 = ItemModels.special(ModelIds.getItemSubModelId(item, "_in_hand"), new TridentModelRenderer.Unbaked());
        ItemModel.Unbaked lv3 = ItemModels.special(ModelIds.getItemSubModelId(item, "_throwing"), new TridentModelRenderer.Unbaked());
        ItemModel.Unbaked lv4 = ItemModels.condition(ItemModels.usingItemProperty(), lv3, lv2);
        this.output.accept(item, ItemModelGenerator.createModelWithInHandVariant(lv, lv4));
    }

    private void registerPotionTinted(Item item, Identifier model) {
        this.output.accept(item, ItemModels.tinted(model, new PotionTintSource()));
    }

    private void registerPotion(Item item) {
        Identifier lv = this.uploadTwoLayers(item, ModelIds.getMinecraftNamespacedItem("potion_overlay"), ModelIds.getItemModelId(item));
        this.registerPotionTinted(item, lv);
    }

    private void registerTippedArrow(Item item) {
        Identifier lv = this.uploadTwoLayers(item, ModelIds.getItemSubModelId(item, "_head"), ModelIds.getItemSubModelId(item, "_base"));
        this.registerPotionTinted(item, lv);
    }

    private void registerDyeable(Item item, int defaultColor) {
        Identifier lv = this.upload(item, Models.GENERATED);
        this.output.accept(item, ItemModels.tinted(lv, new DyeTintSource(defaultColor)));
    }

    private void registerSpawnEgg(Item item, int shellColor, int spotsColor) {
        Identifier lv = ModelIds.getMinecraftNamespacedItem("template_spawn_egg");
        this.output.accept(item, ItemModels.tinted(lv, ItemModels.constantTintSource(shellColor), ItemModels.constantTintSource(spotsColor)));
    }

    private void registerWithDyeableOverlay(Item item) {
        Identifier lv = TextureMap.getId(item);
        Identifier lv2 = TextureMap.getSubId(item, "_overlay");
        Identifier lv3 = Models.GENERATED.upload(item, TextureMap.layer0(lv), this.modelCollector);
        Identifier lv4 = ModelIds.getItemSubModelId(item, "_dyed");
        Models.GENERATED_TWO_LAYERS.upload(lv4, TextureMap.layered(lv, lv2), this.modelCollector);
        this.output.accept(item, ItemModels.condition(ItemModels.hasComponentProperty(DataComponentTypes.DYED_COLOR), ItemModels.tinted(lv4, UNTINTED, new DyeTintSource(0)), ItemModels.basic(lv3)));
    }

    public void register() {
        this.register(Items.ACACIA_BOAT, Models.GENERATED);
        this.register(Items.CHERRY_BOAT, Models.GENERATED);
        this.register(Items.ACACIA_CHEST_BOAT, Models.GENERATED);
        this.register(Items.CHERRY_CHEST_BOAT, Models.GENERATED);
        this.register(Items.AMETHYST_SHARD, Models.GENERATED);
        this.register(Items.APPLE, Models.GENERATED);
        this.register(Items.ARMADILLO_SCUTE, Models.GENERATED);
        this.register(Items.ARMOR_STAND, Models.GENERATED);
        this.register(Items.ARROW, Models.GENERATED);
        this.register(Items.BAKED_POTATO, Models.GENERATED);
        this.register(Items.BAMBOO, Models.HANDHELD);
        this.register(Items.BEEF, Models.GENERATED);
        this.register(Items.BEETROOT, Models.GENERATED);
        this.register(Items.BEETROOT_SOUP, Models.GENERATED);
        this.register(Items.BIRCH_BOAT, Models.GENERATED);
        this.register(Items.BIRCH_CHEST_BOAT, Models.GENERATED);
        this.register(Items.BLACK_DYE, Models.GENERATED);
        this.register(Items.BLAZE_POWDER, Models.GENERATED);
        this.register(Items.BLAZE_ROD, Models.HANDHELD);
        this.register(Items.BLUE_DYE, Models.GENERATED);
        this.register(Items.BONE_MEAL, Models.GENERATED);
        this.register(Items.BORDURE_INDENTED_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.BOOK, Models.GENERATED);
        this.register(Items.BOWL, Models.GENERATED);
        this.register(Items.BREAD, Models.GENERATED);
        this.register(Items.BRICK, Models.GENERATED);
        this.register(Items.BREEZE_ROD, Models.HANDHELD);
        this.register(Items.BROWN_DYE, Models.GENERATED);
        this.register(Items.BUCKET, Models.GENERATED);
        this.register(Items.CARROT_ON_A_STICK, Models.HANDHELD_ROD);
        this.register(Items.WARPED_FUNGUS_ON_A_STICK, Models.HANDHELD_ROD);
        this.register(Items.CHARCOAL, Models.GENERATED);
        this.register(Items.CHEST_MINECART, Models.GENERATED);
        this.register(Items.CHICKEN, Models.GENERATED);
        this.register(Items.CHORUS_FRUIT, Models.GENERATED);
        this.register(Items.CLAY_BALL, Models.GENERATED);
        this.registerClock(Items.CLOCK);
        this.register(Items.COAL, Models.GENERATED);
        this.register(Items.COD_BUCKET, Models.GENERATED);
        this.register(Items.COMMAND_BLOCK_MINECART, Models.GENERATED);
        this.registerCompass(Items.COMPASS);
        this.registerRecoveryCompass(Items.RECOVERY_COMPASS);
        this.register(Items.COOKED_BEEF, Models.GENERATED);
        this.register(Items.COOKED_CHICKEN, Models.GENERATED);
        this.register(Items.COOKED_COD, Models.GENERATED);
        this.register(Items.COOKED_MUTTON, Models.GENERATED);
        this.register(Items.COOKED_PORKCHOP, Models.GENERATED);
        this.register(Items.COOKED_RABBIT, Models.GENERATED);
        this.register(Items.COOKED_SALMON, Models.GENERATED);
        this.register(Items.COOKIE, Models.GENERATED);
        this.register(Items.RAW_COPPER, Models.GENERATED);
        this.register(Items.COPPER_INGOT, Models.GENERATED);
        this.register(Items.CREEPER_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.CYAN_DYE, Models.GENERATED);
        this.register(Items.DARK_OAK_BOAT, Models.GENERATED);
        this.register(Items.DARK_OAK_CHEST_BOAT, Models.GENERATED);
        this.register(Items.DIAMOND, Models.GENERATED);
        this.register(Items.DIAMOND_AXE, Models.HANDHELD);
        this.register(Items.DIAMOND_HOE, Models.HANDHELD);
        this.register(Items.DIAMOND_HORSE_ARMOR, Models.GENERATED);
        this.register(Items.DIAMOND_PICKAXE, Models.HANDHELD);
        this.register(Items.DIAMOND_SHOVEL, Models.HANDHELD);
        this.register(Items.DIAMOND_SWORD, Models.HANDHELD);
        this.register(Items.DRAGON_BREATH, Models.GENERATED);
        this.register(Items.DRIED_KELP, Models.GENERATED);
        this.register(Items.EGG, Models.GENERATED);
        this.register(Items.EMERALD, Models.GENERATED);
        this.register(Items.ENCHANTED_BOOK, Models.GENERATED);
        this.register(Items.ENDER_EYE, Models.GENERATED);
        this.register(Items.ENDER_PEARL, Models.GENERATED);
        this.register(Items.END_CRYSTAL, Models.GENERATED);
        this.register(Items.EXPERIENCE_BOTTLE, Models.GENERATED);
        this.register(Items.FERMENTED_SPIDER_EYE, Models.GENERATED);
        this.register(Items.FIELD_MASONED_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.FIREWORK_ROCKET, Models.GENERATED);
        this.register(Items.FIRE_CHARGE, Models.GENERATED);
        this.register(Items.FLINT, Models.GENERATED);
        this.register(Items.FLINT_AND_STEEL, Models.GENERATED);
        this.register(Items.FLOW_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.FLOWER_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.FURNACE_MINECART, Models.GENERATED);
        this.register(Items.GHAST_TEAR, Models.GENERATED);
        this.register(Items.GLASS_BOTTLE, Models.GENERATED);
        this.register(Items.GLISTERING_MELON_SLICE, Models.GENERATED);
        this.register(Items.GLOBE_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.GLOW_BERRIES, Models.GENERATED);
        this.register(Items.GLOWSTONE_DUST, Models.GENERATED);
        this.register(Items.GLOW_INK_SAC, Models.GENERATED);
        this.register(Items.GLOW_ITEM_FRAME, Models.GENERATED);
        this.register(Items.RAW_GOLD, Models.GENERATED);
        this.register(Items.GOLDEN_APPLE, Models.GENERATED);
        this.register(Items.GOLDEN_AXE, Models.HANDHELD);
        this.register(Items.GOLDEN_CARROT, Models.GENERATED);
        this.register(Items.GOLDEN_HOE, Models.HANDHELD);
        this.register(Items.GOLDEN_HORSE_ARMOR, Models.GENERATED);
        this.register(Items.GOLDEN_PICKAXE, Models.HANDHELD);
        this.register(Items.GOLDEN_SHOVEL, Models.HANDHELD);
        this.register(Items.GOLDEN_SWORD, Models.HANDHELD);
        this.register(Items.GOLD_INGOT, Models.GENERATED);
        this.register(Items.GOLD_NUGGET, Models.GENERATED);
        this.register(Items.GRAY_DYE, Models.GENERATED);
        this.register(Items.GREEN_DYE, Models.GENERATED);
        this.register(Items.GUNPOWDER, Models.GENERATED);
        this.register(Items.GUSTER_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.HEART_OF_THE_SEA, Models.GENERATED);
        this.register(Items.HONEYCOMB, Models.GENERATED);
        this.register(Items.HONEY_BOTTLE, Models.GENERATED);
        this.register(Items.HOPPER_MINECART, Models.GENERATED);
        this.register(Items.INK_SAC, Models.GENERATED);
        this.register(Items.RAW_IRON, Models.GENERATED);
        this.register(Items.IRON_AXE, Models.HANDHELD);
        this.register(Items.IRON_HOE, Models.HANDHELD);
        this.register(Items.IRON_HORSE_ARMOR, Models.GENERATED);
        this.register(Items.IRON_INGOT, Models.GENERATED);
        this.register(Items.IRON_NUGGET, Models.GENERATED);
        this.register(Items.IRON_PICKAXE, Models.HANDHELD);
        this.register(Items.IRON_SHOVEL, Models.HANDHELD);
        this.register(Items.IRON_SWORD, Models.HANDHELD);
        this.register(Items.ITEM_FRAME, Models.GENERATED);
        this.register(Items.JUNGLE_BOAT, Models.GENERATED);
        this.register(Items.JUNGLE_CHEST_BOAT, Models.GENERATED);
        this.register(Items.KNOWLEDGE_BOOK, Models.GENERATED);
        this.register(Items.LAPIS_LAZULI, Models.GENERATED);
        this.register(Items.LAVA_BUCKET, Models.GENERATED);
        this.register(Items.LEATHER, Models.GENERATED);
        this.register(Items.LIGHT_BLUE_DYE, Models.GENERATED);
        this.register(Items.LIGHT_GRAY_DYE, Models.GENERATED);
        this.register(Items.LIME_DYE, Models.GENERATED);
        this.register(Items.MAGENTA_DYE, Models.GENERATED);
        this.register(Items.MAGMA_CREAM, Models.GENERATED);
        this.register(Items.MANGROVE_BOAT, Models.GENERATED);
        this.register(Items.MANGROVE_CHEST_BOAT, Models.GENERATED);
        this.register(Items.BAMBOO_RAFT, Models.GENERATED);
        this.register(Items.BAMBOO_CHEST_RAFT, Models.GENERATED);
        this.register(Items.MAP, Models.GENERATED);
        this.register(Items.MELON_SLICE, Models.GENERATED);
        this.register(Items.MILK_BUCKET, Models.GENERATED);
        this.register(Items.MINECART, Models.GENERATED);
        this.register(Items.MOJANG_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.MUSHROOM_STEW, Models.GENERATED);
        this.register(Items.DISC_FRAGMENT_5, Models.GENERATED);
        this.register(Items.MUSIC_DISC_11, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_13, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_BLOCKS, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_CAT, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_CHIRP, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_CREATOR, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_FAR, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_MALL, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_MELLOHI, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_PIGSTEP, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_PRECIPICE, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_STAL, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_STRAD, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_WAIT, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_WARD, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_OTHERSIDE, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_RELIC, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUSIC_DISC_5, Models.TEMPLATE_MUSIC_DISC);
        this.register(Items.MUTTON, Models.GENERATED);
        this.register(Items.NAME_TAG, Models.GENERATED);
        this.register(Items.NAUTILUS_SHELL, Models.GENERATED);
        this.register(Items.NETHERITE_AXE, Models.HANDHELD);
        this.register(Items.NETHERITE_HOE, Models.HANDHELD);
        this.register(Items.NETHERITE_INGOT, Models.GENERATED);
        this.register(Items.NETHERITE_PICKAXE, Models.HANDHELD);
        this.register(Items.NETHERITE_SCRAP, Models.GENERATED);
        this.register(Items.NETHERITE_SHOVEL, Models.HANDHELD);
        this.register(Items.NETHERITE_SWORD, Models.HANDHELD);
        this.register(Items.NETHER_BRICK, Models.GENERATED);
        this.register(Items.RESIN_BRICK, Models.GENERATED);
        this.register(Items.NETHER_STAR, Models.GENERATED);
        this.register(Items.OAK_BOAT, Models.GENERATED);
        this.register(Items.OAK_CHEST_BOAT, Models.GENERATED);
        this.register(Items.ORANGE_DYE, Models.GENERATED);
        this.register(Items.PAINTING, Models.GENERATED);
        this.register(Items.PALE_OAK_BOAT, Models.GENERATED);
        this.register(Items.PALE_OAK_CHEST_BOAT, Models.GENERATED);
        this.register(Items.PAPER, Models.GENERATED);
        this.register(Items.PHANTOM_MEMBRANE, Models.GENERATED);
        this.register(Items.PIGLIN_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.PINK_DYE, Models.GENERATED);
        this.register(Items.POISONOUS_POTATO, Models.GENERATED);
        this.register(Items.POPPED_CHORUS_FRUIT, Models.GENERATED);
        this.register(Items.PORKCHOP, Models.GENERATED);
        this.register(Items.POWDER_SNOW_BUCKET, Models.GENERATED);
        this.register(Items.PRISMARINE_CRYSTALS, Models.GENERATED);
        this.register(Items.PRISMARINE_SHARD, Models.GENERATED);
        this.register(Items.PUFFERFISH, Models.GENERATED);
        this.register(Items.PUFFERFISH_BUCKET, Models.GENERATED);
        this.register(Items.PUMPKIN_PIE, Models.GENERATED);
        this.register(Items.PURPLE_DYE, Models.GENERATED);
        this.register(Items.QUARTZ, Models.GENERATED);
        this.register(Items.RABBIT, Models.GENERATED);
        this.register(Items.RABBIT_FOOT, Models.GENERATED);
        this.register(Items.RABBIT_HIDE, Models.GENERATED);
        this.register(Items.RABBIT_STEW, Models.GENERATED);
        this.register(Items.RED_DYE, Models.GENERATED);
        this.register(Items.ROTTEN_FLESH, Models.GENERATED);
        this.register(Items.SADDLE, Models.GENERATED);
        this.register(Items.SALMON, Models.GENERATED);
        this.register(Items.SALMON_BUCKET, Models.GENERATED);
        this.register(Items.TURTLE_SCUTE, Models.GENERATED);
        this.register(Items.SHEARS, Models.GENERATED);
        this.register(Items.SHULKER_SHELL, Models.GENERATED);
        this.register(Items.SKULL_BANNER_PATTERN, Models.GENERATED);
        this.register(Items.SLIME_BALL, Models.GENERATED);
        this.register(Items.SNOWBALL, Models.GENERATED);
        this.register(Items.ECHO_SHARD, Models.GENERATED);
        this.register(Items.SPECTRAL_ARROW, Models.GENERATED);
        this.register(Items.SPIDER_EYE, Models.GENERATED);
        this.register(Items.SPRUCE_BOAT, Models.GENERATED);
        this.register(Items.SPRUCE_CHEST_BOAT, Models.GENERATED);
        this.register(Items.STICK, Models.HANDHELD);
        this.register(Items.STONE_AXE, Models.HANDHELD);
        this.register(Items.STONE_HOE, Models.HANDHELD);
        this.register(Items.STONE_PICKAXE, Models.HANDHELD);
        this.register(Items.STONE_SHOVEL, Models.HANDHELD);
        this.register(Items.STONE_SWORD, Models.HANDHELD);
        this.register(Items.SUGAR, Models.GENERATED);
        this.register(Items.SUSPICIOUS_STEW, Models.GENERATED);
        this.register(Items.TNT_MINECART, Models.GENERATED);
        this.register(Items.TOTEM_OF_UNDYING, Models.GENERATED);
        this.register(Items.TROPICAL_FISH, Models.GENERATED);
        this.register(Items.TROPICAL_FISH_BUCKET, Models.GENERATED);
        this.register(Items.AXOLOTL_BUCKET, Models.GENERATED);
        this.register(Items.TADPOLE_BUCKET, Models.GENERATED);
        this.register(Items.WATER_BUCKET, Models.GENERATED);
        this.register(Items.WHEAT, Models.GENERATED);
        this.register(Items.WHITE_DYE, Models.GENERATED);
        this.register(Items.WIND_CHARGE, Models.GENERATED);
        this.register(Items.MACE, Models.HANDHELD_MACE);
        this.register(Items.WOODEN_AXE, Models.HANDHELD);
        this.register(Items.WOODEN_HOE, Models.HANDHELD);
        this.register(Items.WOODEN_PICKAXE, Models.HANDHELD);
        this.register(Items.WOODEN_SHOVEL, Models.HANDHELD);
        this.register(Items.WOODEN_SWORD, Models.HANDHELD);
        this.register(Items.WRITABLE_BOOK, Models.GENERATED);
        this.register(Items.WRITTEN_BOOK, Models.GENERATED);
        this.register(Items.YELLOW_DYE, Models.GENERATED);
        this.register(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.register(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        this.registerWithTextureSource(Items.DEBUG_STICK, Items.STICK, Models.HANDHELD);
        this.registerWithTextureSource(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, Models.GENERATED);
        this.registerArmor(Items.TURTLE_HELMET, EquipmentAssetKeys.TURTLE_SCUTE, HELMET, false);
        this.registerArmor(Items.LEATHER_HELMET, EquipmentAssetKeys.LEATHER, HELMET, true);
        this.registerArmor(Items.LEATHER_CHESTPLATE, EquipmentAssetKeys.LEATHER, CHESTPLATE, true);
        this.registerArmor(Items.LEATHER_LEGGINGS, EquipmentAssetKeys.LEATHER, LEGGINGS, true);
        this.registerArmor(Items.LEATHER_BOOTS, EquipmentAssetKeys.LEATHER, BOOTS, true);
        this.registerArmor(Items.CHAINMAIL_HELMET, EquipmentAssetKeys.CHAINMAIL, HELMET, false);
        this.registerArmor(Items.CHAINMAIL_CHESTPLATE, EquipmentAssetKeys.CHAINMAIL, CHESTPLATE, false);
        this.registerArmor(Items.CHAINMAIL_LEGGINGS, EquipmentAssetKeys.CHAINMAIL, LEGGINGS, false);
        this.registerArmor(Items.CHAINMAIL_BOOTS, EquipmentAssetKeys.CHAINMAIL, BOOTS, false);
        this.registerArmor(Items.IRON_HELMET, EquipmentAssetKeys.IRON, HELMET, false);
        this.registerArmor(Items.IRON_CHESTPLATE, EquipmentAssetKeys.IRON, CHESTPLATE, false);
        this.registerArmor(Items.IRON_LEGGINGS, EquipmentAssetKeys.IRON, LEGGINGS, false);
        this.registerArmor(Items.IRON_BOOTS, EquipmentAssetKeys.IRON, BOOTS, false);
        this.registerArmor(Items.DIAMOND_HELMET, EquipmentAssetKeys.DIAMOND, HELMET, false);
        this.registerArmor(Items.DIAMOND_CHESTPLATE, EquipmentAssetKeys.DIAMOND, CHESTPLATE, false);
        this.registerArmor(Items.DIAMOND_LEGGINGS, EquipmentAssetKeys.DIAMOND, LEGGINGS, false);
        this.registerArmor(Items.DIAMOND_BOOTS, EquipmentAssetKeys.DIAMOND, BOOTS, false);
        this.registerArmor(Items.GOLDEN_HELMET, EquipmentAssetKeys.GOLD, HELMET, false);
        this.registerArmor(Items.GOLDEN_CHESTPLATE, EquipmentAssetKeys.GOLD, CHESTPLATE, false);
        this.registerArmor(Items.GOLDEN_LEGGINGS, EquipmentAssetKeys.GOLD, LEGGINGS, false);
        this.registerArmor(Items.GOLDEN_BOOTS, EquipmentAssetKeys.GOLD, BOOTS, false);
        this.registerArmor(Items.NETHERITE_HELMET, EquipmentAssetKeys.NETHERITE, HELMET, false);
        this.registerArmor(Items.NETHERITE_CHESTPLATE, EquipmentAssetKeys.NETHERITE, CHESTPLATE, false);
        this.registerArmor(Items.NETHERITE_LEGGINGS, EquipmentAssetKeys.NETHERITE, LEGGINGS, false);
        this.registerArmor(Items.NETHERITE_BOOTS, EquipmentAssetKeys.NETHERITE, BOOTS, false);
        this.registerDyeable(Items.LEATHER_HORSE_ARMOR, -6265536);
        this.register(Items.ANGLER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.ARCHER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.ARMS_UP_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.BLADE_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.BREWER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.BURN_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.DANGER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.EXPLORER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.FLOW_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.FRIEND_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.GUSTER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.HEART_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.HEARTBREAK_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.HOWL_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.MINER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.MOURNER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.PLENTY_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.PRIZE_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.SCRAPE_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.SHEAF_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.SHELTER_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.SKULL_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.SNORT_POTTERY_SHERD, Models.GENERATED);
        this.register(Items.TRIAL_KEY, Models.GENERATED);
        this.register(Items.OMINOUS_TRIAL_KEY, Models.GENERATED);
        this.register(Items.OMINOUS_BOTTLE, Models.GENERATED);
        this.registerWithTintedOverlay(Items.FIREWORK_STAR, new FireworkTintSource());
        this.registerWithTintedLayer(Items.FILLED_MAP, "_markings", new MapColorTintSource());
        this.registerBundle(Items.BUNDLE);
        this.registerBundle(Items.BLACK_BUNDLE);
        this.registerBundle(Items.WHITE_BUNDLE);
        this.registerBundle(Items.GRAY_BUNDLE);
        this.registerBundle(Items.LIGHT_GRAY_BUNDLE);
        this.registerBundle(Items.LIGHT_BLUE_BUNDLE);
        this.registerBundle(Items.BLUE_BUNDLE);
        this.registerBundle(Items.CYAN_BUNDLE);
        this.registerBundle(Items.YELLOW_BUNDLE);
        this.registerBundle(Items.RED_BUNDLE);
        this.registerBundle(Items.PURPLE_BUNDLE);
        this.registerBundle(Items.MAGENTA_BUNDLE);
        this.registerBundle(Items.PINK_BUNDLE);
        this.registerBundle(Items.GREEN_BUNDLE);
        this.registerBundle(Items.LIME_BUNDLE);
        this.registerBundle(Items.BROWN_BUNDLE);
        this.registerBundle(Items.ORANGE_BUNDLE);
        this.registerWithInHandModel(Items.SPYGLASS);
        this.registerTrident(Items.TRIDENT);
        this.registerWithDyeableOverlay(Items.WOLF_ARMOR);
        this.registerBow(Items.BOW);
        this.registerCrossbow(Items.CROSSBOW);
        this.registerWithBrokenCondition(Items.ELYTRA);
        this.registerBrush(Items.BRUSH);
        this.registerFishingRod(Items.FISHING_ROD);
        this.registerGoatHorn(Items.GOAT_HORN);
        this.registerShield(Items.SHIELD);
        this.registerTippedArrow(Items.TIPPED_ARROW);
        this.registerPotion(Items.POTION);
        this.registerPotion(Items.SPLASH_POTION);
        this.registerPotion(Items.LINGERING_POTION);
        this.registerSpawnEgg(Items.ARMADILLO_SPAWN_EGG, 11366765, 0x824848);
        this.registerSpawnEgg(Items.ALLAY_SPAWN_EGG, 56063, 44543);
        this.registerSpawnEgg(Items.AXOLOTL_SPAWN_EGG, 16499171, 10890612);
        this.registerSpawnEgg(Items.BAT_SPAWN_EGG, 4996656, 986895);
        this.registerSpawnEgg(Items.BEE_SPAWN_EGG, 15582019, 4400155);
        this.registerSpawnEgg(Items.BLAZE_SPAWN_EGG, 16167425, 16775294);
        this.registerSpawnEgg(Items.BOGGED_SPAWN_EGG, 9084018, 3231003);
        this.registerSpawnEgg(Items.BREEZE_SPAWN_EGG, 11506911, 9529055);
        this.registerSpawnEgg(Items.CAT_SPAWN_EGG, 15714446, 9794134);
        this.registerSpawnEgg(Items.CAMEL_SPAWN_EGG, 16565097, 13341495);
        this.registerSpawnEgg(Items.CAVE_SPIDER_SPAWN_EGG, 803406, 11013646);
        this.registerSpawnEgg(Items.CHICKEN_SPAWN_EGG, 0xA1A1A1, 0xFF0000);
        this.registerSpawnEgg(Items.COD_SPAWN_EGG, 12691306, 15058059);
        this.registerSpawnEgg(Items.COW_SPAWN_EGG, 4470310, 0xA1A1A1);
        this.registerSpawnEgg(Items.CREEPER_SPAWN_EGG, 894731, 0);
        this.registerSpawnEgg(Items.DOLPHIN_SPAWN_EGG, 2243405, 0xF9F9F9);
        this.registerSpawnEgg(Items.DONKEY_SPAWN_EGG, 5457209, 8811878);
        this.registerSpawnEgg(Items.DROWNED_SPAWN_EGG, 9433559, 7969893);
        this.registerSpawnEgg(Items.ELDER_GUARDIAN_SPAWN_EGG, 13552826, 7632531);
        this.registerSpawnEgg(Items.ENDER_DRAGON_SPAWN_EGG, 0x1C1C1C, 14711290);
        this.registerSpawnEgg(Items.ENDERMAN_SPAWN_EGG, 0x161616, 0);
        this.registerSpawnEgg(Items.ENDERMITE_SPAWN_EGG, 0x161616, 0x6E6E6E);
        this.registerSpawnEgg(Items.EVOKER_SPAWN_EGG, 0x959B9B, 1973274);
        this.registerSpawnEgg(Items.FOX_SPAWN_EGG, 14005919, 13396256);
        this.registerSpawnEgg(Items.FROG_SPAWN_EGG, 13661252, 0xFFC77C);
        this.registerSpawnEgg(Items.GHAST_SPAWN_EGG, 0xF9F9F9, 0xBCBCBC);
        this.registerSpawnEgg(Items.GLOW_SQUID_SPAWN_EGG, 611926, 8778172);
        this.registerSpawnEgg(Items.GOAT_SPAWN_EGG, 10851452, 5589310);
        this.registerSpawnEgg(Items.GUARDIAN_SPAWN_EGG, 5931634, 15826224);
        this.registerSpawnEgg(Items.HOGLIN_SPAWN_EGG, 13004373, 6251620);
        this.registerSpawnEgg(Items.HORSE_SPAWN_EGG, 12623485, 0xEEE500);
        this.registerSpawnEgg(Items.HUSK_SPAWN_EGG, 7958625, 15125652);
        this.registerSpawnEgg(Items.IRON_GOLEM_SPAWN_EGG, 14405058, 7643954);
        this.registerSpawnEgg(Items.LLAMA_SPAWN_EGG, 12623485, 10051392);
        this.registerSpawnEgg(Items.MAGMA_CUBE_SPAWN_EGG, 0x340000, 0xFCFC00);
        this.registerSpawnEgg(Items.MOOSHROOM_SPAWN_EGG, 10489616, 0xB7B7B7);
        this.registerSpawnEgg(Items.MULE_SPAWN_EGG, 1769984, 5321501);
        this.registerSpawnEgg(Items.OCELOT_SPAWN_EGG, 15720061, 5653556);
        this.registerSpawnEgg(Items.PANDA_SPAWN_EGG, 0xE7E7E7, 0x1B1B22);
        this.registerSpawnEgg(Items.PARROT_SPAWN_EGG, 894731, 0xFF0000);
        this.registerSpawnEgg(Items.PHANTOM_SPAWN_EGG, 4411786, 0x88FF00);
        this.registerSpawnEgg(Items.PIG_SPAWN_EGG, 15771042, 14377823);
        this.registerSpawnEgg(Items.PIGLIN_SPAWN_EGG, 10051392, 16380836);
        this.registerSpawnEgg(Items.PIGLIN_BRUTE_SPAWN_EGG, 5843472, 16380836);
        this.registerSpawnEgg(Items.PILLAGER_SPAWN_EGG, 5451574, 0x959B9B);
        this.registerSpawnEgg(Items.POLAR_BEAR_SPAWN_EGG, 0xEEEEDE, 14014157);
        this.registerSpawnEgg(Items.PUFFERFISH_SPAWN_EGG, 16167425, 3654642);
        this.registerSpawnEgg(Items.RABBIT_SPAWN_EGG, 10051392, 7555121);
        this.registerSpawnEgg(Items.RAVAGER_SPAWN_EGG, 7697520, 5984329);
        this.registerSpawnEgg(Items.SALMON_SPAWN_EGG, 10489616, 951412);
        this.registerSpawnEgg(Items.SHEEP_SPAWN_EGG, 0xE7E7E7, 0xFFB5B5);
        this.registerSpawnEgg(Items.SHULKER_SPAWN_EGG, 9725844, 5060690);
        this.registerSpawnEgg(Items.SILVERFISH_SPAWN_EGG, 0x6E6E6E, 0x303030);
        this.registerSpawnEgg(Items.SKELETON_SPAWN_EGG, 0xC1C1C1, 0x494949);
        this.registerSpawnEgg(Items.SKELETON_HORSE_SPAWN_EGG, 6842447, 15066584);
        this.registerSpawnEgg(Items.SLIME_SPAWN_EGG, 5349438, 8306542);
        this.registerSpawnEgg(Items.SNIFFER_SPAWN_EGG, 8855049, 2468720);
        this.registerSpawnEgg(Items.SNOW_GOLEM_SPAWN_EGG, 14283506, 8496292);
        this.registerSpawnEgg(Items.SPIDER_SPAWN_EGG, 3419431, 11013646);
        this.registerSpawnEgg(Items.SQUID_SPAWN_EGG, 2243405, 7375001);
        this.registerSpawnEgg(Items.STRAY_SPAWN_EGG, 0x617677, 0xDDEAEA);
        this.registerSpawnEgg(Items.STRIDER_SPAWN_EGG, 10236982, 0x4D494D);
        this.registerSpawnEgg(Items.TADPOLE_SPAWN_EGG, 7164733, 1444352);
        this.registerSpawnEgg(Items.TRADER_LLAMA_SPAWN_EGG, 15377456, 4547222);
        this.registerSpawnEgg(Items.TROPICAL_FISH_SPAWN_EGG, 15690005, 0xFFF9EF);
        this.registerSpawnEgg(Items.TURTLE_SPAWN_EGG, 0xE7E7E7, 44975);
        this.registerSpawnEgg(Items.VEX_SPAWN_EGG, 8032420, 15265265);
        this.registerSpawnEgg(Items.VILLAGER_SPAWN_EGG, 5651507, 12422002);
        this.registerSpawnEgg(Items.VINDICATOR_SPAWN_EGG, 0x959B9B, 2580065);
        this.registerSpawnEgg(Items.WANDERING_TRADER_SPAWN_EGG, 4547222, 15377456);
        this.registerSpawnEgg(Items.WARDEN_SPAWN_EGG, 1001033, 3790560);
        this.registerSpawnEgg(Items.WITCH_SPAWN_EGG, 0x340000, 5349438);
        this.registerSpawnEgg(Items.WITHER_SPAWN_EGG, 0x141414, 5075616);
        this.registerSpawnEgg(Items.WITHER_SKELETON_SPAWN_EGG, 0x141414, 0x474D4D);
        this.registerSpawnEgg(Items.WOLF_SPAWN_EGG, 0xD7D3D3, 13545366);
        this.registerSpawnEgg(Items.ZOGLIN_SPAWN_EGG, 13004373, 0xE6E6E6);
        this.registerSpawnEgg(Items.CREAKING_SPAWN_EGG, 0x5F5F5F, 16545810);
        this.registerSpawnEgg(Items.ZOMBIE_SPAWN_EGG, 44975, 7969893);
        this.registerSpawnEgg(Items.ZOMBIE_HORSE_SPAWN_EGG, 3232308, 9945732);
        this.registerSpawnEgg(Items.ZOMBIE_VILLAGER_SPAWN_EGG, 5651507, 7969893);
        this.registerSpawnEgg(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, 15373203, 5009705);
        this.register(Items.AIR);
        this.register(Items.AMETHYST_CLUSTER);
        this.register(Items.SMALL_AMETHYST_BUD);
        this.register(Items.MEDIUM_AMETHYST_BUD);
        this.register(Items.LARGE_AMETHYST_BUD);
        this.register(Items.SMALL_DRIPLEAF);
        this.register(Items.BIG_DRIPLEAF);
        this.register(Items.HANGING_ROOTS);
        this.register(Items.POINTED_DRIPSTONE);
        this.register(Items.BONE);
        this.register(Items.COD);
        this.register(Items.FEATHER);
        this.register(Items.LEAD);
    }

    @Environment(value=EnvType.CLIENT)
    record TrimMaterial(String name, RegistryKey<ArmorTrimMaterial> materialKey, Map<RegistryKey<EquipmentAsset>, String> overrideArmorMaterials) {
        public String texture(RegistryKey<EquipmentAsset> equipmentKey) {
            return this.overrideArmorMaterials.getOrDefault(equipmentKey, this.name);
        }
    }
}

