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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBoatEntityModel
extends EntityModel<BoatEntityRenderState> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;

    public AbstractBoatEntityModel(ModelPart arg) {
        super(arg);
        this.leftPaddle = arg.getChild(EntityModelPartNames.LEFT_PADDLE);
        this.rightPaddle = arg.getChild(EntityModelPartNames.RIGHT_PADDLE);
    }

    @Override
    public void setAngles(BoatEntityRenderState arg) {
        super.setAngles(arg);
        AbstractBoatEntityModel.setPaddleAngles(arg.leftPaddleAngle, 0, this.leftPaddle);
        AbstractBoatEntityModel.setPaddleAngles(arg.rightPaddleAngle, 1, this.rightPaddle);
    }

    private static void setPaddleAngles(float angle, int paddle, ModelPart modelPart) {
        modelPart.pitch = MathHelper.clampedLerp(-1.0471976f, -0.2617994f, (MathHelper.sin(-angle) + 1.0f) / 2.0f);
        modelPart.yaw = MathHelper.clampedLerp(-0.7853982f, 0.7853982f, (MathHelper.sin(-angle + 1.0f) + 1.0f) / 2.0f);
        if (paddle == 1) {
            modelPart.yaw = (float)Math.PI - modelPart.yaw;
        }
    }
}

