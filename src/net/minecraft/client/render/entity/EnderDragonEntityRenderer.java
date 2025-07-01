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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DragonEntityModel;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends EntityRenderer<EnderDragonEntity, EnderDragonEntityRenderState> {
    public static final Identifier CRYSTAL_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal_beam.png");
    private static final Identifier EXPLOSION_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_exploding.png");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon.png");
    private static final Identifier EYE_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderLayer DRAGON_CUTOUT = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private static final RenderLayer DRAGON_DECAL = RenderLayer.getEntityDecal(TEXTURE);
    private static final RenderLayer DRAGON_EYES = RenderLayer.getEyes(EYE_TEXTURE);
    private static final RenderLayer CRYSTAL_BEAM_LAYER = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonEntityModel model;

    public EnderDragonEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg);
        this.shadowRadius = 0.5f;
        this.model = new DragonEntityModel(arg.getPart(EntityModelLayers.ENDER_DRAGON));
    }

    @Override
    public void render(EnderDragonEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        float f = arg.getLerpedFrame(7).yRot();
        float g = (float)(arg.getLerpedFrame(5).y() - arg.getLerpedFrame(10).y());
        arg2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
        arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * 10.0f));
        arg2.translate(0.0f, 0.0f, 1.0f);
        arg2.scale(-1.0f, -1.0f, 1.0f);
        arg2.translate(0.0f, -1.501f, 0.0f);
        this.model.setAngles(arg);
        if (arg.ticksSinceDeath > 0.0f) {
            float h = arg.ticksSinceDeath / 200.0f;
            int j = ColorHelper.withAlpha(MathHelper.floor(h * 255.0f), Colors.WHITE);
            VertexConsumer lv = arg3.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEXTURE));
            this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, j);
            VertexConsumer lv2 = arg3.getBuffer(DRAGON_DECAL);
            this.model.render(arg2, lv2, i, OverlayTexture.getUv(0.0f, arg.hurt));
        } else {
            VertexConsumer lv3 = arg3.getBuffer(DRAGON_CUTOUT);
            this.model.render(arg2, lv3, i, OverlayTexture.getUv(0.0f, arg.hurt));
        }
        VertexConsumer lv3 = arg3.getBuffer(DRAGON_EYES);
        this.model.render(arg2, lv3, i, OverlayTexture.DEFAULT_UV);
        if (arg.ticksSinceDeath > 0.0f) {
            float k = arg.ticksSinceDeath / 200.0f;
            arg2.push();
            arg2.translate(0.0f, -1.0f, -2.0f);
            EnderDragonEntityRenderer.renderDeathAnimation(arg2, k, arg3.getBuffer(RenderLayer.getDragonRays()));
            EnderDragonEntityRenderer.renderDeathAnimation(arg2, k, arg3.getBuffer(RenderLayer.getDragonRaysDepth()));
            arg2.pop();
        }
        arg2.pop();
        if (arg.crystalBeamPos != null) {
            EnderDragonEntityRenderer.renderCrystalBeam((float)arg.crystalBeamPos.x, (float)arg.crystalBeamPos.y, (float)arg.crystalBeamPos.z, arg.age, arg2, arg3, i);
        }
        super.render(arg, arg2, arg3, i);
    }

    private static void renderDeathAnimation(MatrixStack matrices, float animationProgress, VertexConsumer vertexCOnsumer) {
        matrices.push();
        float g = Math.min(animationProgress > 0.8f ? (animationProgress - 0.8f) / 0.2f : 0.0f, 1.0f);
        int i = ColorHelper.fromFloats(1.0f - g, 1.0f, 1.0f, 1.0f);
        int j = 0xFF00FF;
        Random lv = Random.create(432L);
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f2 = new Vector3f();
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        int k = MathHelper.floor((animationProgress + animationProgress * animationProgress) / 2.0f * 60.0f);
        for (int l = 0; l < k; ++l) {
            quaternionf.rotationXYZ(lv.nextFloat() * ((float)Math.PI * 2), lv.nextFloat() * ((float)Math.PI * 2), lv.nextFloat() * ((float)Math.PI * 2)).rotateXYZ(lv.nextFloat() * ((float)Math.PI * 2), lv.nextFloat() * ((float)Math.PI * 2), lv.nextFloat() * ((float)Math.PI * 2) + animationProgress * 1.5707964f);
            matrices.multiply(quaternionf);
            float h = lv.nextFloat() * 20.0f + 5.0f + g * 10.0f;
            float m = lv.nextFloat() * 2.0f + 1.0f + g * 2.0f;
            vector3f2.set(-HALF_SQRT_3 * m, h, -0.5f * m);
            vector3f3.set(HALF_SQRT_3 * m, h, -0.5f * m);
            vector3f4.set(0.0f, h, m);
            MatrixStack.Entry lv2 = matrices.peek();
            vertexCOnsumer.vertex(lv2, vector3f).color(i);
            vertexCOnsumer.vertex(lv2, vector3f2).color(0xFF00FF);
            vertexCOnsumer.vertex(lv2, vector3f3).color(0xFF00FF);
            vertexCOnsumer.vertex(lv2, vector3f).color(i);
            vertexCOnsumer.vertex(lv2, vector3f3).color(0xFF00FF);
            vertexCOnsumer.vertex(lv2, vector3f4).color(0xFF00FF);
            vertexCOnsumer.vertex(lv2, vector3f).color(i);
            vertexCOnsumer.vertex(lv2, vector3f4).color(0xFF00FF);
            vertexCOnsumer.vertex(lv2, vector3f2).color(0xFF00FF);
        }
        matrices.pop();
    }

    public static void renderCrystalBeam(float dx, float dy, float dz, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float k = MathHelper.sqrt(dx * dx + dz * dz);
        float l = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        matrices.push();
        matrices.translate(0.0f, 2.0f, 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2(dz, dx)) - 1.5707964f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2(k, dy)) - 1.5707964f));
        VertexConsumer lv = vertexConsumers.getBuffer(CRYSTAL_BEAM_LAYER);
        float m = 0.0f - tickDelta * 0.01f;
        float n = l / 32.0f - tickDelta * 0.01f;
        int o = 8;
        float p = 0.0f;
        float q = 0.75f;
        float r = 0.0f;
        MatrixStack.Entry lv2 = matrices.peek();
        for (int s = 1; s <= 8; ++s) {
            float t = MathHelper.sin((float)s * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float u = MathHelper.cos((float)s * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float v = (float)s / 8.0f;
            lv.vertex(lv2, p * 0.2f, q * 0.2f, 0.0f).color(Colors.BLACK).texture(r, m).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(lv2, 0.0f, -1.0f, 0.0f);
            lv.vertex(lv2, p, q, l).color(Colors.WHITE).texture(r, n).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(lv2, 0.0f, -1.0f, 0.0f);
            lv.vertex(lv2, t, u, l).color(Colors.WHITE).texture(v, n).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(lv2, 0.0f, -1.0f, 0.0f);
            lv.vertex(lv2, t * 0.2f, u * 0.2f, 0.0f).color(Colors.BLACK).texture(v, m).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(lv2, 0.0f, -1.0f, 0.0f);
            p = t;
            q = u;
            r = v;
        }
        matrices.pop();
    }

    @Override
    public EnderDragonEntityRenderState createRenderState() {
        return new EnderDragonEntityRenderState();
    }

    @Override
    public void updateRenderState(EnderDragonEntity arg, EnderDragonEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.wingPosition = MathHelper.lerp(f, arg.prevWingPosition, arg.wingPosition);
        arg2.ticksSinceDeath = arg.ticksSinceDeath > 0 ? (float)arg.ticksSinceDeath + f : 0.0f;
        arg2.hurt = arg.hurtTime > 0;
        EndCrystalEntity lv = arg.connectedCrystal;
        if (lv != null) {
            Vec3d lv2 = lv.getLerpedPos(f).add(0.0, EndCrystalEntityRenderer.getYOffset((float)lv.endCrystalAge + f), 0.0);
            arg2.crystalBeamPos = lv2.subtract(arg.getLerpedPos(f));
        } else {
            arg2.crystalBeamPos = null;
        }
        Phase lv3 = arg.getPhaseManager().getCurrent();
        arg2.inLandingOrTakeoffPhase = lv3 == PhaseType.LANDING || lv3 == PhaseType.TAKEOFF;
        arg2.sittingOrHovering = lv3.isSittingOrHovering();
        BlockPos lv4 = arg.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(arg.getFightOrigin()));
        arg2.squaredDistanceFromOrigin = lv4.getSquaredDistance(arg.getPos());
        arg2.tickDelta = arg.isDead() ? 0.0f : f;
        arg2.frameTracker.copyFrom(arg.frameTracker);
    }

    @Override
    protected boolean canBeCulled(EnderDragonEntity arg) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean canBeCulled(Entity entity) {
        return this.canBeCulled((EnderDragonEntity)entity);
    }
}

