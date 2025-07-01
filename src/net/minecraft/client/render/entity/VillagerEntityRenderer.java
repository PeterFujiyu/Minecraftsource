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
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VillagerEntityRenderer
extends AgeableMobEntityRenderer<VillagerEntity, VillagerEntityRenderState, VillagerResemblingModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/villager/villager.png");
    public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(-0.1171875f, -0.07421875f, 1.0f);

    public VillagerEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new VillagerResemblingModel(arg.getPart(EntityModelLayers.VILLAGER)), new VillagerResemblingModel(arg.getPart(EntityModelLayers.VILLAGER_BABY)), 0.5f);
        this.addFeature(new HeadFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this, arg.getEntityModels(), HEAD_TRANSFORMATION));
        this.addFeature(new VillagerClothingFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this, arg.getResourceManager(), "villager"));
        this.addFeature(new VillagerHeldItemFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this));
    }

    @Override
    public Identifier getTexture(VillagerEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    protected float getShadowRadius(VillagerEntityRenderState arg) {
        float f = super.getShadowRadius(arg);
        if (arg.baby) {
            return f * 0.5f;
        }
        return f;
    }

    @Override
    public VillagerEntityRenderState createRenderState() {
        return new VillagerEntityRenderState();
    }

    @Override
    public void updateRenderState(VillagerEntity arg, VillagerEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        ItemHolderEntityRenderState.update(arg, arg2, this.itemModelResolver);
        arg2.headRolling = arg.getHeadRollingTimeLeft() > 0;
        arg2.villagerData = arg.getVillagerData();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState arg) {
        return this.getShadowRadius((VillagerEntityRenderState)arg);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VillagerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((VillagerEntityRenderState)state);
    }
}

