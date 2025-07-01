/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.ClosableFactory;

@Environment(value=EnvType.CLIENT)
public record SimpleFramebufferFactory(int width, int height, boolean useDepth) implements ClosableFactory<Framebuffer>
{
    @Override
    public Framebuffer create() {
        return new SimpleFramebuffer(this.width, this.height, this.useDepth);
    }

    @Override
    public void close(Framebuffer arg) {
        arg.delete();
    }

    @Override
    public /* synthetic */ Object create() {
        return this.create();
    }
}

