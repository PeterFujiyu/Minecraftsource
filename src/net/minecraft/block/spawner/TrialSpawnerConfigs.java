/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.spawner;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import org.jetbrains.annotations.Nullable;

public class TrialSpawnerConfigs {
    private static final ConfigKeyPair BREEZE = ConfigKeyPair.of("trial_chamber/breeze");
    private static final ConfigKeyPair MELEE_HUSK = ConfigKeyPair.of("trial_chamber/melee/husk");
    private static final ConfigKeyPair MELEE_SPIDER = ConfigKeyPair.of("trial_chamber/melee/spider");
    private static final ConfigKeyPair MELEE_ZOMBIE = ConfigKeyPair.of("trial_chamber/melee/zombie");
    private static final ConfigKeyPair RANGED_POISON_SKELETON = ConfigKeyPair.of("trial_chamber/ranged/poison_skeleton");
    private static final ConfigKeyPair RANGED_SKELETON = ConfigKeyPair.of("trial_chamber/ranged/skeleton");
    private static final ConfigKeyPair RANGED_STRAY = ConfigKeyPair.of("trial_chamber/ranged/stray");
    private static final ConfigKeyPair SLOW_RANGED_POISON_SKELETON = ConfigKeyPair.of("trial_chamber/slow_ranged/poison_skeleton");
    private static final ConfigKeyPair SLOW_RANGED_SKELETON = ConfigKeyPair.of("trial_chamber/slow_ranged/skeleton");
    private static final ConfigKeyPair SLOW_RANGED_STRAY = ConfigKeyPair.of("trial_chamber/slow_ranged/stray");
    private static final ConfigKeyPair SMALL_MELEE_BABY_ZOMBIE = ConfigKeyPair.of("trial_chamber/small_melee/baby_zombie");
    private static final ConfigKeyPair SMALL_MELEE_CAVE_SPIDER = ConfigKeyPair.of("trial_chamber/small_melee/cave_spider");
    private static final ConfigKeyPair SMALL_MELEE_SILVERFISH = ConfigKeyPair.of("trial_chamber/small_melee/silverfish");
    private static final ConfigKeyPair SMALL_MELEE_SLIME = ConfigKeyPair.of("trial_chamber/small_melee/slime");

