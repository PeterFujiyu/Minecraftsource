/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends EntityRenderer<T, S>
implements FeatureRendererContext<S, M> {
    private static final float field_32939 = 0.1f;
    protected M model;
    protected final ItemModelManager itemModelResolver;
    protected final List<FeatureRenderer<S, M>> features = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx);
        this.itemModelResolver = ctx.getItemModelManager();
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    protected final boolean addFeature(FeatureRenderer<S, M> feature) {
        return this.features.add(feature);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    protected Box getBoundingBox(T arg) {
        Box lv = super.getBoundingBox(arg);
        if (((LivingEntity)arg).getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
            float f = 0.5f;
            return lv.expand(0.5, 0.5, 0.5);
        }
        return lv;
    }

    @Override
    public void render(S arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        Direction lv;
        arg2.push();
        if (((LivingEntityRenderState)arg).isInPose(EntityPose.SLEEPING) && (lv = ((LivingEntityRenderState)arg).sleepingDirection) != null) {
            float f = ((LivingEntityRenderState)arg).standingEyeHeight - 0.1f;
            arg2.translate((float)(-lv.getOffsetX()) * f, 0.0f, (float)(-lv.getOffsetZ()) * f);
        }
        float g = ((LivingEntityRenderState)arg).baseScale;
        arg2.scale(g, g, g);
        this.setupTransforms(arg, arg2, ((LivingEntityRenderState)arg).bodyYaw, g);
        arg2.scale(-1.0f, -1.0f, 1.0f);
        this.scale(arg, arg2);
        arg2.translate(0.0f, -1.501f, 0.0f);
        ((EntityModel)this.model).setAngles(arg);
        boolean bl = this.isVisible(arg);
        boolean bl2 = !bl && !((LivingEntityRenderState)arg).invisibleToPlayer;
        RenderLayer lv2 = this.getRenderLayer(arg, bl, bl2, ((LivingEntityRenderState)arg).hasOutline);
        if (lv2 != null) {
            VertexConsumer lv3 = arg3.getBuffer(lv2);
            int j = LivingEntityRenderer.getOverlay(arg, this.getAnimationCounter(arg));
            int k = bl2 ? 0x26FFFFFF : Colors.WHITE;
            int l = ColorHelper.mix(k, this.getMixColor(arg));
            ((Model)this.model).render(arg2, lv3, i, j, l);
        }
        if (this.shouldRenderFeatures(arg)) {
            for (FeatureRenderer<S, M> lv4 : this.features) {
                lv4.render(arg2, arg3, i, arg, ((LivingEntityRenderState)arg).yawDegrees, ((LivingEntityRenderState)arg).pitch);
            }
        }
        arg2.pop();
        super.render(arg, arg2, arg3, i);
    }

    protected boolean shouldRenderFeatures(S state) {
        return true;
    }

    protected int getMixColor(S state) {
        return -1;
    }

    public abstract Identifier getTexture(S var1);

    @Nullable
    protected RenderLayer getRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier lv = this.getTexture(state);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(lv);
        }
        if (showBody) {
            return ((Model)this.model).getLayer(lv);
        }
        if (showOutline) {
            return RenderLayer.getOutline(lv);
        }
        return null;
    }

    public static int getOverlay(LivingEntityRenderState state, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(state.hurt));
    }

    protected boolean isVisible(S state) {
        return !((LivingEntityRenderState)state).invisible;
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(S state) {
        return ((LivingEntityRenderState)state).shaking;
    }

    protected void setupTransforms(S state, MatrixStack matrices, float bodyYaw, float baseHeight) {
        if (this.isShaking(state)) {
            bodyYaw += (float)(Math.cos((float)MathHelper.floor(((LivingEntityRenderState)state).age) * 3.25f) * Math.PI * (double)0.4f);
        }
        if (!((LivingEntityRenderState)state).isInPose(EntityPose.SLEEPING)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (((LivingEntityRenderState)state).deathTime > 0.0f) {
            float h = (((LivingEntityRenderState)state).deathTime - 1.0f) / 20.0f * 1.6f;
            if ((h = MathHelper.sqrt(h)) > 1.0f) {
                h = 1.0f;
            }
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h * this.method_3919()));
        } else if (((LivingEntityRenderState)state).usingRiptide) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - ((LivingEntityRenderState)state).pitch));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((LivingEntityRenderState)state).age * -75.0f));
        } else if (((LivingEntityRenderState)state).isInPose(EntityPose.SLEEPING)) {
            Direction lv = ((LivingEntityRenderState)state).sleepingDirection;
            float i = lv != null ? LivingEntityRenderer.getYaw(lv) : bodyYaw;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.method_3919()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else if (((LivingEntityRenderState)state).flipUpsideDown) {
            matrices.translate(0.0f, (((LivingEntityRenderState)state).height + 0.1f) / baseHeight, 0.0f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        }
    }

    protected float method_3919() {
        return 90.0f;
    }

    protected float getAnimationCounter(S state) {
        return 0.0f;
    }

    protected void scale(S state, MatrixStack matrices) {
    }

    @Override
    protected boolean hasLabel(T arg, double d) {
        boolean bl;
        if (((Entity)arg).isSneaky()) {
            float f = 32.0f;
            if (d >= 1024.0) {
                return false;
            }
        }
        MinecraftClient lv = MinecraftClient.getInstance();
        ClientPlayerEntity lv2 = lv.player;
        boolean bl2 = bl = !((Entity)arg).isInvisibleTo(lv2);
        if (arg != lv2) {
            Team lv3 = ((Entity)arg).getScoreboardTeam();
            Team lv4 = lv2.getScoreboardTeam();
            if (lv3 != null) {
                AbstractTeam.VisibilityRule lv5 = ((AbstractTeam)lv3).getNameTagVisibilityRule();
                switch (lv5) {
                    case ALWAYS: {
                        return bl;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return lv4 == null ? bl : lv3.isEqual(lv4) && (((AbstractTeam)lv3).shouldShowFriendlyInvisibles() || bl);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return lv4 == null ? bl : !lv3.isEqual(lv4) && bl;
                    }
                }
                return true;
            }
        }
        return MinecraftClient.isHudEnabled() && arg != lv.getCameraEntity() && bl && !((Entity)arg).hasPassengers();
    }

    public static boolean shouldFlipUpsideDown(LivingEntity entity) {
        String string;
        if ((entity instanceof PlayerEntity || entity.hasCustomName()) && ("Dinnerbone".equals(string = Formatting.strip(entity.getName().getString())) || "Grumm".equals(string))) {
            return !(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
        }
        return false;
    }

    @Override
    protected float getShadowRadius(S arg) {
        return super.getShadowRadius(arg) * ((LivingEntityRenderState)arg).baseScale;
    }

    @Override
    public void updateRenderState(T arg, S arg2, float f) {
        BlockItem lv3;
        super.updateRenderState(arg, arg2, f);
        float g = MathHelper.lerpAngleDegrees(f, ((LivingEntity)arg).prevHeadYaw, ((LivingEntity)arg).headYaw);
        ((LivingEntityRenderState)arg2).bodyYaw = LivingEntityRenderer.clampBodyYaw(arg, g, f);
        ((LivingEntityRenderState)arg2).yawDegrees = MathHelper.wrapDegrees(g - ((LivingEntityRenderState)arg2).bodyYaw);
        ((LivingEntityRenderState)arg2).pitch = ((Entity)arg).getLerpedPitch(f);
        ((LivingEntityRenderState)arg2).customName = ((Entity)arg).getCustomName();
        ((LivingEntityRenderState)arg2).flipUpsideDown = LivingEntityRenderer.shouldFlipUpsideDown(arg);
        if (((LivingEntityRenderState)arg2).flipUpsideDown) {
            ((LivingEntityRenderState)arg2).pitch *= -1.0f;
            ((LivingEntityRenderState)arg2).yawDegrees *= -1.0f;
        }
        if (!((Entity)arg).hasVehicle() && ((LivingEntity)arg).isAlive()) {
            ((LivingEntityRenderState)arg2).limbFrequency = ((LivingEntity)arg).limbAnimator.getPos(f);
            ((LivingEntityRenderState)arg2).limbAmplitudeMultiplier = ((LivingEntity)arg).limbAnimator.getSpeed(f);
        } else {
            ((LivingEntityRenderState)arg2).limbFrequency = 0.0f;
            ((LivingEntityRenderState)arg2).limbAmplitudeMultiplier = 0.0f;
        }
        Entity entity = ((Entity)arg).getVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)entity;
            ((LivingEntityRenderState)arg2).headItemAnimationProgress = lv.limbAnimator.getPos(f);
        } else {
            ((LivingEntityRenderState)arg2).headItemAnimationProgress = ((LivingEntityRenderState)arg2).limbFrequency;
        }
        ((LivingEntityRenderState)arg2).baseScale = ((LivingEntity)arg).getScale();
        ((LivingEntityRenderState)arg2).ageScale = ((LivingEntity)arg).getScaleFactor();
        ((LivingEntityRenderState)arg2).pose = ((Entity)arg).getPose();
        ((LivingEntityRenderState)arg2).sleepingDirection = ((LivingEntity)arg).getSleepingDirection();
        if (((LivingEntityRenderState)arg2).sleepingDirection != null) {
            ((LivingEntityRenderState)arg2).standingEyeHeight = ((Entity)arg).getEyeHeight(EntityPose.STANDING);
        }
        ((LivingEntityRenderState)arg2).shaking = ((Entity)arg).isFrozen();
        ((LivingEntityRenderState)arg2).baby = ((LivingEntity)arg).isBaby();
        ((LivingEntityRenderState)arg2).touchingWater = ((Entity)arg).isTouchingWater();
        ((LivingEntityRenderState)arg2).usingRiptide = ((LivingEntity)arg).isUsingRiptide();
        ((LivingEntityRenderState)arg2).hurt = ((LivingEntity)arg).hurtTime > 0 || ((LivingEntity)arg).deathTime > 0;
        ItemStack lv2 = ((LivingEntity)arg).getEquippedStack(EquipmentSlot.HEAD);
        ItemConvertible itemConvertible = lv2.getItem();
        if (itemConvertible instanceof BlockItem && (itemConvertible = (lv3 = (BlockItem)itemConvertible).getBlock()) instanceof AbstractSkullBlock) {
            AbstractSkullBlock lv4 = (AbstractSkullBlock)itemConvertible;
            ((LivingEntityRenderState)arg2).wearingSkullType = lv4.getSkullType();
            ((LivingEntityRenderState)arg2).wearingSkullProfile = lv2.get(DataComponentTypes.PROFILE);
            ((LivingEntityRenderState)arg2).headItemRenderState.clear();
        } else {
            ((LivingEntityRenderState)arg2).wearingSkullType = null;
            ((LivingEntityRenderState)arg2).wearingSkullProfile = null;
            if (!ArmorFeatureRenderer.hasModel(lv2, EquipmentSlot.HEAD)) {
                this.itemModelResolver.updateForLivingEntity(((LivingEntityRenderState)arg2).headItemRenderState, lv2, ModelTransformationMode.HEAD, false, (LivingEntity)arg);
            } else {
                ((LivingEntityRenderState)arg2).headItemRenderState.clear();
            }
        }
        ((LivingEntityRenderState)arg2).deathTime = ((LivingEntity)arg).deathTime > 0 ? (float)((LivingEntity)arg).deathTime + f : 0.0f;
        MinecraftClient lv5 = MinecraftClient.getInstance();
        ((LivingEntityRenderState)arg2).invisibleToPlayer = ((LivingEntityRenderState)arg2).invisible && ((Entity)arg).isInvisibleTo(lv5.player);
        ((LivingEntityRenderState)arg2).hasOutline = lv5.hasOutline((Entity)arg);
    }

    private static float clampBodyYaw(LivingEntity entity, float degrees, float tickDelta) {
        Entity entity2 = entity.getVehicle();
        if (entity2 instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)entity2;
            float h = MathHelper.lerpAngleDegrees(tickDelta, lv.prevBodyYaw, lv.bodyYaw);
            float i = 85.0f;
            float j = MathHelper.clamp(MathHelper.wrapDegrees(degrees - h), -85.0f, 85.0f);
            h = degrees - j;
            if (Math.abs(j) > 50.0f) {
                h += j * 0.2f;
            }
            return h;
        }
        return MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((S)((LivingEntityRenderState)state));
    }
}

