/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.EntityView;
import org.jetbrains.annotations.Nullable;

public interface EntityLookupView
extends EntityView {
    public ServerWorld toServerWorld();

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, entity.getX(), entity.getY(), entity.getZ());
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, null, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(Class<? extends T> clazz, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box) {
        return (T)this.getClosestEntity(this.getEntitiesByClass(clazz, box, potentialEntity -> true), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(List<? extends T> entities, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z) {
        double g = -1.0;
        LivingEntity lv = null;
        for (LivingEntity lv2 : entities) {
            if (!targetPredicate.test(this.toServerWorld(), entity, lv2)) continue;
            double h = lv2.squaredDistanceTo(x, y, z);
            if (g != -1.0 && !(h < g)) continue;
            g = h;
            lv = lv2;
        }
        return (T)lv;
    }

    default public List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
        ArrayList<PlayerEntity> list = new ArrayList<PlayerEntity>();
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) || !targetPredicate.test(this.toServerWorld(), entity, playerEntity)) continue;
            list.add(playerEntity);
        }
        return list;
    }

    default public <T extends LivingEntity> List<T> getTargets(Class<T> clazz, TargetPredicate targetPredicate, LivingEntity entity2, Box box) {
        List<LivingEntity> list = this.getEntitiesByClass(clazz, box, entity -> true);
        ArrayList<LivingEntity> list2 = new ArrayList<LivingEntity>();
        for (LivingEntity lv : list) {
            if (!targetPredicate.test(this.toServerWorld(), entity2, lv)) continue;
            list2.add(lv);
        }
        return list2;
    }
}

