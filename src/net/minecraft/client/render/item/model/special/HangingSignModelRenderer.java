/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HangingSignModelRenderer
implements SimpleSpecialModelRenderer {
    private final Model model;
    private final SpriteIdentifier texture;

    public HangingSignModelRenderer(Model model, SpriteIdentifier texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void render(ModelTransformationMode modelTransformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        HangingSignBlockEntityRenderer.renderAsItem(matrices, vertexConsumers, light, overlay, this.model, this.texture);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(WoodType woodType, Optional<Identifier> texture) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)WoodType.CODEC.fieldOf("wood_type")).forGetter(Unbaked::woodType), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative<Unbaked, ?>)instance, Unbaked::new));

        public Unbaked(WoodType woodType) {
            this(woodType, Optional.empty());
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            Model lv = HangingSignBlockEntityRenderer.createModel(entityModels, this.woodType, HangingSignBlockEntityRenderer.AttachmentType.CEILING_MIDDLE);
            SpriteIdentifier lv2 = this.texture.map(TexturedRenderLayers::createHangingSignTextureId).orElseGet(() -> TexturedRenderLayers.getHangingSignTextureId(this.woodType));
            return new HangingSignModelRenderer(lv, lv2);
        }
    }
}

