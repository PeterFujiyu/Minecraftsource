/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DynamicRegistriesProvider;
import net.minecraft.data.MetadataProvider;
import net.minecraft.data.SnbtProvider;
import net.minecraft.data.advancement.vanilla.VanillaAdvancementProviders;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.loottable.rebalance.TradeRebalanceLootTableProviders;
import net.minecraft.data.loottable.vanilla.VanillaLootTableProviders;
import net.minecraft.data.recipe.VanillaRecipeGenerator;
import net.minecraft.data.report.BiomeParametersProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.DataPackStructureProvider;
import net.minecraft.data.report.ItemListProvider;
import net.minecraft.data.report.PacketReportProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.tag.TagProvider;
import net.minecraft.data.tag.rebalance.TradeRebalanceEnchantmentTagProvider;
import net.minecraft.data.tag.rebalance.TradeRebalanceStructureTagProvider;
import net.minecraft.data.tag.vanilla.VanillaBannerPatternTagProvider;
import net.minecraft.data.tag.vanilla.VanillaBiomeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaBlockTagProvider;
import net.minecraft.data.tag.vanilla.VanillaCatVariantTagProvider;
import net.minecraft.data.tag.vanilla.VanillaDamageTypeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaEnchantmentTagProvider;
import net.minecraft.data.tag.vanilla.VanillaEntityTypeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaFlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.tag.vanilla.VanillaFluidTagProvider;
import net.minecraft.data.tag.vanilla.VanillaGameEventTagProvider;
import net.minecraft.data.tag.vanilla.VanillaInstrumentTagProvider;
import net.minecraft.data.tag.vanilla.VanillaItemTagProvider;
import net.minecraft.data.tag.vanilla.VanillaPaintingVariantTagProvider;
import net.minecraft.data.tag.vanilla.VanillaPointOfInterestTypeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaStructureTagProvider;
import net.minecraft.data.tag.vanilla.VanillaWorldPresetTagProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.TradeRebalanceBuiltinRegistries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.UsesSystemOut;

public class Main {
    @UsesSystemOut(reason="System.out needed before bootstrap")
    @DontObfuscate
    public static void main(String[] args) throws IOException {
        SharedConstants.createGameVersion();
        OptionParser optionParser = new OptionParser();
        AbstractOptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpec2 = optionParser.accepts("server", "Include server generators");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("dev", "Include development tools");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("reports", "Include data reports");
        optionParser.accepts("validate", "Validate inputs");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec<String> optionSpec6 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec7 = optionParser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has(optionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn(System.out);
            return;
        }
        Path path = Paths.get((String)optionSpec6.value(optionSet), new String[0]);
        boolean bl = optionSet.has(optionSpec5);
        boolean bl2 = bl || optionSet.has(optionSpec2);
        boolean bl3 = bl || optionSet.has(optionSpec3);
        boolean bl4 = bl || optionSet.has(optionSpec4);
        List<Path> collection = optionSet.valuesOf(optionSpec7).stream().map(input -> Paths.get(input, new String[0])).toList();
        DataGenerator lv = new DataGenerator(path, SharedConstants.getGameVersion(), true);
        Main.create(lv, collection, bl2, bl3, bl4);
        lv.run();
    }

    private static <T extends DataProvider> DataProvider.Factory<T> toFactory(BiFunction<DataOutput, CompletableFuture<RegistryWrapper.WrapperLookup>, T> baseFactory, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        return output -> (DataProvider)baseFactory.apply(output, registriesFuture);
    }

    public static void create(DataGenerator dataGenerator, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev) {
        DataGenerator.Pack lv = dataGenerator.createVanillaPack(includeClient);
        lv.addProvider(output -> new SnbtProvider(output, inputs).addWriter(new StructureValidatorProvider()));
        CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture = CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup, Util.getMainWorkerExecutor());
        DataGenerator.Pack lv2 = dataGenerator.createVanillaPack(includeClient);
        lv2.addProvider(Main.toFactory(DynamicRegistriesProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaAdvancementProviders::createVanillaProvider, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaLootTableProviders::createVanillaProvider, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaRecipeGenerator.Provider::new, completableFuture));
        TagProvider lv3 = lv2.addProvider(Main.toFactory(VanillaBlockTagProvider::new, completableFuture));
        TagProvider lv4 = lv2.addProvider(output -> new VanillaItemTagProvider(output, completableFuture, lv3.getTagLookupFuture()));
        TagProvider lv5 = lv2.addProvider(Main.toFactory(VanillaBiomeTagProvider::new, completableFuture));
        TagProvider lv6 = lv2.addProvider(Main.toFactory(VanillaBannerPatternTagProvider::new, completableFuture));
        TagProvider lv7 = lv2.addProvider(Main.toFactory(VanillaStructureTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaCatVariantTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaDamageTypeTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaEntityTypeTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaFlatLevelGeneratorPresetTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaFluidTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaGameEventTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaInstrumentTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaPaintingVariantTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaPointOfInterestTypeTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaWorldPresetTagProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(VanillaEnchantmentTagProvider::new, completableFuture));
        lv2 = dataGenerator.createVanillaPack(includeServer);
        lv2.addProvider(output -> new NbtProvider(output, inputs));
        lv2 = dataGenerator.createVanillaPack(includeDev);
        lv2.addProvider(Main.toFactory(BiomeParametersProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(ItemListProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(BlockListProvider::new, completableFuture));
        lv2.addProvider(Main.toFactory(CommandSyntaxProvider::new, completableFuture));
        lv2.addProvider(RegistryDumpProvider::new);
        lv2.addProvider(PacketReportProvider::new);
        lv2.addProvider(DataPackStructureProvider::new);
        CompletableFuture<RegistryBuilder.FullPatchesRegistriesPair> completableFuture2 = TradeRebalanceBuiltinRegistries.validate(completableFuture);
        CompletionStage completableFuture3 = completableFuture2.thenApply(RegistryBuilder.FullPatchesRegistriesPair::patches);
        DataGenerator.Pack lv8 = dataGenerator.createVanillaSubPack(includeClient, "trade_rebalance");
        lv8.addProvider(Main.toFactory(DynamicRegistriesProvider::new, (CompletableFuture<RegistryWrapper.WrapperLookup>)completableFuture3));
        lv8.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.trade_rebalance.description"), FeatureSet.of(FeatureFlags.TRADE_REBALANCE)));
        lv8.addProvider(Main.toFactory(TradeRebalanceLootTableProviders::createTradeRebalanceProvider, completableFuture));
        lv8.addProvider(Main.toFactory(TradeRebalanceStructureTagProvider::new, completableFuture));
        lv8.addProvider(Main.toFactory(TradeRebalanceEnchantmentTagProvider::new, completableFuture));
        lv2 = dataGenerator.createVanillaSubPack(includeClient, "redstone_experiments");
        lv2.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.redstone_experiments.description"), FeatureSet.of(FeatureFlags.REDSTONE_EXPERIMENTS)));
        lv2 = dataGenerator.createVanillaSubPack(includeClient, "minecart_improvements");
        lv2.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.minecart_improvements.description"), FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS)));
    }
}

