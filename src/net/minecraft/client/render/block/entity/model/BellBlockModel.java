/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockModel
extends Model {
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;

    public BellBlockModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
        this.bellBody = root.getChild(BELL_BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        ModelPartData lv3 = lv2.addChild(BELL_BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f), ModelTransform.pivot(8.0f, 12.0f, 8.0f));
        lv3.addChild("bell_base", ModelPartBuilder.create().uv(0, 13).cuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f), ModelTransform.pivot(-8.0f, -12.0f, -8.0f));
        return TexturedModelData.of(lv, 32, 32);
    }

    public void update(BellBlockEntity blockEntity, float tickDelta) {
        float g = (float)blockEntity.ringTicks + tickDelta;
        float h = 0.0f;
        float i = 0.0f;
        if (blockEntity.ringing) {
            float j = MathHelper.sin(g / (float)Math.PI) / (4.0f + g / 3.0f);
            if (blockEntity.lastSideHit == Direction.NORTH) {
                h = -j;
            } else if (blockEntity.lastSideHit == Direction.SOUTH) {
                h = j;
            } else if (blockEntity.lastSideHit == Direction.EAST) {
                i = -j;
            } else if (blockEntity.lastSideHit == Direction.WEST) {
                i = j;
            }
        }
        this.bellBody.pitch = h;
        this.bellBody.roll = i;
    }
}

