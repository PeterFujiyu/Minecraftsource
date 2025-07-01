/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public abstract class RedstoneController {
    protected final RedstoneWireBlock wire;

    protected RedstoneController(RedstoneWireBlock wire) {
        this.wire = wire;
    }

    public abstract void update(World var1, BlockPos var2, BlockState var3, @Nullable WireOrientation var4, boolean var5);

    protected int getStrongPowerAt(World world, BlockPos pos) {
        return this.wire.getStrongPower(world, pos);
    }

    protected int getWirePowerAt(BlockPos world, BlockState pos) {
        return pos.isOf(this.wire) ? pos.get(RedstoneWireBlock.POWER) : 0;
    }

    protected int calculateWirePowerAt(World world, BlockPos pos) {
        int i = 0;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv5;
            BlockPos lv2 = pos.offset(lv);
            BlockState lv3 = world.getBlockState(lv2);
            i = Math.max(i, this.getWirePowerAt(lv2, lv3));
            BlockPos lv4 = pos.up();
            if (lv3.isSolidBlock(world, lv2) && !world.getBlockState(lv4).isSolidBlock(world, lv4)) {
                lv5 = lv2.up();
                i = Math.max(i, this.getWirePowerAt(lv5, world.getBlockState(lv5)));
                continue;
            }
            if (lv3.isSolidBlock(world, lv2)) continue;
            lv5 = lv2.down();
            i = Math.max(i, this.getWirePowerAt(lv5, world.getBlockState(lv5)));
        }
        return Math.max(0, i - 1);
    }
}

