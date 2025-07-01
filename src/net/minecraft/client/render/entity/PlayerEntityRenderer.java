/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
    public PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, new PlayerEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new ArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)), ctx.getEquipmentRenderer()));
        this.addFeature(new PlayerHeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<PlayerEntityModel>(this, ctx));
        this.addFeature(new Deadmau5FeatureRenderer(this, ctx.getEntityModels()));
        this.addFeature(new CapeFeatureRenderer(this, ctx.getEntityModels(), ctx.getEquipmentModelLoader()));
        this.addFeature(new HeadFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(this, ctx.getEntityModels()));
        this.addFeature(new ElytraFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(this, ctx.getEntityModels(), ctx.getEquipmentRenderer()));
        this.addFeature(new ShoulderParrotFeatureRenderer(this, ctx.getEntityModels()));
        this.addFeature(new TridentRiptideFeatureRenderer(this, ctx.getEntityModels()));
        this.addFeature(new StuckStingersFeatureRenderer<PlayerEntityModel>(this, ctx));
    }

    @Override
    protected boolean shouldRenderFeatures(PlayerEntityRenderState arg) {
        return !arg.spectator;
    }

    @Override
    public Vec3d getPositionOffset(PlayerEntityRenderState arg) {
        Vec3d lv = super.getPositionOffset(arg);
        if (arg.isInSneakingPose) {
            return lv.add(0.0, (double)(arg.baseScale * -2.0f) / 16.0, 0.0);
        }
        return lv;
    }

    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Arm arm) {
        ItemStack lv = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack lv2 = player.getStackInHand(Hand.OFF_HAND);
        BipedEntityModel.ArmPose lv3 = PlayerEntityRenderer.getArmPose(player, lv, Hand.MAIN_HAND);
        BipedEntityModel.ArmPose lv4 = PlayerEntityRenderer.getArmPose(player, lv2, Hand.OFF_HAND);
        if (lv3.isTwoHanded()) {
            BipedEntityModel.ArmPose armPose = lv4 = lv2.isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        }
        if (player.getMainArm() == arm) {
            return lv3;
        }
        return lv4;
    }

    private static BipedEntityModel.ArmPose getArmPose(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
            UseAction lv = stack.getUseAction();
            if (lv == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (lv == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (lv == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.THROW_SPEAR;
            }
            if (lv == UseAction.CROSSBOW) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (lv == UseAction.SPYGLASS) {
                return BipedEntityModel.ArmPose.SPYGLASS;
            }
            if (lv == UseAction.TOOT_HORN) {
                return BipedEntityModel.ArmPose.TOOT_HORN;
            }
            if (lv == UseAction.BRUSH) {
                return BipedEntityModel.ArmPose.BRUSH;
            }
        } else if (!player.handSwinging && stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(stack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    @Override
    public Identifier getTexture(PlayerEntityRenderState arg) {
        return arg.skinTextures.texture();
    }

    @Override
    protected void scale(PlayerEntityRenderState arg, MatrixStack arg2) {
        float f = 0.9375f;
        arg2.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderLabelIfPresent(PlayerEntityRenderState arg, Text arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        arg3.push();
        if (arg.playerName != null) {
            super.renderLabelIfPresent(arg, arg.playerName, arg3, arg4, i);
            Objects.requireNonNull(this.getTextRenderer());
            arg3.translate(0.0f, 9.0f * 1.15f * 0.025f, 0.0f);
        }
        super.renderLabelIfPresent(arg, arg2, arg3, arg4, i);
        arg3.pop();
    }

    @Override
    public PlayerEntityRenderState createRenderState() {
        return new PlayerEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractClientPlayerEntity arg, PlayerEntityRenderState arg2, float f) {
        ItemStack lv5;
        super.updateRenderState(arg, arg2, f);
        BipedEntityRenderer.updateBipedRenderState(arg, arg2, f, this.itemModelResolver);
        arg2.leftArmPose = PlayerEntityRenderer.getArmPose(arg, Arm.LEFT);
        arg2.rightArmPose = PlayerEntityRenderer.getArmPose(arg, Arm.RIGHT);
        arg2.skinTextures = arg.getSkinTextures();
        arg2.stuckArrowCount = arg.getStuckArrowCount();
        arg2.stingerCount = arg.getStingerCount();
        arg2.itemUseTimeLeft = arg.getItemUseTimeLeft();
        arg2.handSwinging = arg.handSwinging;
        arg2.spectator = arg.isSpectator();
        arg2.hatVisible = arg.isPartVisible(PlayerModelPart.HAT);
        arg2.jacketVisible = arg.isPartVisible(PlayerModelPart.JACKET);
        arg2.leftPantsLegVisible = arg.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
        arg2.rightPantsLegVisible = arg.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
        arg2.leftSleeveVisible = arg.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
        arg2.rightSleeveVisible = arg.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
        arg2.capeVisible = arg.isPartVisible(PlayerModelPart.CAPE);
        PlayerEntityRenderer.updateGliding(arg, arg2, f);
        PlayerEntityRenderer.updateCape(arg, arg2, f);
        if (arg2.squaredDistanceToCamera < 100.0) {
            Scoreboard lv = arg.getScoreboard();
            ScoreboardObjective lv2 = lv.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            if (lv2 != null) {
                ReadableScoreboardScore lv3 = lv.getScore(arg, lv2);
                MutableText lv4 = ReadableScoreboardScore.getFormattedScore(lv3, lv2.getNumberFormatOr(StyledNumberFormat.EMPTY));
                arg2.playerName = Text.empty().append(lv4).append(ScreenTexts.SPACE).append(lv2.getDisplayName());
            } else {
                arg2.playerName = null;
            }
        } else {
            arg2.playerName = null;
        }
        arg2.leftShoulderParrotVariant = PlayerEntityRenderer.getShoulderParrotVariant(arg, true);
        arg2.rightShoulderParrotVariant = PlayerEntityRenderer.getShoulderParrotVariant(arg, false);
        arg2.id = arg.getId();
        arg2.name = arg.getGameProfile().getName();
        arg2.spyglassState.clear();
        if (arg2.isUsingItem && (lv5 = arg.getStackInHand(arg2.activeHand)).isOf(Items.SPYGLASS)) {
            this.itemModelResolver.updateForLivingEntity(arg2.spyglassState, lv5, ModelTransformationMode.HEAD, false, arg);
        }
    }

    private static void updateGliding(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta) {
        state.glidingTicks = (float)player.getGlidingTicks() + tickDelta;
        Vec3d lv = player.getRotationVec(tickDelta);
        Vec3d lv2 = player.lerpVelocity(tickDelta);
        double d = lv2.horizontalLengthSquared();
        double e = lv.horizontalLengthSquared();
        if (d > 0.0 && e > 0.0) {
            state.applyFlyingRotation = true;
            double g = Math.min(1.0, (lv2.x * lv.x + lv2.z * lv.z) / Math.sqrt(d * e));
            double h = lv2.x * lv.z - lv2.z * lv.x;
            state.flyingRotation = (float)(Math.signum(h) * Math.acos(g));
        } else {
            state.applyFlyingRotation = false;
            state.flyingRotation = 0.0f;
        }
    }

    private static void updateCape(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta) {
        double d = MathHelper.lerp((double)tickDelta, player.prevCapeX, player.capeX) - MathHelper.lerp((double)tickDelta, player.prevX, player.getX());
        double e = MathHelper.lerp((double)tickDelta, player.prevCapeY, player.capeY) - MathHelper.lerp((double)tickDelta, player.prevY, player.getY());
        double g = MathHelper.lerp((double)tickDelta, player.prevCapeZ, player.capeZ) - MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ());
        float h = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        double i = MathHelper.sin(h * ((float)Math.PI / 180));
        double j = -MathHelper.cos(h * ((float)Math.PI / 180));
        state.field_53536 = (float)e * 10.0f;
        state.field_53536 = MathHelper.clamp(state.field_53536, -6.0f, 32.0f);
        state.field_53537 = (float)(d * i + g * j) * 100.0f;
        state.field_53537 *= 1.0f - state.getGlidingProgress();
        state.field_53537 = MathHelper.clamp(state.field_53537, 0.0f, 150.0f);
        state.field_53538 = (float)(d * j - g * i) * 100.0f;
        state.field_53538 = MathHelper.clamp(state.field_53538, -20.0f, 20.0f);
        float k = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
        float l = MathHelper.lerp(tickDelta, player.lastDistanceMoved, player.distanceMoved);
        state.field_53536 += MathHelper.sin(l * 6.0f) * 32.0f * k;
    }

    @Nullable
    private static ParrotEntity.Variant getShoulderParrotVariant(AbstractClientPlayerEntity player, boolean left) {
        NbtCompound lv;
        NbtCompound nbtCompound = lv = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        if (EntityType.get(lv.getString("id")).filter(type -> type == EntityType.PARROT).isPresent()) {
            return ParrotEntity.Variant.byIndex(lv.getInt("Variant"));
        }
        return null;
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, boolean sleeveVisible) {
        this.renderArm(matrices, vertexConsumers, light, skinTexture, ((PlayerEntityModel)this.model).rightArm, sleeveVisible);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, boolean sleeveVisible) {
        this.renderArm(matrices, vertexConsumers, light, skinTexture, ((PlayerEntityModel)this.model).leftArm, sleeveVisible);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible) {
        PlayerEntityModel lv = (PlayerEntityModel)this.getModel();
        arm.resetTransform();
        arm.visible = true;
        lv.leftSleeve.visible = sleeveVisible;
        lv.rightSleeve.visible = sleeveVisible;
        lv.leftArm.roll = -0.1f;
        lv.rightArm.roll = 0.1f;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skinTexture)), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected void setupTransforms(PlayerEntityRenderState arg, MatrixStack arg2, float f, float g) {
        float h = arg.leaningPitch;
        float i = arg.pitch;
        if (arg.isGliding) {
            super.setupTransforms(arg, arg2, f, g);
            float j = arg.getGlidingProgress();
            if (!arg.usingRiptide) {
                arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0f - i)));
            }
            if (arg.applyFlyingRotation) {
                arg2.multiply(RotationAxis.POSITIVE_Y.rotation(arg.flyingRotation));
            }
        } else if (h > 0.0f) {
            super.setupTransforms(arg, arg2, f, g);
            float j = arg.touchingWater ? -90.0f - i : -90.0f;
            float k = MathHelper.lerp(h, 0.0f, j);
            arg2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (arg.isSwimming) {
                arg2.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupTransforms(arg, arg2, f, g);
        }
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PlayerEntityRenderState)state);
    }

    @Override
    protected /* synthetic */ boolean shouldRenderFeatures(LivingEntityRenderState state) {
        return this.shouldRenderFeatures((PlayerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    public /* synthetic */ Vec3d getPositionOffset(EntityRenderState state) {
        return this.getPositionOffset((PlayerEntityRenderState)state);
    }
}

