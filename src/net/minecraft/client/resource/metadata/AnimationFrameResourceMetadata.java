/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record AnimationFrameResourceMetadata(int index, Optional<Integer> time) {
    public static final Codec<AnimationFrameResourceMetadata> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.NON_NEGATIVE_INT.fieldOf("index")).forGetter(AnimationFrameResourceMetadata::index), Codecs.POSITIVE_INT.optionalFieldOf("time").forGetter(AnimationFrameResourceMetadata::time)).apply((Applicative<AnimationFrameResourceMetadata, ?>)instance, AnimationFrameResourceMetadata::new));
    public static final Codec<AnimationFrameResourceMetadata> CODEC = Codec.either(Codecs.NON_NEGATIVE_INT, BASE_CODEC).xmap(either -> either.map(AnimationFrameResourceMetadata::new, metadata -> metadata), metadatax -> metadatax.time.isPresent() ? Either.right(metadatax) : Either.left(metadatax.index));

    public AnimationFrameResourceMetadata(int index) {
        this(index, Optional.empty());
    }

    public int getTime(int defaultTime) {
        return this.time.orElse(defaultTime);
    }
}

