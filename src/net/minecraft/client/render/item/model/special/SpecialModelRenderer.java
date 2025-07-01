/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SpecialModelRenderer<T> {
    public void render(@Nullable T var1, ModelTransformationMode var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6, boolean var7);

    @Nullable
    public T getData(ItemStack var1);

    @Environment(value=EnvType.CLIENT)
    public static interface Unbaked {
        @Nullable
        public SpecialModelRenderer<?> bake(LoadedEntityModels var1);

        public MapCodec<? extends Unbaked> getCodec();
    }
}

