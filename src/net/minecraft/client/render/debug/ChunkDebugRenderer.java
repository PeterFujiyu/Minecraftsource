/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class ChunkDebugRenderer
implements DebugRenderer.Renderer {
    public static final Direction[] DIRECTIONS = Direction.values();
    private final MinecraftClient client;

    public ChunkDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Frustum lv11;
        WorldRenderer lv = this.client.worldRenderer;
        if (this.client.debugChunkInfo || this.client.debugChunkOcclusion) {
            ChunkRenderingDataPreparer lv2 = lv.getChunkRenderingDataPreparer();
            for (ChunkBuilder.BuiltChunk lv3 : lv.getBuiltChunks()) {
                int i;
                VertexConsumer lv6;
                ChunkRenderingDataPreparer.ChunkInfo lv4 = lv2.getInfo(lv3);
                if (lv4 == null) continue;
                BlockPos lv5 = lv3.getOrigin();
                matrices.push();
                matrices.translate((double)lv5.getX() - cameraX, (double)lv5.getY() - cameraY, (double)lv5.getZ() - cameraZ);
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                if (this.client.debugChunkInfo) {
                    lv6 = vertexConsumers.getBuffer(RenderLayer.getLines());
                    i = lv4.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)lv4.propagationLevel / 50.0f, 0.9f, 0.9f);
                    int j = i >> 16 & 0xFF;
                    int k = i >> 8 & 0xFF;
                    int l = i & 0xFF;
                    for (int m = 0; m < DIRECTIONS.length; ++m) {
                        if (!lv4.hasDirection(m)) continue;
                        Direction lv7 = DIRECTIONS[m];
                        lv6.vertex(matrix4f, 8.0f, 8.0f, 8.0f).color(j, k, l, 255).normal(lv7.getOffsetX(), lv7.getOffsetY(), lv7.getOffsetZ());
                        lv6.vertex(matrix4f, (float)(8 - 16 * lv7.getOffsetX()), (float)(8 - 16 * lv7.getOffsetY()), (float)(8 - 16 * lv7.getOffsetZ())).color(j, k, l, 255).normal(lv7.getOffsetX(), lv7.getOffsetY(), lv7.getOffsetZ());
                    }
                }
                if (this.client.debugChunkOcclusion && lv3.getData().hasNonEmptyLayers()) {
                    lv6 = vertexConsumers.getBuffer(RenderLayer.getLines());
                    i = 0;
                    for (Direction lv8 : DIRECTIONS) {
                        for (Direction lv9 : DIRECTIONS) {
                            boolean bl = lv3.getData().isVisibleThrough(lv8, lv9);
                            if (bl) continue;
                            ++i;
                            lv6.vertex(matrix4f, (float)(8 + 8 * lv8.getOffsetX()), (float)(8 + 8 * lv8.getOffsetY()), (float)(8 + 8 * lv8.getOffsetZ())).color(255, 0, 0, 255).normal(lv8.getOffsetX(), lv8.getOffsetY(), lv8.getOffsetZ());
                            lv6.vertex(matrix4f, (float)(8 + 8 * lv9.getOffsetX()), (float)(8 + 8 * lv9.getOffsetY()), (float)(8 + 8 * lv9.getOffsetZ())).color(255, 0, 0, 255).normal(lv9.getOffsetX(), lv9.getOffsetY(), lv9.getOffsetZ());
                        }
                    }
                    if (i > 0) {
                        VertexConsumer lv10 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
                        float g = 0.5f;
                        float h = 0.2f;
                        lv10.vertex(matrix4f, 0.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 15.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        lv10.vertex(matrix4f, 0.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                    }
                }
                matrices.pop();
            }
        }
        if ((lv11 = lv.getCapturedFrustum()) != null) {
            matrices.push();
            matrices.translate((float)(lv11.getX() - cameraX), (float)(lv11.getY() - cameraY), (float)(lv11.getZ() - cameraZ));
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            Vector4f[] vector4fs = lv11.getBoundaryPoints();
            VertexConsumer lv12 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
            this.addFace(lv12, matrix4f2, vector4fs, 0, 1, 2, 3, 0, 1, 1);
            this.addFace(lv12, matrix4f2, vector4fs, 4, 5, 6, 7, 1, 0, 0);
            this.addFace(lv12, matrix4f2, vector4fs, 0, 1, 5, 4, 1, 1, 0);
            this.addFace(lv12, matrix4f2, vector4fs, 2, 3, 7, 6, 0, 0, 1);
            this.addFace(lv12, matrix4f2, vector4fs, 0, 4, 7, 3, 0, 1, 0);
            this.addFace(lv12, matrix4f2, vector4fs, 1, 5, 6, 2, 1, 0, 1);
            VertexConsumer lv13 = vertexConsumers.getBuffer(RenderLayer.getLines());
            this.addEndpoint(lv13, matrix4f2, vector4fs[0]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[1]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[1]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[2]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[2]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[3]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[3]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[0]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[4]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[5]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[5]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[6]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[6]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[7]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[7]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[4]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[0]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[4]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[1]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[5]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[2]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[6]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[3]);
            this.addEndpoint(lv13, matrix4f2, vector4fs[7]);
            matrices.pop();
        }
    }

    private void addEndpoint(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector4f vertex) {
        vertexConsumer.vertex(positionMatrix, vertex.x(), vertex.y(), vertex.z()).color(Colors.BLACK).normal(0.0f, 0.0f, -1.0f);
    }

    private void addFace(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector4f[] vertices, int i1, int i2, int i3, int i4, int r, int g, int b) {
        float f = 0.25f;
        vertexConsumer.vertex(positionMatrix, vertices[i1].x(), vertices[i1].y(), vertices[i1].z()).color((float)r, (float)g, (float)b, 0.25f);
        vertexConsumer.vertex(positionMatrix, vertices[i2].x(), vertices[i2].y(), vertices[i2].z()).color((float)r, (float)g, (float)b, 0.25f);
        vertexConsumer.vertex(positionMatrix, vertices[i3].x(), vertices[i3].y(), vertices[i3].z()).color((float)r, (float)g, (float)b, 0.25f);
        vertexConsumer.vertex(positionMatrix, vertices[i4].x(), vertices[i4].y(), vertices[i4].z()).color((float)r, (float)g, (float)b, 0.25f);
    }
}

