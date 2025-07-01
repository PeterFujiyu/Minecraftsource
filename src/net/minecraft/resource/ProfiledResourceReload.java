/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.resource;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.slf4j.Logger;

public class ProfiledResourceReload
extends SimpleResourceReload<Summary> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Stopwatch reloadTimer = Stopwatch.createUnstarted();

    public ProfiledResourceReload(ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage) {
        super(prepareExecutor, applyExecutor, manager, reloaders, (arg, arg2, arg3, executor2, executor3) -> {
            AtomicLong atomicLong = new AtomicLong();
            AtomicLong atomicLong2 = new AtomicLong();
            CompletableFuture<Void> completableFuture = arg3.reload(arg, arg2, ProfiledResourceReload.method_64141(executor2, atomicLong, arg3.getName()), ProfiledResourceReload.method_64141(executor3, atomicLong2, arg3.getName()));
            return completableFuture.thenApplyAsync(void_ -> {
                LOGGER.debug("Finished reloading {}", (Object)arg3.getName());
                return new Summary(arg3.getName(), atomicLong, atomicLong2);
            }, applyExecutor);
        }, initialStage);
        this.reloadTimer.start();
        this.applyStageFuture = this.applyStageFuture.thenApplyAsync(this::finish, applyExecutor);
    }

    private static Executor method_64141(Executor executor, AtomicLong atomicLong, String string) {
        return runnable -> executor.execute(() -> {
            Profiler lv = Profilers.get();
            lv.push(string);
            long l = Util.getMeasuringTimeNano();
            runnable.run();
            atomicLong.addAndGet(Util.getMeasuringTimeNano() - l);
            lv.pop();
        });
    }

    private List<Summary> finish(List<Summary> summaries) {
        this.reloadTimer.stop();
        long l = 0L;
        LOGGER.info("Resource reload finished after {} ms", (Object)this.reloadTimer.elapsed(TimeUnit.MILLISECONDS));
        for (Summary lv : summaries) {
            long m = TimeUnit.NANOSECONDS.toMillis(lv.prepareTimeMs.get());
            long n = TimeUnit.NANOSECONDS.toMillis(lv.applyTimeMs.get());
            long o = m + n;
            String string = lv.name;
            LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", string, o, m, n);
            l += n;
        }
        LOGGER.info("Total blocking time: {} ms", (Object)l);
        return summaries;
    }

    public record Summary(String name, AtomicLong prepareTimeMs, AtomicLong applyTimeMs) {
    }
}

