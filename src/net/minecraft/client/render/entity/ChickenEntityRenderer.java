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
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityRenderer
extends AgeableMobEntityRenderer<ChickenEntity, ChickenEntityRenderState, ChickenEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/chicken.png");

    public ChickenEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new ChickenEntityModel(arg.getPart(EntityModelLayers.CHICKEN)), new ChickenEntityModel(arg.getPart(EntityModelLayers.CHICKEN_BABY)), 0.3f);
    }

    @Override
    public Identifier getTexture(ChickenEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public ChickenEntityRenderState createRenderState() {
        return new ChickenEntityRenderState();
    }

    @Override
    public void updateRenderState(ChickenEntity arg, ChickenEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.flapProgress = MathHelper.lerp(f, arg.prevFlapProgress, arg.flapProgress);
        arg2.maxWingDeviation = MathHelper.lerp(f, arg.prevMaxWingDeviation, arg.maxWingDeviation);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ChickenEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

