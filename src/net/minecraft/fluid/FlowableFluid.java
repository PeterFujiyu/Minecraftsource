/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.fluid;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.IceBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class FlowableFluid
extends Fluid {
    public static final BooleanProperty FALLING = Properties.FALLING;
    public static final IntProperty LEVEL = Properties.LEVEL_1_8;
    private static final int field_31726 = 200;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<NeighborGroup>> field_15901 = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<NeighborGroup>(200){

            @Override
            protected void rehash(int i) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });
    private final Map<FluidState, VoxelShape> shapeCache = Maps.newIdentityHashMap();

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        builder.add(FALLING);
    }

    @Override
    public Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        double d = 0.0;
        double e = 0.0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            lv.set((Vec3i)pos, lv2);
            FluidState lv3 = world.getFluidState(lv);
            if (!this.isEmptyOrThis(lv3)) continue;
            float f = lv3.getHeight();
            float g = 0.0f;
            if (f == 0.0f) {
                Vec3i lv4;
                FluidState lv5;
                if (!world.getBlockState(lv).blocksMovement() && this.isEmptyOrThis(lv5 = world.getFluidState((BlockPos)(lv4 = lv.down()))) && (f = lv5.getHeight()) > 0.0f) {
                    g = state.getHeight() - (f - 0.8888889f);
                }
            } else if (f > 0.0f) {
                g = state.getHeight() - f;
            }
            if (g == 0.0f) continue;
            d += (double)((float)lv2.getOffsetX() * g);
            e += (double)((float)lv2.getOffsetZ() * g);
        }
        Vec3d lv6 = new Vec3d(d, 0.0, e);
        if (state.get(FALLING).booleanValue()) {
            for (Direction lv7 : Direction.Type.HORIZONTAL) {
                lv.set((Vec3i)pos, lv7);
                if (!this.isFlowBlocked(world, lv, lv7) && !this.isFlowBlocked(world, (BlockPos)lv.up(), lv7)) continue;
                lv6 = lv6.normalize().add(0.0, -6.0, 0.0);
                break;
            }
        }
        return lv6.normalize();
    }

    private boolean isEmptyOrThis(FluidState state) {
        return state.isEmpty() || state.getFluid().matchesType(this);
    }

    protected boolean isFlowBlocked(BlockView world, BlockPos pos, Direction direction) {
        BlockState lv = world.getBlockState(pos);
        FluidState lv2 = world.getFluidState(pos);
        if (lv2.getFluid().matchesType(this)) {
            return false;
        }
        if (direction == Direction.UP) {
            return true;
        }
        if (lv.getBlock() instanceof IceBlock) {
            return false;
        }
        return lv.isSideSolidFullSquare(world, pos, direction);
    }

    protected void tryFlow(ServerWorld world, BlockPos fluidPos, BlockState blockState, FluidState fluidState) {
        FluidState lv4;
        Fluid lv5;
        FluidState lv3;
        BlockState lv2;
        if (fluidState.isEmpty()) {
            return;
        }
        BlockPos lv = fluidPos.down();
        if (this.canFlowThrough(world, fluidPos, blockState, Direction.DOWN, lv, lv2 = world.getBlockState(lv), lv3 = lv2.getFluidState()) && lv3.canBeReplacedWith(world, lv, lv5 = (lv4 = this.getUpdatedState(world, lv, lv2)).getFluid(), Direction.DOWN) && FlowableFluid.canFillWithFluid(world, lv, lv2, lv5)) {
            this.flow(world, lv, lv2, Direction.DOWN, lv4);
            if (this.countNeighboringSources(world, fluidPos) >= 3) {
                this.flowToSides(world, fluidPos, fluidState, blockState);
            }
            return;
        }
        if (fluidState.isStill() || !this.canFlowDownTo(world, fluidPos, blockState, lv, lv2)) {
            this.flowToSides(world, fluidPos, fluidState, blockState);
        }
    }

    private void flowToSides(ServerWorld world, BlockPos pos, FluidState fluidState, BlockState blockState) {
        int i = fluidState.getLevel() - this.getLevelDecreasePerBlock(world);
        if (fluidState.get(FALLING).booleanValue()) {
            i = 7;
        }
        if (i <= 0) {
            return;
        }
        Map<Direction, FluidState> map = this.getSpread(world, pos, blockState);
        for (Map.Entry<Direction, FluidState> entry : map.entrySet()) {
            Direction lv = entry.getKey();
            FluidState lv2 = entry.getValue();
            BlockPos lv3 = pos.offset(lv);
            this.flow(world, lv3, world.getBlockState(lv3), lv, lv2);
        }
    }

    protected FluidState getUpdatedState(ServerWorld world, BlockPos pos, BlockState state) {
        BlockPos.Mutable lv8;
        BlockState lv9;
        FluidState lv10;
        int i = 0;
        int j = 0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            BlockPos.Mutable lv3 = lv.set((Vec3i)pos, lv2);
            BlockState lv4 = world.getBlockState(lv3);
            FluidState lv5 = lv4.getFluidState();
            if (!lv5.getFluid().matchesType(this) || !FlowableFluid.receivesFlow(lv2, world, pos, state, lv3, lv4)) continue;
            if (lv5.isStill()) {
                ++j;
            }
            i = Math.max(i, lv5.getLevel());
        }
        if (j >= 2 && this.isInfinite(world)) {
            BlockState lv6 = world.getBlockState(lv.set((Vec3i)pos, Direction.DOWN));
            FluidState lv7 = lv6.getFluidState();
            if (lv6.isSolid() || this.isMatchingAndStill(lv7)) {
                return this.getStill(false);
            }
        }
        if (!(lv10 = (lv9 = world.getBlockState(lv8 = lv.set((Vec3i)pos, Direction.UP))).getFluidState()).isEmpty() && lv10.getFluid().matchesType(this) && FlowableFluid.receivesFlow(Direction.UP, world, pos, state, lv8, lv9)) {
            return this.getFlowing(8, true);
        }
        int k = i - this.getLevelDecreasePerBlock(world);
        if (k <= 0) {
            return Fluids.EMPTY.getDefaultState();
        }
        return this.getFlowing(k, false);
    }

    private static boolean receivesFlow(Direction face, BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
        boolean bl;
        NeighborGroup lv3;
        VoxelShape lv = fromState.getCollisionShape(world, fromPos);
        if (lv == VoxelShapes.fullCube()) {
            return false;
        }
        VoxelShape lv2 = state.getCollisionShape(world, pos);
        if (lv2 == VoxelShapes.fullCube()) {
            return false;
        }
        if (lv2 == VoxelShapes.empty() && lv == VoxelShapes.empty()) {
            return true;
        }
        Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = state.getBlock().hasDynamicBounds() || fromState.getBlock().hasDynamicBounds() ? null : field_15901.get();
        if (object2ByteLinkedOpenHashMap != null) {
            lv3 = new NeighborGroup(state, fromState, face);
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(lv3);
            if (b != 127) {
                return b != 0;
            }
        } else {
            lv3 = null;
        }
        boolean bl2 = bl = !VoxelShapes.adjacentSidesCoverSquare(lv2, lv, face);
        if (object2ByteLinkedOpenHashMap != null) {
            if (object2ByteLinkedOpenHashMap.size() == 200) {
                object2ByteLinkedOpenHashMap.removeLastByte();
            }
            object2ByteLinkedOpenHashMap.putAndMoveToFirst(lv3, (byte)(bl ? 1 : 0));
        }
        return bl;
    }

    public abstract Fluid getFlowing();

    public FluidState getFlowing(int level, boolean falling) {
        return (FluidState)((FluidState)this.getFlowing().getDefaultState().with(LEVEL, level)).with(FALLING, falling);
    }

    public abstract Fluid getStill();

    public FluidState getStill(boolean falling) {
        return (FluidState)this.getStill().getDefaultState().with(FALLING, falling);
    }

    protected abstract boolean isInfinite(ServerWorld var1);

    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        Block block = state.getBlock();
        if (block instanceof FluidFillable) {
            FluidFillable lv = (FluidFillable)((Object)block);
            lv.tryFillWithFluid(world, pos, state, fluidState);
        } else {
            if (!state.isAir()) {
                this.beforeBreakingBlock(world, pos, state);
            }
            world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL);
        }
    }

    protected abstract void beforeBreakingBlock(WorldAccess var1, BlockPos var2, BlockState var3);

    protected int getMinFlowDownDistance(WorldView world, BlockPos pos, int i, Direction direction, BlockState state, SpreadCache spreadCache) {
        int j = 1000;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            int k;
            if (lv == direction) continue;
            BlockPos lv2 = pos.offset(lv);
            BlockState lv3 = spreadCache.getBlockState(lv2);
            FluidState lv4 = lv3.getFluidState();
            if (!this.canFlowThrough(world, this.getFlowing(), pos, state, lv, lv2, lv3, lv4)) continue;
            if (spreadCache.canFlowDownTo(lv2)) {
                return i;
            }
            if (i >= this.getMaxFlowDistance(world) || (k = this.getMinFlowDownDistance(world, lv2, i + 1, lv.getOpposite(), lv3, spreadCache)) >= j) continue;
            j = k;
        }
        return j;
    }

    boolean canFlowDownTo(BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
        if (!FlowableFluid.receivesFlow(Direction.DOWN, world, pos, state, fromPos, fromState)) {
            return false;
        }
        if (fromState.getFluidState().getFluid().matchesType(this)) {
            return true;
        }
        return FlowableFluid.canFill(world, fromPos, fromState, this.getFlowing());
    }

    private boolean canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState) {
        return this.canFlowThrough(world, pos, state, face, fromPos, fromState, fluidState) && FlowableFluid.canFillWithFluid(world, fromPos, fromState, fluid);
    }

    private boolean canFlowThrough(BlockView world, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState) {
        return !this.isMatchingAndStill(fluidState) && FlowableFluid.canFill(fromState) && FlowableFluid.receivesFlow(face, world, pos, state, fromPos, fromState);
    }

    private boolean isMatchingAndStill(FluidState state) {
        return state.getFluid().matchesType(this) && state.isStill();
    }

    protected abstract int getMaxFlowDistance(WorldView var1);

    private int countNeighboringSources(WorldView world, BlockPos pos) {
        int i = 0;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv2 = pos.offset(lv);
            FluidState lv3 = world.getFluidState(lv2);
            if (!this.isMatchingAndStill(lv3)) continue;
            ++i;
        }
        return i;
    }

    protected Map<Direction, FluidState> getSpread(ServerWorld world, BlockPos pos, BlockState state) {
        int i = 1000;
        EnumMap<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
        SpreadCache lv = null;
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            int j;
            FluidState lv6;
            FluidState lv5;
            BlockState lv4;
            BlockPos lv3;
            if (!this.canFlowThrough(world, pos, state, lv2, lv3 = pos.offset(lv2), lv4 = world.getBlockState(lv3), lv5 = lv4.getFluidState()) || !FlowableFluid.canFillWithFluid(world, lv3, lv4, (lv6 = this.getUpdatedState(world, lv3, lv4)).getFluid())) continue;
            if (lv == null) {
                lv = new SpreadCache(world, pos);
            }
            if ((j = lv.canFlowDownTo(lv3) ? 0 : this.getMinFlowDownDistance(world, lv3, 1, lv2.getOpposite(), lv4, lv)) < i) {
                map.clear();
            }
            if (j > i) continue;
            if (lv5.canBeReplacedWith(world, lv3, lv6.getFluid(), lv2)) {
                map.put(lv2, lv6);
            }
            i = j;
        }
        return map;
    }

    private static boolean canFill(BlockState state) {
        Block lv = state.getBlock();
        if (lv instanceof FluidFillable) {
            return true;
        }
        if (state.blocksMovement()) {
            return false;
        }
        return !(lv instanceof DoorBlock) && !state.isIn(BlockTags.SIGNS) && !state.isOf(Blocks.LADDER) && !state.isOf(Blocks.SUGAR_CANE) && !state.isOf(Blocks.BUBBLE_COLUMN) && !state.isOf(Blocks.NETHER_PORTAL) && !state.isOf(Blocks.END_PORTAL) && !state.isOf(Blocks.END_GATEWAY) && !state.isOf(Blocks.STRUCTURE_VOID);
    }

    private static boolean canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return FlowableFluid.canFill(state) && FlowableFluid.canFillWithFluid(world, pos, state, fluid);
    }

    private static boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        Block lv = state.getBlock();
        if (lv instanceof FluidFillable) {
            FluidFillable lv2 = (FluidFillable)((Object)lv);
            return lv2.canFillWithFluid(null, world, pos, state, fluid);
        }
        return true;
    }

    protected abstract int getLevelDecreasePerBlock(WorldView var1);

    protected int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
        return this.getTickRate(world);
    }

    @Override
    public void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (!fluidState.isStill()) {
            FluidState lv = this.getUpdatedState(world, pos, world.getBlockState(pos));
            int i = this.getNextTickDelay(world, pos, fluidState, lv);
            if (lv.isEmpty()) {
                fluidState = lv;
                blockState = Blocks.AIR.getDefaultState();
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
            } else if (!lv.equals(fluidState)) {
                fluidState = lv;
                blockState = fluidState.getBlockState();
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
                world.scheduleFluidTick(pos, fluidState.getFluid(), i);
            }
        }
        this.tryFlow(world, pos, blockState, fluidState);
    }

    protected static int getBlockStateLevel(FluidState state) {
        if (state.isStill()) {
            return 0;
        }
        return 8 - Math.min(state.getLevel(), 8) + (state.get(FALLING) != false ? 8 : 0);
    }

    private static boolean isFluidAboveEqual(FluidState state, BlockView world, BlockPos pos) {
        return state.getFluid().matchesType(world.getFluidState(pos.up()).getFluid());
    }

    @Override
    public float getHeight(FluidState state, BlockView world, BlockPos pos) {
        if (FlowableFluid.isFluidAboveEqual(state, world, pos)) {
            return 1.0f;
        }
        return state.getHeight();
    }

    @Override
    public float getHeight(FluidState state) {
        return (float)state.getLevel() / 9.0f;
    }

    @Override
    public abstract int getLevel(FluidState var1);

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        if (state.getLevel() == 9 && FlowableFluid.isFluidAboveEqual(state, world, pos)) {
            return VoxelShapes.fullCube();
        }
        return this.shapeCache.computeIfAbsent(state, state2 -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, state2.getHeight(world, pos), 1.0));
    }

    record NeighborGroup(BlockState self, BlockState other, Direction facing) {
        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NeighborGroup)) return false;
            NeighborGroup lv = (NeighborGroup)o;
            if (this.self != lv.self) return false;
            if (this.other != lv.other) return false;
            if (this.facing != lv.facing) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int i = System.identityHashCode(this.self);
            i = 31 * i + System.identityHashCode(this.other);
            i = 31 * i + this.facing.hashCode();
            return i;
        }
    }

    protected class SpreadCache {
        private final BlockView world;
        private final BlockPos startPos;
        private final Short2ObjectMap<BlockState> stateCache = new Short2ObjectOpenHashMap<BlockState>();
        private final Short2BooleanMap flowDownCache = new Short2BooleanOpenHashMap();

        SpreadCache(BlockView world, BlockPos startPos) {
            this.world = world;
            this.startPos = startPos;
        }

        public BlockState getBlockState(BlockPos pos) {
            return this.getBlockState(pos, this.pack(pos));
        }

        private BlockState getBlockState(BlockPos pos, short packed) {
            return this.stateCache.computeIfAbsent(packed, packedPos -> this.world.getBlockState(pos));
        }

        public boolean canFlowDownTo(BlockPos pos) {
            return this.flowDownCache.computeIfAbsent(this.pack(pos), packed -> {
                BlockState lv = this.getBlockState(pos, packed);
                BlockPos lv2 = pos.down();
                BlockState lv3 = this.world.getBlockState(lv2);
                return FlowableFluid.this.canFlowDownTo(this.world, pos, lv, lv2, lv3);
            });
        }

        private short pack(BlockPos pos) {
            int i = pos.getX() - this.startPos.getX();
            int j = pos.getZ() - this.startPos.getZ();
            return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
        }
    }
}

