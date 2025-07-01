/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe.book;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.RecipeBookType;

public final class RecipeBookOptions {
    public static final PacketCodec<PacketByteBuf, RecipeBookOptions> PACKET_CODEC = PacketCodec.of(RecipeBookOptions::toPacket, RecipeBookOptions::fromPacket);
    private static final Map<RecipeBookType, Pair<String, String>> CATEGORY_OPTION_NAMES = ImmutableMap.of(RecipeBookType.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookType.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookType.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookType.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
    private final Map<RecipeBookType, CategoryOption> categoryOptions;

    private RecipeBookOptions(Map<RecipeBookType, CategoryOption> categoryOptions) {
        this.categoryOptions = categoryOptions;
    }

    public RecipeBookOptions() {
        this(new EnumMap<RecipeBookType, CategoryOption>(RecipeBookType.class));
    }

    private CategoryOption getOption(RecipeBookType category) {
        return this.categoryOptions.getOrDefault((Object)category, CategoryOption.DEFAULT);
    }

    private void apply(RecipeBookType category, UnaryOperator<CategoryOption> modifier) {
        this.categoryOptions.compute(category, (key, value) -> {
            if (value == null) {
                value = CategoryOption.DEFAULT;
            }
            if ((value = (CategoryOption)modifier.apply((CategoryOption)value)).equals(CategoryOption.DEFAULT)) {
                value = null;
            }
            return value;
        });
    }

    public boolean isGuiOpen(RecipeBookType category) {
        return this.getOption((RecipeBookType)category).guiOpen;
    }

    public void setGuiOpen(RecipeBookType category, boolean open) {
        this.apply(category, option -> option.withGuiOpen(open));
    }

    public boolean isFilteringCraftable(RecipeBookType category) {
        return this.getOption((RecipeBookType)category).filteringCraftable;
    }

    public void setFilteringCraftable(RecipeBookType category, boolean filtering) {
        this.apply(category, option -> option.withFilteringCraftable(filtering));
    }

    private static RecipeBookOptions fromPacket(PacketByteBuf buf) {
        EnumMap<RecipeBookType, CategoryOption> map = new EnumMap<RecipeBookType, CategoryOption>(RecipeBookType.class);
        for (RecipeBookType lv : RecipeBookType.values()) {
            boolean bl = buf.readBoolean();
            boolean bl2 = buf.readBoolean();
            if (!bl && !bl2) continue;
            map.put(lv, new CategoryOption(bl, bl2));
        }
        return new RecipeBookOptions(map);
    }

    private void toPacket(PacketByteBuf buf) {
        for (RecipeBookType lv : RecipeBookType.values()) {
            CategoryOption lv2 = this.categoryOptions.getOrDefault((Object)lv, CategoryOption.DEFAULT);
            buf.writeBoolean(lv2.guiOpen);
            buf.writeBoolean(lv2.filteringCraftable);
        }
    }

    public static RecipeBookOptions fromNbt(NbtCompound nbt) {
        EnumMap<RecipeBookType, CategoryOption> map = new EnumMap<RecipeBookType, CategoryOption>(RecipeBookType.class);
        CATEGORY_OPTION_NAMES.forEach((category, pair) -> {
            boolean bl = nbt.getBoolean((String)pair.getFirst());
            boolean bl2 = nbt.getBoolean((String)pair.getSecond());
            if (bl || bl2) {
                map.put((RecipeBookType)((Object)category), new CategoryOption(bl, bl2));
            }
        });
        return new RecipeBookOptions(map);
    }

    public void writeNbt(NbtCompound nbt) {
        CATEGORY_OPTION_NAMES.forEach((category, pair) -> {
            CategoryOption lv = this.categoryOptions.getOrDefault(category, CategoryOption.DEFAULT);
            nbt.putBoolean((String)pair.getFirst(), lv.guiOpen);
            nbt.putBoolean((String)pair.getSecond(), lv.filteringCraftable);
        });
    }

    public RecipeBookOptions copy() {
        return new RecipeBookOptions(new EnumMap<RecipeBookType, CategoryOption>(this.categoryOptions));
    }

    public void copyFrom(RecipeBookOptions other) {
        this.categoryOptions.clear();
        this.categoryOptions.putAll(other.categoryOptions);
    }

    public boolean equals(Object o) {
        return this == o || o instanceof RecipeBookOptions && this.categoryOptions.equals(((RecipeBookOptions)o).categoryOptions);
    }

    public int hashCode() {
        return this.categoryOptions.hashCode();
    }

    record CategoryOption(boolean guiOpen, boolean filteringCraftable) {
        public static final CategoryOption DEFAULT = new CategoryOption(false, false);

        @Override
        public String toString() {
            return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + "]";
        }

        public CategoryOption withGuiOpen(boolean guiOpen) {
            return new CategoryOption(guiOpen, this.filteringCraftable);
        }

        public CategoryOption withFilteringCraftable(boolean filteringCraftable) {
            return new CategoryOption(this.guiOpen, filteringCraftable);
        }
    }
}

