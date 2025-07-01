/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.Consumable;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.PlaySoundConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record ConsumableComponent(float consumeSeconds, UseAction useAction, RegistryEntry<SoundEvent> sound, boolean hasConsumeParticles, List<ConsumeEffect> onConsumeEffects) {
    public static final float DEFAULT_CONSUME_SECONDS = 1.6f;
    private static final int PARTICLES_AND_SOUND_TICK_INTERVAL = 4;
    private static final float PARTICLES_AND_SOUND_TICK_THRESHOLD = 0.21875f;
    public static final Codec<ConsumableComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("consume_seconds", Float.valueOf(1.6f)).forGetter(ConsumableComponent::consumeSeconds), UseAction.CODEC.optionalFieldOf("animation", UseAction.EAT).forGetter(ConsumableComponent::useAction), SoundEvent.ENTRY_CODEC.optionalFieldOf("sound", SoundEvents.ENTITY_GENERIC_EAT).forGetter(ConsumableComponent::sound), Codec.BOOL.optionalFieldOf("has_consume_particles", true).forGetter(ConsumableComponent::hasConsumeParticles), ConsumeEffect.CODEC.listOf().optionalFieldOf("on_consume_effects", List.of()).forGetter(ConsumableComponent::onConsumeEffects)).apply((Applicative<ConsumableComponent, ?>)instance, ConsumableComponent::new));
    public static final PacketCodec<RegistryByteBuf, ConsumableComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, ConsumableComponent::consumeSeconds, UseAction.PACKET_CODEC, ConsumableComponent::useAction, SoundEvent.ENTRY_PACKET_CODEC, ConsumableComponent::sound, PacketCodecs.BOOLEAN, ConsumableComponent::hasConsumeParticles, ConsumeEffect.PACKET_CODEC.collect(PacketCodecs.toList()), ConsumableComponent::onConsumeEffects, ConsumableComponent::new);

    public ActionResult consume(LivingEntity user, ItemStack stack, Hand hand) {
        boolean bl;
        if (!this.canConsume(user, stack)) {
            return ActionResult.FAIL;
        }
        boolean bl2 = bl = this.getConsumeTicks() > 0;
        if (bl) {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        }
        ItemStack lv = this.finishConsumption(user.getWorld(), user, stack);
        return ActionResult.CONSUME.withNewHandStack(lv);
    }

    public ItemStack finishConsumption(World world, LivingEntity user, ItemStack stack) {
        Random lv = user.getRandom();
        this.spawnParticlesAndPlaySound(lv, user, stack, 16);
        if (user instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv2 = (ServerPlayerEntity)user;
            lv2.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            Criteria.CONSUME_ITEM.trigger(lv2, stack);
        }
        stack.streamAll(Consumable.class).forEach(consumable -> consumable.onConsume(world, user, stack, this));
        if (!world.isClient) {
            this.onConsumeEffects.forEach(effect -> effect.onConsume(world, stack, user));
        }
        user.emitGameEvent(this.useAction == UseAction.DRINK ? GameEvent.DRINK : GameEvent.EAT);
        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    public boolean canConsume(LivingEntity user, ItemStack stack) {
        FoodComponent lv = stack.get(DataComponentTypes.FOOD);
        if (lv != null && user instanceof PlayerEntity) {
            PlayerEntity lv2 = (PlayerEntity)user;
            return lv2.canConsume(lv.canAlwaysEat());
        }
        return true;
    }

    public int getConsumeTicks() {
        return (int)(this.consumeSeconds * 20.0f);
    }

    public void spawnParticlesAndPlaySound(Random random, LivingEntity user, ItemStack stack, int particleCount) {
        SoundEvent soundEvent;
        float l;
        float f = random.nextBoolean() ? 0.5f : 1.0f;
        float g = random.nextTriangular(1.0f, 0.2f);
        float h = 0.5f;
        float j = MathHelper.nextBetween(random, 0.9f, 1.0f);
        float k = this.useAction == UseAction.DRINK ? 0.5f : f;
        float f2 = l = this.useAction == UseAction.DRINK ? j : g;
        if (this.hasConsumeParticles) {
            user.spawnItemParticles(stack, particleCount);
        }
        if (user instanceof ConsumableSoundProvider) {
            ConsumableSoundProvider lv = (ConsumableSoundProvider)((Object)user);
            soundEvent = lv.getConsumeSound(stack);
        } else {
            soundEvent = this.sound.value();
        }
        SoundEvent lv2 = soundEvent;
        user.playSound(lv2, k, l);
    }

    public boolean shouldSpawnParticlesAndPlaySounds(int remainingUseTicks) {
        int k;
        int j = this.getConsumeTicks() - remainingUseTicks;
        boolean bl = j > (k = (int)((float)this.getConsumeTicks() * 0.21875f));
        return bl && remainingUseTicks % 4 == 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static interface ConsumableSoundProvider {
        public SoundEvent getConsumeSound(ItemStack var1);
    }

    public static class Builder {
        private float consumeSeconds = 1.6f;
        private UseAction useAction = UseAction.EAT;
        private RegistryEntry<SoundEvent> sound = SoundEvents.ENTITY_GENERIC_EAT;
        private boolean consumeParticles = true;
        private final List<ConsumeEffect> consumeEffects = new ArrayList<ConsumeEffect>();

        Builder() {
        }

        public Builder consumeSeconds(float consumeSeconds) {
            this.consumeSeconds = consumeSeconds;
            return this;
        }

        public Builder useAction(UseAction useAction) {
            this.useAction = useAction;
            return this;
        }

        public Builder sound(RegistryEntry<SoundEvent> sound) {
            this.sound = sound;
            return this;
        }

        public Builder finishSound(RegistryEntry<SoundEvent> finishSound) {
            return this.consumeEffect(new PlaySoundConsumeEffect(finishSound));
        }

        public Builder consumeParticles(boolean consumeParticles) {
            this.consumeParticles = consumeParticles;
            return this;
        }

        public Builder consumeEffect(ConsumeEffect consumeEffect) {
            this.consumeEffects.add(consumeEffect);
            return this;
        }

        public ConsumableComponent build() {
            return new ConsumableComponent(this.consumeSeconds, this.useAction, this.sound, this.consumeParticles, this.consumeEffects);
        }
    }
}

