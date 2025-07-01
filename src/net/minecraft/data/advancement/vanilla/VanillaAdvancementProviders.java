/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.data.advancement.vanilla;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.advancement.AdvancementProvider;
import net.minecraft.data.advancement.vanilla.VanillaAdventureTabAdvancementGenerator;
import net.minecraft.data.advancement.vanilla.VanillaEndTabAdvancementGenerator;
import net.minecraft.data.advancement.vanilla.VanillaHusbandryTabAdvancementGenerator;
import net.minecraft.data.advancement.vanilla.VanillaNetherTabAdvancementGenerator;
import net.minecraft.data.advancement.vanilla.VanillaStoryTabAdvancementGenerator;
import net.minecraft.registry.RegistryWrapper;

public class VanillaAdvancementProviders {
    public static AdvancementProvider createVanillaProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        return new AdvancementProvider(output, registriesFuture, List.of(new VanillaEndTabAdvancementGenerator(), new VanillaHusbandryTabAdvancementGenerator(), new VanillaAdventureTabAdvancementGenerator(), new VanillaNetherTabAdvancementGenerator(), new VanillaStoryTabAdvancementGenerator()));
    }
}

