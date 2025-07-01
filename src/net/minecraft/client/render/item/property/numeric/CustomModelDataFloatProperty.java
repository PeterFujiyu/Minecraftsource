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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CustomModelDataFloatProperty(int index) implements NumericProperty
{
    public static final MapCodec<CustomModelDataFloatProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataFloatProperty::index)).apply((Applicative<CustomModelDataFloatProperty, ?>)instance, CustomModelDataFloatProperty::new));

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        Float float_;
        CustomModelDataComponent lv = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (lv != null && (float_ = lv.getFloat(this.index)) != null) {
            return float_.floatValue();
        }
        return 0.0f;
    }

    public MapCodec<CustomModelDataFloatProperty> getCodec() {
        return CODEC;
    }
}

