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
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerBoxBlockModel model;

    public ShulkerBoxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this(ctx.getLoadedEntityModels());
    }

    public ShulkerBoxBlockEntityRenderer(LoadedEntityModels models) {
        this.model = new ShulkerBoxBlockModel(models.getModelPart(EntityModelLayers.SHULKER_BOX));
    }

    @Override
    public void render(ShulkerBoxBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        Direction lv = arg.getCachedState().get(ShulkerBoxBlock.FACING, Direction.UP);
        DyeColor lv2 = arg.getColor();
        SpriteIdentifier lv3 = lv2 == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.getShulkerBoxTextureId(lv2);
        float g = arg.getAnimationProgress(f);
        this.render(arg2, arg3, i, j, lv, g, lv3);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, float openness, SpriteIdentifier textureId) {
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        float g = 0.9995f;
        matrices.scale(0.9995f, 0.9995f, 0.9995f);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.scale(1.0f, -1.0f, -1.0f);
        matrices.translate(0.0f, -1.0f, 0.0f);
        this.model.animateLid(openness);
        VertexConsumer lv = textureId.getVertexConsumer(vertexConsumers, this.model::getLayer);
        this.model.render(matrices, lv, light, overlay);
        matrices.pop();
    }

    @Environment(value=EnvType.CLIENT)
    static class ShulkerBoxBlockModel
    extends Model {
        private final ModelPart lid;

        public ShulkerBoxBlockModel(ModelPart root) {
            super(root, RenderLayer::getEntityCutoutNoCull);
            this.lid = root.getChild("lid");
        }

        public void animateLid(float openness) {
            this.lid.setPivot(0.0f, 24.0f - openness * 0.5f * 16.0f, 0.0f);
            this.lid.yaw = 270.0f * openness * ((float)Math.PI / 180);
        }
    }
}

