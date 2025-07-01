/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.enchantment.effect.entity;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record ChangeItemDamageEnchantmentEffect(EnchantmentLevelBasedValue amount) implements EnchantmentEntityEffect
{
    public static final MapCodec<ChangeItemDamageEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)EnchantmentLevelBasedValue.CODEC.fieldOf("amount")).forGetter(arg -> arg.amount)).apply((Applicative<ChangeItemDamageEnchantmentEffect, ?>)instance, ChangeItemDamageEnchantmentEffect::new));

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        ItemStack lv = context.stack();
        if (lv.contains(DataComponentTypes.MAX_DAMAGE) && lv.contains(DataComponentTypes.DAMAGE)) {
            ServerPlayerEntity lv2;
            LivingEntity livingEntity = context.owner();
            ServerPlayerEntity lv3 = livingEntity instanceof ServerPlayerEntity ? (lv2 = (ServerPlayerEntity)livingEntity) : null;
            int j = (int)this.amount.getValue(level);
            lv.damage(j, world, lv3, context.breakCallback());
        }
    }

    public MapCodec<ChangeItemDamageEnchantmentEffect> getCodec() {
        return CODEC;
    }
}

