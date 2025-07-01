/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ExplosiveProjectileEntity
extends ProjectileEntity {
    public static final double field_51891 = 0.1;
    public static final double field_51892 = 0.5;
    public double accelerationPower = 0.1;

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, Vec3d velocity, World world) {
        this(type, world);
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
        this.refreshPosition();
        this.setVelocityWithAcceleration(velocity, this.accelerationPower);
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, Vec3d velocity, World world) {
        this(type, owner.getX(), owner.getY(), owner.getZ(), velocity, world);
        this.setOwner(owner);
        this.setRotation(owner.getYaw(), owner.getPitch());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public boolean shouldRender(double distance) {
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e)) {
            e = 4.0;
        }
        return distance < (e *= 64.0) * e;
    }

    protected RaycastContext.ShapeType getRaycastShapeType() {
        return RaycastContext.ShapeType.COLLIDER;
    }

    @Override
    public void tick() {
        Entity lv = this.getOwner();
        this.applyDrag();
        if (!this.getWorld().isClient && (lv != null && lv.isRemoved() || !this.getWorld().isChunkLoaded(this.getBlockPos()))) {
            this.discard();
            return;
        }
        HitResult lv2 = ProjectileUtil.getCollision((Entity)this, this::canHit, this.getRaycastShapeType());
        Vec3d lv3 = lv2.getType() != HitResult.Type.MISS ? lv2.getPos() : this.getPos().add(this.getVelocity());
        ProjectileUtil.setRotationFromVelocity(this, 0.2f);
        this.setPosition(lv3);
        this.tickBlockCollision();
        super.tick();
        if (this.isBurning()) {
            this.setOnFireFor(1.0f);
        }
        if (lv2.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitOrDeflect(lv2);
        }
        this.addParticles();
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
            g = this.getDragInWater();
        } else {
            g = this.getDrag();
        }
        this.setVelocity(lv.add(lv.normalize().multiply(this.accelerationPower)).multiply(g));
    }

    private void addParticles() {
        ParticleEffect lv = this.getParticleType();
        Vec3d lv2 = this.getPos();
        if (lv != null) {
            this.getWorld().addParticle(lv, lv2.x, lv2.y + 0.5, lv2.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
    }

    protected boolean isBurning() {
        return true;
    }

    @Nullable
    protected ParticleEffect getParticleType() {
        return ParticleTypes.SMOKE;
    }

    protected float getDrag() {
        return 0.95f;
    }

    protected float getDragInWater() {
        return 0.8f;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putDouble("acceleration_power", this.accelerationPower);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("acceleration_power", NbtElement.DOUBLE_TYPE)) {
            this.accelerationPower = nbt.getDouble("acceleration_power");
        }
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    private void setVelocityWithAcceleration(Vec3d velocity, double accelerationPower) {
        this.setVelocity(velocity.normalize().multiply(accelerationPower));
        this.velocityDirty = true;
    }

    @Override
    protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
        super.onDeflected(deflector, fromAttack);
        this.accelerationPower = fromAttack ? 0.1 : (this.accelerationPower *= 0.5);
    }
}

