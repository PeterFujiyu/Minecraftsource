/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.conversion;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;

public enum EntityConversionType {
    SINGLE(true){

        @Override
        void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
            Entity lv3;
            Entity lv = oldEntity.getFirstPassenger();
            newEntity.copyPositionAndRotation(oldEntity);
            newEntity.setVelocity(oldEntity.getVelocity());
            if (lv != null) {
                lv.stopRiding();
                lv.ridingCooldown = 0;
                for (Entity entity : newEntity.getPassengerList()) {
                    entity.stopRiding();
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
                lv.startRiding(newEntity);
            }
            if ((lv3 = oldEntity.getVehicle()) != null) {
                oldEntity.stopRiding();
                newEntity.startRiding(lv3);
            }
            if (context.keepEquipment()) {
                for (EquipmentSlot lv4 : EquipmentSlot.VALUES) {
                    ItemStack lv5 = oldEntity.getEquippedStack(lv4);
                    if (lv5.isEmpty()) continue;
                    newEntity.equipStack(lv4, lv5.copyAndEmpty());
                    newEntity.setEquipmentDropChance(lv4, oldEntity.getDropChance(lv4));
                }
            }
            newEntity.fallDistance = oldEntity.fallDistance;
            newEntity.setFlag(Entity.GLIDING_FLAG_INDEX, oldEntity.isGliding());
            newEntity.playerHitTimer = oldEntity.playerHitTimer;
            newEntity.hurtTime = oldEntity.hurtTime;
            newEntity.bodyYaw = oldEntity.bodyYaw;
            newEntity.setOnGround(oldEntity.isOnGround());
            oldEntity.getSleepingPosition().ifPresent(newEntity::setSleepingPosition);
            Entity entity = oldEntity.getLeashHolder();
            if (entity != null) {
                newEntity.attachLeash(entity, true);
            }
            this.copyData(oldEntity, newEntity, context);
        }
    }
    ,
    SPLIT_ON_DEATH(false){

        @Override
        void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
            Entity lv2;
            Entity lv = oldEntity.getFirstPassenger();
            if (lv != null) {
                lv.stopRiding();
            }
            if ((lv2 = oldEntity.getLeashHolder()) != null) {
                oldEntity.detachLeash();
            }
            this.copyData(oldEntity, newEntity, context);
        }
    };

    private final boolean discardOldEntity;

    EntityConversionType(boolean discardOldEntity) {
        this.discardOldEntity = discardOldEntity;
    }

    public boolean shouldDiscardOldEntity() {
        return this.discardOldEntity;
    }

    abstract void setUpNewEntity(MobEntity var1, MobEntity var2, EntityConversionContext var3);

    void copyData(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
        ZombieEntity lv7;
        newEntity.setAbsorptionAmount(oldEntity.getAbsorptionAmount());
        for (StatusEffectInstance lv : oldEntity.getStatusEffects()) {
            newEntity.addStatusEffect(new StatusEffectInstance(lv));
        }
        if (oldEntity.isBaby()) {
            newEntity.setBaby(true);
        }
        if (oldEntity instanceof PassiveEntity) {
            PassiveEntity lv2 = (PassiveEntity)oldEntity;
            if (newEntity instanceof PassiveEntity) {
                PassiveEntity lv3 = (PassiveEntity)newEntity;
                lv3.setBreedingAge(lv2.getBreedingAge());
                lv3.forcedAge = lv2.forcedAge;
                lv3.happyTicksRemaining = lv2.happyTicksRemaining;
            }
        }
        Brain<UUID> lv4 = oldEntity.getBrain();
        Brain<?> lv5 = newEntity.getBrain();
        if (lv4.isMemoryInState(MemoryModuleType.ANGRY_AT, MemoryModuleState.REGISTERED) && lv4.hasMemoryModule(MemoryModuleType.ANGRY_AT)) {
            lv5.remember(MemoryModuleType.ANGRY_AT, lv4.getOptionalRegisteredMemory(MemoryModuleType.ANGRY_AT));
        }
        if (context.preserveCanPickUpLoot()) {
            newEntity.setCanPickUpLoot(oldEntity.canPickUpLoot());
        }
        newEntity.setLeftHanded(oldEntity.isLeftHanded());
        newEntity.setAiDisabled(oldEntity.isAiDisabled());
        if (oldEntity.isPersistent()) {
            newEntity.setPersistent();
        }
        if (oldEntity.hasCustomName()) {
            newEntity.setCustomName(oldEntity.getCustomName());
            newEntity.setCustomNameVisible(oldEntity.isCustomNameVisible());
        }
        newEntity.setOnFire(oldEntity.isOnFire());
        newEntity.setInvulnerable(oldEntity.isInvulnerable());
        newEntity.setNoGravity(oldEntity.hasNoGravity());
        newEntity.setPortalCooldown(oldEntity.getPortalCooldown());
        newEntity.setSilent(oldEntity.isSilent());
        oldEntity.getCommandTags().forEach(newEntity::addCommandTag);
        if (context.team() != null) {
            Scoreboard lv6 = newEntity.getWorld().getScoreboard();
            lv6.addScoreHolderToTeam(newEntity.getUuidAsString(), context.team());
            if (oldEntity.getScoreboardTeam() != null && oldEntity.getScoreboardTeam() == context.team()) {
                lv6.removeScoreHolderFromTeam(oldEntity.getUuidAsString(), oldEntity.getScoreboardTeam());
            }
        }
        if (oldEntity instanceof ZombieEntity && (lv7 = (ZombieEntity)oldEntity).canBreakDoors() && newEntity instanceof ZombieEntity) {
            ZombieEntity lv8 = (ZombieEntity)newEntity;
            lv8.setCanBreakDoors(true);
        }
    }
}

