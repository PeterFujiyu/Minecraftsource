/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class GlFenceSync
implements AutoCloseable {
    private long handle = GlStateManager._glFenceSync(37143, 0);

    @Override
    public void close() {
        if (this.handle != 0L) {
            GlStateManager._glDeleteSync(this.handle);
            this.handle = 0L;
        }
    }

    public boolean wait(long timeoutNanos) {
        if (this.handle == 0L) {
            return true;
        }
        int i = GlStateManager._glClientWaitSync(this.handle, 0, timeoutNanos);
        if (i == 37147) {
            return false;
        }
        if (i == 37149) {
            throw new IllegalStateException("Failed to complete gpu fence");
        }
        return true;
    }
}

