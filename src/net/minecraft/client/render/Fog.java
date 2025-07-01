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
import net.minecraft.client.render.FogShape;

@Environment(value=EnvType.CLIENT)
public record Fog(float start, float end, FogShape shape, float red, float green, float blue, float alpha) {
    public static final Fog DUMMY = new Fog(Float.MAX_VALUE, 0.0f, FogShape.SPHERE, 0.0f, 0.0f, 0.0f, 0.0f);
}

