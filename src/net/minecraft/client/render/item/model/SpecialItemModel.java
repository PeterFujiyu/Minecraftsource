/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpecialItemModel<T>
implements ItemModel {
    private final SpecialModelRenderer<T> specialModelType;
    private final BakedModel base;

    public SpecialItemModel(SpecialModelRenderer<T> specialModelType, BakedModel base) {
        this.specialModelType = specialModelType;
        this.base = base;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        ItemRenderState.LayerRenderState lv = state.newLayer();
        if (stack.hasGlint()) {
            lv.setGlint(ItemRenderState.Glint.STANDARD);
        }
        lv.setSpecialModel(this.specialModelType, this.specialModelType.getData(stack), this.base);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("base")).forGetter(Unbaked::base), ((MapCodec)SpecialModelTypes.CODEC.fieldOf("model")).forGetter(Unbaked::specialModel)).apply((Applicative<Unbaked, ?>)instance, Unbaked::new));

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            resolver.resolve(this.base);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            BakedModel lv = context.bake(this.base);
            SpecialModelRenderer<?> lv2 = this.specialModel.bake(context.entityModelSet());
            if (lv2 == null) {
                return context.missingItemModel();
            }
            return new SpecialItemModel(lv2, lv);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }
    }
}

