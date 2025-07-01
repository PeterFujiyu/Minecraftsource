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
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record FishingRodCastProperty() implements BooleanProperty
{
    public static final MapCodec<FishingRodCastProperty> CODEC = MapCodec.unit(new FishingRodCastProperty());

    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        if (user instanceof PlayerEntity) {
            PlayerEntity lv = (PlayerEntity)user;
            if (lv.fishHook != null) {
                Arm lv2 = FishingBobberEntityRenderer.getArmHoldingRod(lv);
                return user.getStackInArm(lv2) == stack;
            }
        }
        return false;
    }

    public MapCodec<FishingRodCastProperty> getCodec() {
        return CODEC;
    }
}

