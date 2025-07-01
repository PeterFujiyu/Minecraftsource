/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.InitialWorldOptions;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.DataPackContents;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.WorldGenSettings;

@Environment(value=EnvType.CLIENT)
public record GeneratorOptionsHolder(GeneratorOptions generatorOptions, Registry<DimensionOptions> dimensionOptionsRegistry, DimensionOptionsRegistryHolder selectedDimensions, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions initialWorldCreationOptions) {
    public GeneratorOptionsHolder(WorldGenSettings worldGenSettings, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration) {
        this(worldGenSettings.generatorOptions(), worldGenSettings.dimensionOptionsRegistryHolder(), combinedDynamicRegistries, dataPackContents, dataConfiguration, new InitialWorldOptions(WorldCreator.Mode.SURVIVAL, Set.of(), null));
    }

    public GeneratorOptionsHolder(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder selectedDimensions, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions arg6) {
        this(generatorOptions, (Registry<DimensionOptions>)combinedDynamicRegistries.get(ServerDynamicRegistryType.DIMENSIONS).getOrThrow(RegistryKeys.DIMENSION), selectedDimensions, combinedDynamicRegistries.with(ServerDynamicRegistryType.DIMENSIONS, new DynamicRegistryManager.Immutable[0]), dataPackContents, dataConfiguration, arg6);
    }

    public GeneratorOptionsHolder with(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder selectedDimensions) {
        return new GeneratorOptionsHolder(generatorOptions, this.dimensionOptionsRegistry, selectedDimensions, this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public GeneratorOptionsHolder apply(Modifier modifier) {
        return new GeneratorOptionsHolder((GeneratorOptions)modifier.apply(this.generatorOptions), this.dimensionOptionsRegistry, this.selectedDimensions, this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public GeneratorOptionsHolder apply(RegistryAwareModifier modifier) {
        return new GeneratorOptionsHolder(this.generatorOptions, this.dimensionOptionsRegistry, (DimensionOptionsRegistryHolder)modifier.apply(this.getCombinedRegistryManager(), this.selectedDimensions), this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public DynamicRegistryManager.Immutable getCombinedRegistryManager() {
        return this.combinedDynamicRegistries.getCombinedRegistryManager();
    }

    public void initializeIndexedFeaturesLists() {
        for (DimensionOptions lv : this.dimensionOptionsRegistry()) {
            lv.chunkGenerator().initializeIndexedFeaturesList();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Modifier
    extends UnaryOperator<GeneratorOptions> {
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RegistryAwareModifier
    extends BiFunction<DynamicRegistryManager.Immutable, DimensionOptionsRegistryHolder, DimensionOptionsRegistryHolder> {
    }
}

