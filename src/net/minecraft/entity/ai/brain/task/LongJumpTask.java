/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.LongJumpUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LongJumpTask<E extends MobEntity>
extends MultiTickTask<E> {
    protected static final int MAX_TARGET_SEARCH_TIME = 20;
    private static final int JUMP_WINDUP_TIME = 40;
    protected static final int PATHING_DISTANCE = 8;
    private static final int RUN_TIME = 200;
    private static final List<Integer> RAM_RANGES = Lists.newArrayList(65, 70, 75, 80);
    private final UniformIntProvider cooldownRange;
    protected final int verticalRange;
    protected final int horizontalRange;
    protected final float maxRange;
    protected List<Target> potentialTargets = Lists.newArrayList();
    protected Optional<Vec3d> startPos = Optional.empty();
    @Nullable
    protected Vec3d currentTarget;
    protected int targetSearchTime;
    protected long targetPickedTime;
    private final Function<E, SoundEvent> entityToSound;
    private final BiPredicate<E, BlockPos> jumpToPredicate;

    public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound) {
        this(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, LongJumpTask::shouldJumpTo);
    }

    public static <E extends MobEntity> boolean shouldJumpTo(E entity, BlockPos pos) {
        BlockPos lv2;
        World lv = entity.getWorld();
        return lv.getBlockState(lv2 = pos.down()).isOpaqueFullCube() && entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(entity, pos)) == 0.0f;
    }

    public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound, BiPredicate<E, BlockPos> jumpToPredicate) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), 200);
        this.cooldownRange = cooldownRange;
        this.verticalRange = verticalRange;
        this.horizontalRange = horizontalRange;
        this.maxRange = maxRange;
        this.entityToSound = entityToSound;
        this.jumpToPredicate = jumpToPredicate;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntity arg2) {
        boolean bl;
        boolean bl2 = bl = arg2.isOnGround() && !arg2.isTouchingWater() && !arg2.isInLava() && !arg.getBlockState(arg2.getBlockPos()).isOf(Blocks.HONEY_BLOCK);
        if (!bl) {
            arg2.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(arg.random) / 2);
        }
        return bl;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, MobEntity arg2, long l) {
        boolean bl;
        boolean bl2 = bl = this.startPos.isPresent() && this.startPos.get().equals(arg2.getPos()) && this.targetSearchTime > 0 && !arg2.isInsideWaterOrBubbleColumn() && (this.currentTarget != null || !this.potentialTargets.isEmpty());
        if (!bl && arg2.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            arg2.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(arg.random) / 2);
            arg2.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        }
        return bl;
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        this.currentTarget = null;
        this.targetSearchTime = 20;
        this.startPos = Optional.of(((Entity)arg2).getPos());
        BlockPos lv = ((Entity)arg2).getBlockPos();
        int i = lv.getX();
        int j = lv.getY();
        int k = lv.getZ();
        this.potentialTargets = BlockPos.stream(i - this.horizontalRange, j - this.verticalRange, k - this.horizontalRange, i + this.horizontalRange, j + this.verticalRange, k + this.horizontalRange).filter(pos -> !pos.equals(lv)).map(pos -> new Target(pos.toImmutable(), MathHelper.ceil(lv.getSquaredDistance((Vec3i)pos)))).collect(Collectors.toCollection(Lists::newArrayList));
    }

    @Override
    protected void keepRunning(ServerWorld arg, E arg2, long l) {
        if (this.currentTarget != null) {
            if (l - this.targetPickedTime >= 40L) {
                ((Entity)arg2).setYaw(((MobEntity)arg2).bodyYaw);
                ((LivingEntity)arg2).setNoDrag(true);
                double d = this.currentTarget.length();
                double e = d + (double)((LivingEntity)arg2).getJumpBoostVelocityModifier();
                ((Entity)arg2).setVelocity(this.currentTarget.multiply(e / d));
                ((LivingEntity)arg2).getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
                arg.playSoundFromEntity(null, (Entity)arg2, this.entityToSound.apply(arg2), SoundCategory.NEUTRAL, 1.0f, 1.0f);
            }
        } else {
            --this.targetSearchTime;
            this.pickTarget(arg, arg2, l);
        }
    }

    protected void pickTarget(ServerWorld world, E entity, long time) {
        while (!this.potentialTargets.isEmpty()) {
            Vec3d lv3;
            Vec3d lv4;
            Target lv;
            BlockPos lv2;
            Optional<Target> optional = this.removeRandomTarget(world);
            if (optional.isEmpty() || !this.canJumpTo(world, entity, lv2 = (lv = optional.get()).getPos()) || (lv4 = this.getJumpingVelocity((MobEntity)entity, lv3 = Vec3d.ofCenter(lv2))) == null) continue;
            ((LivingEntity)entity).getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(lv2));
            EntityNavigation lv5 = ((MobEntity)entity).getNavigation();
            Path lv6 = lv5.findPathTo(lv2, 0, 8);
            if (lv6 != null && lv6.reachesTarget()) continue;
            this.currentTarget = lv4;
            this.targetPickedTime = time;
            return;
        }
    }

    protected Optional<Target> removeRandomTarget(ServerWorld world) {
        Optional<Target> optional = Weighting.getRandom(world.random, this.potentialTargets);
        optional.ifPresent(this.potentialTargets::remove);
        return optional;
    }

    private boolean canJumpTo(ServerWorld world, E entity, BlockPos pos) {
        BlockPos lv = ((Entity)entity).getBlockPos();
        int i = lv.getX();
        int j = lv.getZ();
        if (i == pos.getX() && j == pos.getZ()) {
            return false;
        }
        return this.jumpToPredicate.test(entity, pos);
    }

    @Nullable
    protected Vec3d getJumpingVelocity(MobEntity entity, Vec3d targetPos) {
        ArrayList<Integer> list = Lists.newArrayList(RAM_RANGES);
        Collections.shuffle(list);
        float f = (float)(entity.getAttributeValue(EntityAttributes.JUMP_STRENGTH) * (double)this.maxRange);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            Optional<Vec3d> optional = LongJumpUtil.getJumpingVelocity(entity, targetPos, f, i, true);
            if (!optional.isPresent()) continue;
            return optional.get();
        }
        return null;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (MobEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (E)((MobEntity)entity), time);
    }

    public static class Target
    extends Weighted.Absent {
        private final BlockPos pos;

        public Target(BlockPos pos, int weight) {
            super(weight);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return this.pos;
        }
    }
}

