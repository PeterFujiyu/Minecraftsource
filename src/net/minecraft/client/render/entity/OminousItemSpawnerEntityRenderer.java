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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class OminousItemSpawnerEntityRenderer
extends EntityRenderer<OminousItemSpawnerEntity, ItemStackEntityRenderState> {
    private static final float field_50231 = 40.0f;
    private static final int field_50232 = 50;
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();

    protected OminousItemSpawnerEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.itemModelManager = arg.getItemModelManager();
    }

    @Override
    public ItemStackEntityRenderState createRenderState() {
        return new ItemStackEntityRenderState();
    }

    @Override
    public void updateRenderState(OminousItemSpawnerEntity arg, ItemStackEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ItemStack lv = arg.getItem();
        arg2.update(arg, lv, this.itemModelManager);
    }

    @Override
    public void render(ItemStackEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float f;
        if (arg.itemRenderState.isEmpty()) {
            return;
        }
        arg2.push();
        if (arg.age <= 50.0f) {
            f = Math.min(arg.age, 50.0f) / 50.0f;
            arg2.scale(f, f, f);
        }
        f = MathHelper.wrapDegrees(arg.age * 40.0f);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f));
        ItemEntityRenderer.renderStack(arg2, arg3, 0xF000F0, arg, this.random);
        arg2.pop();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

