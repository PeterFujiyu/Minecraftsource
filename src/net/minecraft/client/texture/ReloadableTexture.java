/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ReloadableTexture
extends AbstractTexture {
    private final Identifier textureId;

    public ReloadableTexture(Identifier textureId) {
        this.textureId = textureId;
    }

    public Identifier getId() {
        return this.textureId;
    }

    public void reload(TextureContents contents) {
        boolean bl2;
        boolean bl = contents.clamp();
        this.bilinear = bl2 = contents.blur();
        NativeImage lv = contents.image();
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.load(lv, bl2, bl));
        } else {
            this.load(lv, bl2, bl);
        }
    }

    private void load(NativeImage image, boolean blur, boolean clamp) {
        TextureUtil.prepareImage(this.getGlId(), 0, image.getWidth(), image.getHeight());
        this.setFilter(blur, false);
        this.setClamp(clamp);
        image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), true);
    }

    public abstract TextureContents loadContents(ResourceManager var1) throws IOException;
}

