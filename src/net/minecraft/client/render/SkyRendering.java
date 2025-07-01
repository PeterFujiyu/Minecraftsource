/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class SkyRendering
implements AutoCloseable {
    private static final Identifier SUN_TEXTURE = Identifier.ofVanilla("textures/environment/sun.png");
    private static final Identifier MOON_PHASES_TEXTURE = Identifier.ofVanilla("textures/environment/moon_phases.png");
    public static final Identifier END_SKY_TEXTURE = Identifier.ofVanilla("textures/environment/end_sky.png");
    private static final float field_53144 = 512.0f;
    private final VertexBuffer starBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION, this::tessellateStar);
    private final VertexBuffer skyBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION, vertexConsumer -> this.tessellateSky((VertexConsumer)vertexConsumer, 16.0f));
    private final VertexBuffer darkSkyBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION, vertexConsumer -> this.tessellateSky((VertexConsumer)vertexConsumer, -16.0f));
    private final VertexBuffer endSkyBuffer = VertexBuffer.createAndUpload(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR, this::tessellateEndSky);

    private void tessellateStar(VertexConsumer vertexConsumer) {
        Random lv = Random.create(10842L);
        int i = 1500;
        float f = 100.0f;
        for (int j = 0; j < 1500; ++j) {
            float g = lv.nextFloat() * 2.0f - 1.0f;
            float h = lv.nextFloat() * 2.0f - 1.0f;
            float k = lv.nextFloat() * 2.0f - 1.0f;
            float l = 0.15f + lv.nextFloat() * 0.1f;
            float m = MathHelper.magnitude(g, h, k);
            if (m <= 0.010000001f || m >= 1.0f) continue;
            Vector3f vector3f = new Vector3f(g, h, k).normalize(100.0f);
            float n = (float)(lv.nextDouble() * 3.1415927410125732 * 2.0);
            Matrix3f matrix3f = new Matrix3f().rotateTowards(new Vector3f(vector3f).negate(), new Vector3f(0.0f, 1.0f, 0.0f)).rotateZ(-n);
            vertexConsumer.vertex(new Vector3f(l, -l, 0.0f).mul(matrix3f).add(vector3f));
            vertexConsumer.vertex(new Vector3f(l, l, 0.0f).mul(matrix3f).add(vector3f));
            vertexConsumer.vertex(new Vector3f(-l, l, 0.0f).mul(matrix3f).add(vector3f));
            vertexConsumer.vertex(new Vector3f(-l, -l, 0.0f).mul(matrix3f).add(vector3f));
        }
    }

    private void tessellateSky(VertexConsumer vertexConsumer, float height) {
        float g = Math.signum(height) * 512.0f;
        vertexConsumer.vertex(0.0f, height, 0.0f);
        for (int i = -180; i <= 180; i += 45) {
            vertexConsumer.vertex(g * MathHelper.cos((float)i * ((float)Math.PI / 180)), height, 512.0f * MathHelper.sin((float)i * ((float)Math.PI / 180)));
        }
    }

    public void renderSky(float red, float green, float blue) {
        RenderSystem.setShaderColor(red, green, blue, 1.0f);
        this.skyBuffer.draw(RenderLayer.getSky());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderSkyDark(MatrixStack matrices) {
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        matrices.push();
        matrices.translate(0.0f, 12.0f, 0.0f);
        this.darkSkyBuffer.draw(RenderLayer.getSky());
        matrices.pop();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderCelestialBodies(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float rot, int phase, float alpha, float starBrightness, Fog fog) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rot * 360.0f));
        this.renderSun(alpha, vertexConsumers, matrices);
        this.renderMoon(phase, alpha, vertexConsumers, matrices);
        vertexConsumers.draw();
        if (starBrightness > 0.0f) {
            this.renderStars(fog, starBrightness, matrices);
        }
        matrices.pop();
    }

    private void renderSun(float alpha, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        float g = 30.0f;
        float h = 100.0f;
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getCelestial(SUN_TEXTURE));
        int i = ColorHelper.getWhite(alpha);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        lv.vertex(matrix4f, -30.0f, 100.0f, -30.0f).texture(0.0f, 0.0f).color(i);
        lv.vertex(matrix4f, 30.0f, 100.0f, -30.0f).texture(1.0f, 0.0f).color(i);
        lv.vertex(matrix4f, 30.0f, 100.0f, 30.0f).texture(1.0f, 1.0f).color(i);
        lv.vertex(matrix4f, -30.0f, 100.0f, 30.0f).texture(0.0f, 1.0f).color(i);
    }

    private void renderMoon(int phase, float alpha, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        float g = 20.0f;
        int j = phase % 4;
        int k = phase / 4 % 2;
        float h = (float)(j + 0) / 4.0f;
        float l = (float)(k + 0) / 2.0f;
        float m = (float)(j + 1) / 4.0f;
        float n = (float)(k + 1) / 2.0f;
        float o = 100.0f;
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getCelestial(MOON_PHASES_TEXTURE));
        int p = ColorHelper.getWhite(alpha);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        lv.vertex(matrix4f, -20.0f, -100.0f, 20.0f).texture(m, n).color(p);
        lv.vertex(matrix4f, 20.0f, -100.0f, 20.0f).texture(h, n).color(p);
        lv.vertex(matrix4f, 20.0f, -100.0f, -20.0f).texture(h, l).color(p);
        lv.vertex(matrix4f, -20.0f, -100.0f, -20.0f).texture(m, l).color(p);
    }

    private void renderStars(Fog fog, float color, MatrixStack matrices) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(matrices.peek().getPositionMatrix());
        RenderSystem.setShaderColor(color, color, color, color);
        RenderSystem.setShaderFog(Fog.DUMMY);
        this.starBuffer.draw(RenderLayer.getStars());
        RenderSystem.setShaderFog(fog);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrix4fStack.popMatrix();
    }

    public void renderGlowingSky(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float angleRadians, int color) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        float g = MathHelper.sin(angleRadians) < 0.0f ? 180.0f : 0.0f;
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(g));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getSunriseSunset());
        float h = ColorHelper.getAlphaFloat(color);
        lv.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(color);
        int j = ColorHelper.zeroAlpha(color);
        int k = 16;
        for (int l = 0; l <= 16; ++l) {
            float m = (float)l * ((float)Math.PI * 2) / 16.0f;
            float n = MathHelper.sin(m);
            float o = MathHelper.cos(m);
            lv.vertex(matrix4f, n * 120.0f, o * 120.0f, -o * 40.0f * h).color(j);
        }
        matrices.pop();
    }

    private void tessellateEndSky(VertexConsumer vertexConsumer) {
        for (int i = 0; i < 6; ++i) {
            Matrix4f matrix4f = new Matrix4f();
            switch (i) {
                case 1: {
                    matrix4f.rotationX(1.5707964f);
                    break;
                }
                case 2: {
                    matrix4f.rotationX(-1.5707964f);
                    break;
                }
                case 3: {
                    matrix4f.rotationX((float)Math.PI);
                    break;
                }
                case 4: {
                    matrix4f.rotationZ(1.5707964f);
                    break;
                }
                case 5: {
                    matrix4f.rotationZ(-1.5707964f);
                }
            }
            vertexConsumer.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(-14145496);
            vertexConsumer.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(-14145496);
            vertexConsumer.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(-14145496);
            vertexConsumer.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(-14145496);
        }
    }

    public void renderEndSky() {
        this.endSkyBuffer.draw(RenderLayer.getEndSky());
    }

    @Override
    public void close() {
        this.starBuffer.close();
        this.skyBuffer.close();
        this.darkSkyBuffer.close();
        this.endSkyBuffer.close();
    }
}

