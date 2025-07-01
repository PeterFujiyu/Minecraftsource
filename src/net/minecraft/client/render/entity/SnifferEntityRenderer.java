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
import net.minecraft.client.render.entity.model.SnifferEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnifferEntityRenderState;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class SnifferEntityRenderer
extends AgeableMobEntityRenderer<SnifferEntity, SnifferEntityRenderState, SnifferEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sniffer/sniffer.png");

    public SnifferEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new SnifferEntityModel(arg.getPart(EntityModelLayers.SNIFFER)), new SnifferEntityModel(arg.getPart(EntityModelLayers.SNIFFER_BABY)), 1.1f);
    }

    @Override
    public Identifier getTexture(SnifferEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public SnifferEntityRenderState createRenderState() {
        return new SnifferEntityRenderState();
    }

    @Override
    public void updateRenderState(SnifferEntity arg, SnifferEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.searching = arg.isSearching();
        arg2.diggingAnimationState.copyFrom(arg.diggingAnimationState);
        arg2.sniffingAnimationState.copyFrom(arg.sniffingAnimationState);
        arg2.risingAnimationState.copyFrom(arg.risingAnimationState);
        arg2.feelingHappyAnimationState.copyFrom(arg.feelingHappyAnimationState);
        arg2.scentingAnimationState.copyFrom(arg.scentingAnimationState);
    }

    @Override
    protected Box getBoundingBox(SnifferEntity arg) {
        return super.getBoundingBox(arg).expand(0.6f);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SnifferEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

