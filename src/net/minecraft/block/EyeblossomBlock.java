/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class EyeblossomBlock
extends FlowerBlock {
    public static final MapCodec<EyeblossomBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("open")).forGetter(block -> block.state.open), EyeblossomBlock.createSettingsCodec()).apply((Applicative<EyeblossomBlock, ?>)instance, EyeblossomBlock::new));
    private static final int NOTIFY_RANGE_XZ = 3;
    private static final int NOTIFY_RANGE_Y = 2;
    private final EyeblossomState state;

    public MapCodec<? extends EyeblossomBlock> getCodec() {
        return CODEC;
    }

    public EyeblossomBlock(EyeblossomState state, AbstractBlock.Settings settings) {
        super(state.stewEffect, state.effectLengthInSeconds, settings);
        this.state = state;
    }

    public EyeblossomBlock(boolean open, AbstractBlock.Settings settings) {
        super(EyeblossomState.of((boolean)open).stewEffect, EyeblossomState.of((boolean)open).effectLengthInSeconds, settings);
        this.state = EyeblossomState.of(open);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockState lv;
        if (this.state.isOpen() && random.nextInt(700) == 0 && (lv = world.getBlockState(pos.down())).isOf(Blocks.PALE_MOSS_BLOCK)) {
            world.playSound(pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_EYEBLOSSOM_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.updateStateAndNotifyOthers(state, world, pos, random)) {
            world.playSound(null, pos, this.state.getOpposite().longSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.randomTick(state, world, pos, random);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.updateStateAndNotifyOthers(state, world, pos, random)) {
            world.playSound(null, pos, this.state.getOpposite().sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.scheduledTick(state, world, pos, random);
    }

    private boolean updateStateAndNotifyOthers(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.getDimension().natural()) {
            return false;
        }
        if (world.isDay() != this.state.open) {
            return false;
        }
        EyeblossomState lv = this.state.getOpposite();
        world.setBlockState(pos, lv.getBlockState(), Block.NOTIFY_ALL);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
        lv.spawnTrailParticle(world, pos, random);
        BlockPos.iterate(pos.add(-3, -2, -3), pos.add(3, 2, 3)).forEach(otherPos -> {
            BlockState lv = world.getBlockState((BlockPos)otherPos);
            if (lv == state) {
                double d = Math.sqrt(pos.getSquaredDistance((Vec3i)otherPos));
                int i = random.nextBetween((int)(d * 5.0), (int)(d * 10.0));
                world.scheduleBlockTick((BlockPos)otherPos, state.getBlock(), i);
            }
        });
        return true;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && world.getDifficulty() != Difficulty.PEACEFUL && entity instanceof BeeEntity) {
            BeeEntity lv = (BeeEntity)entity;
            if (BeeEntity.isAttractive(state) && !lv.hasStatusEffect(StatusEffects.POISON)) {
                lv.addStatusEffect(this.getContactEffect());
            }
        }
    }

    @Override
    public StatusEffectInstance getContactEffect() {
        return new StatusEffectInstance(StatusEffects.POISON, 25);
    }

    public static enum EyeblossomState {
        OPEN(true, StatusEffects.BLINDNESS, 11.0f, SoundEvents.BLOCK_EYEBLOSSOM_OPEN_LONG, SoundEvents.BLOCK_EYEBLOSSOM_OPEN, 16545810),
        CLOSED(false, StatusEffects.NAUSEA, 7.0f, SoundEvents.BLOCK_EYEBLOSSOM_CLOSE_LONG, SoundEvents.BLOCK_EYEBLOSSOM_CLOSE, 0x5F5F5F);

        final boolean open;
        final RegistryEntry<StatusEffect> stewEffect;
        final float effectLengthInSeconds;
        final SoundEvent longSound;
        final SoundEvent sound;
        private final int particleColor;

        private EyeblossomState(boolean open, RegistryEntry<StatusEffect> stewEffect, float effectLengthInSeconds, SoundEvent longSound, SoundEvent sound, int particleColor) {
            this.open = open;
            this.stewEffect = stewEffect;
            this.effectLengthInSeconds = effectLengthInSeconds;
            this.longSound = longSound;
            this.sound = sound;
            this.particleColor = particleColor;
        }

        public Block getBlock() {
            return this.open ? Blocks.OPEN_EYEBLOSSOM : Blocks.CLOSED_EYEBLOSSOM;
        }

        public BlockState getBlockState() {
            return this.getBlock().getDefaultState();
        }

        public EyeblossomState getOpposite() {
            return EyeblossomState.of(!this.open);
        }

        public boolean isOpen() {
            return this.open;
        }

        public static EyeblossomState of(boolean open) {
            return open ? OPEN : CLOSED;
        }

        public void spawnTrailParticle(ServerWorld world, BlockPos pos, Random random) {
            Vec3d lv = pos.toCenterPos();
            double d = 0.5 + random.nextDouble();
            Vec3d lv2 = new Vec3d(random.nextDouble() - 0.5, random.nextDouble() + 1.0, random.nextDouble() - 0.5);
            Vec3d lv3 = lv.add(lv2.multiply(d));
            TrailParticleEffect lv4 = new TrailParticleEffect(lv3, this.particleColor, (int)(20.0 * d));
            world.spawnParticles(lv4, lv.x, lv.y, lv.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        public SoundEvent getLongSound() {
            return this.longSound;
        }
    }
}

