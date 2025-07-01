/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    private final ChestBlockModel singleChest;
    private final ChestBlockModel doubleChestLeft;
    private final ChestBlockModel doubleChestRight;
    private final boolean christmas = ChestBlockEntityRenderer.isAroundChristmas();

    public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.singleChest = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.doubleChestLeft = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.doubleChestRight = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
    }

    public static boolean isAroundChristmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World lv = ((BlockEntity)entity).getWorld();
        boolean bl = lv != null;
        BlockState lv2 = bl ? ((BlockEntity)entity).getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType lv3 = lv2.contains(ChestBlock.CHEST_TYPE) ? lv2.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        Block lv4 = lv2.getBlock();
        if (!(lv4 instanceof AbstractChestBlock)) {
            return;
        }
        AbstractChestBlock lv5 = (AbstractChestBlock)lv4;
        boolean bl2 = lv3 != ChestType.SINGLE;
        matrices.push();
        float g = lv2.get(ChestBlock.FACING).getPositiveHorizontalDegrees();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-g));
        matrices.translate(-0.5f, -0.5f, -0.5f);
        DoubleBlockProperties.PropertySource<Object> lv6 = bl ? lv5.getBlockEntitySource(lv2, lv, ((BlockEntity)entity).getPos(), true) : DoubleBlockProperties.PropertyRetriever::getFallback;
        float h = lv6.apply(ChestBlock.getAnimationProgressRetriever((LidOpenable)entity)).get(tickDelta);
        h = 1.0f - h;
        h = 1.0f - h * h * h;
        int k = ((Int2IntFunction)lv6.apply(new LightmapCoordinatesRetriever())).applyAsInt(light);
        SpriteIdentifier lv7 = TexturedRenderLayers.getChestTextureId(entity, lv3, this.christmas);
        VertexConsumer lv8 = lv7.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        if (bl2) {
            if (lv3 == ChestType.LEFT) {
                this.render(matrices, lv8, this.doubleChestLeft, h, k, overlay);
            } else {
                this.render(matrices, lv8, this.doubleChestRight, h, k, overlay);
            }
        } else {
            this.render(matrices, lv8, this.singleChest, h, k, overlay);
        }
        matrices.pop();
    }

    private void render(MatrixStack matrices, VertexConsumer vertices, ChestBlockModel model, float animationProgress, int light, int overlay) {
        model.setLockAndLidPitch(animationProgress);
        model.render(matrices, vertices, light, overlay);
    }
}

