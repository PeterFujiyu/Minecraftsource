/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class BufferBuilderStorage {
    private final BlockBufferAllocatorStorage blockBufferBuilders = new BlockBufferAllocatorStorage();
    private final BlockBufferBuilderPool blockBufferBuildersPool;
    private final VertexConsumerProvider.Immediate entityVertexConsumers;
    private final VertexConsumerProvider.Immediate effectVertexConsumers;
    private final OutlineVertexConsumerProvider outlineVertexConsumers;

    public BufferBuilderStorage(int maxBlockBuildersPoolSize) {
        this.blockBufferBuildersPool = BlockBufferBuilderPool.allocate(maxBlockBuildersPoolSize);
        SequencedMap sequencedMap = Util.make(new Object2ObjectLinkedOpenHashMap(), map -> {
            map.put(TexturedRenderLayers.getEntitySolid(), this.blockBufferBuilders.get(RenderLayer.getSolid()));
            map.put(TexturedRenderLayers.getEntityCutout(), this.blockBufferBuilders.get(RenderLayer.getCutout()));
            map.put(TexturedRenderLayers.getBannerPatterns(), this.blockBufferBuilders.get(RenderLayer.getCutoutMipped()));
            map.put(TexturedRenderLayers.getItemEntityTranslucentCull(), this.blockBufferBuilders.get(RenderLayer.getTranslucent()));
            BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getShieldPatterns());
            BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getBeds());
            BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getShulkerBoxes());
            BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getSign());
            BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getHangingSign());
            map.put(TexturedRenderLayers.getChest(), new BufferAllocator(786432));
            BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getArmorEntityGlint());
            BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getGlint());
            BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getGlintTranslucent());
            BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getEntityGlint());
            BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getWaterMask());
        });
        this.entityVertexConsumers = VertexConsumerProvider.immediate(sequencedMap, new BufferAllocator(786432));
        this.outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);
        SequencedMap sequencedMap2 = Util.make(new Object2ObjectLinkedOpenHashMap(), object2ObjectLinkedOpenHashMap -> ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(renderLayer -> BufferBuilderStorage.assignBufferBuilder(object2ObjectLinkedOpenHashMap, renderLayer)));
        this.effectVertexConsumers = VertexConsumerProvider.immediate(sequencedMap2, new BufferAllocator(0));
    }

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage, RenderLayer layer) {
        builderStorage.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
    }

    public BlockBufferAllocatorStorage getBlockBufferBuilders() {
        return this.blockBufferBuilders;
    }

    public BlockBufferBuilderPool getBlockBufferBuildersPool() {
        return this.blockBufferBuildersPool;
    }

    public VertexConsumerProvider.Immediate getEntityVertexConsumers() {
        return this.entityVertexConsumers;
    }

    public VertexConsumerProvider.Immediate getEffectVertexConsumers() {
        return this.effectVertexConsumers;
    }

    public OutlineVertexConsumerProvider getOutlineVertexConsumers() {
        return this.outlineVertexConsumers;
    }
}

