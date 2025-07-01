/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.loot.provider.nbt;

import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.Nullable;

public interface LootNbtProvider {
    @Nullable
    public NbtElement getNbt(LootContext var1);

    public Set<ContextParameter<?>> getRequiredParameters();

    public LootNbtProviderType getType();
}

