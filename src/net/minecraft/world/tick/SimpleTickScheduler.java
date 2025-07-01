/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.tick;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.SerializableTickScheduler;
import net.minecraft.world.tick.Tick;

public class SimpleTickScheduler<T>
implements SerializableTickScheduler<T>,
BasicTickScheduler<T> {
    private final List<Tick<T>> scheduledTicks = Lists.newArrayList();
    private final Set<Tick<?>> scheduledTicksSet = new ObjectOpenCustomHashSet(Tick.HASH_STRATEGY);

    @Override
    public void scheduleTick(OrderedTick<T> orderedTick) {
        Tick<T> lv = new Tick<T>(orderedTick.type(), orderedTick.pos(), 0, orderedTick.priority());
        this.scheduleTick(lv);
    }

    @Override
    private void scheduleTick(Tick<T> tick) {
        if (this.scheduledTicksSet.add(tick)) {
            this.scheduledTicks.add(tick);
        }
    }

    @Override
    public boolean isQueued(BlockPos pos, T type) {
        return this.scheduledTicksSet.contains(Tick.create(type, pos));
    }

    @Override
    public int getTickCount() {
        return this.scheduledTicks.size();
    }

    @Override
    public List<Tick<T>> collectTicks(long time) {
        return this.scheduledTicks;
    }

    public List<Tick<T>> getTicks() {
        return List.copyOf(this.scheduledTicks);
    }

    public static <T> SimpleTickScheduler<T> tick(List<Tick<T>> ticks) {
        SimpleTickScheduler<T> lv = new SimpleTickScheduler<T>();
        ticks.forEach(lv::scheduleTick);
        return lv;
    }
}

