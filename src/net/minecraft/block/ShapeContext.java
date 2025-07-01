/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ExperimentalMinecartShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;

public interface ShapeContext {
    public static ShapeContext absent() {
        return EntityShapeContext.ABSENT;
    }

    public static ShapeContext of(Entity entity) {
        Entity entity2 = entity;
        Objects.requireNonNull(entity2);
        Entity entity3 = entity2;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractMinecartEntity.class}, (Object)entity3, n)) {
            case 0 -> {
                AbstractMinecartEntity lv = (AbstractMinecartEntity)entity3;
                if (AbstractMinecartEntity.areMinecartImprovementsEnabled(lv.getWorld())) {
                    yield new ExperimentalMinecartShapeContext(lv, false);
                }
                yield new EntityShapeContext(entity, false);
            }
            default -> new EntityShapeContext(entity, false);
        };
    }

    public static ShapeContext of(Entity entity, boolean collidesWithFluid) {
        return new EntityShapeContext(entity, collidesWithFluid);
    }

    public boolean isDescending();

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

    public boolean isHolding(Item var1);

    public boolean canWalkOnFluid(FluidState var1, FluidState var2);

    public VoxelShape getCollisionShape(BlockState var1, CollisionView var2, BlockPos var3);
}

