/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerAdvancementLoader
extends JsonDataLoader<Advancement> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<Identifier, AdvancementEntry> advancements = Map.of();
    private AdvancementManager manager = new AdvancementManager();
    private final RegistryWrapper.WrapperLookup registries;

    public ServerAdvancementLoader(RegistryWrapper.WrapperLookup registries) {
        super(registries, Advancement.CODEC, RegistryKeys.ADVANCEMENT);
        this.registries = registries;
    }

    @Override
    protected void apply(Map<Identifier, Advancement> map, ResourceManager arg, Profiler arg2) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((id, advancement) -> {
            this.validate((Identifier)id, (Advancement)advancement);
            builder.put(id, new AdvancementEntry((Identifier)id, (Advancement)advancement));
        });
        this.advancements = builder.buildOrThrow();
        AdvancementManager lv = new AdvancementManager();
        lv.addAll(this.advancements.values());
        for (PlacedAdvancement lv2 : lv.getRoots()) {
            if (!lv2.getAdvancementEntry().value().display().isPresent()) continue;
            AdvancementPositioner.arrangeForTree(lv2);
        }
        this.manager = lv;
    }

    private void validate(Identifier id, Advancement advancement) {
        ErrorReporter.Impl lv = new ErrorReporter.Impl();
        advancement.validate(lv, this.registries);
        lv.getErrorsAsString().ifPresent(error -> LOGGER.warn("Found validation problems in advancement {}: \n{}", (Object)id, error));
    }

    @Nullable
    public AdvancementEntry get(Identifier id) {
        return this.advancements.get(id);
    }

    public AdvancementManager getManager() {
        return this.manager;
    }

    public Collection<AdvancementEntry> getAdvancements() {
        return this.advancements.values();
    }
}

