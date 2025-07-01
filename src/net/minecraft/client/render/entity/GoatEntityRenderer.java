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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GoatEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GoatEntityRenderer
extends AgeableMobEntityRenderer<GoatEntity, GoatEntityRenderState, GoatEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/goat/goat.png");

    public GoatEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new GoatEntityModel(arg.getPart(EntityModelLayers.GOAT)), new GoatEntityModel(arg.getPart(EntityModelLayers.GOAT_BABY)), 0.7f);
    }

    @Override
    public Identifier getTexture(GoatEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public GoatEntityRenderState createRenderState() {
        return new GoatEntityRenderState();
    }

    @Override
    public void updateRenderState(GoatEntity arg, GoatEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.hasLeftHorn = arg.hasLeftHorn();
        arg2.hasRightHorn = arg.hasRightHorn();
        arg2.headPitch = arg.getHeadPitch();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GoatEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

