/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.state.property;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public final class EnumProperty<T extends Enum<T>>
extends Property<T> {
    private final List<T> values;
    private final Map<String, T> byName;
    private final int[] enumOrdinalToPropertyOrdinal;

    private EnumProperty(String name, Class<T> type, List<T> values) {
        super(name, type);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Trying to make empty EnumProperty '" + name + "'");
        }
        this.values = List.copyOf(values);
        Enum[] enums = (Enum[])type.getEnumConstants();
        this.enumOrdinalToPropertyOrdinal = new int[enums.length];
        for (Enum enum_ : enums) {
            this.enumOrdinalToPropertyOrdinal[enum_.ordinal()] = values.indexOf(enum_);
        }
        ImmutableMap.Builder<String, Enum> builder = ImmutableMap.builder();
        for (Enum enum2 : values) {
            String string2 = ((StringIdentifiable)((Object)enum2)).asString();
            builder.put(string2, enum2);
        }
        this.byName = builder.buildOrThrow();
    }

    @Override
    public List<T> getValues() {
        return this.values;
    }

    @Override
    public Optional<T> parse(String name) {
        return Optional.ofNullable((Enum)this.byName.get(name));
    }

    @Override
    public String name(T enum_) {
        return ((StringIdentifiable)enum_).asString();
    }

    @Override
    public int ordinal(T enum_) {
        return this.enumOrdinalToPropertyOrdinal[((Enum)enum_).ordinal()];
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof EnumProperty) {
            EnumProperty lv = (EnumProperty)object;
            if (super.equals(object)) {
                return this.values.equals(lv.values);
            }
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int i = super.computeHashCode();
        i = 31 * i + this.values.hashCode();
        return i;
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type) {
        return EnumProperty.of(name, type, (T enum_) -> true);
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type, Predicate<T> filter) {
        return EnumProperty.of(name, type, Arrays.stream((Enum[])type.getEnumConstants()).filter(filter).collect(Collectors.toList()));
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type, T ... values) {
        return EnumProperty.of(name, type, List.of(values));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String name, Class<T> type, List<T> values) {
        return new EnumProperty<T>(name, type, values);
    }

    @Override
    public /* synthetic */ int ordinal(Comparable value) {
        return this.ordinal((Enum)((Object)value));
    }

    @Override
    public /* synthetic */ String name(Comparable value) {
        return this.name((Enum)((Object)value));
    }
}

