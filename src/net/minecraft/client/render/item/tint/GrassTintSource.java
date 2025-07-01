/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.tint;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.GrassColors;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record GrassTintSource(float temperature, float downfall) implements TintSource
{
    public static final MapCodec<GrassTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codecs.rangedInclusiveFloat(0.0f, 1.0f).fieldOf("temperature")).forGetter(GrassTintSource::temperature), ((MapCodec)Codecs.rangedInclusiveFloat(0.0f, 1.0f).fieldOf("downfall")).forGetter(GrassTintSource::downfall)).apply((Applicative<GrassTintSource, ?>)instance, GrassTintSource::new));

    public GrassTintSource() {
        this(0.5f, 1.0f);
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        return GrassColors.getColor(this.temperature, this.downfall);
    }

    public MapCodec<GrassTintSource> getCodec() {
        return CODEC;
    }
}

