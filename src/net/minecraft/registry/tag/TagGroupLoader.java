/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.registry.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final EntrySupplier<T> entrySupplier;
    private final String dataType;

    public TagGroupLoader(EntrySupplier<T> entrySupplier, String dataType) {
        this.entrySupplier = entrySupplier;
        this.dataType = dataType;
    }

    public Map<Identifier, List<TrackedEntry>> loadTags(ResourceManager resourceManager) {
        HashMap<Identifier, List<TrackedEntry>> map = new HashMap<Identifier, List<TrackedEntry>>();
        ResourceFinder lv = ResourceFinder.json(this.dataType);
        for (Map.Entry<Identifier, List<Resource>> entry2 : lv.findAllResources(resourceManager).entrySet()) {
            Identifier lv2 = entry2.getKey();
            Identifier lv3 = lv.toResourceId(lv2);
            for (Resource lv4 : entry2.getValue()) {
                try {
                    BufferedReader reader = lv4.getReader();
                    try {
                        JsonElement jsonElement = JsonParser.parseReader(reader);
                        List list = map.computeIfAbsent(lv3, id -> new ArrayList());
                        TagFile lv5 = (TagFile)TagFile.CODEC.parse(new Dynamic<JsonElement>(JsonOps.INSTANCE, jsonElement)).getOrThrow();
                        if (lv5.replace()) {
                            list.clear();
                        }
                        String string = lv4.getPackId();
                        lv5.entries().forEach(entry -> list.add(new TrackedEntry((TagEntry)entry, string)));
                    } finally {
                        if (reader == null) continue;
                        ((Reader)reader).close();
                    }
                } catch (Exception exception) {
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", lv3, lv2, lv4.getPackId(), exception);
                }
            }
        }
        return map;
    }

    private Either<List<TrackedEntry>, List<T>> resolveAll(TagEntry.ValueGetter<T> valueGetter, List<TrackedEntry> entries) {
        LinkedHashSet sequencedSet = new LinkedHashSet();
        ArrayList<TrackedEntry> list2 = new ArrayList<TrackedEntry>();
        for (TrackedEntry lv : entries) {
            if (lv.entry().resolve(valueGetter, sequencedSet::add)) continue;
            list2.add(lv);
        }
        return list2.isEmpty() ? Either.right(List.copyOf(sequencedSet)) : Either.left(list2);
    }

    public Map<Identifier, List<T>> buildGroup(Map<Identifier, List<TrackedEntry>> tags) {
        final HashMap map2 = new HashMap();
        TagEntry.ValueGetter lv = new TagEntry.ValueGetter<T>(){

            @Override
            @Nullable
            public T direct(Identifier id, boolean required) {
                return TagGroupLoader.this.entrySupplier.get(id, required).orElse(null);
            }

            @Override
            @Nullable
            public Collection<T> tag(Identifier id) {
                return (Collection)map2.get(id);
            }
        };
        DependencyTracker<Identifier, TagDependencies> lv2 = new DependencyTracker<Identifier, TagDependencies>();
        tags.forEach((id, entries) -> lv2.add((Identifier)id, new TagDependencies((List<TrackedEntry>)entries)));
        lv2.traverse((id, dependencies) -> this.resolveAll(lv, dependencies.entries).ifLeft(missingReferences -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", id, (Object)missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight(values -> map2.put((Identifier)id, (List)values)));
        return map2;
    }

    public static <T> void loadFromNetwork(TagPacketSerializer.Serialized tags, MutableRegistry<T> registry) {
        tags.toRegistryTags(registry).tags.forEach(registry::setEntries);
    }

    public static List<Registry.PendingTagLoad<?>> startReload(ResourceManager resourceManager, DynamicRegistryManager registryManager) {
        return registryManager.streamAllRegistries().map(registry -> TagGroupLoader.startReload(resourceManager, registry.value())).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
    }

    public static <T> void loadInitial(ResourceManager resourceManager, MutableRegistry<T> registry) {
        RegistryKey lv = registry.getKey();
        TagGroupLoader<RegistryEntry<T>> lv2 = new TagGroupLoader<RegistryEntry<T>>(EntrySupplier.forInitial(registry), RegistryKeys.getTagPath(lv));
        lv2.buildGroup(lv2.loadTags(resourceManager)).forEach((id, entries) -> registry.setEntries(TagKey.of(lv, id), (List)entries));
    }

    private static <T> Map<TagKey<T>, List<RegistryEntry<T>>> toTagKeyedMap(RegistryKey<? extends Registry<T>> registryRef, Map<Identifier, List<RegistryEntry<T>>> tags) {
        return tags.entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> TagKey.of(registryRef, (Identifier)entry.getKey()), Map.Entry::getValue));
    }

    private static <T> Optional<Registry.PendingTagLoad<T>> startReload(ResourceManager resourceManager, Registry<T> registry) {
        RegistryKey<Registry<T>> lv = registry.getKey();
        TagGroupLoader<RegistryEntry<T>> lv2 = new TagGroupLoader<RegistryEntry<T>>(EntrySupplier.forReload(registry), RegistryKeys.getTagPath(lv));
        RegistryTags<T> lv3 = new RegistryTags<T>(lv, TagGroupLoader.toTagKeyedMap(registry.getKey(), lv2.buildGroup(lv2.loadTags(resourceManager))));
        return lv3.tags().isEmpty() ? Optional.empty() : Optional.of(registry.startTagReload(lv3));
    }

    public static List<RegistryWrapper.Impl<?>> collectRegistries(DynamicRegistryManager.Immutable registryManager, List<Registry.PendingTagLoad<?>> tagLoads) {
        ArrayList list2 = new ArrayList();
        registryManager.streamAllRegistries().forEach(registry -> {
            Registry.PendingTagLoad lv = TagGroupLoader.find(tagLoads, registry.key());
            list2.add(lv != null ? lv.getLookup() : registry.value());
        });
        return list2;
    }

    @Nullable
    private static Registry.PendingTagLoad<?> find(List<Registry.PendingTagLoad<?>> pendingTags, RegistryKey<? extends Registry<?>> registryRef) {
        for (Registry.PendingTagLoad<?> lv : pendingTags) {
            if (lv.getKey() != registryRef) continue;
            return lv;
        }
        return null;
    }

    public static interface EntrySupplier<T> {
        public Optional<? extends T> get(Identifier var1, boolean var2);

        public static <T> EntrySupplier<? extends RegistryEntry<T>> forReload(Registry<T> registry) {
            return (id, required) -> registry.getEntry(id);
        }

        public static <T> EntrySupplier<RegistryEntry<T>> forInitial(MutableRegistry<T> registry) {
            RegistryEntryLookup lv = registry.createMutableRegistryLookup();
            return (id, required) -> (required ? lv : registry).getOptional(RegistryKey.of(registry.getKey(), id));
        }
    }

    public record TrackedEntry(TagEntry entry, String source) {
        @Override
        public String toString() {
            return String.valueOf(this.entry) + " (from " + this.source + ")";
        }
    }

    public record RegistryTags<T>(RegistryKey<? extends Registry<T>> key, Map<TagKey<T>, List<RegistryEntry<T>>> tags) {
    }

    record TagDependencies(List<TrackedEntry> entries) implements DependencyTracker.Dependencies<Identifier>
    {
        @Override
        public void forDependencies(Consumer<Identifier> callback) {
            this.entries.forEach(entry -> entry.entry.forEachRequiredTagId(callback));
        }

        @Override
        public void forOptionalDependencies(Consumer<Identifier> callback) {
            this.entries.forEach(entry -> entry.entry.forEachOptionalTagId(callback));
        }
    }
}

