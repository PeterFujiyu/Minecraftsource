/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.util.math.MathHelper;

public interface Control {
    default public float changeAngle(float start, float end, float maxChange) {
        float i = MathHelper.subtractAngles(start, end);
        float j = MathHelper.clamp(i, -maxChange, maxChange);
        return start + j;
    }
}

