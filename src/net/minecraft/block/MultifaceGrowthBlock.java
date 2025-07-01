/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.MultifaceGrower;

public abstract class MultifaceGrowthBlock
extends MultifaceBlock {
    public MultifaceGrowthBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    public abstract MapCodec<? extends MultifaceGrowthBlock> getCodec();

    public abstract MultifaceGrower getGrower();
}

