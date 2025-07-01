/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.registry;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class ReloadableRegistries {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

    public static CompletableFuture<ReloadResult> reload(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, List<Registry.PendingTagLoad<?>> pendingTagLoads, ResourceManager resourceManager, Executor prepareExecutor) {
        List<RegistryWrapper.Impl<?>> list2 = TagGroupLoader.collectRegistries(dynamicRegistries.getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE), pendingTagLoads);
        RegistryWrapper.WrapperLookup lv = RegistryWrapper.WrapperLookup.of(list2.stream());
        RegistryOps<JsonElement> lv2 = lv.getOps(JsonOps.INSTANCE);
        List<CompletableFuture> list3 = LootDataType.stream().map(type -> ReloadableRegistries.prepare(type, lv2, resourceManager, prepareExecutor)).toList();
        CompletableFuture completableFuture = Util.combineSafe(list3);
        return completableFuture.thenApplyAsync(registries -> ReloadableRegistries.toResult(dynamicRegistries, lv, registries), prepareExecutor);
    }

    private static <T> CompletableFuture<MutableRegistry<?>> prepare(LootDataType<T> type, RegistryOps<JsonElement> ops, ResourceManager resourceManager, Executor prepareExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            SimpleRegistry lv = new SimpleRegistry(type.registryKey(), Lifecycle.experimental());
            HashMap<Identifier, Object> map = new HashMap<Identifier, Object>();
            JsonDataLoader.load(resourceManager, type.registryKey(), (DynamicOps<JsonElement>)ops, type.codec(), map);
            map.forEach((id, value) -> lv.add(RegistryKey.of(type.registryKey(), id), value, DEFAULT_REGISTRY_ENTRY_INFO));
            TagGroupLoader.loadInitial(resourceManager, lv);
            return lv;
        }, prepareExecutor);
    }

    private static ReloadResult toResult(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, RegistryWrapper.WrapperLookup nonReloadables, List<MutableRegistry<?>> registries) {
        CombinedDynamicRegistries<ServerDynamicRegistryType> lv = ReloadableRegistries.with(dynamicRegistries, registries);
        RegistryWrapper.WrapperLookup lv2 = ReloadableRegistries.concat(nonReloadables, lv.get(ServerDynamicRegistryType.RELOADABLE));
        ReloadableRegistries.validate(lv2);
        return new ReloadResult(lv, lv2);
    }

    private static RegistryWrapper.WrapperLookup concat(RegistryWrapper.WrapperLookup first, RegistryWrapper.WrapperLookup second) {
        return RegistryWrapper.WrapperLookup.of(Stream.concat(first.stream(), second.stream()));
    }

    private static void validate(RegistryWrapper.WrapperLookup registries) {
        ErrorReporter.Impl lv = new ErrorReporter.Impl();
        LootTableReporter lv2 = new LootTableReporter(lv, LootContextTypes.GENERIC, registries);
        LootDataType.stream().forEach(type -> ReloadableRegistries.validateLootData(lv2, type, registries));
        lv.getErrors().forEach((id, error) -> LOGGER.warn("Found loot table element validation problem in {}: {}", id, error));
    }

    private static CombinedDynamicRegistries<ServerDynamicRegistryType> with(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, List<MutableRegistry<?>> registries) {
        return dynamicRegistries.with(ServerDynamicRegistryType.RELOADABLE, new DynamicRegistryManager.ImmutableImpl(registries).toImmutable());
    }

    private static <T> void validateLootData(LootTableReporter reporter, LootDataType<T> lootDataType, RegistryWrapper.WrapperLookup registries) {
        RegistryEntryLookup lv = registries.getOrThrow(lootDataType.registryKey());
        lv.streamEntries().forEach(entry -> lootDataType.validate(reporter, entry.registryKey(), entry.value()));
    }

    public record ReloadResult(CombinedDynamicRegistries<ServerDynamicRegistryType> layers, RegistryWrapper.WrapperLookup lookupWithUpdatedTags) {
    }

    public static class Lookup {
        private final RegistryWrapper.WrapperLookup registries;

        public Lookup(RegistryWrapper.WrapperLookup registries) {
            this.registries = registries;
        }

        public RegistryEntryLookup.RegistryLookup createRegistryLookup() {
            return this.registries;
        }

        public Collection<Identifier> getIds(RegistryKey<? extends Registry<?>> registryRef) {
            return this.registries.getOrThrow(registryRef).streamKeys().map(RegistryKey::getValue).toList();
        }

        public LootTable getLootTable(RegistryKey<LootTable> key) {
            return this.registries.getOptional(RegistryKeys.LOOT_TABLE).flatMap(registryEntryLookup -> registryEntryLookup.getOptional(key)).map(RegistryEntry::value).orElse(LootTable.EMPTY);
        }
    }
}

