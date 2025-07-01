/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public record VillagerResourceMetadata(HatType hatType) {
    public static final Codec<VillagerResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(HatType.CODEC.optionalFieldOf("hat", HatType.NONE).forGetter(VillagerResourceMetadata::hatType)).apply((Applicative<VillagerResourceMetadata, ?>)instance, VillagerResourceMetadata::new));
    public static final ResourceMetadataSerializer<VillagerResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<VillagerResourceMetadata>("villager", CODEC);

    @Environment(value=EnvType.CLIENT)
    public static enum HatType implements StringIdentifiable
    {
        NONE("none"),
        PARTIAL("partial"),
        FULL("full");

        public static final Codec<HatType> CODEC;
        private final String name;

        private HatType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(HatType::values);
        }
    }
}

