/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBufferTarget;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.GpuBuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.BufferAllocator;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class VertexBuffer
implements AutoCloseable {
    private final GlUsage usage;
    private final GpuBuffer vertexBuffer;
    @Nullable
    private GpuBuffer indexBuffer = null;
    private int vertexArrayId;
    @Nullable
    private VertexFormat vertexFormat;
    @Nullable
    private RenderSystem.ShapeIndexBuffer sharedSequentialIndexBuffer;
    private VertexFormat.IndexType indexType;
    private int indexCount;
    private VertexFormat.DrawMode drawMode;

    public VertexBuffer(GlUsage usage) {
        this.usage = usage;
        RenderSystem.assertOnRenderThread();
        this.vertexBuffer = new GpuBuffer(GlBufferTarget.VERTICES, usage, 0);
        this.vertexArrayId = GlStateManager._glGenVertexArrays();
    }

    public static VertexBuffer createAndUpload(VertexFormat.DrawMode drawMode, VertexFormat format, Consumer<VertexConsumer> callback) {
        BufferBuilder lv = Tessellator.getInstance().begin(drawMode, format);
        callback.accept(lv);
        VertexBuffer lv2 = new VertexBuffer(GlUsage.STATIC_WRITE);
        lv2.bind();
        lv2.upload(lv.end());
        VertexBuffer.unbind();
        return lv2;
    }

    public void upload(BuiltBuffer data) {
        try (BuiltBuffer builtBuffer = data;){
            if (this.isClosed()) {
                return;
            }
            RenderSystem.assertOnRenderThread();
            BuiltBuffer.DrawParameters lv = data.getDrawParameters();
            this.vertexFormat = this.uploadVertexBuffer(lv, data.getBuffer());
            this.sharedSequentialIndexBuffer = this.uploadIndexBuffer(lv, data.getSortedBuffer());
            this.indexCount = lv.indexCount();
            this.indexType = lv.indexType();
            this.drawMode = lv.mode();
        }
    }

    public void uploadIndexBuffer(BufferAllocator.CloseableBuffer buf) {
        try (BufferAllocator.CloseableBuffer closeableBuffer = buf;){
            if (this.isClosed()) {
                return;
            }
            RenderSystem.assertOnRenderThread();
            if (this.indexBuffer != null) {
                this.indexBuffer.close();
            }
            this.indexBuffer = new GpuBuffer(GlBufferTarget.INDICES, this.usage, buf.getBuffer());
            this.sharedSequentialIndexBuffer = null;
        }
    }

    private VertexFormat uploadVertexBuffer(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer vertexBuffer) {
        boolean bl = false;
        if (!parameters.format().equals(this.vertexFormat)) {
            if (this.vertexFormat != null) {
                this.vertexFormat.clearState();
            }
            this.vertexBuffer.bind();
            parameters.format().setupState();
            bl = true;
        }
        if (vertexBuffer != null) {
            if (!bl) {
                this.vertexBuffer.bind();
            }
            this.vertexBuffer.resize(vertexBuffer.remaining());
            this.vertexBuffer.copyFrom(vertexBuffer, 0);
        }
        return parameters.format();
    }

    @Nullable
    private RenderSystem.ShapeIndexBuffer uploadIndexBuffer(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer buf) {
        if (buf == null) {
            RenderSystem.ShapeIndexBuffer lv = RenderSystem.getSequentialBuffer(parameters.mode());
            if (lv != this.sharedSequentialIndexBuffer || !lv.isLargeEnough(parameters.indexCount())) {
                lv.bindAndGrow(parameters.indexCount());
            }
            return lv;
        }
        if (this.indexBuffer != null) {
            this.indexBuffer.close();
        }
        this.indexBuffer = new GpuBuffer(GlBufferTarget.INDICES, this.usage, buf);
        return null;
    }

    public void bind() {
        BufferRenderer.resetCurrentVertexBuffer();
        GlStateManager._glBindVertexArray(this.vertexArrayId);
    }

    public static void unbind() {
        BufferRenderer.resetCurrentVertexBuffer();
        GlStateManager._glBindVertexArray(0);
    }

    public void draw() {
        RenderSystem.drawElements(this.drawMode.glMode, this.indexCount, this.getIndexType().glType);
    }

    private VertexFormat.IndexType getIndexType() {
        RenderSystem.ShapeIndexBuffer lv = this.sharedSequentialIndexBuffer;
        return lv != null ? lv.getIndexType() : this.indexType;
    }

    public void draw(Matrix4f viewMatrix, Matrix4f projectionMatrix, @Nullable ShaderProgram program) {
        if (program == null) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        program.initializeUniforms(this.drawMode, viewMatrix, projectionMatrix, MinecraftClient.getInstance().getWindow());
        program.bind();
        this.draw();
        program.unbind();
    }

    public void draw(RenderLayer layer) {
        layer.startDrawing();
        this.bind();
        this.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
        layer.endDrawing();
    }

    @Override
    public void close() {
        this.vertexBuffer.close();
        if (this.indexBuffer != null) {
            this.indexBuffer.close();
            this.indexBuffer = null;
        }
        if (this.vertexArrayId >= 0) {
            RenderSystem.glDeleteVertexArrays(this.vertexArrayId);
            this.vertexArrayId = -1;
        }
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public boolean isClosed() {
        return this.vertexArrayId == -1;
    }
}

