/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.block;

import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class OrientationHelper {
    @Nullable
    public static WireOrientation getEmissionOrientation(World world, @Nullable Direction up, @Nullable Direction front) {
        if (world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
            WireOrientation lv = WireOrientation.random(world.random).withSideBias(WireOrientation.SideBias.LEFT);
            if (front != null) {
                lv = lv.withUp(front);
            }
            if (up != null) {
                lv = lv.withFront(up);
            }
            return lv;
        }
        return null;
    }

    @Nullable
    public static WireOrientation withFrontNullable(@Nullable WireOrientation orientation, Direction direction) {
        return orientation == null ? null : orientation.withFront(direction);
    }
}

