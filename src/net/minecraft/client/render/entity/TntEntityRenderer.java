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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class TntEntityRenderer
extends EntityRenderer<TntEntity, TntEntityRenderState> {
    private final BlockRenderManager blockRenderManager;

    public TntEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = arg.getBlockRenderManager();
    }

    @Override
    public void render(TntEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.translate(0.0f, 0.5f, 0.0f);
        float f = arg.fuse;
        if (arg.fuse < 10.0f) {
            float g = 1.0f - arg.fuse / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            arg2.scale(h, h, h);
        }
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        arg2.translate(-0.5f, -0.5f, 0.5f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        if (arg.blockState != null) {
            TntMinecartEntityRenderer.renderFlashingBlock(this.blockRenderManager, arg.blockState, arg2, arg3, i, (int)f / 5 % 2 == 0);
        }
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public TntEntityRenderState createRenderState() {
        return new TntEntityRenderState();
    }

    @Override
    public void updateRenderState(TntEntity arg, TntEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.fuse = (float)arg.getFuse() - f + 1.0f;
        arg2.blockState = arg.getBlockState();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

