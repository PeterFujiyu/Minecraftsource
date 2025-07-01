/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.select;

import com.google.common.collect.HashMultiset;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SelectProperty<T> {
    @Nullable
    public T getValue(ItemStack var1, @Nullable ClientWorld var2, @Nullable LivingEntity var3, int var4, ModelTransformationMode var5);

    public Type<? extends SelectProperty<T>, T> getType();

    @Environment(value=EnvType.CLIENT)
    public record Type<P extends SelectProperty<T>, T>(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> switchCodec) {
        public static <P extends SelectProperty<T>, T> Type<P, T> create(MapCodec<P> propertyCodec, Codec<T> valueCodec) {
            Codec<List> codec2 = SelectItemModel.SwitchCase.createCodec(valueCodec).listOf().validate(cases -> {
                if (cases.isEmpty()) {
                    return DataResult.error(() -> "Empty case list");
                }
                HashMultiset multiset = HashMultiset.create();
                for (SelectItemModel.SwitchCase lv : cases) {
                    multiset.addAll(lv.values());
                }
                if (multiset.size() != multiset.entrySet().size()) {
                    return DataResult.error(() -> "Duplicate case conditions: " + multiset.entrySet().stream().filter(entry -> entry.getCount() > 1).map(entry -> entry.getElement().toString()).collect(Collectors.joining(", ")));
                }
                return DataResult.success(cases);
            });
            MapCodec<SelectItemModel.UnbakedSwitch<P, T>> mapCodec2 = RecordCodecBuilder.mapCodec(instance -> instance.group(propertyCodec.forGetter(SelectItemModel.UnbakedSwitch::property), ((MapCodec)codec2.fieldOf("cases")).forGetter(SelectItemModel.UnbakedSwitch::cases)).apply((Applicative<SelectItemModel.UnbakedSwitch, ?>)instance, SelectItemModel.UnbakedSwitch::new));
            return new Type<P, T>(mapCodec2);
        }
    }
}

