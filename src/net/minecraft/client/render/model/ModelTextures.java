/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelNameSupplier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelTextures {
    public static final ModelTextures EMPTY = new ModelTextures(Map.of());
    private static final char TEXTURE_REFERENCE_PREFIX = '#';
    private final Map<String, SpriteIdentifier> textures;

    ModelTextures(Map<String, SpriteIdentifier> textures) {
        this.textures = textures;
    }

    @Nullable
    public SpriteIdentifier get(String textureId) {
        if (ModelTextures.isTextureReference(textureId)) {
            textureId = textureId.substring(1);
        }
        return this.textures.get(textureId);
    }

    private static boolean isTextureReference(String textureId) {
        return textureId.charAt(0) == '#';
    }

    public static Textures fromJson(JsonObject json, Identifier atlasTexture) {
        Textures.Builder lv = new Textures.Builder();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            ModelTextures.add(atlasTexture, entry.getKey(), entry.getValue().getAsString(), lv);
        }
        return lv.build();
    }

    private static void add(Identifier atlasTexture, String textureId, String value, Textures.Builder builder) {
        if (ModelTextures.isTextureReference(value)) {
            builder.addTextureReference(textureId, value.substring(1));
        } else {
            Identifier lv = Identifier.tryParse(value);
            if (lv == null) {
                throw new JsonParseException(value + " is not valid resource location");
            }
            builder.addSprite(textureId, new SpriteIdentifier(atlasTexture, lv));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Textures(Map<String, Entry> values) {
        public static final Textures EMPTY = new Textures(Map.of());

        @Environment(value=EnvType.CLIENT)
        public static class Builder {
            private final Map<String, Entry> entries = new HashMap<String, Entry>();

            public Builder addTextureReference(String textureId, String target) {
                this.entries.put(textureId, new TextureReferenceEntry(target));
                return this;
            }

            public Builder addSprite(String textureId, SpriteIdentifier spriteId) {
                this.entries.put(textureId, new SpriteEntry(spriteId));
                return this;
            }

            public Textures build() {
                if (this.entries.isEmpty()) {
                    return EMPTY;
                }
                return new Textures(Map.copyOf(this.entries));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private static final Logger LOGGER = LogUtils.getLogger();
        private final List<Textures> textures = new ArrayList<Textures>();

        public Builder addLast(Textures textures) {
            this.textures.addLast(textures);
            return this;
        }

        public Builder addFirst(Textures textures) {
            this.textures.addFirst(textures);
            return this;
        }

        public ModelTextures build(ModelNameSupplier modelNameSupplier) {
            if (this.textures.isEmpty()) {
                return EMPTY;
            }
            Object2ObjectArrayMap<String, SpriteIdentifier> object2ObjectMap = new Object2ObjectArrayMap<String, SpriteIdentifier>();
            Object2ObjectArrayMap object2ObjectMap2 = new Object2ObjectArrayMap();
            for (Textures lv : Lists.reverse(this.textures)) {
                lv.values.forEach((textureId, entry) -> {
                    Entry entry2 = entry;
                    Objects.requireNonNull(entry2);
                    Entry lv = entry2;
                    int i = 0;
                    switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SpriteEntry.class, TextureReferenceEntry.class}, (Object)lv, i)) {
                        default: {
                            throw new MatchException(null, null);
                        }
                        case 0: {
                            SpriteEntry lv2 = (SpriteEntry)lv;
                            object2ObjectMap2.remove(textureId);
                            object2ObjectMap.put((String)textureId, lv2.material());
                            break;
                        }
                        case 1: {
                            TextureReferenceEntry lv3 = (TextureReferenceEntry)lv;
                            object2ObjectMap.remove(textureId);
                            object2ObjectMap2.put(textureId, lv3);
                        }
                    }
                });
            }
            if (object2ObjectMap2.isEmpty()) {
                return new ModelTextures(object2ObjectMap);
            }
            boolean bl = true;
            while (bl) {
                bl = false;
                ObjectIterator objectIterator = Object2ObjectMaps.fastIterator(object2ObjectMap2);
                while (objectIterator.hasNext()) {
                    Object2ObjectMap.Entry entry2 = (Object2ObjectMap.Entry)objectIterator.next();
                    SpriteIdentifier lv2 = (SpriteIdentifier)object2ObjectMap.get(((TextureReferenceEntry)entry2.getValue()).target);
                    if (lv2 == null) continue;
                    object2ObjectMap.put((String)entry2.getKey(), lv2);
                    objectIterator.remove();
                    bl = true;
                }
            }
            if (!object2ObjectMap2.isEmpty()) {
                LOGGER.warn("Unresolved texture references in {}:\n{}", modelNameSupplier.get(), (Object)object2ObjectMap2.entrySet().stream().map(entry -> "\t#" + (String)entry.getKey() + "-> #" + ((TextureReferenceEntry)entry.getValue()).target + "\n").collect(Collectors.joining()));
            }
            return new ModelTextures(object2ObjectMap);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record TextureReferenceEntry(String target) implements Entry
    {
    }

    @Environment(value=EnvType.CLIENT)
    record SpriteEntry(SpriteIdentifier material) implements Entry
    {
    }

    @Environment(value=EnvType.CLIENT)
    public static sealed interface Entry
    permits SpriteEntry, TextureReferenceEntry {
    }
}

