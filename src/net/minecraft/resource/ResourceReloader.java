/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;

public interface ResourceReloader {
    public CompletableFuture<Void> reload(Synchronizer var1, ResourceManager var2, Executor var3, Executor var4);

    default public String getName() {
        return this.getClass().getSimpleName();
    }

    public static interface Synchronizer {
        public <T> CompletableFuture<T> whenPrepared(T var1);
    }
}

