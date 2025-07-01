/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class MobNavigation
extends EntityNavigation {
    private boolean avoidSunlight;

    public MobNavigation(MobEntity arg, World arg2) {
        super(arg, arg2);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LandPathNodeMaker();
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.entity.isOnGround() || this.entity.isInFluid() || this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.getPathfindingY(), this.entity.getZ());
    }

    @Override
    public Path findPathTo(BlockPos target, int distance) {
        BlockPos.Mutable lv2;
        WorldChunk lv = this.world.getChunkManager().getWorldChunk(ChunkSectionPos.getSectionCoord(target.getX()), ChunkSectionPos.getSectionCoord(target.getZ()));
        if (lv == null) {
            return null;
        }
        if (lv.getBlockState(target).isAir()) {
            lv2 = target.mutableCopy().move(Direction.DOWN);
            while (lv2.getY() > this.world.getBottomY() && lv.getBlockState(lv2).isAir()) {
                lv2.move(Direction.DOWN);
            }
            if (lv2.getY() > this.world.getBottomY()) {
                return super.findPathTo((BlockPos)lv2.up(), distance);
            }
            lv2.setY(target.getY() + 1);
            while (lv2.getY() <= this.world.getTopYInclusive() && lv.getBlockState(lv2).isAir()) {
                lv2.move(Direction.UP);
            }
            target = lv2;
        }
        if (lv.getBlockState(target).isSolid()) {
            lv2 = target.mutableCopy().move(Direction.UP);
            while (lv2.getY() <= this.world.getTopYInclusive() && lv.getBlockState(lv2).isSolid()) {
                lv2.move(Direction.UP);
            }
            return super.findPathTo(lv2.toImmutable(), distance);
        }
        return super.findPathTo(target, distance);
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(entity.getBlockPos(), distance);
    }

    private int getPathfindingY() {
        if (!this.entity.isTouchingWater() || !this.canSwim()) {
            return MathHelper.floor(this.entity.getY() + 0.5);
        }
        int i = this.entity.getBlockY();
        BlockState lv = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), i, this.entity.getZ()));
        int j = 0;
        while (lv.isOf(Blocks.WATER)) {
            lv = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), ++i, this.entity.getZ()));
            if (++j <= 16) continue;
            return this.entity.getBlockY();
        }
        return i;
    }

    @Override
    protected void adjustPath() {
        super.adjustPath();
        if (this.avoidSunlight) {
            if (this.world.isSkyVisible(BlockPos.ofFloored(this.entity.getX(), this.entity.getY() + 0.5, this.entity.getZ()))) {
                return;
            }
            for (int i = 0; i < this.currentPath.getLength(); ++i) {
                PathNode lv = this.currentPath.getNode(i);
                if (!this.world.isSkyVisible(new BlockPos(lv.x, lv.y, lv.z))) continue;
                this.currentPath.setLength(i);
                return;
            }
        }
    }

    protected boolean canWalkOnPath(PathNodeType pathType) {
        if (pathType == PathNodeType.WATER) {
            return false;
        }
        if (pathType == PathNodeType.LAVA) {
            return false;
        }
        return pathType != PathNodeType.OPEN;
    }

    public void setCanPathThroughDoors(boolean canPathThroughDoors) {
        this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
    }

    public void setAvoidSunlight(boolean avoidSunlight) {
        this.avoidSunlight = avoidSunlight;
    }

    public void setCanWalkOverFences(boolean canWalkOverFences) {
        this.nodeMaker.setCanWalkOverFences(canWalkOverFences);
    }
}

