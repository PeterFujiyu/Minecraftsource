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
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BasicItemModel
implements ItemModel {
    private final BakedModel model;
    private final List<TintSource> tints;

    BasicItemModel(BakedModel model, List<TintSource> tints) {
        this.model = model;
        this.tints = tints;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        ItemRenderState.LayerRenderState lv = state.newLayer();
        if (stack.hasGlint()) {
            lv.setGlint(BasicItemModel.shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD);
        }
        int j = this.tints.size();
        int[] is = lv.initTints(j);
        for (int k = 0; k < j; ++k) {
            is[k] = this.tints.get(k).getTint(stack, world, user);
        }
        RenderLayer lv2 = RenderLayers.getItemLayer(stack);
        lv.setModel(this.model, lv2);
    }

    private static boolean shouldUseSpecialGlint(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier model, List<TintSource> tints) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("model")).forGetter(Unbaked::model), TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply((Applicative<Unbaked, ?>)instance, Unbaked::new));

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            resolver.resolve(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            BakedModel lv = context.bake(this.model);
            return new BasicItemModel(lv, this.tints);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }
    }
}

