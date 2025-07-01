/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;

public class BoatDispenserBehavior
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
    private final EntityType<? extends AbstractBoatEntity> boatType;

    public BoatDispenserBehavior(EntityType<? extends AbstractBoatEntity> boatType) {
        this.boatType = boatType;
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        double h;
        Direction lv = pointer.state().get(DispenserBlock.FACING);
        ServerWorld lv2 = pointer.world();
        Vec3d lv3 = pointer.centerPos();
        double d = 0.5625 + (double)this.boatType.getWidth() / 2.0;
        double e = lv3.getX() + (double)lv.getOffsetX() * d;
        double f = lv3.getY() + (double)((float)lv.getOffsetY() * 1.125f);
        double g = lv3.getZ() + (double)lv.getOffsetZ() * d;
        BlockPos lv4 = pointer.pos().offset(lv);
        if (lv2.getFluidState(lv4).isIn(FluidTags.WATER)) {
            h = 1.0;
        } else if (lv2.getBlockState(lv4).isAir() && lv2.getFluidState(lv4.down()).isIn(FluidTags.WATER)) {
            h = 0.0;
        } else {
            return this.fallbackBehavior.dispense(pointer, stack);
        }
        AbstractBoatEntity lv5 = this.boatType.create(lv2, SpawnReason.DISPENSER);
        if (lv5 != null) {
            lv5.initPosition(e, f + h, g);
            EntityType.copier(lv2, stack, null).accept(lv5);
            lv5.setYaw(lv.getPositiveHorizontalDegrees());
            lv2.spawnEntity(lv5);
            stack.decrement(1);
        }
        return stack;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
    }
}

