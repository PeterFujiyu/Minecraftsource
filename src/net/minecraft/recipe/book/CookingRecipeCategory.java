/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe.book;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum CookingRecipeCategory implements StringIdentifiable
{
    FOOD(0, "food"),
    BLOCKS(1, "blocks"),
    MISC(2, "misc");

    private static final IntFunction<CookingRecipeCategory> BY_ID;
    public static final Codec<CookingRecipeCategory> CODEC;
    public static final PacketCodec<ByteBuf, CookingRecipeCategory> PACKET_CODEC;
    private final int id;
    private final String name;

    private CookingRecipeCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(category -> category.id, CookingRecipeCategory.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(CookingRecipeCategory::values);
        PACKET_CODEC = PacketCodecs.indexed(BY_ID, category -> category.id);
    }
}

