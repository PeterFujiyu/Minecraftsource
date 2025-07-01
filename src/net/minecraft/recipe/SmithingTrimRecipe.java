/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SmithingRecipeDisplay;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class SmithingTrimRecipe
implements SmithingRecipe {
    final Optional<Ingredient> template;
    final Optional<Ingredient> base;
    final Optional<Ingredient> addition;
    @Nullable
    private IngredientPlacement ingredientPlacement;

    public SmithingTrimRecipe(Optional<Ingredient> template, Optional<Ingredient> base, Optional<Ingredient> addition) {
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public ItemStack craft(SmithingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        return SmithingTrimRecipe.craft(arg2, arg.base(), arg.addition(), arg.template());
    }

    public static ItemStack craft(RegistryWrapper.WrapperLookup registries, ItemStack base, ItemStack addition, ItemStack template) {
        Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional = ArmorTrimMaterials.get(registries, addition);
        Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional2 = ArmorTrimPatterns.get(registries, template);
        if (optional.isPresent() && optional2.isPresent()) {
            ArmorTrim lv = base.get(DataComponentTypes.TRIM);
            if (lv != null && lv.equals((RegistryEntry<ArmorTrimPattern>)optional2.get(), (RegistryEntry<ArmorTrimMaterial>)optional.get())) {
                return ItemStack.EMPTY;
            }
            ItemStack lv2 = base.copyWithCount(1);
            lv2.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional.get(), (RegistryEntry<ArmorTrimPattern>)optional2.get()));
            return lv2;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<Ingredient> template() {
        return this.template;
    }

    @Override
    public Optional<Ingredient> base() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> addition() {
        return this.addition;
    }

    @Override
    public RecipeSerializer<SmithingTrimRecipe> getSerializer() {
        return RecipeSerializer.SMITHING_TRIM;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(this.template, this.base, this.addition));
        }
        return this.ingredientPlacement;
    }

    @Override
    public List<RecipeDisplay> getDisplays() {
        SlotDisplay lv = Ingredient.toDisplay(this.base);
        SlotDisplay lv2 = Ingredient.toDisplay(this.addition);
        SlotDisplay lv3 = Ingredient.toDisplay(this.template);
        return List.of(new SmithingRecipeDisplay(lv3, lv, lv2, new SlotDisplay.SmithingTrimSlotDisplay(lv, lv2, lv3), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
    }

    public static class Serializer
    implements RecipeSerializer<SmithingTrimRecipe> {
        private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Ingredient.CODEC.optionalFieldOf("template").forGetter(recipe -> recipe.template), Ingredient.CODEC.optionalFieldOf("base").forGetter(recipe -> recipe.base), Ingredient.CODEC.optionalFieldOf("addition").forGetter(recipe -> recipe.addition)).apply((Applicative<SmithingTrimRecipe, ?>)instance, SmithingTrimRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SmithingTrimRecipe> PACKET_CODEC = PacketCodec.tuple(Ingredient.OPTIONAL_PACKET_CODEC, recipe -> recipe.template, Ingredient.OPTIONAL_PACKET_CODEC, recipe -> recipe.base, Ingredient.OPTIONAL_PACKET_CODEC, recipe -> recipe.addition, SmithingTrimRecipe::new);

        @Override
        public MapCodec<SmithingTrimRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SmithingTrimRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}

