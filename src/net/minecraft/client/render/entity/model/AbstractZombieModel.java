/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractZombieModel<S extends ZombieEntityRenderState>
extends BipedEntityModel<S> {
    protected AbstractZombieModel(ModelPart arg) {
        super(arg);
    }

    @Override
    public void setAngles(S arg) {
        super.setAngles(arg);
        float f = ((ZombieEntityRenderState)arg).handSwingProgress;
        CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, ((ZombieEntityRenderState)arg).attacking, f, ((ZombieEntityRenderState)arg).age);
    }
}

