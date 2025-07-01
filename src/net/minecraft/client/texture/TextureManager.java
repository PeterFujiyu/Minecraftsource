/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.screen.BuyRealmsScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TextureManager
implements ResourceReloader,
TextureTickListener,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Identifier MISSING_IDENTIFIER = Identifier.ofVanilla("");
    private final Map<Identifier, AbstractTexture> textures = new HashMap<Identifier, AbstractTexture>();
    private final Set<TextureTickListener> tickListeners = new HashSet<TextureTickListener>();
    private final ResourceManager resourceContainer;

    public TextureManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
        NativeImage lv = MissingSprite.createImage();
        this.registerTexture(MissingSprite.getMissingSpriteId(), new NativeImageBackedTexture(lv));
    }

    public void registerTexture(Identifier id, ReloadableTexture texture) {
        try {
            texture.reload(this.loadTexture(id, texture));
        } catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Uploading texture");
            CrashReportSection lv2 = lv.addElement("Uploaded texture");
            lv2.add("Resource location", texture.getId());
            lv2.add("Texture id", id);
            throw new CrashException(lv);
        }
        this.registerTexture(id, (AbstractTexture)texture);
    }

    private TextureContents loadTexture(Identifier id, ReloadableTexture texture) {
        try {
            return TextureManager.loadTexture(this.resourceContainer, id, texture);
        } catch (Exception exception) {
            LOGGER.error("Failed to load texture {} into slot {}", texture.getId(), id, exception);
            return TextureContents.createMissing();
        }
    }

    public void registerTexture(Identifier id) {
        this.registerTexture(id, (AbstractTexture)new ResourceTexture(id));
    }

    public void registerTexture(Identifier id, AbstractTexture texture) {
        AbstractTexture lv = this.textures.put(id, texture);
        if (lv != texture) {
            if (lv != null) {
                this.closeTexture(id, lv);
            }
            if (texture instanceof TextureTickListener) {
                TextureTickListener lv2 = (TextureTickListener)((Object)texture);
                this.tickListeners.add(lv2);
            }
        }
    }

    private void closeTexture(Identifier id, AbstractTexture texture) {
        this.tickListeners.remove(texture);
        try {
            texture.close();
        } catch (Exception exception) {
            LOGGER.warn("Failed to close texture {}", (Object)id, (Object)exception);
        }
        texture.clearGlId();
    }

    public AbstractTexture getTexture(Identifier id) {
        AbstractTexture lv = this.textures.get(id);
        if (lv != null) {
            return lv;
        }
        ResourceTexture lv2 = new ResourceTexture(id);
        this.registerTexture(id, lv2);
        return lv2;
    }

    @Override
    public void tick() {
        for (TextureTickListener lv : this.tickListeners) {
            lv.tick();
        }
    }

    public void destroyTexture(Identifier id) {
        AbstractTexture lv = this.textures.remove(id);
        if (lv != null) {
            this.closeTexture(id, lv);
        }
    }

    @Override
    public void close() {
        this.textures.forEach(this::closeTexture);
        this.textures.clear();
        this.tickListeners.clear();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
        ArrayList list = new ArrayList();
        this.textures.forEach((id, texture) -> {
            if (texture instanceof ReloadableTexture) {
                ReloadableTexture lv = (ReloadableTexture)texture;
                list.add(TextureManager.reloadTexture(manager, id, lv, prepareExecutor));
            }
        });
        return ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])list.stream().map(ReloadedTexture::newContents).toArray(CompletableFuture[]::new)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            BuyRealmsScreen.refreshImages(this.resourceContainer);
            for (ReloadedTexture lv : list) {
                lv.texture.reload(lv.newContents.join());
            }
        }, applyExecutor);
    }

    public void dumpDynamicTextures(Path path) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.dumpDynamicTexturesInternal(path));
        } else {
            this.dumpDynamicTexturesInternal(path);
        }
    }

    private void dumpDynamicTexturesInternal(Path path) {
        try {
            Files.createDirectories(path, new FileAttribute[0]);
        } catch (IOException iOException) {
            LOGGER.error("Failed to create directory {}", (Object)path, (Object)iOException);
            return;
        }
        this.textures.forEach((id, texture) -> {
            if (texture instanceof DynamicTexture) {
                DynamicTexture lv = (DynamicTexture)((Object)texture);
                try {
                    lv.save((Identifier)id, path);
                } catch (IOException iOException) {
                    LOGGER.error("Failed to dump texture {}", id, (Object)iOException);
                }
            }
        });
    }

    private static TextureContents loadTexture(ResourceManager resourceManager, Identifier textureId, ReloadableTexture texture) throws IOException {
        try {
            return texture.loadContents(resourceManager);
        } catch (FileNotFoundException fileNotFoundException) {
            if (textureId != MISSING_IDENTIFIER) {
                LOGGER.warn("Missing resource {} referenced from {}", (Object)texture.getId(), (Object)textureId);
            }
            return TextureContents.createMissing();
        }
    }

    private static ReloadedTexture reloadTexture(ResourceManager resourceManager, Identifier textureId, ReloadableTexture texture, Executor prepareExecutor) {
        return new ReloadedTexture(texture, CompletableFuture.supplyAsync(() -> {
            try {
                return TextureManager.loadTexture(resourceManager, textureId, texture);
            } catch (IOException iOException) {
                throw new UncheckedIOException(iOException);
            }
        }, prepareExecutor));
    }

    @Environment(value=EnvType.CLIENT)
    record ReloadedTexture(ReloadableTexture texture, CompletableFuture<TextureContents> newContents) {
    }
}

