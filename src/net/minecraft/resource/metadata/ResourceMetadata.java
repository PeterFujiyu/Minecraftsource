/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.resource.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.JsonHelper;

public interface ResourceMetadata {
    public static final ResourceMetadata NONE = new ResourceMetadata(){

        @Override
        public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
            return Optional.empty();
        }
    };
    public static final InputSupplier<ResourceMetadata> NONE_SUPPLIER = () -> NONE;

    public static ResourceMetadata create(InputStream stream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));){
            final JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
            ResourceMetadata resourceMetadata = new ResourceMetadata(){

                @Override
                public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
                    String string = serializer.name();
                    if (jsonObject.has(string)) {
                        Object object = serializer.codec().parse(JsonOps.INSTANCE, jsonObject.get(string)).getOrThrow(JsonParseException::new);
                        return Optional.of(object);
                    }
                    return Optional.empty();
                }
            };
            return resourceMetadata;
        }
    }

    public <T> Optional<T> decode(ResourceMetadataSerializer<T> var1);

    default public ResourceMetadata copy(Collection<ResourceMetadataSerializer<?>> serializers) {
        Builder lv = new Builder();
        for (ResourceMetadataSerializer<?> lv2 : serializers) {
            this.decodeAndAdd(lv, lv2);
        }
        return lv.build();
    }

    private <T> void decodeAndAdd(Builder builder, ResourceMetadataSerializer<T> serializer) {
        this.decode(serializer).ifPresent(value -> builder.add(serializer, value));
    }

    public static class Builder {
        private final ImmutableMap.Builder<ResourceMetadataSerializer<?>, Object> values = ImmutableMap.builder();

        public <T> Builder add(ResourceMetadataSerializer<T> serializer, T value) {
            this.values.put(serializer, value);
            return this;
        }

        public ResourceMetadata build() {
            final ImmutableMap<ResourceMetadataSerializer<?>, Object> immutableMap = this.values.build();
            if (immutableMap.isEmpty()) {
                return NONE;
            }
            return new ResourceMetadata(){

                @Override
                public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
                    return Optional.ofNullable(immutableMap.get(serializer));
                }
            };
        }
    }
}

