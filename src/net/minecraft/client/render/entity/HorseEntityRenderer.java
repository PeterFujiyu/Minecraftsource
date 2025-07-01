/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public final class HorseEntityRenderer
extends AbstractHorseEntityRenderer<HorseEntity, HorseEntityRenderState, HorseEntityModel> {
    private static final Map<HorseColor, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseColor.class), map -> {
        map.put(HorseColor.WHITE, Identifier.ofVanilla("textures/entity/horse/horse_white.png"));
        map.put(HorseColor.CREAMY, Identifier.ofVanilla("textures/entity/horse/horse_creamy.png"));
        map.put(HorseColor.CHESTNUT, Identifier.ofVanilla("textures/entity/horse/horse_chestnut.png"));
        map.put(HorseColor.BROWN, Identifier.ofVanilla("textures/entity/horse/horse_brown.png"));
        map.put(HorseColor.BLACK, Identifier.ofVanilla("textures/entity/horse/horse_black.png"));
        map.put(HorseColor.GRAY, Identifier.ofVanilla("textures/entity/horse/horse_gray.png"));
        map.put(HorseColor.DARK_BROWN, Identifier.ofVanilla("textures/entity/horse/horse_darkbrown.png"));
    });

    public HorseEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new HorseEntityModel(arg.getPart(EntityModelLayers.HORSE)), new HorseEntityModel(arg.getPart(EntityModelLayers.HORSE_BABY)));
        this.addFeature(new HorseMarkingFeatureRenderer(this));
        this.addFeature(new HorseArmorFeatureRenderer(this, arg.getEntityModels(), arg.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(HorseEntityRenderState arg) {
        return TEXTURES.get(arg.color);
    }

    @Override
    public HorseEntityRenderState createRenderState() {
        return new HorseEntityRenderState();
    }

    @Override
    public void updateRenderState(HorseEntity arg, HorseEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.color = arg.getVariant();
        arg2.marking = arg.getMarking();
        arg2.armor = arg.getBodyArmor().copy();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HorseEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

