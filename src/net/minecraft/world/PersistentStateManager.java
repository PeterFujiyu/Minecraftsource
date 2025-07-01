/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PersistentStateManager
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, Optional<PersistentState>> loadedStates = new HashMap<String, Optional<PersistentState>>();
    private final DataFixer dataFixer;
    private final RegistryWrapper.WrapperLookup registries;
    private final Path directory;
    private CompletableFuture<?> savingFuture = CompletableFuture.completedFuture(null);

    public PersistentStateManager(Path directory, DataFixer dataFixer, RegistryWrapper.WrapperLookup registries) {
        this.dataFixer = dataFixer;
        this.directory = directory;
        this.registries = registries;
    }

    private Path getFile(String id) {
        return this.directory.resolve(id + ".dat");
    }

    public <T extends PersistentState> T getOrCreate(PersistentState.Type<T> type, String id) {
        T lv = this.get(type, id);
        if (lv != null) {
            return lv;
        }
        PersistentState lv2 = (PersistentState)type.constructor().get();
        this.set(id, lv2);
        return (T)lv2;
    }

    @Nullable
    public <T extends PersistentState> T get(PersistentState.Type<T> type, String id) {
        Optional<PersistentState> optional = this.loadedStates.get(id);
        if (optional == null) {
            optional = Optional.ofNullable(this.readFromFile(type.deserializer(), type.type(), id));
            this.loadedStates.put(id, optional);
        }
        return (T)((PersistentState)optional.orElse(null));
    }

    @Nullable
    private <T extends PersistentState> T readFromFile(BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, T> readFunction, DataFixTypes dataFixTypes, String id) {
        try {
            Path path = this.getFile(id);
            if (Files.exists(path, new LinkOption[0])) {
                NbtCompound lv = this.readNbt(id, dataFixTypes, SharedConstants.getGameVersion().getSaveVersion().getId());
                return (T)((PersistentState)readFunction.apply(lv.getCompound("data"), this.registries));
            }
        } catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", (Object)id, (Object)exception);
        }
        return null;
    }

    public void set(String id, PersistentState state) {
        this.loadedStates.put(id, Optional.of(state));
        state.markDirty();
    }

    public NbtCompound readNbt(String id, DataFixTypes dataFixTypes, int currentSaveVersion) throws IOException {
        try (InputStream inputStream = Files.newInputStream(this.getFile(id), new OpenOption[0]);){
            NbtCompound nbtCompound;
            try (PushbackInputStream pushbackInputStream = new PushbackInputStream(new FixedBufferInputStream(inputStream), 2);){
                NbtCompound lv;
                if (this.isCompressed(pushbackInputStream)) {
                    lv = NbtIo.readCompressed(pushbackInputStream, NbtSizeTracker.ofUnlimitedBytes());
                } else {
                    try (DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);){
                        lv = NbtIo.readCompound(dataInputStream);
                    }
                }
                int j = NbtHelper.getDataVersion(lv, 1343);
                nbtCompound = dataFixTypes.update(this.dataFixer, lv, j, currentSaveVersion);
            }
            return nbtCompound;
        }
    }

    private boolean isCompressed(PushbackInputStream stream) throws IOException {
        int j;
        byte[] bs = new byte[2];
        boolean bl = false;
        int i = stream.read(bs, 0, 2);
        if (i == 2 && (j = (bs[1] & 0xFF) << 8 | bs[0] & 0xFF) == 35615) {
            bl = true;
        }
        if (i != 0) {
            stream.unread(bs, 0, i);
        }
        return bl;
    }

    public CompletableFuture<?> startSaving() {
        Map<Path, NbtCompound> map = this.collectStatesToSave();
        if (map.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        int i = Util.getAvailableBackgroundThreads();
        int j = map.size();
        this.savingFuture = j > i ? this.savingFuture.thenCompose(object -> {
            ArrayList<CompletableFuture<Void>> list = new ArrayList<CompletableFuture<Void>>(i);
            int k = MathHelper.ceilDiv(j, i);
            for (List list2 : Iterables.partition(map.entrySet(), k)) {
                list.add(CompletableFuture.runAsync(() -> {
                    for (Map.Entry entry : list2) {
                        PersistentStateManager.save((Path)entry.getKey(), (NbtCompound)entry.getValue());
                    }
                }, Util.getIoWorkerExecutor()));
            }
            return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
        }) : this.savingFuture.thenCompose(v -> CompletableFuture.allOf((CompletableFuture[])map.entrySet().stream().map(entry -> CompletableFuture.runAsync(() -> PersistentStateManager.save((Path)entry.getKey(), (NbtCompound)entry.getValue()), Util.getIoWorkerExecutor())).toArray(CompletableFuture[]::new)));
        return this.savingFuture;
    }

    private Map<Path, NbtCompound> collectStatesToSave() {
        Object2ObjectArrayMap<Path, NbtCompound> map = new Object2ObjectArrayMap<Path, NbtCompound>();
        this.loadedStates.forEach((id, state) -> state.filter(PersistentState::isDirty).ifPresent(state2 -> map.put(this.getFile((String)id), state2.toNbt(this.registries))));
        return map;
    }

    private static void save(Path path, NbtCompound nbt) {
        try {
            NbtIo.writeCompressed(nbt, path);
        } catch (IOException iOException) {
            LOGGER.error("Could not save data to {}", (Object)path.getFileName(), (Object)iOException);
        }
    }

    public void save() {
        this.startSaving().join();
    }

    @Override
    public void close() {
        this.save();
    }
}

