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
import net.minecraft.client.render.entity.state.SaddleableRenderState;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderState
extends LivingEntityRenderState
implements SaddleableRenderState {
    public boolean saddled;
    public boolean cold;
    public boolean hasPassengers;

    @Override
    public boolean isSaddled() {
        return this.saddled;
    }
}

