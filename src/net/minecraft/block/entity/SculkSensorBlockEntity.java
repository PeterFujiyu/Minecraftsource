/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SculkSensorBlockEntity
extends BlockEntity
implements GameEventListener.Holder<Vibrations.VibrationListener>,
Vibrations {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Vibrations.ListenerData listenerData;
    private final Vibrations.VibrationListener listener;
    private final Vibrations.Callback callback = this.createCallback();
    private int lastVibrationFrequency;

    protected SculkSensorBlockEntity(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
        this.listenerData = new Vibrations.ListenerData();
        this.listener = new Vibrations.VibrationListener(this);
    }

    public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityType.SCULK_SENSOR, pos, state);
    }

    public Vibrations.Callback createCallback() {
        return new VibrationCallback(this.getPos());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
        RegistryOps<NbtElement> lv = registries.getOps(NbtOps.INSTANCE);
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            Vibrations.ListenerData.CODEC.parse(lv, nbt.getCompound("listener")).resultOrPartial(string -> LOGGER.error("Failed to parse vibration listener for Sculk Sensor: '{}'", string)).ifPresent(listener -> {
                this.listenerData = listener;
            });
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        RegistryOps<NbtElement> lv = registries.getOps(NbtOps.INSTANCE);
        Vibrations.ListenerData.CODEC.encodeStart(lv, this.listenerData).resultOrPartial(string -> LOGGER.error("Failed to encode vibration listener for Sculk Sensor: '{}'", string)).ifPresent(listenerNbt -> nbt.put("listener", (NbtElement)listenerNbt));
    }

    @Override
    public Vibrations.ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    @Override
    public Vibrations.Callback getVibrationCallback() {
        return this.callback;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        this.lastVibrationFrequency = lastVibrationFrequency;
    }

    @Override
    public Vibrations.VibrationListener getEventListener() {
        return this.listener;
    }

    @Override
    public /* synthetic */ GameEventListener getEventListener() {
        return this.getEventListener();
    }

    protected class VibrationCallback
    implements Vibrations.Callback {
        public static final int RANGE = 8;
        protected final BlockPos pos;
        private final PositionSource positionSource;

        public VibrationCallback(BlockPos pos) {
            this.pos = pos;
            this.positionSource = new BlockPositionSource(pos);
        }

        @Override
        public int getRange() {
            return 8;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public boolean triggersAvoidCriterion() {
            return true;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
            if (pos.equals(this.pos) && (event.matches(GameEvent.BLOCK_DESTROY) || event.matches(GameEvent.BLOCK_PLACE))) {
                return false;
            }
            return SculkSensorBlock.isInactive(SculkSensorBlockEntity.this.getCachedState());
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            BlockState lv = SculkSensorBlockEntity.this.getCachedState();
            if (SculkSensorBlock.isInactive(lv)) {
                SculkSensorBlockEntity.this.setLastVibrationFrequency(Vibrations.getFrequency(event));
                int i = Vibrations.getSignalStrength(distance, this.getRange());
                Block block = lv.getBlock();
                if (block instanceof SculkSensorBlock) {
                    SculkSensorBlock lv2 = (SculkSensorBlock)block;
                    lv2.setActive(sourceEntity, world, this.pos, lv, i, SculkSensorBlockEntity.this.getLastVibrationFrequency());
                }
            }
        }

        @Override
        public void onListen() {
            SculkSensorBlockEntity.this.markDirty();
        }

        @Override
        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}

