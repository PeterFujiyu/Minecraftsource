/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecipeCraftedCriterion
extends AbstractCriterion<Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, RegistryKey<Recipe<?>> recipeKey, List<ItemStack> ingredients) {
        this.trigger(player, conditions -> conditions.matches(recipeKey, ingredients));
    }

    public record Conditions(Optional<LootContextPredicate> player, RegistryKey<Recipe<?>> recipeId, List<ItemPredicate> ingredients) implements AbstractCriterion.Conditions
    {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create((RecordCodecBuilder.Instance<O> instance) -> instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ((MapCodec)RegistryKey.createCodec(RegistryKeys.RECIPE).fieldOf("recipe_id")).forGetter(Conditions::recipeId), ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(Conditions::ingredients)).apply((Applicative<Conditions, ?>)instance, Conditions::new));

        public static AdvancementCriterion<Conditions> create(RegistryKey<Recipe<?>> recipeKey, List<ItemPredicate.Builder> ingredients) {
            return Criteria.RECIPE_CRAFTED.create(new Conditions(Optional.empty(), recipeKey, ingredients.stream().map(ItemPredicate.Builder::build).toList()));
        }

        public static AdvancementCriterion<Conditions> create(RegistryKey<Recipe<?>> recipeKey) {
            return Criteria.RECIPE_CRAFTED.create(new Conditions(Optional.empty(), recipeKey, List.of()));
        }

        public static AdvancementCriterion<Conditions> createCrafterRecipeCrafted(RegistryKey<Recipe<?>> recipeKey) {
            return Criteria.CRAFTER_RECIPE_CRAFTED.create(new Conditions(Optional.empty(), recipeKey, List.of()));
        }

        boolean matches(RegistryKey<Recipe<?>> recipeKey, List<ItemStack> ingredients) {
            if (recipeKey != this.recipeId) {
                return false;
            }
            ArrayList<ItemStack> list2 = new ArrayList<ItemStack>(ingredients);
            for (ItemPredicate lv : this.ingredients) {
                boolean bl = false;
                Iterator iterator = list2.iterator();
                while (iterator.hasNext()) {
                    if (!lv.test((ItemStack)iterator.next())) continue;
                    iterator.remove();
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
            return true;
        }
    }
}

