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
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class ItemEntityRenderer
extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
    private static final float field_32924 = 0.15f;
    private static final float field_32929 = 0.0f;
    private static final float field_32930 = 0.0f;
    private static final float field_32931 = 0.09375f;
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();

    public ItemEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.itemModelManager = arg.getItemModelManager();
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    @Override
    public ItemEntityRenderState createRenderState() {
        return new ItemEntityRenderState();
    }

    @Override
    public void updateRenderState(ItemEntity arg, ItemEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.age = (float)arg.getItemAge() + f;
        arg2.uniqueOffset = arg.uniqueOffset;
        arg2.update(arg, arg.getStack(), this.itemModelManager);
    }

    @Override
    public void render(ItemEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        if (arg.itemRenderState.isEmpty()) {
            return;
        }
        arg2.push();
        float f = 0.25f;
        float g = MathHelper.sin(arg.age / 10.0f + arg.uniqueOffset) * 0.1f + 0.1f;
        float h = arg.itemRenderState.getTransformation().scale.y();
        arg2.translate(0.0f, g + 0.25f * h, 0.0f);
        float j = ItemEntity.getRotation(arg.age, arg.uniqueOffset);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotation(j));
        ItemEntityRenderer.renderStack(arg2, arg3, i, arg, this.random);
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    public static void renderStack(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStackEntityRenderState state, Random random) {
        float m;
        float l;
        random.setSeed(state.seed);
        int j = state.renderedAmount;
        ItemRenderState lv = state.itemRenderState;
        boolean bl = lv.hasDepth();
        float f = lv.getTransformation().scale.x();
        float g = lv.getTransformation().scale.y();
        float h = lv.getTransformation().scale.z();
        if (!bl) {
            float k = -0.0f * (float)(j - 1) * 0.5f * f;
            l = -0.0f * (float)(j - 1) * 0.5f * g;
            m = -0.09375f * (float)(j - 1) * 0.5f * h;
            matrices.translate(k, l, m);
        }
        for (int n = 0; n < j; ++n) {
            matrices.push();
            if (n > 0) {
                if (bl) {
                    l = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    m = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float o = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrices.translate(l, m, o);
                } else {
                    l = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    m = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrices.translate(l, m, 0.0f);
                }
            }
            lv.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
            if (bl) continue;
            matrices.translate(0.0f * f, 0.0f * g, 0.09375f * h);
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

