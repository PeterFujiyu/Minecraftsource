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
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TurtleEntityRenderState;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityRenderer
extends AgeableMobEntityRenderer<TurtleEntity, TurtleEntityRenderState, TurtleEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/turtle/big_sea_turtle.png");

    public TurtleEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new TurtleEntityModel(arg.getPart(EntityModelLayers.TURTLE)), new TurtleEntityModel(arg.getPart(EntityModelLayers.TURTLE_BABY)), 0.7f);
    }

    @Override
    protected float getShadowRadius(TurtleEntityRenderState arg) {
        float f = super.getShadowRadius(arg);
        if (arg.baby) {
            return f * 0.83f;
        }
        return f;
    }

    @Override
    public TurtleEntityRenderState createRenderState() {
        return new TurtleEntityRenderState();
    }

    @Override
    public void updateRenderState(TurtleEntity arg, TurtleEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.onLand = !arg.isTouchingWater() && arg.isOnGround();
        arg2.diggingSand = arg.isDiggingSand();
        arg2.hasEgg = !arg.isBaby() && arg.hasEgg();
    }

    @Override
    public Identifier getTexture(TurtleEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState arg) {
        return this.getShadowRadius((TurtleEntityRenderState)arg);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((TurtleEntityRenderState)state);
    }
}

