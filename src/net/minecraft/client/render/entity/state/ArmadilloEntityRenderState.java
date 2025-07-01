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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

@Environment(value=EnvType.CLIENT)
public class ArmadilloEntityRenderState
extends LivingEntityRenderState {
    public boolean rolledUp;
    public final AnimationState unrollingAnimationState = new AnimationState();
    public final AnimationState rollingAnimationState = new AnimationState();
    public final AnimationState scaredAnimationState = new AnimationState();
}

