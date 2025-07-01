/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PaintingEntityRenderState;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class PaintingEntityRenderer
extends EntityRenderer<PaintingEntity, PaintingEntityRenderState> {
    public PaintingEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
    }

    @Override
    public void render(PaintingEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        PaintingVariant lv = arg.variant;
        if (lv == null) {
            return;
        }
        arg2.push();
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - arg.facing.getHorizontalQuarterTurns() * 90));
        PaintingManager lv2 = MinecraftClient.getInstance().getPaintingManager();
        Sprite lv3 = lv2.getBackSprite();
        VertexConsumer lv4 = arg3.getBuffer(RenderLayer.getEntitySolidZOffsetForward(lv3.getAtlasId()));
        this.renderPainting(arg2, lv4, arg.lightmapCoordinates, lv.width(), lv.height(), lv2.getPaintingSprite(lv), lv3);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public PaintingEntityRenderState createRenderState() {
        return new PaintingEntityRenderState();
    }

    @Override
    public void updateRenderState(PaintingEntity arg, PaintingEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        Direction lv = arg.getHorizontalFacing();
        PaintingVariant lv2 = (PaintingVariant)arg.getVariant().value();
        arg2.facing = lv;
        arg2.variant = lv2;
        int i = lv2.width();
        int j = lv2.height();
        if (arg2.lightmapCoordinates.length != i * j) {
            arg2.lightmapCoordinates = new int[i * j];
        }
        float g = (float)(-i) / 2.0f;
        float h = (float)(-j) / 2.0f;
        World lv3 = arg.getWorld();
        for (int k = 0; k < j; ++k) {
            for (int l = 0; l < i; ++l) {
                float m = (float)l + g + 0.5f;
                float n = (float)k + h + 0.5f;
                int o = arg.getBlockX();
                int p = MathHelper.floor(arg.getY() + (double)n);
                int q = arg.getBlockZ();
                switch (lv) {
                    case NORTH: {
                        o = MathHelper.floor(arg.getX() + (double)m);
                        break;
                    }
                    case WEST: {
                        q = MathHelper.floor(arg.getZ() - (double)m);
                        break;
                    }
                    case SOUTH: {
                        o = MathHelper.floor(arg.getX() - (double)m);
                        break;
                    }
                    case EAST: {
                        q = MathHelper.floor(arg.getZ() + (double)m);
                    }
                }
                arg2.lightmapCoordinates[l + k * i] = WorldRenderer.getLightmapCoordinates(lv3, new BlockPos(o, p, q));
            }
        }
    }

    private void renderPainting(MatrixStack matrices, VertexConsumer vertexConsumer, int[] lightmapCoordinates, int width, int height, Sprite paintingSprite, Sprite backSprite) {
        MatrixStack.Entry lv = matrices.peek();
        float f = (float)(-width) / 2.0f;
        float g = (float)(-height) / 2.0f;
        float h = 0.03125f;
        float k = backSprite.getMinU();
        float l = backSprite.getMaxU();
        float m = backSprite.getMinV();
        float n = backSprite.getMaxV();
        float o = backSprite.getMinU();
        float p = backSprite.getMaxU();
        float q = backSprite.getMinV();
        float r = backSprite.getFrameV(0.0625f);
        float s = backSprite.getMinU();
        float t = backSprite.getFrameU(0.0625f);
        float u = backSprite.getMinV();
        float v = backSprite.getMaxV();
        double d = 1.0 / (double)width;
        double e = 1.0 / (double)height;
        for (int w = 0; w < width; ++w) {
            for (int x = 0; x < height; ++x) {
                float y = f + (float)(w + 1);
                float z = f + (float)w;
                float aa = g + (float)(x + 1);
                float ab = g + (float)x;
                int ac = lightmapCoordinates[w + x * width];
                float ad = paintingSprite.getFrameU((float)(d * (double)(width - w)));
                float ae = paintingSprite.getFrameU((float)(d * (double)(width - (w + 1))));
                float af = paintingSprite.getFrameV((float)(e * (double)(height - x)));
                float ag = paintingSprite.getFrameV((float)(e * (double)(height - (x + 1))));
                this.vertex(lv, vertexConsumer, y, ab, ae, af, -0.03125f, 0, 0, -1, ac);
                this.vertex(lv, vertexConsumer, z, ab, ad, af, -0.03125f, 0, 0, -1, ac);
                this.vertex(lv, vertexConsumer, z, aa, ad, ag, -0.03125f, 0, 0, -1, ac);
                this.vertex(lv, vertexConsumer, y, aa, ae, ag, -0.03125f, 0, 0, -1, ac);
                this.vertex(lv, vertexConsumer, y, aa, l, m, 0.03125f, 0, 0, 1, ac);
                this.vertex(lv, vertexConsumer, z, aa, k, m, 0.03125f, 0, 0, 1, ac);
                this.vertex(lv, vertexConsumer, z, ab, k, n, 0.03125f, 0, 0, 1, ac);
                this.vertex(lv, vertexConsumer, y, ab, l, n, 0.03125f, 0, 0, 1, ac);
                this.vertex(lv, vertexConsumer, y, aa, o, q, -0.03125f, 0, 1, 0, ac);
                this.vertex(lv, vertexConsumer, z, aa, p, q, -0.03125f, 0, 1, 0, ac);
                this.vertex(lv, vertexConsumer, z, aa, p, r, 0.03125f, 0, 1, 0, ac);
                this.vertex(lv, vertexConsumer, y, aa, o, r, 0.03125f, 0, 1, 0, ac);
                this.vertex(lv, vertexConsumer, y, ab, o, q, 0.03125f, 0, -1, 0, ac);
                this.vertex(lv, vertexConsumer, z, ab, p, q, 0.03125f, 0, -1, 0, ac);
                this.vertex(lv, vertexConsumer, z, ab, p, r, -0.03125f, 0, -1, 0, ac);
                this.vertex(lv, vertexConsumer, y, ab, o, r, -0.03125f, 0, -1, 0, ac);
                this.vertex(lv, vertexConsumer, y, aa, t, u, 0.03125f, -1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, y, ab, t, v, 0.03125f, -1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, y, ab, s, v, -0.03125f, -1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, y, aa, s, u, -0.03125f, -1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, z, aa, t, u, -0.03125f, 1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, z, ab, t, v, -0.03125f, 1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, z, ab, s, v, 0.03125f, 1, 0, 0, ac);
                this.vertex(lv, vertexConsumer, z, aa, s, u, 0.03125f, 1, 0, 0, ac);
            }
        }
    }

    private void vertex(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light) {
        vertexConsumer.vertex(matrix, x, y, z).color(Colors.WHITE).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, normalX, normalY, normalZ);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

