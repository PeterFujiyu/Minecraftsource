/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item.equipment;

import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record ArmorMaterial(int durability, Map<EquipmentType, Integer> defense, int enchantmentValue, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, TagKey<Item> repairIngredient, RegistryKey<EquipmentAsset> assetId) {
    public Item.Settings applySettings(Item.Settings settings, EquipmentType equipmentType) {
        return settings.maxDamage(equipmentType.getMaxDamage(this.durability)).attributeModifiers(this.createAttributeModifiers(equipmentType)).enchantable(this.enchantmentValue).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(equipmentType.getEquipmentSlot()).equipSound(this.equipSound).model(this.assetId).build()).repairable(this.repairIngredient);
    }

    public Item.Settings applyBodyArmorSettings(Item.Settings settings, RegistryEntryList<EntityType<?>> allowedEntities) {
        return settings.maxDamage(EquipmentType.BODY.getMaxDamage(this.durability)).attributeModifiers(this.createAttributeModifiers(EquipmentType.BODY)).repairable(this.repairIngredient).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.BODY).equipSound(this.equipSound).model(this.assetId).allowedEntities(allowedEntities).build());
    }

    public Item.Settings applyBodyArmorSettings(Item.Settings settings, RegistryEntry<SoundEvent> equipSound, boolean damageOnHurt, RegistryEntryList<EntityType<?>> allowedEntities) {
        if (damageOnHurt) {
            settings = settings.maxDamage(EquipmentType.BODY.getMaxDamage(this.durability)).repairable(this.repairIngredient);
        }
        return settings.attributeModifiers(this.createAttributeModifiers(EquipmentType.BODY)).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.BODY).equipSound(equipSound).model(this.assetId).allowedEntities(allowedEntities).damageOnHurt(damageOnHurt).build());
    }

    private AttributeModifiersComponent createAttributeModifiers(EquipmentType equipmentType) {
        int i = this.defense.getOrDefault(equipmentType, 0);
        AttributeModifiersComponent.Builder lv = AttributeModifiersComponent.builder();
        AttributeModifierSlot lv2 = AttributeModifierSlot.forEquipmentSlot(equipmentType.getEquipmentSlot());
        Identifier lv3 = Identifier.ofVanilla("armor." + equipmentType.getName());
        lv.add(EntityAttributes.ARMOR, new EntityAttributeModifier(lv3, i, EntityAttributeModifier.Operation.ADD_VALUE), lv2);
        lv.add(EntityAttributes.ARMOR_TOUGHNESS, new EntityAttributeModifier(lv3, this.toughness, EntityAttributeModifier.Operation.ADD_VALUE), lv2);
        if (this.knockbackResistance > 0.0f) {
            lv.add(EntityAttributes.KNOCKBACK_RESISTANCE, new EntityAttributeModifier(lv3, this.knockbackResistance, EntityAttributeModifier.Operation.ADD_VALUE), lv2);
        }
        return lv.build();
    }
}

