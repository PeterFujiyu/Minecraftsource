/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractFileResourcePack
implements ResourcePack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourcePackInfo info;

    protected AbstractFileResourcePack(ResourcePackInfo info) {
        this.info = info;
    }

    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException {
        InputSupplier<InputStream> lv = this.openRoot("pack.mcmeta");
        if (lv == null) {
            return null;
        }
        try (InputStream inputStream = lv.get();){
            T t = AbstractFileResourcePack.parseMetadata(metadataSerializer, inputStream);
            return t;
        }
    }

    @Nullable
    public static <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer, InputStream inputStream) {
        JsonObject jsonObject;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
            jsonObject = JsonHelper.deserialize(bufferedReader);
        } catch (Exception exception) {
            LOGGER.error("Couldn't load {} metadata", (Object)metadataSerializer.name(), (Object)exception);
            return null;
        }
        if (!jsonObject.has(metadataSerializer.name())) {
            return null;
        }
        return metadataSerializer.codec().parse(JsonOps.INSTANCE, jsonObject.get(metadataSerializer.name())).ifError(error -> LOGGER.error("Couldn't load {} metadata: {}", (Object)metadataSerializer.name(), error)).result().orElse(null);
    }

    @Override
    public ResourcePackInfo getInfo() {
        return this.info;
    }
}

