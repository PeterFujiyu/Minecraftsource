/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderTaskScheduler {
    private static final int field_53953 = 2;
    private int remainingPrioritizableTasks = 2;
    private final List<ChunkBuilder.BuiltChunk.Task> queue = new ObjectArrayList<ChunkBuilder.BuiltChunk.Task>();

    public synchronized void enqueue(ChunkBuilder.BuiltChunk.Task task) {
        this.queue.add(task);
    }

    @Nullable
    public synchronized ChunkBuilder.BuiltChunk.Task dequeueNearest(Vec3d pos) {
        boolean bl2;
        int i = -1;
        int j = -1;
        double d = Double.MAX_VALUE;
        double e = Double.MAX_VALUE;
        ListIterator<ChunkBuilder.BuiltChunk.Task> listIterator = this.queue.listIterator();
        while (listIterator.hasNext()) {
            int k = listIterator.nextIndex();
            ChunkBuilder.BuiltChunk.Task lv = listIterator.next();
            if (lv.cancelled.get()) {
                listIterator.remove();
                continue;
            }
            double f = lv.getOrigin().getSquaredDistance(pos);
            if (!lv.isPrioritized() && f < d) {
                d = f;
                i = k;
            }
            if (!lv.isPrioritized() || !(f < e)) continue;
            e = f;
            j = k;
        }
        boolean bl = j >= 0;
        boolean bl3 = bl2 = i >= 0;
        if (bl && (!bl2 || this.remainingPrioritizableTasks > 0 && e < d)) {
            --this.remainingPrioritizableTasks;
            return this.remove(j);
        }
        this.remainingPrioritizableTasks = 2;
        return this.remove(i);
    }

    public int size() {
        return this.queue.size();
    }

    @Nullable
    private ChunkBuilder.BuiltChunk.Task remove(int index) {
        if (index >= 0) {
            return this.queue.remove(index);
        }
        return null;
    }

    public synchronized void cancelAll() {
        for (ChunkBuilder.BuiltChunk.Task lv : this.queue) {
            lv.cancel();
        }
        this.queue.clear();
    }
}

