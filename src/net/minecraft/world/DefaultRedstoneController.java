/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneController;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class DefaultRedstoneController
extends RedstoneController {
    public DefaultRedstoneController(RedstoneWireBlock arg) {
        super(arg);
    }

    @Override
    public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
        int i = this.calculateTotalPowerAt(world, pos);
        if (state.get(RedstoneWireBlock.POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, (BlockState)state.with(RedstoneWireBlock.POWER, i), Block.NOTIFY_LISTENERS);
            }
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            for (Direction lv : Direction.values()) {
                set.add(pos.offset(lv));
            }
            for (BlockPos lv2 : set) {
                world.updateNeighborsAlways(lv2, this.wire);
            }
        }
    }

    private int calculateTotalPowerAt(World world, BlockPos pos) {
        int i = this.getStrongPowerAt(world, pos);
        if (i == 15) {
            return i;
        }
        return Math.max(i, this.calculateWirePowerAt(world, pos));
    }
}

