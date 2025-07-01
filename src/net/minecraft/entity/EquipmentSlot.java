/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum EquipmentSlot implements StringIdentifiable
{
    MAINHAND(Type.HAND, 0, 0, "mainhand"),
    OFFHAND(Type.HAND, 1, 5, "offhand"),
    FEET(Type.HUMANOID_ARMOR, 0, 1, 1, "feet"),
    LEGS(Type.HUMANOID_ARMOR, 1, 1, 2, "legs"),
    CHEST(Type.HUMANOID_ARMOR, 2, 1, 3, "chest"),
    HEAD(Type.HUMANOID_ARMOR, 3, 1, 4, "head"),
    BODY(Type.ANIMAL_ARMOR, 0, 1, 6, "body");

    public static final int NO_MAX_COUNT = 0;
    public static final List<EquipmentSlot> VALUES;
    public static final IntFunction<EquipmentSlot> FROM_INDEX;
    public static final StringIdentifiable.EnumCodec<EquipmentSlot> CODEC;
    public static final PacketCodec<ByteBuf, EquipmentSlot> PACKET_CODEC;
    private final Type type;
    private final int entityId;
    private final int maxCount;
    private final int index;
    private final String name;

    private EquipmentSlot(Type type, int entityId, int maxCount, int index, String name) {
        this.type = type;
        this.entityId = entityId;
        this.maxCount = maxCount;
        this.index = index;
        this.name = name;
    }

    private EquipmentSlot(Type type, int entityId, int index, String name) {
        this(type, entityId, 0, index, name);
    }

    public Type getType() {
        return this.type;
    }

    public int getEntitySlotId() {
        return this.entityId;
    }

    public int getOffsetEntitySlotId(int offset) {
        return offset + this.entityId;
    }

    public ItemStack split(ItemStack stack) {
        return this.maxCount > 0 ? stack.split(this.maxCount) : stack;
    }

    public int getIndex() {
        return this.index;
    }

    public int getOffsetIndex(int offset) {
        return this.index + offset;
    }

    public String getName() {
        return this.name;
    }

    public boolean isArmorSlot() {
        return this.type == Type.HUMANOID_ARMOR || this.type == Type.ANIMAL_ARMOR;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static EquipmentSlot byName(String name) {
        EquipmentSlot lv = CODEC.byId(name);
        if (lv != null) {
            return lv;
        }
        throw new IllegalArgumentException("Invalid slot '" + name + "'");
    }

    static {
        VALUES = List.of(EquipmentSlot.values());
        FROM_INDEX = ValueLists.createIdToValueFunction(slot -> slot.index, EquipmentSlot.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(EquipmentSlot::values);
        PACKET_CODEC = PacketCodecs.indexed(FROM_INDEX, slot -> slot.index);
    }

    public static enum Type {
        HAND,
        HUMANOID_ARMOR,
        ANIMAL_ARMOR;

    }
}

