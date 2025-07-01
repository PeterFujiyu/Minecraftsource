/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GoatHornItem
extends Item {
    private final TagKey<Instrument> instrumentTag;

    public GoatHornItem(TagKey<Instrument> instrumentTag, Item.Settings settings) {
        super(settings);
        this.instrumentTag = instrumentTag;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        RegistryWrapper.WrapperLookup lv = context.getRegistryLookup();
        if (lv == null) {
            return;
        }
        Optional<RegistryEntry<Instrument>> optional = this.getInstrument(stack, lv);
        if (optional.isPresent()) {
            MutableText lv2 = optional.get().value().description().copy();
            Texts.setStyleIfAbsent(lv2, Style.EMPTY.withColor(Formatting.GRAY));
            tooltip.add(lv2);
        }
    }

    public static ItemStack getStackForInstrument(Item item, RegistryEntry<Instrument> instrument) {
        ItemStack lv = new ItemStack(item);
        lv.set(DataComponentTypes.INSTRUMENT, instrument);
        return lv;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        Optional<RegistryEntry<Instrument>> optional = this.getInstrument(lv, user.getRegistryManager());
        if (optional.isPresent()) {
            Instrument lv2 = optional.get().value();
            user.setCurrentHand(hand);
            GoatHornItem.playSound(world, user, lv2);
            user.getItemCooldownManager().set(lv, MathHelper.floor(lv2.useDuration() * 20.0f));
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return ActionResult.CONSUME;
        }
        return ActionResult.FAIL;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        Optional<RegistryEntry<Instrument>> optional = this.getInstrument(stack, user.getRegistryManager());
        return optional.map(instrument -> MathHelper.floor(((Instrument)instrument.value()).useDuration() * 20.0f)).orElse(0);
    }

    private Optional<RegistryEntry<Instrument>> getInstrument(ItemStack stack, RegistryWrapper.WrapperLookup registries) {
        Iterator iterator;
        RegistryEntry<Instrument> lv = stack.get(DataComponentTypes.INSTRUMENT);
        if (lv != null) {
            return Optional.of(lv);
        }
        Optional<RegistryEntryList.Named<Instrument>> optional = registries.getOrThrow(RegistryKeys.INSTRUMENT).getOptional(this.instrumentTag);
        if (optional.isPresent() && (iterator = optional.get().iterator()).hasNext()) {
            return Optional.of(iterator.next());
        }
        return Optional.empty();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    private static void playSound(World world, PlayerEntity player, Instrument instrument) {
        SoundEvent lv = instrument.soundEvent().value();
        float f = instrument.range() / 16.0f;
        world.playSoundFromEntity(player, player, lv, SoundCategory.RECORDS, f, 1.0f);
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.getPos(), GameEvent.Emitter.of(player));
    }
}

