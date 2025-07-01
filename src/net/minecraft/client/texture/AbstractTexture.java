/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TriState;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTexture
implements AutoCloseable {
    public static final int DEFAULT_ID = -1;
    protected int glId = -1;
    protected boolean bilinear;
    private int wrapS = 10497;
    private int wrapT = 10497;
    private int minFilter = 9986;
    private int magFilter = 9729;

    public void setClamp(boolean clamp) {
        boolean bl3;
        int j;
        int i;
        RenderSystem.assertOnRenderThreadOrInit();
        if (clamp) {
            i = 33071;
            j = 33071;
        } else {
            i = 10497;
            j = 10497;
        }
        boolean bl2 = this.wrapS != i;
        boolean bl = bl3 = this.wrapT != j;
        if (bl2 || bl3) {
            this.bindTexture();
            if (bl2) {
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_S, i);
                this.wrapS = i;
            }
            if (bl3) {
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_T, j);
                this.wrapT = j;
            }
        }
    }

    public void setFilter(TriState bilinear, boolean mipmap) {
        this.setFilter(bilinear.asBoolean(this.bilinear), mipmap);
    }

    public void setFilter(boolean bilinear, boolean mipmap) {
        boolean bl4;
        int j;
        int i;
        RenderSystem.assertOnRenderThreadOrInit();
        if (bilinear) {
            i = mipmap ? 9987 : 9729;
            j = 9729;
        } else {
            i = mipmap ? GlConst.GL_NEAREST_MIPMAP_LINEAR : GlConst.GL_NEAREST;
            j = GlConst.GL_NEAREST;
        }
        boolean bl3 = this.minFilter != i;
        boolean bl = bl4 = this.magFilter != j;
        if (bl4 || bl3) {
            this.bindTexture();
            if (bl3) {
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, i);
                this.minFilter = i;
            }
            if (bl4) {
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, j);
                this.magFilter = j;
            }
        }
    }

    public int getGlId() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.glId == -1) {
            this.glId = TextureUtil.generateTextureId();
        }
        return this.glId;
    }

    public void clearGlId() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                if (this.glId != -1) {
                    TextureUtil.releaseTextureId(this.glId);
                    this.glId = -1;
                }
            });
        } else if (this.glId != -1) {
            TextureUtil.releaseTextureId(this.glId);
            this.glId = -1;
        }
    }

    public void bindTexture() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._bindTexture(this.getGlId()));
        } else {
            GlStateManager._bindTexture(this.getGlId());
        }
    }

    @Override
    public void close() {
    }
}

