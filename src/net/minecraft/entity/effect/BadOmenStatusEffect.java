/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;

class BadOmenStatusEffect
extends StatusEffect {
    protected BadOmenStatusEffect(StatusEffectCategory arg, int i) {
        super(arg, i);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        Raid lv2;
        ServerPlayerEntity lv;
        if (entity instanceof ServerPlayerEntity && !(lv = (ServerPlayerEntity)entity).isSpectator() && world.getDifficulty() != Difficulty.PEACEFUL && world.isNearOccupiedPointOfInterest(lv.getBlockPos()) && ((lv2 = world.getRaidAt(lv.getBlockPos())) == null || lv2.getBadOmenLevel() < lv2.getMaxAcceptableBadOmenLevel())) {
            lv.addStatusEffect(new StatusEffectInstance(StatusEffects.RAID_OMEN, 600, amplifier));
            lv.setStartRaidPos(lv.getBlockPos());
            return false;
        }
        return true;
    }
}

