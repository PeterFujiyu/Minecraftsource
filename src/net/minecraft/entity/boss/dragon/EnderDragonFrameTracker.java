/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.boss.dragon;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;

public class EnderDragonFrameTracker {
    public static final int field_52489 = 64;
    private static final int field_52490 = 63;
    private final Frame[] frames = new Frame[64];
    private int currentIndex = -1;

    public EnderDragonFrameTracker() {
        Arrays.fill(this.frames, new Frame(0.0, 0.0f));
    }

    public void copyFrom(EnderDragonFrameTracker other) {
        System.arraycopy(other.frames, 0, this.frames, 0, 64);
        this.currentIndex = other.currentIndex;
    }

    public void tick(double y, float yaw) {
        Frame lv = new Frame(y, yaw);
        if (this.currentIndex < 0) {
            Arrays.fill(this.frames, lv);
        }
        if (++this.currentIndex == 64) {
            this.currentIndex = 0;
        }
        this.frames[this.currentIndex] = lv;
    }

    public Frame getFrame(int age) {
        return this.frames[this.currentIndex - age & 0x3F];
    }

    public Frame getLerpedFrame(int age, float tickDelta) {
        Frame lv = this.getFrame(age);
        Frame lv2 = this.getFrame(age + 1);
        return new Frame(MathHelper.lerp((double)tickDelta, lv2.y, lv.y), MathHelper.lerpAngleDegrees(tickDelta, lv2.yRot, lv.yRot));
    }

    public record Frame(double y, float yRot) {
    }
}

