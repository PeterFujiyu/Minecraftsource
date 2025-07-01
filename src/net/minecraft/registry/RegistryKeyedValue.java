/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.registry;

import net.minecraft.registry.RegistryKey;

@FunctionalInterface
public interface RegistryKeyedValue<T, V> {
    public V get(RegistryKey<T> var1);

    public static <T, V> RegistryKeyedValue<T, V> fixed(V value) {
        return registryKey -> value;
    }
}

