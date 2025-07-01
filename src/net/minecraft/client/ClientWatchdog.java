/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import java.io.File;
import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.util.crash.CrashReport;

@Environment(value=EnvType.CLIENT)
public class ClientWatchdog {
    private static final Duration timeout = Duration.ofSeconds(15L);

    public static void shutdownClient(File runDir, long threadId) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException interruptedException) {
                return;
            }
            CrashReport lv = DedicatedServerWatchdog.createCrashReport("Client shutdown", threadId);
            MinecraftClient.saveCrashReport(runDir, lv);
        });
        thread.setDaemon(true);
        thread.setName("Client shutdown watchdog");
        thread.start();
    }
}

