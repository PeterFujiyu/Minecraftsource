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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SheepWoolFeatureRenderer
extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/sheep/sheep_fur.png");
    private final EntityModel<SheepEntityRenderState> woolModel;
    private final EntityModel<SheepEntityRenderState> babyWoolModel;

    public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.woolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL));
        this.babyWoolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, SheepEntityRenderState arg3, float f, float g) {
        int r;
        EntityModel<SheepEntityRenderState> lv;
        if (arg3.sheared) {
            return;
        }
        EntityModel<SheepEntityRenderState> entityModel = lv = arg3.baby ? this.babyWoolModel : this.woolModel;
        if (arg3.invisible) {
            if (arg3.hasOutline) {
                lv.setAngles(arg3);
                VertexConsumer lv2 = arg2.getBuffer(RenderLayer.getOutline(SKIN));
                lv.render(arg, lv2, i, LivingEntityRenderer.getOverlay(arg3, 0.0f), -16777216);
            }
            return;
        }
        if (arg3.customName != null && "jeb_".equals(arg3.customName.getString())) {
            int j = 25;
            int k = MathHelper.floor(arg3.age);
            int l = k / 25 + arg3.id;
            int m = DyeColor.values().length;
            int n = l % m;
            int o = (l + 1) % m;
            float h = ((float)(k % 25) + MathHelper.fractionalPart(arg3.age)) / 25.0f;
            int p = SheepEntity.getRgbColor(DyeColor.byId(n));
            int q = SheepEntity.getRgbColor(DyeColor.byId(o));
            r = ColorHelper.lerp(h, p, q);
        } else {
            r = SheepEntity.getRgbColor(arg3.color);
        }
        SheepWoolFeatureRenderer.render(lv, SKIN, arg, arg2, i, arg3, r);
    }
}

