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
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record PotionTintSource(int defaultColor) implements TintSource
{
    public static final MapCodec<PotionTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codecs.RGB.fieldOf("default")).forGetter(PotionTintSource::defaultColor)).apply((Applicative<PotionTintSource, ?>)instance, PotionTintSource::new));

    public PotionTintSource() {
        this(-13083194);
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        PotionContentsComponent lv = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (lv != null) {
            return ColorHelper.fullAlpha(lv.getColor(this.defaultColor));
        }
        return ColorHelper.fullAlpha(this.defaultColor);
    }

    public MapCodec<PotionTintSource> getCodec() {
        return CODEC;
    }
}

