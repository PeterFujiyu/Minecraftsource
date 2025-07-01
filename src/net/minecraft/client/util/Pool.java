/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;

@Environment(value=EnvType.CLIENT)
public class Pool
implements ObjectAllocator,
AutoCloseable {
    private final int lifespan;
    private final Deque<Entry<?>> entries = new ArrayDeque();

    public Pool(int lifespan) {
        this.lifespan = lifespan;
    }

    public void decrementLifespan() {
        Iterator<Entry<?>> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            Entry<?> lv = iterator.next();
            if (lv.lifespan-- != 0) continue;
            lv.close();
            iterator.remove();
        }
    }

    @Override
    public <T> T acquire(ClosableFactory<T> factory) {
        Iterator<Entry<?>> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            Entry<?> lv = iterator.next();
            if (!lv.factory.equals(factory)) continue;
            iterator.remove();
            return lv.object;
        }
        return factory.create();
    }

    @Override
    public <T> void release(ClosableFactory<T> factory, T value) {
        this.entries.addFirst(new Entry<T>(factory, value, this.lifespan));
    }

    public void clear() {
        this.entries.forEach(Entry::close);
        this.entries.clear();
    }

    @Override
    public void close() {
        this.clear();
    }

    @VisibleForTesting
    protected Collection<Entry<?>> getEntries() {
        return this.entries;
    }

    @Environment(value=EnvType.CLIENT)
    @VisibleForTesting
    protected static final class Entry<T>
    implements AutoCloseable {
        final ClosableFactory<T> factory;
        final T object;
        int lifespan;

        Entry(ClosableFactory<T> factory, T object, int lifespan) {
            this.factory = factory;
            this.object = object;
            this.lifespan = lifespan;
        }

        @Override
        public void close() {
            this.factory.close(this.object);
        }
    }
}

