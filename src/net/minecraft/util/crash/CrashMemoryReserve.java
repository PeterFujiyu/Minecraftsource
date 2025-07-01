/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util.crash;

import org.jetbrains.annotations.Nullable;

public class CrashMemoryReserve {
    @Nullable
    private static byte[] reservedMemory;

    public static void reserveMemory() {
        reservedMemory = new byte[0xA00000];
    }

    public static void releaseMemory() {
        if (reservedMemory != null) {
            reservedMemory = null;
            try {
                System.gc();
                System.gc();
                System.gc();
            } catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

