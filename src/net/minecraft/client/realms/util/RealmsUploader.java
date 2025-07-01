/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.realms.FileUpload;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.CloseFailureRealmsUploadException;
import net.minecraft.client.realms.exception.upload.FailedRealmsUploadException;
import net.minecraft.client.realms.util.UploadCompressor;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.client.realms.util.UploadResult;
import net.minecraft.client.realms.util.UploadTokenCache;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUploader {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MAX_ATTEMPTS = 20;
    private final RealmsClient client = RealmsClient.create();
    private final Path directory;
    private final RealmsWorldOptions options;
    private final Session session;
    private final long worldId;
    private final int slotId;
    private final UploadProgressTracker progressTracker;
    private volatile boolean cancelled;
    @Nullable
    private FileUpload upload;

    public RealmsUploader(Path directory, RealmsWorldOptions options, Session session, long worldId, int slotId, UploadProgressTracker progressTracker) {
        this.directory = directory;
        this.options = options;
        this.session = session;
        this.worldId = worldId;
        this.slotId = slotId;
        this.progressTracker = progressTracker;
    }

    public CompletableFuture<?> upload() {
        return CompletableFuture.runAsync(() -> {
            File file = null;
            try {
                FileUpload lv2;
                UploadInfo lv = this.uploadSync();
                file = UploadCompressor.compress(this.directory, () -> this.cancelled);
                this.progressTracker.updateProgressDisplay();
                this.upload = lv2 = new FileUpload(file, this.worldId, this.slotId, lv, this.session, SharedConstants.getGameVersion().getName(), this.options.version, this.progressTracker.getUploadProgress());
                UploadResult lv3 = lv2.upload();
                String string = lv3.getErrorMessage();
                if (string != null) {
                    throw new FailedRealmsUploadException(string);
                }
                UploadTokenCache.invalidate(this.worldId);
                this.client.updateSlot(this.worldId, this.slotId, this.options);
            } catch (IOException iOException) {
                throw new FailedRealmsUploadException(iOException.getMessage());
            } catch (RealmsServiceException lv4) {
                throw new FailedRealmsUploadException(lv4.error.getText());
            } catch (InterruptedException | CancellationException exception) {
                throw new CancelledRealmsUploadException();
            } finally {
                if (file != null) {
                    LOGGER.debug("Deleting file {}", (Object)file.getAbsolutePath());
                    file.delete();
                }
            }
        }, Util.getMainWorkerExecutor());
    }

    public void cancel() {
        this.cancelled = true;
        if (this.upload != null) {
            this.upload.cancel();
            this.upload = null;
        }
    }

    private UploadInfo uploadSync() throws RealmsServiceException, InterruptedException {
        for (int i = 0; i < 20; ++i) {
            try {
                UploadInfo lv = this.client.upload(this.worldId);
                if (this.cancelled) {
                    throw new CancelledRealmsUploadException();
                }
                if (lv == null) continue;
                if (!lv.isWorldClosed()) {
                    throw new CloseFailureRealmsUploadException();
                }
                return lv;
            } catch (RetryCallException lv2) {
                Thread.sleep((long)lv2.delaySeconds * 1000L);
            }
        }
        throw new CloseFailureRealmsUploadException();
    }
}

