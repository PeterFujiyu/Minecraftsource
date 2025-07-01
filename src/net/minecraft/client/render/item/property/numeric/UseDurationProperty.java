/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record UseDurationProperty(boolean remaining) implements NumericProperty
{
    public static final MapCodec<UseDurationProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.optionalFieldOf("remaining", false).forGetter(UseDurationProperty::remaining)).apply((Applicative<UseDurationProperty, ?>)instance, UseDurationProperty::new));

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (holder == null || holder.getActiveItem() != stack) {
            return 0.0f;
        }
        return this.remaining ? (float)holder.getItemUseTimeLeft() : (float)UseDurationProperty.getTicksUsedSoFar(stack, holder);
    }

    public MapCodec<UseDurationProperty> getCodec() {
        return CODEC;
    }

    public static int getTicksUsedSoFar(ItemStack stack, LivingEntity user) {
        return stack.getMaxUseTime(user) - user.getItemUseTimeLeft();
    }
}

