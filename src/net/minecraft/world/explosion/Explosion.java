/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.explosion;

import java.lang.runtime.SwitchBootstraps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Explosion {
    public static DamageSource createDamageSource(World world, @Nullable Entity source) {
        return world.getDamageSources().explosion(source, Explosion.getCausingEntity(source));
    }

    @Nullable
    public static LivingEntity getCausingEntity(@Nullable Entity entity) {
        LivingEntity livingEntity;
        Entity entity2 = entity;
        int n = 0;
        block5: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{TntEntity.class, LivingEntity.class, ProjectileEntity.class}, (Object)entity2, n)) {
                case 0: {
                    TntEntity lv = (TntEntity)entity2;
                    livingEntity = lv.getOwner();
                    break block5;
                }
                case 1: {
                    LivingEntity lv2;
                    livingEntity = lv2 = (LivingEntity)entity2;
                    break block5;
                }
                case 2: {
                    ProjectileEntity lv3 = (ProjectileEntity)entity2;
                    Entity entity3 = lv3.getOwner();
                    if (!(entity3 instanceof LivingEntity)) {
                        n = 3;
                        continue block5;
                    }
                    LivingEntity lv4 = (LivingEntity)entity3;
                    livingEntity = lv4;
                    break block5;
                }
                default: {
                    livingEntity = null;
                    break block5;
                }
            }
            break;
        }
        return livingEntity;
    }

    public ServerWorld getWorld();

    public DestructionType getDestructionType();

    @Nullable
    public LivingEntity getCausingEntity();

    @Nullable
    public Entity getEntity();

    public float getPower();

    public Vec3d getPosition();

    public boolean canTriggerBlocks();

    public boolean preservesDecorativeEntities();

    public static enum DestructionType {
        KEEP(false),
        DESTROY(true),
        DESTROY_WITH_DECAY(true),
        TRIGGER_BLOCK(false);

        private final boolean destroysBlocks;

        private DestructionType(boolean destroysBlocks) {
            this.destroysBlocks = destroysBlocks;
        }

        public boolean destroysBlocks() {
            return this.destroysBlocks;
        }
    }
}

