/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class RaftEntity
extends AbstractBoatEntity {
    public RaftEntity(EntityType<? extends RaftEntity> arg, World arg2, Supplier<Item> supplier) {
        super(arg, arg2, supplier);
    }

    @Override
    protected double getPassengerAttachmentY(EntityDimensions dimensions) {
        return dimensions.height() * 0.8888889f;
    }
}

