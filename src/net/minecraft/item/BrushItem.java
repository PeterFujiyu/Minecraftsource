/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BrushItem
extends Item {
    public static final int ANIMATION_DURATION = 10;
    private static final int MAX_BRUSH_TIME = 200;

    public BrushItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity lv = context.getPlayer();
        if (lv != null && this.getHitResult(lv).getType() == HitResult.Type.BLOCK) {
            lv.setCurrentHand(context.getHand());
        }
        return ActionResult.CONSUME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 200;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        boolean bl;
        BlockHitResult lv3;
        PlayerEntity lv;
        block11: {
            block10: {
                if (remainingUseTicks < 0 || !(user instanceof PlayerEntity)) {
                    user.stopUsingItem();
                    return;
                }
                lv = (PlayerEntity)user;
                HitResult lv2 = this.getHitResult(lv);
                if (!(lv2 instanceof BlockHitResult)) break block10;
                lv3 = (BlockHitResult)lv2;
                if (lv2.getType() == HitResult.Type.BLOCK) break block11;
            }
            user.stopUsingItem();
            return;
        }
        int j = this.getMaxUseTime(stack, user) - remainingUseTicks + 1;
        boolean bl2 = bl = j % 10 == 5;
        if (bl) {
            SoundEvent lv8;
            Block block;
            Arm lv6;
            BlockPos lv4 = lv3.getBlockPos();
            BlockState lv5 = world.getBlockState(lv4);
            Arm arm = lv6 = user.getActiveHand() == Hand.MAIN_HAND ? lv.getMainArm() : lv.getMainArm().getOpposite();
            if (lv5.hasBlockBreakParticles() && lv5.getRenderType() != BlockRenderType.INVISIBLE) {
                this.addDustParticles(world, lv3, lv5, user.getRotationVec(0.0f), lv6);
            }
            if ((block = lv5.getBlock()) instanceof BrushableBlock) {
                BrushableBlock lv7 = (BrushableBlock)block;
                lv8 = lv7.getBrushingSound();
            } else {
                lv8 = SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC;
            }
            world.playSound(lv, lv4, lv8, SoundCategory.BLOCKS);
            if (world instanceof ServerWorld) {
                BrushableBlockEntity lv10;
                boolean bl22;
                ServerWorld lv9 = (ServerWorld)world;
                BlockEntity blockEntity = world.getBlockEntity(lv4);
                if (blockEntity instanceof BrushableBlockEntity && (bl22 = (lv10 = (BrushableBlockEntity)blockEntity).brush(world.getTime(), lv9, lv, lv3.getSide(), stack))) {
                    EquipmentSlot lv11 = stack.equals(lv.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    stack.damage(1, lv, lv11);
                }
            }
        }
    }

    private HitResult getHitResult(PlayerEntity user) {
        return ProjectileUtil.getCollision((Entity)user, EntityPredicates.CAN_HIT, user.getBlockInteractionRange());
    }

    private void addDustParticles(World world, BlockHitResult hitResult, BlockState state, Vec3d userRotation, Arm arm) {
        double d = 3.0;
        int i = arm == Arm.RIGHT ? 1 : -1;
        int j = world.getRandom().nextBetweenExclusive(7, 12);
        BlockStateParticleEffect lv = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
        Direction lv2 = hitResult.getSide();
        DustParticlesOffset lv3 = DustParticlesOffset.fromSide(userRotation, lv2);
        Vec3d lv4 = hitResult.getPos();
        for (int k = 0; k < j; ++k) {
            world.addParticle(lv, lv4.x - (double)(lv2 == Direction.WEST ? 1.0E-6f : 0.0f), lv4.y, lv4.z - (double)(lv2 == Direction.NORTH ? 1.0E-6f : 0.0f), lv3.xd() * (double)i * 3.0 * world.getRandom().nextDouble(), 0.0, lv3.zd() * (double)i * 3.0 * world.getRandom().nextDouble());
        }
    }

    record DustParticlesOffset(double xd, double yd, double zd) {
        private static final double field_42685 = 1.0;
        private static final double field_42686 = 0.1;

        public static DustParticlesOffset fromSide(Vec3d userRotation, Direction side) {
            double d = 0.0;
            return switch (side) {
                default -> throw new MatchException(null, null);
                case Direction.DOWN, Direction.UP -> new DustParticlesOffset(userRotation.getZ(), 0.0, -userRotation.getX());
                case Direction.NORTH -> new DustParticlesOffset(1.0, 0.0, -0.1);
                case Direction.SOUTH -> new DustParticlesOffset(-1.0, 0.0, 0.1);
                case Direction.WEST -> new DustParticlesOffset(-0.1, 0.0, -1.0);
                case Direction.EAST -> new DustParticlesOffset(0.1, 0.0, 1.0);
            };
        }
    }
}

