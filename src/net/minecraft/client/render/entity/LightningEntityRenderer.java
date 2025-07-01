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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LightningEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class LightningEntityRenderer
extends EntityRenderer<LightningEntity, LightningEntityRenderState> {
    public LightningEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
    }

    @Override
    public void render(LightningEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float[] fs = new float[8];
        float[] gs = new float[8];
        float f = 0.0f;
        float g = 0.0f;
        Random lv = Random.create(arg.seed);
        for (int j = 7; j >= 0; --j) {
            fs[j] = f;
            gs[j] = g;
            f += (float)(lv.nextInt(11) - 5);
            g += (float)(lv.nextInt(11) - 5);
        }
        VertexConsumer lv2 = arg3.getBuffer(RenderLayer.getLightning());
        Matrix4f matrix4f = arg2.peek().getPositionMatrix();
        for (int k = 0; k < 4; ++k) {
            Random lv3 = Random.create(arg.seed);
            for (int l = 0; l < 3; ++l) {
                int m = 7;
                int n = 0;
                if (l > 0) {
                    m = 7 - l;
                }
                if (l > 0) {
                    n = m - 2;
                }
                float h = fs[m] - f;
                float o = gs[m] - g;
                for (int p = m; p >= n; --p) {
                    float q = h;
                    float r = o;
                    if (l == 0) {
                        h += (float)(lv3.nextInt(11) - 5);
                        o += (float)(lv3.nextInt(11) - 5);
                    } else {
                        h += (float)(lv3.nextInt(31) - 15);
                        o += (float)(lv3.nextInt(31) - 15);
                    }
                    float s = 0.5f;
                    float t = 0.45f;
                    float u = 0.45f;
                    float v = 0.5f;
                    float w = 0.1f + (float)k * 0.2f;
                    if (l == 0) {
                        w *= (float)p * 0.1f + 1.0f;
                    }
                    float x = 0.1f + (float)k * 0.2f;
                    if (l == 0) {
                        x *= ((float)p - 1.0f) * 0.1f + 1.0f;
                    }
                    LightningEntityRenderer.drawBranch(matrix4f, lv2, h, o, p, q, r, 0.45f, 0.45f, 0.5f, w, x, false, false, true, false);
                    LightningEntityRenderer.drawBranch(matrix4f, lv2, h, o, p, q, r, 0.45f, 0.45f, 0.5f, w, x, true, false, true, true);
                    LightningEntityRenderer.drawBranch(matrix4f, lv2, h, o, p, q, r, 0.45f, 0.45f, 0.5f, w, x, true, true, false, true);
                    LightningEntityRenderer.drawBranch(matrix4f, lv2, h, o, p, q, r, 0.45f, 0.45f, 0.5f, w, x, false, true, false, false);
                }
            }
        }
    }

    private static void drawBranch(Matrix4f matrix, VertexConsumer buffer, float x1, float z1, int y, float x2, float z2, float red, float green, float blue, float offset2, float offset1, boolean shiftEast1, boolean shiftSouth1, boolean shiftEast2, boolean shiftSouth2) {
        buffer.vertex(matrix, x1 + (shiftEast1 ? offset1 : -offset1), (float)(y * 16), z1 + (shiftSouth1 ? offset1 : -offset1)).color(red, green, blue, 0.3f);
        buffer.vertex(matrix, x2 + (shiftEast1 ? offset2 : -offset2), (float)((y + 1) * 16), z2 + (shiftSouth1 ? offset2 : -offset2)).color(red, green, blue, 0.3f);
        buffer.vertex(matrix, x2 + (shiftEast2 ? offset2 : -offset2), (float)((y + 1) * 16), z2 + (shiftSouth2 ? offset2 : -offset2)).color(red, green, blue, 0.3f);
        buffer.vertex(matrix, x1 + (shiftEast2 ? offset1 : -offset1), (float)(y * 16), z1 + (shiftSouth2 ? offset1 : -offset1)).color(red, green, blue, 0.3f);
    }

    @Override
    public LightningEntityRenderState createRenderState() {
        return new LightningEntityRenderState();
    }

    @Override
    public void updateRenderState(LightningEntity arg, LightningEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.seed = arg.seed;
    }

    @Override
    protected boolean canBeCulled(LightningEntity arg) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean canBeCulled(Entity entity) {
        return this.canBeCulled((LightningEntity)entity);
    }
}

