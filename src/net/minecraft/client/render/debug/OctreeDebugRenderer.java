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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(value=EnvType.CLIENT)
public class OctreeDebugRenderer {
    private final MinecraftClient client;

    public OctreeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrices, Frustum frustum, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Octree lv = this.client.worldRenderer.getChunkRenderingDataPreparer().getOctree();
        MutableInt mutableInt = new MutableInt(0);
        lv.visit((node, skipVisibilityCheck, depth, bl2) -> this.renderNode(node, matrices, vertexConsumers, cameraX, cameraY, cameraZ, depth, skipVisibilityCheck, mutableInt, bl2), frustum, 32);
    }

    private void renderNode(Octree.Node node, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, int depth, boolean skipVisibilityCheck, MutableInt id, boolean bl2) {
        Box lv = node.getBoundingBox();
        double g = lv.getLengthX();
        long l = Math.round(g / 16.0);
        if (l == 1L) {
            id.add(1);
            double h = lv.getCenter().x;
            double j = lv.getCenter().y;
            double k = lv.getCenter().z;
            int m = bl2 ? Colors.GREEN : Colors.WHITE;
            DebugRenderer.drawString(matrices, vertexConsumers, String.valueOf(id.getValue()), h, j, k, m, 0.3f);
        }
        VertexConsumer lv2 = vertexConsumers.getBuffer(RenderLayer.getLines());
        long n = l + 5L;
        VertexRendering.drawBox(matrices, lv2, lv.contract(0.1 * (double)depth).offset(-cameraX, -cameraY, -cameraZ), OctreeDebugRenderer.getColorComponent(n, 0.3f), OctreeDebugRenderer.getColorComponent(n, 0.8f), OctreeDebugRenderer.getColorComponent(n, 0.5f), skipVisibilityCheck ? 0.4f : 1.0f);
    }

    private static float getColorComponent(long size, float gradient) {
        float g = 0.1f;
        return MathHelper.fractionalPart(gradient * (float)size) * 0.9f + 0.1f;
    }
}

