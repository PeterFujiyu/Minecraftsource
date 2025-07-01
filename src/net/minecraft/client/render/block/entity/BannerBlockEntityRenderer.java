/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.model.BannerBlockModel;
import net.minecraft.client.render.block.entity.model.BannerFlagBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
implements BlockEntityRenderer<BannerBlockEntity> {
    private static final int ROTATIONS = 16;
    private static final float field_55282 = 0.6666667f;
    private final BannerBlockModel standingModel;
    private final BannerBlockModel wallModel;
    private final BannerFlagBlockModel standingFlagModel;
    private final BannerFlagBlockModel wallFlagModel;

    public BannerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this(context.getLoadedEntityModels());
    }

    public BannerBlockEntityRenderer(LoadedEntityModels models) {
        this.standingModel = new BannerBlockModel(models.getModelPart(EntityModelLayers.STANDING_BANNER));
        this.wallModel = new BannerBlockModel(models.getModelPart(EntityModelLayers.WALL_BANNER));
        this.standingFlagModel = new BannerFlagBlockModel(models.getModelPart(EntityModelLayers.STANDING_BANNER_FLAG));
        this.wallFlagModel = new BannerFlagBlockModel(models.getModelPart(EntityModelLayers.WALL_BANNER_FLAG));
    }

    @Override
    public void render(BannerBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        BannerFlagBlockModel lv3;
        BannerBlockModel lv2;
        float g;
        BlockState lv = arg.getCachedState();
        if (lv.getBlock() instanceof BannerBlock) {
            g = -RotationPropertyHelper.toDegrees(lv.get(BannerBlock.ROTATION));
            lv2 = this.standingModel;
            lv3 = this.standingFlagModel;
        } else {
            g = -lv.get(WallBannerBlock.FACING).getPositiveHorizontalDegrees();
            lv2 = this.wallModel;
            lv3 = this.wallFlagModel;
        }
        long l = arg.getWorld().getTime();
        BlockPos lv4 = arg.getPos();
        float h = ((float)Math.floorMod((long)(lv4.getX() * 7 + lv4.getY() * 9 + lv4.getZ() * 13) + l, 100L) + f) / 100.0f;
        BannerBlockEntityRenderer.render(arg2, arg3, i, j, g, lv2, lv3, h, arg.getColorForState(), arg.getPatterns());
    }

    public void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DyeColor baseColor, BannerPatternsComponent patterns) {
        BannerBlockEntityRenderer.render(matrices, vertexConsumers, light, overlay, 0.0f, this.standingModel, this.standingFlagModel, 0.0f, baseColor, patterns);
    }

    private static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float rotation, BannerBlockModel model, BannerFlagBlockModel flagModel, float sway, DyeColor baseColor, BannerPatternsComponent patterns) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
        model.render(matrices, ModelBaker.BANNER_BASE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid), light, overlay);
        flagModel.sway(sway);
        BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, flagModel.getRootPart(), ModelBaker.BANNER_BASE, true, baseColor, patterns);
        matrices.pop();
    }

    public static void renderCanvas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, DyeColor color, BannerPatternsComponent patterns) {
        BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, canvas, baseSprite, isBanner, color, patterns, false, true);
    }

    public static void renderCanvas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, DyeColor color, BannerPatternsComponent patterns, boolean glint, boolean solid) {
        canvas.render(matrices, baseSprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid, solid, glint), light, overlay);
        BannerBlockEntityRenderer.renderLayer(matrices, vertexConsumers, light, overlay, canvas, isBanner ? TexturedRenderLayers.BANNER_BASE : TexturedRenderLayers.SHIELD_BASE, color);
        for (int k = 0; k < 16 && k < patterns.layers().size(); ++k) {
            BannerPatternsComponent.Layer lv = patterns.layers().get(k);
            SpriteIdentifier lv2 = isBanner ? TexturedRenderLayers.getBannerPatternTextureId(lv.pattern()) : TexturedRenderLayers.getShieldPatternTextureId(lv.pattern());
            BannerBlockEntityRenderer.renderLayer(matrices, vertexConsumers, light, overlay, canvas, lv2, lv.color());
        }
    }

    private static void renderLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier textureId, DyeColor color) {
        int k = color.getEntityColor();
        canvas.render(matrices, textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, k);
    }
}

