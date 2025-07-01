/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record KeybindDownProperty(KeyBinding keybind) implements BooleanProperty
{
    private static final Codec<KeyBinding> KEY_BINDING_CODEC = Codec.STRING.comapFlatMap(id -> {
        KeyBinding lv = KeyBinding.byId(id);
        return lv != null ? DataResult.success(lv) : DataResult.error(() -> "Invalid keybind: " + id);
    }, KeyBinding::getTranslationKey);
    public static final MapCodec<KeybindDownProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)KEY_BINDING_CODEC.fieldOf("keybind")).forGetter(KeybindDownProperty::keybind)).apply((Applicative<KeybindDownProperty, ?>)instance, KeybindDownProperty::new));

    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return this.keybind.isPressed();
    }

    public MapCodec<KeybindDownProperty> getCodec() {
        return CODEC;
    }
}

