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
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityRenderer
extends AgeableMobEntityRenderer<LlamaEntity, LlamaEntityRenderState, LlamaEntityModel> {
    private static final Identifier CREAMY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/creamy.png");
    private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/llama/white.png");
    private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/llama/brown.png");
    private static final Identifier GRAY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/gray.png");

    public LlamaEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer) {
        super(context, new LlamaEntityModel(context.getPart(layer)), new LlamaEntityModel(context.getPart(babyLayer)), 0.7f);
        this.addFeature(new LlamaDecorFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(LlamaEntityRenderState arg) {
        return switch (arg.variant) {
            default -> throw new MatchException(null, null);
            case LlamaEntity.Variant.CREAMY -> CREAMY_TEXTURE;
            case LlamaEntity.Variant.WHITE -> WHITE_TEXTURE;
            case LlamaEntity.Variant.BROWN -> BROWN_TEXTURE;
            case LlamaEntity.Variant.GRAY -> GRAY_TEXTURE;
        };
    }

    @Override
    public LlamaEntityRenderState createRenderState() {
        return new LlamaEntityRenderState();
    }

    @Override
    public void updateRenderState(LlamaEntity arg, LlamaEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.variant = arg.getVariant();
        arg2.hasChest = !arg.isBaby() && arg.hasChest();
        arg2.bodyArmor = arg.getBodyArmor();
        arg2.trader = arg.isTrader();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((LlamaEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

