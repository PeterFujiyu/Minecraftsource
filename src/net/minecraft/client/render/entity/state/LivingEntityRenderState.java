/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EntityPose;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LivingEntityRenderState
extends EntityRenderState {
    public float bodyYaw;
    public float yawDegrees;
    public float pitch;
    public float deathTime;
    public float limbFrequency;
    public float limbAmplitudeMultiplier;
    public float baseScale = 1.0f;
    public float ageScale = 1.0f;
    public boolean flipUpsideDown;
    public boolean shaking;
    public boolean baby;
    public boolean touchingWater;
    public boolean usingRiptide;
    public boolean hurt;
    public boolean invisibleToPlayer;
    public boolean hasOutline;
    @Nullable
    public Direction sleepingDirection;
    @Nullable
    public Text customName;
    public EntityPose pose = EntityPose.STANDING;
    public final ItemRenderState headItemRenderState = new ItemRenderState();
    public float headItemAnimationProgress;
    @Nullable
    public SkullBlock.SkullType wearingSkullType;
    @Nullable
    public ProfileComponent wearingSkullProfile;

    public boolean isInPose(EntityPose pose) {
        return this.pose == pose;
    }
}

