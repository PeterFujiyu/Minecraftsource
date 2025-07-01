/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CloudRenderer
extends SinglePreparationResourceReloader<Optional<CloudCells>>
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier CLOUD_TEXTURE = Identifier.ofVanilla("textures/environment/clouds.png");
    private static final float field_53043 = 12.0f;
    private static final float field_53044 = 4.0f;
    private static final float field_53045 = 0.6f;
    private static final long field_53046 = 0L;
    private static final int field_53047 = 4;
    private static final int field_53048 = 3;
    private static final int field_53049 = 2;
    private static final int field_53050 = 1;
    private static final int field_53051 = 0;
    private boolean field_53052 = true;
    private int centerX = Integer.MIN_VALUE;
    private int centerZ = Integer.MIN_VALUE;
    private ViewMode viewMode = ViewMode.INSIDE_CLOUDS;
    @Nullable
    private CloudRenderMode renderMode;
    @Nullable
    private CloudCells cells;
    private final VertexBuffer buffer = new VertexBuffer(GlUsage.STATIC_WRITE);
    private boolean renderClouds;

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Optional<CloudCells> prepare(ResourceManager arg, Profiler arg2) {
        try (InputStream inputStream = arg.open(CLOUD_TEXTURE);){
            NativeImage lv = NativeImage.read(inputStream);
            try {
                int i = lv.getWidth();
                int j = lv.getHeight();
                long[] ls = new long[i * j];
                for (int k = 0; k < j; ++k) {
                    for (int l = 0; l < i; ++l) {
                        int m = lv.getColorArgb(l, k);
                        if (CloudRenderer.isEmpty(m)) {
                            ls[l + k * i] = 0L;
                            continue;
                        }
                        boolean bl = CloudRenderer.isEmpty(lv.getColorArgb(l, Math.floorMod(k - 1, j)));
                        boolean bl2 = CloudRenderer.isEmpty(lv.getColorArgb(Math.floorMod(l + 1, j), k));
                        boolean bl3 = CloudRenderer.isEmpty(lv.getColorArgb(l, Math.floorMod(k + 1, j)));
                        boolean bl4 = CloudRenderer.isEmpty(lv.getColorArgb(Math.floorMod(l - 1, j), k));
                        ls[l + k * i] = CloudRenderer.packCloudCell(m, bl, bl2, bl3, bl4);
                    }
                }
                Optional<CloudCells> optional = Optional.of(new CloudCells(ls, i, j));
                if (lv != null) {
                    lv.close();
                }
                return optional;
            } catch (Throwable throwable) {
                if (lv != null) {
                    try {
                        lv.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        } catch (IOException iOException) {
            LOGGER.error("Failed to load cloud texture", iOException);
            return Optional.empty();
        }
    }

    @Override
    protected void apply(Optional<CloudCells> optional, ResourceManager arg, Profiler arg2) {
        this.cells = optional.orElse(null);
        this.field_53052 = true;
    }

    private static boolean isEmpty(int color) {
        return ColorHelper.getAlpha(color) < 10;
    }

    private static long packCloudCell(int color, boolean borderNorth, boolean borderEast, boolean borderSouth, boolean borderWest) {
        return (long)color << 4 | (long)((borderNorth ? 1 : 0) << 3) | (long)((borderEast ? 1 : 0) << 2) | (long)((borderSouth ? 1 : 0) << 1) | (long)((borderWest ? 1 : 0) << 0);
    }

    private static int unpackColor(long packed) {
        return (int)(packed >> 4 & 0xFFFFFFFFL);
    }

    private static boolean hasBorderNorth(long packed) {
        return (packed >> 3 & 1L) != 0L;
    }

    private static boolean hasBorderEast(long packed) {
        return (packed >> 2 & 1L) != 0L;
    }

    private static boolean hasBorderSouth(long packed) {
        return (packed >> 1 & 1L) != 0L;
    }

    private static boolean hasBorderWest(long packed) {
        return (packed >> 0 & 1L) != 0L;
    }

    public void renderClouds(int color, CloudRenderMode cloudRenderMode, float cloudHeight, Matrix4f positionMatrix, Matrix4f projectionMatrix, Vec3d cameraPos, float ticks) {
        if (this.cells == null) {
            return;
        }
        float h = (float)((double)cloudHeight - cameraPos.y);
        float j = h + 4.0f;
        ViewMode lv = j < 0.0f ? ViewMode.ABOVE_CLOUDS : (h > 0.0f ? ViewMode.BELOW_CLOUDS : ViewMode.INSIDE_CLOUDS);
        double d = cameraPos.x + (double)(ticks * 0.030000001f);
        double e = cameraPos.z + (double)3.96f;
        double k = (double)this.cells.width * 12.0;
        double l = (double)this.cells.height * 12.0;
        d -= (double)MathHelper.floor(d / k) * k;
        e -= (double)MathHelper.floor(e / l) * l;
        int m = MathHelper.floor(d / 12.0);
        int n = MathHelper.floor(e / 12.0);
        float o = (float)(d - (double)((float)m * 12.0f));
        float p = (float)(e - (double)((float)n * 12.0f));
        RenderLayer lv2 = cloudRenderMode == CloudRenderMode.FANCY ? RenderLayer.getFastClouds() : RenderLayer.getNoCullingClouds();
        this.buffer.bind();
        if (this.field_53052 || m != this.centerX || n != this.centerZ || lv != this.viewMode || cloudRenderMode != this.renderMode) {
            this.field_53052 = false;
            this.centerX = m;
            this.centerZ = n;
            this.viewMode = lv;
            this.renderMode = cloudRenderMode;
            BuiltBuffer lv3 = this.tessellateClouds(Tessellator.getInstance(), m, n, cloudRenderMode, lv, lv2);
            if (lv3 != null) {
                this.buffer.upload(lv3);
                this.renderClouds = false;
            } else {
                this.renderClouds = true;
            }
        }
        if (this.renderClouds) {
            return;
        }
        RenderSystem.setShaderColor(ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), 1.0f);
        if (cloudRenderMode == CloudRenderMode.FANCY) {
            this.renderClouds(RenderLayer.getFancyClouds(), positionMatrix, projectionMatrix, o, h, p);
        }
        this.renderClouds(lv2, positionMatrix, projectionMatrix, o, h, p);
        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderClouds(RenderLayer layer, Matrix4f positionMatrix, Matrix4f projectionMatrix, float x, float y, float z) {
        layer.startDrawing();
        ShaderProgram lv = RenderSystem.getShader();
        if (lv != null && lv.modelOffset != null) {
            lv.modelOffset.set(-x, y, -z);
        }
        this.buffer.draw(positionMatrix, projectionMatrix, lv);
        layer.endDrawing();
    }

    @Nullable
    private BuiltBuffer tessellateClouds(Tessellator tessellator, int x, int z, CloudRenderMode renderMode, ViewMode viewMode, RenderLayer layer) {
        float f = 0.8f;
        int k = ColorHelper.fromFloats(0.8f, 1.0f, 1.0f, 1.0f);
        int l = ColorHelper.fromFloats(0.8f, 0.9f, 0.9f, 0.9f);
        int m = ColorHelper.fromFloats(0.8f, 0.7f, 0.7f, 0.7f);
        int n = ColorHelper.fromFloats(0.8f, 0.8f, 0.8f, 0.8f);
        BufferBuilder lv = tessellator.begin(layer.getDrawMode(), layer.getVertexFormat());
        this.buildCloudCells(viewMode, lv, x, z, m, k, l, n, renderMode == CloudRenderMode.FANCY);
        return lv.endNullable();
    }

    private void buildCloudCells(ViewMode viewMode, BufferBuilder builder, int x, int z, int bottomColor, int topColor, int northSouthColor, int eastWestColor, boolean fancy) {
        if (this.cells == null) {
            return;
        }
        int o = 32;
        long[] ls = this.cells.cells;
        int p = this.cells.width;
        int q = this.cells.height;
        for (int r = -32; r <= 32; ++r) {
            for (int s = -32; s <= 32; ++s) {
                int u;
                int t = Math.floorMod(x + s, p);
                long v = ls[t + (u = Math.floorMod(z + r, q)) * p];
                if (v == 0L) continue;
                int w = CloudRenderer.unpackColor(v);
                if (fancy) {
                    this.buildCloudCellFancy(viewMode, builder, ColorHelper.mix(bottomColor, w), ColorHelper.mix(topColor, w), ColorHelper.mix(northSouthColor, w), ColorHelper.mix(eastWestColor, w), s, r, v);
                    continue;
                }
                this.buildCloudCellFast(builder, ColorHelper.mix(topColor, w), s, r);
            }
        }
    }

    private void buildCloudCellFast(BufferBuilder builder, int color, int x, int z) {
        float f = (float)x * 12.0f;
        float g = f + 12.0f;
        float h = (float)z * 12.0f;
        float l = h + 12.0f;
        builder.vertex(f, 0.0f, h).color(color);
        builder.vertex(f, 0.0f, l).color(color);
        builder.vertex(g, 0.0f, l).color(color);
        builder.vertex(g, 0.0f, h).color(color);
    }

    private void buildCloudCellFancy(ViewMode viewMode, BufferBuilder builder, int bottomColor, int topColor, int northSouthColor, int eastWestColor, int x, int z, long cell) {
        boolean bl;
        float f = (float)x * 12.0f;
        float g = f + 12.0f;
        float h = 0.0f;
        float p = 4.0f;
        float q = (float)z * 12.0f;
        float r = q + 12.0f;
        if (viewMode != ViewMode.BELOW_CLOUDS) {
            builder.vertex(f, 4.0f, q).color(topColor);
            builder.vertex(f, 4.0f, r).color(topColor);
            builder.vertex(g, 4.0f, r).color(topColor);
            builder.vertex(g, 4.0f, q).color(topColor);
        }
        if (viewMode != ViewMode.ABOVE_CLOUDS) {
            builder.vertex(g, 0.0f, q).color(bottomColor);
            builder.vertex(g, 0.0f, r).color(bottomColor);
            builder.vertex(f, 0.0f, r).color(bottomColor);
            builder.vertex(f, 0.0f, q).color(bottomColor);
        }
        if (CloudRenderer.hasBorderNorth(cell) && z > 0) {
            builder.vertex(f, 0.0f, q).color(eastWestColor);
            builder.vertex(f, 4.0f, q).color(eastWestColor);
            builder.vertex(g, 4.0f, q).color(eastWestColor);
            builder.vertex(g, 0.0f, q).color(eastWestColor);
        }
        if (CloudRenderer.hasBorderSouth(cell) && z < 0) {
            builder.vertex(g, 0.0f, r).color(eastWestColor);
            builder.vertex(g, 4.0f, r).color(eastWestColor);
            builder.vertex(f, 4.0f, r).color(eastWestColor);
            builder.vertex(f, 0.0f, r).color(eastWestColor);
        }
        if (CloudRenderer.hasBorderWest(cell) && x > 0) {
            builder.vertex(f, 0.0f, r).color(northSouthColor);
            builder.vertex(f, 4.0f, r).color(northSouthColor);
            builder.vertex(f, 4.0f, q).color(northSouthColor);
            builder.vertex(f, 0.0f, q).color(northSouthColor);
        }
        if (CloudRenderer.hasBorderEast(cell) && x < 0) {
            builder.vertex(g, 0.0f, q).color(northSouthColor);
            builder.vertex(g, 4.0f, q).color(northSouthColor);
            builder.vertex(g, 4.0f, r).color(northSouthColor);
            builder.vertex(g, 0.0f, r).color(northSouthColor);
        }
        boolean bl2 = bl = Math.abs(x) <= 1 && Math.abs(z) <= 1;
        if (bl) {
            builder.vertex(g, 4.0f, q).color(topColor);
            builder.vertex(g, 4.0f, r).color(topColor);
            builder.vertex(f, 4.0f, r).color(topColor);
            builder.vertex(f, 4.0f, q).color(topColor);
            builder.vertex(f, 0.0f, q).color(bottomColor);
            builder.vertex(f, 0.0f, r).color(bottomColor);
            builder.vertex(g, 0.0f, r).color(bottomColor);
            builder.vertex(g, 0.0f, q).color(bottomColor);
            builder.vertex(g, 0.0f, q).color(eastWestColor);
            builder.vertex(g, 4.0f, q).color(eastWestColor);
            builder.vertex(f, 4.0f, q).color(eastWestColor);
            builder.vertex(f, 0.0f, q).color(eastWestColor);
            builder.vertex(f, 0.0f, r).color(eastWestColor);
            builder.vertex(f, 4.0f, r).color(eastWestColor);
            builder.vertex(g, 4.0f, r).color(eastWestColor);
            builder.vertex(g, 0.0f, r).color(eastWestColor);
            builder.vertex(f, 0.0f, q).color(northSouthColor);
            builder.vertex(f, 4.0f, q).color(northSouthColor);
            builder.vertex(f, 4.0f, r).color(northSouthColor);
            builder.vertex(f, 0.0f, r).color(northSouthColor);
            builder.vertex(g, 0.0f, r).color(northSouthColor);
            builder.vertex(g, 4.0f, r).color(northSouthColor);
            builder.vertex(g, 4.0f, q).color(northSouthColor);
            builder.vertex(g, 0.0f, q).color(northSouthColor);
        }
    }

    public void scheduleTerrainUpdate() {
        this.field_53052 = true;
    }

    @Override
    public void close() {
        this.buffer.close();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    static enum ViewMode {
        ABOVE_CLOUDS,
        INSIDE_CLOUDS,
        BELOW_CLOUDS;

    }

    @Environment(value=EnvType.CLIENT)
    public record CloudCells(long[] cells, int width, int height) {
    }
}

