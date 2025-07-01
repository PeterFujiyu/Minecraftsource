/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.MapDecorationsAtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class MapRenderer {
    private static final float field_53102 = -0.01f;
    private static final float field_53103 = -0.001f;
    private static final int DEFAULT_IMAGE_WIDTH = 128;
    private static final int DEFAULT_IMAGE_HEIGHT = 128;
    private final MapTextureManager textureManager;
    private final MapDecorationsAtlasManager decorationsAtlasManager;

    public MapRenderer(MapDecorationsAtlasManager decorationsAtlasManager, MapTextureManager textureManager) {
        this.decorationsAtlasManager = decorationsAtlasManager;
        this.textureManager = textureManager;
    }

    public void draw(MapRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean bl, int light) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getText(state.texture));
        lv.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(Colors.WHITE).texture(0.0f, 1.0f).light(light);
        lv.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(Colors.WHITE).texture(1.0f, 1.0f).light(light);
        lv.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(Colors.WHITE).texture(1.0f, 0.0f).light(light);
        lv.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(Colors.WHITE).texture(0.0f, 0.0f).light(light);
        int j = 0;
        for (MapRenderState.Decoration lv2 : state.decorations) {
            if (bl && !lv2.alwaysRendered) continue;
            matrices.push();
            matrices.translate((float)lv2.x / 2.0f + 64.0f, (float)lv2.z / 2.0f + 64.0f, -0.02f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)(lv2.rotation * 360) / 16.0f));
            matrices.scale(4.0f, 4.0f, 3.0f);
            matrices.translate(-0.125f, 0.125f, 0.0f);
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            Sprite lv3 = lv2.sprite;
            if (lv3 != null) {
                VertexConsumer lv4 = vertexConsumers.getBuffer(RenderLayer.getText(lv3.getAtlasId()));
                lv4.vertex(matrix4f2, -1.0f, 1.0f, (float)j * -0.001f).color(Colors.WHITE).texture(lv3.getMinU(), lv3.getMinV()).light(light);
                lv4.vertex(matrix4f2, 1.0f, 1.0f, (float)j * -0.001f).color(Colors.WHITE).texture(lv3.getMaxU(), lv3.getMinV()).light(light);
                lv4.vertex(matrix4f2, 1.0f, -1.0f, (float)j * -0.001f).color(Colors.WHITE).texture(lv3.getMaxU(), lv3.getMaxV()).light(light);
                lv4.vertex(matrix4f2, -1.0f, -1.0f, (float)j * -0.001f).color(Colors.WHITE).texture(lv3.getMinU(), lv3.getMaxV()).light(light);
                matrices.pop();
            }
            if (lv2.name != null) {
                TextRenderer lv5 = MinecraftClient.getInstance().textRenderer;
                float f = lv5.getWidth(lv2.name);
                float f2 = 25.0f / f;
                Objects.requireNonNull(lv5);
                float g = MathHelper.clamp(f2, 0.0f, 6.0f / 9.0f);
                matrices.push();
                matrices.translate((float)lv2.x / 2.0f + 64.0f - f * g / 2.0f, (float)lv2.z / 2.0f + 64.0f + 4.0f, -0.025f);
                matrices.scale(g, g, 1.0f);
                matrices.translate(0.0f, 0.0f, -0.1f);
                lv5.draw(lv2.name, 0.0f, 0.0f, -1, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, Integer.MIN_VALUE, light, false);
                matrices.pop();
            }
            ++j;
        }
    }

    public void update(MapIdComponent mapId, MapState mapState, MapRenderState renderState) {
        renderState.texture = this.textureManager.getTextureId(mapId, mapState);
        renderState.decorations.clear();
        for (MapDecoration lv : mapState.getDecorations()) {
            renderState.decorations.add(this.createDecoration(lv));
        }
    }

    private MapRenderState.Decoration createDecoration(MapDecoration decoration) {
        MapRenderState.Decoration lv = new MapRenderState.Decoration();
        lv.sprite = this.decorationsAtlasManager.getSprite(decoration);
        lv.x = decoration.x();
        lv.z = decoration.z();
        lv.rotation = decoration.rotation();
        lv.name = decoration.name().orElse(null);
        lv.alwaysRendered = decoration.isAlwaysRendered();
        return lv;
    }
}

