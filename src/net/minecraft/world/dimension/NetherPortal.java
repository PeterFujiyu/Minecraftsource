/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.dimension;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class NetherPortal {
    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;
    private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (state, world, pos) -> state.isOf(Blocks.OBSIDIAN);
    private static final float FALLBACK_THRESHOLD = 4.0f;
    private static final double HEIGHT_STRETCH = 1.0;
    private final Direction.Axis axis;
    private final Direction negativeDir;
    private final int foundPortalBlocks;
    private final BlockPos lowerCorner;
    private final int height;
    private final int width;

    private NetherPortal(Direction.Axis axis, int foundPortalBlocks, Direction negativeDir, BlockPos lowerCorner, int width, int height) {
        this.axis = axis;
        this.foundPortalBlocks = foundPortalBlocks;
        this.negativeDir = negativeDir;
        this.lowerCorner = lowerCorner;
        this.width = width;
        this.height = height;
    }

    public static Optional<NetherPortal> getNewPortal(WorldAccess world, BlockPos pos, Direction.Axis firstCheckedAxis) {
        return NetherPortal.getOrEmpty(world, pos, areaHelper -> areaHelper.isValid() && areaHelper.foundPortalBlocks == 0, firstCheckedAxis);
    }

    public static Optional<NetherPortal> getOrEmpty(WorldAccess world, BlockPos pos, Predicate<NetherPortal> validator, Direction.Axis firstCheckedAxis) {
        Optional<NetherPortal> optional = Optional.of(NetherPortal.getOnAxis(world, pos, firstCheckedAxis)).filter(validator);
        if (optional.isPresent()) {
            return optional;
        }
        Direction.Axis lv = firstCheckedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        return Optional.of(NetherPortal.getOnAxis(world, pos, lv)).filter(validator);
    }

    public static NetherPortal getOnAxis(BlockView world, BlockPos pos, Direction.Axis axis) {
        Direction lv = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        BlockPos lv2 = NetherPortal.getLowerCorner(world, lv, pos);
        if (lv2 == null) {
            return new NetherPortal(axis, 0, lv, pos, 0, 0);
        }
        int i = NetherPortal.getValidatedWidth(world, lv2, lv);
        if (i == 0) {
            return new NetherPortal(axis, 0, lv, lv2, 0, 0);
        }
        MutableInt mutableInt = new MutableInt();
        int j = NetherPortal.getHeight(world, lv2, lv, i, mutableInt);
        return new NetherPortal(axis, mutableInt.getValue(), lv, lv2, i, j);
    }

    @Nullable
    private static BlockPos getLowerCorner(BlockView world, Direction direction, BlockPos pow) {
        int i = Math.max(world.getBottomY(), pow.getY() - 21);
        while (pow.getY() > i && NetherPortal.validStateInsidePortal(world.getBlockState(pow.down()))) {
            pow = pow.down();
        }
        Direction lv = direction.getOpposite();
        int j = NetherPortal.getWidth(world, pow, lv) - 1;
        if (j < 0) {
            return null;
        }
        return pow.offset(lv, j);
    }

    private static int getValidatedWidth(BlockView world, BlockPos lowerCorner, Direction negativeDir) {
        int i = NetherPortal.getWidth(world, lowerCorner, negativeDir);
        if (i < 2 || i > 21) {
            return 0;
        }
        return i;
    }

    private static int getWidth(BlockView world, BlockPos lowerCorner, Direction negativeDir) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i <= 21; ++i) {
            lv.set(lowerCorner).move(negativeDir, i);
            BlockState lv2 = world.getBlockState(lv);
            if (!NetherPortal.validStateInsidePortal(lv2)) {
                if (!IS_VALID_FRAME_BLOCK.test(lv2, world, lv)) break;
                return i;
            }
            BlockState lv3 = world.getBlockState(lv.move(Direction.DOWN));
            if (!IS_VALID_FRAME_BLOCK.test(lv3, world, lv)) break;
        }
        return 0;
    }

    private static int getHeight(BlockView world, BlockPos lowerCorner, Direction negativeDir, int width, MutableInt foundPortalBlocks) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int j = NetherPortal.getPotentialHeight(world, lowerCorner, negativeDir, lv, width, foundPortalBlocks);
        if (j < 3 || j > 21 || !NetherPortal.isHorizontalFrameValid(world, lowerCorner, negativeDir, lv, width, j)) {
            return 0;
        }
        return j;
    }

    private static boolean isHorizontalFrameValid(BlockView world, BlockPos lowerCorner, Direction direction, BlockPos.Mutable pos, int width, int height) {
        for (int k = 0; k < width; ++k) {
            BlockPos.Mutable lv = pos.set(lowerCorner).move(Direction.UP, height).move(direction, k);
            if (IS_VALID_FRAME_BLOCK.test(world.getBlockState(lv), world, lv)) continue;
            return false;
        }
        return true;
    }

    private static int getPotentialHeight(BlockView world, BlockPos lowerCorner, Direction negativeDir, BlockPos.Mutable pos, int width, MutableInt foundPortalBlocks) {
        for (int j = 0; j < 21; ++j) {
            pos.set(lowerCorner).move(Direction.UP, j).move(negativeDir, -1);
            if (!IS_VALID_FRAME_BLOCK.test(world.getBlockState(pos), world, pos)) {
                return j;
            }
            pos.set(lowerCorner).move(Direction.UP, j).move(negativeDir, width);
            if (!IS_VALID_FRAME_BLOCK.test(world.getBlockState(pos), world, pos)) {
                return j;
            }
            for (int k = 0; k < width; ++k) {
                pos.set(lowerCorner).move(Direction.UP, j).move(negativeDir, k);
                BlockState lv = world.getBlockState(pos);
                if (!NetherPortal.validStateInsidePortal(lv)) {
                    return j;
                }
                if (!lv.isOf(Blocks.NETHER_PORTAL)) continue;
                foundPortalBlocks.increment();
            }
        }
        return 21;
    }

    private static boolean validStateInsidePortal(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.NETHER_PORTAL);
    }

    public boolean isValid() {
        return this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortal(WorldAccess world) {
        BlockState lv = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
        BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1)).forEach(pos -> world.setBlockState((BlockPos)pos, lv, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));
    }

    public boolean wasAlreadyValid() {
        return this.isValid() && this.foundPortalBlocks == this.width * this.height;
    }

    public static Vec3d entityPosInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d entityPos, EntityDimensions entityDimensions) {
        Direction.Axis lv2;
        double g;
        double f;
        double d = (double)portalRect.width - (double)entityDimensions.width();
        double e = (double)portalRect.height - (double)entityDimensions.height();
        BlockPos lv = portalRect.lowerLeft;
        if (d > 0.0) {
            f = (double)lv.getComponentAlongAxis(portalAxis) + (double)entityDimensions.width() / 2.0;
            g = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(portalAxis) - f, 0.0, d), 0.0, 1.0);
        } else {
            g = 0.5;
        }
        if (e > 0.0) {
            lv2 = Direction.Axis.Y;
            f = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(lv2) - (double)lv.getComponentAlongAxis(lv2), 0.0, e), 0.0, 1.0);
        } else {
            f = 0.0;
        }
        lv2 = portalAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double h = entityPos.getComponentAlongAxis(lv2) - ((double)lv.getComponentAlongAxis(lv2) + 0.5);
        return new Vec3d(g, f, h);
    }

    public static Vec3d findOpenPosition(Vec3d fallback, ServerWorld world, Entity entity, EntityDimensions dimensions) {
        if (dimensions.width() > 4.0f || dimensions.height() > 4.0f) {
            return fallback;
        }
        double d = (double)dimensions.height() / 2.0;
        Vec3d lv = fallback.add(0.0, d, 0.0);
        VoxelShape lv2 = VoxelShapes.cuboid(Box.of(lv, dimensions.width(), 0.0, dimensions.width()).stretch(0.0, 1.0, 0.0).expand(1.0E-6));
        Optional<Vec3d> optional = world.findClosestCollision(entity, lv2, lv, dimensions.width(), dimensions.height(), dimensions.width());
        Optional<Vec3d> optional2 = optional.map(pos -> pos.subtract(0.0, d, 0.0));
        return optional2.orElse(fallback);
    }
}

