/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static final int WATER_FOG_LENGTH = 96;
    private static final List<StatusEffectFogModifier> FOG_MODIFIERS = Lists.newArrayList(new BlindnessFogModifier(), new DarknessFogModifier());
    public static final float WATER_FOG_CHANGE_DURATION = 5000.0f;
    private static int waterFogColor = -1;
    private static int nextWaterFogColor = -1;
    private static long lastWaterFogColorUpdateTime = -1L;
    private static boolean fogEnabled = true;

    public static Vector4f getFogColor(Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance, float skyDarkness) {
        LivingEntity lv8;
        float w;
        float v;
        float u;
        CameraSubmersionType lv = camera.getSubmersionType();
        Entity lv2 = camera.getFocusedEntity();
        if (lv == CameraSubmersionType.WATER) {
            long l = Util.getMeasuringTimeMs();
            int j2 = world.getBiome(BlockPos.ofFloored(camera.getPos())).value().getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = j2;
                nextWaterFogColor = j2;
                lastWaterFogColorUpdateTime = l;
            }
            int k2 = waterFogColor >> 16 & 0xFF;
            int m = waterFogColor >> 8 & 0xFF;
            int n = waterFogColor & 0xFF;
            int o = nextWaterFogColor >> 16 & 0xFF;
            int p = nextWaterFogColor >> 8 & 0xFF;
            int q = nextWaterFogColor & 0xFF;
            float h = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float r = MathHelper.lerp(h, (float)o, (float)k2);
            float s = MathHelper.lerp(h, (float)p, (float)m);
            float t = MathHelper.lerp(h, (float)q, (float)n);
            u = r / 255.0f;
            v = s / 255.0f;
            w = t / 255.0f;
            if (waterFogColor != j2) {
                waterFogColor = j2;
                nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(s) << 8 | MathHelper.floor(t);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (lv == CameraSubmersionType.LAVA) {
            u = 0.6f;
            v = 0.1f;
            w = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else if (lv == CameraSubmersionType.POWDER_SNOW) {
            u = 0.623f;
            v = 0.734f;
            w = 0.785f;
            lastWaterFogColorUpdateTime = -1L;
        } else {
            float r;
            float s;
            float h;
            float x = 0.25f + 0.75f * (float)clampedViewDistance / 32.0f;
            x = 1.0f - (float)Math.pow(x, 0.25);
            int y = world.getSkyColor(camera.getPos(), tickDelta);
            float z = ColorHelper.getRedFloat(y);
            float aa = ColorHelper.getGreenFloat(y);
            float ab = ColorHelper.getBlueFloat(y);
            float ac = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeAccess lv3 = world.getBiomeAccess();
            Vec3d lv4 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d lv5 = CubicSampler.sampleColor(lv4, (i, j, k) -> world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(lv3.getBiomeForNoiseGen(i, j, k).value().getFogColor()), ac));
            u = (float)lv5.getX();
            v = (float)lv5.getY();
            w = (float)lv5.getZ();
            if (clampedViewDistance >= 4) {
                h = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0f ? -1.0f : 1.0f;
                Vector3f vector3f = new Vector3f(h, 0.0f, 0.0f);
                s = camera.getHorizontalPlane().dot(vector3f);
                if (s < 0.0f) {
                    s = 0.0f;
                }
                if (s > 0.0f && world.getDimensionEffects().isSunRisingOrSetting(world.getSkyAngle(tickDelta))) {
                    int ad = world.getDimensionEffects().getSkyColor(world.getSkyAngle(tickDelta));
                    u = u * (1.0f - (s *= ColorHelper.getAlphaFloat(ad))) + ColorHelper.getRedFloat(ad) * s;
                    v = v * (1.0f - s) + ColorHelper.getGreenFloat(ad) * s;
                    w = w * (1.0f - s) + ColorHelper.getBlueFloat(ad) * s;
                }
            }
            u += (z - u) * x;
            v += (aa - v) * x;
            w += (ab - w) * x;
            h = world.getRainGradient(tickDelta);
            if (h > 0.0f) {
                float r2 = 1.0f - h * 0.5f;
                s = 1.0f - h * 0.4f;
                u *= r2;
                v *= r2;
                w *= s;
            }
            if ((r = world.getThunderGradient(tickDelta)) > 0.0f) {
                s = 1.0f - r * 0.5f;
                u *= s;
                v *= s;
                w *= s;
            }
            lastWaterFogColorUpdateTime = -1L;
        }
        float x = ((float)camera.getPos().y - (float)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
        StatusEffectFogModifier lv6 = BackgroundRenderer.getFogModifier(lv2, tickDelta);
        if (lv6 != null) {
            LivingEntity lv7 = (LivingEntity)lv2;
            x = lv6.applyColorModifier(lv7, lv7.getStatusEffect(lv6.getStatusEffect()), x, tickDelta);
        }
        if (x < 1.0f && lv != CameraSubmersionType.LAVA && lv != CameraSubmersionType.POWDER_SNOW) {
            if (x < 0.0f) {
                x = 0.0f;
            }
            x *= x;
            u *= x;
            v *= x;
            w *= x;
        }
        if (skyDarkness > 0.0f) {
            u = u * (1.0f - skyDarkness) + u * 0.7f * skyDarkness;
            v = v * (1.0f - skyDarkness) + v * 0.6f * skyDarkness;
            w = w * (1.0f - skyDarkness) + w * 0.6f * skyDarkness;
        }
        float z = lv == CameraSubmersionType.WATER ? (lv2 instanceof ClientPlayerEntity ? ((ClientPlayerEntity)lv2).getUnderwaterVisibility() : 1.0f) : (lv2 instanceof LivingEntity && (lv8 = (LivingEntity)lv2).hasStatusEffect(StatusEffects.NIGHT_VISION) && !lv8.hasStatusEffect(StatusEffects.DARKNESS) ? GameRenderer.getNightVisionStrength(lv8, tickDelta) : 0.0f);
        if (u != 0.0f && v != 0.0f && w != 0.0f) {
            float aa = Math.min(1.0f / u, Math.min(1.0f / v, 1.0f / w));
            u = u * (1.0f - z) + u * aa * z;
            v = v * (1.0f - z) + v * aa * z;
            w = w * (1.0f - z) + w * aa * z;
        }
        return new Vector4f(u, v, w, 1.0f);
    }

    public static boolean toggleFog() {
        fogEnabled = !fogEnabled;
        return fogEnabled;
    }

    @Nullable
    private static StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
        if (entity instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)entity;
            return FOG_MODIFIERS.stream().filter(modifier -> modifier.shouldApply(lv, tickDelta)).findFirst().orElse(null);
        }
        return null;
    }

    public static Fog applyFog(Camera camera, FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta) {
        if (!fogEnabled) {
            return Fog.DUMMY;
        }
        CameraSubmersionType lv = camera.getSubmersionType();
        Entity lv2 = camera.getFocusedEntity();
        FogData lv3 = new FogData(fogType);
        StatusEffectFogModifier lv4 = BackgroundRenderer.getFogModifier(lv2, tickDelta);
        if (lv == CameraSubmersionType.LAVA) {
            if (lv2.isSpectator()) {
                lv3.fogStart = -8.0f;
                lv3.fogEnd = viewDistance * 0.5f;
            } else if (lv2 instanceof LivingEntity && ((LivingEntity)lv2).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                lv3.fogStart = 0.0f;
                lv3.fogEnd = 5.0f;
            } else {
                lv3.fogStart = 0.25f;
                lv3.fogEnd = 1.0f;
            }
        } else if (lv == CameraSubmersionType.POWDER_SNOW) {
            if (lv2.isSpectator()) {
                lv3.fogStart = -8.0f;
                lv3.fogEnd = viewDistance * 0.5f;
            } else {
                lv3.fogStart = 0.0f;
                lv3.fogEnd = 2.0f;
            }
        } else if (lv4 != null) {
            LivingEntity lv5 = (LivingEntity)lv2;
            StatusEffectInstance lv6 = lv5.getStatusEffect(lv4.getStatusEffect());
            if (lv6 != null) {
                lv4.applyStartEndModifier(lv3, lv5, lv6, viewDistance, tickDelta);
            }
        } else if (lv == CameraSubmersionType.WATER) {
            lv3.fogStart = -8.0f;
            lv3.fogEnd = 96.0f;
            if (lv2 instanceof ClientPlayerEntity) {
                ClientPlayerEntity lv7 = (ClientPlayerEntity)lv2;
                lv3.fogEnd *= Math.max(0.25f, lv7.getUnderwaterVisibility());
                RegistryEntry<Biome> lv8 = lv7.getWorld().getBiome(lv7.getBlockPos());
                if (lv8.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    lv3.fogEnd *= 0.85f;
                }
            }
            if (lv3.fogEnd > viewDistance) {
                lv3.fogEnd = viewDistance;
                lv3.fogShape = FogShape.CYLINDER;
            }
        } else if (thickenFog) {
            lv3.fogStart = viewDistance * 0.05f;
            lv3.fogEnd = Math.min(viewDistance, 192.0f) * 0.5f;
        } else if (fogType == FogType.FOG_SKY) {
            lv3.fogStart = 0.0f;
            lv3.fogEnd = viewDistance;
            lv3.fogShape = FogShape.CYLINDER;
        } else if (fogType == FogType.FOG_TERRAIN) {
            float h = MathHelper.clamp(viewDistance / 10.0f, 4.0f, 64.0f);
            lv3.fogStart = viewDistance - h;
            lv3.fogEnd = viewDistance;
            lv3.fogShape = FogShape.CYLINDER;
        }
        return new Fog(lv3.fogStart, lv3.fogEnd, lv3.fogShape, color.x, color.y, color.z, color.w);
    }

    @Environment(value=EnvType.CLIENT)
    static interface StatusEffectFogModifier {
        public RegistryEntry<StatusEffect> getStatusEffect();

        public void applyStartEndModifier(FogData var1, LivingEntity var2, StatusEffectInstance var3, float var4, float var5);

        default public boolean shouldApply(LivingEntity entity, float tickDelta) {
            return entity.hasStatusEffect(this.getStatusEffect());
        }

        default public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float defaultModifier, float tickDelta) {
            StatusEffectInstance lv = entity.getStatusEffect(this.getStatusEffect());
            if (lv != null) {
                defaultModifier = lv.isDurationBelow(19) ? 1.0f - (float)lv.getDuration() / 20.0f : 0.0f;
            }
            return defaultModifier;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FogData {
        public final FogType fogType;
        public float fogStart;
        public float fogEnd;
        public FogShape fogShape = FogShape.SPHERE;

        public FogData(FogType fogType) {
            this.fogType = fogType;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

    }

    @Environment(value=EnvType.CLIENT)
    static class BlindnessFogModifier
    implements StatusEffectFogModifier {
        BlindnessFogModifier() {
        }

        @Override
        public RegistryEntry<StatusEffect> getStatusEffect() {
            return StatusEffects.BLINDNESS;
        }

        @Override
        public void applyStartEndModifier(FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
            float h;
            float f = h = effect.isInfinite() ? 5.0f : MathHelper.lerp(Math.min(1.0f, (float)effect.getDuration() / 20.0f), viewDistance, 5.0f);
            if (fogData.fogType == FogType.FOG_SKY) {
                fogData.fogStart = 0.0f;
                fogData.fogEnd = h * 0.8f;
            } else if (fogData.fogType == FogType.FOG_TERRAIN) {
                fogData.fogStart = h * 0.25f;
                fogData.fogEnd = h;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DarknessFogModifier
    implements StatusEffectFogModifier {
        DarknessFogModifier() {
        }

        @Override
        public RegistryEntry<StatusEffect> getStatusEffect() {
            return StatusEffects.DARKNESS;
        }

        @Override
        public void applyStartEndModifier(FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
            float h = MathHelper.lerp(effect.getFadeFactor(entity, tickDelta), viewDistance, 15.0f);
            fogData.fogStart = switch (fogData.fogType.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> 0.0f;
                case 1 -> h * 0.75f;
            };
            fogData.fogEnd = h;
        }

        @Override
        public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float defaultModifier, float tickDelta) {
            return 1.0f - effect.getFadeFactor(entity, tickDelta);
        }
    }
}

