/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe.display;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.display.DisplayedItemFactory;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.random.Random;

public interface SlotDisplay {
    public static final Codec<SlotDisplay> CODEC = Registries.SLOT_DISPLAY.getCodec().dispatch(SlotDisplay::serializer, Serializer::codec);
    public static final PacketCodec<RegistryByteBuf, SlotDisplay> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.SLOT_DISPLAY).dispatch(SlotDisplay::serializer, Serializer::streamCodec);

    public <T> Stream<T> appendStacks(ContextParameterMap var1, DisplayedItemFactory<T> var2);

    public Serializer<? extends SlotDisplay> serializer();

    default public boolean isEnabled(FeatureSet features) {
        return true;
    }

    default public List<ItemStack> getStacks(ContextParameterMap parameters) {
        return this.appendStacks(parameters, NoopDisplayedItemFactory.INSTANCE).toList();
    }

    default public ItemStack getFirst(ContextParameterMap context) {
        return this.appendStacks(context, NoopDisplayedItemFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
    }

    public static class NoopDisplayedItemFactory
    implements DisplayedItemFactory.FromStack<ItemStack> {
        public static final NoopDisplayedItemFactory INSTANCE = new NoopDisplayedItemFactory();

        @Override
        public ItemStack toDisplayed(ItemStack arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object toDisplayed(ItemStack stack) {
            return this.toDisplayed(stack);
        }
    }

    public record WithRemainderSlotDisplay(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay
    {
        public static final MapCodec<WithRemainderSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)CODEC.fieldOf("input")).forGetter(WithRemainderSlotDisplay::input), ((MapCodec)CODEC.fieldOf("remainder")).forGetter(WithRemainderSlotDisplay::remainder)).apply((Applicative<WithRemainderSlotDisplay, ?>)instance, WithRemainderSlotDisplay::new));
        public static final PacketCodec<RegistryByteBuf, WithRemainderSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PACKET_CODEC, WithRemainderSlotDisplay::input, PACKET_CODEC, WithRemainderSlotDisplay::remainder, WithRemainderSlotDisplay::new);
        public static final Serializer<WithRemainderSlotDisplay> SERIALIZER = new Serializer<WithRemainderSlotDisplay>(CODEC, PACKET_CODEC);

        public Serializer<WithRemainderSlotDisplay> serializer() {
            return SERIALIZER;
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            if (factory instanceof DisplayedItemFactory.FromRemainder) {
                DisplayedItemFactory.FromRemainder lv = (DisplayedItemFactory.FromRemainder)factory;
                List list = this.remainder.appendStacks(parameters, factory).toList();
                return this.input.appendStacks(parameters, factory).map(input -> lv.toDisplayed(input, list));
            }
            return this.input.appendStacks(parameters, factory);
        }

        @Override
        public boolean isEnabled(FeatureSet features) {
            return this.input.isEnabled(features) && this.remainder.isEnabled(features);
        }
    }

    public record CompositeSlotDisplay(List<SlotDisplay> contents) implements SlotDisplay
    {
        public static final MapCodec<CompositeSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)CODEC.listOf().fieldOf("contents")).forGetter(CompositeSlotDisplay::contents)).apply((Applicative<CompositeSlotDisplay, ?>)instance, CompositeSlotDisplay::new));
        public static final PacketCodec<RegistryByteBuf, CompositeSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PACKET_CODEC.collect(PacketCodecs.toList()), CompositeSlotDisplay::contents, CompositeSlotDisplay::new);
        public static final Serializer<CompositeSlotDisplay> SERIALIZER = new Serializer<CompositeSlotDisplay>(CODEC, PACKET_CODEC);

        public Serializer<CompositeSlotDisplay> serializer() {
            return SERIALIZER;
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            return this.contents.stream().flatMap(display -> display.appendStacks(parameters, factory));
        }

        @Override
        public boolean isEnabled(FeatureSet features) {
            return this.contents.stream().allMatch(child -> child.isEnabled(features));
        }
    }

    public record TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay
    {
        public static final MapCodec<TagSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag")).forGetter(TagSlotDisplay::tag)).apply((Applicative<TagSlotDisplay, ?>)instance, TagSlotDisplay::new));
        public static final PacketCodec<RegistryByteBuf, TagSlotDisplay> PACKET_CODEC = PacketCodec.tuple(TagKey.packetCodec(RegistryKeys.ITEM), TagSlotDisplay::tag, TagSlotDisplay::new);
        public static final Serializer<TagSlotDisplay> SERIALIZER = new Serializer<TagSlotDisplay>(CODEC, PACKET_CODEC);

        public Serializer<TagSlotDisplay> serializer() {
            return SERIALIZER;
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            if (factory instanceof DisplayedItemFactory.FromStack) {
                DisplayedItemFactory.FromStack lv = (DisplayedItemFactory.FromStack)factory;
                RegistryWrapper.WrapperLookup lv2 = parameters.getNullable(SlotDisplayContexts.REGISTRIES);
                if (lv2 != null) {
                    return lv2.getOrThrow(RegistryKeys.ITEM).getOptional(this.tag).map(tag -> tag.stream().map(lv::toDisplayed)).stream().flatMap(values -> values);
                }
            }
            return Stream.empty();
        }
    }

    public record StackSlotDisplay(ItemStack stack) implements SlotDisplay
    {
        public static final MapCodec<StackSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)ItemStack.VALIDATED_CODEC.fieldOf("item")).forGetter(StackSlotDisplay::stack)).apply((Applicative<StackSlotDisplay, ?>)instance, StackSlotDisplay::new));
        public static final PacketCodec<RegistryByteBuf, StackSlotDisplay> PACKET_CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, StackSlotDisplay::stack, StackSlotDisplay::new);
        public static final Serializer<StackSlotDisplay> SERIALIZER = new Serializer<StackSlotDisplay>(CODEC, PACKET_CODEC);

        public Serializer<StackSlotDisplay> serializer() {
            return SERIALIZER;
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            if (factory instanceof DisplayedItemFactory.FromStack) {
                DisplayedItemFactory.FromStack lv = (DisplayedItemFactory.FromStack)factory;
                return Stream.of(lv.toDisplayed(this.stack));
            }
            return Stream.empty();
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StackSlotDisplay)) return false;
            StackSlotDisplay lv = (StackSlotDisplay)o;
            if (!ItemStack.areEqual(this.stack, lv.stack)) return false;
            return true;
        }

        @Override
        public boolean isEnabled(FeatureSet features) {
            return this.stack.getItem().isEnabled(features);
        }
    }

    public record ItemSlotDisplay(RegistryEntry<Item> item) implements SlotDisplay
    {
        public static final MapCodec<ItemSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Item.ENTRY_CODEC.fieldOf("item")).forGetter(ItemSlotDisplay::item)).apply((Applicative<ItemSlotDisplay, ?>)instance, ItemSlotDisplay::new));
        public static final PacketCodec<RegistryByteBuf, ItemSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntry(RegistryKeys.ITEM), ItemSlotDisplay::item, ItemSlotDisplay::new);
        public static final Serializer<ItemSlotDisplay> SERIALIZER = new Serializer<ItemSlotDisplay>(CODEC, PACKET_CODEC);

        public ItemSlotDisplay(Item item) {
            this(item.getRegistryEntry());
        }

        public Serializer<ItemSlotDisplay> serializer() {
            return SERIALIZER;
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            if (factory instanceof DisplayedItemFactory.FromStack) {
                DisplayedItemFactory.FromStack lv = (DisplayedItemFactory.FromStack)factory;
                return Stream.of(lv.toDisplayed(this.item));
            }
            return Stream.empty();
        }

        @Override
        public boolean isEnabled(FeatureSet features) {
            return this.item.value().isEnabled(features);
        }
    }

    public record SmithingTrimSlotDisplay(SlotDisplay base, SlotDisplay material, SlotDisplay pattern) implements SlotDisplay
    {
        public static final MapCodec<SmithingTrimSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)CODEC.fieldOf("base")).forGetter(SmithingTrimSlotDisplay::base), ((MapCodec)CODEC.fieldOf("material")).forGetter(SmithingTrimSlotDisplay::material), ((MapCodec)CODEC.fieldOf("pattern")).forGetter(SmithingTrimSlotDisplay::pattern)).apply((Applicative<SmithingTrimSlotDisplay, ?>)instance, SmithingTrimSlotDisplay::new));
        public static final PacketCodec<RegistryByteBuf, SmithingTrimSlotDisplay> PACKET_CODEC = PacketCodec.tuple(PACKET_CODEC, SmithingTrimSlotDisplay::base, PACKET_CODEC, SmithingTrimSlotDisplay::material, PACKET_CODEC, SmithingTrimSlotDisplay::pattern, SmithingTrimSlotDisplay::new);
        public static final Serializer<SmithingTrimSlotDisplay> SERIALIZER = new Serializer<SmithingTrimSlotDisplay>(CODEC, PACKET_CODEC);

        public Serializer<SmithingTrimSlotDisplay> serializer() {
            return SERIALIZER;
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            if (factory instanceof DisplayedItemFactory.FromStack) {
                DisplayedItemFactory.FromStack lv = (DisplayedItemFactory.FromStack)factory;
                RegistryWrapper.WrapperLookup lv2 = parameters.getNullable(SlotDisplayContexts.REGISTRIES);
                if (lv2 != null) {
                    Random lv3 = Random.create(System.identityHashCode(this));
                    List<ItemStack> list = this.base.getStacks(parameters);
                    if (list.isEmpty()) {
                        return Stream.empty();
                    }
                    List<ItemStack> list2 = this.material.getStacks(parameters);
                    if (list2.isEmpty()) {
                        return Stream.empty();
                    }
                    List<ItemStack> list3 = this.pattern.getStacks(parameters);
                    if (list3.isEmpty()) {
                        return Stream.empty();
                    }
                    return Stream.generate(() -> {
                        ItemStack lv = (ItemStack)Util.getRandom(list, lv3);
                        ItemStack lv2 = (ItemStack)Util.getRandom(list2, lv3);
                        ItemStack lv3 = (ItemStack)Util.getRandom(list3, lv3);
                        return SmithingTrimRecipe.craft(lv2, lv, lv2, lv3);
                    }).limit(256L).filter(stack -> !stack.isEmpty()).limit(16L).map(lv::toDisplayed);
                }
            }
            return Stream.empty();
        }
    }

    public static class AnyFuelSlotDisplay
    implements SlotDisplay {
        public static final AnyFuelSlotDisplay INSTANCE = new AnyFuelSlotDisplay();
        public static final MapCodec<AnyFuelSlotDisplay> CODEC = MapCodec.unit(INSTANCE);
        public static final PacketCodec<RegistryByteBuf, AnyFuelSlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
        public static final Serializer<AnyFuelSlotDisplay> SERIALIZER = new Serializer<AnyFuelSlotDisplay>(CODEC, PACKET_CODEC);

        private AnyFuelSlotDisplay() {
        }

        public Serializer<AnyFuelSlotDisplay> serializer() {
            return SERIALIZER;
        }

        public String toString() {
            return "<any fuel>";
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            if (factory instanceof DisplayedItemFactory.FromStack) {
                DisplayedItemFactory.FromStack lv = (DisplayedItemFactory.FromStack)factory;
                FuelRegistry lv2 = parameters.getNullable(SlotDisplayContexts.FUEL_REGISTRY);
                if (lv2 != null) {
                    return lv2.getFuelItems().stream().map(lv::toDisplayed);
                }
            }
            return Stream.empty();
        }
    }

    public static class EmptySlotDisplay
    implements SlotDisplay {
        public static final EmptySlotDisplay INSTANCE = new EmptySlotDisplay();
        public static final MapCodec<EmptySlotDisplay> CODEC = MapCodec.unit(INSTANCE);
        public static final PacketCodec<RegistryByteBuf, EmptySlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
        public static final Serializer<EmptySlotDisplay> SERIALIZER = new Serializer<EmptySlotDisplay>(CODEC, PACKET_CODEC);

        private EmptySlotDisplay() {
        }

        public Serializer<EmptySlotDisplay> serializer() {
            return SERIALIZER;
        }

        public String toString() {
            return "<empty>";
        }

        @Override
        public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
            return Stream.empty();
        }
    }

    public record Serializer<T extends SlotDisplay>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
    }
}

