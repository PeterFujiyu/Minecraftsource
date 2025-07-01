/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SingleStackRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerRecipeManager
extends SinglePreparationResourceReloader<PreparedRecipes>
implements RecipeManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<RegistryKey<RecipePropertySet>, SoleIngredientGetter> SOLE_INGREDIENT_GETTERS = Map.of(RecipePropertySet.SMITHING_ADDITION, recipe -> {
        Optional<Object> optional;
        if (recipe instanceof SmithingRecipe) {
            SmithingRecipe lv = (SmithingRecipe)recipe;
            optional = lv.addition();
        } else {
            optional = Optional.empty();
        }
        return optional;
    }, RecipePropertySet.SMITHING_BASE, recipe -> {
        Optional<Object> optional;
        if (recipe instanceof SmithingRecipe) {
            SmithingRecipe lv = (SmithingRecipe)recipe;
            optional = lv.base();
        } else {
            optional = Optional.empty();
        }
        return optional;
    }, RecipePropertySet.SMITHING_TEMPLATE, recipe -> {
        Optional<Object> optional;
        if (recipe instanceof SmithingRecipe) {
            SmithingRecipe lv = (SmithingRecipe)recipe;
            optional = lv.template();
        } else {
            optional = Optional.empty();
        }
        return optional;
    }, RecipePropertySet.FURNACE_INPUT, ServerRecipeManager.cookingIngredientGetter(RecipeType.SMELTING), RecipePropertySet.BLAST_FURNACE_INPUT, ServerRecipeManager.cookingIngredientGetter(RecipeType.BLASTING), RecipePropertySet.SMOKER_INPUT, ServerRecipeManager.cookingIngredientGetter(RecipeType.SMOKING), RecipePropertySet.CAMPFIRE_INPUT, ServerRecipeManager.cookingIngredientGetter(RecipeType.CAMPFIRE_COOKING));
    private static final ResourceFinder FINDER = ResourceFinder.json(RegistryKeys.RECIPE);
    private final RegistryWrapper.WrapperLookup registries;
    private PreparedRecipes preparedRecipes = PreparedRecipes.EMPTY;
    private Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets = Map.of();
    private CuttingRecipeDisplay.Grouping<StonecuttingRecipe> stonecutterRecipes = CuttingRecipeDisplay.Grouping.empty();
    private List<ServerRecipe> recipes = List.of();
    private Map<RegistryKey<Recipe<?>>, List<ServerRecipe>> recipesByKey = Map.of();

    public ServerRecipeManager(RegistryWrapper.WrapperLookup registries) {
        this.registries = registries;
    }

    @Override
    protected PreparedRecipes prepare(ResourceManager arg, Profiler arg2) {
        TreeMap<Identifier, Recipe> sortedMap = new TreeMap<Identifier, Recipe>();
        JsonDataLoader.load(arg, FINDER, this.registries.getOps(JsonOps.INSTANCE), Recipe.CODEC, sortedMap);
        ArrayList list = new ArrayList(sortedMap.size());
        sortedMap.forEach((id, recipe) -> {
            RegistryKey<Recipe<?>> lv = RegistryKey.of(RegistryKeys.RECIPE, id);
            RecipeEntry<Recipe> lv2 = new RecipeEntry<Recipe>(lv, (Recipe)recipe);
            list.add(lv2);
        });
        return PreparedRecipes.of(list);
    }

    @Override
    protected void apply(PreparedRecipes arg, ResourceManager arg2, Profiler arg3) {
        this.preparedRecipes = arg;
        LOGGER.info("Loaded {} recipes", (Object)arg.recipes().size());
    }

    public void initialize(FeatureSet features) {
        ArrayList list = new ArrayList();
        List<PropertySetBuilder> list2 = SOLE_INGREDIENT_GETTERS.entrySet().stream().map(entry -> new PropertySetBuilder((RegistryKey)entry.getKey(), (SoleIngredientGetter)entry.getValue())).toList();
        this.preparedRecipes.recipes().forEach(recipe -> {
            Object lv = recipe.value();
            if (!lv.isIgnoredInRecipeBook() && lv.getIngredientPlacement().hasNoPlacement()) {
                LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", (Object)recipe.id().getValue());
                return;
            }
            list2.forEach(builder -> builder.accept((Recipe<?>)lv));
            if (lv instanceof StonecuttingRecipe) {
                StonecuttingRecipe lv2 = (StonecuttingRecipe)lv;
                RecipeEntry lv3 = recipe;
                if (ServerRecipeManager.isEnabled(features, lv2.ingredient()) && lv2.createResultDisplay().isEnabled(features)) {
                    list.add(new CuttingRecipeDisplay.GroupEntry(lv2.ingredient(), new CuttingRecipeDisplay(lv2.createResultDisplay(), Optional.of(lv3))));
                }
            }
        });
        this.propertySets = list2.stream().collect(Collectors.toUnmodifiableMap(builder -> builder.propertySetKey, builder -> builder.build(features)));
        this.stonecutterRecipes = new CuttingRecipeDisplay.Grouping(list);
        this.recipes = ServerRecipeManager.collectServerRecipes(this.preparedRecipes.recipes(), features);
        this.recipesByKey = this.recipes.stream().collect(Collectors.groupingBy(recipe -> recipe.parent.id(), IdentityHashMap::new, Collectors.toList()));
    }

    static List<Ingredient> filterIngredients(FeatureSet features, List<Ingredient> ingredients) {
        ingredients.removeIf(ingredient -> !ServerRecipeManager.isEnabled(features, ingredient));
        return ingredients;
    }

    private static boolean isEnabled(FeatureSet features, Ingredient ingredient) {
        return ingredient.getMatchingItems().allMatch(entry -> ((Item)entry.value()).isEnabled(features));
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, I input, World world, @Nullable RegistryKey<Recipe<?>> recipe) {
        RecipeEntry<T> lv = recipe != null ? this.get(type, recipe) : null;
        return this.getFirstMatch(type, input, world, lv);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, I input, World world, @Nullable RecipeEntry<T> recipe) {
        if (recipe != null && recipe.value().matches(input, world)) {
            return Optional.of(recipe);
        }
        return this.getFirstMatch(type, input, world);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, I input, World world) {
        return this.preparedRecipes.find(type, input, world).findFirst();
    }

    public Optional<RecipeEntry<?>> get(RegistryKey<Recipe<?>> key) {
        return Optional.ofNullable(this.preparedRecipes.get(key));
    }

    @Nullable
    private <T extends Recipe<?>> RecipeEntry<T> get(RecipeType<T> type, RegistryKey<Recipe<?>> key) {
        RecipeEntry<?> lv = this.preparedRecipes.get(key);
        if (lv != null && lv.value().getType().equals(type)) {
            return lv;
        }
        return null;
    }

    public Map<RegistryKey<RecipePropertySet>, RecipePropertySet> getPropertySets() {
        return this.propertySets;
    }

    public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipeForSync() {
        return this.stonecutterRecipes;
    }

    @Override
    public RecipePropertySet getPropertySet(RegistryKey<RecipePropertySet> key) {
        return this.propertySets.getOrDefault(key, RecipePropertySet.EMPTY);
    }

    @Override
    public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipes() {
        return this.stonecutterRecipes;
    }

    public Collection<RecipeEntry<?>> values() {
        return this.preparedRecipes.recipes();
    }

    @Nullable
    public ServerRecipe get(NetworkRecipeId id) {
        return this.recipes.get(id.index());
    }

    public void forEachRecipeDisplay(RegistryKey<Recipe<?>> key, Consumer<RecipeDisplayEntry> action) {
        List<ServerRecipe> list = this.recipesByKey.get(key);
        if (list != null) {
            list.forEach(recipe -> action.accept(recipe.display));
        }
    }

    @VisibleForTesting
    protected static RecipeEntry<?> deserialize(RegistryKey<Recipe<?>> key, JsonObject json, RegistryWrapper.WrapperLookup registries) {
        Recipe lv = (Recipe)Recipe.CODEC.parse(registries.getOps(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
        return new RecipeEntry<Recipe>(key, lv);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> MatchGetter<I, T> createCachedMatchGetter(final RecipeType<T> type) {
        return new MatchGetter<I, T>(){
            @Nullable
            private RegistryKey<Recipe<?>> id;

            @Override
            public Optional<RecipeEntry<T>> getFirstMatch(I input, ServerWorld world) {
                ServerRecipeManager lv = world.getRecipeManager();
                Optional optional = lv.getFirstMatch(type, input, (World)world, this.id);
                if (optional.isPresent()) {
                    RecipeEntry lv2 = optional.get();
                    this.id = lv2.id();
                    return Optional.of(lv2);
                }
                return Optional.empty();
            }
        };
    }

    private static List<ServerRecipe> collectServerRecipes(Iterable<RecipeEntry<?>> recipes, FeatureSet enabledFeatures) {
        ArrayList<ServerRecipe> list = new ArrayList<ServerRecipe>();
        Object2IntOpenHashMap<String> object2IntMap = new Object2IntOpenHashMap<String>();
        for (RecipeEntry<?> lv : recipes) {
            Object lv2 = lv.value();
            OptionalInt optionalInt = lv2.getGroup().isEmpty() ? OptionalInt.empty() : OptionalInt.of(object2IntMap.computeIfAbsent(lv2.getGroup(), group -> object2IntMap.size()));
            Optional<Object> optional = lv2.isIgnoredInRecipeBook() ? Optional.empty() : Optional.of(lv2.getIngredientPlacement().getIngredients());
            for (RecipeDisplay lv3 : lv2.getDisplays()) {
                if (!lv3.isEnabled(enabledFeatures)) continue;
                int i = list.size();
                NetworkRecipeId lv4 = new NetworkRecipeId(i);
                RecipeDisplayEntry lv5 = new RecipeDisplayEntry(lv4, lv3, optionalInt, lv2.getRecipeBookCategory(), optional);
                list.add(new ServerRecipe(lv5, lv));
            }
        }
        return list;
    }

    private static SoleIngredientGetter cookingIngredientGetter(RecipeType<? extends SingleStackRecipe> expectedType) {
        return recipe -> {
            Optional<Object> optional;
            if (recipe.getType() == expectedType && recipe instanceof SingleStackRecipe) {
                SingleStackRecipe lv = (SingleStackRecipe)recipe;
                optional = Optional.of(lv.ingredient());
            } else {
                optional = Optional.empty();
            }
            return optional;
        };
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    public record ServerRecipe(RecipeDisplayEntry display, RecipeEntry<?> parent) {
    }

    @FunctionalInterface
    public static interface SoleIngredientGetter {
        public Optional<Ingredient> apply(Recipe<?> var1);
    }

    public static class PropertySetBuilder
    implements Consumer<Recipe<?>> {
        final RegistryKey<RecipePropertySet> propertySetKey;
        private final SoleIngredientGetter ingredientGetter;
        private final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        protected PropertySetBuilder(RegistryKey<RecipePropertySet> propertySetKey, SoleIngredientGetter ingredientGetter) {
            this.propertySetKey = propertySetKey;
            this.ingredientGetter = ingredientGetter;
        }

        @Override
        public void accept(Recipe<?> arg) {
            this.ingredientGetter.apply(arg).ifPresent(this.ingredients::add);
        }

        public RecipePropertySet build(FeatureSet enabledFeatures) {
            return RecipePropertySet.of(ServerRecipeManager.filterIngredients(enabledFeatures, this.ingredients));
        }

        @Override
        public /* synthetic */ void accept(Object recipe) {
            this.accept((Recipe)recipe);
        }
    }

    public static interface MatchGetter<I extends RecipeInput, T extends Recipe<I>> {
        public Optional<RecipeEntry<T>> getFirstMatch(I var1, ServerWorld var2);
    }
}

