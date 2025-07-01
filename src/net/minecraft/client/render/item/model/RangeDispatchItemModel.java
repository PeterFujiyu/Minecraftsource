/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RangeDispatchItemModel
implements ItemModel {
    private static final int field_55353 = 16;
    private final NumericProperty property;
    private final float scale;
    private final float[] thresholds;
    private final ItemModel[] models;
    private final ItemModel fallback;

    RangeDispatchItemModel(NumericProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback) {
        this.property = property;
        this.thresholds = thresholds;
        this.models = models;
        this.fallback = fallback;
        this.scale = scale;
    }

    private static int getIndex(float[] thresholds, float value) {
        if (thresholds.length < 16) {
            for (int i = 0; i < thresholds.length; ++i) {
                if (!(thresholds[i] > value)) continue;
                return i - 1;
            }
            return thresholds.length - 1;
        }
        int i = Arrays.binarySearch(thresholds, value);
        if (i < 0) {
            int j = ~i;
            return j - 1;
        }
        return i;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        int j;
        float f = this.property.getValue(stack, world, user, seed) * this.scale;
        ItemModel lv = Float.isNaN(f) ? this.fallback : ((j = RangeDispatchItemModel.getIndex(this.thresholds, f)) == -1 ? this.fallback : this.models[j]);
        lv.update(state, stack, resolver, transformationMode, world, user, seed);
    }

    @Environment(value=EnvType.CLIENT)
    public record Entry(float threshold, ItemModel.Unbaked model) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("threshold")).forGetter(Entry::threshold), ((MapCodec)ItemModelTypes.CODEC.fieldOf("model")).forGetter(Entry::model)).apply((Applicative<Entry, ?>)instance, Entry::new));
        public static final Comparator<Entry> COMPARATOR = Comparator.comparingDouble(Entry::threshold);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(NumericProperty property, float scale, List<Entry> entries, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(NumericProperties.CODEC.forGetter(Unbaked::property), Codec.FLOAT.optionalFieldOf("scale", Float.valueOf(1.0f)).forGetter(Unbaked::scale), ((MapCodec)Entry.CODEC.listOf().fieldOf("entries")).forGetter(Unbaked::entries), ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply((Applicative<Unbaked, ?>)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            float[] fs = new float[this.entries.size()];
            ItemModel[] lvs = new ItemModel[this.entries.size()];
            ArrayList<Entry> list = new ArrayList<Entry>(this.entries);
            list.sort(Entry.COMPARATOR);
            for (int i = 0; i < list.size(); ++i) {
                Entry lv = (Entry)list.get(i);
                fs[i] = lv.threshold;
                lvs[i] = lv.model.bake(context);
            }
            ItemModel lv2 = this.fallback.map(model -> model.bake(context)).orElse(context.missingItemModel());
            return new RangeDispatchItemModel(this.property, this.scale, fs, lvs, lv2);
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.fallback.ifPresent(model -> model.resolve(resolver));
            this.entries.forEach(entry -> entry.model.resolve(resolver));
        }
    }
}

