/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMinecartEntity
extends VehicleEntity {
    private static final Vec3d VILLAGER_PASSENGER_ATTACHMENT_POS = new Vec3d(0.0, 0.0, 0.0);
    private static final TrackedData<Integer> CUSTOM_BLOCK_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final ImmutableMap<EntityPose, ImmutableList<Integer>> DISMOUNT_FREE_Y_SPACES_NEEDED = ImmutableMap.of(EntityPose.STANDING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(-1)), EntityPose.CROUCHING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(-1)), EntityPose.SWIMMING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1)));
    protected static final float VELOCITY_SLOWDOWN_MULTIPLIER = 0.95f;
    private boolean onRail;
    private boolean yawFlipped;
    private final MinecartController controller;
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> ADJACENT_RAIL_POSITIONS_BY_SHAPE = Util.make(Maps.newEnumMap(RailShape.class), map -> {
        Vec3i lv = Direction.WEST.getVector();
        Vec3i lv2 = Direction.EAST.getVector();
        Vec3i lv3 = Direction.NORTH.getVector();
        Vec3i lv4 = Direction.SOUTH.getVector();
        Vec3i lv5 = lv.down();
        Vec3i lv6 = lv2.down();
        Vec3i lv7 = lv3.down();
        Vec3i lv8 = lv4.down();
        map.put(RailShape.NORTH_SOUTH, Pair.of(lv3, lv4));
        map.put(RailShape.EAST_WEST, Pair.of(lv, lv2));
        map.put(RailShape.ASCENDING_EAST, Pair.of(lv5, lv2));
        map.put(RailShape.ASCENDING_WEST, Pair.of(lv, lv6));
        map.put(RailShape.ASCENDING_NORTH, Pair.of(lv3, lv8));
        map.put(RailShape.ASCENDING_SOUTH, Pair.of(lv7, lv4));
        map.put(RailShape.SOUTH_EAST, Pair.of(lv4, lv2));
        map.put(RailShape.SOUTH_WEST, Pair.of(lv4, lv));
        map.put(RailShape.NORTH_WEST, Pair.of(lv3, lv));
        map.put(RailShape.NORTH_EAST, Pair.of(lv3, lv2));
    });

    protected AbstractMinecartEntity(EntityType<?> arg, World arg2) {
        super(arg, arg2);
        this.intersectionChecked = true;
        this.controller = AbstractMinecartEntity.areMinecartImprovementsEnabled(arg2) ? new ExperimentalMinecartController(this) : new DefaultMinecartController(this);
    }

    protected AbstractMinecartEntity(EntityType<?> type, World world, double x, double y, double z) {
        this(type, world);
        this.initPosition(x, y, z);
    }

    public void initPosition(double x, double y, double z) {
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Nullable
    public static <T extends AbstractMinecartEntity> T create(World world, double x, double y, double z, EntityType<T> type, SpawnReason reason, ItemStack stack, @Nullable PlayerEntity player) {
        AbstractMinecartEntity lv = (AbstractMinecartEntity)type.create(world, reason);
        if (lv != null) {
            lv.initPosition(x, y, z);
            EntityType.copier(world, stack, player).accept(lv);
            MinecartController minecartController = lv.getController();
            if (minecartController instanceof ExperimentalMinecartController) {
                ExperimentalMinecartController lv2 = (ExperimentalMinecartController)minecartController;
                BlockPos lv3 = lv.getRailOrMinecartPos();
                BlockState lv4 = world.getBlockState(lv3);
                lv2.adjustToRail(lv3, lv4, true);
            }
        }
        return (T)lv;
    }

    public MinecartController getController() {
        return this.controller;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.EVENTS;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
        builder.add(CUSTOM_BLOCK_OFFSET, 6);
        builder.add(CUSTOM_BLOCK_PRESENT, false);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return AbstractBoatEntity.canCollide(this, other);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
        return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        boolean bl;
        boolean bl2 = bl = passenger instanceof VillagerEntity || passenger instanceof WanderingTraderEntity;
        if (bl) {
            return VILLAGER_PASSENGER_ATTACHMENT_POS;
        }
        return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Direction lv = this.getMovementDirection();
        if (lv.getAxis() == Direction.Axis.Y) {
            return super.updatePassengerForDismount(passenger);
        }
        int[][] is = Dismounting.getDismountOffsets(lv);
        BlockPos lv2 = this.getBlockPos();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        ImmutableList<EntityPose> immutableList = passenger.getPoses();
        for (EntityPose lv4 : immutableList) {
            EntityDimensions lv5 = passenger.getDimensions(lv4);
            float f = Math.min(lv5.width(), 1.0f) / 2.0f;
            Iterator iterator = DISMOUNT_FREE_Y_SPACES_NEEDED.get((Object)lv4).iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                for (int[] js : is) {
                    lv3.set(lv2.getX() + js[0], lv2.getY() + i, lv2.getZ() + js[1]);
                    double d = this.getWorld().getDismountHeight(Dismounting.getCollisionShape(this.getWorld(), lv3), () -> Dismounting.getCollisionShape(this.getWorld(), (BlockPos)lv3.down()));
                    if (!Dismounting.canDismountInBlock(d)) continue;
                    Box lv6 = new Box(-f, 0.0, -f, f, lv5.height(), f);
                    Vec3d lv7 = Vec3d.ofCenter(lv3, d);
                    if (!Dismounting.canPlaceEntityAt(this.getWorld(), passenger, lv6.offset(lv7))) continue;
                    passenger.setPose(lv4);
                    return lv7;
                }
            }
        }
        double e = this.getBoundingBox().maxY;
        lv3.set((double)lv2.getX(), e, (double)lv2.getZ());
        for (EntityPose lv8 : immutableList) {
            int j;
            double h;
            double g = passenger.getDimensions(lv8).height();
            if (!(e + g <= (h = Dismounting.getCeilingHeight(lv3, j = MathHelper.ceil(e - (double)lv3.getY() + g), pos -> this.getWorld().getBlockState((BlockPos)pos).getCollisionShape(this.getWorld(), (BlockPos)pos))))) continue;
            passenger.setPose(lv8);
            break;
        }
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    protected float getVelocityMultiplier() {
        BlockState lv = this.getWorld().getBlockState(this.getBlockPos());
        if (lv.isIn(BlockTags.RAILS)) {
            return 1.0f;
        }
        return super.getVelocityMultiplier();
    }

    @Override
    public void animateDamage(float yaw) {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + this.getDamageWobbleStrength() * 10.0f);
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    public static Pair<Vec3i, Vec3i> getAdjacentRailPositionsByShape(RailShape shape) {
        return ADJACENT_RAIL_POSITIONS_BY_SHAPE.get(shape);
    }

    @Override
    public Direction getMovementDirection() {
        return this.controller.getHorizontalFacing();
    }

    @Override
    protected double getGravity() {
        return this.isTouchingWater() ? 0.005 : 0.04;
    }

    @Override
    public void tick() {
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }
        this.attemptTickInVoid();
        this.tickPortalTeleportation();
        this.controller.tick();
        this.updateWaterState();
        if (this.isInLava()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5f;
        }
        this.firstUpdate = false;
    }

    public boolean isFirstUpdate() {
        return this.firstUpdate;
    }

    public BlockPos getRailOrMinecartPos() {
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getY());
        int k = MathHelper.floor(this.getZ());
        if (AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getWorld())) {
            double d = this.getY() - 0.1 - (double)1.0E-5f;
            if (this.getWorld().getBlockState(BlockPos.ofFloored(i, d, k)).isIn(BlockTags.RAILS)) {
                j = MathHelper.floor(d);
            }
        } else if (this.getWorld().getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
            --j;
        }
        return new BlockPos(i, j, k);
    }

    protected double getMaxSpeed(ServerWorld world) {
        return this.controller.getMaxSpeed(world);
    }

    public void onActivatorRail(int x, int y, int z, boolean powered) {
    }

    @Override
    public void lerpPosAndRotation(int step, double x, double y, double z, double yaw, double pitch) {
        super.lerpPosAndRotation(step, x, y, z, yaw, pitch);
    }

    @Override
    public void applyGravity() {
        super.applyGravity();
    }

    @Override
    public void refreshPosition() {
        super.refreshPosition();
    }

    @Override
    public boolean updateWaterState() {
        return super.updateWaterState();
    }

    @Override
    public Vec3d getMovement() {
        return this.controller.limitSpeed(super.getMovement());
    }

    @Override
    public void resetLerp() {
        this.controller.resetLerp();
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.controller.setPos(x, y, z, yaw, pitch, interpolationSteps);
    }

    @Override
    public double getLerpTargetX() {
        return this.controller.getLerpTargetX();
    }

    @Override
    public double getLerpTargetY() {
        return this.controller.getLerpTargetY();
    }

    @Override
    public double getLerpTargetZ() {
        return this.controller.getLerpTargetZ();
    }

    @Override
    public float getLerpTargetPitch() {
        return this.controller.getLerpTargetPitch();
    }

    @Override
    public float getLerpTargetYaw() {
        return this.controller.getLerpTargetYaw();
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        this.controller.setLerpTargetVelocity(x, y, z);
    }

    protected void moveOnRail(ServerWorld world) {
        this.controller.moveOnRail(world);
    }

    protected void moveOffRail(ServerWorld world) {
        double d = this.getMaxSpeed(world);
        Vec3d lv = this.getVelocity();
        this.setVelocity(MathHelper.clamp(lv.x, -d, d), lv.y, MathHelper.clamp(lv.z, -d, d));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.5));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (!this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.95));
        }
    }

    protected double moveAlongTrack(BlockPos pos, RailShape shape, double remainingMovement) {
        return this.controller.moveAlongTrack(pos, shape, remainingMovement);
    }

    @Override
    public void move(MovementType type, Vec3d movement) {
        if (AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getWorld())) {
            Vec3d lv = this.getPos().add(movement);
            super.move(type, movement);
            boolean bl = this.controller.handleCollision();
            if (bl) {
                super.move(type, lv.subtract(this.getPos()));
            }
            if (type.equals((Object)MovementType.PISTON)) {
                this.onRail = false;
            }
        } else {
            super.move(type, movement);
            this.tickBlockCollision();
        }
    }

    @Override
    public void tickBlockCollision() {
        if (!AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getWorld())) {
            this.tickBlockCollision(this.getPos(), this.getPos());
        } else {
            super.tickBlockCollision();
        }
    }

    @Override
    public boolean isOnRail() {
        return this.onRail;
    }

    public void setOnRail(boolean onRail) {
        this.onRail = onRail;
    }

    public boolean isYawFlipped() {
        return this.yawFlipped;
    }

    public void setYawFlipped(boolean yawFlipped) {
        this.yawFlipped = yawFlipped;
    }

    public Vec3d getLaunchDirection(BlockPos railPos) {
        BlockState lv = this.getWorld().getBlockState(railPos);
        if (!lv.isOf(Blocks.POWERED_RAIL) || !lv.get(PoweredRailBlock.POWERED).booleanValue()) {
            return Vec3d.ZERO;
        }
        RailShape lv2 = lv.get(((AbstractRailBlock)lv.getBlock()).getShapeProperty());
        if (lv2 == RailShape.EAST_WEST) {
            if (this.willHitBlockAt(railPos.west())) {
                return new Vec3d(1.0, 0.0, 0.0);
            }
            if (this.willHitBlockAt(railPos.east())) {
                return new Vec3d(-1.0, 0.0, 0.0);
            }
        } else if (lv2 == RailShape.NORTH_SOUTH) {
            if (this.willHitBlockAt(railPos.north())) {
                return new Vec3d(0.0, 0.0, 1.0);
            }
            if (this.willHitBlockAt(railPos.south())) {
                return new Vec3d(0.0, 0.0, -1.0);
            }
        }
        return Vec3d.ZERO;
    }

    public boolean willHitBlockAt(BlockPos pos) {
        return this.getWorld().getBlockState(pos).isSolidBlock(this.getWorld(), pos);
    }

    protected Vec3d applySlowdown(Vec3d velocity) {
        double d = this.controller.getSpeedRetention();
        Vec3d lv = velocity.multiply(d, 0.0, d);
        if (this.isTouchingWater()) {
            lv = lv.multiply(0.95f);
        }
        return lv;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.getBoolean("CustomDisplayTile")) {
            this.setCustomBlock(NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("DisplayState")));
            this.setCustomBlockOffset(nbt.getInt("DisplayOffset"));
        }
        this.yawFlipped = nbt.getBoolean("FlippedRotation");
        this.firstUpdate = nbt.getBoolean("HasTicked");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.hasCustomBlock()) {
            nbt.putBoolean("CustomDisplayTile", true);
            nbt.put("DisplayState", NbtHelper.fromBlockState(this.getContainedBlock()));
            nbt.putInt("DisplayOffset", this.getBlockOffset());
        }
        nbt.putBoolean("FlippedRotation", this.yawFlipped);
        nbt.putBoolean("HasTicked", this.firstUpdate);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        double e;
        if (this.getWorld().isClient) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        if (this.hasPassenger(entity)) {
            return;
        }
        double d = entity.getX() - this.getX();
        double f = d * d + (e = entity.getZ() - this.getZ()) * e;
        if (f >= (double)1.0E-4f) {
            f = Math.sqrt(f);
            d /= f;
            e /= f;
            double g = 1.0 / f;
            if (g > 1.0) {
                g = 1.0;
            }
            d *= g;
            e *= g;
            d *= (double)0.1f;
            e *= (double)0.1f;
            d *= 0.5;
            e *= 0.5;
            if (entity instanceof AbstractMinecartEntity) {
                AbstractMinecartEntity lv = (AbstractMinecartEntity)entity;
                this.pushAwayFromMinecart(lv, d, e);
            } else {
                this.addVelocity(-d, 0.0, -e);
                entity.addVelocity(d / 4.0, 0.0, e / 4.0);
            }
        }
    }

    private void pushAwayFromMinecart(AbstractMinecartEntity entity, double xDiff, double zDiff) {
        double g;
        double f;
        if (AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getWorld())) {
            f = this.getVelocity().x;
            g = this.getVelocity().z;
        } else {
            f = entity.getX() - this.getX();
            g = entity.getZ() - this.getZ();
        }
        Vec3d lv = new Vec3d(f, 0.0, g).normalize();
        Vec3d lv2 = new Vec3d(MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)), 0.0, MathHelper.sin(this.getYaw() * ((float)Math.PI / 180))).normalize();
        double h = Math.abs(lv.dotProduct(lv2));
        if (h < (double)0.8f && !AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getWorld())) {
            return;
        }
        Vec3d lv3 = this.getVelocity();
        Vec3d lv4 = entity.getVelocity();
        if (entity.isSelfPropelling() && !this.isSelfPropelling()) {
            this.setVelocity(lv3.multiply(0.2, 1.0, 0.2));
            this.addVelocity(lv4.x - xDiff, 0.0, lv4.z - zDiff);
            entity.setVelocity(lv4.multiply(0.95, 1.0, 0.95));
        } else if (!entity.isSelfPropelling() && this.isSelfPropelling()) {
            entity.setVelocity(lv4.multiply(0.2, 1.0, 0.2));
            entity.addVelocity(lv3.x + xDiff, 0.0, lv3.z + zDiff);
            this.setVelocity(lv3.multiply(0.95, 1.0, 0.95));
        } else {
            double i = (lv4.x + lv3.x) / 2.0;
            double j = (lv4.z + lv3.z) / 2.0;
            this.setVelocity(lv3.multiply(0.2, 1.0, 0.2));
            this.addVelocity(i - xDiff, 0.0, j - zDiff);
            entity.setVelocity(lv4.multiply(0.2, 1.0, 0.2));
            entity.addVelocity(i + xDiff, 0.0, j + zDiff);
        }
    }

    public BlockState getContainedBlock() {
        if (!this.hasCustomBlock()) {
            return this.getDefaultContainedBlock();
        }
        return Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
    }

    public BlockState getDefaultContainedBlock() {
        return Blocks.AIR.getDefaultState();
    }

    public int getBlockOffset() {
        if (!this.hasCustomBlock()) {
            return this.getDefaultBlockOffset();
        }
        return this.getDataTracker().get(CUSTOM_BLOCK_OFFSET);
    }

    public int getDefaultBlockOffset() {
        return 6;
    }

    public void setCustomBlock(BlockState state) {
        this.getDataTracker().set(CUSTOM_BLOCK_ID, Block.getRawIdFromState(state));
        this.setCustomBlockPresent(true);
    }

    public void setCustomBlockOffset(int offset) {
        this.getDataTracker().set(CUSTOM_BLOCK_OFFSET, offset);
        this.setCustomBlockPresent(true);
    }

    public boolean hasCustomBlock() {
        return this.getDataTracker().get(CUSTOM_BLOCK_PRESENT);
    }

    public void setCustomBlockPresent(boolean present) {
        this.getDataTracker().set(CUSTOM_BLOCK_PRESENT, present);
    }

    public static boolean areMinecartImprovementsEnabled(World world) {
        return world.getEnabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS);
    }

    @Override
    public abstract ItemStack getPickBlockStack();

    public boolean isRideable() {
        return false;
    }

    public boolean isSelfPropelling() {
        return false;
    }
}

