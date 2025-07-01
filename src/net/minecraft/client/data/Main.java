/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.data.EquipmentAssetProvider;
import net.minecraft.client.data.ModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.util.annotation.UsesSystemOut;

@Environment(value=EnvType.CLIENT)
public class Main {
    @DontObfuscate
    @UsesSystemOut(reason="System.out needed before bootstrap")
    public static void main(String[] args) throws IOException {
        SharedConstants.createGameVersion();
        OptionParser optionParser = new OptionParser();
        AbstractOptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpec2 = optionParser.accepts("client", "Include client generators");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec<String> optionSpec4 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", (String[])new String[0]);
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has(optionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn(System.out);
            return;
        }
        Path path = Paths.get((String)optionSpec4.value(optionSet), new String[0]);
        boolean bl = optionSet.has(optionSpec3);
        boolean bl2 = bl || optionSet.has(optionSpec2);
        Bootstrap.initialize();
        ClientBootstrap.initialize();
        DataGenerator lv = new DataGenerator(path, SharedConstants.getGameVersion(), true);
        Main.create(lv, bl2);
        lv.run();
    }

    public static void create(DataGenerator dataGenerator, boolean includeClient) {
        DataGenerator.Pack lv = dataGenerator.createVanillaPack(includeClient);
        lv.addProvider(ModelProvider::new);
        lv.addProvider(EquipmentAssetProvider::new);
    }
}

