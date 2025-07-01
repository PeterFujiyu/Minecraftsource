/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

class RaidOmenStatusEffect
extends StatusEffect {
    protected RaidOmenStatusEffect(StatusEffectCategory arg, int i, ParticleEffect arg2) {
        super(arg, i, arg2);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration == 1;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity) {
            BlockPos lv2;
            ServerPlayerEntity lv = (ServerPlayerEntity)entity;
            if (!entity.isSpectator() && (lv2 = lv.getStartRaidPos()) != null) {
                world.getRaidManager().startRaid(lv, lv2);
                lv.clearStartRaidPos();
                return false;
            }
        }
        return true;
    }
}

