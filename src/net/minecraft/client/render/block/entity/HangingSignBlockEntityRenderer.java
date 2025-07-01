/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class HangingSignBlockEntityRenderer
extends AbstractSignBlockEntityRenderer {
    private static final String PLANK = "plank";
    private static final String V_CHAINS = "vChains";
    private static final String NORMAL_CHAINS = "normalChains";
    private static final String CHAIN_L1 = "chainL1";
    private static final String CHAIN_L2 = "chainL2";
    private static final String CHAIN_R1 = "chainR1";
    private static final String CHAIN_R2 = "chainR2";
    private static final String BOARD = "board";
    private static final float MODEL_SCALE = 1.0f;
    private static final float TEXT_SCALE = 0.9f;
    private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, -0.32f, 0.073f);
    private final Map<Variant, Model> models;

    public HangingSignBlockEntityRenderer(BlockEntityRendererFactory.Context arg) {
        super(arg);
        Stream<ImmutableMap<Variant, Model>> stream = WoodType.stream().flatMap(woodType -> Arrays.stream(AttachmentType.values()).map(attachmentType -> new Variant((WoodType)woodType, (AttachmentType)attachmentType)));
        this.models = stream.collect(ImmutableMap.toImmutableMap(variant -> variant, variant -> HangingSignBlockEntityRenderer.createModel(arg.getLoadedEntityModels(), variant.woodType, variant.attachmentType)));
    }

    public static Model createModel(LoadedEntityModels models, WoodType woodType, AttachmentType attachmentType) {
        return new Model.SinglePartModel(models.getModelPart(EntityModelLayers.createHangingSign(woodType, attachmentType)), RenderLayer::getEntityCutoutNoCull);
    }

    @Override
    protected float getSignScale() {
        return 1.0f;
    }

    @Override
    protected float getTextScale() {
        return 0.9f;
    }

    private static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
        matrices.translate(0.5, 0.9375, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
        matrices.translate(0.0f, -0.3125f, 0.0f);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
        HangingSignBlockEntityRenderer.setAngles(matrices, blockRotationDegrees);
    }

    @Override
    protected Model getModel(BlockState state, WoodType woodType) {
        AttachmentType lv = AttachmentType.from(state);
        return this.models.get(new Variant(woodType, lv));
    }

    @Override
    protected SpriteIdentifier getTextureId(WoodType woodType) {
        return TexturedRenderLayers.getHangingSignTextureId(woodType);
    }

    @Override
    protected Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Model model, SpriteIdentifier texture) {
        matrices.push();
        HangingSignBlockEntityRenderer.setAngles(matrices, 0.0f);
        matrices.scale(1.0f, -1.0f, -1.0f);
        VertexConsumer lv = texture.getVertexConsumer(vertexConsumers, model::getLayer);
        model.render(matrices, lv, light, overlay);
        matrices.pop();
    }

    public static TexturedModelData getTexturedModelData(AttachmentType attachmentType) {
        ModelData lv = new ModelData();
        ModelPartData lv2 = lv.getRoot();
        lv2.addChild(BOARD, ModelPartBuilder.create().uv(0, 12).cuboid(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f), ModelTransform.NONE);
        if (attachmentType == AttachmentType.WALL) {
            lv2.addChild(PLANK, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f), ModelTransform.NONE);
        }
        if (attachmentType == AttachmentType.WALL || attachmentType == AttachmentType.CEILING) {
            ModelPartData lv3 = lv2.addChild(NORMAL_CHAINS, ModelPartBuilder.create(), ModelTransform.NONE);
            lv3.addChild(CHAIN_L1, ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(-5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
            lv3.addChild(CHAIN_L2, ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(-5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
            lv3.addChild(CHAIN_R1, ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
            lv3.addChild(CHAIN_R2, ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        }
        if (attachmentType == AttachmentType.CEILING_MIDDLE) {
            lv2.addChild(V_CHAINS, ModelPartBuilder.create().uv(14, 6).cuboid(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f), ModelTransform.NONE);
        }
        return TexturedModelData.of(lv, 64, 32);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum AttachmentType implements StringIdentifiable
    {
        WALL("wall"),
        CEILING("ceiling"),
        CEILING_MIDDLE("ceiling_middle");

        private final String id;

        private AttachmentType(String id) {
            this.id = id;
        }

        public static AttachmentType from(BlockState state) {
            if (state.getBlock() instanceof HangingSignBlock) {
                return state.get(Properties.ATTACHED) != false ? CEILING_MIDDLE : CEILING;
            }
            return WALL;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Variant(WoodType woodType, AttachmentType attachmentType) {
    }
}

