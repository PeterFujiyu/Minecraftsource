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
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityRenderer
extends AgeableMobEntityRenderer<OcelotEntity, FelineEntityRenderState, OcelotEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/cat/ocelot.png");

    public OcelotEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new OcelotEntityModel(arg.getPart(EntityModelLayers.OCELOT)), new OcelotEntityModel(arg.getPart(EntityModelLayers.OCELOT_BABY)), 0.4f);
    }

    @Override
    public Identifier getTexture(FelineEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public FelineEntityRenderState createRenderState() {
        return new FelineEntityRenderState();
    }

    @Override
    public void updateRenderState(OcelotEntity arg, FelineEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.inSneakingPose = arg.isInSneakingPose();
        arg2.sprinting = arg.isSprinting();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FelineEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

