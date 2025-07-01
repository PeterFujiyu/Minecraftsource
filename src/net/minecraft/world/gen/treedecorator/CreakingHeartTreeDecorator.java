/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class CreakingHeartTreeDecorator
extends TreeDecorator {
    public static final MapCodec<CreakingHeartTreeDecorator> CODEC = ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).xmap(CreakingHeartTreeDecorator::new, treeDecorator -> Float.valueOf(treeDecorator.probability));
    private final float probability;

    public CreakingHeartTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.CREAKING_HEART;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random lv = generator.getRandom();
        ObjectArrayList<BlockPos> list = generator.getLogPositions();
        if (list.isEmpty()) {
            return;
        }
        if (lv.nextFloat() >= this.probability) {
            return;
        }
        ArrayList<BlockPos> list2 = new ArrayList<BlockPos>(list);
        Util.shuffle(list2, lv);
        Optional<BlockPos> optional = list2.stream().filter(pos -> {
            for (Direction lv : Direction.values()) {
                if (generator.matches(pos.offset(lv), state -> state.isIn(BlockTags.LOGS))) continue;
                return false;
            }
            return true;
        }).findFirst();
        if (optional.isEmpty()) {
            return;
        }
        generator.replace(optional.get(), (BlockState)((BlockState)Blocks.CREAKING_HEART.getDefaultState().with(CreakingHeartBlock.ACTIVE, true)).with(CreakingHeartBlock.NATURAL, true));
    }
}

