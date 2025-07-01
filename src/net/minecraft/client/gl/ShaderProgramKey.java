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
import net.minecraft.client.gl.Defines;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ShaderProgramKey(Identifier configId, VertexFormat vertexFormat, Defines defines) {
    @Override
    public String toString() {
        String string = String.valueOf(this.configId) + " (" + String.valueOf(this.vertexFormat) + ")";
        if (!this.defines.isEmpty()) {
            return string + " with " + String.valueOf(this.defines);
        }
        return string;
    }
}

