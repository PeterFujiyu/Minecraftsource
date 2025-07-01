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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderState
extends LivingEntityRenderState {
    private static final Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/wolf/wolf.png");
    public boolean angerTime;
    public boolean inSittingPose;
    public float tailAngle = 0.62831855f;
    public float begAnimationProgress;
    public float shakeProgress;
    public float furWetBrightnessMultiplier = 1.0f;
    public Identifier texture = DEFAULT_TEXTURE;
    @Nullable
    public DyeColor collarColor;
    public ItemStack bodyArmor = ItemStack.EMPTY;

    public float getRoll(float shakeOffset) {
        float g = (this.shakeProgress + shakeOffset) / 1.8f;
        if (g < 0.0f) {
            g = 0.0f;
        } else if (g > 1.0f) {
            g = 1.0f;
        }
        return MathHelper.sin(g * (float)Math.PI) * MathHelper.sin(g * (float)Math.PI * 11.0f) * 0.15f * (float)Math.PI;
    }
}

