/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record EquipmentModel(Map<LayerType, List<Layer>> layers) {
    private static final Codec<List<Layer>> LAYER_LIST_CODEC = Codecs.nonEmptyList(Layer.CODEC.listOf());
    public static final Codec<EquipmentModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.nonEmptyMap(Codec.unboundedMap(LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers")).forGetter(EquipmentModel::layers)).apply((Applicative<EquipmentModel, ?>)instance, EquipmentModel::new));

    public static Builder builder() {
        return new Builder();
    }

    public List<Layer> getLayers(LayerType layerType) {
        return this.layers.getOrDefault(layerType, List.of());
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Map<LayerType, List<Layer>> layers = new EnumMap<LayerType, List<Layer>>(LayerType.class);

        Builder() {
        }

        public Builder addHumanoidLayers(Identifier textureId) {
            return this.addHumanoidLayers(textureId, false);
        }

        public Builder addHumanoidLayers(Identifier textureId, boolean dyeable) {
            this.addLayers(LayerType.HUMANOID_LEGGINGS, Layer.createWithLeatherColor(textureId, dyeable));
            this.addMainHumanoidLayer(textureId, dyeable);
            return this;
        }

        public Builder addMainHumanoidLayer(Identifier textureId, boolean dyeable) {
            return this.addLayers(LayerType.HUMANOID, Layer.createWithLeatherColor(textureId, dyeable));
        }

        public Builder addLayers(LayerType layerType, Layer ... layers) {
            Collections.addAll(this.layers.computeIfAbsent(layerType, arg -> new ArrayList()), layers);
            return this;
        }

        public EquipmentModel build() {
            return new EquipmentModel((Map<LayerType, List<Layer>>)this.layers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> List.copyOf((Collection)entry.getValue()))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum LayerType implements StringIdentifiable
    {
        HUMANOID("humanoid"),
        HUMANOID_LEGGINGS("humanoid_leggings"),
        WINGS("wings"),
        WOLF_BODY("wolf_body"),
        HORSE_BODY("horse_body"),
        LLAMA_BODY("llama_body");

        public static final Codec<LayerType> CODEC;
        private final String name;

        private LayerType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(LayerType::values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Layer(Identifier textureId, Optional<Dyeable> dyeable, boolean usePlayerTexture) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("texture")).forGetter(Layer::textureId), Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(Layer::dyeable), Codec.BOOL.optionalFieldOf("use_player_texture", false).forGetter(Layer::usePlayerTexture)).apply((Applicative<Layer, ?>)instance, Layer::new));

        public Layer(Identifier textureId) {
            this(textureId, Optional.empty(), false);
        }

        public static Layer createWithLeatherColor(Identifier textureId, boolean dyeable) {
            return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
        }

        public static Layer create(Identifier textureId, boolean dyeable) {
            return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.empty())) : Optional.empty(), false);
        }

        public Identifier getFullTextureId(LayerType layerType) {
            return this.textureId.withPath(textureName -> "textures/entity/equipment/" + layerType.asString() + "/" + textureName + ".png");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Dyeable(Optional<Integer> colorWhenUndyed) {
        public static final Codec<Dyeable> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codecs.RGB.optionalFieldOf("color_when_undyed").forGetter(Dyeable::colorWhenUndyed)).apply((Applicative<Dyeable, ?>)instance, Dyeable::new));
    }
}

