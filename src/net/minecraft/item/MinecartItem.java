/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MinecartItem
extends Item {
    private final EntityType<? extends AbstractMinecartEntity> type;

    public MinecartItem(EntityType<? extends AbstractMinecartEntity> type, Item.Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos lv2;
        World lv = context.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = context.getBlockPos());
        if (!lv3.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        }
        ItemStack lv4 = context.getStack();
        RailShape lv5 = lv3.getBlock() instanceof AbstractRailBlock ? lv3.get(((AbstractRailBlock)lv3.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
        double d = 0.0;
        if (lv5.isAscending()) {
            d = 0.5;
        }
        Vec3d lv6 = new Vec3d((double)lv2.getX() + 0.5, (double)lv2.getY() + 0.0625 + d, (double)lv2.getZ() + 0.5);
        AbstractMinecartEntity lv7 = AbstractMinecartEntity.create(lv, lv6.x, lv6.y, lv6.z, this.type, SpawnReason.DISPENSER, lv4, context.getPlayer());
        if (lv7 == null) {
            return ActionResult.FAIL;
        }
        if (AbstractMinecartEntity.areMinecartImprovementsEnabled(lv)) {
            List<Entity> list = lv.getOtherEntities(null, lv7.getBoundingBox());
            for (Entity lv8 : list) {
                if (!(lv8 instanceof AbstractMinecartEntity)) continue;
                return ActionResult.FAIL;
            }
        }
        if (lv instanceof ServerWorld) {
            ServerWorld lv9 = (ServerWorld)lv;
            lv9.spawnEntity(lv7);
            lv9.emitGameEvent(GameEvent.ENTITY_PLACE, lv2, GameEvent.Emitter.of(context.getPlayer(), lv9.getBlockState(lv2.down())));
        }
        lv4.decrement(1);
        return ActionResult.SUCCESS;
    }
}

