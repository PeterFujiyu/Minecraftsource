/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public enum ProjectionType {
    PERSPECTIVE(VertexSorter.BY_DISTANCE, (matrix, f) -> matrix.scale(1.0f - f / 4096.0f)),
    ORTHOGRAPHIC(VertexSorter.BY_Z, (matrix, f) -> matrix.translate(0.0f, 0.0f, f / 512.0f));

    private final VertexSorter vertexSorter;
    private final Applier applier;

    private ProjectionType(VertexSorter vertexSorter, Applier applier) {
        this.vertexSorter = vertexSorter;
        this.applier = applier;
    }

    public VertexSorter getVertexSorter() {
        return this.vertexSorter;
    }

    public void apply(Matrix4f matrix, float f) {
        this.applier.apply(matrix, f);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface Applier {
        public void apply(Matrix4f var1, float var2);
    }
}

