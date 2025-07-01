/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;

@Environment(value=EnvType.CLIENT)
public record ItemAsset(ItemModel.Unbaked model, Properties properties) {
    public static final Codec<ItemAsset> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ItemModelTypes.CODEC.fieldOf("model")).forGetter(ItemAsset::model), Properties.CODEC.forGetter(ItemAsset::properties)).apply((Applicative<ItemAsset, ?>)instance, ItemAsset::new));

    @Environment(value=EnvType.CLIENT)
    public record Properties(boolean handAnimationOnSwap) {
        public static final Properties DEFAULT = new Properties(true);
        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.optionalFieldOf("hand_animation_on_swap", true).forGetter(Properties::handAnimationOnSwap)).apply((Applicative<Properties, ?>)instance, Properties::new));
    }
}

