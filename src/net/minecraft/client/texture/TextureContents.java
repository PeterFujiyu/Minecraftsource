/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record TextureContents(NativeImage image, @Nullable TextureResourceMetadata metadata) implements Closeable
{
    public static TextureContents load(ResourceManager resourceManager, Identifier textureId) throws IOException {
        NativeImage lv2;
        Resource lv = resourceManager.getResourceOrThrow(textureId);
        try (InputStream inputStream = lv.getInputStream();){
            lv2 = NativeImage.read(inputStream);
        }
        TextureResourceMetadata lv3 = lv.getMetadata().decode(TextureResourceMetadata.SERIALIZER).orElse(null);
        return new TextureContents(lv2, lv3);
    }

    public static TextureContents createMissing() {
        return new TextureContents(MissingSprite.createImage(), null);
    }

    public boolean blur() {
        return this.metadata != null ? this.metadata.blur() : false;
    }

    public boolean clamp() {
        return this.metadata != null ? this.metadata.clamp() : false;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Nullable
    public TextureResourceMetadata metadata() {
        return this.metadata;
    }
}

