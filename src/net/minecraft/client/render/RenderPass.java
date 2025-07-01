/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.Handle;

@Environment(value=EnvType.CLIENT)
public interface RenderPass {
    public <T> Handle<T> addRequiredResource(String var1, ClosableFactory<T> var2);

    public <T> void dependsOn(Handle<T> var1);

    public <T> Handle<T> transfer(Handle<T> var1);

    public void addRequired(RenderPass var1);

    public void markToBeVisited();

    public void setRenderer(Runnable var1);
}

