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
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class VaultBlockEntityRenderer
implements BlockEntityRenderer<VaultBlockEntity> {
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();
    private final ItemStackEntityRenderState itemRenderState = new ItemStackEntityRenderState();

    public VaultBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public void render(VaultBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        if (!VaultBlockEntity.Client.hasDisplayItem(arg.getSharedData())) {
            return;
        }
        World lv = arg.getWorld();
        if (lv == null) {
            return;
        }
        ItemStack lv2 = arg.getSharedData().getDisplayItem();
        if (lv2.isEmpty()) {
            return;
        }
        this.itemModelManager.update(this.itemRenderState.itemRenderState, lv2, ModelTransformationMode.GROUND, false, lv, null, 0);
        this.itemRenderState.renderedAmount = ItemStackEntityRenderState.getRenderedAmount(lv2.getCount());
        this.itemRenderState.seed = ItemStackEntityRenderState.getSeed(lv2);
        VaultClientData lv3 = arg.getClientData();
        arg2.push();
        arg2.translate(0.5f, 0.4f, 0.5f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerpAngleDegrees(f, lv3.getPreviousDisplayRotation(), lv3.getDisplayRotation())));
        ItemEntityRenderer.renderStack(arg2, arg3, i, this.itemRenderState, this.random);
        arg2.pop();
    }
}

