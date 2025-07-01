/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class AnimalArmorItem
extends Item {
    private final Type type;

    public AnimalArmorItem(ArmorMaterial material, Type type, Item.Settings settings) {
        super(material.applyBodyArmorSettings(settings, type.allowedEntities));
        this.type = type;
    }

    public AnimalArmorItem(ArmorMaterial material, Type type, RegistryEntry<SoundEvent> equipSound, boolean damageOnHurt, Item.Settings settings) {
        super(material.applyBodyArmorSettings(settings, equipSound, damageOnHurt, type.allowedEntities));
        this.type = type;
    }

    @Override
    public SoundEvent getBreakSound() {
        return this.type.breakSound;
    }

    public static enum Type {
        EQUESTRIAN(SoundEvents.ENTITY_ITEM_BREAK, EntityType.HORSE),
        CANINE(SoundEvents.ITEM_WOLF_ARMOR_BREAK, EntityType.WOLF);

        final SoundEvent breakSound;
        final RegistryEntryList<EntityType<?>> allowedEntities;

        private Type(SoundEvent breakSound, EntityType<?> ... allowedEntities) {
            this.breakSound = breakSound;
            this.allowedEntities = RegistryEntryList.of(EntityType::getRegistryEntry, allowedEntities);
        }
    }
}

