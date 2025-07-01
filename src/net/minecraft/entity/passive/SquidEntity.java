/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.passive;

import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SquidEntity
extends WaterAnimalEntity {
    public float tiltAngle;
    public float prevTiltAngle;
    public float rollAngle;
    public float prevRollAngle;
    public float thrustTimer;
    public float prevThrustTimer;
    public float tentacleAngle;
    public float prevTentacleAngle;
    private float swimVelocityScale;
    private float thrustTimerSpeed;
    private float turningSpeed;
    Vec3d swimVec = Vec3d.ZERO;

    public SquidEntity(EntityType<? extends SquidEntity> arg, World arg2) {
        super((EntityType<? extends WaterAnimalEntity>)arg, arg2);
        this.random.setSeed(this.getId());
        this.thrustTimerSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeAttackerGoal());
    }

    public static DefaultAttributeContainer.Builder createSquidAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 10.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    protected SoundEvent getSquirtSound() {
        return SoundEvents.ENTITY_SQUID_SQUIRT;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.EVENTS;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return EntityType.SQUID.create(world, SpawnReason.BREEDING);
    }

    @Override
    protected double getGravity() {
        return 0.08;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.prevTiltAngle = this.tiltAngle;
        this.prevRollAngle = this.rollAngle;
        this.prevThrustTimer = this.thrustTimer;
        this.prevTentacleAngle = this.tentacleAngle;
        this.thrustTimer += this.thrustTimerSpeed;
        if ((double)this.thrustTimer > Math.PI * 2) {
            if (this.getWorld().isClient) {
                this.thrustTimer = (float)Math.PI * 2;
            } else {
                this.thrustTimer -= (float)Math.PI * 2;
                if (this.random.nextInt(10) == 0) {
                    this.thrustTimerSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
                }
                this.getWorld().sendEntityStatus(this, EntityStatuses.RESET_SQUID_THRUST_TIMER);
            }
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            if (this.thrustTimer < (float)Math.PI) {
                float f = this.thrustTimer / (float)Math.PI;
                this.tentacleAngle = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25f;
                if ((double)f > 0.75) {
                    if (this.isLogicalSideForUpdatingMovement()) {
                        this.setVelocity(this.swimVec);
                    }
                    this.turningSpeed = 1.0f;
                } else {
                    this.turningSpeed *= 0.8f;
                }
            } else {
                this.tentacleAngle = 0.0f;
                if (this.isLogicalSideForUpdatingMovement()) {
                    this.setVelocity(this.getVelocity().multiply(0.9));
                }
                this.turningSpeed *= 0.99f;
            }
            Vec3d lv = this.getVelocity();
            double d = lv.horizontalLength();
            this.bodyYaw += (-((float)MathHelper.atan2(lv.x, lv.z)) * 57.295776f - this.bodyYaw) * 0.1f;
            this.setYaw(this.bodyYaw);
            this.rollAngle += (float)Math.PI * this.turningSpeed * 1.5f;
            this.tiltAngle += (-((float)MathHelper.atan2(d, lv.y)) * 57.295776f - this.tiltAngle) * 0.1f;
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.thrustTimer)) * (float)Math.PI * 0.25f;
            if (!this.getWorld().isClient) {
                double e = this.getVelocity().y;
                e = this.hasStatusEffect(StatusEffects.LEVITATION) ? 0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) : (e -= this.getFinalGravity());
                this.setVelocity(0.0, e * (double)0.98f, 0.0);
            }
            this.tiltAngle += (-90.0f - this.tiltAngle) * 0.02f;
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (super.damage(world, source, amount) && this.getAttacker() != null) {
            this.squirt();
            return true;
        }
        return false;
    }

    private Vec3d applyBodyRotations(Vec3d shootVector) {
        Vec3d lv = shootVector.rotateX(this.prevTiltAngle * ((float)Math.PI / 180));
        lv = lv.rotateY(-this.prevBodyYaw * ((float)Math.PI / 180));
        return lv;
    }

    private void squirt() {
        this.playSound(this.getSquirtSound());
        Vec3d lv = this.applyBodyRotations(new Vec3d(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());
        for (int i = 0; i < 30; ++i) {
            Vec3d lv2 = this.applyBodyRotations(new Vec3d((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
            float f = this.isBaby() ? 0.1f : 0.3f;
            Vec3d lv3 = lv2.multiply(f + this.random.nextFloat() * 2.0f);
            ((ServerWorld)this.getWorld()).spawnParticles(this.getInkParticle(), lv.x, lv.y + 0.5, lv.z, 0, lv3.x, lv3.y, lv3.z, 0.1f);
        }
    }

    protected ParticleEffect getInkParticle() {
        return ParticleTypes.SQUID_INK;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.move(MovementType.SELF, this.getVelocity());
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.RESET_SQUID_THRUST_TIMER) {
            this.thrustTimer = 0.0f;
        } else {
            super.handleStatus(status);
        }
    }

    public boolean hasSwimmingVector() {
        return this.swimVec.lengthSquared() > (double)1.0E-5f;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData lv = Objects.requireNonNullElseGet(entityData, () -> new PassiveEntity.PassiveData(0.05f));
        return super.initialize(world, difficulty, spawnReason, lv);
    }

    static class SwimGoal
    extends Goal {
        private final SquidEntity squid;

        public SwimGoal(SquidEntity squid) {
            this.squid = squid;
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            int i = this.squid.getDespawnCounter();
            if (i > 100) {
                this.squid.swimVec = Vec3d.ZERO;
            } else if (this.squid.getRandom().nextInt(SwimGoal.toGoalTicks(50)) == 0 || !this.squid.touchingWater || !this.squid.hasSwimmingVector()) {
                float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2);
                this.squid.swimVec = new Vec3d(MathHelper.cos(f) * 0.2f, -0.1f + this.squid.getRandom().nextFloat() * 0.2f, MathHelper.sin(f) * 0.2f);
            }
        }
    }

    class EscapeAttackerGoal
    extends Goal {
        private static final float field_30375 = 3.0f;
        private static final float field_30376 = 5.0f;
        private static final float field_30377 = 10.0f;
        private int timer;

        EscapeAttackerGoal() {
        }

        @Override
        public boolean canStart() {
            LivingEntity lv = SquidEntity.this.getAttacker();
            if (SquidEntity.this.isTouchingWater() && lv != null) {
                return SquidEntity.this.squaredDistanceTo(lv) < 100.0;
            }
            return false;
        }

        @Override
        public void start() {
            this.timer = 0;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.timer;
            LivingEntity lv = SquidEntity.this.getAttacker();
            if (lv == null) {
                return;
            }
            Vec3d lv2 = new Vec3d(SquidEntity.this.getX() - lv.getX(), SquidEntity.this.getY() - lv.getY(), SquidEntity.this.getZ() - lv.getZ());
            BlockState lv3 = SquidEntity.this.getWorld().getBlockState(BlockPos.ofFloored(SquidEntity.this.getX() + lv2.x, SquidEntity.this.getY() + lv2.y, SquidEntity.this.getZ() + lv2.z));
            FluidState lv4 = SquidEntity.this.getWorld().getFluidState(BlockPos.ofFloored(SquidEntity.this.getX() + lv2.x, SquidEntity.this.getY() + lv2.y, SquidEntity.this.getZ() + lv2.z));
            if (lv4.isIn(FluidTags.WATER) || lv3.isAir()) {
                double d = lv2.length();
                if (d > 0.0) {
                    lv2.normalize();
                    double e = 3.0;
                    if (d > 5.0) {
                        e -= (d - 5.0) / 5.0;
                    }
                    if (e > 0.0) {
                        lv2 = lv2.multiply(e);
                    }
                }
                if (lv3.isAir()) {
                    lv2 = lv2.subtract(0.0, lv2.y, 0.0);
                }
                SquidEntity.this.swimVec = new Vec3d(lv2.x / 20.0, lv2.y / 20.0, lv2.z / 20.0);
            }
            if (this.timer % 10 == 5) {
                SquidEntity.this.getWorld().addParticle(ParticleTypes.BUBBLE, SquidEntity.this.getX(), SquidEntity.this.getY(), SquidEntity.this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }
}

