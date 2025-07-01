/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Spawner;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class TrialSpawnerBlockEntity
extends BlockEntity
implements Spawner,
TrialSpawnerLogic.TrialSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private TrialSpawnerLogic spawner;

    public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.TRIAL_SPAWNER, pos, state);
        EntityDetector lv = EntityDetector.SURVIVAL_PLAYERS;
        EntityDetector.Selector lv2 = EntityDetector.Selector.IN_WORLD;
        this.spawner = new TrialSpawnerLogic(this, lv, lv2);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.spawner.codec().parse(registries.getOps(NbtOps.INSTANCE), nbt).resultOrPartial(LOGGER::error).ifPresent(spawner -> {
            this.spawner = spawner;
        });
        if (this.world != null) {
            this.updateListeners();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        this.spawner.codec().encodeStart(registries.getOps(NbtOps.INSTANCE), this.spawner).ifSuccess(nbtx -> nbt.copyFrom((NbtCompound)nbtx)).ifError(error -> LOGGER.warn("Failed to encode TrialSpawner {}", (Object)error.message()));
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.spawner.getData().getSpawnDataNbt(this.getCachedState().get(TrialSpawnerBlock.TRIAL_SPAWNER_STATE));
    }

    @Override
    public void setEntityType(EntityType<?> type, Random random) {
        if (this.world == null) {
            Util.logErrorOrPause("Expected non-null level");
            return;
        }
        this.spawner.setEntityType(type, this.world);
        this.markDirty();
    }

    public TrialSpawnerLogic getSpawner() {
        return this.spawner;
    }

    @Override
    public TrialSpawnerState getSpawnerState() {
        if (!this.getCachedState().contains(Properties.TRIAL_SPAWNER_STATE)) {
            return TrialSpawnerState.INACTIVE;
        }
        return this.getCachedState().get(Properties.TRIAL_SPAWNER_STATE);
    }

    @Override
    public void setSpawnerState(World world, TrialSpawnerState spawnerState) {
        this.markDirty();
        world.setBlockState(this.pos, (BlockState)this.getCachedState().with(Properties.TRIAL_SPAWNER_STATE, spawnerState));
    }

    @Override
    public void updateListeners() {
        this.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