    public static void bootstrap(Registerable<TrialSpawnerConfig> registry) {
        TrialSpawnerConfigs.register(registry, BREEZE, TrialSpawnerConfig.builder().simultaneousMobs(1.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(2.0f).totalMobsAddedPerPlayer(1.0f).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.BREEZE))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(4.0f).totalMobsAddedPerPlayer(1.0f).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.BREEZE))).lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
        TrialSpawnerConfigs.register(registry, MELEE_HUSK, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.HUSK))).build(), TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.HUSK, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
        TrialSpawnerConfigs.register(registry, MELEE_SPIDER, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SPIDER))).build(), TrialSpawnerConfigs.ominousMeleeBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SPIDER))).lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
        TrialSpawnerConfigs.register(registry, MELEE_ZOMBIE, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.ZOMBIE))).build(), TrialSpawnerConfigs.genericBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.ZOMBIE, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, RANGED_POISON_SKELETON, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.BOGGED))).build(), TrialSpawnerConfigs.genericBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.BOGGED, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, RANGED_SKELETON, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SKELETON))).build(), TrialSpawnerConfigs.genericBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SKELETON, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, RANGED_STRAY, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.STRAY))).build(), TrialSpawnerConfigs.genericBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.STRAY, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, SLOW_RANGED_POISON_SKELETON, TrialSpawnerConfigs.slowRangedBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.BOGGED))).build(), TrialSpawnerConfigs.slowRangedBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.BOGGED, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, SLOW_RANGED_SKELETON, TrialSpawnerConfigs.slowRangedBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SKELETON))).build(), TrialSpawnerConfigs.slowRangedBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SKELETON, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, SLOW_RANGED_STRAY, TrialSpawnerConfigs.slowRangedBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.STRAY))).build(), TrialSpawnerConfigs.slowRangedBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.STRAY, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, SMALL_MELEE_BABY_ZOMBIE, TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.ZOMBIE, nbt -> nbt.putBoolean("IsBaby", true), null))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.ZOMBIE, nbt -> nbt.putBoolean("IsBaby", true), LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, SMALL_MELEE_CAVE_SPIDER, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.CAVE_SPIDER))).build(), TrialSpawnerConfigs.ominousMeleeBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.CAVE_SPIDER))).build());
        TrialSpawnerConfigs.register(registry, SMALL_MELEE_SILVERFISH, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SILVERFISH))).build(), TrialSpawnerConfigs.ominousMeleeBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.of(TrialSpawnerConfigs.createEntry(EntityType.SILVERFISH))).build());
        TrialSpawnerConfigs.register(registry, SMALL_MELEE_SLIME, TrialSpawnerConfigs.genericBuilder().spawnPotentials(DataPool.builder().add(TrialSpawnerConfigs.createEntry(EntityType.SLIME, (NbtCompound nbt) -> nbt.putByte("Size", (byte)1)), 3).add(TrialSpawnerConfigs.createEntry(EntityType.SLIME, (NbtCompound nbt) -> nbt.putByte("Size", (byte)2)), 1).build()).build(), TrialSpawnerConfigs.ominousMeleeBuilder().lootTablesToEject(DataPool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(DataPool.builder().add(TrialSpawnerConfigs.createEntry(EntityType.SLIME, (NbtCompound nbt) -> nbt.putByte("Size", (byte)1)), 3).add(TrialSpawnerConfigs.createEntry(EntityType.SLIME, (NbtCompound nbt) -> nbt.putByte("Size", (byte)2)), 1).build()).build());
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType) {
        return TrialSpawnerConfigs.createEntry(entityType, nbt -> {}, null);
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, Consumer<NbtCompound> nbtConsumer) {
        return TrialSpawnerConfigs.createEntry(entityType, nbtConsumer, null);
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, RegistryKey<LootTable> equipmentTable) {
        return TrialSpawnerConfigs.createEntry(entityType, nbt -> {}, equipmentTable);
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, Consumer<NbtCompound> nbtConsumer, @Nullable RegistryKey<LootTable> equipmentTable) {
        NbtCompound lv = new NbtCompound();
        lv.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        nbtConsumer.accept(lv);
        Optional<EquipmentTable> optional = Optional.ofNullable(equipmentTable).map(lootTable -> new EquipmentTable((RegistryKey<LootTable>)lootTable, 0.0f));
        return new MobSpawnerEntry(lv, Optional.empty(), optional);
    }

    private static void register(Registerable<TrialSpawnerConfig> registry, ConfigKeyPair configPair, TrialSpawnerConfig normalConfig, TrialSpawnerConfig ominousConfig) {
        registry.register(configPair.normal, normalConfig);
        registry.register(configPair.ominous, ominousConfig);
    }

    static RegistryKey<TrialSpawnerConfig> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.TRIAL_SPAWNER, Identifier.ofVanilla(id));
    }

    private static TrialSpawnerConfig.Builder ominousMeleeBuilder() {
        return TrialSpawnerConfig.builder().simultaneousMobs(4.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(12.0f);
    }

    private static TrialSpawnerConfig.Builder slowRangedBuilder() {
        return TrialSpawnerConfig.builder().simultaneousMobs(4.0f).simultaneousMobsAddedPerPlayer(2.0f).ticksBetweenSpawn(160);
    }

    private static TrialSpawnerConfig.Builder genericBuilder() {
        return TrialSpawnerConfig.builder().simultaneousMobs(3.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20);
    }

    record ConfigKeyPair(RegistryKey<TrialSpawnerConfig> normal, RegistryKey<TrialSpawnerConfig> ominous) {
        public static ConfigKeyPair of(String id) {
            return new ConfigKeyPair(TrialSpawnerConfigs.keyOf(id + "/normal"), TrialSpawnerConfigs.keyOf(id + "/ominous"));
        }
    }
}

