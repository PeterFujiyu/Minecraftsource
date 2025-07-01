/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.network.packet.s2c.custom.DebugRedstoneUpdateOrderCustomPayload;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneController;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class ExperimentalRedstoneController
extends RedstoneController {
    private final Deque<BlockPos> powerIncreaseQueue = new ArrayDeque<BlockPos>();
    private final Deque<BlockPos> powerDecreaseQueue = new ArrayDeque<BlockPos>();
    private final Object2IntMap<BlockPos> wireOrientationsAndPowers = new Object2IntLinkedOpenHashMap<BlockPos>();

    public ExperimentalRedstoneController(RedstoneWireBlock arg) {
        super(arg);
    }

    @Override
    public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
        WireOrientation lv = ExperimentalRedstoneController.tweakOrientation(world, orientation);
        this.propagatePowerUpdates(world, pos, lv);
        Iterator objectIterator = this.wireOrientationsAndPowers.object2IntEntrySet().iterator();
        boolean bl2 = true;
        while (objectIterator.hasNext()) {
            Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
            BlockPos lv2 = (BlockPos)entry.getKey();
            int i = entry.getIntValue();
            int j = ExperimentalRedstoneController.unpackPower(i);
            BlockState lv3 = world.getBlockState(lv2);
            if (lv3.isOf(this.wire) && !lv3.get(RedstoneWireBlock.POWER).equals(j)) {
                int k = Block.NOTIFY_LISTENERS;
                if (!blockAdded || !bl2) {
                    k |= Block.SKIP_REDSTONE_WIRE_STATE_REPLACEMENT;
                }
                world.setBlockState(lv2, (BlockState)lv3.with(RedstoneWireBlock.POWER, j), k);
            } else {
                objectIterator.remove();
            }
            bl2 = false;
        }
        this.update(world);
    }

    private void update(World world) {
        this.wireOrientationsAndPowers.forEach((pos, orientationAndPower) -> {
            WireOrientation lv = ExperimentalRedstoneController.unpackOrientation(orientationAndPower);
            BlockState lv2 = world.getBlockState((BlockPos)pos);
            for (Direction lv3 : lv.getDirectionsByPriority()) {
                if (!ExperimentalRedstoneController.canProvidePowerTo(lv2, lv3)) continue;
                BlockPos lv4 = pos.offset(lv3);
                BlockState lv5 = world.getBlockState(lv4);
                WireOrientation lv6 = lv.withFrontIfNotUp(lv3);
                world.updateNeighbor(lv5, lv4, this.wire, lv6, false);
                if (!lv5.isSolidBlock(world, lv4)) continue;
                for (Direction lv7 : lv6.getDirectionsByPriority()) {
                    if (lv7 == lv3.getOpposite()) continue;
                    world.updateNeighbor(lv4.offset(lv7), this.wire, lv6.withFrontIfNotUp(lv7));
                }
            }
        });
    }

    private static boolean canProvidePowerTo(BlockState wireState, Direction direction) {
        EnumProperty<WireConnection> lv = RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction);
        if (lv == null) {
            return direction == Direction.DOWN;
        }
        return wireState.get(lv).isConnected();
    }

    private static WireOrientation tweakOrientation(World world, @Nullable WireOrientation orientation) {
        WireOrientation lv = orientation != null ? orientation : WireOrientation.random(world.random);
        return lv.withUp(Direction.UP).withSideBias(WireOrientation.SideBias.LEFT);
    }

    private void propagatePowerUpdates(World world, BlockPos pos, WireOrientation orientation) {
        int l;
        int k;
        int j;
        int i;
        BlockPos lv2;
        BlockState lv = world.getBlockState(pos);
        if (lv.isOf(this.wire)) {
            this.updatePowerAt(pos, lv.get(RedstoneWireBlock.POWER), orientation);
            this.powerIncreaseQueue.add(pos);
        } else {
            this.spreadPowerUpdateToNeighbors(world, pos, 0, orientation, true);
        }
        while (!this.powerIncreaseQueue.isEmpty()) {
            int n;
            lv2 = this.powerIncreaseQueue.removeFirst();
            i = this.wireOrientationsAndPowers.getInt(lv2);
            WireOrientation lv3 = ExperimentalRedstoneController.unpackOrientation(i);
            j = ExperimentalRedstoneController.unpackPower(i);
            k = this.getStrongPowerAt(world, lv2);
            int m = Math.max(k, l = this.calculateWirePowerAt(world, lv2));
            if (m < j) {
                if (k > 0 && !this.powerDecreaseQueue.contains(lv2)) {
                    this.powerDecreaseQueue.add(lv2);
                }
                n = 0;
            } else {
                n = m;
            }
            if (n != j) {
                this.updatePowerAt(lv2, n, lv3);
            }
            this.spreadPowerUpdateToNeighbors(world, lv2, n, lv3, j > m);
        }
        while (!this.powerDecreaseQueue.isEmpty()) {
            lv2 = this.powerDecreaseQueue.removeFirst();
            i = this.wireOrientationsAndPowers.getInt(lv2);
            int o = ExperimentalRedstoneController.unpackPower(i);
            j = this.getStrongPowerAt(world, lv2);
            k = this.calculateWirePowerAt(world, lv2);
            l = Math.max(j, k);
            WireOrientation lv4 = ExperimentalRedstoneController.unpackOrientation(i);
            if (l > o) {
                this.updatePowerAt(lv2, l, lv4);
            } else if (l < o) {
                throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
            }
            this.spreadPowerUpdateToNeighbors(world, lv2, l, lv4, false);
        }
    }

    private static int packOrientationAndPower(WireOrientation orientation, int power) {
        return orientation.ordinal() << 4 | power;
    }

    private static WireOrientation unpackOrientation(int packed) {
        return WireOrientation.fromOrdinal(packed >> 4);
    }

    private static int unpackPower(int packed) {
        return packed & 0xF;
    }

    private void updatePowerAt(BlockPos pos, int power, WireOrientation defaultOrientation) {
        this.wireOrientationsAndPowers.compute(pos, (pos2, orientationAndPower) -> {
            if (orientationAndPower == null) {
                return ExperimentalRedstoneController.packOrientationAndPower(defaultOrientation, power);
            }
            return ExperimentalRedstoneController.packOrientationAndPower(ExperimentalRedstoneController.unpackOrientation(orientationAndPower), power);
        });
    }

    private void spreadPowerUpdateToNeighbors(World world, BlockPos pos, int power, WireOrientation orientation, boolean canIncreasePower) {
        BlockPos lv2;
        for (Direction lv : orientation.getHorizontalDirections()) {
            lv2 = pos.offset(lv);
            this.spreadPowerUpdateTo(world, lv2, power, orientation.withFront(lv), canIncreasePower);
        }
        for (Direction lv : orientation.getVerticalDirections()) {
            lv2 = pos.offset(lv);
            boolean bl2 = world.getBlockState(lv2).isSolidBlock(world, lv2);
            for (Direction lv3 : orientation.getHorizontalDirections()) {
                BlockPos lv5;
                BlockPos lv4 = pos.offset(lv3);
                if (lv == Direction.UP && !bl2) {
                    lv5 = lv2.offset(lv3);
                    this.spreadPowerUpdateTo(world, lv5, power, orientation.withFront(lv3), canIncreasePower);
                    continue;
                }
                if (lv != Direction.DOWN || world.getBlockState(lv4).isSolidBlock(world, lv4)) continue;
                lv5 = lv2.offset(lv3);
                this.spreadPowerUpdateTo(world, lv5, power, orientation.withFront(lv3), canIncreasePower);
            }
        }
    }

    private void spreadPowerUpdateTo(World world, BlockPos neighborPos, int power, WireOrientation orientation, boolean canIncreasePower) {
        BlockState lv = world.getBlockState(neighborPos);
        if (lv.isOf(this.wire)) {
            int j = this.getWirePowerAt(neighborPos, lv);
            if (j < power - 1 && !this.powerDecreaseQueue.contains(neighborPos)) {
                this.powerDecreaseQueue.add(neighborPos);
                this.updatePowerAt(neighborPos, j, orientation);
            }
            if (canIncreasePower && j > power && !this.powerIncreaseQueue.contains(neighborPos)) {
                this.powerIncreaseQueue.add(neighborPos);
                this.updatePowerAt(neighborPos, j, orientation);
            }
        }
    }

    @Override
    protected int getWirePowerAt(BlockPos world, BlockState pos) {
        int i = this.wireOrientationsAndPowers.getOrDefault((Object)world, -1);
        if (i != -1) {
            return ExperimentalRedstoneController.unpackPower(i);
        }
        return super.getWirePowerAt(world, pos);
    }

    private static /* synthetic */ void method_61837(List list, BlockPos arg, Integer integer) {
        WireOrientation lv = ExperimentalRedstoneController.unpackOrientation(integer);
        list.add(new DebugRedstoneUpdateOrderCustomPayload.Wire(arg, lv));
    }
}

