/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.conversion;

import net.minecraft.entity.conversion.EntityConversionType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

public record EntityConversionContext(EntityConversionType type, boolean keepEquipment, boolean preserveCanPickUpLoot, @Nullable Team team) {
    public static EntityConversionContext create(MobEntity entity, boolean keepEquipment, boolean preserveCanPickUpLoot) {
        return new EntityConversionContext(EntityConversionType.SINGLE, keepEquipment, preserveCanPickUpLoot, entity.getScoreboardTeam());
    }

    @Nullable
    public Team team() {
        return this.team;
    }

    @FunctionalInterface
    public static interface Finalizer<T extends MobEntity> {
        public void finalizeConversion(T var1);
    }
}

