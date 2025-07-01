/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component;

import java.util.stream.Stream;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import org.jetbrains.annotations.Nullable;

public interface ComponentHolder {
    public ComponentMap getComponents();

    @Nullable
    default public <T> T get(ComponentType<? extends T> type) {
        return this.getComponents().get(type);
    }

    default public <T> Stream<T> streamAll(Class<? extends T> valueClass) {
        return this.getComponents().stream().map(Component::value).filter(value -> valueClass.isAssignableFrom(value.getClass())).map(value -> value);
    }

    default public <T> T getOrDefault(ComponentType<? extends T> type, T fallback) {
        return this.getComponents().getOrDefault(type, fallback);
    }

    default public boolean contains(ComponentType<?> type) {
        return this.getComponents().contains(type);
    }
}

