/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;

public class MinecartDispenserBehavior
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
    private final EntityType<? extends AbstractMinecartEntity> minecartEntityType;

    public MinecartDispenserBehavior(EntityType<? extends AbstractMinecartEntity> minecartEntityType) {
        this.minecartEntityType = minecartEntityType;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        double g;
        Direction lv = pointer.state().get(DispenserBlock.FACING);
        ServerWorld lv2 = pointer.world();
        Vec3d lv3 = pointer.centerPos();
        double d = lv3.getX() + (double)lv.getOffsetX() * 1.125;
        double e = Math.floor(lv3.getY()) + (double)lv.getOffsetY();
        double f = lv3.getZ() + (double)lv.getOffsetZ() * 1.125;
        BlockPos lv4 = pointer.pos().offset(lv);
        BlockState lv5 = lv2.getBlockState(lv4);
        if (lv5.isIn(BlockTags.RAILS)) {
            g = MinecartDispenserBehavior.getRailShape(lv5).isAscending() ? 0.6 : 0.1;
        } else {
            if (!lv5.isAir()) return this.fallbackBehavior.dispense(pointer, stack);
            BlockState lv6 = lv2.getBlockState(lv4.down());
            if (!lv6.isIn(BlockTags.RAILS)) return this.fallbackBehavior.dispense(pointer, stack);
            g = lv == Direction.DOWN || !MinecartDispenserBehavior.getRailShape(lv6).isAscending() ? -0.9 : -0.4;
        }
        Vec3d lv7 = new Vec3d(d, e + g, f);
        AbstractMinecartEntity lv8 = AbstractMinecartEntity.create(lv2, lv7.x, lv7.y, lv7.z, this.minecartEntityType, SpawnReason.DISPENSER, stack, null);
        if (lv8 == null) return stack;
        lv2.spawnEntity(lv8);
        stack.decrement(1);
        return stack;
    }

    private static RailShape getRailShape(BlockState state) {
        RailShape railShape;
        Block block = state.getBlock();
        if (block instanceof AbstractRailBlock) {
            AbstractRailBlock lv = (AbstractRailBlock)block;
            railShape = state.get(lv.getShapeProperty());
        } else {
            railShape = RailShape.NORTH_SOUTH;
        }
        return railShape;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
    }
}

