/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class CreakingHeartBlock
extends BlockWithEntity {
    public static final MapCodec<CreakingHeartBlock> CODEC = CreakingHeartBlock.createCodec(CreakingHeartBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    public static final BooleanProperty ACTIVE = Properties.ACTIVE;
    public static final BooleanProperty NATURAL = Properties.NATURAL;

    public MapCodec<CreakingHeartBlock> getCodec() {
        return CODEC;
    }

    protected CreakingHeartBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.getDefaultState().with(AXIS, Direction.Axis.Y)).with(ACTIVE, false)).with(NATURAL, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CreakingHeartBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        if (state.get(ACTIVE).booleanValue()) {
            return CreakingHeartBlock.validateTicker(type, BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::tick);
        }
        return null;
    }

    public static boolean isNightAndNatural(World world) {
        return world.getDimension().natural() && world.isNight();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!CreakingHeartBlock.isNightAndNatural(world)) {
            return;
        }
        if (!state.get(ACTIVE).booleanValue()) {
            return;
        }
        if (random.nextInt(16) == 0 && CreakingHeartBlock.isSurroundedByPaleOakLogs(world, pos)) {
            world.playSound(pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_CREAKING_HEART_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        BlockState lv = super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        return CreakingHeartBlock.enableIfValid(lv, world, pos);
    }

    private static BlockState enableIfValid(BlockState state, WorldView world, BlockPos pos) {
        boolean bl2;
        boolean bl = CreakingHeartBlock.shouldBeEnabled(state, world, pos);
        boolean bl3 = bl2 = state.get(ACTIVE) == false;
        if (bl && bl2) {
            return (BlockState)state.with(ACTIVE, true);
        }
        return state;
    }

    public static boolean shouldBeEnabled(BlockState state, WorldView world, BlockPos pos) {
        Direction.Axis lv = state.get(AXIS);
        for (Direction lv2 : lv.getDirections()) {
            BlockState lv3 = world.getBlockState(pos.offset(lv2));
            if (lv3.isIn(BlockTags.PALE_OAK_LOGS) && lv3.get(AXIS) == lv) continue;
            return false;
        }
        return true;
    }

    private static boolean isSurroundedByPaleOakLogs(WorldAccess world, BlockPos pos) {
        for (Direction lv : Direction.values()) {
            BlockPos lv2 = pos.offset(lv);
            BlockState lv3 = world.getBlockState(lv2);
            if (lv3.isIn(BlockTags.PALE_OAK_LOGS)) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return CreakingHeartBlock.enableIfValid((BlockState)this.getDefaultState().with(AXIS, ctx.getSide().getAxis()), ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return PillarBlock.changeRotation(state, rotation);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS, ACTIVE, NATURAL);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity lv = (CreakingHeartBlockEntity)blockEntity;
            lv.killPuppet(null);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity lv = (CreakingHeartBlockEntity)blockEntity;
            if (explosion instanceof ExplosionImpl) {
                ExplosionImpl lv2 = (ExplosionImpl)explosion;
                if (explosion.getDestructionType().destroysBlocks()) {
                    lv.killPuppet(lv2.getDamageSource());
                    LivingEntity livingEntity = explosion.getCausingEntity();
                    if (livingEntity instanceof PlayerEntity) {
                        PlayerEntity lv3 = (PlayerEntity)livingEntity;
                        if (explosion.getDestructionType().destroysBlocks()) {
                            this.dropExperienceOnBreak(lv3, state, world, pos);
                        }
                    }
                }
            }
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity lv = (CreakingHeartBlockEntity)blockEntity;
            lv.killPuppet(player.getDamageSources().playerAttack(player));
            this.dropExperienceOnBreak(player, state, world, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    private void dropExperienceOnBreak(PlayerEntity player, BlockState state, World world, BlockPos pos) {
        if (!player.isCreative() && !player.isSpectator() && state.get(NATURAL).booleanValue() && world instanceof ServerWorld) {
            ServerWorld lv = (ServerWorld)world;
            this.dropExperience(lv, pos, world.random.nextBetween(20, 24));
        }
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (!state.get(ACTIVE).booleanValue()) {
            return 0;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CreakingHeartBlockEntity)) {
            return 0;
        }
        CreakingHeartBlockEntity lv = (CreakingHeartBlockEntity)blockEntity;
        return lv.getComparatorOutput();
    }
}

