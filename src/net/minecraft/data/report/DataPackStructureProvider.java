/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.data.report;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class DataPackStructureProvider
implements DataProvider {
    private final DataOutput output;
    private static final Entry RELOADABLE_REGISTRY = new Entry(true, false, true);
    private static final Entry RELOADABLE_REGISTRY_WITH_TAGS = new Entry(true, true, true);
    private static final Entry DYNAMIC_REGISTRY = new Entry(true, true, false);
    private static final Entry STATIC_REGISTRY = new Entry(false, true, true);
    private static final Map<RegistryKey<? extends Registry<?>>, Entry> RELOADABLE_REGISTRIES = Map.of(RegistryKeys.RECIPE, RELOADABLE_REGISTRY, RegistryKeys.ADVANCEMENT, RELOADABLE_REGISTRY, RegistryKeys.LOOT_TABLE, RELOADABLE_REGISTRY_WITH_TAGS, RegistryKeys.ITEM_MODIFIER, RELOADABLE_REGISTRY_WITH_TAGS, RegistryKeys.PREDICATE, RELOADABLE_REGISTRY_WITH_TAGS);
    private static final Map<String, FakeRegistry> FAKE_REGISTRIES = Map.of("structure", new FakeRegistry(Format.STRUCTURE, new Entry(true, false, true)), "function", new FakeRegistry(Format.MCFUNCTION, new Entry(true, true, true)));
    static final Codec<RegistryKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);

    public DataPackStructureProvider(DataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Registries lv = new Registries(this.buildEntries(), FAKE_REGISTRIES);
        Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("datapack.json");
        return DataProvider.writeToPath(writer, Registries.CODEC.encodeStart(JsonOps.INSTANCE, lv).getOrThrow(), path);
    }

    @Override
    public String getName() {
        return "Datapack Structure";
    }

    private void addEntry(Map<RegistryKey<? extends Registry<?>>, Entry> map, RegistryKey<? extends Registry<?>> key, Entry entry) {
        Entry lv = map.putIfAbsent(key, entry);
        if (lv != null) {
            throw new IllegalStateException("Duplicate entry for key " + String.valueOf(key.getValue()));
        }
    }

    private Map<RegistryKey<? extends Registry<?>>, Entry> buildEntries() {
        HashMap map = new HashMap();
        net.minecraft.registry.Registries.REGISTRIES.forEach(registry -> this.addEntry(map, registry.getKey(), STATIC_REGISTRY));
        RegistryLoader.DYNAMIC_REGISTRIES.forEach(registry -> this.addEntry(map, registry.key(), DYNAMIC_REGISTRY));
        RegistryLoader.DIMENSION_REGISTRIES.forEach(registry -> this.addEntry(map, registry.key(), DYNAMIC_REGISTRY));
        RELOADABLE_REGISTRIES.forEach((key, entry) -> this.addEntry(map, (RegistryKey<? extends Registry<?>>)key, (Entry)entry));
        return map;
    }

    record Registries(Map<RegistryKey<? extends Registry<?>>, Entry> registries, Map<String, FakeRegistry> others) {
        public static final Codec<Registries> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.unboundedMap(REGISTRY_KEY_CODEC, Entry.CODEC).fieldOf("registries")).forGetter(Registries::registries), ((MapCodec)Codec.unboundedMap(Codec.STRING, FakeRegistry.CODEC).fieldOf("others")).forGetter(Registries::others)).apply((Applicative<Registries, ?>)instance, Registries::new));
    }

    record Entry(boolean elements, boolean tags, boolean stable) {
        public static final MapCodec<Entry> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("elements")).forGetter(Entry::elements), ((MapCodec)Codec.BOOL.fieldOf("tags")).forGetter(Entry::tags), ((MapCodec)Codec.BOOL.fieldOf("stable")).forGetter(Entry::stable)).apply((Applicative<Entry, ?>)instance, Entry::new));
        public static final Codec<Entry> CODEC = MAP_CODEC.codec();
    }

    record FakeRegistry(Format format, Entry entry) {
        public static final Codec<FakeRegistry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Format.CODEC.fieldOf("format")).forGetter(FakeRegistry::format), Entry.MAP_CODEC.forGetter(FakeRegistry::entry)).apply((Applicative<FakeRegistry, ?>)instance, FakeRegistry::new));
    }

    static enum Format implements StringIdentifiable
    {
        STRUCTURE("structure"),
        MCFUNCTION("mcfunction");

        public static final Codec<Format> CODEC;
        private final String id;

        private Format(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Format::values);
        }
    }
}

