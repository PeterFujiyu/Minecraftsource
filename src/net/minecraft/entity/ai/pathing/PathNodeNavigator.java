/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class PathNodeNavigator {
    private static final float TARGET_DISTANCE_MULTIPLIER = 1.5f;
    private final PathNode[] successors = new PathNode[32];
    private int range;
    private final PathNodeMaker pathNodeMaker;
    private static final boolean field_31808 = false;
    private final PathMinHeap minHeap = new PathMinHeap();

    public PathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
        this.pathNodeMaker = pathNodeMaker;
        this.range = range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Nullable
    public Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        this.minHeap.clear();
        this.pathNodeMaker.init(world, mob);
        PathNode lv = this.pathNodeMaker.getStart();
        if (lv == null) {
            return null;
        }
        Map<TargetPathNode, BlockPos> map = positions.stream().collect(Collectors.toMap(pos -> this.pathNodeMaker.getNode((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), Function.identity()));
        Path lv2 = this.findPathToAny(lv, map, followRange, distance, rangeMultiplier);
        this.pathNodeMaker.clear();
        return lv2;
    }

    @Nullable
    private Path findPathToAny(PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        Profiler lv = Profilers.get();
        lv.push("find_path");
        lv.markSampleType(SampleType.PATH_FINDING);
        Set<TargetPathNode> set = positions.keySet();
        startNode.penalizedPathLength = 0.0f;
        startNode.heapWeight = startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
        this.minHeap.clear();
        this.minHeap.push(startNode);
        ImmutableSet set2 = ImmutableSet.of();
        int j = 0;
        HashSet<TargetPathNode> set3 = Sets.newHashSetWithExpectedSize(set.size());
        int k = (int)((float)this.range * rangeMultiplier);
        while (!this.minHeap.isEmpty() && ++j < k) {
            PathNode lv2 = this.minHeap.pop();
            lv2.visited = true;
            for (TargetPathNode lv3 : set) {
                if (!(lv2.getManhattanDistance(lv3) <= (float)distance)) continue;
                lv3.markReached();
                set3.add(lv3);
            }
            if (!set3.isEmpty()) break;
            if (lv2.getDistance(startNode) >= followRange) continue;
            int l = this.pathNodeMaker.getSuccessors(this.successors, lv2);
            for (int m = 0; m < l; ++m) {
                PathNode lv4 = this.successors[m];
                float h = this.getDistance(lv2, lv4);
                lv4.pathLength = lv2.pathLength + h;
                float n = lv2.penalizedPathLength + h + lv4.penalty;
                if (!(lv4.pathLength < followRange) || lv4.isInHeap() && !(n < lv4.penalizedPathLength)) continue;
                lv4.previous = lv2;
                lv4.penalizedPathLength = n;
                lv4.distanceToNearestTarget = this.calculateDistances(lv4, set) * 1.5f;
                if (lv4.isInHeap()) {
                    this.minHeap.setNodeWeight(lv4, lv4.penalizedPathLength + lv4.distanceToNearestTarget);
                    continue;
                }
                lv4.heapWeight = lv4.penalizedPathLength + lv4.distanceToNearestTarget;
                this.minHeap.push(lv4);
            }
        }
        Optional<Path> optional = !set3.isEmpty() ? set3.stream().map(node -> this.createPath(node.getNearestNode(), (BlockPos)positions.get(node), true)).min(Comparator.comparingInt(Path::getLength)) : set.stream().map(arg -> this.createPath(arg.getNearestNode(), (BlockPos)positions.get(arg), false)).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
        lv.pop();
        if (optional.isEmpty()) {
            return null;
        }
        Path lv5 = optional.get();
        return lv5;
    }

    protected float getDistance(PathNode a, PathNode b) {
        return a.getDistance(b);
    }

    private float calculateDistances(PathNode node, Set<TargetPathNode> targets) {
        float f = Float.MAX_VALUE;
        for (TargetPathNode lv : targets) {
            float g = node.getDistance(lv);
            lv.updateNearestNode(g, node);
            f = Math.min(g, f);
        }
        return f;
    }

    private Path createPath(PathNode endNode, BlockPos target, boolean reachesTarget) {
        ArrayList<PathNode> list = Lists.newArrayList();
        PathNode lv = endNode;
        list.add(0, lv);
        while (lv.previous != null) {
            lv = lv.previous;
            list.add(0, lv);
        }
        return new Path(list, target, reachesTarget);
    }

    private static /* synthetic */ PathNode[] method_52609(int i) {
        return new PathNode[i];
    }
}

