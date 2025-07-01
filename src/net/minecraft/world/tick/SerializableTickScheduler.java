/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.tick;

import java.util.List;
import net.minecraft.world.tick.Tick;

public interface SerializableTickScheduler<T> {
    public List<Tick<T>> collectTicks(long var1);
}

