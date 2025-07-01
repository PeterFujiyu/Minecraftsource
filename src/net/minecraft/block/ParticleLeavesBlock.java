/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleLeavesBlock
extends LeavesBlock {
    public static final MapCodec<ParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codecs.POSITIVE_INT.fieldOf("chance")).forGetter(block -> block.chance), ((MapCodec)ParticleTypes.TYPE_CODEC.fieldOf("particle")).forGetter(block -> block.particle), ParticleLeavesBlock.createSettingsCodec()).apply((Applicative<ParticleLeavesBlock, ?>)instance, ParticleLeavesBlock::new));
    private final ParticleEffect particle;
    private final int chance;

    public MapCodec<ParticleLeavesBlock> getCodec() {
        return CODEC;
    }

    public ParticleLeavesBlock(int chance, ParticleEffect particle, AbstractBlock.Settings settings) {
        super(settings);
        this.chance = chance;
        this.particle = particle;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextInt(this.chance) != 0) {
            return;
        }
        BlockPos lv = pos.down();
        BlockState lv2 = world.getBlockState(lv);
        if (ParticleLeavesBlock.isFaceFullSquare(lv2.getCollisionShape(world, lv), Direction.UP)) {
            return;
        }
        ParticleUtil.spawnParticle(world, pos, random, this.particle);
    }
}

