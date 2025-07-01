/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramDefinition;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class PostEffectPass {
    private final String id;
    private final ShaderProgram program;
    private final Identifier outputTargetId;
    private final List<PostEffectPipeline.Uniform> uniforms;
    private final List<Sampler> samplers = new ArrayList<Sampler>();

    public PostEffectPass(String id, ShaderProgram program, Identifier outputTargetId, List<PostEffectPipeline.Uniform> uniforms) {
        this.id = id;
        this.program = program;
        this.outputTargetId = outputTargetId;
        this.uniforms = uniforms;
    }

    public void addSampler(Sampler sampler) {
        this.samplers.add(sampler);
    }

    public void render(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, Matrix4f projectionMatrix) {
        RenderPass lv = builder.createPass(this.id);
        for (Sampler lv2 : this.samplers) {
            lv2.preRender(lv, handles);
        }
        Handle lv3 = handles.computeIfPresent(this.outputTargetId, (id, handle) -> lv.transfer(handle));
        if (lv3 == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
        }
        lv.setRenderer(() -> {
            Framebuffer lv = (Framebuffer)lv3.get();
            RenderSystem.viewport(0, 0, lv.textureWidth, lv.textureHeight);
            for (Sampler lv2 : this.samplers) {
                lv2.bind(this.program, handles);
            }
            this.program.getUniformOrDefault("OutSize").set((float)lv.textureWidth, (float)lv.textureHeight);
            for (PostEffectPipeline.Uniform lv3 : this.uniforms) {
                GlUniform lv4 = this.program.getUniform(lv3.name());
                if (lv4 == null) continue;
                lv4.set(lv3.values(), lv3.values().size());
            }
            lv.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            lv.clear();
            lv.beginWrite(false);
            RenderSystem.depthFunc(519);
            RenderSystem.setShader(this.program);
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(projectionMatrix, ProjectionType.ORTHOGRAPHIC);
            BufferBuilder lv5 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            lv5.vertex(0.0f, 0.0f, 500.0f);
            lv5.vertex(lv.textureWidth, 0.0f, 500.0f);
            lv5.vertex(lv.textureWidth, lv.textureHeight, 500.0f);
            lv5.vertex(0.0f, lv.textureHeight, 500.0f);
            BufferRenderer.drawWithGlobalProgram(lv5.end());
            RenderSystem.depthFunc(515);
            RenderSystem.restoreProjectionMatrix();
            lv.endWrite();
            for (Sampler lv6 : this.samplers) {
                lv6.postRender(handles);
            }
            this.setUniforms();
        });
    }

    private void setUniforms() {
        for (PostEffectPipeline.Uniform lv : this.uniforms) {
            String string = lv.name();
            GlUniform lv2 = this.program.getUniform(string);
            ShaderProgramDefinition.Uniform lv3 = this.program.getUniformDefinition(string);
            if (lv2 == null || lv3 == null || lv.values().equals(lv3.values())) continue;
            lv2.set(lv3);
        }
    }

    public ShaderProgram getProgram() {
        return this.program;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Sampler {
        public void preRender(RenderPass var1, Map<Identifier, Handle<Framebuffer>> var2);

        public void bind(ShaderProgram var1, Map<Identifier, Handle<Framebuffer>> var2);

        default public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TargetSampler(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements Sampler
    {
        private Handle<Framebuffer> getTarget(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            Handle<Framebuffer> lv = internalTargets.get(this.targetId);
            if (lv == null) {
                throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
            }
            return lv;
        }

        @Override
        public void preRender(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
            pass.dependsOn(this.getTarget(internalTargets));
        }

        @Override
        public void bind(ShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
            Handle<Framebuffer> lv = this.getTarget(internalTargets);
            Framebuffer lv2 = lv.get();
            lv2.setTexFilter(this.bilinear ? GlConst.GL_LINEAR : GlConst.GL_NEAREST);
            program.addSamplerTexture(this.samplerName + "Sampler", this.depthBuffer ? lv2.getDepthAttachment() : lv2.getColorAttachment());
            program.getUniformOrDefault(this.samplerName + "Size").set((float)lv2.textureWidth, (float)lv2.textureHeight);
        }

        @Override
        public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            if (this.bilinear) {
                this.getTarget(internalTargets).get().setTexFilter(GlConst.GL_NEAREST);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TextureSampler(String samplerName, AbstractTexture texture, int width, int height) implements Sampler
    {
        @Override
        public void preRender(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        }

        @Override
        public void bind(ShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
            program.addSamplerTexture(this.samplerName + "Sampler", this.texture.getGlId());
            program.getUniformOrDefault(this.samplerName + "Size").set((float)this.width, (float)this.height);
        }
    }
}

