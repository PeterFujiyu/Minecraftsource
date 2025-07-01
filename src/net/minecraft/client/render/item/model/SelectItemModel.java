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
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SelectItemModel<T>
implements ItemModel {
    private final SelectProperty<T> property;
    private final Object2ObjectMap<T, ItemModel> cases;

    public SelectItemModel(SelectProperty<T> property, Object2ObjectMap<T, ItemModel> cases) {
        this.property = property;
        this.cases = cases;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        T object = this.property.getValue(stack, world, user, seed, transformationMode);
        ItemModel lv = (ItemModel)this.cases.get(object);
        if (lv != null) {
            lv.update(state, stack, resolver, transformationMode, world, user, seed);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record SwitchCase<T>(List<T> values, ItemModel.Unbaked model) {
        public static <T> Codec<SwitchCase<T>> createCodec(Codec<T> conditionCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.nonEmptyList(Codecs.listOrSingle(conditionCodec)).fieldOf("when")).forGetter(SwitchCase::values), ((MapCodec)ItemModelTypes.CODEC.fieldOf("model")).forGetter(SwitchCase::model)).apply((Applicative<SwitchCase, ?>)instance, SwitchCase::new));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record UnbakedSwitch<P extends SelectProperty<T>, T>(P property, List<SwitchCase<T>> cases) {
        public static final MapCodec<UnbakedSwitch<?, ?>> CODEC = SelectProperties.CODEC.dispatchMap("property", unbakedSwitch -> unbakedSwitch.property().getType(), SelectProperty.Type::switchCodec);

        public ItemModel bake(ItemModel.BakeContext context, ItemModel fallback) {
            Object2ObjectOpenHashMap object2ObjectMap = new Object2ObjectOpenHashMap();
            for (SwitchCase<T> lv : this.cases) {
                ItemModel.Unbaked lv2 = lv.model;
                ItemModel lv3 = lv2.bake(context);
                for (Object object : lv.values) {
                    object2ObjectMap.put(object, lv3);
                }
            }
            object2ObjectMap.defaultReturnValue(fallback);
            return new SelectItemModel(this.property, object2ObjectMap);
        }

        public void resolveCases(ResolvableModel.Resolver resolver) {
            for (SwitchCase<T> lv : this.cases) {
                lv.model.resolve(resolver);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(UnbakedSwitch.CODEC.forGetter(Unbaked::unbakedSwitch), ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply((Applicative<Unbaked, ?>)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            ItemModel lv = this.fallback.map(model -> model.bake(context)).orElse(context.missingItemModel());
            return this.unbakedSwitch.bake(context, lv);
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.unbakedSwitch.resolveCases(resolver);
            this.fallback.ifPresent(model -> model.resolve(resolver));
        }
    }
}

