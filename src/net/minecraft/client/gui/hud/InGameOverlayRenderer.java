/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class InGameOverlayRenderer {
    private static final Identifier UNDERWATER_TEXTURE = Identifier.ofVanilla("textures/misc/underwater.png");

    public static void renderOverlays(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        BlockState lv2;
        ClientPlayerEntity lv = client.player;
        if (!lv.noClip && (lv2 = InGameOverlayRenderer.getInWallBlockState(lv)) != null) {
            InGameOverlayRenderer.renderInWallOverlay(client.getBlockRenderManager().getModels().getModelParticleSprite(lv2), matrices, vertexConsumers);
        }
        if (!client.player.isSpectator()) {
            if (client.player.isSubmergedIn(FluidTags.WATER)) {
                InGameOverlayRenderer.renderUnderwaterOverlay(client, matrices, vertexConsumers);
            }
            if (client.player.isOnFire()) {
                InGameOverlayRenderer.renderFireOverlay(matrices, vertexConsumers);
            }
        }
    }

    @Nullable
    private static BlockState getInWallBlockState(PlayerEntity player) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i < 8; ++i) {
            double d = player.getX() + (double)(((float)((i >> 0) % 2) - 0.5f) * player.getWidth() * 0.8f);
            double e = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f * player.getScale());
            double f = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * player.getWidth() * 0.8f);
            lv.set(d, e, f);
            BlockState lv2 = player.getWorld().getBlockState(lv);
            if (lv2.getRenderType() == BlockRenderType.INVISIBLE || !lv2.shouldBlockVision(player.getWorld(), lv)) continue;
            return lv2;
        }
        return null;
    }

    private static void renderInWallOverlay(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        float f = 0.1f;
        int i = ColorHelper.fromFloats(1.0f, 0.1f, 0.1f, 0.1f);
        float g = -1.0f;
        float h = 1.0f;
        float j = -1.0f;
        float k = 1.0f;
        float l = -0.5f;
        float m = sprite.getMinU();
        float n = sprite.getMaxU();
        float o = sprite.getMinV();
        float p = sprite.getMaxV();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getBlockScreenEffect(sprite.getAtlasId()));
        lv.vertex(matrix4f, -1.0f, -1.0f, -0.5f).texture(n, p).color(i);
        lv.vertex(matrix4f, 1.0f, -1.0f, -0.5f).texture(m, p).color(i);
        lv.vertex(matrix4f, 1.0f, 1.0f, -0.5f).texture(m, o).color(i);
        lv.vertex(matrix4f, -1.0f, 1.0f, -0.5f).texture(n, o).color(i);
    }

    private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        BlockPos lv = BlockPos.ofFloored(client.player.getX(), client.player.getEyeY(), client.player.getZ());
        float f = LightmapTextureManager.getBrightness(client.player.getWorld().getDimension(), client.player.getWorld().getLightLevel(lv));
        int i = ColorHelper.fromFloats(0.1f, f, f, f);
        float g = 4.0f;
        float h = -1.0f;
        float j = 1.0f;
        float k = -1.0f;
        float l = 1.0f;
        float m = -0.5f;
        float n = -client.player.getYaw() / 64.0f;
        float o = client.player.getPitch() / 64.0f;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer lv2 = vertexConsumers.getBuffer(RenderLayer.getBlockScreenEffect(UNDERWATER_TEXTURE));
        lv2.vertex(matrix4f, -1.0f, -1.0f, -0.5f).texture(4.0f + n, 4.0f + o).color(i);
        lv2.vertex(matrix4f, 1.0f, -1.0f, -0.5f).texture(0.0f + n, 4.0f + o).color(i);
        lv2.vertex(matrix4f, 1.0f, 1.0f, -0.5f).texture(0.0f + n, 0.0f + o).color(i);
        lv2.vertex(matrix4f, -1.0f, 1.0f, -0.5f).texture(4.0f + n, 0.0f + o).color(i);
    }

    private static void renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Sprite lv = ModelBaker.FIRE_1.getSprite();
        VertexConsumer lv2 = vertexConsumers.getBuffer(RenderLayer.getFireScreenEffect(lv.getAtlasId()));
        float f = lv.getMinU();
        float g = lv.getMaxU();
        float h = (f + g) / 2.0f;
        float i = lv.getMinV();
        float j = lv.getMaxV();
        float k = (i + j) / 2.0f;
        float l = lv.getAnimationFrameDelta();
        float m = MathHelper.lerp(l, f, h);
        float n = MathHelper.lerp(l, g, h);
        float o = MathHelper.lerp(l, i, k);
        float p = MathHelper.lerp(l, j, k);
        float q = 1.0f;
        for (int r = 0; r < 2; ++r) {
            matrices.push();
            float s = -0.5f;
            float t = 0.5f;
            float u = -0.5f;
            float v = 0.5f;
            float w = -0.5f;
            matrices.translate((float)(-(r * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(r * 2 - 1) * 10.0f));
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            lv2.vertex(matrix4f, -0.5f, -0.5f, -0.5f).texture(n, p).color(1.0f, 1.0f, 1.0f, 0.9f);
            lv2.vertex(matrix4f, 0.5f, -0.5f, -0.5f).texture(m, p).color(1.0f, 1.0f, 1.0f, 0.9f);
            lv2.vertex(matrix4f, 0.5f, 0.5f, -0.5f).texture(m, o).color(1.0f, 1.0f, 1.0f, 0.9f);
            lv2.vertex(matrix4f, -0.5f, 0.5f, -0.5f).texture(n, o).color(1.0f, 1.0f, 1.0f, 0.9f);
            matrices.pop();
        }
    }
}

