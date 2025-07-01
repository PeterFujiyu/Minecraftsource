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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CustomModelDataTintSource(int index, int defaultColor) implements TintSource
{
    public static final MapCodec<CustomModelDataTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataTintSource::index), ((MapCodec)Codecs.RGB.fieldOf("default")).forGetter(CustomModelDataTintSource::defaultColor)).apply((Applicative<CustomModelDataTintSource, ?>)instance, CustomModelDataTintSource::new));

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        Integer integer;
        CustomModelDataComponent lv = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (lv != null && (integer = lv.getColor(this.index)) != null) {
            return ColorHelper.fullAlpha(integer);
        }
        return ColorHelper.fullAlpha(this.defaultColor);
    }

    public MapCodec<CustomModelDataTintSource> getCodec() {
        return CODEC;
    }
}

