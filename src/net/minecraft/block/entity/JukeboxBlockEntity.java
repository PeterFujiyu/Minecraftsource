/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class JukeboxBlockEntity
extends BlockEntity
implements SingleStackInventory.SingleStackBlockEntityInventory {
    public static final String RECORD_ITEM_NBT_KEY = "RecordItem";
    public static final String TICKS_SINCE_SONG_STARTED_NBT_KEY = "ticks_since_song_started";
    private ItemStack recordStack = ItemStack.EMPTY;
    private final JukeboxManager manager = new JukeboxManager(this::onManagerChange, this.getPos());

    public JukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JUKEBOX, pos, state);
    }

    public JukeboxManager getManager() {
        return this.manager;
    }

    public void onManagerChange() {
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.markDirty();
    }

    private void onRecordStackChanged(boolean hasRecord) {
        if (this.world == null || this.world.getBlockState(this.getPos()) != this.getCachedState()) {
            return;
        }
        this.world.setBlockState(this.getPos(), (BlockState)this.getCachedState().with(JukeboxBlock.HAS_RECORD, hasRecord), Block.NOTIFY_LISTENERS);
        this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
    }

    public void dropRecord() {
        if (this.world == null || this.world.isClient) {
            return;
        }
        BlockPos lv = this.getPos();
        ItemStack lv2 = this.getStack();
        if (lv2.isEmpty()) {
            return;
        }
        this.emptyStack();
        Vec3d lv3 = Vec3d.add(lv, 0.5, 1.01, 0.5).addRandom(this.world.random, 0.7f);
        ItemStack lv4 = lv2.copy();
        ItemEntity lv5 = new ItemEntity(this.world, lv3.getX(), lv3.getY(), lv3.getZ(), lv4);
        lv5.setToDefaultPickupDelay();
        this.world.spawnEntity(lv5);
    }

    public static void tick(World world, BlockPos pos, BlockState state, JukeboxBlockEntity blockEntity) {
        blockEntity.manager.tick(world, state);
    }

    public int getComparatorOutput() {
        return JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), this.recordStack).map(RegistryEntry::value).map(JukeboxSong::comparatorOutput).orElse(0);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.contains(RECORD_ITEM_NBT_KEY, NbtElement.COMPOUND_TYPE)) {
            this.recordStack = ItemStack.fromNbt(registries, nbt.getCompound(RECORD_ITEM_NBT_KEY)).orElse(ItemStack.EMPTY);
        } else {
            if (!this.recordStack.isEmpty()) {
                this.manager.stopPlaying(this.world, this.getCachedState());
            }
            this.recordStack = ItemStack.EMPTY;
        }
        if (nbt.contains(TICKS_SINCE_SONG_STARTED_NBT_KEY, NbtElement.LONG_TYPE)) {
            JukeboxSong.getSongEntryFromStack(registries, this.recordStack).ifPresent(song -> this.manager.setValues((RegistryEntry<JukeboxSong>)song, nbt.getLong(TICKS_SINCE_SONG_STARTED_NBT_KEY)));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (!this.getStack().isEmpty()) {
            nbt.put(RECORD_ITEM_NBT_KEY, this.getStack().toNbt(registries));
        }
        if (this.manager.getSong() != null) {
            nbt.putLong(TICKS_SINCE_SONG_STARTED_NBT_KEY, this.manager.getTicksSinceSongStarted());
        }
    }

    @Override
    public ItemStack getStack() {
        return this.recordStack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        ItemStack lv = this.recordStack;
        this.setStack(ItemStack.EMPTY);
        return lv;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.recordStack = stack;
        boolean bl = !this.recordStack.isEmpty();
        Optional<RegistryEntry<JukeboxSong>> optional = JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), this.recordStack);
        this.onRecordStackChanged(bl);
        if (bl && optional.isPresent()) {
            this.manager.startPlaying(this.world, optional.get());
        } else {
            this.manager.stopPlaying(this.world, this.getCachedState());
        }
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE) && this.getStack(slot).isEmpty();
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return hopperInventory.containsAny(ItemStack::isEmpty);
    }

    @VisibleForTesting
    public void setDisc(ItemStack stack) {
        this.recordStack = stack;
        JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), stack).ifPresent(song -> this.manager.setValues((RegistryEntry<JukeboxSong>)song, 0L));
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.markDirty();
    }

    @VisibleForTesting
    public void reloadDisc() {
        JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), this.getStack()).ifPresent(song -> this.manager.startPlaying(this.world, (RegistryEntry<JukeboxSong>)song));
    }
}

