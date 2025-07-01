/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractChestBoatEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ChestRaftEntity
extends AbstractChestBoatEntity {
    public ChestRaftEntity(EntityType<? extends ChestRaftEntity> arg, World arg2, Supplier<Item> supplier) {
        super((EntityType<? extends AbstractChestBoatEntity>)arg, arg2, supplier);
    }

    @Override
    protected double getPassengerAttachmentY(EntityDimensions dimensions) {
        return dimensions.height() * 0.8888889f;
    }
}

