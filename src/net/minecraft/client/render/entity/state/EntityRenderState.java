/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EntityRenderState {
    public double x;
    public double y;
    public double z;
    public float age;
    public float width;
    public float height;
    public float standingEyeHeight;
    public double squaredDistanceToCamera;
    public boolean invisible;
    public boolean sneaking;
    public boolean onFire;
    @Nullable
    public Vec3d positionOffset;
    @Nullable
    public Text displayName;
    @Nullable
    public Vec3d nameLabelPos;
    @Nullable
    public LeashData leashData;

    @Environment(value=EnvType.CLIENT)
    public static class LeashData {
        public Vec3d offset = Vec3d.ZERO;
        public Vec3d startPos = Vec3d.ZERO;
        public Vec3d endPos = Vec3d.ZERO;
        public int leashedEntityBlockLight = 0;
        public int leashHolderBlockLight = 0;
        public int leashedEntitySkyLight = 15;
        public int leashHolderSkyLight = 15;
    }
}

