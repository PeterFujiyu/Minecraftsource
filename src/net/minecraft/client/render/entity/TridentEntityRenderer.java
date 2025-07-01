/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class TridentEntityRenderer
extends EntityRenderer<TridentEntity, TridentEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident.png");
    private final TridentEntityModel model;

    public TridentEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.model = new TridentEntityModel(arg.getPart(EntityModelLayers.TRIDENT));
    }

    @Override
    public void render(TridentEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arg.yaw - 90.0f));
        arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arg.pitch + 90.0f));
        VertexConsumer lv = ItemRenderer.getItemGlintConsumer(arg3, this.model.getLayer(TEXTURE), false, arg.enchanted);
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public TridentEntityRenderState createRenderState() {
        return new TridentEntityRenderState();
    }

    @Override
    public void updateRenderState(TridentEntity arg, TridentEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.yaw = arg.getLerpedYaw(f);
        arg2.pitch = arg.getLerpedPitch(f);
        arg2.enchanted = arg.isEnchanted();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

