/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class ExperimentalMinecartShapeContext
extends EntityShapeContext {
    @Nullable
    private BlockPos belowPos;
    @Nullable
    private BlockPos ascendingPos;

    protected ExperimentalMinecartShapeContext(AbstractMinecartEntity minecart, boolean collidesWithFluid) {
        super(minecart, collidesWithFluid);
        this.setIgnoredPositions(minecart);
    }

    private void setIgnoredPositions(AbstractMinecartEntity minecart) {
        BlockPos lv = minecart.getRailOrMinecartPos();
        BlockState lv2 = minecart.getWorld().getBlockState(lv);
        boolean bl = AbstractRailBlock.isRail(lv2);
        if (bl) {
            this.belowPos = lv.down();
            RailShape lv3 = lv2.get(((AbstractRailBlock)lv2.getBlock()).getShapeProperty());
            if (lv3.isAscending()) {
                this.ascendingPos = switch (lv3) {
                    case RailShape.ASCENDING_EAST -> lv.east();
                    case RailShape.ASCENDING_WEST -> lv.west();
                    case RailShape.ASCENDING_NORTH -> lv.north();
                    case RailShape.ASCENDING_SOUTH -> lv.south();
                    default -> null;
                };
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
        if (pos.equals(this.belowPos) || pos.equals(this.ascendingPos)) {
            return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, world, pos);
    }
}

