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
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DamageProperty(boolean normalize) implements NumericProperty
{
    public static final MapCodec<DamageProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.optionalFieldOf("normalize", true).forGetter(DamageProperty::normalize)).apply((Applicative<DamageProperty, ?>)instance, DamageProperty::new));

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        float f = stack.getDamage();
        float g = stack.getMaxDamage();
        if (this.normalize) {
            return MathHelper.clamp(f / g, 0.0f, 1.0f);
        }
        return MathHelper.clamp(f, 0.0f, g);
    }

    public MapCodec<DamageProperty> getCodec() {
        return CODEC;
    }
}

