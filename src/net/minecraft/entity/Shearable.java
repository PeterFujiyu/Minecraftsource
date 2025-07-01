/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

public interface Shearable {
    public void sheared(ServerWorld var1, SoundCategory var2, ItemStack var3);

    public boolean isShearable();
}

