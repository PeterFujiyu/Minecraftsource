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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityRenderer
extends BipedEntityRenderer<AbstractPiglinEntity, PiglinEntityRenderState, PiglinEntityModel> {
    private static final Identifier PIGLIN_TEXTURE = Identifier.ofVanilla("textures/entity/piglin/piglin.png");
    private static final Identifier PIGLIN_BRUTE_TEXTURE = Identifier.ofVanilla("textures/entity/piglin/piglin_brute.png");
    public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(0.0f, 0.0f, 1.0019531f);

    public PiglinEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer mainLayer, EntityModelLayer babyMainLayer, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer, EntityModelLayer babyArmorInnerLayer, EntityModelLayer babyArmorOuterLayer) {
        super(ctx, new PiglinEntityModel(ctx.getPart(mainLayer)), new PiglinEntityModel(ctx.getPart(babyMainLayer)), 0.5f, HEAD_TRANSFORMATION);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(ctx.getPart(armorInnerLayer)), new ArmorEntityModel(ctx.getPart(armorOuterLayer)), new ArmorEntityModel(ctx.getPart(babyArmorInnerLayer)), new ArmorEntityModel(ctx.getPart(babyArmorOuterLayer)), ctx.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(PiglinEntityRenderState arg) {
        return arg.brute ? PIGLIN_BRUTE_TEXTURE : PIGLIN_TEXTURE;
    }

    @Override
    public PiglinEntityRenderState createRenderState() {
        return new PiglinEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractPiglinEntity arg, PiglinEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.brute = arg.getType() == EntityType.PIGLIN_BRUTE;
        arg2.activity = arg.getActivity();
        arg2.piglinCrossbowPullTime = CrossbowItem.getPullTime(arg.getActiveItem(), arg);
        arg2.shouldZombify = arg.shouldZombify();
    }

    @Override
    protected boolean isShaking(PiglinEntityRenderState arg) {
        return super.isShaking(arg) || arg.shouldZombify;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((PiglinEntityRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PiglinEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

