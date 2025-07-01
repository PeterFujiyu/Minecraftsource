/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderer
extends MobEntityRenderer<ShulkerEntity, ShulkerEntityRenderState, ShulkerEntityModel> {
    private static final Identifier TEXTURE = TexturedRenderLayers.SHULKER_TEXTURE_ID.getTextureId().withPath(string -> "textures/" + string + ".png");
    private static final Identifier[] COLORED_TEXTURES = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.stream().map(spriteId -> spriteId.getTextureId().withPath(string -> "textures/" + string + ".png")).toArray(Identifier[]::new);

    public ShulkerEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new ShulkerEntityModel(arg.getPart(EntityModelLayers.SHULKER)), 0.0f);
    }

    @Override
    public Vec3d getPositionOffset(ShulkerEntityRenderState arg) {
        return arg.renderPositionOffset;
    }

    @Override
    public boolean shouldRender(ShulkerEntity arg, Frustum arg2, double d, double e, double f) {
        if (super.shouldRender(arg, arg2, d, e, f)) {
            return true;
        }
        Vec3d lv = arg.getRenderPositionOffset(0.0f);
        if (lv == null) {
            return false;
        }
        EntityType<?> lv2 = arg.getType();
        float g = lv2.getHeight() / 2.0f;
        float h = lv2.getWidth() / 2.0f;
        Vec3d lv3 = Vec3d.ofBottomCenter(arg.getBlockPos());
        return arg2.isVisible(new Box(lv.x, lv.y + (double)g, lv.z, lv3.x, lv3.y + (double)g, lv3.z).expand(h, g, h));
    }

    @Override
    public Identifier getTexture(ShulkerEntityRenderState arg) {
        return ShulkerEntityRenderer.getTexture(arg.color);
    }

    @Override
    public ShulkerEntityRenderState createRenderState() {
        return new ShulkerEntityRenderState();
    }

    @Override
    public void updateRenderState(ShulkerEntity arg, ShulkerEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.renderPositionOffset = Objects.requireNonNullElse(arg.getRenderPositionOffset(f), Vec3d.ZERO);
        arg2.color = arg.getColor();
        arg2.openProgress = arg.getOpenProgress(f);
        arg2.headYaw = arg.headYaw;
        arg2.shellYaw = arg.bodyYaw;
        arg2.facing = arg.getAttachedFace();
    }

    public static Identifier getTexture(@Nullable DyeColor shulkerColor) {
        if (shulkerColor == null) {
            return TEXTURE;
        }
        return COLORED_TEXTURES[shulkerColor.getId()];
    }

    @Override
    protected void setupTransforms(ShulkerEntityRenderState arg, MatrixStack arg2, float f, float g) {
        super.setupTransforms(arg, arg2, f + 180.0f, g);
        arg2.multiply(arg.facing.getOpposite().getRotationQuaternion(), 0.0f, 0.5f, 0.0f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

