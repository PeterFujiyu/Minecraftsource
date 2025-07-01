/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Language;
import org.slf4j.Logger;

public record DeprecatedLanguageData(List<String> removed, Map<String, String> renamed) {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeprecatedLanguageData NONE = new DeprecatedLanguageData(List.of(), Map.of());
    public static final Codec<DeprecatedLanguageData> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.listOf().fieldOf("removed")).forGetter(DeprecatedLanguageData::removed), ((MapCodec)Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("renamed")).forGetter(DeprecatedLanguageData::renamed)).apply((Applicative<DeprecatedLanguageData, ?>)instance, DeprecatedLanguageData::new));

    public static DeprecatedLanguageData fromInputStream(InputStream stream) {
        JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return (DeprecatedLanguageData)CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(error -> new IllegalStateException("Failed to parse deprecated language data: " + error));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static DeprecatedLanguageData fromPath(String path) {
        try (InputStream inputStream = Language.class.getResourceAsStream(path);){
            if (inputStream == null) return NONE;
            DeprecatedLanguageData deprecatedLanguageData = DeprecatedLanguageData.fromInputStream(inputStream);
            return deprecatedLanguageData;
        } catch (Exception exception) {
            LOGGER.error("Failed to read {}", (Object)path, (Object)exception);
        }
        return NONE;
    }

    public static DeprecatedLanguageData create() {
        return DeprecatedLanguageData.fromPath("/assets/minecraft/lang/deprecated.json");
    }

    public void apply(Map<String, String> map) {
        for (String string : this.removed) {
            map.remove(string);
        }
        this.renamed.forEach((oldKey, newKey) -> {
            String string3 = (String)map.remove(oldKey);
            if (string3 == null) {
                LOGGER.warn("Missing translation key for rename: {}", oldKey);
                map.remove(newKey);
            } else {
                map.put((String)newKey, string3);
            }
        });
    }
}

