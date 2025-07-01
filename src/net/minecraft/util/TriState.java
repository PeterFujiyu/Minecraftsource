/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util;

public enum TriState {
    TRUE,
    FALSE,
    DEFAULT;


    public boolean asBoolean(boolean fallback) {
        return switch (this.ordinal()) {
            case 0 -> true;
            case 1 -> false;
            default -> fallback;
        };
    }
}

