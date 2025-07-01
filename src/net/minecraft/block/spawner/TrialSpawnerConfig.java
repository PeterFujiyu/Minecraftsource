/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.spawner;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DataPool;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, DataPool<MobSpawnerEntry> spawnPotentials, DataPool<RegistryKey<LootTable>> lootTablesToEject, RegistryKey<LootTable> itemsToDropWhenOminous) {
    public static final TrialSpawnerConfig DEFAULT = TrialSpawnerConfig.builder().build();
    public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.intRange(1, 128).optionalFieldOf("spawn_range", TrialSpawnerConfig.DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange), Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("total_mobs", Float.valueOf(TrialSpawnerConfig.DEFAULT.totalMobs)).forGetter(TrialSpawnerConfig::totalMobs), Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", Float.valueOf(TrialSpawnerConfig.DEFAULT.simultaneousMobs)).forGetter(TrialSpawnerConfig::simultaneousMobs), Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("total_mobs_added_per_player", Float.valueOf(TrialSpawnerConfig.DEFAULT.totalMobsAddedPerPlayer)).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer), Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs_added_per_player", Float.valueOf(TrialSpawnerConfig.DEFAULT.simultaneousMobsAddedPerPlayer)).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer), Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", TrialSpawnerConfig.DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn), MobSpawnerEntry.DATA_POOL_CODEC.optionalFieldOf("spawn_potentials", DataPool.empty()).forGetter(TrialSpawnerConfig::spawnPotentials), DataPool.createEmptyAllowedCodec(RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)).optionalFieldOf("loot_tables_to_eject", TrialSpawnerConfig.DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject), RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).optionalFieldOf("items_to_drop_when_ominous", TrialSpawnerConfig.DEFAULT.itemsToDropWhenOminous).forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)).apply((Applicative<TrialSpawnerConfig, ?>)instance, TrialSpawnerConfig::new));
    public static final Codec<RegistryEntry<TrialSpawnerConfig>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIAL_SPAWNER, CODEC);

    public int getTotalMobs(int additionalPlayers) {
        return (int)Math.floor(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers);
    }

    public int getSimultaneousMobs(int additionalPlayers) {
        return (int)Math.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers);
    }

    public long getCooldownLength() {
        return 160L;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TrialSpawnerConfig withSpawnPotential(EntityType<?> entityType) {
        NbtCompound lv = new NbtCompound();
        lv.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        MobSpawnerEntry lv2 = new MobSpawnerEntry(lv, Optional.empty(), Optional.empty());
        return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, DataPool.of(lv2), this.lootTablesToEject, this.itemsToDropWhenOminous);
    }

    public static class Builder {
        private int spawnRange = 4;
        private float totalMobs = 6.0f;
        private float simultaneousMobs = 2.0f;
        private float totalMobsAddedPerPlayer = 2.0f;
        private float simultaneousMobsAddedPerPlayer = 1.0f;
        private int ticksBetweenSpawn = 40;
        private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.empty();
        private DataPool<RegistryKey<LootTable>> lootTablesToEject = DataPool.builder().add(LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER).add(LootTables.TRIAL_CHAMBER_KEY_SPAWNER).build();
        private RegistryKey<LootTable> itemsToDropWhenOminous = LootTables.TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS_SPAWNER;

        public Builder spawnRange(int spawnRange) {
            this.spawnRange = spawnRange;
            return this;
        }

        public Builder totalMobs(float totalMobs) {
            this.totalMobs = totalMobs;
            return this;
        }

        public Builder simultaneousMobs(float simultaneousMobs) {
            this.simultaneousMobs = simultaneousMobs;
            return this;
        }

        public Builder totalMobsAddedPerPlayer(float totalMobsAddedPerPlayer) {
            this.totalMobsAddedPerPlayer = totalMobsAddedPerPlayer;
            return this;
        }

        public Builder simultaneousMobsAddedPerPlayer(float simultaneousMobsAddedPerPlayer) {
            this.simultaneousMobsAddedPerPlayer = simultaneousMobsAddedPerPlayer;
            return this;
        }

        public Builder ticksBetweenSpawn(int ticksBetweenSpawn) {
            this.ticksBetweenSpawn = ticksBetweenSpawn;
            return this;
        }

        public Builder spawnPotentials(DataPool<MobSpawnerEntry> spawnPotentials) {
            this.spawnPotentials = spawnPotentials;
            return this;
        }

        public Builder lootTablesToEject(DataPool<RegistryKey<LootTable>> lootTablesToEject) {
            this.lootTablesToEject = lootTablesToEject;
            return this;
        }

        public Builder itemsToDropWhenOminous(RegistryKey<LootTable> itemsToDropWhenOminous) {
            this.itemsToDropWhenOminous = itemsToDropWhenOminous;
            return this;
        }

        public TrialSpawnerConfig build() {
            return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, this.spawnPotentials, this.lootTablesToEject, this.itemsToDropWhenOminous);
        }
    }
}

