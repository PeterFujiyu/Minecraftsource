/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record Defines(Map<String, String> values, Set<String> flags) {
    public static final Defines EMPTY = new Defines(Map.of(), Set.of());
    public static final Codec<Defines> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("values", Map.of()).forGetter(Defines::values), Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(Defines::flags)).apply((Applicative<Defines, ?>)instance, Defines::new));

    public static Builder builder() {
        return new Builder();
    }

    public Defines withMerged(Defines other) {
        if (this.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builderWithExpectedSize(this.values.size() + other.values.size());
        builder.putAll(this.values);
        builder.putAll(other.values);
        ImmutableSet.Builder builder2 = ImmutableSet.builderWithExpectedSize(this.flags.size() + other.flags.size());
        builder2.addAll(this.flags);
        builder2.addAll(other.flags);
        return new Defines(builder.buildKeepingLast(), (Set<String>)((Object)builder2.build()));
    }

    public String toSource() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : this.values.entrySet()) {
            String string = entry.getKey();
            String string2 = entry.getValue();
            stringBuilder.append("#define ").append(string).append(" ").append(string2).append('\n');
        }
        for (String string3 : this.flags) {
            stringBuilder.append("#define ").append(string3).append('\n');
        }
        return stringBuilder.toString();
    }

    public boolean isEmpty() {
        return this.values.isEmpty() && this.flags.isEmpty();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
        private final ImmutableSet.Builder<String> flags = ImmutableSet.builder();

        Builder() {
        }

        public Builder define(String key, String value) {
            if (value.isBlank()) {
                throw new IllegalArgumentException("Cannot define empty string");
            }
            this.values.put(key, Builder.escapeLinebreak(value));
            return this;
        }

        private static String escapeLinebreak(String string) {
            return string.replaceAll("\n", "\\\\\n");
        }

        public Builder define(String key, float value) {
            this.values.put(key, String.valueOf(value));
            return this;
        }

        public Builder flag(String flag) {
            this.flags.add((Object)flag);
            return this;
        }

        public Defines build() {
            return new Defines(this.values.build(), (Set<String>)((Object)this.flags.build()));
        }
    }
}

