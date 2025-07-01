/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShieldModelRenderer
implements SpecialModelRenderer<ComponentMap> {
    private final ShieldEntityModel model;

    public ShieldModelRenderer(ShieldEntityModel model) {
        this.model = model;
    }

    @Override
    @Nullable
    public ComponentMap getData(ItemStack arg) {
        return arg.getImmutableComponents();
    }

    @Override
    public void render(@Nullable ComponentMap arg, ModelTransformationMode arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i, int j, boolean bl) {
        BannerPatternsComponent lv = arg != null ? arg.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT) : BannerPatternsComponent.DEFAULT;
        DyeColor lv2 = arg != null ? arg.get(DataComponentTypes.BASE_COLOR) : null;
        boolean bl2 = !lv.layers().isEmpty() || lv2 != null;
        arg3.push();
        arg3.scale(1.0f, -1.0f, -1.0f);
        SpriteIdentifier lv3 = bl2 ? ModelBaker.SHIELD_BASE : ModelBaker.SHIELD_BASE_NO_PATTERN;
        VertexConsumer lv4 = lv3.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getItemGlintConsumer(arg4, this.model.getLayer(lv3.getAtlasId()), arg2 == ModelTransformationMode.GUI, bl));
        this.model.getHandle().render(arg3, lv4, i, j);
        if (bl2) {
            BannerBlockEntityRenderer.renderCanvas(arg3, arg4, i, j, this.model.getPlate(), lv3, false, Objects.requireNonNullElse(lv2, DyeColor.WHITE), lv, bl, false);
        } else {
            this.model.getPlate().render(arg3, lv4, i, j);
        }
        arg3.pop();
    }

    @Override
    @Nullable
    public /* synthetic */ Object getData(ItemStack stack) {
        return this.getData(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(INSTANCE);

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new ShieldModelRenderer(new ShieldEntityModel(entityModels.getModelPart(EntityModelLayers.SHIELD)));
        }
    }
}

