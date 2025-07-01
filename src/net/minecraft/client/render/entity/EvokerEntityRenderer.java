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
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerEntityRenderer<T extends SpellcastingIllagerEntity>
extends IllagerEntityRenderer<T, EvokerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker.png");

    public EvokerEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new IllagerEntityModel(arg.getPart(EntityModelLayers.EVOKER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<EvokerEntityRenderState, IllagerEntityModel<EvokerEntityRenderState>>(this, (FeatureRendererContext)this){

            @Override
            public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, EvokerEntityRenderState arg3, float f, float g) {
                if (arg3.spellcasting) {
                    super.render(arg, arg2, i, arg3, f, g);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(EvokerEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public EvokerEntityRenderState createRenderState() {
        return new EvokerEntityRenderState();
    }

    @Override
    public void updateRenderState(T arg, EvokerEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.spellcasting = ((SpellcastingIllagerEntity)arg).isSpellcasting();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((EvokerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

