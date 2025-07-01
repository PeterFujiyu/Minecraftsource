/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask {
    private static final int REMEMBER_TIME = 200;

    public static <E extends MobEntity> Task<E> create(ForgetCallback<E> callback) {
        return ForgetAttackTargetTask.create((world, target) -> false, callback, true);
    }

    public static <E extends MobEntity> Task<E> create(AlternativeCondition condition) {
        return ForgetAttackTargetTask.create(condition, (world, entity, target) -> {}, true);
    }

    public static <E extends MobEntity> Task<E> create() {
        return ForgetAttackTargetTask.create((world, target) -> false, (world, entity, target) -> {}, true);
    }

    public static <E extends MobEntity> Task<E> create(AlternativeCondition condition, ForgetCallback<E> callback, boolean shouldForgetIfTargetUnreachable) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(context, (attackTarget, cantReachWalkTargetSince) -> (world, entity, time) -> {
            LivingEntity lv = (LivingEntity)context.getValue(attackTarget);
            if (!entity.canTarget(lv) || shouldForgetIfTargetUnreachable && ForgetAttackTargetTask.cannotReachTarget(entity, context.getOptionalValue(cantReachWalkTargetSince)) || !lv.isAlive() || lv.getWorld() != entity.getWorld() || condition.test(world, lv)) {
                callback.accept(world, entity, lv);
                attackTarget.forget();
                return true;
            }
            return true;
        }));
    }

    private static boolean cannotReachTarget(LivingEntity target, Optional<Long> lastReachTime) {
        return lastReachTime.isPresent() && target.getWorld().getTime() - lastReachTime.get() > 200L;
    }

    @FunctionalInterface
    public static interface AlternativeCondition {
        public boolean test(ServerWorld var1, LivingEntity var2);
    }

    @FunctionalInterface
    public static interface ForgetCallback<E> {
        public void accept(ServerWorld var1, E var2, LivingEntity var3);
    }
}

