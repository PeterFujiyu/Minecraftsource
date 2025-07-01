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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityRenderer
extends EntityRenderer<EndCrystalEntity, EndCrystalEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private final EndCrystalEntityModel model;

    public EndCrystalEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.shadowRadius = 0.5f;
        this.model = new EndCrystalEntityModel(arg.getPart(EntityModelLayers.END_CRYSTAL));
    }

    @Override
    public void render(EndCrystalEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.scale(2.0f, 2.0f, 2.0f);
        arg2.translate(0.0f, -0.5f, 0.0f);
        this.model.setAngles(arg);
        this.model.render(arg2, arg3.getBuffer(END_CRYSTAL), i, OverlayTexture.DEFAULT_UV);
        arg2.pop();
        Vec3d lv = arg.beamOffset;
        if (lv != null) {
            float f = EndCrystalEntityRenderer.getYOffset(arg.age);
            float g = (float)lv.x;
            float h = (float)lv.y;
            float j = (float)lv.z;
            arg2.translate(lv);
            EnderDragonEntityRenderer.renderCrystalBeam(-g, -h + f, -j, arg.age, arg2, arg3, i);
        }
        super.render(arg, arg2, arg3, i);
    }

    public static float getYOffset(float f) {
        float g = MathHelper.sin(f * 0.2f) / 2.0f + 0.5f;
        g = (g * g + g) * 0.4f;
        return g - 1.4f;
    }

    @Override
    public EndCrystalEntityRenderState createRenderState() {
        return new EndCrystalEntityRenderState();
    }

    @Override
    public void updateRenderState(EndCrystalEntity arg, EndCrystalEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.age = (float)arg.endCrystalAge + f;
        arg2.baseVisible = arg.shouldShowBottom();
        BlockPos lv = arg.getBeamTarget();
        arg2.beamOffset = lv != null ? Vec3d.ofCenter(lv).subtract(arg.getLerpedPos(f)) : null;
    }

    @Override
    public boolean shouldRender(EndCrystalEntity arg, Frustum arg2, double d, double e, double f) {
        return super.shouldRender(arg, arg2, d, e, f) || arg.getBeamTarget() != null;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

