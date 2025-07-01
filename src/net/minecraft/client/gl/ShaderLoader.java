/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.GlImportProcessor;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramDefinition;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PathUtil;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ShaderLoader
extends SinglePreparationResourceReloader<Definitions>
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final String SHADERS_PATH = "shaders";
    public static final String INCLUDE_PATH = "shaders/include/";
    private static final ResourceFinder SHADERS_FINDER = ResourceFinder.json("shaders");
    private static final ResourceFinder POST_EFFECT_FINDER = ResourceFinder.json("post_effect");
    public static final int field_53936 = 32768;
    final TextureManager textureManager;
    private final Consumer<Exception> onError;
    private Cache cache = new Cache(Definitions.EMPTY);

    public ShaderLoader(TextureManager textureManager, Consumer<Exception> onError) {
        this.textureManager = textureManager;
        this.onError = onError;
    }

    @Override
    protected Definitions prepare(ResourceManager arg, Profiler arg2) {
        ImmutableMap.Builder<Identifier, ShaderProgramDefinition> builder = ImmutableMap.builder();
        ImmutableMap.Builder<ShaderSourceKey, String> builder2 = ImmutableMap.builder();
        Map<Identifier, Resource> map = arg.findResources(SHADERS_PATH, id -> ShaderLoader.isDefinition(id) || ShaderLoader.isShaderSource(id));
        for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
            Identifier lv = entry.getKey();
            CompiledShader.Type lv2 = CompiledShader.Type.fromId(lv);
            if (lv2 != null) {
                ShaderLoader.loadShaderSource(lv, entry.getValue(), lv2, map, builder2);
                continue;
            }
            if (!ShaderLoader.isDefinition(lv)) continue;
            ShaderLoader.loadDefinition(lv, entry.getValue(), builder);
        }
        ImmutableMap.Builder<Identifier, PostEffectPipeline> builder3 = ImmutableMap.builder();
        for (Map.Entry<Identifier, Resource> entry2 : POST_EFFECT_FINDER.findResources(arg).entrySet()) {
            ShaderLoader.loadPostEffect(entry2.getKey(), entry2.getValue(), builder3);
        }
        return new Definitions(builder.build(), builder2.build(), builder3.build());
    }

    private static void loadShaderSource(Identifier id, Resource resource, CompiledShader.Type type, Map<Identifier, Resource> allResources, ImmutableMap.Builder<ShaderSourceKey, String> builder) {
        Identifier lv = type.createFinder().toResourceId(id);
        GlImportProcessor lv2 = ShaderLoader.createImportProcessor(allResources, id);
        try (BufferedReader reader = resource.getReader();){
            String string = IOUtils.toString(reader);
            builder.put(new ShaderSourceKey(lv, type), String.join((CharSequence)"", lv2.readSource(string)));
        } catch (IOException iOException) {
            LOGGER.error("Failed to load shader source at {}", (Object)id, (Object)iOException);
        }
    }

    private static GlImportProcessor createImportProcessor(final Map<Identifier, Resource> allResources, Identifier id) {
        final Identifier lv = id.withPath(PathUtil::getPosixFullPath);
        return new GlImportProcessor(){
            private final Set<Identifier> processed = new ObjectArraySet<Identifier>();

            @Override
            public String loadImport(boolean inline, String name) {
                String string;
                block11: {
                    Identifier lv3;
                    try {
                        lv3 = inline ? lv.withPath(path -> PathUtil.normalizeToPosix(path + name)) : Identifier.of(name).withPrefixedPath(ShaderLoader.INCLUDE_PATH);
                    } catch (InvalidIdentifierException lv2) {
                        LOGGER.error("Malformed GLSL import {}: {}", (Object)name, (Object)lv2.getMessage());
                        return "#error " + lv2.getMessage();
                    }
                    if (!this.processed.add(lv3)) {
                        return null;
                    }
                    BufferedReader reader = ((Resource)allResources.get(lv3)).getReader();
                    try {
                        string = IOUtils.toString(reader);
                        if (reader == null) break block11;
                    } catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    ((Reader)reader).close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (IOException iOException) {
                            LOGGER.error("Could not open GLSL import {}: {}", (Object)lv3, (Object)iOException.getMessage());
                            return "#error " + iOException.getMessage();
                        }
                    }
                    ((Reader)reader).close();
                }
                return string;
            }
        };
    }

    private static void loadDefinition(Identifier id, Resource resource, ImmutableMap.Builder<Identifier, ShaderProgramDefinition> builder) {
        Identifier lv = SHADERS_FINDER.toResourceId(id);
        try (BufferedReader reader = resource.getReader();){
            JsonElement jsonElement = JsonParser.parseReader(reader);
            ShaderProgramDefinition lv2 = (ShaderProgramDefinition)ShaderProgramDefinition.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new);
            builder.put(lv, lv2);
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Failed to parse shader config at {}", (Object)id, (Object)exception);
        }
    }

    private static void loadPostEffect(Identifier id, Resource resource, ImmutableMap.Builder<Identifier, PostEffectPipeline> builder) {
        Identifier lv = POST_EFFECT_FINDER.toResourceId(id);
        try (BufferedReader reader = resource.getReader();){
            JsonElement jsonElement = JsonParser.parseReader(reader);
            builder.put(lv, (PostEffectPipeline)PostEffectPipeline.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new));
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Failed to parse post chain at {}", (Object)id, (Object)exception);
        }
    }

    private static boolean isDefinition(Identifier id) {
        return id.getPath().endsWith(".json");
    }

    private static boolean isShaderSource(Identifier id) {
        return CompiledShader.Type.fromId(id) != null || id.getPath().endsWith(".glsl");
    }

    @Override
    protected void apply(Definitions arg, ResourceManager arg2, Profiler arg3) {
        Cache lv = new Cache(arg);
        HashMap<ShaderProgramKey, LoadException> map = new HashMap<ShaderProgramKey, LoadException>();
        HashSet<ShaderProgramKey> set = new HashSet<ShaderProgramKey>(ShaderProgramKeys.getAll());
        for (PostEffectPipeline lv2 : arg.postChains.values()) {
            for (PostEffectPipeline.Pass lv3 : lv2.passes()) {
                set.add(lv3.getShaderProgramKey());
            }
        }
        for (ShaderProgramKey lv4 : set) {
            try {
                lv.shaderPrograms.put(lv4, Optional.of(lv.loadProgram(lv4)));
            } catch (LoadException lv5) {
                map.put(lv4, lv5);
            }
        }
        if (!map.isEmpty()) {
            lv.close();
            throw new RuntimeException("Failed to load required shader programs:\n" + map.entrySet().stream().map(entry -> " - " + String.valueOf(entry.getKey()) + ": " + ((LoadException)entry.getValue()).getMessage()).collect(Collectors.joining("\n")));
        }
        this.cache.close();
        this.cache = lv;
    }

    @Override
    public String getName() {
        return "Shader Loader";
    }

    private void handleError(Exception exception) {
        if (this.cache.errorHandled) {
            return;
        }
        this.onError.accept(exception);
        this.cache.errorHandled = true;
    }

    public void preload(ResourceFactory factory, ShaderProgramKey ... keys) throws IOException, LoadException {
        for (ShaderProgramKey lv : keys) {
            Resource lv2 = factory.getResourceOrThrow(SHADERS_FINDER.toResourcePath(lv.configId()));
            try (BufferedReader reader = lv2.getReader();){
                JsonElement jsonElement = JsonParser.parseReader(reader);
                ShaderProgramDefinition lv3 = (ShaderProgramDefinition)ShaderProgramDefinition.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new);
                Defines lv4 = lv3.defines().withMerged(lv.defines());
                CompiledShader lv5 = this.compileShader(factory, lv3.vertex(), CompiledShader.Type.VERTEX, lv4);
                CompiledShader lv6 = this.compileShader(factory, lv3.fragment(), CompiledShader.Type.FRAGMENT, lv4);
                ShaderProgram lv7 = ShaderLoader.createProgram(lv, lv3, lv5, lv6);
                this.cache.shaderPrograms.put(lv, Optional.of(lv7));
            }
        }
    }

    private CompiledShader compileShader(ResourceFactory factory, Identifier id, CompiledShader.Type type, Defines defines) throws IOException, LoadException {
        Identifier lv = type.createFinder().toResourcePath(id);
        try (BufferedReader reader = factory.getResourceOrThrow(lv).getReader();){
            String string = IOUtils.toString(reader);
            String string2 = GlImportProcessor.addDefines(string, defines);
            CompiledShader lv2 = CompiledShader.compile(id, type, string2);
            this.cache.compiledShaders.put(new ShaderKey(id, type, defines), lv2);
            CompiledShader compiledShader = lv2;
            return compiledShader;
        }
    }

    @Nullable
    public ShaderProgram getOrCreateProgram(ShaderProgramKey key) {
        try {
            return this.cache.getOrLoadProgram(key);
        } catch (LoadException lv) {
            LOGGER.error("Failed to load shader program: {}", (Object)key, (Object)lv);
            this.cache.shaderPrograms.put(key, Optional.empty());
            this.handleError(lv);
            return null;
        }
    }

    public ShaderProgram getProgramToLoad(ShaderProgramKey key) throws LoadException {
        ShaderProgram lv = this.cache.getOrLoadProgram(key);
        if (lv == null) {
            throw new LoadException("Shader '" + String.valueOf(key) + "' could not be found");
        }
        return lv;
    }

    static ShaderProgram createProgram(ShaderProgramKey key, ShaderProgramDefinition definition, CompiledShader vertexShader, CompiledShader fragmentShader) throws LoadException {
        ShaderProgram lv = ShaderProgram.create(vertexShader, fragmentShader, key.vertexFormat());
        lv.set(definition.uniforms(), definition.samplers());
        return lv;
    }

    @Nullable
    public PostEffectProcessor loadPostEffect(Identifier id, Set<Identifier> availableExternalTargets) {
        try {
            return this.cache.getOrLoadProcessor(id, availableExternalTargets);
        } catch (LoadException lv) {
            LOGGER.error("Failed to load post chain: {}", (Object)id, (Object)lv);
            this.cache.postEffectProcessors.put(id, Optional.empty());
            this.handleError(lv);
            return null;
        }
    }

    @Override
    public void close() {
        this.cache.close();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    class Cache
    implements AutoCloseable {
        private final Definitions definitions;
        final Map<ShaderProgramKey, Optional<ShaderProgram>> shaderPrograms = new HashMap<ShaderProgramKey, Optional<ShaderProgram>>();
        final Map<ShaderKey, CompiledShader> compiledShaders = new HashMap<ShaderKey, CompiledShader>();
        final Map<Identifier, Optional<PostEffectProcessor>> postEffectProcessors = new HashMap<Identifier, Optional<PostEffectProcessor>>();
        boolean errorHandled;

        Cache(Definitions definitions) {
            this.definitions = definitions;
        }

        @Nullable
        public ShaderProgram getOrLoadProgram(ShaderProgramKey key) throws LoadException {
            Optional<ShaderProgram> optional = this.shaderPrograms.get(key);
            if (optional != null) {
                return optional.orElse(null);
            }
            ShaderProgram lv = this.loadProgram(key);
            this.shaderPrograms.put(key, Optional.of(lv));
            return lv;
        }

        ShaderProgram loadProgram(ShaderProgramKey key) throws LoadException {
            ShaderProgramDefinition lv = this.definitions.programs.get(key.configId());
            if (lv == null) {
                throw new LoadException("Could not find program with id: " + String.valueOf(key.configId()));
            }
            Defines lv2 = lv.defines().withMerged(key.defines());
            CompiledShader lv3 = this.loadShader(lv.vertex(), CompiledShader.Type.VERTEX, lv2);
            CompiledShader lv4 = this.loadShader(lv.fragment(), CompiledShader.Type.FRAGMENT, lv2);
            return ShaderLoader.createProgram(key, lv, lv3, lv4);
        }

        private CompiledShader loadShader(Identifier id, CompiledShader.Type type, Defines defines) throws LoadException {
            ShaderKey lv = new ShaderKey(id, type, defines);
            CompiledShader lv2 = this.compiledShaders.get(lv);
            if (lv2 == null) {
                lv2 = this.compileShader(lv);
                this.compiledShaders.put(lv, lv2);
            }
            return lv2;
        }

        private CompiledShader compileShader(ShaderKey key) throws LoadException {
            String string = this.definitions.shaderSources.get(new ShaderSourceKey(key.id, key.type));
            if (string == null) {
                throw new LoadException("Could not find shader: " + String.valueOf(key));
            }
            String string2 = GlImportProcessor.addDefines(string, key.defines);
            return CompiledShader.compile(key.id, key.type, string2);
        }

        @Nullable
        public PostEffectProcessor getOrLoadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws LoadException {
            Optional<PostEffectProcessor> optional = this.postEffectProcessors.get(id);
            if (optional != null) {
                return optional.orElse(null);
            }
            PostEffectProcessor lv = this.loadProcessor(id, availableExternalTargets);
            this.postEffectProcessors.put(id, Optional.of(lv));
            return lv;
        }

        private PostEffectProcessor loadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws LoadException {
            PostEffectPipeline lv = this.definitions.postChains.get(id);
            if (lv == null) {
                throw new LoadException("Could not find post chain with id: " + String.valueOf(id));
            }
            return PostEffectProcessor.parseEffect(lv, ShaderLoader.this.textureManager, ShaderLoader.this, availableExternalTargets);
        }

        @Override
        public void close() {
            RenderSystem.assertOnRenderThread();
            this.shaderPrograms.values().forEach(program -> program.ifPresent(ShaderProgram::close));
            this.compiledShaders.values().forEach(CompiledShader::close);
            this.shaderPrograms.clear();
            this.compiledShaders.clear();
            this.postEffectProcessors.clear();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Definitions(Map<Identifier, ShaderProgramDefinition> programs, Map<ShaderSourceKey, String> shaderSources, Map<Identifier, PostEffectPipeline> postChains) {
        public static final Definitions EMPTY = new Definitions(Map.of(), Map.of(), Map.of());
    }

    @Environment(value=EnvType.CLIENT)
    record ShaderSourceKey(Identifier id, CompiledShader.Type type) {
        @Override
        public String toString() {
            return String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LoadException
    extends Exception {
        public LoadException(String message) {
            super(message);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record ShaderKey(Identifier id, CompiledShader.Type type, Defines defines) {
        @Override
        public String toString() {
            String string = String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
            if (!this.defines.isEmpty()) {
                return string + " with " + String.valueOf(this.defines);
            }
            return string;
        }
    }
}

