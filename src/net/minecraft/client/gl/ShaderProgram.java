/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgramDefinition;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class ShaderProgram
implements AutoCloseable {
    private static final Uniform DEFAULT_UNIFORM = new Uniform();
    private static final int field_53837 = -1;
    private final List<ShaderProgramDefinition.Sampler> samplers = new ArrayList<ShaderProgramDefinition.Sampler>();
    private final Object2IntMap<String> samplerTextures = new Object2IntArrayMap<String>();
    private final IntList samplerLocations = new IntArrayList();
    private final List<GlUniform> uniforms = new ArrayList<GlUniform>();
    private final Map<String, GlUniform> uniformsByName = new HashMap<String, GlUniform>();
    private final Map<String, ShaderProgramDefinition.Uniform> uniformDefinitionsByName = new HashMap<String, ShaderProgramDefinition.Uniform>();
    private final int glRef;
    @Nullable
    public GlUniform modelViewMat;
    @Nullable
    public GlUniform projectionMat;
    @Nullable
    public GlUniform textureMat;
    @Nullable
    public GlUniform screenSize;
    @Nullable
    public GlUniform colorModulator;
    @Nullable
    public GlUniform light0Direction;
    @Nullable
    public GlUniform light1Direction;
    @Nullable
    public GlUniform glintAlpha;
    @Nullable
    public GlUniform fogStart;
    @Nullable
    public GlUniform fogEnd;
    @Nullable
    public GlUniform fogColor;
    @Nullable
    public GlUniform fogShape;
    @Nullable
    public GlUniform lineWidth;
    @Nullable
    public GlUniform gameTime;
    @Nullable
    public GlUniform modelOffset;

    private ShaderProgram(int glRef) {
        this.glRef = glRef;
        this.samplerTextures.defaultReturnValue(-1);
    }

    public static ShaderProgram create(CompiledShader vertexShader, CompiledShader fragmentShader, VertexFormat format) throws ShaderLoader.LoadException {
        int i = GlStateManager.glCreateProgram();
        if (i <= 0) {
            throw new ShaderLoader.LoadException("Could not create shader program (returned program ID " + i + ")");
        }
        format.bindAttributes(i);
        GlStateManager.glAttachShader(i, vertexShader.getHandle());
        GlStateManager.glAttachShader(i, fragmentShader.getHandle());
        GlStateManager.glLinkProgram(i);
        int j = GlStateManager.glGetProgrami(i, GlConst.GL_LINK_STATUS);
        if (j == 0) {
            String string = GlStateManager.glGetProgramInfoLog(i, 32768);
            throw new ShaderLoader.LoadException("Error encountered when linking program containing VS " + String.valueOf(vertexShader.getId()) + " and FS " + String.valueOf(fragmentShader.getId()) + ". Log output: " + string);
        }
        return new ShaderProgram(i);
    }

    public void set(List<ShaderProgramDefinition.Uniform> uniforms, List<ShaderProgramDefinition.Sampler> samplers) {
        RenderSystem.assertOnRenderThread();
        for (ShaderProgramDefinition.Uniform lv : uniforms) {
            String string = lv.name();
            int i = GlUniform.getUniformLocation(this.glRef, string);
            if (i == -1) continue;
            GlUniform lv2 = this.createGlUniform(lv);
            lv2.setLocation(i);
            this.uniforms.add(lv2);
            this.uniformsByName.put(string, lv2);
            this.uniformDefinitionsByName.put(string, lv);
        }
        for (ShaderProgramDefinition.Sampler lv3 : samplers) {
            int j = GlUniform.getUniformLocation(this.glRef, lv3.name());
            if (j == -1) continue;
            this.samplers.add(lv3);
            this.samplerLocations.add(j);
        }
        this.modelViewMat = this.getUniform("ModelViewMat");
        this.projectionMat = this.getUniform("ProjMat");
        this.textureMat = this.getUniform("TextureMat");
        this.screenSize = this.getUniform("ScreenSize");
        this.colorModulator = this.getUniform("ColorModulator");
        this.light0Direction = this.getUniform("Light0_Direction");
        this.light1Direction = this.getUniform("Light1_Direction");
        this.glintAlpha = this.getUniform("GlintAlpha");
        this.fogStart = this.getUniform("FogStart");
        this.fogEnd = this.getUniform("FogEnd");
        this.fogColor = this.getUniform("FogColor");
        this.fogShape = this.getUniform("FogShape");
        this.lineWidth = this.getUniform("LineWidth");
        this.gameTime = this.getUniform("GameTime");
        this.modelOffset = this.getUniform("ModelOffset");
    }

    @Override
    public void close() {
        this.uniforms.forEach(GlUniform::close);
        GlStateManager.glDeleteProgram(this.glRef);
    }

    public void unbind() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram(0);
        int i = GlStateManager._getActiveTexture();
        for (int j = 0; j < this.samplerLocations.size(); ++j) {
            ShaderProgramDefinition.Sampler lv = this.samplers.get(j);
            if (this.samplerTextures.containsKey(lv.name())) continue;
            GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + j);
            GlStateManager._bindTexture(0);
        }
        GlStateManager._activeTexture(i);
    }

    public void bind() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram(this.glRef);
        int i = GlStateManager._getActiveTexture();
        for (int j = 0; j < this.samplerLocations.size(); ++j) {
            String string = this.samplers.get(j).name();
            int k = this.samplerTextures.getInt(string);
            if (k == -1) continue;
            int l = this.samplerLocations.getInt(j);
            GlUniform.uniform1(l, j);
            RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + j);
            RenderSystem.bindTexture(k);
        }
        GlStateManager._activeTexture(i);
        for (GlUniform lv : this.uniforms) {
            lv.upload();
        }
    }

    @Nullable
    public GlUniform getUniform(String name) {
        RenderSystem.assertOnRenderThread();
        return this.uniformsByName.get(name);
    }

    @Nullable
    public ShaderProgramDefinition.Uniform getUniformDefinition(String name) {
        return this.uniformDefinitionsByName.get(name);
    }

    public Uniform getUniformOrDefault(String name) {
        GlUniform lv = this.getUniform(name);
        return lv == null ? DEFAULT_UNIFORM : lv;
    }

    public void addSamplerTexture(String name, int texture) {
        this.samplerTextures.put(name, texture);
    }

    private GlUniform createGlUniform(ShaderProgramDefinition.Uniform uniform) {
        int i = GlUniform.getTypeIndex(uniform.type());
        int j = uniform.count();
        int k = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
        GlUniform lv = new GlUniform(uniform.name(), i + k, j);
        lv.set(uniform);
        return lv;
    }

    public void initializeUniforms(VertexFormat.DrawMode drawMode, Matrix4f viewMatrix, Matrix4f projectionMatrix, Window window) {
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            this.addSamplerTexture("Sampler" + i, j);
        }
        if (this.modelViewMat != null) {
            this.modelViewMat.set(viewMatrix);
        }
        if (this.projectionMat != null) {
            this.projectionMat.set(projectionMatrix);
        }
        if (this.colorModulator != null) {
            this.colorModulator.set(RenderSystem.getShaderColor());
        }
        if (this.glintAlpha != null) {
            this.glintAlpha.set(RenderSystem.getShaderGlintAlpha());
        }
        Fog lv = RenderSystem.getShaderFog();
        if (this.fogStart != null) {
            this.fogStart.set(lv.start());
        }
        if (this.fogEnd != null) {
            this.fogEnd.set(lv.end());
        }
        if (this.fogColor != null) {
            this.fogColor.setAndFlip(lv.red(), lv.green(), lv.blue(), lv.alpha());
        }
        if (this.fogShape != null) {
            this.fogShape.set(lv.shape().getId());
        }
        if (this.textureMat != null) {
            this.textureMat.set(RenderSystem.getTextureMatrix());
        }
        if (this.gameTime != null) {
            this.gameTime.set(RenderSystem.getShaderGameTime());
        }
        if (this.screenSize != null) {
            this.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
        }
        if (this.lineWidth != null && (drawMode == VertexFormat.DrawMode.LINES || drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            this.lineWidth.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(this);
    }

    @VisibleForTesting
    public void addUniform(GlUniform uniform) {
        this.uniforms.add(uniform);
        this.uniformsByName.put(uniform.getName(), uniform);
    }

    @VisibleForTesting
    public int getGlRef() {
        return this.glRef;
    }
}

