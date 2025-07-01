/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.vehicle;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DefaultMinecartController
extends MinecartController {
    private static final double field_52547 = 0.01;
    private static final double field_54466 = 0.2;
    private static final double field_54467 = 0.4;
    private static final double field_54468 = 0.4;
    private int step;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;
    private Vec3d velocity = Vec3d.ZERO;

    public DefaultMinecartController(AbstractMinecartEntity arg) {
        super(arg);
    }

    @Override
    public void resetLerp() {
        this.step = 0;
    }

    @Override
    public void setPos(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.step = interpolationSteps + 2;
        this.setVelocity(this.velocity);
    }

    @Override
    public double getLerpTargetX() {
        return this.step > 0 ? this.x : this.minecart.getX();
    }

    @Override
    public double getLerpTargetY() {
        return this.step > 0 ? this.y : this.minecart.getY();
    }

    @Override
    public double getLerpTargetZ() {
        return this.step > 0 ? this.z : this.minecart.getZ();
    }

    @Override
    public float getLerpTargetPitch() {
        return this.step > 0 ? (float)this.pitch : this.getPitch();
    }

    @Override
    public float getLerpTargetYaw() {
        return this.step > 0 ? (float)this.yaw : this.getYaw();
    }

    @Override
    public void setLerpTargetVelocity(double x, double y, double z) {
        this.velocity = new Vec3d(x, y, z);
        this.setVelocity(this.velocity);
    }

    @Override
    public void tick() {
        double f;
        World world = this.getWorld();
        if (!(world instanceof ServerWorld)) {
            if (this.step > 0) {
                this.minecart.lerpPosAndRotation(this.step, this.x, this.y, this.z, this.yaw, this.pitch);
                --this.step;
            } else {
                this.minecart.refreshPosition();
                this.setPitch(this.getPitch() % 360.0f);
                this.setYaw(this.getYaw() % 360.0f);
            }
            return;
        }
        ServerWorld lv = (ServerWorld)world;
        this.minecart.applyGravity();
        BlockPos lv2 = this.minecart.getRailOrMinecartPos();
        BlockState lv3 = this.getWorld().getBlockState(lv2);
        boolean bl = AbstractRailBlock.isRail(lv3);
        this.minecart.setOnRail(bl);
        if (bl) {
            this.moveOnRail(lv);
            if (lv3.isOf(Blocks.ACTIVATOR_RAIL)) {
                this.minecart.onActivatorRail(lv2.getX(), lv2.getY(), lv2.getZ(), lv3.get(PoweredRailBlock.POWERED));
            }
        } else {
            this.minecart.moveOffRail(lv);
        }
        this.minecart.tickBlockCollision();
        this.setPitch(0.0f);
        double d = this.minecart.prevX - this.getX();
        double e = this.minecart.prevZ - this.getZ();
        if (d * d + e * e > 0.001) {
            this.setYaw((float)(MathHelper.atan2(e, d) * 180.0 / Math.PI));
            if (this.minecart.isYawFlipped()) {
                this.setYaw(this.getYaw() + 180.0f);
            }
        }
        if ((f = (double)MathHelper.wrapDegrees(this.getYaw() - this.minecart.prevYaw)) < -170.0 || f >= 170.0) {
            this.setYaw(this.getYaw() + 180.0f);
            this.minecart.setYawFlipped(!this.minecart.isYawFlipped());
        }
        this.setPitch(this.getPitch() % 360.0f);
        this.setYaw(this.getYaw() % 360.0f);
        this.handleCollision();
    }

    @Override
    public void moveOnRail(ServerWorld world) {
        double v;
        Vec3d lv13;
        double t;
        double s;
        double r;
        Vec3d lv10;
        BlockPos lv = this.minecart.getRailOrMinecartPos();
        BlockState lv2 = this.getWorld().getBlockState(lv);
        this.minecart.onLanding();
        double d = this.minecart.getX();
        double e = this.minecart.getY();
        double f = this.minecart.getZ();
        Vec3d lv3 = this.snapPositionToRail(d, e, f);
        e = lv.getY();
        boolean bl = false;
        boolean bl2 = false;
        if (lv2.isOf(Blocks.POWERED_RAIL)) {
            bl = lv2.get(PoweredRailBlock.POWERED);
            bl2 = !bl;
        }
        double g = 0.0078125;
        if (this.minecart.isTouchingWater()) {
            g *= 0.2;
        }
        Vec3d lv4 = this.getVelocity();
        RailShape lv5 = lv2.get(((AbstractRailBlock)lv2.getBlock()).getShapeProperty());
        switch (lv5) {
            case ASCENDING_EAST: {
                this.setVelocity(lv4.add(-g, 0.0, 0.0));
                e += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                this.setVelocity(lv4.add(g, 0.0, 0.0));
                e += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                this.setVelocity(lv4.add(0.0, 0.0, g));
                e += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                this.setVelocity(lv4.add(0.0, 0.0, -g));
                e += 1.0;
            }
        }
        lv4 = this.getVelocity();
        Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(lv5);
        Vec3i lv6 = pair.getFirst();
        Vec3i lv7 = pair.getSecond();
        double h = lv7.getX() - lv6.getX();
        double i = lv7.getZ() - lv6.getZ();
        double j = Math.sqrt(h * h + i * i);
        double k = lv4.x * h + lv4.z * i;
        if (k < 0.0) {
            h = -h;
            i = -i;
        }
        double l = Math.min(2.0, lv4.horizontalLength());
        lv4 = new Vec3d(l * h / j, lv4.y, l * i / j);
        this.setVelocity(lv4);
        Entity lv8 = this.minecart.getFirstPassenger();
        Entity entity = this.minecart.getFirstPassenger();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv9 = (ServerPlayerEntity)entity;
            lv10 = lv9.getInputVelocityForMinecart();
        } else {
            lv10 = Vec3d.ZERO;
        }
        if (lv8 instanceof PlayerEntity && lv10.lengthSquared() > 0.0) {
            Vec3d lv11 = lv10.normalize();
            double m = this.getVelocity().horizontalLengthSquared();
            if (lv11.lengthSquared() > 0.0 && m < 0.01) {
                this.setVelocity(this.getVelocity().add(lv10.x * 0.001, 0.0, lv10.z * 0.001));
                bl2 = false;
            }
        }
        if (bl2) {
            double n = this.getVelocity().horizontalLength();
            if (n < 0.03) {
                this.setVelocity(Vec3d.ZERO);
            } else {
                this.setVelocity(this.getVelocity().multiply(0.5, 0.0, 0.5));
            }
        }
        double n = (double)lv.getX() + 0.5 + (double)lv6.getX() * 0.5;
        double o = (double)lv.getZ() + 0.5 + (double)lv6.getZ() * 0.5;
        double p = (double)lv.getX() + 0.5 + (double)lv7.getX() * 0.5;
        double q = (double)lv.getZ() + 0.5 + (double)lv7.getZ() * 0.5;
        h = p - n;
        i = q - o;
        if (h == 0.0) {
            r = f - (double)lv.getZ();
        } else if (i == 0.0) {
            r = d - (double)lv.getX();
        } else {
            s = d - n;
            t = f - o;
            r = (s * h + t * i) * 2.0;
        }
        d = n + h * r;
        f = o + i * r;
        this.setPos(d, e, f);
        s = this.minecart.hasPassengers() ? 0.75 : 1.0;
        t = this.minecart.getMaxSpeed(world);
        lv4 = this.getVelocity();
        this.minecart.move(MovementType.SELF, new Vec3d(MathHelper.clamp(s * lv4.x, -t, t), 0.0, MathHelper.clamp(s * lv4.z, -t, t)));
        if (lv6.getY() != 0 && MathHelper.floor(this.minecart.getX()) - lv.getX() == lv6.getX() && MathHelper.floor(this.minecart.getZ()) - lv.getZ() == lv6.getZ()) {
            this.setPos(this.minecart.getX(), this.minecart.getY() + (double)lv6.getY(), this.minecart.getZ());
        } else if (lv7.getY() != 0 && MathHelper.floor(this.minecart.getX()) - lv.getX() == lv7.getX() && MathHelper.floor(this.minecart.getZ()) - lv.getZ() == lv7.getZ()) {
            this.setPos(this.minecart.getX(), this.minecart.getY() + (double)lv7.getY(), this.minecart.getZ());
        }
        this.setVelocity(this.minecart.applySlowdown(this.getVelocity()));
        Vec3d lv12 = this.snapPositionToRail(this.minecart.getX(), this.minecart.getY(), this.minecart.getZ());
        if (lv12 != null && lv3 != null) {
            double u = (lv3.y - lv12.y) * 0.05;
            lv13 = this.getVelocity();
            v = lv13.horizontalLength();
            if (v > 0.0) {
                this.setVelocity(lv13.multiply((v + u) / v, 1.0, (v + u) / v));
            }
            this.setPos(this.minecart.getX(), lv12.y, this.minecart.getZ());
        }
        int w = MathHelper.floor(this.minecart.getX());
        int x = MathHelper.floor(this.minecart.getZ());
        if (w != lv.getX() || x != lv.getZ()) {
            lv13 = this.getVelocity();
            v = lv13.horizontalLength();
            this.setVelocity(v * (double)(w - lv.getX()), lv13.y, v * (double)(x - lv.getZ()));
        }
        if (bl) {
            lv13 = this.getVelocity();
            v = lv13.horizontalLength();
            if (v > 0.01) {
                double y = 0.06;
                this.setVelocity(lv13.add(lv13.x / v * 0.06, 0.0, lv13.z / v * 0.06));
            } else {
                Vec3d lv14 = this.getVelocity();
                double z = lv14.x;
                double aa = lv14.z;
                if (lv5 == RailShape.EAST_WEST) {
                    if (this.minecart.willHitBlockAt(lv.west())) {
                        z = 0.02;
                    } else if (this.minecart.willHitBlockAt(lv.east())) {
                        z = -0.02;
                    }
                } else if (lv5 == RailShape.NORTH_SOUTH) {
                    if (this.minecart.willHitBlockAt(lv.north())) {
                        aa = 0.02;
                    } else if (this.minecart.willHitBlockAt(lv.south())) {
                        aa = -0.02;
                    }
                } else {
                    return;
                }
                this.setVelocity(z, lv14.y, aa);
            }
        }
    }

    @Nullable
    public Vec3d method_61619(double x, double y, double z, double g) {
        BlockState lv;
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        if (this.getWorld().getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(lv = this.getWorld().getBlockState(new BlockPos(i, j, k)))) {
            RailShape lv2 = lv.get(((AbstractRailBlock)lv.getBlock()).getShapeProperty());
            y = j;
            if (lv2.isAscending()) {
                y = j + 1;
            }
            Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(lv2);
            Vec3i lv3 = pair.getFirst();
            Vec3i lv4 = pair.getSecond();
            double h = lv4.getX() - lv3.getX();
            double l = lv4.getZ() - lv3.getZ();
            double m = Math.sqrt(h * h + l * l);
            if (lv3.getY() != 0 && MathHelper.floor(x += (h /= m) * g) - i == lv3.getX() && MathHelper.floor(z += (l /= m) * g) - k == lv3.getZ()) {
                y += (double)lv3.getY();
            } else if (lv4.getY() != 0 && MathHelper.floor(x) - i == lv4.getX() && MathHelper.floor(z) - k == lv4.getZ()) {
                y += (double)lv4.getY();
            }
            return this.snapPositionToRail(x, y, z);
        }
        return null;
    }

    @Nullable
    public Vec3d snapPositionToRail(double x, double y, double z) {
        BlockState lv;
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        if (this.getWorld().getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(lv = this.getWorld().getBlockState(new BlockPos(i, j, k)))) {
            double s;
            RailShape lv2 = lv.get(((AbstractRailBlock)lv.getBlock()).getShapeProperty());
            Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(lv2);
            Vec3i lv3 = pair.getFirst();
            Vec3i lv4 = pair.getSecond();
            double g = (double)i + 0.5 + (double)lv3.getX() * 0.5;
            double h = (double)j + 0.0625 + (double)lv3.getY() * 0.5;
            double l = (double)k + 0.5 + (double)lv3.getZ() * 0.5;
            double m = (double)i + 0.5 + (double)lv4.getX() * 0.5;
            double n = (double)j + 0.0625 + (double)lv4.getY() * 0.5;
            double o = (double)k + 0.5 + (double)lv4.getZ() * 0.5;
            double p = m - g;
            double q = (n - h) * 2.0;
            double r = o - l;
            if (p == 0.0) {
                s = z - (double)k;
            } else if (r == 0.0) {
                s = x - (double)i;
            } else {
                double t = x - g;
                double u = z - l;
                s = (t * p + u * r) * 2.0;
            }
            x = g + p * s;
            y = h + q * s;
            z = l + r * s;
            if (q < 0.0) {
                y += 1.0;
            } else if (q > 0.0) {
                y += 0.5;
            }
            return new Vec3d(x, y, z);
        }
        return null;
    }

    @Override
    public double moveAlongTrack(BlockPos blockPos, RailShape railShape, double remainingMovement) {
        return 0.0;
    }

    @Override
    public boolean handleCollision() {
        block4: {
            Box lv;
            block3: {
                lv = this.minecart.getBoundingBox().expand(0.2f, 0.0, 0.2f);
                if (!this.minecart.isRideable() || !(this.getVelocity().horizontalLengthSquared() >= 0.01)) break block3;
                List<Entity> list = this.getWorld().getOtherEntities(this.minecart, lv, EntityPredicates.canBePushedBy(this.minecart));
                if (list.isEmpty()) break block4;
                for (Entity lv2 : list) {
                    if (lv2 instanceof PlayerEntity || lv2 instanceof IronGolemEntity || lv2 instanceof AbstractMinecartEntity || this.minecart.hasPassengers() || lv2.hasVehicle()) {
                        lv2.pushAwayFrom(this.minecart);
                        continue;
                    }
                    lv2.startRiding(this.minecart);
                }
                break block4;
            }
            for (Entity lv3 : this.getWorld().getOtherEntities(this.minecart, lv)) {
                if (this.minecart.hasPassenger(lv3) || !lv3.isPushable() || !(lv3 instanceof AbstractMinecartEntity)) continue;
                lv3.pushAwayFrom(this.minecart);
            }
        }
        return false;
    }

    @Override
    public Direction getHorizontalFacing() {
        return this.minecart.isYawFlipped() ? this.minecart.getHorizontalFacing().getOpposite().rotateYClockwise() : this.minecart.getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public Vec3d limitSpeed(Vec3d velocity) {
        return new Vec3d(MathHelper.clamp(velocity.x, -0.4, 0.4), velocity.y, MathHelper.clamp(velocity.z, -0.4, 0.4));
    }

    @Override
    public double getMaxSpeed(ServerWorld world) {
        return this.minecart.isTouchingWater() ? 0.2 : 0.4;
    }

    @Override
    public double getSpeedRetention() {
        return this.minecart.hasPassengers() ? 0.997 : 0.96;
    }
}

