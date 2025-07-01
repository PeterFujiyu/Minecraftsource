/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PersistentProjectileEntity
extends ProjectileEntity {
    private static final double field_30657 = 2.0;
    private static final int field_54968 = 7;
    private static final float field_55017 = 0.6f;
    private static final float DEFAULT_DRAG = 0.99f;
    private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> IN_GROUND = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final int CRITICAL_FLAG = 1;
    private static final int NO_CLIP_FLAG = 2;
    @Nullable
    private BlockState inBlockState;
    protected int inGroundTime;
    public PickupPermission pickupType = PickupPermission.DISALLOWED;
    public int shake;
    private int life;
    private double damage = 2.0;
    private SoundEvent sound = this.getHitSound();
    @Nullable
    private IntOpenHashSet piercedEntities;
    @Nullable
    private List<Entity> piercingKilledEntities;
    private ItemStack stack = this.getDefaultItemStack();
    @Nullable
    private ItemStack weapon = null;

    protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world, ItemStack stack, @Nullable ItemStack weapon) {
        this(type, world);
        this.stack = stack.copy();
        this.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));
        Unit lv = stack.remove(DataComponentTypes.INTANGIBLE_PROJECTILE);
        if (lv != null) {
            this.pickupType = PickupPermission.CREATIVE_ONLY;
        }
        this.setPosition(x, y, z);
        if (weapon != null && world instanceof ServerWorld) {
            ServerWorld lv2 = (ServerWorld)world;
            if (weapon.isEmpty()) {
                throw new IllegalArgumentException("Invalid weapon firing an arrow");
            }
            this.weapon = weapon.copy();
            int i = EnchantmentHelper.getProjectilePiercing(lv2, weapon, this.stack);
            if (i > 0) {
                this.setPierceLevel((byte)i);
            }
        }
    }

    protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, ItemStack stack, @Nullable ItemStack shotFrom) {
        this(type, owner.getX(), owner.getEyeY() - (double)0.1f, owner.getZ(), world, stack, shotFrom);
        this.setOwner(owner);
    }

    public void setSound(SoundEvent sound) {
        this.sound = sound;
    }

    @Override
    public boolean shouldRender(double distance) {
        double e = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return distance < (e *= 64.0 * PersistentProjectileEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(PROJECTILE_FLAGS, (byte)0);
        builder.add(PIERCE_LEVEL, (byte)0);
        builder.add(IN_GROUND, false);
    }

    @Override
    public void setVelocity(double x, double y, double z, float power, float uncertainty) {
        super.setVelocity(x, y, z, power, uncertainty);
        this.life = 0;
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        super.setVelocityClient(x, y, z);
        this.life = 0;
        if (this.isInGround() && MathHelper.squaredMagnitude(x, y, z) > 0.0) {
            this.setInGround(false);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (!this.firstUpdate && this.shake <= 0 && data.equals(IN_GROUND) && this.isInGround()) {
            this.shake = 7;
        }
    }

    @Override
    public void tick() {
        VoxelShape lv4;
        boolean bl = !this.isNoClip();
        Vec3d lv = this.getVelocity();
        BlockPos lv2 = this.getBlockPos();
        BlockState lv3 = this.getWorld().getBlockState(lv2);
        if (!lv3.isAir() && bl && !(lv4 = lv3.getCollisionShape(this.getWorld(), lv2)).isEmpty()) {
            Vec3d lv5 = this.getPos();
            for (Box lv6 : lv4.getBoundingBoxes()) {
                if (!lv6.offset(lv2).contains(lv5)) continue;
                this.setInGround(true);
                break;
            }
        }
        if (this.shake > 0) {
            --this.shake;
        }
        if (this.isTouchingWaterOrRain() || lv3.isOf(Blocks.POWDER_SNOW)) {
            this.extinguish();
        }
        if (this.isInGround() && bl) {
            if (!this.getWorld().isClient()) {
                if (this.inBlockState != lv3 && this.shouldFall()) {
                    this.fall();
                } else {
                    this.age();
                }
            }
            ++this.inGroundTime;
            if (this.isAlive()) {
                this.tickBlockCollision();
            }
            return;
        }
        this.inGroundTime = 0;
        Vec3d lv7 = this.getPos();
        if (this.isTouchingWater()) {
            this.applyDrag(this.getDragInWater());
            this.spawnBubbleParticles(lv7);
        }
        if (this.isCritical()) {
            for (int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(ParticleTypes.CRIT, lv7.x + lv.x * (double)i / 4.0, lv7.y + lv.y * (double)i / 4.0, lv7.z + lv.z * (double)i / 4.0, -lv.x, -lv.y + 0.2, -lv.z);
            }
        }
        float f = !bl ? (float)(MathHelper.atan2(-lv.x, -lv.z) * 57.2957763671875) : (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875);
        float g = (float)(MathHelper.atan2(lv.y, lv.horizontalLength()) * 57.2957763671875);
        this.setPitch(PersistentProjectileEntity.updateRotation(this.getPitch(), g));
        this.setYaw(PersistentProjectileEntity.updateRotation(this.getYaw(), f));
        if (bl) {
            BlockHitResult lv8 = this.getWorld().getCollisionsIncludingWorldBorder(new RaycastContext(lv7, lv7.add(lv), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            this.applyCollision(lv8);
        } else {
            this.setPosition(lv7.add(lv));
            this.tickBlockCollision();
        }
        if (!this.isTouchingWater()) {
            this.applyDrag(0.99f);
        }
        if (bl && !this.isInGround()) {
            this.applyGravity();
        }
        super.tick();
    }

    private void applyCollision(BlockHitResult blockHitResult) {
        while (this.isAlive()) {
            Vec3d lv = this.getPos();
            EntityHitResult lv2 = this.getEntityCollision(lv, blockHitResult.getPos());
            Vec3d lv3 = ((HitResult)Objects.requireNonNullElse(lv2, blockHitResult)).getPos();
            this.setPosition(lv3);
            this.tickBlockCollision(lv, lv3);
            if (this.portalManager != null && this.portalManager.isInPortal()) {
                this.tickPortalTeleportation();
            }
            if (lv2 == null) {
                if (!this.isAlive() || blockHitResult.getType() == HitResult.Type.MISS) break;
                this.hitOrDeflect(blockHitResult);
                this.velocityDirty = true;
                break;
            }
            if (!this.isAlive() || this.noClip) continue;
            ProjectileDeflection lv4 = this.hitOrDeflect(lv2);
            this.velocityDirty = true;
            if (this.getPierceLevel() > 0 && lv4 == ProjectileDeflection.NONE) continue;
            break;
        }
    }

    private void applyDrag(float drag) {
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.multiply(drag));
    }

    private void spawnBubbleParticles(Vec3d pos) {
        Vec3d lv = this.getVelocity();
        for (int i = 0; i < 4; ++i) {
            float f = 0.25f;
            this.getWorld().addParticle(ParticleTypes.BUBBLE, pos.x - lv.x * 0.25, pos.y - lv.y * 0.25, pos.z - lv.z * 0.25, lv.x, lv.y, lv.z);
        }
    }

    @Override
    protected double getGravity() {
        return 0.05;
    }

    private boolean shouldFall() {
        return this.isInGround() && this.getWorld().isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
    }

    private void fall() {
        this.setInGround(false);
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        this.life = 0;
    }

    protected boolean isInGround() {
        return this.dataTracker.get(IN_GROUND);
    }

    protected void setInGround(boolean inGround) {
        this.dataTracker.set(IN_GROUND, inGround);
    }

    @Override
    public void move(MovementType type, Vec3d movement) {
        super.move(type, movement);
        if (type != MovementType.SELF && this.shouldFall()) {
            this.fall();
        }
    }

    protected void age() {
        ++this.life;
        if (this.life >= 1200) {
            this.discard();
        }
    }

    private void clearPiercingStatus() {
        if (this.piercingKilledEntities != null) {
            this.piercingKilledEntities.clear();
        }
        if (this.piercedEntities != null) {
            this.piercedEntities.clear();
        }
    }

    @Override
    protected void onBroken(Item item) {
        this.weapon = null;
    }

    @Override
    public void onBubbleColumnCollision(boolean drag) {
        if (this.isInGround()) {
            return;
        }
        super.onBubbleColumnCollision(drag);
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        if (this.isInGround()) {
            return;
        }
        super.addVelocity(deltaX, deltaY, deltaZ);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        World world;
        super.onEntityHit(entityHitResult);
        Entity lv = entityHitResult.getEntity();
        float f = (float)this.getVelocity().length();
        double d = this.damage;
        Entity lv2 = this.getOwner();
        DamageSource lv3 = this.getDamageSources().arrow(this, lv2 != null ? lv2 : this);
        if (this.getWeaponStack() != null && (world = this.getWorld()) instanceof ServerWorld) {
            ServerWorld lv4 = (ServerWorld)world;
            d = EnchantmentHelper.getDamage(lv4, this.getWeaponStack(), lv, lv3, (float)d);
        }
        int i = MathHelper.ceil(MathHelper.clamp((double)f * d, 0.0, 2.147483647E9));
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }
            if (this.piercingKilledEntities == null) {
                this.piercingKilledEntities = Lists.newArrayListWithCapacity(5);
            }
            if (this.piercedEntities.size() < this.getPierceLevel() + 1) {
                this.piercedEntities.add(lv.getId());
            } else {
                this.discard();
                return;
            }
        }
        if (this.isCritical()) {
            long l = this.random.nextInt(i / 2 + 2);
            i = (int)Math.min(l + (long)i, Integer.MAX_VALUE);
        }
        if (lv2 instanceof LivingEntity) {
            LivingEntity lv5 = (LivingEntity)lv2;
            lv5.onAttacking(lv);
        }
        boolean bl = lv.getType() == EntityType.ENDERMAN;
        int j = lv.getFireTicks();
        if (this.isOnFire() && !bl) {
            lv.setOnFireFor(5.0f);
        }
        if (lv.sidedDamage(lv3, i)) {
            if (bl) {
                return;
            }
            if (lv instanceof LivingEntity) {
                LivingEntity lv6 = (LivingEntity)lv;
                if (!this.getWorld().isClient && this.getPierceLevel() <= 0) {
                    lv6.setStuckArrowCount(lv6.getStuckArrowCount() + 1);
                }
                this.knockback(lv6, lv3);
                World world2 = this.getWorld();
                if (world2 instanceof ServerWorld) {
                    ServerWorld lv7 = (ServerWorld)world2;
                    EnchantmentHelper.onTargetDamaged(lv7, lv6, lv3, this.getWeaponStack());
                }
                this.onHit(lv6);
                if (lv6 != lv2 && lv6 instanceof PlayerEntity && lv2 instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)lv2).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.field_33328));
                }
                if (!lv.isAlive() && this.piercingKilledEntities != null) {
                    this.piercingKilledEntities.add(lv6);
                }
                if (!this.getWorld().isClient && lv2 instanceof ServerPlayerEntity) {
                    ServerPlayerEntity lv8 = (ServerPlayerEntity)lv2;
                    if (this.piercingKilledEntities != null) {
                        Criteria.KILLED_BY_ARROW.trigger(lv8, this.piercingKilledEntities, this.weapon);
                    } else if (!lv.isAlive()) {
                        Criteria.KILLED_BY_ARROW.trigger(lv8, List.of(lv), this.weapon);
                    }
                }
            }
            this.playSound(this.sound, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            lv.setFireTicks(j);
            this.deflect(ProjectileDeflection.SIMPLE, lv, this.getOwner(), false);
            this.setVelocity(this.getVelocity().multiply(0.2));
            World world3 = this.getWorld();
            if (world3 instanceof ServerWorld) {
                ServerWorld lv9 = (ServerWorld)world3;
                if (this.getVelocity().lengthSquared() < 1.0E-7) {
                    if (this.pickupType == PickupPermission.ALLOWED) {
                        this.dropStack(lv9, this.asItemStack(), 0.1f);
                    }
                    this.discard();
                }
            }
        }
    }

    protected void knockback(LivingEntity target, DamageSource source) {
        float f;
        World world;
        if (this.weapon != null && (world = this.getWorld()) instanceof ServerWorld) {
            ServerWorld lv = (ServerWorld)world;
            f = EnchantmentHelper.modifyKnockback(lv, this.weapon, target, source, 0.0f);
        } else {
            f = 0.0f;
        }
        double d = f;
        if (d > 0.0) {
            double e = Math.max(0.0, 1.0 - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
            Vec3d lv2 = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply(d * 0.6 * e);
            if (lv2.lengthSquared() > 0.0) {
                target.addVelocity(lv2.x, 0.1, lv2.z);
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.inBlockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        super.onBlockHit(blockHitResult);
        ItemStack lv = this.getWeaponStack();
        World world = this.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld lv2 = (ServerWorld)world;
            if (lv != null) {
                this.onBlockHitEnchantmentEffects(lv2, blockHitResult, lv);
            }
        }
        Vec3d lv3 = this.getVelocity();
        Vec3d lv4 = new Vec3d(Math.signum(lv3.x), Math.signum(lv3.y), Math.signum(lv3.z));
        Vec3d lv5 = lv4.multiply(0.05f);
        this.setPosition(this.getPos().subtract(lv5));
        this.setVelocity(Vec3d.ZERO);
        this.playSound(this.getSound(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        this.setInGround(true);
        this.shake = 7;
        this.setCritical(false);
        this.setPierceLevel((byte)0);
        this.setSound(SoundEvents.ENTITY_ARROW_HIT);
        this.clearPiercingStatus();
    }

    protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack) {
        LivingEntity lv2;
        Vec3d lv = blockHitResult.getBlockPos().clampToWithin(blockHitResult.getPos());
        Entity entity = this.getOwner();
        EnchantmentHelper.onHitBlock(world, weaponStack, entity instanceof LivingEntity ? (lv2 = (LivingEntity)entity) : null, this, null, lv, world.getBlockState(blockHitResult.getBlockPos()), item -> {
            this.weapon = null;
        });
    }

    @Override
    public ItemStack getWeaponStack() {
        return this.weapon;
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    protected final SoundEvent getSound() {
        return this.sound;
    }

    protected void onHit(LivingEntity target) {
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
    }

    @Override
    protected boolean canHit(Entity entity) {
        PlayerEntity lv;
        Entity entity2;
        if (entity instanceof PlayerEntity && (entity2 = this.getOwner()) instanceof PlayerEntity && !(lv = (PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
            return false;
        }
        return super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putShort("life", (short)this.life);
        if (this.inBlockState != null) {
            nbt.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
        }
        nbt.putByte("shake", (byte)this.shake);
        nbt.putBoolean("inGround", this.isInGround());
        nbt.putByte("pickup", (byte)this.pickupType.ordinal());
        nbt.putDouble("damage", this.damage);
        nbt.putBoolean("crit", this.isCritical());
        nbt.putByte("PierceLevel", this.getPierceLevel());
        nbt.putString("SoundEvent", Registries.SOUND_EVENT.getId(this.sound).toString());
        nbt.put("item", this.stack.toNbt(this.getRegistryManager()));
        if (this.weapon != null) {
            nbt.put("weapon", this.weapon.toNbt(this.getRegistryManager(), new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.life = nbt.getShort("life");
        if (nbt.contains("inBlockState", NbtElement.COMPOUND_TYPE)) {
            this.inBlockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("inBlockState"));
        }
        this.shake = nbt.getByte("shake") & 0xFF;
        this.setInGround(nbt.getBoolean("inGround"));
        if (nbt.contains("damage", NbtElement.NUMBER_TYPE)) {
            this.damage = nbt.getDouble("damage");
        }
        this.pickupType = PickupPermission.fromOrdinal(nbt.getByte("pickup"));
        this.setCritical(nbt.getBoolean("crit"));
        this.setPierceLevel(nbt.getByte("PierceLevel"));
        if (nbt.contains("SoundEvent", NbtElement.STRING_TYPE)) {
            this.sound = Registries.SOUND_EVENT.getOptionalValue(Identifier.of(nbt.getString("SoundEvent"))).orElse(this.getHitSound());
        }
        if (nbt.contains("item", NbtElement.COMPOUND_TYPE)) {
            this.setStack(ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("item")).orElse(this.getDefaultItemStack()));
        } else {
            this.setStack(this.getDefaultItemStack());
        }
        this.weapon = nbt.contains("weapon", NbtElement.COMPOUND_TYPE) ? (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("weapon")).orElse(null) : null;
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        PickupPermission pickupPermission;
        super.setOwner(entity);
        Entity entity2 = entity;
        int n = 0;
        block4: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PlayerEntity.class, OminousItemSpawnerEntity.class}, (Object)entity2, n)) {
                case 0: {
                    PlayerEntity lv = (PlayerEntity)entity2;
                    if (this.pickupType != PickupPermission.DISALLOWED) {
                        n = 1;
                        continue block4;
                    }
                    pickupPermission = PickupPermission.ALLOWED;
                    break block4;
                }
                case 1: {
                    OminousItemSpawnerEntity lv2 = (OminousItemSpawnerEntity)entity2;
                    pickupPermission = PickupPermission.DISALLOWED;
                    break block4;
                }
                default: {
                    pickupPermission = this.pickupType;
                    break block4;
                }
            }
            break;
        }
        this.pickupType = pickupPermission;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.getWorld().isClient || !this.isInGround() && !this.isNoClip() || this.shake > 0) {
            return;
        }
        if (this.tryPickup(player)) {
            player.sendPickup(this, 1);
            this.discard();
        }
    }

    protected boolean tryPickup(PlayerEntity player) {
        return switch (this.pickupType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> false;
            case 1 -> player.getInventory().insertStack(this.asItemStack());
            case 2 -> player.isInCreativeMode();
        };
    }

    protected ItemStack asItemStack() {
        return this.stack.copy();
    }

    protected abstract ItemStack getDefaultItemStack();

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    @Override
    public boolean isAttackable() {
        return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
    }

    public void setCritical(boolean critical) {
        this.setProjectileFlag(CRITICAL_FLAG, critical);
    }

    private void setPierceLevel(byte level) {
        this.dataTracker.set(PIERCE_LEVEL, level);
    }

    private void setProjectileFlag(int index, boolean flag) {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        if (flag) {
            this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | index));
        } else {
            this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~index));
        }
    }

    protected void setStack(ItemStack stack) {
        this.stack = !stack.isEmpty() ? stack : this.getDefaultItemStack();
    }

    public boolean isCritical() {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        return (b & 1) != 0;
    }

    public byte getPierceLevel() {
        return this.dataTracker.get(PIERCE_LEVEL);
    }

    public void applyDamageModifier(float damageModifier) {
        this.setDamage((double)(damageModifier * 2.0f) + this.random.nextTriangular((double)this.getWorld().getDifficulty().getId() * 0.11, 0.57425));
    }

    protected float getDragInWater() {
        return 0.6f;
    }

    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
        this.setProjectileFlag(NO_CLIP_FLAG, noClip);
    }

    public boolean isNoClip() {
        if (!this.getWorld().isClient) {
            return this.noClip;
        }
        return (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
    }

    @Override
    public boolean canHit() {
        return super.canHit() && !this.isInGround();
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        if (mappedIndex == 0) {
            return StackReference.of(this::getItemStack, this::setStack);
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    protected boolean deflectsAgainstWorldBorder() {
        return true;
    }

    public static enum PickupPermission {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;


        public static PickupPermission fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal > PickupPermission.values().length) {
                ordinal = 0;
            }
            return PickupPermission.values()[ordinal];
        }
    }
}

