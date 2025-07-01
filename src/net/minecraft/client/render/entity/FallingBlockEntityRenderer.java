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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer<FallingBlockEntity, FallingBlockEntityRenderState> {
    private final BlockRenderManager blockRenderManager;

    public FallingBlockEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = arg.getBlockRenderManager();
    }

    @Override
    public boolean shouldRender(FallingBlockEntity arg, Frustum arg2, double d, double e, double f) {
        if (!super.shouldRender(arg, arg2, d, e, f)) {
            return false;
        }
        return arg.getBlockState() != arg.getWorld().getBlockState(arg.getBlockPos());
    }

    @Override
    public void render(FallingBlockEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        BlockState lv = arg.blockState;
        if (lv.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        arg2.push();
        arg2.translate(-0.5, 0.0, -0.5);
        this.blockRenderManager.getModelRenderer().render(arg, this.blockRenderManager.getModel(lv), lv, arg.currentPos, arg2, arg3.getBuffer(RenderLayers.getMovingBlockLayer(lv)), false, Random.create(), lv.getRenderingSeed(arg.fallingBlockPos), OverlayTexture.DEFAULT_UV);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public FallingBlockEntityRenderState createRenderState() {
        return new FallingBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(FallingBlockEntity arg, FallingBlockEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        BlockPos lv = BlockPos.ofFloored(arg.getX(), arg.getBoundingBox().maxY, arg.getZ());
        arg2.fallingBlockPos = arg.getFallingBlockPos();
        arg2.currentPos = lv;
        arg2.blockState = arg.getBlockState();
        arg2.biome = arg.getWorld().getBiome(lv);
        arg2.world = arg.getWorld();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

