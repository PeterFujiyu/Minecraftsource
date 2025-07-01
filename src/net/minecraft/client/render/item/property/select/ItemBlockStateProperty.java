/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ItemBlockStateProperty(String property) implements SelectProperty<String>
{
    public static final SelectProperty.Type<ItemBlockStateProperty, String> TYPE = SelectProperty.Type.create(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("block_state_property")).forGetter(ItemBlockStateProperty::property)).apply((Applicative<ItemBlockStateProperty, ?>)instance, ItemBlockStateProperty::new)), Codec.STRING);

    @Override
    @Nullable
    public String getValue(ItemStack arg, @Nullable ClientWorld arg2, @Nullable LivingEntity arg3, int i, ModelTransformationMode arg4) {
        BlockStateComponent lv = arg.get(DataComponentTypes.BLOCK_STATE);
        if (lv == null) {
            return null;
        }
        return lv.properties().get(this.property);
    }

    @Override
    public SelectProperty.Type<ItemBlockStateProperty, String> getType() {
        return TYPE;
    }

    @Override
    @Nullable
    public /* synthetic */ Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return this.getValue(stack, world, user, seed, modelTransformationMode);
    }
}

