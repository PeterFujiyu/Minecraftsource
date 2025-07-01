/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class LimbAnimator {
    private float prevSpeed;
    private float speed;
    private float pos;
    private float scale = 1.0f;

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void updateLimbs(float speed, float multiplier, float scale) {
        this.prevSpeed = this.speed;
        this.speed += (speed - this.speed) * multiplier;
        this.pos += this.speed;
        this.scale = scale;
    }

    public void reset() {
        this.prevSpeed = 0.0f;
        this.speed = 0.0f;
        this.pos = 0.0f;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getSpeed(float tickDelta) {
        return Math.min(MathHelper.lerp(tickDelta, this.prevSpeed, this.speed), 1.0f);
    }

    public float getPos() {
        return this.pos * this.scale;
    }

    public float getPos(float tickDelta) {
        return (this.pos - this.speed * (1.0f - tickDelta)) * this.scale;
    }

    public boolean isLimbMoving() {
        return this.speed > 1.0E-5f;
    }
}

