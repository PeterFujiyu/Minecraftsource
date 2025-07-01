/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline(Map<Identifier, Targets> internalTargets, List<Pass> passes) {
    public static final Codec<PostEffectPipeline> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(Identifier.CODEC, Targets.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostEffectPipeline::internalTargets), Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostEffectPipeline::passes)).apply((Applicative<PostEffectPipeline, ?>)instance, PostEffectPipeline::new));

    @Environment(value=EnvType.CLIENT)
    public static sealed interface Targets
    permits ScreenSized, CustomSized {
        public static final Codec<Targets> CODEC = Codec.either(CustomSized.CODEC, ScreenSized.CODEC).xmap(either -> (Targets)either.map(Function.identity(), Function.identity()), targets -> {
            Targets targets2 = targets;
            Objects.requireNonNull(targets2);
            Targets lv = targets2;
            int i = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{CustomSized.class, ScreenSized.class}, (Object)lv, i)) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    CustomSized lv2 = (CustomSized)lv;
                    yield Either.left(lv2);
                }
                case 1 -> {
                    ScreenSized lv3 = (ScreenSized)lv;
                    yield Either.right(lv3);
                }
            };
        });
    }

    @Environment(value=EnvType.CLIENT)
    public record Pass(Identifier programId, List<Input> inputs, Identifier outputTarget, List<Uniform> uniforms) {
        private static final Codec<List<Input>> INPUTS_CODEC = Input.CODEC.listOf().validate(inputs -> {
            ObjectArraySet set = new ObjectArraySet(inputs.size());
            for (Input lv : inputs) {
                if (set.add(lv.samplerName())) continue;
                return DataResult.error(() -> "Encountered repeated sampler name: " + lv.samplerName());
            }
            return DataResult.success(inputs);
        });
        public static final Codec<Pass> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("program")).forGetter(Pass::programId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(Pass::inputs), ((MapCodec)Identifier.CODEC.fieldOf("output")).forGetter(Pass::outputTarget), Uniform.CODEC.listOf().optionalFieldOf("uniforms", List.of()).forGetter(Pass::uniforms)).apply((Applicative<Pass, ?>)instance, Pass::new));

        public ShaderProgramKey getShaderProgramKey() {
            return new ShaderProgramKey(this.programId, VertexFormats.POSITION, Defines.EMPTY);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Uniform(String name, List<Float> values) {
        public static final Codec<Uniform> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("name")).forGetter(Uniform::name), ((MapCodec)Codec.FLOAT.sizeLimitedListOf(4).fieldOf("values")).forGetter(Uniform::values)).apply((Applicative<Uniform, ?>)instance, Uniform::new));
    }

    @Environment(value=EnvType.CLIENT)
    public record TargetSampler(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements Input
    {
        public static final Codec<TargetSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("sampler_name")).forGetter(TargetSampler::samplerName), ((MapCodec)Identifier.CODEC.fieldOf("target")).forGetter(TargetSampler::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(TargetSampler::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TargetSampler::bilinear)).apply((Applicative<TargetSampler, ?>)instance, TargetSampler::new));

        @Override
        public Set<Identifier> getTargetId() {
            return Set.of(this.targetId);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TextureSampler(String samplerName, Identifier location, int width, int height, boolean bilinear) implements Input
    {
        public static final Codec<TextureSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("sampler_name")).forGetter(TextureSampler::samplerName), ((MapCodec)Identifier.CODEC.fieldOf("location")).forGetter(TextureSampler::location), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("width")).forGetter(TextureSampler::width), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("height")).forGetter(TextureSampler::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TextureSampler::bilinear)).apply((Applicative<TextureSampler, ?>)instance, TextureSampler::new));

        @Override
        public Set<Identifier> getTargetId() {
            return Set.of();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static sealed interface Input
    permits TextureSampler, TargetSampler {
        public static final Codec<Input> CODEC = Codec.xor(TextureSampler.CODEC, TargetSampler.CODEC).xmap(either -> (Input)either.map(Function.identity(), Function.identity()), sampler -> {
            Input input = sampler;
            Objects.requireNonNull(input);
            Input lv = input;
            int i = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{TextureSampler.class, TargetSampler.class}, (Object)lv, i)) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    TextureSampler lv2 = (TextureSampler)lv;
                    yield Either.left(lv2);
                }
                case 1 -> {
                    TargetSampler lv3 = (TargetSampler)lv;
                    yield Either.right(lv3);
                }
            };
        });

        public String samplerName();

        public Set<Identifier> getTargetId();
    }

    @Environment(value=EnvType.CLIENT)
    public record CustomSized(int width, int height) implements Targets
    {
        public static final Codec<CustomSized> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.POSITIVE_INT.fieldOf("width")).forGetter(CustomSized::width), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("height")).forGetter(CustomSized::height)).apply((Applicative<CustomSized, ?>)instance, CustomSized::new));
    }

    @Environment(value=EnvType.CLIENT)
    public record ScreenSized() implements Targets
    {
        public static final Codec<ScreenSized> CODEC = Codec.unit(ScreenSized::new);
    }
}

