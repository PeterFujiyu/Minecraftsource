/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    public static final int MAX_LIGHT_COORDINATE = 0xF000F0;
    public static final int MAX_SKY_LIGHT_COORDINATE = 0xF00000;
    public static final int MAX_BLOCK_LIGHT_COORDINATE = 240;
    private static final int field_53098 = 16;
    private final SimpleFramebuffer lightmapFramebuffer;
    private boolean dirty;
    private float flickerIntensity;
    private final GameRenderer renderer;
    private final MinecraftClient client;

    public LightmapTextureManager(GameRenderer renderer, MinecraftClient client) {
        this.renderer = renderer;
        this.client = client;
        this.lightmapFramebuffer = new SimpleFramebuffer(16, 16, false);
        this.lightmapFramebuffer.setTexFilter(GlConst.GL_LINEAR);
        this.lightmapFramebuffer.setClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.lightmapFramebuffer.clear();
    }

    @Override
    public void close() {
        this.lightmapFramebuffer.delete();
    }

    public void tick() {
        this.flickerIntensity += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.flickerIntensity *= 0.9f;
        this.dirty = true;
    }

    public void disable() {
        RenderSystem.setShaderTexture(2, 0);
    }

    public void enable() {
        RenderSystem.setShaderTexture(2, this.lightmapFramebuffer.getColorAttachment());
    }

    private float getDarknessFactor(float delta) {
        StatusEffectInstance lv = this.client.player.getStatusEffect(StatusEffects.DARKNESS);
        if (lv != null) {
            return lv.getFadeFactor(this.client.player, delta);
        }
        return 0.0f;
    }

    private float getDarkness(LivingEntity entity, float factor, float delta) {
        float h = 0.45f * factor;
        return Math.max(0.0f, MathHelper.cos(((float)entity.age - delta) * (float)Math.PI * 0.025f) * h);
    }

    public void update(float delta) {
        if (!this.dirty) {
            return;
        }
        this.dirty = false;
        Profiler lv = Profilers.get();
        lv.push("lightTex");
        ClientWorld lv2 = this.client.world;
        if (lv2 == null) {
            return;
        }
        float g = lv2.getSkyBrightness(1.0f);
        float h = lv2.getLightningTicksLeft() > 0 ? 1.0f : g * 0.95f + 0.05f;
        float i = this.client.options.getDarknessEffectScale().getValue().floatValue();
        float j = this.getDarknessFactor(delta) * i;
        float k = this.getDarkness(this.client.player, j, delta) * i;
        float l = this.client.player.getUnderwaterVisibility();
        float m = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength(this.client.player, delta) : (l > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? l : 0.0f);
        Vector3f vector3f = new Vector3f(g, g, 1.0f).lerp(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float n = this.flickerIntensity + 1.5f;
        float o = lv2.getDimension().ambientLight();
        boolean bl = lv2.getDimensionEffects().shouldBrightenLighting();
        float p = this.client.options.getGamma().getValue().floatValue();
        ShaderProgram lv3 = Objects.requireNonNull(RenderSystem.setShader(ShaderProgramKeys.LIGHTMAP), "Lightmap shader not loaded");
        lv3.getUniformOrDefault("AmbientLightFactor").set(o);
        lv3.getUniformOrDefault("SkyFactor").set(h);
        lv3.getUniformOrDefault("BlockFactor").set(n);
        lv3.getUniformOrDefault("UseBrightLightmap").set(bl ? 1 : 0);
        lv3.getUniformOrDefault("SkyLightColor").set(vector3f);
        lv3.getUniformOrDefault("NightVisionFactor").set(m);
        lv3.getUniformOrDefault("DarknessScale").set(k);
        lv3.getUniformOrDefault("DarkenWorldFactor").set(this.renderer.getSkyDarkness(delta));
        lv3.getUniformOrDefault("BrightnessFactor").set(Math.max(0.0f, p - j));
        this.lightmapFramebuffer.beginWrite(true);
        BufferBuilder lv4 = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
        lv4.vertex(0.0f, 0.0f, 0.0f);
        lv4.vertex(1.0f, 0.0f, 0.0f);
        lv4.vertex(1.0f, 1.0f, 0.0f);
        lv4.vertex(0.0f, 1.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram(lv4.end());
        this.lightmapFramebuffer.endWrite();
        lv.pop();
    }

    public static float getBrightness(DimensionType type, int lightLevel) {
        return LightmapTextureManager.getBrightness(type.ambientLight(), lightLevel);
    }

    public static float getBrightness(float ambientLight, int lightLevel) {
        float g = (float)lightLevel / 15.0f;
        float h = g / (4.0f - 3.0f * g);
        return MathHelper.lerp(ambientLight, h, 1.0f);
    }

    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    public static int getBlockLightCoordinates(int light) {
        return light >>> 4 & 0xF;
    }

    public static int getSkyLightCoordinates(int light) {
        return light >>> 20 & 0xF;
    }

    public static int applyEmission(int light, int lightEmission) {
        if (lightEmission == 0) {
            return light;
        }
        int k = Math.max(LightmapTextureManager.getSkyLightCoordinates(light), lightEmission);
        int l = Math.max(LightmapTextureManager.getBlockLightCoordinates(light), lightEmission);
        return LightmapTextureManager.pack(l, k);
    }
}

