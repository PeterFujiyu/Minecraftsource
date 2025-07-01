/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.apache.commons.lang3.mutable.MutableObject;

public class PaleMossTreeDecorator
extends TreeDecorator {
    public static final MapCodec<PaleMossTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("leaves_probability")).forGetter(treeDecorator -> Float.valueOf(treeDecorator.leavesProbability)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("trunk_probability")).forGetter(treeDecorator -> Float.valueOf(treeDecorator.trunkProbability)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("ground_probability")).forGetter(treeDecorator -> Float.valueOf(treeDecorator.groundProbability))).apply((Applicative<PaleMossTreeDecorator, ?>)instance, PaleMossTreeDecorator::new));
    private final float leavesProbability;
    private final float trunkProbability;
    private final float groundProbability;

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.PALE_MOSS;
    }

    public PaleMossTreeDecorator(float leavesProbability, float trunkProbability, float groundProbability) {
        this.leavesProbability = leavesProbability;
        this.trunkProbability = trunkProbability;
        this.groundProbability = groundProbability;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random lv = generator.getRandom();
        StructureWorldAccess lv2 = (StructureWorldAccess)generator.getWorld();
        List<BlockPos> list = Util.copyShuffled(generator.getLogPositions(), lv);
        if (list.isEmpty()) {
            return;
        }
        MutableObject<BlockPos> mutable = new MutableObject<BlockPos>(list.getFirst());
        list.forEach(pos -> {
            if (pos.getY() < ((BlockPos)mutable.getValue()).getY()) {
                mutable.setValue((BlockPos)pos);
            }
        });
        BlockPos lv3 = (BlockPos)mutable.getValue();
        if (lv.nextFloat() < this.groundProbability) {
            lv2.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap(registry -> registry.getOptional(VegetationConfiguredFeatures.PALE_MOSS_PATCH)).ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate(lv2, lv2.toServerWorld().getChunkManager().getChunkGenerator(), lv, lv3.up()));
        }
        generator.getLogPositions().forEach(pos -> {
            BlockPos lv;
            if (lv.nextFloat() < this.trunkProbability && generator.isAir(lv = pos.down())) {
                PaleMossTreeDecorator.decorate(lv, generator);
            }
        });
        generator.getLeavesPositions().forEach(pos -> {
            BlockPos lv;
            if (lv.nextFloat() < this.leavesProbability && generator.isAir(lv = pos.down())) {
                PaleMossTreeDecorator.decorate(lv, generator);
            }
        });
    }

    private static void decorate(BlockPos pos, TreeDecorator.Generator generator) {
        while (generator.isAir(pos.down()) && !((double)generator.getRandom().nextFloat() < 0.5)) {
            generator.replace(pos, (BlockState)Blocks.PALE_HANGING_MOSS.getDefaultState().with(HangingMossBlock.TIP, false));
            pos = pos.down();
        }
        generator.replace(pos, (BlockState)Blocks.PALE_HANGING_MOSS.getDefaultState().with(HangingMossBlock.TIP, true));
    }
}

