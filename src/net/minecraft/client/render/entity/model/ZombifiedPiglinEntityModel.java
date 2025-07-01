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
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PiglinBaseEntityModel;
import net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ZombifiedPiglinEntityModel
extends PiglinBaseEntityModel<ZombifiedPiglinEntityRenderState> {
    public ZombifiedPiglinEntityModel(ModelPart arg) {
        super(arg);
    }

    @Override
    public void setAngles(ZombifiedPiglinEntityRenderState arg) {
        super.setAngles(arg);
        CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, arg.attacking, arg.handSwingProgress, arg.age);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }
}

