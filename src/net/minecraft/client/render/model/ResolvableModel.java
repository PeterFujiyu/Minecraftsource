/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface ResolvableModel {
    public void resolve(Resolver var1);

    @Environment(value=EnvType.CLIENT)
    public static interface Resolver {
        public UnbakedModel resolve(Identifier var1);
    }
}

