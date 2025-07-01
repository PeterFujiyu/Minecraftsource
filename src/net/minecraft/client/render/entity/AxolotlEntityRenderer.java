/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityRenderer
extends AgeableMobEntityRenderer<AxolotlEntity, AxolotlEntityRenderState, AxolotlEntityModel> {
    private static final Map<AxolotlEntity.Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), variants -> {
        for (AxolotlEntity.Variant lv : AxolotlEntity.Variant.values()) {
            variants.put(lv, Identifier.ofVanilla(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", lv.getName())));
        }
    });

    public AxolotlEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new AxolotlEntityModel(arg.getPart(EntityModelLayers.AXOLOTL)), new AxolotlEntityModel(arg.getPart(EntityModelLayers.AXOLOTL_BABY)), 0.5f);
    }

    @Override
    public Identifier getTexture(AxolotlEntityRenderState arg) {
        return TEXTURES.get(arg.variant);
    }

    @Override
    public AxolotlEntityRenderState createRenderState() {
        return new AxolotlEntityRenderState();
    }

    @Override
    public void updateRenderState(AxolotlEntity arg, AxolotlEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.variant = arg.getVariant();
        arg2.playingDeadValue = arg.playingDeadFf.getValue(f);
        arg2.inWaterValue = arg.inWaterFf.getValue(f);
        arg2.onGroundValue = arg.onGroundFf.getValue(f);
        arg2.isMovingValue = arg.isMovingFf.getValue(f);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((AxolotlEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

