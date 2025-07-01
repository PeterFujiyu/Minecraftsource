/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSignBlockEntityRenderer
implements BlockEntityRenderer<SignBlockEntity> {
    private static final int GLOWING_BLACK_TEXT_COLOR = -988212;
    private static final int MAX_COLORED_TEXT_OUTLINE_RENDER_DISTANCE = MathHelper.square(16);
    private final TextRenderer textRenderer;

    public AbstractSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.getTextRenderer();
    }

    protected abstract Model getModel(BlockState var1, WoodType var2);

    protected abstract SpriteIdentifier getTextureId(WoodType var1);

    protected abstract float getSignScale();

    protected abstract float getTextScale();

    protected abstract Vec3d getTextOffset();

    protected abstract void applyTransforms(MatrixStack var1, float var2, BlockState var3);

    @Override
    public void render(SignBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        BlockState lv = arg.getCachedState();
        AbstractSignBlock lv2 = (AbstractSignBlock)lv.getBlock();
        Model lv3 = this.getModel(lv, lv2.getWoodType());
        this.render(arg, arg2, arg3, i, j, lv, lv2, lv2.getWoodType(), lv3);
    }

    private void render(SignBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BlockState state, AbstractSignBlock block, WoodType woodType, Model model) {
        matrices.push();
        this.applyTransforms(matrices, -block.getRotationDegrees(state), state);
        this.renderSign(matrices, vertexConsumers, light, overlay, woodType, model);
        this.renderText(blockEntity.getPos(), blockEntity.getFrontText(), matrices, vertexConsumers, light, blockEntity.getTextLineHeight(), blockEntity.getMaxTextWidth(), true);
        this.renderText(blockEntity.getPos(), blockEntity.getBackText(), matrices, vertexConsumers, light, blockEntity.getTextLineHeight(), blockEntity.getMaxTextWidth(), false);
        matrices.pop();
    }

    protected void renderSign(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, WoodType woodType, Model model) {
        matrices.push();
        float f = this.getSignScale();
        matrices.scale(f, -f, -f);
        SpriteIdentifier lv = this.getTextureId(woodType);
        VertexConsumer lv2 = lv.getVertexConsumer(vertexConsumers, model::getLayer);
        model.render(matrices, lv2, light, overlay);
        matrices.pop();
    }

    private void renderText(BlockPos pos, SignText text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int textLineHeight, int maxTextWidth, boolean front) {
        int o;
        boolean bl2;
        int n;
        matrices.push();
        this.applyTextTransforms(matrices, front, this.getTextOffset());
        int l = AbstractSignBlockEntityRenderer.getTextColor(text);
        int m = 4 * textLineHeight / 2;
        OrderedText[] lvs = text.getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), textx -> {
            List<OrderedText> list = this.textRenderer.wrapLines((StringVisitable)textx, maxTextWidth);
            return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
        });
        if (text.isGlowing()) {
            n = text.getColor().getSignColor();
            bl2 = AbstractSignBlockEntityRenderer.shouldRenderTextOutline(pos, n);
            o = 0xF000F0;
        } else {
            n = l;
            bl2 = false;
            o = light;
        }
        for (int p = 0; p < 4; ++p) {
            OrderedText lv = lvs[p];
            float f = -this.textRenderer.getWidth(lv) / 2;
            if (bl2) {
                this.textRenderer.drawWithOutline(lv, f, p * textLineHeight - m, n, l, matrices.peek().getPositionMatrix(), vertexConsumers, o);
                continue;
            }
            this.textRenderer.draw(lv, f, (float)(p * textLineHeight - m), n, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, o);
        }
        matrices.pop();
    }

    private void applyTextTransforms(MatrixStack matrices, boolean front, Vec3d textOffset) {
        if (!front) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }
        float f = 0.015625f * this.getTextScale();
        matrices.translate(textOffset);
        matrices.scale(f, -f, f);
    }

    private static boolean shouldRenderTextOutline(BlockPos pos, int color) {
        if (color == DyeColor.BLACK.getSignColor()) {
            return true;
        }
        MinecraftClient lv = MinecraftClient.getInstance();
        ClientPlayerEntity lv2 = lv.player;
        if (lv2 != null && lv.options.getPerspective().isFirstPerson() && lv2.isUsingSpyglass()) {
            return true;
        }
        Entity lv3 = lv.getCameraEntity();
        return lv3 != null && lv3.squaredDistanceTo(Vec3d.ofCenter(pos)) < (double)MAX_COLORED_TEXT_OUTLINE_RENDER_DISTANCE;
    }

    public static int getTextColor(SignText text) {
        int i = text.getColor().getSignColor();
        if (i == DyeColor.BLACK.getSignColor() && text.isGlowing()) {
            return -988212;
        }
        double d = 0.4;
        int j = (int)((double)ColorHelper.getRed(i) * 0.4);
        int k = (int)((double)ColorHelper.getGreen(i) * 0.4);
        int l = (int)((double)ColorHelper.getBlue(i) * 0.4);
        return ColorHelper.getArgb(0, j, k, l);
    }
}

