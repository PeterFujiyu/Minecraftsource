/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ShaderProgramDefinition(Identifier vertex, Identifier fragment, List<Sampler> samplers, List<Uniform> uniforms, Defines defines) {
    public static final Codec<ShaderProgramDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("vertex")).forGetter(ShaderProgramDefinition::vertex), ((MapCodec)Identifier.CODEC.fieldOf("fragment")).forGetter(ShaderProgramDefinition::fragment), Sampler.CODEC.listOf().optionalFieldOf("samplers", List.of()).forGetter(ShaderProgramDefinition::samplers), Uniform.CODEC.listOf().optionalFieldOf("uniforms", List.of()).forGetter(ShaderProgramDefinition::uniforms), Defines.CODEC.optionalFieldOf("defines", Defines.EMPTY).forGetter(ShaderProgramDefinition::defines)).apply((Applicative<ShaderProgramDefinition, ?>)instance, ShaderProgramDefinition::new));

    @Environment(value=EnvType.CLIENT)
    public record Sampler(String name) {
        public static final Codec<Sampler> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("name")).forGetter(Sampler::name)).apply((Applicative<Sampler, ?>)instance, Sampler::new));
    }

    @Environment(value=EnvType.CLIENT)
    public record Uniform(String name, String type, int count, List<Float> values) {
        public static final Codec<Uniform> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("name")).forGetter(Uniform::name), ((MapCodec)Codec.STRING.fieldOf("type")).forGetter(Uniform::type), ((MapCodec)Codec.INT.fieldOf("count")).forGetter(Uniform::count), ((MapCodec)Codec.FLOAT.listOf().fieldOf("values")).forGetter(Uniform::values)).apply((Applicative<Uniform, ?>)instance, Uniform::new)).validate(Uniform::validate);

        private static DataResult<Uniform> validate(Uniform uniform) {
            int i = uniform.count;
            int j = uniform.values.size();
            if (j != i && j > 1) {
                return DataResult.error(() -> "Invalid amount of uniform values specified (expected " + i + ", found " + j + ")");
            }
            return DataResult.success(uniform);
        }
    }
}

