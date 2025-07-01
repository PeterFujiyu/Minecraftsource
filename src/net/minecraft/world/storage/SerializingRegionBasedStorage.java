/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.storage.ChunkPosKeyedStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SerializingRegionBasedStorage<R, P>
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String SECTIONS_KEY = "Sections";
    private final ChunkPosKeyedStorage storageAccess;
    private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap<Optional<R>>();
    private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
    private final Codec<P> codec;
    private final Function<R, P> serializer;
    private final BiFunction<P, Runnable, R> deserializer;
    private final Function<Runnable, R> factory;
    private final DynamicRegistryManager registryManager;
    private final ChunkErrorHandler errorHandler;
    protected final HeightLimitView world;
    private final LongSet loadedChunks = new LongOpenHashSet();
    private final Long2ObjectMap<CompletableFuture<Optional<LoadResult<P>>>> pendingLoads = new Long2ObjectOpenHashMap<CompletableFuture<Optional<LoadResult<P>>>>();
    private final Object lock = new Object();

    public SerializingRegionBasedStorage(ChunkPosKeyedStorage storageAccess, Codec<P> codec, Function<R, P> serializer, BiFunction<P, Runnable, R> deserializer, Function<Runnable, R> factory, DynamicRegistryManager registryManager, ChunkErrorHandler errorHandler, HeightLimitView world) {
        this.storageAccess = storageAccess;
        this.codec = codec;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.factory = factory;
        this.registryManager = registryManager;
        this.errorHandler = errorHandler;
        this.world = world;
    }

    protected void tick(BooleanSupplier shouldKeepTicking) {
        LongListIterator longIterator = this.unsavedElements.iterator();
        while (longIterator.hasNext() && shouldKeepTicking.getAsBoolean()) {
            ChunkPos lv = new ChunkPos(longIterator.nextLong());
            longIterator.remove();
            this.save(lv);
        }
        this.tickPendingLoads();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tickPendingLoads() {
        Object object = this.lock;
        synchronized (object) {
            ObjectIterator<Long2ObjectMap.Entry<CompletableFuture<Optional<LoadResult<P>>>>> iterator = Long2ObjectMaps.fastIterator(this.pendingLoads);
            while (iterator.hasNext()) {
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
                Optional optional = ((CompletableFuture)entry.getValue()).getNow(null);
                if (optional == null) continue;
                long l = entry.getLongKey();
                this.onLoad(new ChunkPos(l), optional.orElse(null));
                iterator.remove();
                this.loadedChunks.add(l);
            }
        }
    }

    public void save() {
        if (!this.unsavedElements.isEmpty()) {
            this.unsavedElements.forEach(chunkPos -> this.save(new ChunkPos(chunkPos)));
            this.unsavedElements.clear();
        }
    }

    public boolean hasUnsavedElements() {
        return !this.unsavedElements.isEmpty();
    }

    @Nullable
    protected Optional<R> getIfLoaded(long pos) {
        return (Optional)this.loadedElements.get(pos);
    }

    protected Optional<R> get(long pos) {
        if (this.isPosInvalid(pos)) {
            return Optional.empty();
        }
        Optional<R> optional = this.getIfLoaded(pos);
        if (optional != null) {
            return optional;
        }
        this.loadAndWait(ChunkSectionPos.from(pos).toChunkPos());
        optional = this.getIfLoaded(pos);
        if (optional == null) {
            throw Util.getFatalOrPause(new IllegalStateException());
        }
        return optional;
    }

    protected boolean isPosInvalid(long pos) {
        int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(pos));
        return this.world.isOutOfHeightLimit(i);
    }

    protected R getOrCreate(long pos) {
        if (this.isPosInvalid(pos)) {
            throw Util.getFatalOrPause(new IllegalArgumentException("sectionPos out of bounds"));
        }
        Optional<R> optional = this.get(pos);
        if (optional.isPresent()) {
            return optional.get();
        }
        R object = this.factory.apply(() -> this.onUpdate(pos));
        this.loadedElements.put(pos, Optional.of(object));
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> load(ChunkPos chunkPos) {
        Object object = this.lock;
        synchronized (object) {
            long l = chunkPos.toLong();
            if (this.loadedChunks.contains(l)) {
                return CompletableFuture.completedFuture(null);
            }
            return this.pendingLoads.computeIfAbsent(l, pos -> this.loadNbt(chunkPos));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadAndWait(ChunkPos chunkPos) {
        CompletableFuture completableFuture;
        long l = chunkPos.toLong();
        Object object = this.lock;
        synchronized (object) {
            if (!this.loadedChunks.add(l)) {
                return;
            }
            completableFuture = this.pendingLoads.computeIfAbsent(l, pos -> this.loadNbt(chunkPos));
        }
        this.onLoad(chunkPos, ((Optional)completableFuture.join()).orElse(null));
        object = this.lock;
        synchronized (object) {
            this.pendingLoads.remove(l);
        }
    }

    private CompletableFuture<Optional<LoadResult<P>>> loadNbt(ChunkPos chunkPos) {
        RegistryOps<NbtElement> lv = this.registryManager.getOps(NbtOps.INSTANCE);
        return ((CompletableFuture)this.storageAccess.read(chunkPos).thenApplyAsync(chunkNbt -> chunkNbt.map(nbt -> LoadResult.fromNbt(this.codec, lv, nbt, this.storageAccess, this.world)), Util.getMainWorkerExecutor().named("parseSection"))).exceptionally(throwable -> {
            if (throwable instanceof CompletionException) {
                throwable = throwable.getCause();
            }
            if (throwable instanceof IOException) {
                IOException iOException = (IOException)throwable;
                LOGGER.error("Error reading chunk {} data from disk", (Object)chunkPos, (Object)iOException);
                this.errorHandler.onChunkLoadFailure(iOException, this.storageAccess.getStorageKey(), chunkPos);
                return Optional.empty();
            }
            throw new CompletionException((Throwable)throwable);
        });
    }

    private void onLoad(ChunkPos chunkPos, @Nullable LoadResult<P> result) {
        if (result == null) {
            for (int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
                this.loadedElements.put(SerializingRegionBasedStorage.chunkSectionPosAsLong(chunkPos, i), (Optional<R>)Optional.empty());
            }
        } else {
            boolean bl = result.versionChanged();
            for (int j = this.world.getBottomSectionCoord(); j <= this.world.getTopSectionCoord(); ++j) {
                long l = SerializingRegionBasedStorage.chunkSectionPosAsLong(chunkPos, j);
                Optional<Object> optional = Optional.ofNullable(result.sectionsByY.get(j)).map(section -> this.deserializer.apply(section, () -> this.onUpdate(l)));
                this.loadedElements.put(l, (Optional<R>)optional);
                optional.ifPresent(object -> {
                    this.onLoad(l);
                    if (bl) {
                        this.onUpdate(l);
                    }
                });
            }
        }
    }

    private void save(ChunkPos pos) {
        RegistryOps<NbtElement> lv = this.registryManager.getOps(NbtOps.INSTANCE);
        Dynamic<NbtElement> dynamic = this.serialize(pos, lv);
        NbtElement lv2 = dynamic.getValue();
        if (lv2 instanceof NbtCompound) {
            this.storageAccess.set(pos, (NbtCompound)lv2).exceptionally(throwable -> {
                this.errorHandler.onChunkSaveFailure((Throwable)throwable, this.storageAccess.getStorageKey(), pos);
                return null;
            });
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)lv2);
        }
    }

    private <T> Dynamic<T> serialize(ChunkPos chunkPos, DynamicOps<T> ops) {
        HashMap map = Maps.newHashMap();
        for (int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
            long l = SerializingRegionBasedStorage.chunkSectionPosAsLong(chunkPos, i);
            Optional optional = (Optional)this.loadedElements.get(l);
            if (optional == null || optional.isEmpty()) continue;
            DataResult<T> dataResult = this.codec.encodeStart(ops, this.serializer.apply(optional.get()));
            String string = Integer.toString(i);
            dataResult.resultOrPartial(LOGGER::error).ifPresent(value -> map.put(ops.createString(string), value));
        }
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString(SECTIONS_KEY), ops.createMap(map), ops.createString("DataVersion"), ops.createInt(SharedConstants.getGameVersion().getSaveVersion().getId()))));
    }

    private static long chunkSectionPosAsLong(ChunkPos chunkPos, int y) {
        return ChunkSectionPos.asLong(chunkPos.x, y, chunkPos.z);
    }

    protected void onLoad(long pos) {
    }

    protected void onUpdate(long pos) {
        Optional optional = (Optional)this.loadedElements.get(pos);
        if (optional == null || optional.isEmpty()) {
            LOGGER.warn("No data for position: {}", (Object)ChunkSectionPos.from(pos));
            return;
        }
        this.unsavedElements.add(ChunkPos.toLong(ChunkSectionPos.unpackX(pos), ChunkSectionPos.unpackZ(pos)));
    }

    static int getDataVersion(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asInt(1945);
    }

    public void saveChunk(ChunkPos pos) {
        if (this.unsavedElements.remove(pos.toLong())) {
            this.save(pos);
        }
    }

    @Override
    public void close() throws IOException {
        this.storageAccess.close();
    }

    record LoadResult<T>(Int2ObjectMap<T> sectionsByY, boolean versionChanged) {
        public static <T> LoadResult<T> fromNbt(Codec<T> sectionCodec, DynamicOps<NbtElement> ops, NbtElement nbt, ChunkPosKeyedStorage storage, HeightLimitView world) {
            int j;
            Dynamic<NbtElement> dynamic = new Dynamic<NbtElement>(ops, nbt);
            int i = SerializingRegionBasedStorage.getDataVersion(dynamic);
            boolean bl = i != (j = SharedConstants.getGameVersion().getSaveVersion().getId());
            Dynamic<NbtElement> dynamic2 = storage.update(dynamic, i);
            OptionalDynamic<NbtElement> optionalDynamic = dynamic2.get(SerializingRegionBasedStorage.SECTIONS_KEY);
            Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
            for (int k = world.getBottomSectionCoord(); k <= world.getTopSectionCoord(); ++k) {
                Optional optional = optionalDynamic.get(Integer.toString(k)).result().flatMap(section -> sectionCodec.parse(section).resultOrPartial(LOGGER::error));
                if (!optional.isPresent()) continue;
                int2ObjectMap.put(k, optional.get());
            }
            return new LoadResult(int2ObjectMap, bl);
        }
    }
}

