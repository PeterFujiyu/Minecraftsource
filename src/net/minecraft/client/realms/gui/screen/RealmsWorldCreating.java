/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.FailedRealmsUploadException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.realms.util.RealmsUploader;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldCreating {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void showCreateWorldScreen(MinecraftClient client, Screen parent, Screen realmsScreen, int slotId, RealmsServer server, @Nullable WorldCreationTask creationTask) {
        CreateWorldScreen.show(client, parent, (screen, dynamicRegistries, levelProperties, dataPackTempDir) -> {
            Path path2;
            try {
                path2 = RealmsWorldCreating.saveTempWorld(dynamicRegistries, levelProperties, dataPackTempDir);
            } catch (IOException iOException) {
                LOGGER.warn("Failed to create temporary world folder.");
                client.setScreen(new RealmsGenericErrorScreen(Text.translatable("mco.create.world.failed"), realmsScreen));
                return true;
            }
            RealmsWorldOptions lv = RealmsWorldOptions.create(levelProperties.getLevelInfo(), SharedConstants.getGameVersion().getName());
            RealmsUploader lv2 = new RealmsUploader(path2, lv, client.getSession(), arg3.id, slotId, UploadProgressTracker.create());
            client.setScreenAndRender(new NoticeScreen(lv2::cancel, Text.translatable("mco.create.world.reset.title"), Text.empty(), ScreenTexts.CANCEL, false));
            if (creationTask != null) {
                creationTask.run();
            }
            lv2.upload().handleAsync((v, throwable) -> {
                if (throwable != null) {
                    if (throwable instanceof CompletionException) {
                        CompletionException completionException = (CompletionException)throwable;
                        throwable = completionException.getCause();
                    }
                    if (throwable instanceof CancelledRealmsUploadException) {
                        client.setScreenAndRender(realmsScreen);
                    } else {
                        if (throwable instanceof FailedRealmsUploadException) {
                            FailedRealmsUploadException lv = (FailedRealmsUploadException)throwable;
                            LOGGER.warn("Failed to create realms world {}", (Object)lv.getStatus());
                        } else {
                            LOGGER.warn("Failed to create realms world {}", (Object)throwable.getMessage());
                        }
                        client.setScreenAndRender(new RealmsGenericErrorScreen(Text.translatable("mco.create.world.failed"), realmsScreen));
                    }
                } else {
                    if (parent instanceof RealmsConfigureWorldScreen) {
                        RealmsConfigureWorldScreen lv2 = (RealmsConfigureWorldScreen)parent;
                        lv2.fetchServerData(arg4.id);
                    }
                    if (creationTask != null) {
                        RealmsMainScreen.play(server, parent, true);
                    } else {
                        client.setScreenAndRender(parent);
                    }
                    RealmsMainScreen.resetServerList();
                }
                return null;
            }, (Executor)client);
            return true;
        });
    }

    private static Path saveTempWorld(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, LevelProperties levelProperties, @Nullable Path dataPackTempDir) throws IOException {
        Path path2 = Files.createTempDirectory("minecraft_realms_world_upload", new FileAttribute[0]);
        if (dataPackTempDir != null) {
            Files.move(dataPackTempDir, path2.resolve("datapacks"), new CopyOption[0]);
        }
        NbtCompound lv = levelProperties.cloneWorldNbt(dynamicRegistries.getCombinedRegistryManager(), null);
        NbtCompound lv2 = new NbtCompound();
        lv2.put("Data", lv);
        Path path3 = Files.createFile(path2.resolve("level.dat"), new FileAttribute[0]);
        NbtIo.writeCompressed(lv2, path3);
        return path2;
    }
}

