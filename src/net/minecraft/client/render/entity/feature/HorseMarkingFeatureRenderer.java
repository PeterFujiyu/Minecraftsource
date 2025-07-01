/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class HorseMarkingFeatureRenderer
extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
    private static final Map<HorseMarking, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseMarking.class), textures -> {
        textures.put(HorseMarking.NONE, null);
        textures.put(HorseMarking.WHITE, Identifier.ofVanilla("textures/entity/horse/horse_markings_white.png"));
        textures.put(HorseMarking.WHITE_FIELD, Identifier.ofVanilla("textures/entity/horse/horse_markings_whitefield.png"));
        textures.put(HorseMarking.WHITE_DOTS, Identifier.ofVanilla("textures/entity/horse/horse_markings_whitedots.png"));
        textures.put(HorseMarking.BLACK_DOTS, Identifier.ofVanilla("textures/entity/horse/horse_markings_blackdots.png"));
    });

    public HorseMarkingFeatureRenderer(FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, HorseEntityRenderState arg3, float f, float g) {
        Identifier lv = TEXTURES.get((Object)arg3.marking);
        if (lv == null || arg3.invisible) {
            return;
        }
        VertexConsumer lv2 = arg2.getBuffer(RenderLayer.getEntityTranslucent(lv));
        ((HorseEntityModel)this.getContextModel()).render(arg, lv2, i, LivingEntityRenderer.getOverlay(arg3, 0.0f));
    }
}

