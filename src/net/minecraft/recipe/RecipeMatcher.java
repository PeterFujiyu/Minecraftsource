/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class RecipeMatcher<T> {
    public final Reference2IntOpenHashMap<T> available = new Reference2IntOpenHashMap();

    boolean hasAtLeast(T input, int minimum) {
        return this.available.getInt(input) >= minimum;
    }

    void consume(T input, int count) {
        int j = this.available.addTo(input, -count);
        if (j < count) {
            throw new IllegalStateException("Took " + count + " items, but only had " + j);
        }
    }

    void addInput(T input, int count) {
        this.available.addTo(input, count);
    }

    public boolean match(List<? extends RawIngredient<T>> ingredients, int quantity, @Nullable ItemCallback<T> itemCallback) {
        return new Matcher(ingredients).match(quantity, itemCallback);
    }

    public int countCrafts(List<? extends RawIngredient<T>> ingredients, int max, @Nullable ItemCallback<T> itemCallback) {
        return new Matcher(ingredients).countCrafts(max, itemCallback);
    }

    public void clear() {
        this.available.clear();
    }

    public void add(T input, int count) {
        this.addInput(input, count);
    }

    List<T> createItemRequirementList(Iterable<? extends RawIngredient<T>> ingredients) {
        ArrayList list = new ArrayList();
        for (Reference2IntMap.Entry entry : Reference2IntMaps.fastIterable(this.available)) {
            if (entry.getIntValue() <= 0 || !RecipeMatcher.anyAccept(ingredients, entry.getKey())) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    private static <T> boolean anyAccept(Iterable<? extends RawIngredient<T>> ingredients, T item) {
        for (RawIngredient<T> lv : ingredients) {
            if (!lv.acceptsItem(item)) continue;
            return true;
        }
        return false;
    }

    @VisibleForTesting
    public int getMaximumCrafts(List<? extends RawIngredient<T>> ingredients) {
        int i = Integer.MAX_VALUE;
        ObjectIterable<Reference2IntMap.Entry<T>> objectIterable = Reference2IntMaps.fastIterable(this.available);
        block0: for (RawIngredient rawIngredient : ingredients) {
            int j = 0;
            for (Reference2IntMap.Entry entry : objectIterable) {
                int k = entry.getIntValue();
                if (k <= j) continue;
                if (rawIngredient.acceptsItem(entry.getKey())) {
                    j = k;
                }
                if (j < i) continue;
                continue block0;
            }
            i = j;
            if (i != 0) continue;
            break;
        }
        return i;
    }

    class Matcher {
        private final List<? extends RawIngredient<T>> ingredients;
        private final int totalIngredients;
        private final List<T> requiredItems;
        private final int totalRequiredItems;
        private final BitSet bits;
        private final IntList ingredientItemLookup = new IntArrayList();

        public Matcher(List<? extends RawIngredient<T>> ingredients) {
            this.ingredients = ingredients;
            this.totalIngredients = ingredients.size();
            this.requiredItems = RecipeMatcher.this.createItemRequirementList(ingredients);
            this.totalRequiredItems = this.requiredItems.size();
            this.bits = new BitSet(this.getVisitedIngredientIndexCount() + this.getVisitedItemIndexCount() + this.getRequirementIndexCount() + this.getItemMatchIndexCount() + this.getMissingIndexCount());
            this.initItemMatch();
        }

        private void initItemMatch() {
            for (int i = 0; i < this.totalIngredients; ++i) {
                RawIngredient lv = this.ingredients.get(i);
                for (int j = 0; j < this.totalRequiredItems; ++j) {
                    if (!lv.acceptsItem(this.requiredItems.get(j))) continue;
                    this.setMatch(j, i);
                }
            }
        }

        public boolean match(int quantity, @Nullable ItemCallback<T> itemCallback) {
            int m;
            int l;
            IntList intList;
            if (quantity <= 0) {
                return true;
            }
            int j = 0;
            while ((intList = this.tryFindIngredientItemLookup(quantity)) != null) {
                int k = intList.getInt(0);
                RecipeMatcher.this.consume(this.requiredItems.get(k), quantity);
                l = intList.size() - 1;
                this.unfulfillRequirement(intList.getInt(l));
                ++j;
                for (m = 0; m < intList.size() - 1; ++m) {
                    int o;
                    int n;
                    if (Matcher.isItem(m)) {
                        n = intList.getInt(m);
                        o = intList.getInt(m + 1);
                        this.markMissing(n, o);
                        continue;
                    }
                    n = intList.getInt(m + 1);
                    o = intList.getInt(m);
                    this.markNotMissing(n, o);
                }
            }
            boolean bl = j == this.totalIngredients;
            boolean bl2 = bl && itemCallback != null;
            this.clearVisited();
            this.clearRequirements();
            block2: for (l = 0; l < this.totalIngredients; ++l) {
                for (m = 0; m < this.totalRequiredItems; ++m) {
                    if (!this.isMissing(m, l)) continue;
                    this.markNotMissing(m, l);
                    RecipeMatcher.this.addInput(this.requiredItems.get(m), quantity);
                    if (!bl2) continue block2;
                    itemCallback.accept(this.requiredItems.get(m));
                    continue block2;
                }
            }
            assert (this.bits.get(this.getMissingIndexOffset(), this.getMissingIndexOffset() + this.getMissingIndexCount()).isEmpty());
            return bl;
        }

        private static boolean isItem(int index) {
            return (index & 1) == 0;
        }

        @Nullable
        private IntList tryFindIngredientItemLookup(int min) {
            this.clearVisited();
            for (int j = 0; j < this.totalRequiredItems; ++j) {
                IntList intList;
                if (!RecipeMatcher.this.hasAtLeast(this.requiredItems.get(j), min) || (intList = this.findIngredientItemLookup(j)) == null) continue;
                return intList;
            }
            return null;
        }

        @Nullable
        private IntList findIngredientItemLookup(int itemIndex) {
            this.ingredientItemLookup.clear();
            this.markItemVisited(itemIndex);
            this.ingredientItemLookup.add(itemIndex);
            while (!this.ingredientItemLookup.isEmpty()) {
                int k;
                int j = this.ingredientItemLookup.size();
                if (Matcher.isItem(j - 1)) {
                    k = this.ingredientItemLookup.getInt(j - 1);
                    for (l = 0; l < this.totalIngredients; ++l) {
                        if (this.hasVisitedIngredient(l) || !this.matches(k, l) || this.isMissing(k, l)) continue;
                        this.markIngredientVisited(l);
                        this.ingredientItemLookup.add(l);
                        break;
                    }
                } else {
                    k = this.ingredientItemLookup.getInt(j - 1);
                    if (!this.getRequirement(k)) {
                        return this.ingredientItemLookup;
                    }
                    for (l = 0; l < this.totalRequiredItems; ++l) {
                        if (this.isRequirementUnfulfilled(l) || !this.isMissing(l, k)) continue;
                        assert (this.matches(l, k));
                        this.markItemVisited(l);
                        this.ingredientItemLookup.add(l);
                        break;
                    }
                }
                if ((k = this.ingredientItemLookup.size()) != j) continue;
                this.ingredientItemLookup.removeInt(k - 1);
            }
            return null;
        }

        private int getVisitedIngredientIndexOffset() {
            return 0;
        }

        private int getVisitedIngredientIndexCount() {
            return this.totalIngredients;
        }

        private int getVisitedItemIndexOffset() {
            return this.getVisitedIngredientIndexOffset() + this.getVisitedIngredientIndexCount();
        }

        private int getVisitedItemIndexCount() {
            return this.totalRequiredItems;
        }

        private int getRequirementIndexOffset() {
            return this.getVisitedItemIndexOffset() + this.getVisitedItemIndexCount();
        }

        private int getRequirementIndexCount() {
            return this.totalIngredients;
        }

        private int getItemMatchIndexOffset() {
            return this.getRequirementIndexOffset() + this.getRequirementIndexCount();
        }

        private int getItemMatchIndexCount() {
            return this.totalIngredients * this.totalRequiredItems;
        }

        private int getMissingIndexOffset() {
            return this.getItemMatchIndexOffset() + this.getItemMatchIndexCount();
        }

        private int getMissingIndexCount() {
            return this.totalIngredients * this.totalRequiredItems;
        }

        private boolean getRequirement(int itemId) {
            return this.bits.get(this.getRequirementIndex(itemId));
        }

        private void unfulfillRequirement(int itemId) {
            this.bits.set(this.getRequirementIndex(itemId));
        }

        private int getRequirementIndex(int itemId) {
            assert (itemId >= 0 && itemId < this.totalIngredients);
            return this.getRequirementIndexOffset() + itemId;
        }

        private void clearRequirements() {
            this.clear(this.getRequirementIndexOffset(), this.getRequirementIndexCount());
        }

        private void setMatch(int itemIndex, int ingredientIndex) {
            this.bits.set(this.getMatchIndex(itemIndex, ingredientIndex));
        }

        private boolean matches(int itemIndex, int ingredientIndex) {
            return this.bits.get(this.getMatchIndex(itemIndex, ingredientIndex));
        }

        private int getMatchIndex(int itemIndex, int ingredientIndex) {
            assert (itemIndex >= 0 && itemIndex < this.totalRequiredItems);
            assert (ingredientIndex >= 0 && ingredientIndex < this.totalIngredients);
            return this.getItemMatchIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
        }

        private boolean isMissing(int itemIndex, int ingredientIndex) {
            return this.bits.get(this.getMissingIndex(itemIndex, ingredientIndex));
        }

        private void markMissing(int itemIndex, int ingredientIndex) {
            int k = this.getMissingIndex(itemIndex, ingredientIndex);
            assert (!this.bits.get(k));
            this.bits.set(k);
        }

        private void markNotMissing(int itemIndex, int ingredientIndex) {
            int k = this.getMissingIndex(itemIndex, ingredientIndex);
            assert (this.bits.get(k));
            this.bits.clear(k);
        }

        private int getMissingIndex(int itemIndex, int ingredientIndex) {
            assert (itemIndex >= 0 && itemIndex < this.totalRequiredItems);
            assert (ingredientIndex >= 0 && ingredientIndex < this.totalIngredients);
            return this.getMissingIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
        }

        private void markIngredientVisited(int index) {
            this.bits.set(this.getVisitedIngredientIndex(index));
        }

        private boolean hasVisitedIngredient(int index) {
            return this.bits.get(this.getVisitedIngredientIndex(index));
        }

        private int getVisitedIngredientIndex(int index) {
            assert (index >= 0 && index < this.totalIngredients);
            return this.getVisitedIngredientIndexOffset() + index;
        }

        private void markItemVisited(int index) {
            this.bits.set(this.getVisitedItemIndex(index));
        }

        private boolean isRequirementUnfulfilled(int index) {
            return this.bits.get(this.getVisitedItemIndex(index));
        }

        private int getVisitedItemIndex(int index) {
            assert (index >= 0 && index < this.totalRequiredItems);
            return this.getVisitedItemIndexOffset() + index;
        }

        private void clearVisited() {
            this.clear(this.getVisitedIngredientIndexOffset(), this.getVisitedIngredientIndexCount());
            this.clear(this.getVisitedItemIndexOffset(), this.getVisitedItemIndexCount());
        }

        private void clear(int start, int offset) {
            this.bits.clear(start, start + offset);
        }

        public int countCrafts(int max, @Nullable ItemCallback<T> itemCallback) {
            int l;
            int j = 0;
            int k = Math.min(max, RecipeMatcher.this.getMaximumCrafts(this.ingredients)) + 1;
            while (true) {
                if (this.match(l = (j + k) / 2, null)) {
                    if (k - j <= 1) break;
                    j = l;
                    continue;
                }
                k = l;
            }
            if (l > 0) {
                this.match(l, itemCallback);
            }
            return l;
        }
    }

    @FunctionalInterface
    public static interface ItemCallback<T> {
        public void accept(T var1);
    }

    @FunctionalInterface
    public static interface RawIngredient<T> {
        public boolean acceptsItem(T var1);
    }
}

