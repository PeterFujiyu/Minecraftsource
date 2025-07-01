/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldCreationSettings;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.DataPackContents;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface GeneratorOptionsFactory {
    public GeneratorOptionsHolder apply(DataPackContents var1, CombinedDynamicRegistries<ServerDynamicRegistryType> var2, WorldCreationSettings var3);
}

