/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ItemAssetsLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder FINDER = ResourceFinder.json("items");

    public static CompletableFuture<Result> load(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> FINDER.findResources(resourceManager), executor).thenCompose(resources -> {
            ArrayList list = new ArrayList(resources.size());
            resources.forEach((id, resource) -> list.add(CompletableFuture.supplyAsync(() -> {
                Definition definition;
                block8: {
                    Identifier lv = FINDER.toResourceId((Identifier)id);
                    BufferedReader reader = resource.getReader();
                    try {
                        ItemAsset lv2 = ItemAsset.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).ifError(error -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", lv, resource.getPackId(), error.message())).result().orElse(null);
                        definition = new Definition(lv, lv2);
                        if (reader == null) break block8;
                    } catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    ((Reader)reader).close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (Exception exception) {
                            LOGGER.error("Failed to open item model {} from pack '{}'", id, resource.getPackId(), exception);
                            return new Definition(lv, null);
                        }
                    }
                    ((Reader)reader).close();
                }
                return definition;
            }, executor)));
            return Util.combineSafe(list).thenApply(definitions -> {
                HashMap<Identifier, ItemAsset> map = new HashMap<Identifier, ItemAsset>();
                for (Definition lv : definitions) {
                    if (lv.clientItemInfo == null) continue;
                    map.put(lv.id, lv.clientItemInfo);
                }
                return new Result(map);
            });
        });
    }

    @Environment(value=EnvType.CLIENT)
    record Definition(Identifier id, @Nullable ItemAsset clientItemInfo) {
        @Nullable
        public ItemAsset clientItemInfo() {
            return this.clientItemInfo;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Result(Map<Identifier, ItemAsset> contents) {
    }
}

