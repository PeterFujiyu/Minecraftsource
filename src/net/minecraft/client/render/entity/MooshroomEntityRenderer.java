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
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.MooshroomEntityRenderState;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MooshroomEntityRenderer
extends AgeableMobEntityRenderer<MooshroomEntity, MooshroomEntityRenderState, CowEntityModel> {
    private static final Map<MooshroomEntity.Type, Identifier> TEXTURES = Util.make(Maps.newHashMap(), map -> {
        map.put(MooshroomEntity.Type.BROWN, Identifier.ofVanilla("textures/entity/cow/brown_mooshroom.png"));
        map.put(MooshroomEntity.Type.RED, Identifier.ofVanilla("textures/entity/cow/red_mooshroom.png"));
    });

    public MooshroomEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new CowEntityModel(arg.getPart(EntityModelLayers.MOOSHROOM)), new CowEntityModel(arg.getPart(EntityModelLayers.MOOSHROOM_BABY)), 0.7f);
        this.addFeature(new MooshroomMushroomFeatureRenderer(this, arg.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(MooshroomEntityRenderState arg) {
        return TEXTURES.get(arg.type);
    }

    @Override
    public MooshroomEntityRenderState createRenderState() {
        return new MooshroomEntityRenderState();
    }

    @Override
    public void updateRenderState(MooshroomEntity arg, MooshroomEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.type = arg.getVariant();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((MooshroomEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

