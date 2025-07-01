/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class RotatingCubeMapRenderer {
    public static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/gui/title/background/panorama_overlay.png");
    private final MinecraftClient client;
    private final CubeMapRenderer cubeMap;
    private float pitch;

    public RotatingCubeMapRenderer(CubeMapRenderer cubeMap) {
        this.cubeMap = cubeMap;
        this.client = MinecraftClient.getInstance();
    }

    public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
        float h = this.client.getRenderTickCounter().getLastDuration();
        float k = (float)((double)h * this.client.options.getPanoramaSpeed().getValue());
        this.pitch = RotatingCubeMapRenderer.wrapOnce(this.pitch + k * 0.1f, 360.0f);
        context.draw();
        this.cubeMap.draw(this.client, 10.0f, -this.pitch, alpha);
        context.draw();
        context.drawTexture(RenderLayer::getGuiTextured, OVERLAY_TEXTURE, 0, 0, 0.0f, 0.0f, width, height, 16, 128, 16, 128, ColorHelper.getWhite(alpha));
    }

    private static float wrapOnce(float a, float b) {
        return a > b ? a - b : a;
    }
}

