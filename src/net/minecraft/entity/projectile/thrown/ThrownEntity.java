/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.projectile.thrown;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ThrownEntity
extends ProjectileEntity {
    private static final float field_52510 = 12.25f;

    protected ThrownEntity(EntityType<? extends ThrownEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }

    @Override
    public boolean shouldRender(double distance) {
        if (this.age < 2 && distance < 12.25) {
            return false;
        }
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e)) {
            e = 4.0;
        }
        return distance < (e *= 64.0) * e;
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return true;
    }

    @Override
    public void tick() {
        this.tickInitialBubbleColumnCollision();
        this.applyGravity();
        this.applyDrag();
        HitResult lv = ProjectileUtil.getCollision(this, this::canHit);
        Vec3d lv2 = lv.getType() != HitResult.Type.MISS ? lv.getPos() : this.getPos().add(this.getVelocity());
        this.setPosition(lv2);
        this.updateRotation();
        this.tickBlockCollision();
        super.tick();
        if (lv.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitOrDeflect(lv);
        }
    }

    private void applyDrag() {
        float g;
        Vec3d lv = this.getVelocity();
        Vec3d lv2 = this.getPos();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float f = 0.25f;
                this.getWorld().addParticle(ParticleTypes.BUBBLE, lv2.x - lv.x * 0.25, lv2.y - lv.y * 0.25, lv2.z - lv.z * 0.25, lv.x, lv.y, lv.z);
            }
            g = 0.8f;
        } else {
            g = 0.99f;
        }
        this.setVelocity(lv.multiply(g));
    }

    private void tickInitialBubbleColumnCollision() {
        if (this.firstUpdate) {
            for (BlockPos lv : BlockPos.iterate(this.getBoundingBox())) {
                BlockState lv2 = this.getWorld().getBlockState(lv);
                if (!lv2.isOf(Blocks.BUBBLE_COLUMN)) continue;
                lv2.onEntityCollision(this.getWorld(), lv, this);
            }
        }
    }

    @Override
    protected double getGravity() {
        return 0.03;
    }
}

