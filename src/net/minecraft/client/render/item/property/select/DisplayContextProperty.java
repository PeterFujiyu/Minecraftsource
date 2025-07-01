/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DisplayContextProperty() implements SelectProperty<ModelTransformationMode>
{
    public static final SelectProperty.Type<DisplayContextProperty, ModelTransformationMode> TYPE = SelectProperty.Type.create(MapCodec.unit(new DisplayContextProperty()), ModelTransformationMode.CODEC);

    @Override
    public ModelTransformationMode getValue(ItemStack arg, @Nullable ClientWorld arg2, @Nullable LivingEntity arg3, int i, ModelTransformationMode arg4) {
        return arg4;
    }

    @Override
    public SelectProperty.Type<DisplayContextProperty, ModelTransformationMode> getType() {
        return TYPE;
    }

    @Override
    public /* synthetic */ Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return this.getValue(stack, world, user, seed, modelTransformationMode);
    }
}

