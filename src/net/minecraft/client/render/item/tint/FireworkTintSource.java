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
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record FireworkTintSource(int defaultColor) implements TintSource
{
    public static final MapCodec<FireworkTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codecs.RGB.fieldOf("default")).forGetter(FireworkTintSource::defaultColor)).apply((Applicative<FireworkTintSource, ?>)instance, FireworkTintSource::new));

    public FireworkTintSource() {
        this(-7697782);
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        FireworkExplosionComponent lv = stack.get(DataComponentTypes.FIREWORK_EXPLOSION);
        IntList intList = lv != null ? lv.colors() : IntList.of();
        int i = intList.size();
        if (i == 0) {
            return this.defaultColor;
        }
        if (i == 1) {
            return ColorHelper.fullAlpha(intList.getInt(0));
        }
        int j = 0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < i; ++m) {
            int n = intList.getInt(m);
            j += ColorHelper.getRed(n);
            k += ColorHelper.getGreen(n);
            l += ColorHelper.getBlue(n);
        }
        return ColorHelper.getArgb(j / i, k / i, l / i);
    }

    public MapCodec<FireworkTintSource> getCodec() {
        return CODEC;
    }
}

