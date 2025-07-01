/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer
extends FeatureRenderer<ZombieEntityRenderState, DrownedEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedEntityModel model;
    private final DrownedEntityModel babyModel;

    public DrownedOverlayFeatureRenderer(FeatureRendererContext<ZombieEntityRenderState, DrownedEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_OUTER));
        this.babyModel = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_BABY_OUTER));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, ZombieEntityRenderState arg3, float f, float g) {
        DrownedEntityModel lv = arg3.baby ? this.babyModel : this.model;
        DrownedOverlayFeatureRenderer.render(lv, SKIN, arg, arg2, i, arg3, -1);
    }
}

