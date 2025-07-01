/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.lang.runtime.SwitchBootstraps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class PostEffectProcessor {
    public static final Identifier MAIN = Identifier.ofVanilla("main");
    private final List<PostEffectPass> passes;
    private final Map<Identifier, PostEffectPipeline.Targets> internalTargets;
    private final Set<Identifier> externalTargets;

    private PostEffectProcessor(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets) {
        this.passes = passes;
        this.internalTargets = internalTargets;
        this.externalTargets = externalTargets;
    }

    public static PostEffectProcessor parseEffect(PostEffectPipeline pipeline, TextureManager textureManager, ShaderLoader shaderLoader, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException {
        Stream stream = pipeline.passes().stream().flatMap(pass -> pass.inputs().stream()).flatMap(input -> input.getTargetId().stream());
        Set<Identifier> set2 = stream.filter(target -> !pipeline.internalTargets().containsKey(target)).collect(Collectors.toSet());
        Sets.SetView set3 = Sets.difference(set2, availableExternalTargets);
        if (!set3.isEmpty()) {
            throw new ShaderLoader.LoadException("Referenced external targets are not available in this context: " + String.valueOf(set3));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (PostEffectPipeline.Pass lv : pipeline.passes()) {
            builder.add(PostEffectProcessor.parsePass(textureManager, shaderLoader, lv));
        }
        return new PostEffectProcessor((List<PostEffectPass>)((Object)builder.build()), pipeline.internalTargets(), set2);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static PostEffectPass parsePass(TextureManager textureManager, ShaderLoader shaderLoader, PostEffectPipeline.Pass pass) throws ShaderLoader.LoadException {
        ShaderProgram lv = shaderLoader.getProgramToLoad(pass.getShaderProgramKey());
        for (PostEffectPipeline.Uniform lv2 : pass.uniforms()) {
            String string = lv2.name();
            if (lv.getUniform(string) != null) continue;
            throw new ShaderLoader.LoadException("Uniform '" + string + "' does not exist for " + String.valueOf(pass.programId()));
        }
        String string2 = pass.programId().toString();
        PostEffectPass lv3 = new PostEffectPass(string2, lv, pass.outputTarget(), pass.uniforms());
        Iterator<PostEffectPipeline.Input> iterator = pass.inputs().iterator();
        block8: while (iterator.hasNext()) {
            Object object;
            PostEffectPipeline.Input input;
            PostEffectPipeline.Input lv4 = iterator.next();
            Objects.requireNonNull(lv4);
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PostEffectPipeline.TextureSampler.class, PostEffectPipeline.TargetSampler.class}, (Object)input, n)) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: {
                    PostEffectPipeline.TextureSampler textureSampler = (PostEffectPipeline.TextureSampler)input;
                    try {
                        int n2;
                        Object string3 = object = textureSampler.samplerName();
                        Object lv5 = object = textureSampler.location();
                        int i = n2 = textureSampler.width();
                        int j = n2 = textureSampler.height();
                        int bl2 = n2 = (int)(textureSampler.bilinear() ? 1 : 0);
                        AbstractTexture lv6 = textureManager.getTexture(((Identifier)lv5).withPath(name -> "textures/effect/" + name + ".png"));
                        lv6.setFilter(bl2 != 0, false);
                        lv3.addSampler(new PostEffectPass.TextureSampler((String)string3, lv6, i, j));
                        continue block8;
                    } catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                }
                case 1: 
            }
            object = (PostEffectPipeline.TargetSampler)input;
            {
                boolean bl;
                Object object2 = ((PostEffectPipeline.TargetSampler)object).samplerName();
                String string4 = object2;
                Object lv7 = object2 = ((PostEffectPipeline.TargetSampler)object).targetId();
                boolean bl2 = bl = ((PostEffectPipeline.TargetSampler)object).useDepthBuffer();
                boolean bl3 = bl = ((PostEffectPipeline.TargetSampler)object).bilinear();
                lv3.addSampler(new PostEffectPass.TargetSampler(string4, (Identifier)lv7, bl2, bl3));
                continue;
            }
            break;
        }
        return lv3;
    }

    /*
     * Could not resolve type clashes
     * Loose catch block
     */
    public void render(FrameGraphBuilder builder, int textureWidth, int textureHeight, FramebufferSet framebufferSet) {
        block11: {
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, textureWidth, 0.0f, textureHeight, 0.1f, 1000.0f);
            HashMap<Identifier, Handle<Framebuffer>> map = new HashMap<Identifier, Handle<Framebuffer>>(this.internalTargets.size() + this.externalTargets.size());
            for (Identifier lv : this.externalTargets) {
                map.put(lv, framebufferSet.getOrThrow(lv));
            }
            for (Map.Entry entry : this.internalTargets.entrySet()) {
                PostEffectPipeline.Targets targets;
                Identifier lv2 = (Identifier)entry.getKey();
                Objects.requireNonNull((PostEffectPipeline.Targets)entry.getValue());
                int n = 0;
                SimpleFramebufferFactory lv3 = switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PostEffectPipeline.CustomSized.class, PostEffectPipeline.ScreenSized.class}, (Object)targets, n)) {
                    default -> throw new MatchException(null, null);
                    case 0 -> {
                        int var16_17;
                        PostEffectPipeline.CustomSized var13_14 = (PostEffectPipeline.CustomSized)targets;
                        int k = var16_17 = var13_14.width();
                        int l = var16_17 = var13_14.height();
                        yield new SimpleFramebufferFactory(k, l, true);
                    }
                    case 1 -> {
                        PostEffectPipeline.ScreenSized var16_18 = (PostEffectPipeline.ScreenSized)targets;
                        yield new SimpleFramebufferFactory(textureWidth, textureHeight, true);
                    }
                };
                map.put(lv2, builder.createResourceHandle(lv2.toString(), lv3));
            }
            for (PostEffectPass lv4 : this.passes) {
                lv4.render(builder, map, matrix4f);
            }
            for (Identifier lv : this.externalTargets) {
                framebufferSet.set(lv, (Handle)map.get(lv));
            }
            break block11;
            catch (Throwable throwable) {
                throw new MatchException(throwable.toString(), throwable);
            }
        }
    }

    @Deprecated
    public void render(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
        FrameGraphBuilder lv = new FrameGraphBuilder();
        FramebufferSet lv2 = FramebufferSet.singleton(MAIN, lv.createObjectNode("main", framebuffer));
        this.render(lv, framebuffer.textureWidth, framebuffer.textureHeight, lv2);
        lv.run(objectAllocator);
    }

    public void setUniforms(String name, float value) {
        for (PostEffectPass lv : this.passes) {
            lv.getProgram().getUniformOrDefault(name).set(value);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface FramebufferSet {
        public static FramebufferSet singleton(final Identifier id, final Handle<Framebuffer> framebuffer) {
            return new FramebufferSet(){
                private Handle<Framebuffer> framebuffer;
                {
                    this.framebuffer = framebuffer;
                }

                @Override
                public void set(Identifier id2, Handle<Framebuffer> framebuffer2) {
                    if (!id2.equals(id)) {
                        throw new IllegalArgumentException("No target with id " + String.valueOf(id2));
                    }
                    this.framebuffer = framebuffer2;
                }

                @Override
                @Nullable
                public Handle<Framebuffer> get(Identifier id2) {
                    return id2.equals(id) ? this.framebuffer : null;
                }
            };
        }

        public void set(Identifier var1, Handle<Framebuffer> var2);

        @Nullable
        public Handle<Framebuffer> get(Identifier var1);

        default public Handle<Framebuffer> getOrThrow(Identifier id) {
            Handle<Framebuffer> lv = this.get(id);
            if (lv == null) {
                throw new IllegalArgumentException("Missing target with id " + String.valueOf(id));
            }
            return lv;
        }
    }
}

