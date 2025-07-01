/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CarriedProperty() implements BooleanProperty
{
    public static final MapCodec<CarriedProperty> CODEC = MapCodec.unit(new CarriedProperty());

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        if (!(user instanceof ClientPlayerEntity)) return false;
        ClientPlayerEntity lv = (ClientPlayerEntity)user;
        if (lv.currentScreenHandler.getCursorStack() != stack) return false;
        return true;
    }

    public MapCodec<CarriedProperty> getCodec() {
        return CODEC;
    }
}

