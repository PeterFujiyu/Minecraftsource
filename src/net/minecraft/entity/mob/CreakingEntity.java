/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.mob;

import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreakingBrain;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CreakingEntity
extends HostileEntity {
    private static final TrackedData<Boolean> UNROOTED = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ACTIVE = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CRUMBLING = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<BlockPos>> HOME_POS = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    private static final int field_54573 = 15;
    private static final int field_54574 = 1;
    private static final float ATTACK_DAMAGE = 3.0f;
    private static final float field_54576 = 32.0f;
    private static final float field_54577 = 144.0f;
    public static final int field_54566 = 40;
    private static final float field_54578 = 0.4f;
    public static final float field_54567 = 0.3f;
    public static final int field_54569 = 16545810;
    public static final int field_54580 = 0x5F5F5F;
    public static final int field_55485 = 8;
    public static final int field_55486 = 45;
    private static final int field_55488 = 4;
    private int attackAnimationTimer;
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState invulnerableAnimationState = new AnimationState();
    public final AnimationState crumblingAnimationState = new AnimationState();
    private int invulnerableAnimationTimer;
    private boolean glowingEyesWhileCrumbling;
    private int nextEyeFlickerTime;
    private int playerIntersectionTimer;

    public CreakingEntity(EntityType<? extends CreakingEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.lookControl = new CreakingLookControl(this);
        this.moveControl = new CreakingMoveControl(this);
        this.jumpControl = new CreakingJumpControl(this);
        MobNavigation lv = (MobNavigation)this.getNavigation();
        lv.setCanSwim(true);
        this.experiencePoints = 0;
    }

    public void initHomePos(BlockPos homePos) {
        this.setHomePos(homePos);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0f);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0f);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0f);
    }

    public boolean isTransient() {
        return this.getHomePos() != null;
    }

    @Override
    protected BodyControl createBodyControl() {
        return new CreakingBodyControl(this);
    }

    protected Brain.Profile<CreakingEntity> createBrainProfile() {
        return CreakingBrain.createBrainProfile();
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return CreakingBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(UNROOTED, true);
        builder.add(ACTIVE, false);
        builder.add(CRUMBLING, false);
        builder.add(HOME_POS, Optional.empty());
    }

    public static DefaultAttributeContainer.Builder createCreakingAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 1.0).add(EntityAttributes.MOVEMENT_SPEED, 0.4f).add(EntityAttributes.ATTACK_DAMAGE, 3.0).add(EntityAttributes.FOLLOW_RANGE, 32.0).add(EntityAttributes.STEP_HEIGHT, 1.0625);
    }

    public boolean isUnrooted() {
        return this.dataTracker.get(UNROOTED);
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!(target instanceof LivingEntity)) {
            return false;
        }
        this.attackAnimationTimer = 15;
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        return super.tryAttack(world, target);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        CreakingHeartBlockEntity lv4;
        BlockPos lv = this.getHomePos();
        if (lv == null || source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(world, source, amount);
        }
        if (this.isInvulnerableTo(world, source) || this.invulnerableAnimationTimer > 0 || this.isDead()) {
            return false;
        }
        PlayerEntity lv2 = this.becomeAngryAndGetPlayer(source);
        Entity lv3 = source.getSource();
        if (!(lv3 instanceof LivingEntity) && !(lv3 instanceof ProjectileEntity) && lv2 == null) {
            return false;
        }
        this.invulnerableAnimationTimer = 8;
        this.getWorld().sendEntityStatus(this, EntityStatuses.INVULNERABLE_CREAKING_HIT);
        BlockEntity blockEntity = this.getWorld().getBlockEntity(lv);
        if (blockEntity instanceof CreakingHeartBlockEntity && (lv4 = (CreakingHeartBlockEntity)blockEntity).isPuppet(this)) {
            if (lv2 != null) {
                lv4.onPuppetDamage();
            }
            this.playHurtSound(source);
        }
        return true;
    }

    public PlayerEntity becomeAngryAndGetPlayer(DamageSource damageSource) {
        this.becomeAngry(damageSource);
        return this.setAttackingPlayer(damageSource);
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && this.isUnrooted();
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        if (!this.isUnrooted()) {
            return;
        }
        super.addVelocity(deltaX, deltaY, deltaZ);
    }

    public Brain<CreakingEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void mobTick(ServerWorld world) {
        Profiler lv = Profilers.get();
        lv.push("creakingBrain");
        this.getBrain().tick((ServerWorld)this.getWorld(), this);
        lv.pop();
        CreakingBrain.updateActivities(this);
    }

    @Override
    public void tickMovement() {
        if (this.invulnerableAnimationTimer > 0) {
            --this.invulnerableAnimationTimer;
        }
        if (this.attackAnimationTimer > 0) {
            --this.attackAnimationTimer;
        }
        if (!this.getWorld().isClient) {
            boolean bl = this.dataTracker.get(UNROOTED);
            boolean bl2 = this.shouldBeUnrooted();
            if (bl2 != bl) {
                this.emitGameEvent(GameEvent.ENTITY_ACTION);
                if (bl2) {
                    this.playSound(SoundEvents.ENTITY_CREAKING_UNFREEZE);
                } else {
                    this.stopMovement();
                    this.playSound(SoundEvents.ENTITY_CREAKING_FREEZE);
                }
            }
            this.dataTracker.set(UNROOTED, bl2);
        }
        super.tickMovement();
    }

    @Override
    public void tick() {
        BlockPos lv;
        if (!this.getWorld().isClient && (lv = this.getHomePos()) != null) {
            CreakingHeartBlockEntity lv2;
            boolean bl;
            BlockEntity blockEntity = this.getWorld().getBlockEntity(lv);
            boolean bl2 = bl = blockEntity instanceof CreakingHeartBlockEntity && (lv2 = (CreakingHeartBlockEntity)blockEntity).isPuppet(this);
            if (!bl) {
                this.setHealth(0.0f);
            }
        }
        super.tick();
        if (this.getWorld().isClient) {
            this.tickAttackAnimation();
            this.updateCrumblingEyeFlicker();
        }
    }

    @Override
    protected void updatePostDeath() {
        if (this.isTransient() && this.isCrumbling()) {
            ++this.deathTime;
            if (!this.getWorld().isClient() && this.deathTime > 45 && !this.isRemoved()) {
                this.finishCrumbling();
            }
        } else {
            super.updatePostDeath();
        }
    }

    @Override
    protected void updateLimbs(float posDelta) {
        float g = Math.min(posDelta * 25.0f, 3.0f);
        this.limbAnimator.updateLimbs(g, 0.4f, 1.0f);
    }

    private void tickAttackAnimation() {
        this.attackAnimationState.setRunning(this.attackAnimationTimer > 0, this.age);
        this.invulnerableAnimationState.setRunning(this.invulnerableAnimationTimer > 0, this.age);
        this.crumblingAnimationState.setRunning(this.isCrumbling(), this.age);
    }

    public void finishCrumbling() {
        World world = this.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld lv = (ServerWorld)world;
            Box lv2 = this.getBoundingBox();
            Vec3d lv3 = lv2.getCenter();
            double d = lv2.getLengthX() * 0.3;
            double e = lv2.getLengthY() * 0.3;
            double f = lv2.getLengthZ() * 0.3;
            lv.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.getDefaultState()), lv3.x, lv3.y, lv3.z, 100, d, e, f, 0.0);
            lv.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK_CRUMBLE, (BlockState)Blocks.CREAKING_HEART.getDefaultState().with(CreakingHeartBlock.ACTIVE, true)), lv3.x, lv3.y, lv3.z, 10, d, e, f, 0.0);
        }
        this.playSound(this.getDeathSound());
        this.remove(Entity.RemovalReason.DISCARDED);
    }

    public void killFromHeart(DamageSource damageSource) {
        this.becomeAngryAndGetPlayer(damageSource);
        this.onDeath(damageSource);
        this.playSound(SoundEvents.ENTITY_CREAKING_TWITCH);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.INVULNERABLE_CREAKING_HIT) {
            this.invulnerableAnimationTimer = 8;
            this.playHurtSound(this.getDamageSources().generic());
        } else if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
            this.attackAnimationTimer = 15;
            this.playAttackSound();
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public boolean isFireImmune() {
        return this.isTransient() || super.isFireImmune();
    }

    @Override
    public boolean canBeNameTagged() {
        return !this.isTransient() && super.canBeNameTagged();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return !this.isTransient() && super.canAddPassenger(passenger);
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return !this.isTransient() && super.couldAcceptPassenger();
    }

    @Override
    protected void addPassenger(Entity passenger) {
        if (this.isTransient()) {
            throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
        }
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return !this.isTransient() && super.canUsePortals(allowVehicles);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new CreakingNavigation(this, world);
    }

    public boolean isStuckWithPlayer() {
        List list = this.brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
        if (list.isEmpty()) {
            this.playerIntersectionTimer = 0;
            return false;
        }
        Box lv = this.getBoundingBox();
        for (PlayerEntity lv2 : list) {
            if (!lv.contains(lv2.getEyePos())) continue;
            ++this.playerIntersectionTimer;
            return this.playerIntersectionTimer > 4;
        }
        this.playerIntersectionTimer = 0;
        return false;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("home_pos")) {
            this.initHomePos(NbtHelper.toBlockPos(nbt, "home_pos").orElseThrow());
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        BlockPos lv = this.getHomePos();
        if (lv != null) {
            nbt.put("home_pos", NbtHelper.fromBlockPos(lv));
        }
    }

    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, Optional.of(pos));
    }

    @Nullable
    public BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS).orElse(null);
    }

    public void setCrumbling() {
        this.dataTracker.set(CRUMBLING, true);
    }

    public boolean isCrumbling() {
        return this.dataTracker.get(CRUMBLING);
    }

    public boolean hasGlowingEyesWhileCrumbling() {
        return this.glowingEyesWhileCrumbling;
    }

    public void updateCrumblingEyeFlicker() {
        if (this.deathTime > this.nextEyeFlickerTime) {
            this.nextEyeFlickerTime = this.deathTime + this.getRandom().nextBetween(this.glowingEyesWhileCrumbling ? 2 : this.deathTime / 4, this.glowingEyesWhileCrumbling ? 8 : this.deathTime / 2);
            this.glowingEyesWhileCrumbling = !this.glowingEyesWhileCrumbling;
        }
    }

    @Override
    public void playAttackSound() {
        this.playSound(SoundEvents.ENTITY_CREAKING_ATTACK);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isActive()) {
            return null;
        }
        return SoundEvents.ENTITY_CREAKING_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isTransient() ? SoundEvents.ENTITY_CREAKING_SWAY : super.getHurtSound(source);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CREAKING_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_CREAKING_STEP, 0.15f, 1.0f);
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.getTargetInBrain();
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isUnrooted()) {
            return;
        }
        super.takeKnockback(strength, x, z);
    }

    public boolean shouldBeUnrooted() {
        List list = this.brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
        boolean bl = this.isActive();
        if (list.isEmpty()) {
            if (bl) {
                this.deactivate();
            }
            return true;
        }
        boolean bl2 = false;
        for (PlayerEntity lv : list) {
            if (!this.canTarget(lv) || this.isTeammate(lv)) continue;
            bl2 = true;
            if (bl && !LivingEntity.NOT_WEARING_GAZE_DISGUISE_PREDICATE.test(lv) || !this.isEntityLookingAtMe(lv, 0.5, false, true, this.getEyeY(), this.getY() + 0.5 * (double)this.getScale(), (this.getEyeY() + this.getY()) / 2.0)) continue;
            if (bl) {
                return false;
            }
            if (!(lv.squaredDistanceTo(this) < 144.0)) continue;
            this.activate(lv);
            return false;
        }
        if (!bl2 && bl) {
            this.deactivate();
        }
        return true;
    }

    public void activate(PlayerEntity player) {
        this.getBrain().remember(MemoryModuleType.ATTACK_TARGET, player);
        this.emitGameEvent(GameEvent.ENTITY_ACTION);
        this.playSound(SoundEvents.ENTITY_CREAKING_ACTIVATE);
        this.setActive(true);
    }

    public void deactivate() {
        this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        this.emitGameEvent(GameEvent.ENTITY_ACTION);
        this.playSound(SoundEvents.ENTITY_CREAKING_DEACTIVATE);
        this.setActive(false);
    }

    public void setActive(boolean active) {
        this.dataTracker.set(ACTIVE, active);
    }

    public boolean isActive() {
        return this.dataTracker.get(ACTIVE);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0f;
    }

    class CreakingLookControl
    extends LookControl {
        public CreakingLookControl(CreakingEntity creaking) {
            super(creaking);
        }

        @Override
        public void tick() {
            if (CreakingEntity.this.isUnrooted()) {
                super.tick();
            }
        }
    }

    class CreakingMoveControl
    extends MoveControl {
        public CreakingMoveControl(CreakingEntity creaking) {
            super(creaking);
        }

        @Override
        public void tick() {
            if (CreakingEntity.this.isUnrooted()) {
                super.tick();
            }
        }
    }

    class CreakingJumpControl
    extends JumpControl {
        public CreakingJumpControl(CreakingEntity creaking) {
            super(creaking);
        }

        @Override
        public void tick() {
            if (CreakingEntity.this.isUnrooted()) {
                super.tick();
            } else {
                CreakingEntity.this.setJumping(false);
            }
        }
    }

    class CreakingBodyControl
    extends BodyControl {
        public CreakingBodyControl(CreakingEntity creaking) {
            super(creaking);
        }

        @Override
        public void tick() {
            if (CreakingEntity.this.isUnrooted()) {
                super.tick();
            }
        }
    }

    class CreakingNavigation
    extends MobNavigation {
        CreakingNavigation(CreakingEntity creaking, World world) {
            super(creaking, world);
        }

        @Override
        public void tick() {
            if (CreakingEntity.this.isUnrooted()) {
                super.tick();
            }
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new CreakingLandPathNodeMaker();
            this.nodeMaker.setCanEnterOpenDoors(true);
            return new PathNodeNavigator(this.nodeMaker, range);
        }
    }

    class CreakingLandPathNodeMaker
    extends LandPathNodeMaker {
        private static final int field_54896 = 1024;

        CreakingLandPathNodeMaker() {
        }

        @Override
        public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
            BlockPos lv = CreakingEntity.this.getHomePos();
            if (lv == null) {
                return super.getDefaultNodeType(context, x, y, z);
            }
            double d = lv.getSquaredDistance(new Vec3i(x, y, z));
            if (d > 1024.0 && d >= lv.getSquaredDistance(context.getEntityPos())) {
                return PathNodeType.BLOCKED;
            }
            return super.getDefaultNodeType(context, x, y, z);
        }
    }
}

