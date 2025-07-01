/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.server;

import java.util.concurrent.Executor;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class QueueingWorldGenerationProgressListener
implements WorldGenerationProgressListener {
    private final WorldGenerationProgressListener progressListener;
    private final SimpleConsecutiveExecutor executor;
    private boolean running;

    private QueueingWorldGenerationProgressListener(WorldGenerationProgressListener progressListener, Executor executor) {
        this.progressListener = progressListener;
        this.executor = new SimpleConsecutiveExecutor(executor, "progressListener");
    }

    public static QueueingWorldGenerationProgressListener create(WorldGenerationProgressListener progressListener, Executor executor) {
        QueueingWorldGenerationProgressListener lv = new QueueingWorldGenerationProgressListener(progressListener, executor);
        lv.start();
        return lv;
    }

    @Override
    public void start(ChunkPos spawnPos) {
        this.executor.send(() -> this.progressListener.start(spawnPos));
    }

    @Override
    public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
        if (this.running) {
            this.executor.send(() -> this.progressListener.setChunkStatus(pos, status));
        }
    }

    @Override
    public void start() {
        this.running = true;
        this.executor.send(this.progressListener::start);
    }

    @Override
    public void stop() {
        this.running = false;
        this.executor.send(this.progressListener::stop);
    }
}

