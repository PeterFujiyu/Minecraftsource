/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBufferTarget;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.GpuBuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeSupplier;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class RenderSystem {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
    private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator(1536);
    private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
    @Nullable
    private static Thread renderThread;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private static boolean isInInit;
    private static double lastDrawTime;
    private static final ShapeIndexBuffer sharedSequential;
    private static final ShapeIndexBuffer sharedSequentialQuad;
    private static final ShapeIndexBuffer sharedSequentialLines;
    private static Matrix4f projectionMatrix;
    private static Matrix4f savedProjectionMatrix;
    private static ProjectionType projectionType;
    private static ProjectionType savedProjectionType;
    private static final Matrix4fStack modelViewStack;
    private static Matrix4f textureMatrix;
    private static final int[] shaderTextures;
    private static final float[] shaderColor;
    private static float shaderGlintAlpha;
    private static Fog shaderFog;
    private static final Vector3f[] shaderLightDirections;
    private static float shaderGameTime;
    private static float shaderLineWidth;
    private static String apiDescription;
    @Nullable
    private static ShaderProgram shader;
    private static final AtomicLong pollEventsWaitStart;
    private static final AtomicBoolean pollingEvents;

    public static void initRenderThread() {
        if (renderThread != null) {
            throw new IllegalStateException("Could not initialize render thread");
        }
        renderThread = Thread.currentThread();
    }

    public static boolean isOnRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public static boolean isOnRenderThreadOrInit() {
        return isInInit || RenderSystem.isOnRenderThread();
    }

    public static void assertOnRenderThreadOrInit() {
        if (isInInit || RenderSystem.isOnRenderThread()) {
            return;
        }
        throw RenderSystem.constructThreadException();
    }

    public static void assertOnRenderThread() {
        if (!RenderSystem.isOnRenderThread()) {
            throw RenderSystem.constructThreadException();
        }
    }

    private static IllegalStateException constructThreadException() {
        return new IllegalStateException("Rendersystem called from wrong thread");
    }

    public static void recordRenderCall(RenderCall renderCall) {
        recordingQueue.add(renderCall);
    }

    private static void pollEvents() {
        pollEventsWaitStart.set(Util.getMeasuringTimeMs());
        pollingEvents.set(true);
        GLFW.glfwPollEvents();
        pollingEvents.set(false);
    }

    public static boolean isFrozenAtPollEvents() {
        return pollingEvents.get() && Util.getMeasuringTimeMs() - pollEventsWaitStart.get() > 200L;
    }

    public static void flipFrame(long window, @Nullable TracyFrameCapturer capturer) {
        RenderSystem.pollEvents();
        RenderSystem.replayQueue();
        Tessellator.getInstance().clear();
        GLFW.glfwSwapBuffers(window);
        if (capturer != null) {
            capturer.markFrame();
        }
        RenderSystem.pollEvents();
    }

    public static void replayQueue() {
        while (!recordingQueue.isEmpty()) {
            RenderCall lv = recordingQueue.poll();
            lv.execute();
        }
    }

    public static void limitDisplayFPS(int fps) {
        double d = lastDrawTime + 1.0 / (double)fps;
        double e = GLFW.glfwGetTime();
        while (e < d) {
            GLFW.glfwWaitEventsTimeout(d - e);
            e = GLFW.glfwGetTime();
        }
        lastDrawTime = e;
    }

    public static void disableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

    public static void enableScissor(int x, int y, int width, int height) {
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(x, y, width, height);
    }

    public static void disableScissor() {
        GlStateManager._disableScissorTest();
    }

    public static void depthFunc(int func) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._depthFunc(func);
    }

    public static void depthMask(boolean mask) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._depthMask(mask);
    }

    public static void enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableBlend();
    }

    public static void disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableBlend();
    }

    public static void blendFunc(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFunc(srcFactor.value, dstFactor.value);
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFunc(srcFactor, dstFactor);
    }

    public static void blendFuncSeparate(GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor, GlStateManager.SrcFactor srcAlpha, GlStateManager.DstFactor dstAlpha) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFuncSeparate(srcFactor.value, dstFactor.value, srcAlpha.value, dstAlpha.value);
    }

    public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
    }

    public static void blendEquation(int mode) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendEquation(mode);
    }

    public static void enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableCull();
    }

    public static void polygonMode(int face, int mode) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._polygonMode(face, mode);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enablePolygonOffset();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disablePolygonOffset();
    }

    public static void polygonOffset(float factor, float units) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._polygonOffset(factor, units);
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableColorLogicOp();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableColorLogicOp();
    }

    public static void logicOp(GlStateManager.LogicOp op) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._logicOp(op.value);
    }

    public static void activeTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._activeTexture(texture);
    }

    public static void texParameter(int target, int pname, int param) {
        GlStateManager._texParameter(target, pname, param);
    }

    public static void deleteTexture(int texture) {
        GlStateManager._deleteTexture(texture);
    }

    public static void bindTextureForSetup(int id) {
        RenderSystem.bindTexture(id);
    }

    public static void bindTexture(int texture) {
        GlStateManager._bindTexture(texture);
    }

    public static void viewport(int x, int y, int width, int height) {
        GlStateManager._viewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(red, green, blue, alpha);
    }

    public static void stencilFunc(int func, int ref, int mask) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._stencilFunc(func, ref, mask);
    }

    public static void stencilMask(int mask) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._stencilMask(mask);
    }

    public static void stencilOp(int sfail, int dpfail, int dppass) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._stencilOp(sfail, dpfail, dppass);
    }

    public static void clearDepth(double depth) {
        GlStateManager._clearDepth(depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        GlStateManager._clearColor(red, green, blue, alpha);
    }

    public static void clearStencil(int stencil) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._clearStencil(stencil);
    }

    public static void clear(int mask) {
        GlStateManager._clear(mask);
    }

    public static void setShaderFog(Fog arg) {
        RenderSystem.assertOnRenderThread();
        shaderFog = arg;
    }

    public static Fog getShaderFog() {
        RenderSystem.assertOnRenderThread();
        return shaderFog;
    }

    public static void setShaderGlintAlpha(double d) {
        RenderSystem.setShaderGlintAlpha((float)d);
    }

    public static void setShaderGlintAlpha(float f) {
        RenderSystem.assertOnRenderThread();
        shaderGlintAlpha = f;
    }

    public static float getShaderGlintAlpha() {
        RenderSystem.assertOnRenderThread();
        return shaderGlintAlpha;
    }

    public static void setShaderLights(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.shaderLightDirections[0] = vector3f;
        RenderSystem.shaderLightDirections[1] = vector3f2;
    }

    public static void setupShaderLights(ShaderProgram shader) {
        RenderSystem.assertOnRenderThread();
        if (shader.light0Direction != null) {
            shader.light0Direction.set(shaderLightDirections[0]);
        }
        if (shader.light1Direction != null) {
            shader.light1Direction.set(shaderLightDirections[1]);
        }
    }

    public static void setShaderColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.shaderColor[0] = red;
        RenderSystem.shaderColor[1] = green;
        RenderSystem.shaderColor[2] = blue;
        RenderSystem.shaderColor[3] = alpha;
    }

    public static float[] getShaderColor() {
        RenderSystem.assertOnRenderThread();
        return shaderColor;
    }

    public static void drawElements(int mode, int count, int type) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._drawElements(mode, count, type, 0L);
    }

    public static void lineWidth(float width) {
        RenderSystem.assertOnRenderThread();
        shaderLineWidth = width;
    }

    public static float getShaderLineWidth() {
        RenderSystem.assertOnRenderThread();
        return shaderLineWidth;
    }

    public static void pixelStore(int pname, int param) {
        GlStateManager._pixelStore(pname, param);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._readPixels(x, y, width, height, format, type, pixels);
    }

    public static void getString(int name, Consumer<String> consumer) {
        RenderSystem.assertOnRenderThread();
        consumer.accept(GlStateManager._getString(name));
    }

    public static String getBackendDescription() {
        return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
    }

    public static String getApiDescription() {
        return apiDescription;
    }

    public static TimeSupplier.Nanoseconds initBackendSystem() {
        return GLX._initGlfw()::getAsLong;
    }

    public static void initRenderer(int debugVerbosity, boolean debugSync) {
        GLX._init(debugVerbosity, debugSync);
        apiDescription = GLX.getOpenGLVersionString();
    }

    public static void setErrorCallback(GLFWErrorCallbackI callback) {
        GLX._setGlfwErrorCallback(callback);
    }

    public static void renderCrosshair(int size) {
        RenderSystem.assertOnRenderThread();
        GLX._renderCrosshair(size, true, true, true);
    }

    public static String getCapsString() {
        RenderSystem.assertOnRenderThread();
        return "Using framebuffer using OpenGL 3.2";
    }

    public static void setupDefaultState(int x, int y, int width, int height) {
        GlStateManager._clearDepth(1.0);
        GlStateManager._enableDepthTest();
        GlStateManager._depthFunc(515);
        projectionMatrix.identity();
        savedProjectionMatrix.identity();
        modelViewStack.clear();
        textureMatrix.identity();
        GlStateManager._viewport(x, y, width, height);
    }

    public static int maxSupportedTextureSize() {
        if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
            RenderSystem.assertOnRenderThreadOrInit();
            int i = GlStateManager._getInteger(GL11.GL_MAX_TEXTURE_SIZE);
            for (int j = Math.max(32768, i); j >= 1024; j >>= 1) {
                GlStateManager._texImage2D(GlConst.GL_PROXY_TEXTURE_2D, 0, GlConst.GL_RGBA, j, j, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null);
                int k = GlStateManager._getTexLevelParameter(32868, 0, 4096);
                if (k == 0) continue;
                MAX_SUPPORTED_TEXTURE_SIZE = j;
                return j;
            }
            MAX_SUPPORTED_TEXTURE_SIZE = Math.max(i, 1024);
            LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)MAX_SUPPORTED_TEXTURE_SIZE);
        }
        return MAX_SUPPORTED_TEXTURE_SIZE;
    }

    public static void glBindBuffer(int target, int buffer) {
        GlStateManager._glBindBuffer(target, buffer);
    }

    public static void glBindVertexArray(int array) {
        GlStateManager._glBindVertexArray(array);
    }

    public static void glBufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBufferData(target, data, usage);
    }

    public static void glDeleteBuffers(int buffer) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glDeleteBuffers(buffer);
    }

    public static void glDeleteVertexArrays(int array) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glDeleteVertexArrays(array);
    }

    public static void glUniform1i(int location, int value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform1i(location, value);
    }

    public static void glUniform1(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform1(location, value);
    }

    public static void glUniform2(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform2(location, value);
    }

    public static void glUniform3(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform3(location, value);
    }

    public static void glUniform4(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform4(location, value);
    }

    public static void glUniform1(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform1(location, value);
    }

    public static void glUniform2(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform2(location, value);
    }

    public static void glUniform3(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform3(location, value);
    }

    public static void glUniform4(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform4(location, value);
    }

    public static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniformMatrix2(location, transpose, value);
    }

    public static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniformMatrix3(location, transpose, value);
    }

    public static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniformMatrix4(location, transpose, value);
    }

    public static void setupOverlayColor(int texture, int size) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, texture);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, 0);
    }

    public static void setupLevelDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderLights(vector3f, vector3f2);
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.setupGuiFlatDiffuseLighting(vector3f, vector3f2);
    }

    public static void setupGui3DDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.setupGui3DDiffuseLighting(vector3f, vector3f2);
    }

    public static void beginInitialization() {
        isInInit = true;
    }

    public static void finishInitialization() {
        isInInit = false;
        if (!recordingQueue.isEmpty()) {
            RenderSystem.replayQueue();
        }
        if (!recordingQueue.isEmpty()) {
            throw new IllegalStateException("Recorded to render queue during initialization");
        }
    }

    public static Tessellator renderThreadTesselator() {
        RenderSystem.assertOnRenderThread();
        return RENDER_THREAD_TESSELATOR;
    }

    public static void defaultBlendFunc() {
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }

    @Nullable
    public static ShaderProgram setShader(ShaderProgramKey arg) {
        ShaderProgram lv;
        RenderSystem.assertOnRenderThread();
        shader = lv = MinecraftClient.getInstance().getShaderLoader().getOrCreateProgram(arg);
        return lv;
    }

    public static void setShader(ShaderProgram arg) {
        RenderSystem.assertOnRenderThread();
        shader = arg;
    }

    public static void clearShader() {
        RenderSystem.assertOnRenderThread();
        shader = null;
    }

    @Nullable
    public static ShaderProgram getShader() {
        RenderSystem.assertOnRenderThread();
        return shader;
    }

    public static void setShaderTexture(int texture, Identifier id) {
        RenderSystem.assertOnRenderThread();
        if (texture >= 0 && texture < shaderTextures.length) {
            TextureManager lv = MinecraftClient.getInstance().getTextureManager();
            AbstractTexture lv2 = lv.getTexture(id);
            RenderSystem.shaderTextures[texture] = lv2.getGlId();
        }
    }

    public static void setShaderTexture(int texture, int glId) {
        RenderSystem.assertOnRenderThread();
        if (texture >= 0 && texture < shaderTextures.length) {
            RenderSystem.shaderTextures[texture] = glId;
        }
    }

    public static int getShaderTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        if (texture >= 0 && texture < shaderTextures.length) {
            return shaderTextures[texture];
        }
        return 0;
    }

    public static void setProjectionMatrix(Matrix4f projectionMatrix, ProjectionType projectionType) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.projectionMatrix = new Matrix4f(projectionMatrix);
        RenderSystem.projectionType = projectionType;
    }

    public static void setTextureMatrix(Matrix4f textureMatrix) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.textureMatrix = new Matrix4f(textureMatrix);
    }

    public static void resetTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        textureMatrix.identity();
    }

    public static void backupProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        savedProjectionMatrix = projectionMatrix;
        savedProjectionType = projectionType;
    }

    public static void restoreProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        projectionMatrix = savedProjectionMatrix;
        projectionType = savedProjectionType;
    }

    public static Matrix4f getProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        return projectionMatrix;
    }

    public static Matrix4f getModelViewMatrix() {
        RenderSystem.assertOnRenderThread();
        return modelViewStack;
    }

    public static Matrix4fStack getModelViewStack() {
        RenderSystem.assertOnRenderThread();
        return modelViewStack;
    }

    public static Matrix4f getTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        return textureMatrix;
    }

    public static ShapeIndexBuffer getSequentialBuffer(VertexFormat.DrawMode drawMode) {
        RenderSystem.assertOnRenderThread();
        return switch (drawMode) {
            case VertexFormat.DrawMode.QUADS -> sharedSequentialQuad;
            case VertexFormat.DrawMode.LINES -> sharedSequentialLines;
            default -> sharedSequential;
        };
    }

    public static void setShaderGameTime(long time, float tickDelta) {
        RenderSystem.assertOnRenderThread();
        shaderGameTime = ((float)(time % 24000L) + tickDelta) / 24000.0f;
    }

    public static float getShaderGameTime() {
        RenderSystem.assertOnRenderThread();
        return shaderGameTime;
    }

    public static ProjectionType getProjectionType() {
        RenderSystem.assertOnRenderThread();
        return projectionType;
    }

    static {
        MAX_SUPPORTED_TEXTURE_SIZE = -1;
        lastDrawTime = Double.MIN_VALUE;
        sharedSequential = new ShapeIndexBuffer(1, 1, java.util.function.IntConsumer::accept);
        sharedSequentialQuad = new ShapeIndexBuffer(4, 6, (indexConsumer, firstVertexIndex) -> {
            indexConsumer.accept(firstVertexIndex + 0);
            indexConsumer.accept(firstVertexIndex + 1);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 3);
            indexConsumer.accept(firstVertexIndex + 0);
        });
        sharedSequentialLines = new ShapeIndexBuffer(4, 6, (indexConsumer, firstVertexIndex) -> {
            indexConsumer.accept(firstVertexIndex + 0);
            indexConsumer.accept(firstVertexIndex + 1);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 3);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 1);
        });
        projectionMatrix = new Matrix4f();
        savedProjectionMatrix = new Matrix4f();
        projectionType = ProjectionType.PERSPECTIVE;
        savedProjectionType = ProjectionType.PERSPECTIVE;
        modelViewStack = new Matrix4fStack(16);
        textureMatrix = new Matrix4f();
        shaderTextures = new int[12];
        shaderColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        shaderGlintAlpha = 1.0f;
        shaderFog = Fog.DUMMY;
        shaderLightDirections = new Vector3f[2];
        shaderLineWidth = 1.0f;
        apiDescription = "Unknown";
        pollEventsWaitStart = new AtomicLong();
        pollingEvents = new AtomicBoolean(false);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ShapeIndexBuffer {
        private final int vertexCountInShape;
        private final int vertexCountInTriangulated;
        private final Triangulator triangulator;
        @Nullable
        private GpuBuffer buffer;
        private VertexFormat.IndexType indexType = VertexFormat.IndexType.SHORT;
        private int size;

        ShapeIndexBuffer(int vertexCountInShape, int vertexCountInTriangulated, Triangulator triangulator) {
            this.vertexCountInShape = vertexCountInShape;
            this.vertexCountInTriangulated = vertexCountInTriangulated;
            this.triangulator = triangulator;
        }

        public boolean isLargeEnough(int requiredSize) {
            return requiredSize <= this.size;
        }

        public void bindAndGrow(int requiredSize) {
            if (this.buffer == null) {
                this.buffer = new GpuBuffer(GlBufferTarget.INDICES, GlUsage.DYNAMIC_WRITE, 0);
            }
            this.buffer.bind();
            this.grow(requiredSize);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void grow(int requiredSize) {
            if (this.isLargeEnough(requiredSize)) {
                return;
            }
            requiredSize = MathHelper.roundUpToMultiple(requiredSize * 2, this.vertexCountInTriangulated);
            LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", (Object)this.size, (Object)requiredSize);
            int j = requiredSize / this.vertexCountInTriangulated;
            int k = j * this.vertexCountInShape;
            VertexFormat.IndexType lv = VertexFormat.IndexType.smallestFor(k);
            int l = MathHelper.roundUpToMultiple(requiredSize * lv.size, 4);
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(l);
            try {
                this.indexType = lv;
                IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);
                for (int m = 0; m < requiredSize; m += this.vertexCountInTriangulated) {
                    this.triangulator.accept(intConsumer, m * this.vertexCountInShape / this.vertexCountInTriangulated);
                }
                byteBuffer.flip();
                this.buffer.resize(l);
                this.buffer.copyFrom(byteBuffer, 0);
            } finally {
                MemoryUtil.memFree(byteBuffer);
            }
            this.size = requiredSize;
        }

        private IntConsumer getIndexConsumer(ByteBuffer indexBuffer) {
            switch (this.indexType) {
                case SHORT: {
                    return index -> indexBuffer.putShort((short)index);
                }
            }
            return indexBuffer::putInt;
        }

        public VertexFormat.IndexType getIndexType() {
            return this.indexType;
        }

        @Environment(value=EnvType.CLIENT)
        static interface Triangulator {
            public void accept(IntConsumer var1, int var2);
        }
    }
}

