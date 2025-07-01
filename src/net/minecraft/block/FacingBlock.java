/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public abstract class FacingBlock
extends Block {
    public static final EnumProperty<Direction> FACING = Properties.FACING;

    protected FacingBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    protected abstract MapCodec<? extends FacingBlock> getCodec();
}

