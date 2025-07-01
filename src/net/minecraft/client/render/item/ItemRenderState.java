/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderState {
    ModelTransformationMode modelTransformationMode = ModelTransformationMode.NONE;
    boolean leftHand;
    private int layerCount;
    private LayerRenderState[] layers = new LayerRenderState[]{new LayerRenderState()};

    public void addLayers(int add) {
        int k = this.layerCount + add;
        int j = this.layers.length;
        if (k > j) {
            this.layers = Arrays.copyOf(this.layers, k);
            for (int l = j; l < k; ++l) {
                this.layers[l] = new LayerRenderState();
            }
        }
    }

    public LayerRenderState newLayer() {
        this.addLayers(1);
        return this.layers[this.layerCount++];
    }

    public void clear() {
        this.modelTransformationMode = ModelTransformationMode.NONE;
        this.leftHand = false;
        for (int i = 0; i < this.layerCount; ++i) {
            this.layers[i].clear();
        }
        this.layerCount = 0;
    }

    private LayerRenderState getFirstLayer() {
        return this.layers[0];
    }

    public boolean isEmpty() {
        return this.layerCount == 0;
    }

    public boolean hasDepth() {
        return this.getFirstLayer().hasDepth();
    }

    public boolean isSideLit() {
        return this.getFirstLayer().isSideLit();
    }

    @Nullable
    public Sprite getParticleSprite(Random random) {
        if (this.layerCount == 0) {
            return null;
        }
        BakedModel lv = this.layers[random.nextInt((int)this.layerCount)].model;
        if (lv == null) {
            return null;
        }
        return lv.getParticleSprite();
    }

    public Transformation getTransformation() {
        return this.getFirstLayer().getTransformation();
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        for (int k = 0; k < this.layerCount; ++k) {
            this.layers[k].render(matrices, vertexConsumers, light, overlay);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class LayerRenderState {
        @Nullable
        BakedModel model;
        @Nullable
        private RenderLayer renderLayer;
        private Glint glint = Glint.NONE;
        private int[] tints = new int[0];
        @Nullable
        private SpecialModelRenderer<Object> specialModelType;
        @Nullable
        private Object data;

        public void clear() {
            this.model = null;
            this.renderLayer = null;
            this.glint = Glint.NONE;
            this.specialModelType = null;
            this.data = null;
            Arrays.fill(this.tints, -1);
        }

        public void setModel(BakedModel model, RenderLayer renderLayer) {
            this.model = model;
            this.renderLayer = renderLayer;
        }

        public <T> void setSpecialModel(SpecialModelRenderer<T> specialModelType, @Nullable T data, BakedModel model) {
            this.model = model;
            this.specialModelType = LayerRenderState.eraseType(specialModelType);
            this.data = data;
        }

        private static SpecialModelRenderer<Object> eraseType(SpecialModelRenderer<?> specialModelType) {
            return specialModelType;
        }

        public void setGlint(Glint glint) {
            this.glint = glint;
        }

        public int[] initTints(int maxIndex) {
            if (maxIndex > this.tints.length) {
                this.tints = new int[maxIndex];
                Arrays.fill(this.tints, -1);
            }
            return this.tints;
        }

        Transformation getTransformation() {
            return this.model != null ? this.model.getTransformation().getTransformation(ItemRenderState.this.modelTransformationMode) : Transformation.IDENTITY;
        }

        void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
            matrices.push();
            this.getTransformation().apply(ItemRenderState.this.leftHand, matrices);
            matrices.translate(-0.5f, -0.5f, -0.5f);
            if (this.specialModelType != null) {
                this.specialModelType.render(this.data, ItemRenderState.this.modelTransformationMode, matrices, vertexConsumers, light, overlay, this.glint != Glint.NONE);
            } else if (this.model != null) {
                ItemRenderer.renderItem(ItemRenderState.this.modelTransformationMode, matrices, vertexConsumers, light, overlay, this.tints, this.model, this.renderLayer, this.glint);
            }
            matrices.pop();
        }

        boolean hasDepth() {
            return this.model != null && this.model.hasDepth();
        }

        boolean isSideLit() {
            return this.model != null && this.model.isSideLit();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Glint {
        NONE,
        STANDARD,
        SPECIAL;

    }
}

