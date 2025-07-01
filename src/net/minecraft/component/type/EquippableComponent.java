/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record EquippableComponent(EquipmentSlot slot, RegistryEntry<SoundEvent> equipSound, Optional<RegistryKey<EquipmentAsset>> assetId, Optional<Identifier> cameraOverlay, Optional<RegistryEntryList<EntityType<?>>> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt) {
    public static final Codec<EquippableComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)EquipmentSlot.CODEC.fieldOf("slot")).forGetter(EquippableComponent::slot), SoundEvent.ENTRY_CODEC.optionalFieldOf("equip_sound", SoundEvents.ITEM_ARMOR_EQUIP_GENERIC).forGetter(EquippableComponent::equipSound), RegistryKey.createCodec(EquipmentAssetKeys.REGISTRY_KEY).optionalFieldOf("asset_id").forGetter(EquippableComponent::assetId), Identifier.CODEC.optionalFieldOf("camera_overlay").forGetter(EquippableComponent::cameraOverlay), RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).optionalFieldOf("allowed_entities").forGetter(EquippableComponent::allowedEntities), Codec.BOOL.optionalFieldOf("dispensable", true).forGetter(EquippableComponent::dispensable), Codec.BOOL.optionalFieldOf("swappable", true).forGetter(EquippableComponent::swappable), Codec.BOOL.optionalFieldOf("damage_on_hurt", true).forGetter(EquippableComponent::damageOnHurt)).apply((Applicative<EquippableComponent, ?>)instance, EquippableComponent::new));
    public static final PacketCodec<RegistryByteBuf, EquippableComponent> PACKET_CODEC = PacketCodec.tuple(EquipmentSlot.PACKET_CODEC, EquippableComponent::slot, SoundEvent.ENTRY_PACKET_CODEC, EquippableComponent::equipSound, RegistryKey.createPacketCodec(EquipmentAssetKeys.REGISTRY_KEY).collect(PacketCodecs::optional), EquippableComponent::assetId, Identifier.PACKET_CODEC.collect(PacketCodecs::optional), EquippableComponent::cameraOverlay, PacketCodecs.registryEntryList(RegistryKeys.ENTITY_TYPE).collect(PacketCodecs::optional), EquippableComponent::allowedEntities, PacketCodecs.BOOLEAN, EquippableComponent::dispensable, PacketCodecs.BOOLEAN, EquippableComponent::swappable, PacketCodecs.BOOLEAN, EquippableComponent::damageOnHurt, EquippableComponent::new);

    public static EquippableComponent ofCarpet(DyeColor color) {
        return EquippableComponent.builder(EquipmentSlot.BODY).equipSound(SoundEvents.ENTITY_LLAMA_SWAG).model(EquipmentAssetKeys.CARPET_FROM_COLOR.get(color)).allowedEntities(EntityType.LLAMA, EntityType.TRADER_LLAMA).build();
    }

    public static Builder builder(EquipmentSlot slot) {
        return new Builder(slot);
    }

    public ActionResult equip(ItemStack stack, PlayerEntity player) {
        if (!player.canUseSlot(this.slot)) {
            return ActionResult.PASS;
        }
        ItemStack lv = player.getEquippedStack(this.slot);
        if (EnchantmentHelper.hasAnyEnchantmentsWith(lv, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) && !player.isCreative() || ItemStack.areItemsAndComponentsEqual(stack, lv)) {
            return ActionResult.FAIL;
        }
        if (!player.getWorld().isClient()) {
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }
        if (stack.getCount() <= 1) {
            ItemStack lv2 = lv.isEmpty() ? stack : lv.copyAndEmpty();
            ItemStack lv3 = player.isCreative() ? stack.copy() : stack.copyAndEmpty();
            player.equipStack(this.slot, lv3);
            return ActionResult.SUCCESS.withNewHandStack(lv2);
        }
        ItemStack lv2 = lv.copyAndEmpty();
        ItemStack lv3 = stack.splitUnlessCreative(1, player);
        player.equipStack(this.slot, lv3);
        if (!player.getInventory().insertStack(lv2)) {
            player.dropItem(lv2, false);
        }
        return ActionResult.SUCCESS.withNewHandStack(stack);
    }

    public boolean allows(EntityType<?> entityType) {
        return this.allowedEntities.isEmpty() || this.allowedEntities.get().contains(entityType.getRegistryEntry());
    }

    public static class Builder {
        private final EquipmentSlot slot;
        private RegistryEntry<SoundEvent> equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        private Optional<RegistryKey<EquipmentAsset>> model = Optional.empty();
        private Optional<Identifier> cameraOverlay = Optional.empty();
        private Optional<RegistryEntryList<EntityType<?>>> allowedEntities = Optional.empty();
        private boolean dispensable = true;
        private boolean swappable = true;
        private boolean damageOnHurt = true;

        Builder(EquipmentSlot slot) {
            this.slot = slot;
        }

        public Builder equipSound(RegistryEntry<SoundEvent> equipSound) {
            this.equipSound = equipSound;
            return this;
        }

        public Builder model(RegistryKey<EquipmentAsset> arg) {
            this.model = Optional.of(arg);
            return this;
        }

        public Builder cameraOverlay(Identifier cameraOverlay) {
            this.cameraOverlay = Optional.of(cameraOverlay);
            return this;
        }

        public Builder allowedEntities(EntityType<?> ... allowedEntities) {
            return this.allowedEntities(RegistryEntryList.of(EntityType::getRegistryEntry, allowedEntities));
        }

        public Builder allowedEntities(RegistryEntryList<EntityType<?>> allowedEntities) {
            this.allowedEntities = Optional.of(allowedEntities);
            return this;
        }

        public Builder dispensable(boolean dispensable) {
            this.dispensable = dispensable;
            return this;
        }

        public Builder swappable(boolean swappable) {
            this.swappable = swappable;
            return this;
        }

        public Builder damageOnHurt(boolean damageOnHurt) {
            this.damageOnHurt = damageOnHurt;
            return this;
        }

        public EquippableComponent build() {
            return new EquippableComponent(this.slot, this.equipSound, this.model, this.cameraOverlay, this.allowedEntities, this.dispensable, this.swappable, this.damageOnHurt);
        }
    }
}

