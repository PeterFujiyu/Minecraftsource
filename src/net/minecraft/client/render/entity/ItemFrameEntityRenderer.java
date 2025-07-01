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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemFrameEntityRenderState;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer<T extends ItemFrameEntity>
extends EntityRenderer<T, ItemFrameEntityRenderState> {
    public static final int GLOW_FRAME_BLOCK_LIGHT = 5;
    public static final int field_32933 = 30;
    private final ItemModelManager itemModelManager;
    private final MapRenderer mapRenderer;
    private final BlockRenderManager blockRenderManager;

    public ItemFrameEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.itemModelManager = arg.getItemModelManager();
        this.mapRenderer = arg.getMapRenderer();
        this.blockRenderManager = arg.getBlockRenderManager();
    }

    @Override
    protected int getBlockLight(T arg, BlockPos arg2) {
        if (((Entity)arg).getType() == EntityType.GLOW_ITEM_FRAME) {
            return Math.max(5, super.getBlockLight(arg, arg2));
        }
        return super.getBlockLight(arg, arg2);
    }

    @Override
    public void render(ItemFrameEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float g;
        float f;
        super.render(arg, arg2, arg3, i);
        arg2.push();
        Direction lv = arg.facing;
        Vec3d lv2 = this.getPositionOffset(arg);
        arg2.translate(-lv2.getX(), -lv2.getY(), -lv2.getZ());
        double d = 0.46875;
        arg2.translate((double)lv.getOffsetX() * 0.46875, (double)lv.getOffsetY() * 0.46875, (double)lv.getOffsetZ() * 0.46875);
        if (lv.getAxis().isHorizontal()) {
            f = 0.0f;
            g = 180.0f - lv.getPositiveHorizontalDegrees();
        } else {
            f = -90 * lv.getDirection().offset();
            g = 180.0f;
        }
        arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f));
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
        if (!arg.invisible) {
            BakedModelManager lv3 = this.blockRenderManager.getModels().getModelManager();
            ModelIdentifier lv4 = ItemFrameEntityRenderer.getModelId(arg);
            arg2.push();
            arg2.translate(-0.5f, -0.5f, -0.5f);
            this.blockRenderManager.getModelRenderer().render(arg2.peek(), arg3.getBuffer(RenderLayer.getEntitySolidZOffsetForward(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), null, lv3.getModel(lv4), 1.0f, 1.0f, 1.0f, i, OverlayTexture.DEFAULT_UV);
            arg2.pop();
        }
        if (arg.invisible) {
            arg2.translate(0.0f, 0.0f, 0.5f);
        } else {
            arg2.translate(0.0f, 0.0f, 0.4375f);
        }
        if (arg.mapId != null) {
            int j = arg.rotation % 4 * 2;
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)j * 360.0f / 8.0f));
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            float h = 0.0078125f;
            arg2.scale(0.0078125f, 0.0078125f, 0.0078125f);
            arg2.translate(-64.0f, -64.0f, 0.0f);
            arg2.translate(0.0f, 0.0f, -1.0f);
            int k = this.getLight(arg.glow, 15728850, i);
            this.mapRenderer.draw(arg.mapRenderState, arg2, arg3, true, k);
        } else if (!arg.itemRenderState.isEmpty()) {
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)arg.rotation * 360.0f / 8.0f));
            int j = this.getLight(arg.glow, 0xF000F0, i);
            arg2.scale(0.5f, 0.5f, 0.5f);
            arg.itemRenderState.render(arg2, arg3, j, OverlayTexture.DEFAULT_UV);
        }
        arg2.pop();
    }

    private int getLight(boolean glow, int glowLight, int regularLight) {
        return glow ? glowLight : regularLight;
    }

    private static ModelIdentifier getModelId(ItemFrameEntityRenderState state) {
        if (state.mapId != null) {
            return state.glow ? BlockStatesLoader.MAP_GLOW_ITEM_FRAME_MODEL_ID : BlockStatesLoader.MAP_ITEM_FRAME_MODEL_ID;
        }
        return state.glow ? BlockStatesLoader.GLOW_ITEM_FRAME_MODEL_ID : BlockStatesLoader.ITEM_FRAME_MODEL_ID;
    }

    @Override
    public Vec3d getPositionOffset(ItemFrameEntityRenderState arg) {
        return new Vec3d((float)arg.facing.getOffsetX() * 0.3f, -0.25, (float)arg.facing.getOffsetZ() * 0.3f);
    }

    @Override
    protected boolean hasLabel(T arg, double d) {
        return MinecraftClient.isHudEnabled() && this.dispatcher.targetedEntity == arg && ((ItemFrameEntity)arg).getHeldItemStack().getCustomName() != null;
    }

    @Override
    protected Text getDisplayName(T arg) {
        return ((ItemFrameEntity)arg).getHeldItemStack().getName();
    }

    @Override
    public ItemFrameEntityRenderState createRenderState() {
        return new ItemFrameEntityRenderState();
    }

    @Override
    public void updateRenderState(T arg, ItemFrameEntityRenderState arg2, float f) {
        MapState lv3;
        MapIdComponent lv2;
        super.updateRenderState(arg, arg2, f);
        arg2.facing = ((AbstractDecorationEntity)arg).getHorizontalFacing();
        ItemStack lv = ((ItemFrameEntity)arg).getHeldItemStack();
        this.itemModelManager.updateForNonLivingEntity(arg2.itemRenderState, lv, ModelTransformationMode.FIXED, (Entity)arg);
        arg2.rotation = ((ItemFrameEntity)arg).getRotation();
        arg2.glow = ((Entity)arg).getType() == EntityType.GLOW_ITEM_FRAME;
        arg2.mapId = null;
        if (!lv.isEmpty() && (lv2 = ((ItemFrameEntity)arg).getMapId(lv)) != null && (lv3 = ((Entity)arg).getWorld().getMapState(lv2)) != null) {
            this.mapRenderer.update(lv2, lv3, arg2.mapRenderState);
            arg2.mapId = lv2;
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ Text getDisplayName(Entity entity) {
        return this.getDisplayName((T)((ItemFrameEntity)entity));
    }
}

