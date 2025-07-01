/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.vehicle;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExperimentalMinecartController
extends MinecartController {
    public static final int REFRESH_FREQUENCY = 3;
    public static final double field_52528 = 0.1;
    public static final double field_53756 = 0.005;
    @Nullable
    private InterpolatedStep lastReturnedInterpolatedStep;
    private int lastQueriedTicksToNextRefresh;
    private float lastQueriedTickDelta;
    private int ticksToNextRefresh = 0;
    public final List<Step> stagingLerpSteps = new LinkedList<Step>();
    public final List<Step> currentLerpSteps = new LinkedList<Step>();
    public double totalWeight = 0.0;
    public Step initialStep = Step.ZERO;

    public ExperimentalMinecartController(AbstractMinecartEntity arg) {
        super(arg);
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if (!(world instanceof ServerWorld)) {
            this.tickClient();
            boolean bl = AbstractRailBlock.isRail(this.getWorld().getBlockState(this.minecart.getRailOrMinecartPos()));
            this.minecart.setOnRail(bl);
            return;
        }
        ServerWorld lv = (ServerWorld)world;
        BlockPos lv2 = this.minecart.getRailOrMinecartPos();
        BlockState lv3 = this.getWorld().getBlockState(lv2);
        if (this.minecart.isFirstUpdate()) {
            this.minecart.setOnRail(AbstractRailBlock.isRail(lv3));
            this.adjustToRail(lv2, lv3, true);
        }
        this.minecart.applyGravity();
        this.minecart.moveOnRail(lv);
    }

    private void tickClient() {
        if (--this.ticksToNextRefresh <= 0) {
            this.setInitialStep();
            this.currentLerpSteps.clear();
            if (!this.stagingLerpSteps.isEmpty()) {
                this.currentLerpSteps.addAll(this.stagingLerpSteps);
                this.stagingLerpSteps.clear();
                this.totalWeight = 0.0;
                for (Step lv : this.currentLerpSteps) {
                    this.totalWeight += (double)lv.weight;
                }
                int n = this.ticksToNextRefresh = this.totalWeight == 0.0 ? 0 : 3;
            }
        }
        if (this.hasCurrentLerpSteps()) {
            this.setPos(this.getLerpedPosition(1.0f));
            this.setVelocity(this.getLerpedVelocity(1.0f));
            this.setPitch(this.getLerpedPitch(1.0f));
            this.setYaw(this.getLerpedYaw(1.0f));
        }
    }

    public void setInitialStep() {
        this.initialStep = new Step(this.getPos(), this.getVelocity(), this.getYaw(), this.getPitch(), 0.0f);
    }

    public boolean hasCurrentLerpSteps() {
        return !this.currentLerpSteps.isEmpty();
    }

    public float getLerpedPitch(float tickDelta) {
        InterpolatedStep lv = this.getLerpedStep(tickDelta);
        return MathHelper.lerpAngleDegrees(lv.partialTicksInStep, lv.previousStep.xRot, lv.currentStep.xRot);
    }

    public float getLerpedYaw(float tickDelta) {
        InterpolatedStep lv = this.getLerpedStep(tickDelta);
        return MathHelper.lerpAngleDegrees(lv.partialTicksInStep, lv.previousStep.yRot, lv.currentStep.yRot);
    }

    public Vec3d getLerpedPosition(float tickDelta) {
        InterpolatedStep lv = this.getLerpedStep(tickDelta);
        return MathHelper.lerp((double)lv.partialTicksInStep, lv.previousStep.position, lv.currentStep.position);
    }

    public Vec3d getLerpedVelocity(float tickDelta) {
        InterpolatedStep lv = this.getLerpedStep(tickDelta);
        return MathHelper.lerp((double)lv.partialTicksInStep, lv.previousStep.movement, lv.currentStep.movement);
    }

    private InterpolatedStep getLerpedStep(float tickDelta) {
        int j;
        if (tickDelta == this.lastQueriedTickDelta && this.ticksToNextRefresh == this.lastQueriedTicksToNextRefresh && this.lastReturnedInterpolatedStep != null) {
            return this.lastReturnedInterpolatedStep;
        }
        float g = ((float)(3 - this.ticksToNextRefresh) + tickDelta) / 3.0f;
        float h = 0.0f;
        float i = 1.0f;
        boolean bl = false;
        for (j = 0; j < this.currentLerpSteps.size(); ++j) {
            float k = this.currentLerpSteps.get((int)j).weight;
            if (k <= 0.0f || !((double)(h += k) >= this.totalWeight * (double)g)) continue;
            float l = h - k;
            i = (float)(((double)g * this.totalWeight - (double)l) / (double)k);
            bl = true;
            break;
        }
        if (!bl) {
            j = this.currentLerpSteps.size() - 1;
        }
        Step lv = this.currentLerpSteps.get(j);
        Step lv2 = j > 0 ? this.currentLerpSteps.get(j - 1) : this.initialStep;
        this.lastReturnedInterpolatedStep = new InterpolatedStep(i, lv, lv2);
        this.lastQueriedTicksToNextRefresh = this.ticksToNextRefresh;
        this.lastQueriedTickDelta = tickDelta;
        return this.lastReturnedInterpolatedStep;
    }

    public void adjustToRail(BlockPos pos, BlockState blockState, boolean ignoreWeight) {
        boolean bl5;
        Vec3d lv11;
        Vec3d lv8;
        boolean bl2;
        if (!AbstractRailBlock.isRail(blockState)) {
            return;
        }
        RailShape lv = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
        Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(lv);
        Vec3d lv2 = new Vec3d(pair.getFirst()).multiply(0.5);
        Vec3d lv3 = new Vec3d(pair.getSecond()).multiply(0.5);
        Vec3d lv4 = lv2.getHorizontal();
        Vec3d lv5 = lv3.getHorizontal();
        if (this.getVelocity().length() > (double)1.0E-5f && this.getVelocity().dotProduct(lv4) < this.getVelocity().dotProduct(lv5) || this.ascends(lv5, lv)) {
            Vec3d lv6 = lv4;
            lv4 = lv5;
            lv5 = lv6;
        }
        float f = 180.0f - (float)(Math.atan2(lv4.z, lv4.x) * 180.0 / Math.PI);
        f += this.minecart.isYawFlipped() ? 180.0f : 0.0f;
        Vec3d lv7 = this.getPos();
        boolean bl = bl2 = lv2.getX() != lv3.getX() && lv2.getZ() != lv3.getZ();
        if (bl2) {
            lv8 = lv3.subtract(lv2);
            Vec3d lv9 = lv7.subtract(pos.toBottomCenterPos()).subtract(lv2);
            Vec3d lv10 = lv8.multiply(lv8.dotProduct(lv9) / lv8.dotProduct(lv8));
            lv11 = pos.toBottomCenterPos().add(lv2).add(lv10);
            f = 180.0f - (float)(Math.atan2(lv10.z, lv10.x) * 180.0 / Math.PI);
            f += this.minecart.isYawFlipped() ? 180.0f : 0.0f;
        } else {
            boolean bl3 = lv2.subtract((Vec3d)lv3).x != 0.0;
            boolean bl4 = lv2.subtract((Vec3d)lv3).z != 0.0;
            lv11 = new Vec3d(bl4 ? pos.toCenterPos().x : lv7.x, pos.getY(), bl3 ? pos.toCenterPos().z : lv7.z);
        }
        lv8 = lv11.subtract(lv7);
        this.setPos(lv7.add(lv8));
        float g = 0.0f;
        boolean bl3 = bl5 = lv2.getY() != lv3.getY();
        if (bl5) {
            Vec3d lv12 = pos.toBottomCenterPos().add(lv5);
            double d = lv12.distanceTo(this.getPos());
            this.setPos(this.getPos().add(0.0, d + 0.1, 0.0));
            g = this.minecart.isYawFlipped() ? 45.0f : -45.0f;
        } else {
            this.setPos(this.getPos().add(0.0, 0.1, 0.0));
        }
        this.setAngles(f, g);
        double e = lv7.distanceTo(this.getPos());
        if (e > 0.0) {
            this.stagingLerpSteps.add(new Step(this.getPos(), this.getVelocity(), this.getYaw(), this.getPitch(), ignoreWeight ? 0.0f : (float)e));
        }
    }

    private void setAngles(float yaw, float pitch) {
        double d = Math.abs(yaw - this.getYaw());
        if (d >= 175.0 && d <= 185.0) {
            this.minecart.setYawFlipped(!this.minecart.isYawFlipped());
            yaw -= 180.0f;
            pitch *= -1.0f;
        }
        pitch = Math.clamp(pitch, -45.0f, 45.0f);
        this.setPitch(pitch % 360.0f);
        this.setYaw(yaw % 360.0f);
    }

    @Override
    public void moveOnRail(ServerWorld world) {
        MoveIteration lv = new MoveIteration();
        while (lv.shouldContinue() && this.minecart.isAlive()) {
            Vec3d lv6;
            Vec3d lv2 = this.getVelocity();
            BlockPos lv3 = this.minecart.getRailOrMinecartPos();
            BlockState lv4 = this.getWorld().getBlockState(lv3);
            boolean bl = AbstractRailBlock.isRail(lv4);
            if (this.minecart.isOnRail() != bl) {
                this.minecart.setOnRail(bl);
                this.adjustToRail(lv3, lv4, false);
            }
            if (bl) {
                this.minecart.onLanding();
                this.minecart.resetPosition();
                if (lv4.isOf(Blocks.ACTIVATOR_RAIL)) {
                    this.minecart.onActivatorRail(lv3.getX(), lv3.getY(), lv3.getZ(), lv4.get(PoweredRailBlock.POWERED));
                }
                RailShape lv5 = lv4.get(((AbstractRailBlock)lv4.getBlock()).getShapeProperty());
                lv6 = this.calcNewHorizontalVelocity(world, lv2.getHorizontal(), lv, lv3, lv4, lv5);
                lv.remainingMovement = lv.initial ? lv6.horizontalLength() : (lv.remainingMovement += lv6.horizontalLength() - lv2.horizontalLength());
                this.setVelocity(lv6);
                lv.remainingMovement = this.minecart.moveAlongTrack(lv3, lv5, lv.remainingMovement);
            } else {
                this.minecart.moveOffRail(world);
                lv.remainingMovement = 0.0;
            }
            Vec3d lv7 = this.getPos();
            lv6 = lv7.subtract(this.minecart.getLastRenderPos());
            double d = lv6.length();
            if (d > (double)1.0E-5f) {
                if (lv6.horizontalLengthSquared() > (double)1.0E-5f) {
                    float f = 180.0f - (float)(Math.atan2(lv6.z, lv6.x) * 180.0 / Math.PI);
                    float g = this.minecart.isOnGround() && !this.minecart.isOnRail() ? 0.0f : 90.0f - (float)(Math.atan2(lv6.horizontalLength(), lv6.y) * 180.0 / Math.PI);
                    this.setAngles(f += this.minecart.isYawFlipped() ? 180.0f : 0.0f, g *= this.minecart.isYawFlipped() ? -1.0f : 1.0f);
                } else if (!this.minecart.isOnRail()) {
                    this.setPitch(this.minecart.isOnGround() ? 0.0f : MathHelper.lerpAngleDegrees(0.2f, this.getPitch(), 0.0f));
                }
                this.stagingLerpSteps.add(new Step(lv7, this.getVelocity(), this.getYaw(), this.getPitch(), (float)Math.min(d, this.getMaxSpeed(world))));
            } else if (lv2.horizontalLengthSquared() > 0.0) {
                this.stagingLerpSteps.add(new Step(lv7, this.getVelocity(), this.getYaw(), this.getPitch(), 1.0f));
            }
            if (d > (double)1.0E-5f || lv.initial) {
                this.minecart.tickBlockCollision();
                this.minecart.tickBlockCollision();
            }
            lv.initial = false;
        }
    }

    private Vec3d calcNewHorizontalVelocity(ServerWorld world, Vec3d horizontalVelocity, MoveIteration iteration, BlockPos pos, BlockState railState, RailShape railShape) {
        Vec3d lv2;
        Vec3d lv22;
        Vec3d lv = horizontalVelocity;
        if (!iteration.slopeVelocityApplied && (lv22 = this.applySlopeVelocity(lv, railShape)).horizontalLengthSquared() != lv.horizontalLengthSquared()) {
            iteration.slopeVelocityApplied = true;
            lv = lv22;
        }
        if (iteration.initial && (lv22 = this.applyInitialVelocity(lv)).horizontalLengthSquared() != lv.horizontalLengthSquared()) {
            iteration.decelerated = true;
            lv = lv22;
        }
        if (!iteration.decelerated && (lv22 = this.decelerateFromPoweredRail(lv, railState)).horizontalLengthSquared() != lv.horizontalLengthSquared()) {
            iteration.decelerated = true;
            lv = lv22;
        }
        if (iteration.initial && (lv = this.minecart.applySlowdown(lv)).lengthSquared() > 0.0) {
            double d = Math.min(lv.length(), this.minecart.getMaxSpeed(world));
            lv = lv.normalize().multiply(d);
        }
        if (!iteration.accelerated && (lv2 = this.accelerateFromPoweredRail(lv, pos, railState)).horizontalLengthSquared() != lv.horizontalLengthSquared()) {
            iteration.accelerated = true;
            lv = lv2;
        }
        return lv;
    }

    private Vec3d applySlopeVelocity(Vec3d horizontalVelocity, RailShape railShape) {
        double d = Math.max(0.0078125, horizontalVelocity.horizontalLength() * 0.02);
        if (this.minecart.isTouchingWater()) {
            d *= 0.2;
        }
        return switch (railShape) {
            case RailShape.ASCENDING_EAST -> horizontalVelocity.add(-d, 0.0, 0.0);
            case RailShape.ASCENDING_WEST -> horizontalVelocity.add(d, 0.0, 0.0);
            case RailShape.ASCENDING_NORTH -> horizontalVelocity.add(0.0, 0.0, d);
            case RailShape.ASCENDING_SOUTH -> horizontalVelocity.add(0.0, 0.0, -d);
            default -> horizontalVelocity;
        };
    }

    private Vec3d applyInitialVelocity(Vec3d horizontalVelocity) {
        Entity entity = this.minecart.getFirstPassenger();
        if (!(entity instanceof ServerPlayerEntity)) {
            return horizontalVelocity;
        }
        ServerPlayerEntity lv = (ServerPlayerEntity)entity;
        Vec3d lv2 = lv.getInputVelocityForMinecart();
        if (lv2.lengthSquared() > 0.0) {
            Vec3d lv3 = lv2.normalize();
            double d = horizontalVelocity.horizontalLengthSquared();
            if (lv3.lengthSquared() > 0.0 && d < 0.01) {
                return horizontalVelocity.add(new Vec3d(lv3.x, 0.0, lv3.z).normalize().multiply(0.001));
            }
        }
        return horizontalVelocity;
    }

    private Vec3d decelerateFromPoweredRail(Vec3d velocity, BlockState railState) {
        if (!railState.isOf(Blocks.POWERED_RAIL) || railState.get(PoweredRailBlock.POWERED).booleanValue()) {
            return velocity;
        }
        if (velocity.length() < 0.03) {
            return Vec3d.ZERO;
        }
        return velocity.multiply(0.5);
    }

    private Vec3d accelerateFromPoweredRail(Vec3d velocity, BlockPos railPos, BlockState railState) {
        if (!railState.isOf(Blocks.POWERED_RAIL) || !railState.get(PoweredRailBlock.POWERED).booleanValue()) {
            return velocity;
        }
        if (velocity.length() > 0.01) {
            return velocity.normalize().multiply(velocity.length() + 0.06);
        }
        Vec3d lv = this.minecart.getLaunchDirection(railPos);
        if (lv.lengthSquared() <= 0.0) {
            return velocity;
        }
        return lv.multiply(velocity.length() + 0.2);
    }

    @Override
    public double moveAlongTrack(BlockPos blockPos, RailShape railShape, double remainingMovement) {
        if (remainingMovement < (double)1.0E-5f) {
            return 0.0;
        }
        Vec3d lv = this.getPos();
        Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
        Vec3i lv2 = pair.getFirst();
        Vec3i lv3 = pair.getSecond();
        Vec3d lv4 = this.getVelocity().getHorizontal();
        if (lv4.length() < (double)1.0E-5f) {
            this.setVelocity(Vec3d.ZERO);
            return 0.0;
        }
        boolean bl = lv2.getY() != lv3.getY();
        Vec3d lv5 = new Vec3d(lv3).multiply(0.5).getHorizontal();
        Vec3d lv6 = new Vec3d(lv2).multiply(0.5).getHorizontal();
        if (lv4.dotProduct(lv6) < lv4.dotProduct(lv5)) {
            lv6 = lv5;
        }
        Vec3d lv7 = blockPos.toBottomCenterPos().add(lv6).add(0.0, 0.1, 0.0).add(lv6.normalize().multiply(1.0E-5f));
        if (bl && !this.ascends(lv4, railShape)) {
            lv7 = lv7.add(0.0, 1.0, 0.0);
        }
        Vec3d lv8 = lv7.subtract(this.getPos()).normalize();
        lv4 = lv8.multiply(lv4.length() / lv8.horizontalLength());
        Vec3d lv9 = lv.add(lv4.normalize().multiply(remainingMovement * (double)(bl ? MathHelper.SQUARE_ROOT_OF_TWO : 1.0f)));
        if (lv.squaredDistanceTo(lv7) <= lv.squaredDistanceTo(lv9)) {
            remainingMovement = lv7.subtract(lv9).horizontalLength();
            lv9 = lv7;
        } else {
            remainingMovement = 0.0;
        }
        this.minecart.move(MovementType.SELF, lv9.subtract(lv));
        BlockState lv10 = this.getWorld().getBlockState(BlockPos.ofFloored(lv9));
        if (bl) {
            RailShape lv11;
            if (AbstractRailBlock.isRail(lv10) && this.restOnVShapedTrack(railShape, lv11 = lv10.get(((AbstractRailBlock)lv10.getBlock()).getShapeProperty()))) {
                return 0.0;
            }
            double e = lv7.getHorizontal().distanceTo(this.getPos().getHorizontal());
            double f = lv7.y + (this.ascends(lv4, railShape) ? e : -e);
            if (this.getPos().y < f) {
                this.setPos(this.getPos().x, f, this.getPos().z);
            }
        }
        if (this.getPos().distanceTo(lv) < (double)1.0E-5f && lv9.distanceTo(lv) > (double)1.0E-5f) {
            this.setVelocity(Vec3d.ZERO);
            return 0.0;
        }
        this.setVelocity(lv4);
        return remainingMovement;
    }

    private boolean restOnVShapedTrack(RailShape currentRailShape, RailShape newRailShape) {
        if (this.getVelocity().lengthSquared() < 0.005 && newRailShape.isAscending() && this.ascends(this.getVelocity(), currentRailShape) && !this.ascends(this.getVelocity(), newRailShape)) {
            this.setVelocity(Vec3d.ZERO);
            return true;
        }
        return false;
    }

    @Override
    public double getMaxSpeed(ServerWorld world) {
        return (double)world.getGameRules().getInt(GameRules.MINECART_MAX_SPEED) * (this.minecart.isTouchingWater() ? 0.5 : 1.0) / 20.0;
    }

    private boolean ascends(Vec3d velocity, RailShape railShape) {
        return switch (railShape) {
            case RailShape.ASCENDING_EAST -> {
                if (velocity.x < 0.0) {
                    yield true;
                }
                yield false;
            }
            case RailShape.ASCENDING_WEST -> {
                if (velocity.x > 0.0) {
                    yield true;
                }
                yield false;
            }
            case RailShape.ASCENDING_NORTH -> {
                if (velocity.z > 0.0) {
                    yield true;
                }
                yield false;
            }
            case RailShape.ASCENDING_SOUTH -> {
                if (velocity.z < 0.0) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @Override
    public double getSpeedRetention() {
        return this.minecart.hasPassengers() ? 0.997 : 0.975;
    }

    @Override
    public boolean handleCollision() {
        boolean bl = this.pickUpEntities(this.minecart.getBoundingBox().expand(0.2, 0.0, 0.2));
        if (this.minecart.horizontalCollision || this.minecart.verticalCollision) {
            boolean bl2 = this.pushAwayFromEntities(this.minecart.getBoundingBox().expand(1.0E-7));
            return bl && !bl2;
        }
        return false;
    }

    public boolean pickUpEntities(Box box) {
        List<Entity> list;
        if (this.minecart.isRideable() && !this.minecart.hasPassengers() && !(list = this.getWorld().getOtherEntities(this.minecart, box, EntityPredicates.canBePushedBy(this.minecart))).isEmpty()) {
            for (Entity lv : list) {
                boolean bl;
                if (lv instanceof PlayerEntity || lv instanceof IronGolemEntity || lv instanceof AbstractMinecartEntity || this.minecart.hasPassengers() || lv.hasVehicle() || !(bl = lv.startRiding(this.minecart))) continue;
                return true;
            }
        }
        return false;
    }

    public boolean pushAwayFromEntities(Box box) {
        boolean bl;
        block3: {
            block2: {
                bl = false;
                if (!this.minecart.isRideable()) break block2;
                List<Entity> list = this.getWorld().getOtherEntities(this.minecart, box, EntityPredicates.canBePushedBy(this.minecart));
                if (list.isEmpty()) break block3;
                for (Entity lv : list) {
                    if (!(lv instanceof PlayerEntity) && !(lv instanceof IronGolemEntity) && !(lv instanceof AbstractMinecartEntity) && !this.minecart.hasPassengers() && !lv.hasVehicle()) continue;
                    lv.pushAwayFrom(this.minecart);
                    bl = true;
                }
                break block3;
            }
            for (Entity lv2 : this.getWorld().getOtherEntities(this.minecart, box)) {
                if (this.minecart.hasPassenger(lv2) || !lv2.isPushable() || !(lv2 instanceof AbstractMinecartEntity)) continue;
                lv2.pushAwayFrom(this.minecart);
                bl = true;
            }
        }
        return bl;
    }

    public record Step(Vec3d position, Vec3d movement, float yRot, float xRot, float weight) {
        public static final PacketCodec<ByteBuf, Step> PACKET_CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, Step::position, Vec3d.PACKET_CODEC, Step::movement, PacketCodecs.DEGREES, Step::yRot, PacketCodecs.DEGREES, Step::xRot, PacketCodecs.FLOAT, Step::weight, Step::new);
        public static Step ZERO = new Step(Vec3d.ZERO, Vec3d.ZERO, 0.0f, 0.0f, 0.0f);
    }

    record InterpolatedStep(float partialTicksInStep, Step currentStep, Step previousStep) {
    }

    static class MoveIteration {
        double remainingMovement = 0.0;
        boolean initial = true;
        boolean slopeVelocityApplied = false;
        boolean decelerated = false;
        boolean accelerated = false;

        MoveIteration() {
        }

        public boolean shouldContinue() {
            return this.initial || this.remainingMovement > (double)1.0E-5f;
        }
    }
}

