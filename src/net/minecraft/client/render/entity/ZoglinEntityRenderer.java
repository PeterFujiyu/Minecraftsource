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
import net.minecraft.client.render.entity.AbstractHoglinEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZoglinEntityRenderer
extends AbstractHoglinEntityRenderer<ZoglinEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/hoglin/zoglin.png");

    public ZoglinEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, EntityModelLayers.ZOGLIN, EntityModelLayers.ZOGLIN_BABY, 0.7f);
    }

    @Override
    public Identifier getTexture(HoglinEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HoglinEntityRenderState)state);
    }
}

