/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryWrapper;

public abstract class PersistentState {
    private boolean dirty;

    public abstract NbtCompound writeNbt(NbtCompound var1, RegistryWrapper.WrapperLookup var2);

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound lv = new NbtCompound();
        lv.put("data", this.writeNbt(new NbtCompound(), registries));
        NbtHelper.putDataVersion(lv);
        this.setDirty(false);
        return lv;
    }

    public record Type<T extends PersistentState>(Supplier<T> constructor, BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, T> deserializer, DataFixTypes type) {
    }
}

