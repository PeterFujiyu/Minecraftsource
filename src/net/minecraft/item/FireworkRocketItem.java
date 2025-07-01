/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkRocketItem
extends Item
implements ProjectileItem {
    public static final byte[] FLIGHT_VALUES = new byte[]{1, 2, 3};
    public static final double OFFSET_POS_MULTIPLIER = 0.15;

    public FireworkRocketItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World lv = context.getWorld();
        if (lv instanceof ServerWorld) {
            ServerWorld lv2 = (ServerWorld)lv;
            ItemStack lv3 = context.getStack();
            Vec3d lv4 = context.getHitPos();
            Direction lv5 = context.getSide();
            ProjectileEntity.spawn(new FireworkRocketEntity(lv, context.getPlayer(), lv4.x + (double)lv5.getOffsetX() * 0.15, lv4.y + (double)lv5.getOffsetY() * 0.15, lv4.z + (double)lv5.getOffsetZ() * 0.15, lv3), lv2, lv3);
            lv3.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user.isGliding()) {
            ItemStack lv = user.getStackInHand(hand);
            if (world instanceof ServerWorld) {
                ServerWorld lv2 = (ServerWorld)world;
                ProjectileEntity.spawn(new FireworkRocketEntity(world, lv, user), lv2, lv);
                lv.decrementUnlessCreative(1, user);
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        FireworksComponent lv = stack.get(DataComponentTypes.FIREWORKS);
        if (lv != null) {
            lv.appendTooltip(context, tooltip::add, type);
        }
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new FireworkRocketEntity(world, stack.copyWithCount(1), pos.getX(), pos.getY(), pos.getZ(), true);
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return ProjectileItem.Settings.builder().positionFunction(FireworkRocketItem::position).uncertainty(1.0f).power(0.5f).overrideDispenseEvent(1004).build();
    }

    private static Vec3d position(BlockPointer pointer, Direction facing) {
        return pointer.centerPos().add((double)facing.getOffsetX() * 0.5000099999997474, (double)facing.getOffsetY() * 0.5000099999997474, (double)facing.getOffsetZ() * 0.5000099999997474);
    }
}

