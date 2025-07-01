/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class HeadModelRenderer
implements SpecialModelRenderer<ProfileComponent> {
    private final SkullBlock.SkullType kind;
    private final SkullBlockEntityModel model;
    @Nullable
    private final Identifier texture;
    private final float animation;

    public HeadModelRenderer(SkullBlock.SkullType kind, SkullBlockEntityModel model, @Nullable Identifier texture, float animation) {
        this.kind = kind;
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @Override
    @Nullable
    public ProfileComponent getData(ItemStack arg) {
        return arg.get(DataComponentTypes.PROFILE);
    }

    @Override
    public void render(@Nullable ProfileComponent arg, ModelTransformationMode arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i, int j, boolean bl) {
        RenderLayer lv = SkullBlockEntityRenderer.getRenderLayer(this.kind, arg, this.texture);
        SkullBlockEntityRenderer.renderSkull(null, 180.0f, this.animation, arg3, arg4, i, this.model, lv);
    }

    @Override
    @Nullable
    public /* synthetic */ Object getData(ItemStack stack) {
        return this.getData(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(SkullBlock.SkullType kind, Optional<Identifier> textureOverride, float animation) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)SkullBlock.SkullType.CODEC.fieldOf("kind")).forGetter(Unbaked::kind), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride), Codec.FLOAT.optionalFieldOf("animation", Float.valueOf(0.0f)).forGetter(Unbaked::animation)).apply((Applicative<Unbaked, ?>)instance, Unbaked::new));

        public Unbaked(SkullBlock.SkullType kind) {
            this(kind, Optional.empty(), 0.0f);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        @Nullable
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            SkullBlockEntityModel lv = SkullBlockEntityRenderer.getModels(entityModels, this.kind);
            Identifier lv2 = this.textureOverride.map(id -> id.withPath(texture -> "textures/entity/" + texture + ".png")).orElse(null);
            return lv != null ? new HeadModelRenderer(this.kind, lv, lv2, this.animation) : null;
        }
    }
}

