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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class VertexRendering {
    public static void drawOutline(MatrixStack matrices, VertexConsumer vertexConsumers, VoxelShape shape, double offsetX, double offsetY, double offsetZ, int color) {
        MatrixStack.Entry lv = matrices.peek();
        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            Vector3f vector3f = new Vector3f((float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1)).normalize();
            vertexConsumers.vertex(lv, (float)(x1 + offsetX), (float)(y1 + offsetY), (float)(z1 + offsetZ)).color(color).normal(lv, vector3f);
            vertexConsumers.vertex(lv, (float)(x2 + offsetX), (float)(y2 + offsetY), (float)(z2 + offsetZ)).color(color).normal(lv, vector3f);
        });
    }

    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumers, Box box, float red, float green, float blue, float alpha) {
        VertexRendering.drawBox(matrices, vertexConsumers, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumers, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        VertexRendering.drawBox(matrices, vertexConsumers, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumers, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha, float xAxisRed, float yAxisGreen, float zAxisBlue) {
        MatrixStack.Entry lv = matrices.peek();
        float q = (float)x1;
        float r = (float)y1;
        float s = (float)z1;
        float t = (float)x2;
        float u = (float)y2;
        float v = (float)z2;
        vertexConsumers.vertex(lv, q, r, s).color(red, yAxisGreen, zAxisBlue, alpha).normal(lv, 1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, t, r, s).color(red, yAxisGreen, zAxisBlue, alpha).normal(lv, 1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, q, r, s).color(xAxisRed, green, zAxisBlue, alpha).normal(lv, 0.0f, 1.0f, 0.0f);
        vertexConsumers.vertex(lv, q, u, s).color(xAxisRed, green, zAxisBlue, alpha).normal(lv, 0.0f, 1.0f, 0.0f);
        vertexConsumers.vertex(lv, q, r, s).color(xAxisRed, yAxisGreen, blue, alpha).normal(lv, 0.0f, 0.0f, 1.0f);
        vertexConsumers.vertex(lv, q, r, v).color(xAxisRed, yAxisGreen, blue, alpha).normal(lv, 0.0f, 0.0f, 1.0f);
        vertexConsumers.vertex(lv, t, r, s).color(red, green, blue, alpha).normal(lv, 0.0f, 1.0f, 0.0f);
        vertexConsumers.vertex(lv, t, u, s).color(red, green, blue, alpha).normal(lv, 0.0f, 1.0f, 0.0f);
        vertexConsumers.vertex(lv, t, u, s).color(red, green, blue, alpha).normal(lv, -1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, q, u, s).color(red, green, blue, alpha).normal(lv, -1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, q, u, s).color(red, green, blue, alpha).normal(lv, 0.0f, 0.0f, 1.0f);
        vertexConsumers.vertex(lv, q, u, v).color(red, green, blue, alpha).normal(lv, 0.0f, 0.0f, 1.0f);
        vertexConsumers.vertex(lv, q, u, v).color(red, green, blue, alpha).normal(lv, 0.0f, -1.0f, 0.0f);
        vertexConsumers.vertex(lv, q, r, v).color(red, green, blue, alpha).normal(lv, 0.0f, -1.0f, 0.0f);
        vertexConsumers.vertex(lv, q, r, v).color(red, green, blue, alpha).normal(lv, 1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, t, r, v).color(red, green, blue, alpha).normal(lv, 1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, t, r, v).color(red, green, blue, alpha).normal(lv, 0.0f, 0.0f, -1.0f);
        vertexConsumers.vertex(lv, t, r, s).color(red, green, blue, alpha).normal(lv, 0.0f, 0.0f, -1.0f);
        vertexConsumers.vertex(lv, q, u, v).color(red, green, blue, alpha).normal(lv, 1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, t, u, v).color(red, green, blue, alpha).normal(lv, 1.0f, 0.0f, 0.0f);
        vertexConsumers.vertex(lv, t, r, v).color(red, green, blue, alpha).normal(lv, 0.0f, 1.0f, 0.0f);
        vertexConsumers.vertex(lv, t, u, v).color(red, green, blue, alpha).normal(lv, 0.0f, 1.0f, 0.0f);
        vertexConsumers.vertex(lv, t, u, s).color(red, green, blue, alpha).normal(lv, 0.0f, 0.0f, 1.0f);
        vertexConsumers.vertex(lv, t, u, v).color(red, green, blue, alpha).normal(lv, 0.0f, 0.0f, 1.0f);
    }

    public static void drawFilledBox(MatrixStack matrices, VertexConsumer vertexConsumers, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        VertexRendering.drawFilledBox(matrices, vertexConsumers, (float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ, red, green, blue, alpha);
    }

    public static void drawFilledBox(MatrixStack matrices, VertexConsumer vertexConsumers, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
    }

    public static void drawSide(MatrixStack matrices, VertexConsumer vertexConsumers, Direction side, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        switch (side) {
            case DOWN: {
                vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
                break;
            }
            case UP: {
                vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
                break;
            }
            case NORTH: {
                vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
                break;
            }
            case SOUTH: {
                vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
                break;
            }
            case WEST: {
                vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
                break;
            }
            case EAST: {
                vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
                vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
            }
        }
    }

    public static void drawVector(MatrixStack matrices, VertexConsumer vertexConsumers, Vector3f offset, Vec3d vec, int argb) {
        MatrixStack.Entry lv = matrices.peek();
        vertexConsumers.vertex(lv, offset).color(argb).normal(lv, (float)vec.x, (float)vec.y, (float)vec.z);
        vertexConsumers.vertex(lv, (float)((double)offset.x() + vec.x), (float)((double)offset.y() + vec.y), (float)((double)offset.z() + vec.z)).color(argb).normal(lv, (float)vec.x, (float)vec.y, (float)vec.z);
    }
}

