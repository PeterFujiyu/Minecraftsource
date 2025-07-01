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
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DyeTintSource(int defaultColor) implements TintSource
{
    public static final MapCodec<DyeTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codecs.RGB.fieldOf("default")).forGetter(DyeTintSource::defaultColor)).apply((Applicative<DyeTintSource, ?>)instance, DyeTintSource::new));

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        return DyedColorComponent.getColor(stack, this.defaultColor);
    }

    public MapCodec<DyeTintSource> getCodec() {
        return CODEC;
    }
}

