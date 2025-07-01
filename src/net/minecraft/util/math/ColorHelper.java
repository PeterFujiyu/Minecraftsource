/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util.math;

import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ColorHelper {
    public static int getAlpha(int argb) {
        return argb >>> 24;
    }

    public static int getRed(int argb) {
        return argb >> 16 & 0xFF;
    }

    public static int getGreen(int argb) {
        return argb >> 8 & 0xFF;
    }

    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    public static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getArgb(int red, int green, int blue) {
        return ColorHelper.getArgb(255, red, green, blue);
    }

    public static int getArgb(Vec3d rgb) {
        return ColorHelper.getArgb(ColorHelper.channelFromFloat((float)rgb.getX()), ColorHelper.channelFromFloat((float)rgb.getY()), ColorHelper.channelFromFloat((float)rgb.getZ()));
    }

    public static int mix(int first, int second) {
        if (first == Colors.WHITE) {
            return second;
        }
        if (second == Colors.WHITE) {
            return first;
        }
        return ColorHelper.getArgb(ColorHelper.getAlpha(first) * ColorHelper.getAlpha(second) / 255, ColorHelper.getRed(first) * ColorHelper.getRed(second) / 255, ColorHelper.getGreen(first) * ColorHelper.getGreen(second) / 255, ColorHelper.getBlue(first) * ColorHelper.getBlue(second) / 255);
    }

    public static int scaleRgb(int argb, float scale) {
        return ColorHelper.scaleRgb(argb, scale, scale, scale);
    }

    public static int scaleRgb(int argb, float redScale, float greenScale, float blueScale) {
        return ColorHelper.getArgb(ColorHelper.getAlpha(argb), Math.clamp((long)((int)((float)ColorHelper.getRed(argb) * redScale)), 0, 255), Math.clamp((long)((int)((float)ColorHelper.getGreen(argb) * greenScale)), 0, 255), Math.clamp((long)((int)((float)ColorHelper.getBlue(argb) * blueScale)), 0, 255));
    }

    public static int scaleRgb(int argb, int scale) {
        return ColorHelper.getArgb(ColorHelper.getAlpha(argb), Math.clamp((long)ColorHelper.getRed(argb) * (long)scale / 255L, 0, 255), Math.clamp((long)ColorHelper.getGreen(argb) * (long)scale / 255L, 0, 255), Math.clamp((long)ColorHelper.getBlue(argb) * (long)scale / 255L, 0, 255));
    }

    public static int grayscale(int argb) {
        int j = (int)((float)ColorHelper.getRed(argb) * 0.3f + (float)ColorHelper.getGreen(argb) * 0.59f + (float)ColorHelper.getBlue(argb) * 0.11f);
        return ColorHelper.getArgb(j, j, j);
    }

    public static int lerp(float delta, int start, int end) {
        int k = MathHelper.lerp(delta, ColorHelper.getAlpha(start), ColorHelper.getAlpha(end));
        int l = MathHelper.lerp(delta, ColorHelper.getRed(start), ColorHelper.getRed(end));
        int m = MathHelper.lerp(delta, ColorHelper.getGreen(start), ColorHelper.getGreen(end));
        int n = MathHelper.lerp(delta, ColorHelper.getBlue(start), ColorHelper.getBlue(end));
        return ColorHelper.getArgb(k, l, m, n);
    }

    public static int fullAlpha(int argb) {
        return argb | Colors.BLACK;
    }

    public static int zeroAlpha(int argb) {
        return argb & 0xFFFFFF;
    }

    public static int withAlpha(int alpha, int rgb) {
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    public static int getWhite(float alpha) {
        return ColorHelper.channelFromFloat(alpha) << 24 | 0xFFFFFF;
    }

    public static int fromFloats(float alpha, float red, float green, float blue) {
        return ColorHelper.getArgb(ColorHelper.channelFromFloat(alpha), ColorHelper.channelFromFloat(red), ColorHelper.channelFromFloat(green), ColorHelper.channelFromFloat(blue));
    }

    public static Vector3f toVector(int rgb) {
        float f = (float)ColorHelper.getRed(rgb) / 255.0f;
        float g = (float)ColorHelper.getGreen(rgb) / 255.0f;
        float h = (float)ColorHelper.getBlue(rgb) / 255.0f;
        return new Vector3f(f, g, h);
    }

    public static int average(int first, int second) {
        return ColorHelper.getArgb((ColorHelper.getAlpha(first) + ColorHelper.getAlpha(second)) / 2, (ColorHelper.getRed(first) + ColorHelper.getRed(second)) / 2, (ColorHelper.getGreen(first) + ColorHelper.getGreen(second)) / 2, (ColorHelper.getBlue(first) + ColorHelper.getBlue(second)) / 2);
    }

    public static int channelFromFloat(float value) {
        return MathHelper.floor(value * 255.0f);
    }

    public static float getAlphaFloat(int argb) {
        return ColorHelper.floatFromChannel(ColorHelper.getAlpha(argb));
    }

    public static float getRedFloat(int argb) {
        return ColorHelper.floatFromChannel(ColorHelper.getRed(argb));
    }

    public static float getGreenFloat(int argb) {
        return ColorHelper.floatFromChannel(ColorHelper.getGreen(argb));
    }

    public static float getBlueFloat(int argb) {
        return ColorHelper.floatFromChannel(ColorHelper.getBlue(argb));
    }

    private static float floatFromChannel(int channel) {
        return (float)channel / 255.0f;
    }

    public static int toAbgr(int argb) {
        return argb & Colors.GREEN | (argb & 0xFF0000) >> 16 | (argb & 0xFF) << 16;
    }

    public static int fromAbgr(int abgr) {
        return ColorHelper.toAbgr(abgr);
    }
}

