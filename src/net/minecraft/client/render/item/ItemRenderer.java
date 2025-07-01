/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderer {
    public static final Identifier ENTITY_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_entity.png");
    public static final Identifier ITEM_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_item.png");
    public static final int field_32937 = 8;
    public static final int field_32938 = 8;
    public static final int field_32934 = 200;
    public static final float COMPASS_WITH_GLINT_GUI_MODEL_MULTIPLIER = 0.5f;
    public static final float COMPASS_WITH_GLINT_FIRST_PERSON_MODEL_MULTIPLIER = 0.75f;
    public static final float field_41120 = 0.0078125f;
    public static final int field_55295 = -1;
    private final ItemModelManager itemModelManager;
    private final ItemRenderState itemRenderState = new ItemRenderState();

    public ItemRenderer(ItemModelManager itemModelManager) {
        this.itemModelManager = itemModelManager;
    }

    private static void renderBakedItemModel(BakedModel model, int[] tints, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer) {
        Random lv = Random.create();
        long l = 42L;
        for (Direction lv2 : Direction.values()) {
            lv.setSeed(42L);
            ItemRenderer.renderBakedItemQuads(matrices, vertexConsumer, model.getQuads(null, lv2, lv), tints, light, overlay);
        }
        lv.setSeed(42L);
        ItemRenderer.renderBakedItemQuads(matrices, vertexConsumer, model.getQuads(null, null, lv), tints, light, overlay);
    }

    public static void renderItem(ModelTransformationMode transformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int[] tints, BakedModel model, RenderLayer layer, ItemRenderState.Glint glint) {
        VertexConsumer lv2;
        if (glint == ItemRenderState.Glint.SPECIAL) {
            MatrixStack.Entry lv = matrices.peek().copy();
            if (transformationMode == ModelTransformationMode.GUI) {
                MatrixUtil.scale(lv.getPositionMatrix(), 0.5f);
            } else if (transformationMode.isFirstPerson()) {
                MatrixUtil.scale(lv.getPositionMatrix(), 0.75f);
            }
            lv2 = ItemRenderer.getDynamicDisplayGlintConsumer(vertexConsumers, layer, lv);
        } else {
            lv2 = ItemRenderer.getItemGlintConsumer(vertexConsumers, layer, true, glint != ItemRenderState.Glint.NONE);
        }
        ItemRenderer.renderBakedItemModel(model, tints, light, overlay, matrices, lv2);
    }

    public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean glint) {
        if (glint) {
            return VertexConsumers.union(provider.getBuffer(RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer));
        }
        return provider.getBuffer(layer);
    }

    private static VertexConsumer getDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
        return VertexConsumers.union((VertexConsumer)new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getGlint()), entry, 0.0078125f), provider.getBuffer(layer));
    }

    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            if (MinecraftClient.isFabulousGraphicsOrBetter() && layer == TexturedRenderLayers.getItemEntityTranslucentCull()) {
                return VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlintTranslucent()), vertexConsumers.getBuffer(layer));
            }
            return VertexConsumers.union(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer));
        }
        return vertexConsumers.getBuffer(layer);
    }

    private static int getTint(int[] tints, int index) {
        if (index >= tints.length) {
            return -1;
        }
        return tints[index];
    }

    private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay) {
        MatrixStack.Entry lv = matrices.peek();
        for (BakedQuad lv2 : quads) {
            float l;
            float h;
            float g;
            float f;
            if (lv2.hasTint()) {
                int k = ItemRenderer.getTint(tints, lv2.getTintIndex());
                f = (float)ColorHelper.getAlpha(k) / 255.0f;
                g = (float)ColorHelper.getRed(k) / 255.0f;
                h = (float)ColorHelper.getGreen(k) / 255.0f;
                l = (float)ColorHelper.getBlue(k) / 255.0f;
            } else {
                f = 1.0f;
                g = 1.0f;
                h = 1.0f;
                l = 1.0f;
            }
            vertexConsumer.quad(lv, lv2, g, h, l, f, light, overlay);
        }
    }

    public void renderItem(ItemStack stack, ModelTransformationMode transformationMode, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int seed) {
        this.renderItem(null, stack, transformationMode, false, matrices, vertexConsumers, world, light, overlay, seed);
    }

    public void renderItem(@Nullable LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {
        this.itemModelManager.update(this.itemRenderState, stack, transformationMode, leftHanded, world, entity, seed);
        this.itemRenderState.render(matrices, vertexConsumers, light, overlay);
    }
}

