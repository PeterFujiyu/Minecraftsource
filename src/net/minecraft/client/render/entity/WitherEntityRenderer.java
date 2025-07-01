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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityRenderer
extends MobEntityRenderer<WitherEntity, WitherEntityRenderState, WitherEntityModel> {
    private static final Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither.png");

    public WitherEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new WitherEntityModel(arg.getPart(EntityModelLayers.WITHER)), 1.0f);
        this.addFeature(new WitherArmorFeatureRenderer(this, arg.getEntityModels()));
    }

    @Override
    protected int getBlockLight(WitherEntity arg, BlockPos arg2) {
        return 15;
    }

    @Override
    public Identifier getTexture(WitherEntityRenderState arg) {
        int i = MathHelper.floor(arg.invulnerableTimer);
        if (i <= 0 || i <= 80 && i / 5 % 2 == 1) {
            return TEXTURE;
        }
        return INVULNERABLE_TEXTURE;
    }

    @Override
    public WitherEntityRenderState createRenderState() {
        return new WitherEntityRenderState();
    }

    @Override
    protected void scale(WitherEntityRenderState arg, MatrixStack arg2) {
        float f = 2.0f;
        if (arg.invulnerableTimer > 0.0f) {
            f -= arg.invulnerableTimer / 220.0f * 0.5f;
        }
        arg2.scale(f, f, f);
    }

    @Override
    public void updateRenderState(WitherEntity arg, WitherEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        int i = arg.getInvulnerableTimer();
        arg2.invulnerableTimer = i > 0 ? (float)i - f : 0.0f;
        System.arraycopy(arg.getSideHeadPitches(), 0, arg2.sideHeadPitches, 0, arg2.sideHeadPitches.length);
        System.arraycopy(arg.getSideHeadYaws(), 0, arg2.sideHeadYaws, 0, arg2.sideHeadYaws.length);
        arg2.renderOverlay = arg.shouldRenderOverlay();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WitherEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

