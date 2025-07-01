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
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.entity.passive.FoxEntity;

@Environment(value=EnvType.CLIENT)
public class FoxEntityRenderState
extends ItemHolderEntityRenderState {
    public float headRoll;
    public float bodyRotationHeightOffset;
    public boolean inSneakingPose;
    public boolean sleeping;
    public boolean sitting;
    public boolean walking;
    public boolean chasing;
    public FoxEntity.Type type = FoxEntity.Type.RED;
}

