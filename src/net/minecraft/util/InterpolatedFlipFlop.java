/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class InterpolatedFlipFlop {
    private final int frames;
    private final SmoothingFunction smoothingFunction;
    private int current;
    private int previous;

    public InterpolatedFlipFlop(int frames, SmoothingFunction smoothingFunction) {
        this.frames = frames;
        this.smoothingFunction = smoothingFunction;
    }

    public InterpolatedFlipFlop(int frames) {
        this(frames, tickDelta -> tickDelta);
    }

    public void tick(boolean active) {
        this.previous = this.current;
        if (active) {
            if (this.current < this.frames) {
                ++this.current;
            }
        } else if (this.current > 0) {
            --this.current;
        }
    }

    public float getValue(float tickDelta) {
        float g = MathHelper.lerp(tickDelta, (float)this.previous, (float)this.current) / (float)this.frames;
        return this.smoothingFunction.apply(g);
    }

    public static interface SmoothingFunction {
        public float apply(float var1);
    }
}

