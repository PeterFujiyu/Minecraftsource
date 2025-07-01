/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

@Environment(value=EnvType.CLIENT)
public class WorldBorderRendering {
    public static final Identifier FORCEFIELD = Identifier.ofVanilla("textures/misc/forcefield.png");

    public void render(WorldBorder border, Vec3d arg2, double d, double e) {
        BuiltBuffer lv3;
        float aa;
        double z;
        double y;
        float x;
        double f = border.getBoundWest();
        double g = border.getBoundEast();
        double h = border.getBoundNorth();
        double i = border.getBoundSouth();
        if (arg2.x < g - d && arg2.x > f + d && arg2.z < i - d && arg2.z > h + d) {
            return;
        }
        double j = 1.0 - border.getDistanceInsideBorder(arg2.x, arg2.z) / d;
        j = Math.pow(j, 4.0);
        j = MathHelper.clamp(j, 0.0, 1.0);
        double k = arg2.x;
        double l = arg2.z;
        float m = (float)e;
        RenderLayer lv = RenderLayer.getWorldBorder(MinecraftClient.isFabulousGraphicsOrBetter());
        lv.startDrawing();
        int n = border.getStage().getColor();
        float o = (float)ColorHelper.getRed(n) / 255.0f;
        float p = (float)ColorHelper.getGreen(n) / 255.0f;
        float q = (float)ColorHelper.getBlue(n) / 255.0f;
        RenderSystem.setShaderColor(o, p, q, (float)j);
        float r = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float s = (float)(-MathHelper.fractionalPart(arg2.y * 0.5));
        float t = s + m;
        BufferBuilder lv2 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        double u = Math.max((double)MathHelper.floor(l - d), h);
        double v = Math.min((double)MathHelper.ceil(l + d), i);
        float w = (float)(MathHelper.floor(u) & 1) * 0.5f;
        if (k > g - d) {
            x = w;
            y = u;
            while (y < v) {
                z = Math.min(1.0, v - y);
                aa = (float)z * 0.5f;
                lv2.vertex((float)(g - k), -m, (float)(y - l)).texture(r - x, r + t);
                lv2.vertex((float)(g - k), -m, (float)(y + z - l)).texture(r - (aa + x), r + t);
                lv2.vertex((float)(g - k), m, (float)(y + z - l)).texture(r - (aa + x), r + s);
                lv2.vertex((float)(g - k), m, (float)(y - l)).texture(r - x, r + s);
                y += 1.0;
                x += 0.5f;
            }
        }
        if (k < f + d) {
            x = w;
            y = u;
            while (y < v) {
                z = Math.min(1.0, v - y);
                aa = (float)z * 0.5f;
                lv2.vertex((float)(f - k), -m, (float)(y - l)).texture(r + x, r + t);
                lv2.vertex((float)(f - k), -m, (float)(y + z - l)).texture(r + aa + x, r + t);
                lv2.vertex((float)(f - k), m, (float)(y + z - l)).texture(r + aa + x, r + s);
                lv2.vertex((float)(f - k), m, (float)(y - l)).texture(r + x, r + s);
                y += 1.0;
                x += 0.5f;
            }
        }
        u = Math.max((double)MathHelper.floor(k - d), f);
        v = Math.min((double)MathHelper.ceil(k + d), g);
        w = (float)(MathHelper.floor(u) & 1) * 0.5f;
        if (l > i - d) {
            x = w;
            y = u;
            while (y < v) {
                z = Math.min(1.0, v - y);
                aa = (float)z * 0.5f;
                lv2.vertex((float)(y - k), -m, (float)(i - l)).texture(r + x, r + t);
                lv2.vertex((float)(y + z - k), -m, (float)(i - l)).texture(r + aa + x, r + t);
                lv2.vertex((float)(y + z - k), m, (float)(i - l)).texture(r + aa + x, r + s);
                lv2.vertex((float)(y - k), m, (float)(i - l)).texture(r + x, r + s);
                y += 1.0;
                x += 0.5f;
            }
        }
        if (l < h + d) {
            x = w;
            y = u;
            while (y < v) {
                z = Math.min(1.0, v - y);
                aa = (float)z * 0.5f;
                lv2.vertex((float)(y - k), -m, (float)(h - l)).texture(r - x, r + t);
                lv2.vertex((float)(y + z - k), -m, (float)(h - l)).texture(r - (aa + x), r + t);
                lv2.vertex((float)(y + z - k), m, (float)(h - l)).texture(r - (aa + x), r + s);
                lv2.vertex((float)(y - k), m, (float)(h - l)).texture(r - x, r + s);
                y += 1.0;
                x += 0.5f;
            }
        }
        if ((lv3 = lv2.endNullable()) != null) {
            BufferRenderer.drawWithGlobalProgram(lv3);
        }
        lv.endDrawing();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}

