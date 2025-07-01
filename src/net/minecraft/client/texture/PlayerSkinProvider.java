/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftSessionService sessionService;
    private final LoadingCache<Key, CompletableFuture<Optional<SkinTextures>>> cache;
    private final FileCache skinCache;
    private final FileCache capeCache;
    private final FileCache elytraCache;

    public PlayerSkinProvider(Path directory, final MinecraftSessionService sessionService, final Executor executor) {
        this.sessionService = sessionService;
        this.skinCache = new FileCache(directory, MinecraftProfileTexture.Type.SKIN);
        this.capeCache = new FileCache(directory, MinecraftProfileTexture.Type.CAPE);
        this.elytraCache = new FileCache(directory, MinecraftProfileTexture.Type.ELYTRA);
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build(new CacheLoader<Key, CompletableFuture<Optional<SkinTextures>>>(){

            @Override
            public CompletableFuture<Optional<SkinTextures>> load(Key arg) {
                return ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
                    Property property = arg.packedTextures();
                    if (property == null) {
                        return MinecraftProfileTextures.EMPTY;
                    }
                    MinecraftProfileTextures minecraftProfileTextures = sessionService.unpackTextures(property);
                    if (minecraftProfileTextures.signatureState() == SignatureState.INVALID) {
                        LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", (Object)arg.profileId());
                    }
                    return minecraftProfileTextures;
                }, Util.getMainWorkerExecutor().named("unpackSkinTextures")).thenComposeAsync(textures -> PlayerSkinProvider.this.fetchSkinTextures(arg.profileId(), (MinecraftProfileTextures)textures), executor)).handle((skinTextures, throwable) -> {
                    if (throwable != null) {
                        LOGGER.warn("Failed to load texture for profile {}", (Object)arg.profileId, throwable);
                    }
                    return Optional.ofNullable(skinTextures);
                });
            }

            @Override
            public /* synthetic */ Object load(Object value) throws Exception {
                return this.load((Key)value);
            }
        });
    }

    public Supplier<SkinTextures> getSkinTexturesSupplier(GameProfile profile) {
        CompletableFuture<Optional<SkinTextures>> completableFuture = this.fetchSkinTextures(profile);
        SkinTextures lv = DefaultSkinHelper.getSkinTextures(profile);
        return () -> completableFuture.getNow(Optional.empty()).orElse(lv);
    }

    public SkinTextures getSkinTextures(GameProfile profile) {
        SkinTextures lv = this.fetchSkinTextures(profile).getNow(Optional.empty()).orElse(null);
        if (lv != null) {
            return lv;
        }
        return DefaultSkinHelper.getSkinTextures(profile);
    }

    public CompletableFuture<Optional<SkinTextures>> fetchSkinTextures(GameProfile profile) {
        Property property = this.sessionService.getPackedTextures(profile);
        return this.cache.getUnchecked(new Key(profile.getId(), property));
    }

    CompletableFuture<SkinTextures> fetchSkinTextures(UUID uuid, MinecraftProfileTextures textures) {
        SkinTextures.Model lv;
        CompletableFuture<Identifier> completableFuture;
        MinecraftProfileTexture minecraftProfileTexture = textures.skin();
        if (minecraftProfileTexture != null) {
            completableFuture = this.skinCache.get(minecraftProfileTexture);
            lv = SkinTextures.Model.fromName(minecraftProfileTexture.getMetadata("model"));
        } else {
            SkinTextures lv2 = DefaultSkinHelper.getSkinTextures(uuid);
            completableFuture = CompletableFuture.completedFuture(lv2.texture());
            lv = lv2.model();
        }
        String string = Nullables.map(minecraftProfileTexture, MinecraftProfileTexture::getUrl);
        MinecraftProfileTexture minecraftProfileTexture2 = textures.cape();
        CompletableFuture<Object> completableFuture2 = minecraftProfileTexture2 != null ? this.capeCache.get(minecraftProfileTexture2) : CompletableFuture.completedFuture(null);
        MinecraftProfileTexture minecraftProfileTexture3 = textures.elytra();
        CompletableFuture<Object> completableFuture3 = minecraftProfileTexture3 != null ? this.elytraCache.get(minecraftProfileTexture3) : CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3).thenApply(v -> new SkinTextures((Identifier)completableFuture.join(), string, (Identifier)completableFuture2.join(), (Identifier)completableFuture3.join(), lv, textures.signatureState() == SignatureState.SIGNED));
    }

    @Environment(value=EnvType.CLIENT)
    static class FileCache {
        private final Path directory;
        private final MinecraftProfileTexture.Type type;
        private final Map<String, CompletableFuture<Identifier>> hashToTexture = new Object2ObjectOpenHashMap<String, CompletableFuture<Identifier>>();

        FileCache(Path directory, MinecraftProfileTexture.Type type) {
            this.directory = directory;
            this.type = type;
        }

        public CompletableFuture<Identifier> get(MinecraftProfileTexture texture) {
            String string = texture.getHash();
            CompletableFuture<Identifier> completableFuture = this.hashToTexture.get(string);
            if (completableFuture == null) {
                completableFuture = this.store(texture);
                this.hashToTexture.put(string, completableFuture);
            }
            return completableFuture;
        }

        private CompletableFuture<Identifier> store(MinecraftProfileTexture texture) {
            String string = Hashing.sha1().hashUnencodedChars(texture.getHash()).toString();
            Identifier lv = this.getTexturePath(string);
            Path path = this.directory.resolve(string.length() > 2 ? string.substring(0, 2) : "xx").resolve(string);
            return PlayerSkinTextureDownloader.downloadAndRegisterTexture(lv, path, texture.getUrl(), this.type == MinecraftProfileTexture.Type.SKIN);
        }

        private Identifier getTexturePath(String hash) {
            String string2 = switch (this.type) {
                default -> throw new MatchException(null, null);
                case MinecraftProfileTexture.Type.SKIN -> "skins";
                case MinecraftProfileTexture.Type.CAPE -> "capes";
                case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
            };
            return Identifier.ofVanilla(string2 + "/" + hash);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Key(UUID profileId, @Nullable Property packedTextures) {
        @Nullable
        public Property packedTextures() {
            return this.packedTextures;
        }
    }
}

