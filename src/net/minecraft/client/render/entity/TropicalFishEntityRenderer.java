/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderer
extends MobEntityRenderer<TropicalFishEntity, TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> {
    private final EntityModel<TropicalFishEntityRenderState> smallModel = this.getModel();
    private final EntityModel<TropicalFishEntityRenderState> largeModel;
    private static final Identifier A_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a.png");
    private static final Identifier B_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b.png");

    public TropicalFishEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new SmallTropicalFishEntityModel(arg.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15f);
        this.largeModel = new LargeTropicalFishEntityModel(arg.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
        this.addFeature(new TropicalFishColorFeatureRenderer(this, arg.getEntityModels()));
    }

    @Override
    public Identifier getTexture(TropicalFishEntityRenderState arg) {
        return switch (arg.variety.getSize()) {
            default -> throw new MatchException(null, null);
            case TropicalFishEntity.Size.SMALL -> A_TEXTURE;
            case TropicalFishEntity.Size.LARGE -> B_TEXTURE;
        };
    }

    @Override
    public TropicalFishEntityRenderState createRenderState() {
        return new TropicalFishEntityRenderState();
    }

    @Override
    public void updateRenderState(TropicalFishEntity arg, TropicalFishEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.variety = arg.getVariant();
        arg2.baseColor = arg.getBaseColorComponents().getEntityColor();
        arg2.patternColor = arg.getPatternColorComponents().getEntityColor();
    }

    @Override
    public void render(TropicalFishEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.model = switch (arg.variety.getSize()) {
            default -> throw new MatchException(null, null);
            case TropicalFishEntity.Size.SMALL -> this.smallModel;
            case TropicalFishEntity.Size.LARGE -> this.largeModel;
        };
        super.render(arg, arg2, arg3, i);
    }

    @Override
    protected int getMixColor(TropicalFishEntityRenderState arg) {
        return arg.baseColor;
    }

    @Override
    protected void setupTransforms(TropicalFishEntityRenderState arg, MatrixStack arg2, float f, float g) {
        super.setupTransforms(arg, arg2, f, g);
        float h = 4.3f * MathHelper.sin(0.6f * arg.age);
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
        if (!arg.touchingWater) {
            arg2.translate(0.2f, 0.1f, 0.0f);
            arg2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((TropicalFishEntityRenderState)state);
    }

    @Override
    protected /* synthetic */ int getMixColor(LivingEntityRenderState state) {
        return this.getMixColor((TropicalFishEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

