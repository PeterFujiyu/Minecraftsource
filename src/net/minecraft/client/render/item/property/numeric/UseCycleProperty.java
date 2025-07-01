/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record UseCycleProperty(float period) implements NumericProperty
{
    public static final MapCodec<UseCycleProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codecs.POSITIVE_FLOAT.optionalFieldOf("period", Float.valueOf(1.0f)).forGetter(UseCycleProperty::period)).apply((Applicative<UseCycleProperty, ?>)instance, UseCycleProperty::new));

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (holder == null || holder.getActiveItem() != stack) {
            return 0.0f;
        }
        return (float)holder.getItemUseTimeLeft() % this.period;
    }

    public MapCodec<UseCycleProperty> getCodec() {
        return CODEC;
    }
}

