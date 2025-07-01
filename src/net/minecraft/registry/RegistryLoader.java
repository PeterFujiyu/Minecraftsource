/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.Instrument;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;

public class RegistryLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Comparator<RegistryKey<?>> KEY_COMPARATOR = Comparator.comparing(RegistryKey::getRegistry).thenComparing(RegistryKey::getValue);
    private static final RegistryEntryInfo EXPERIMENTAL_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());
    private static final Function<Optional<VersionedIdentifier>, RegistryEntryInfo> RESOURCE_ENTRY_INFO_GETTER = Util.memoize(knownPacks -> {
        Lifecycle lifecycle = knownPacks.map(VersionedIdentifier::isVanilla).map(vanilla -> Lifecycle.stable()).orElse(Lifecycle.experimental());
        return new RegistryEntryInfo((Optional<VersionedIdentifier>)knownPacks, lifecycle);
    });
    public static final List<Entry<?>> DYNAMIC_REGISTRIES = List.of(new Entry<DimensionType>(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC), new Entry<Biome>(RegistryKeys.BIOME, Biome.CODEC), new Entry<MessageType>(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC), new Entry(RegistryKeys.CONFIGURED_CARVER, ConfiguredCarver.CODEC), new Entry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeature.CODEC), new Entry<PlacedFeature>(RegistryKeys.PLACED_FEATURE, PlacedFeature.CODEC), new Entry<Structure>(RegistryKeys.STRUCTURE, Structure.STRUCTURE_CODEC), new Entry<StructureSet>(RegistryKeys.STRUCTURE_SET, StructureSet.CODEC), new Entry<StructureProcessorList>(RegistryKeys.PROCESSOR_LIST, StructureProcessorType.PROCESSORS_CODEC), new Entry<StructurePool>(RegistryKeys.TEMPLATE_POOL, StructurePool.CODEC), new Entry<ChunkGeneratorSettings>(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ChunkGeneratorSettings.CODEC), new Entry<DoublePerlinNoiseSampler.NoiseParameters>(RegistryKeys.NOISE_PARAMETERS, DoublePerlinNoiseSampler.NoiseParameters.CODEC), new Entry<DensityFunction>(RegistryKeys.DENSITY_FUNCTION, DensityFunction.CODEC), new Entry<WorldPreset>(RegistryKeys.WORLD_PRESET, WorldPreset.CODEC), new Entry<FlatLevelGeneratorPreset>(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.CODEC), new Entry<ArmorTrimPattern>(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC), new Entry<ArmorTrimMaterial>(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC), new Entry<TrialSpawnerConfig>(RegistryKeys.TRIAL_SPAWNER, TrialSpawnerConfig.CODEC), new Entry<WolfVariant>(RegistryKeys.WOLF_VARIANT, WolfVariant.CODEC, true), new Entry<PaintingVariant>(RegistryKeys.PAINTING_VARIANT, PaintingVariant.CODEC, true), new Entry<DamageType>(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC), new Entry<MultiNoiseBiomeSourceParameterList>(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.CODEC), new Entry<BannerPattern>(RegistryKeys.BANNER_PATTERN, BannerPattern.CODEC), new Entry<Enchantment>(RegistryKeys.ENCHANTMENT, Enchantment.CODEC), new Entry<EnchantmentProvider>(RegistryKeys.ENCHANTMENT_PROVIDER, EnchantmentProvider.CODEC), new Entry<JukeboxSong>(RegistryKeys.JUKEBOX_SONG, JukeboxSong.CODEC), new Entry<Instrument>(RegistryKeys.INSTRUMENT, Instrument.CODEC));
    public static final List<Entry<?>> DIMENSION_REGISTRIES = List.of(new Entry<DimensionOptions>(RegistryKeys.DIMENSION, DimensionOptions.CODEC));
    public static final List<Entry<?>> SYNCED_REGISTRIES = List.of(new Entry<Biome>(RegistryKeys.BIOME, Biome.NETWORK_CODEC), new Entry<MessageType>(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC), new Entry<ArmorTrimPattern>(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC), new Entry<ArmorTrimMaterial>(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC), new Entry<WolfVariant>(RegistryKeys.WOLF_VARIANT, WolfVariant.CODEC, true), new Entry<PaintingVariant>(RegistryKeys.PAINTING_VARIANT, PaintingVariant.CODEC, true), new Entry<DimensionType>(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC), new Entry<DamageType>(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC), new Entry<BannerPattern>(RegistryKeys.BANNER_PATTERN, BannerPattern.CODEC), new Entry<Enchantment>(RegistryKeys.ENCHANTMENT, Enchantment.CODEC), new Entry<JukeboxSong>(RegistryKeys.JUKEBOX_SONG, JukeboxSong.CODEC), new Entry<Instrument>(RegistryKeys.INSTRUMENT, Instrument.CODEC));

    public static DynamicRegistryManager.Immutable loadFromResource(ResourceManager resourceManager, List<RegistryWrapper.Impl<?>> registries, List<Entry<?>> entries) {
        return RegistryLoader.load((loader, infoGetter) -> loader.loadFromResource(resourceManager, infoGetter), registries, entries);
    }

    public static DynamicRegistryManager.Immutable loadFromNetwork(Map<RegistryKey<? extends Registry<?>>, ElementsAndTags> data, ResourceFactory factory, List<RegistryWrapper.Impl<?>> registries, List<Entry<?>> entries) {
        return RegistryLoader.load((loader, infoGetter) -> loader.loadFromNetwork(data, factory, infoGetter), registries, entries);
    }

    private static DynamicRegistryManager.Immutable load(RegistryLoadable loadable, List<RegistryWrapper.Impl<?>> registries, List<Entry<?>> entries) {
        HashMap map = new HashMap();
        List<Loader<?>> list3 = entries.stream().map(entry -> entry.getLoader(Lifecycle.stable(), map)).collect(Collectors.toUnmodifiableList());
        RegistryOps.RegistryInfoGetter lv = RegistryLoader.createInfoGetter(registries, list3);
        list3.forEach(loader -> loadable.apply((Loader<?>)loader, lv));
        list3.forEach(loader -> {
            MutableRegistry lv = loader.registry();
            try {
                lv.freeze();
            } catch (Exception exception) {
                map.put(lv.getKey(), exception);
            }
            if (loader.data.requiredNonEmpty && lv.size() == 0) {
                map.put(lv.getKey(), new IllegalStateException("Registry must be non-empty"));
            }
        });
        if (!map.isEmpty()) {
            throw RegistryLoader.writeAndCreateLoadingException(map);
        }
        return new DynamicRegistryManager.ImmutableImpl(list3.stream().map(Loader::registry).toList()).toImmutable();
    }

    private static RegistryOps.RegistryInfoGetter createInfoGetter(List<RegistryWrapper.Impl<?>> registries, List<Loader<?>> additionalRegistries) {
        final HashMap map = new HashMap();
        registries.forEach(registry -> map.put(registry.getKey(), RegistryLoader.createInfo(registry)));
        additionalRegistries.forEach(loader -> map.put(loader.registry.getKey(), RegistryLoader.createInfo(loader.registry)));
        return new RegistryOps.RegistryInfoGetter(){

            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
                return Optional.ofNullable((RegistryOps.RegistryInfo)map.get(registryRef));
            }
        };
    }

    private static <T> RegistryOps.RegistryInfo<T> createInfo(MutableRegistry<T> registry) {
        return new RegistryOps.RegistryInfo<T>(registry, registry.createMutableRegistryLookup(), registry.getLifecycle());
    }

    private static <T> RegistryOps.RegistryInfo<T> createInfo(RegistryWrapper.Impl<T> registry) {
        return new RegistryOps.RegistryInfo<T>(registry, registry, registry.getLifecycle());
    }

    private static CrashException writeAndCreateLoadingException(Map<RegistryKey<?>, Exception> exceptions) {
        RegistryLoader.writeLoadingError(exceptions);
        return RegistryLoader.createLoadingException(exceptions);
    }

    private static void writeLoadingError(Map<RegistryKey<?>, Exception> exceptions) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Map<Identifier, Map<Identifier, Exception>> map2 = exceptions.entrySet().stream().collect(Collectors.groupingBy(entry -> ((RegistryKey)entry.getKey()).getRegistry(), Collectors.toMap(entry -> ((RegistryKey)entry.getKey()).getValue(), Map.Entry::getValue)));
        map2.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            printWriter.printf("> Errors in registry %s:%n", entry.getKey());
            ((Map)entry.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(element -> {
                printWriter.printf(">> Errors in element %s:%n", element.getKey());
                ((Exception)element.getValue()).printStackTrace(printWriter);
            });
        });
        printWriter.flush();
        LOGGER.error("Registry loading errors:\n{}", (Object)stringWriter);
    }

    private static CrashException createLoadingException(Map<RegistryKey<?>, Exception> exceptions) {
        CrashReport lv = CrashReport.create(new IllegalStateException("Failed to load registries due to errors"), "Registry Loading");
        CrashReportSection lv2 = lv.addElement("Loading info");
        lv2.add("Errors", () -> {
            StringBuilder stringBuilder = new StringBuilder();
            exceptions.entrySet().stream().sorted(Map.Entry.comparingByKey(KEY_COMPARATOR)).forEach(entry -> stringBuilder.append("\n\t\t").append(((RegistryKey)entry.getKey()).getRegistry()).append("/").append(((RegistryKey)entry.getKey()).getValue()).append(": ").append(((Exception)entry.getValue()).getMessage()));
            return stringBuilder.toString();
        });
        return new CrashException(lv);
    }

    private static <E> void parseAndAdd(MutableRegistry<E> registry, Decoder<E> decoder, RegistryOps<JsonElement> ops, RegistryKey<E> key, Resource resource, RegistryEntryInfo entryInfo) throws IOException {
        try (BufferedReader reader = resource.getReader();){
            JsonElement jsonElement = JsonParser.parseReader(reader);
            DataResult<E> dataResult = decoder.parse(ops, jsonElement);
            E object = dataResult.getOrThrow();
            registry.add(key, object, entryInfo);
        }
    }

    static <E> void loadFromResource(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry<E> registry, Decoder<E> elementDecoder, Map<RegistryKey<?>, Exception> errors) {
        ResourceFinder lv = ResourceFinder.json(registry.getKey());
        RegistryOps<JsonElement> lv2 = RegistryOps.of(JsonOps.INSTANCE, infoGetter);
        for (Map.Entry<Identifier, Resource> entry : lv.findResources(resourceManager).entrySet()) {
            Identifier lv3 = entry.getKey();
            RegistryKey lv4 = RegistryKey.of(registry.getKey(), lv.toResourceId(lv3));
            Resource lv5 = entry.getValue();
            RegistryEntryInfo lv6 = RESOURCE_ENTRY_INFO_GETTER.apply(lv5.getKnownPackInfo());
            try {
                RegistryLoader.parseAndAdd(registry, elementDecoder, lv2, lv4, lv5, lv6);
            } catch (Exception exception) {
                errors.put(lv4, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", lv3, lv5.getPackId()), exception));
            }
        }
        TagGroupLoader.loadInitial(resourceManager, registry);
    }

    static <E> void loadFromNetwork(Map<RegistryKey<? extends Registry<?>>, ElementsAndTags> data, ResourceFactory factory, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry<E> registry, Decoder<E> decoder, Map<RegistryKey<?>, Exception> loadingErrors) {
        ElementsAndTags lv = data.get(registry.getKey());
        if (lv == null) {
            return;
        }
        RegistryOps<NbtElement> lv2 = RegistryOps.of(NbtOps.INSTANCE, infoGetter);
        RegistryOps<JsonElement> lv3 = RegistryOps.of(JsonOps.INSTANCE, infoGetter);
        ResourceFinder lv4 = ResourceFinder.json(registry.getKey());
        for (SerializableRegistries.SerializedRegistryEntry lv5 : lv.elements) {
            RegistryKey lv6 = RegistryKey.of(registry.getKey(), lv5.id());
            Optional<NbtElement> optional = lv5.data();
            if (optional.isPresent()) {
                try {
                    DataResult<E> dataResult = decoder.parse(lv2, optional.get());
                    E object = dataResult.getOrThrow();
                    registry.add(lv6, object, EXPERIMENTAL_ENTRY_INFO);
                } catch (Exception exception) {
                    loadingErrors.put(lv6, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", optional.get()), exception));
                }
                continue;
            }
            Identifier lv7 = lv4.toResourcePath(lv5.id());
            try {
                Resource lv8 = factory.getResourceOrThrow(lv7);
                RegistryLoader.parseAndAdd(registry, decoder, lv3, lv6, lv8, EXPERIMENTAL_ENTRY_INFO);
            } catch (Exception exception2) {
                loadingErrors.put(lv6, new IllegalStateException("Failed to parse local data", exception2));
            }
        }
        TagGroupLoader.loadFromNetwork(lv.tags, registry);
    }

    @FunctionalInterface
    static interface RegistryLoadable {
        public void apply(Loader<?> var1, RegistryOps.RegistryInfoGetter var2);
    }

    public record ElementsAndTags(List<SerializableRegistries.SerializedRegistryEntry> elements, TagPacketSerializer.Serialized tags) {
    }

    record Loader<T>(Entry<T> data, MutableRegistry<T> registry, Map<RegistryKey<?>, Exception> loadingErrors) {
        public void loadFromResource(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter) {
            RegistryLoader.loadFromResource(resourceManager, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
        }

        public void loadFromNetwork(Map<RegistryKey<? extends Registry<?>>, ElementsAndTags> data, ResourceFactory factory, RegistryOps.RegistryInfoGetter infoGetter) {
            RegistryLoader.loadFromNetwork(data, factory, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
        }
    }

    public record Entry<T>(RegistryKey<? extends Registry<T>> key, Codec<T> elementCodec, boolean requiredNonEmpty) {
        Entry(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
            this(key, codec, false);
        }

        Loader<T> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> errors) {
            SimpleRegistry lv = new SimpleRegistry(this.key, lifecycle);
            return new Loader(this, lv, errors);
        }

        public void addToCloner(BiConsumer<RegistryKey<? extends Registry<T>>, Codec<T>> callback) {
            callback.accept(this.key, this.elementCodec);
        }
    }
}

