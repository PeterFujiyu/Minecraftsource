/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item.consume;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record TeleportRandomlyConsumeEffect(float diameter) implements ConsumeEffect
{
    private static final float DEFAULT_DIAMETER = 16.0f;
    public static final MapCodec<TeleportRandomlyConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codecs.POSITIVE_FLOAT.optionalFieldOf("diameter", Float.valueOf(16.0f)).forGetter(TeleportRandomlyConsumeEffect::diameter)).apply((Applicative<TeleportRandomlyConsumeEffect, ?>)instance, TeleportRandomlyConsumeEffect::new));
    public static final PacketCodec<RegistryByteBuf, TeleportRandomlyConsumeEffect> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, TeleportRandomlyConsumeEffect::diameter, TeleportRandomlyConsumeEffect::new);

    public TeleportRandomlyConsumeEffect() {
        this(16.0f);
    }

    public ConsumeEffect.Type<TeleportRandomlyConsumeEffect> getType() {
        return ConsumeEffect.Type.TELEPORT_RANDOMLY;
    }

    @Override
    public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
        boolean bl = false;
        for (int i = 0; i < 16; ++i) {
            SoundCategory lv3;
            SoundEvent lv2;
            double d = user.getX() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter;
            double e = MathHelper.clamp(user.getY() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter, (double)world.getBottomY(), (double)(world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1));
            double f = user.getZ() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter;
            if (user.hasVehicle()) {
                user.stopRiding();
            }
            Vec3d lv = user.getPos();
            if (!user.teleport(d, e, f, true)) continue;
            world.emitGameEvent(GameEvent.TELEPORT, lv, GameEvent.Emitter.of(user));
            if (user instanceof FoxEntity) {
                lv2 = SoundEvents.ENTITY_FOX_TELEPORT;
                lv3 = SoundCategory.NEUTRAL;
            } else {
                lv2 = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                lv3 = SoundCategory.PLAYERS;
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(), lv2, lv3);
            user.onLanding();
            bl = true;
            break;
        }
        if (bl && user instanceof PlayerEntity) {
            PlayerEntity lv4 = (PlayerEntity)user;
            lv4.clearCurrentExplosion();
        }
        return bl;
    }
}

