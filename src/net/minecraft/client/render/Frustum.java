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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class Frustum {
    public static final int RECESSION_SCALE = 4;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();
    private final Matrix4f positionProjectionMatrix = new Matrix4f();
    private Vector4f recession;
    private double x;
    private double y;
    private double z;

    public Frustum(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        this.init(positionMatrix, projectionMatrix);
    }

    public Frustum(Frustum frustum) {
        this.frustumIntersection.set(frustum.positionProjectionMatrix);
        this.positionProjectionMatrix.set(frustum.positionProjectionMatrix);
        this.x = frustum.x;
        this.y = frustum.y;
        this.z = frustum.z;
        this.recession = frustum.recession;
    }

    public Frustum coverBoxAroundSetPosition(int boxSize) {
        double d = Math.floor(this.x / (double)boxSize) * (double)boxSize;
        double e = Math.floor(this.y / (double)boxSize) * (double)boxSize;
        double f = Math.floor(this.z / (double)boxSize) * (double)boxSize;
        double g = Math.ceil(this.x / (double)boxSize) * (double)boxSize;
        double h = Math.ceil(this.y / (double)boxSize) * (double)boxSize;
        double j = Math.ceil(this.z / (double)boxSize) * (double)boxSize;
        while (this.frustumIntersection.intersectAab((float)(d - this.x), (float)(e - this.y), (float)(f - this.z), (float)(g - this.x), (float)(h - this.y), (float)(j - this.z)) != -2) {
            this.x -= (double)(this.recession.x() * 4.0f);
            this.y -= (double)(this.recession.y() * 4.0f);
            this.z -= (double)(this.recession.z() * 4.0f);
        }
        return this;
    }

    public void setPosition(double cameraX, double cameraY, double cameraZ) {
        this.x = cameraX;
        this.y = cameraY;
        this.z = cameraZ;
    }

    private void init(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        projectionMatrix.mul(positionMatrix, this.positionProjectionMatrix);
        this.frustumIntersection.set(this.positionProjectionMatrix);
        this.recession = this.positionProjectionMatrix.transformTranspose(new Vector4f(0.0f, 0.0f, 1.0f, 0.0f));
    }

    public boolean isVisible(Box box) {
        int i = this.intersectAab(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        return i == -2 || i == -1;
    }

    public int intersectAab(BlockBox box) {
        return this.intersectAab(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX() + 1, box.getMaxY() + 1, box.getMaxZ() + 1);
    }

    private int intersectAab(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float j = (float)(minX - this.x);
        float k = (float)(minY - this.y);
        float l = (float)(minZ - this.z);
        float m = (float)(maxX - this.x);
        float n = (float)(maxY - this.y);
        float o = (float)(maxZ - this.z);
        return this.frustumIntersection.intersectAab(j, k, l, m, n, o);
    }

    public Vector4f[] getBoundaryPoints() {
        Vector4f[] vector4fs = new Vector4f[]{new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f), new Vector4f(1.0f, -1.0f, -1.0f, 1.0f), new Vector4f(1.0f, 1.0f, -1.0f, 1.0f), new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f), new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f), new Vector4f(1.0f, -1.0f, 1.0f, 1.0f), new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f)};
        Matrix4f matrix4f = this.positionProjectionMatrix.invert(new Matrix4f());
        for (int i = 0; i < 8; ++i) {
            matrix4f.transform(vector4fs[i]);
            vector4fs[i].div(vector4fs[i].w());
        }
        return vector4fs;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}

