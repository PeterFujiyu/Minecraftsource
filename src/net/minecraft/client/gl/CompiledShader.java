/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompiledShader
implements AutoCloseable {
    private static final int CLOSED = -1;
    private final Identifier id;
    private int handle;

    private CompiledShader(int handle, Identifier path) {
        this.id = path;
        this.handle = handle;
    }

    public static CompiledShader compile(Identifier id, Type type, String source) throws ShaderLoader.LoadException {
        RenderSystem.assertOnRenderThread();
        int i = GlStateManager.glCreateShader(type.getGlType());
        GlStateManager.glShaderSource(i, source);
        GlStateManager.glCompileShader(i);
        if (GlStateManager.glGetShaderi(i, GlConst.GL_COMPILE_STATUS) == 0) {
            String string2 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
            throw new ShaderLoader.LoadException("Couldn't compile " + type.getName() + " shader (" + String.valueOf(id) + ") : " + string2);
        }
        return new CompiledShader(i, id);
    }

    @Override
    public void close() {
        if (this.handle == -1) {
            throw new IllegalStateException("Already closed");
        }
        RenderSystem.assertOnRenderThread();
        GlStateManager.glDeleteShader(this.handle);
        this.handle = -1;
    }

    public Identifier getId() {
        return this.id;
    }

    public int getHandle() {
        return this.handle;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        VERTEX("vertex", ".vsh", 35633),
        FRAGMENT("fragment", ".fsh", 35632);

        private static final Type[] VALUES;
        private final String name;
        private final String fileExtension;
        private final int glType;

        private Type(String name, String extension, int glType) {
            this.name = name;
            this.fileExtension = extension;
            this.glType = glType;
        }

        @Nullable
        public static Type fromId(Identifier id) {
            for (Type lv : VALUES) {
                if (!id.getPath().endsWith(lv.fileExtension)) continue;
                return lv;
            }
            return null;
        }

        public String getName() {
            return this.name;
        }

        public int getGlType() {
            return this.glType;
        }

        public ResourceFinder createFinder() {
            return new ResourceFinder("shaders", this.fileExtension);
        }

        static {
            VALUES = Type.values();
        }
    }
}

