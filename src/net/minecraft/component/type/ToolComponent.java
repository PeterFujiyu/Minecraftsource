/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public record ToolComponent(List<Rule> rules, float defaultMiningSpeed, int damagePerBlock) {
    public static final Codec<ToolComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Rule.CODEC.listOf().fieldOf("rules")).forGetter(ToolComponent::rules), Codec.FLOAT.optionalFieldOf("default_mining_speed", Float.valueOf(1.0f)).forGetter(ToolComponent::defaultMiningSpeed), Codecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_block", 1).forGetter(ToolComponent::damagePerBlock)).apply((Applicative<ToolComponent, ?>)instance, ToolComponent::new));
    public static final PacketCodec<RegistryByteBuf, ToolComponent> PACKET_CODEC = PacketCodec.tuple(Rule.PACKET_CODEC.collect(PacketCodecs.toList()), ToolComponent::rules, PacketCodecs.FLOAT, ToolComponent::defaultMiningSpeed, PacketCodecs.VAR_INT, ToolComponent::damagePerBlock, ToolComponent::new);

    public float getSpeed(BlockState state) {
        for (Rule lv : this.rules) {
            if (!lv.speed.isPresent() || !state.isIn(lv.blocks)) continue;
            return lv.speed.get().floatValue();
        }
        return this.defaultMiningSpeed;
    }

    public boolean isCorrectForDrops(BlockState state) {
        for (Rule lv : this.rules) {
            if (!lv.correctForDrops.isPresent() || !state.isIn(lv.blocks)) continue;
            return lv.correctForDrops.get();
        }
        return false;
    }

    public record Rule(RegistryEntryList<Block> blocks, Optional<Float> speed, Optional<Boolean> correctForDrops) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("blocks")).forGetter(Rule::blocks), Codecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Rule::speed), Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Rule::correctForDrops)).apply((Applicative<Rule, ?>)instance, Rule::new));
        public static final PacketCodec<RegistryByteBuf, Rule> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntryList(RegistryKeys.BLOCK), Rule::blocks, PacketCodecs.FLOAT.collect(PacketCodecs::optional), Rule::speed, PacketCodecs.BOOLEAN.collect(PacketCodecs::optional), Rule::correctForDrops, Rule::new);

        public static Rule ofAlwaysDropping(RegistryEntryList<Block> blocks, float speed) {
            return new Rule(blocks, Optional.of(Float.valueOf(speed)), Optional.of(true));
        }

        public static Rule ofNeverDropping(RegistryEntryList<Block> blocks) {
            return new Rule(blocks, Optional.empty(), Optional.of(false));
        }

        public static Rule of(RegistryEntryList<Block> blocks, float speed) {
            return new Rule(blocks, Optional.of(Float.valueOf(speed)), Optional.empty());
        }
    }
}

