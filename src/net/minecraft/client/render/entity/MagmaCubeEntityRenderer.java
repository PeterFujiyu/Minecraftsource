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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityRenderer
extends MobEntityRenderer<MagmaCubeEntity, SlimeEntityRenderState, MagmaCubeEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/magmacube.png");

    public MagmaCubeEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new MagmaCubeEntityModel(arg.getPart(EntityModelLayers.MAGMA_CUBE)), 0.25f);
    }

    @Override
    protected int getBlockLight(MagmaCubeEntity arg, BlockPos arg2) {
        return 15;
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public SlimeEntityRenderState createRenderState() {
        return new SlimeEntityRenderState();
    }

    @Override
    public void updateRenderState(MagmaCubeEntity arg, SlimeEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.stretch = MathHelper.lerp(f, arg.lastStretch, arg.stretch);
        arg2.size = arg.getSize();
    }

    @Override
    protected float getShadowRadius(SlimeEntityRenderState arg) {
        return (float)arg.size * 0.25f;
    }

    @Override
    protected void scale(SlimeEntityRenderState arg, MatrixStack arg2) {
        int i = arg.size;
        float f = arg.stretch / ((float)i * 0.5f + 1.0f);
        float g = 1.0f / (f + 1.0f);
        arg2.scale(g * (float)i, 1.0f / g * (float)i, g * (float)i);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState arg) {
        return this.getShadowRadius((SlimeEntityRenderState)arg);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SlimeEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((SlimeEntityRenderState)state);
    }
}

