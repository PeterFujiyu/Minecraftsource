/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

@Environment(value=EnvType.CLIENT)
public class WorldEventHandler {
    private final MinecraftClient client;
    private final World world;
    private final WorldRenderer worldRenderer;
    private final Map<BlockPos, SoundInstance> playingSongs = new HashMap<BlockPos, SoundInstance>();

    public WorldEventHandler(MinecraftClient client, World world, WorldRenderer worldRenderer) {
        this.client = client;
        this.world = world;
        this.worldRenderer = worldRenderer;
    }

    public void processGlobalEvent(int eventId, BlockPos pos, int data) {
        switch (eventId) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera lv = this.client.gameRenderer.getCamera();
                if (!lv.isReady()) break;
                Vec3d lv2 = Vec3d.ofCenter(pos).subtract(lv.getPos()).normalize();
                Vec3d lv3 = lv.getPos().add(lv2.multiply(2.0));
                if (eventId == WorldEvents.WITHER_SPAWNS) {
                    this.world.playSound(lv3.x, lv3.y, lv3.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (eventId == WorldEvents.END_PORTAL_OPENED) {
                    this.world.playSound(lv3.x, lv3.y, lv3.z, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.world.playSound(lv3.x, lv3.y, lv3.z, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void processWorldEvent(int eventId, BlockPos pos, int data) {
        Random lv = this.world.random;
        switch (eventId) {
            case 1035: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRAVEL, lv.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1049: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CRAFTER_CRAFT, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1050: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CRAFTER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1004: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1051: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.BLOCKS, 0.5f, 0.4f / (this.world.getRandom().nextFloat() * 0.4f + 0.8f), false);
                break;
            }
            case 2010: {
                this.shootParticles(data, pos, lv, ParticleTypes.WHITE_SMOKE);
                break;
            }
            case 2000: {
                this.shootParticles(data, pos, lv, ParticleTypes.SMOKE);
                break;
            }
            case 2003: {
                double d = (double)pos.getX() + 0.5;
                double e = pos.getY();
                double f = (double)pos.getZ() + 0.5;
                for (int k = 0; k < 8; ++k) {
                    this.worldRenderer.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), d, e, f, lv.nextGaussian() * 0.15, lv.nextDouble() * 0.2, lv.nextGaussian() * 0.15);
                }
                for (double g = 0.0; g < Math.PI * 2; g += 0.15707963267948966) {
                    this.worldRenderer.addParticle(ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -5.0, 0.0, Math.sin(g) * -5.0);
                    this.worldRenderer.addParticle(ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -7.0, 0.0, Math.sin(g) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                Vec3d lv2 = Vec3d.ofBottomCenter(pos);
                for (int l = 0; l < 8; ++l) {
                    this.worldRenderer.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), lv2.x, lv2.y, lv2.z, lv.nextGaussian() * 0.15, lv.nextDouble() * 0.2, lv.nextGaussian() * 0.15);
                }
                float h = (float)(data >> 16 & 0xFF) / 255.0f;
                float m = (float)(data >> 8 & 0xFF) / 255.0f;
                float n = (float)(data >> 0 & 0xFF) / 255.0f;
                SimpleParticleType lv3 = eventId == WorldEvents.INSTANT_SPLASH_POTION_SPLASHED ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int o = 0; o < 100; ++o) {
                    double g = lv.nextDouble() * 4.0;
                    double p = lv.nextDouble() * Math.PI * 2.0;
                    double q = Math.cos(p) * g;
                    double r = 0.01 + lv.nextDouble() * 0.5;
                    double s = Math.sin(p) * g;
                    Particle lv4 = this.worldRenderer.spawnParticle(lv3, lv3.getType().shouldAlwaysSpawn(), lv2.x + q * 0.1, lv2.y + 0.3, lv2.z + s * 0.1, q, r, s);
                    if (lv4 == null) continue;
                    float t = 0.75f + lv.nextFloat() * 0.25f;
                    lv4.setColor(h * t, m * t, n * t);
                    lv4.move((float)g);
                }
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, lv.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState lv5 = Block.getStateFromRawId(data);
                if (!lv5.isAir()) {
                    BlockSoundGroup lv6 = lv5.getSoundGroup();
                    this.world.playSoundAtBlockCenter(pos, lv6.getBreakSound(), SoundCategory.BLOCKS, (lv6.getVolume() + 1.0f) / 2.0f, lv6.getPitch() * 0.8f, false);
                }
                this.world.addBlockBreakParticles(pos, lv5);
                break;
            }
            case 3008: {
                BlockState lv7 = Block.getStateFromRawId(data);
                Block n = lv7.getBlock();
                if (n instanceof BrushableBlock) {
                    BrushableBlock lv8 = (BrushableBlock)n;
                    this.world.playSoundAtBlockCenter(pos, lv8.getBrushingCompleteSound(), SoundCategory.PLAYERS, 1.0f, 1.0f, false);
                }
                this.world.addBlockBreakParticles(pos, lv7);
                break;
            }
            case 2004: {
                for (int u = 0; u < 20; ++u) {
                    double v = (double)pos.getX() + 0.5 + (lv.nextDouble() - 0.5) * 2.0;
                    double w = (double)pos.getY() + 0.5 + (lv.nextDouble() - 0.5) * 2.0;
                    double x = (double)pos.getZ() + 0.5 + (lv.nextDouble() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.SMOKE, v, w, x, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.FLAME, v, w, x, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 3011: {
                TrialSpawnerLogic.addMobSpawnParticles(this.world, pos, lv, TrialSpawnerLogic.Type.fromIndex((int)data).particle);
                break;
            }
            case 3012: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawnerLogic.addMobSpawnParticles(this.world, pos, lv, TrialSpawnerLogic.Type.fromIndex((int)data).particle);
                break;
            }
            case 3021: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawnerLogic.addMobSpawnParticles(this.world, pos, lv, TrialSpawnerLogic.Type.fromIndex((int)data).particle);
                break;
            }
            case 3013: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawnerLogic.addDetectionParticles(this.world, pos, lv, data, ParticleTypes.TRIAL_SPAWNER_DETECTION);
                break;
            }
            case 3019: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawnerLogic.addDetectionParticles(this.world, pos, lv, data, ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS);
                break;
            }
            case 3020: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundCategory.BLOCKS, data == 0 ? 0.3f : 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawnerLogic.addDetectionParticles(this.world, pos, lv, 0, ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS);
                TrialSpawnerLogic.addTrialOmenParticles(this.world, pos, lv);
                break;
            }
            case 3014: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawnerLogic.addEjectItemParticles(this.world, pos, lv);
                break;
            }
            case 3017: {
                TrialSpawnerLogic.addEjectItemParticles(this.world, pos, lv);
                break;
            }
            case 3015: {
                BlockEntity v = this.world.getBlockEntity(pos);
                if (!(v instanceof VaultBlockEntity)) break;
                VaultBlockEntity lv9 = (VaultBlockEntity)v;
                VaultBlockEntity.Client.spawnActivateParticles(this.world, lv9.getPos(), lv9.getCachedState(), lv9.getSharedData(), data == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_VAULT_ACTIVATE, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 3016: {
                VaultBlockEntity.Client.spawnDeactivateParticles(this.world, pos, data == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_VAULT_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 3018: {
                for (int u = 0; u < 10; ++u) {
                    double v = lv.nextGaussian() * 0.02;
                    double w = lv.nextGaussian() * 0.02;
                    double x = lv.nextGaussian() * 0.02;
                    this.world.addParticle(ParticleTypes.POOF, (double)pos.getX() + lv.nextDouble(), (double)pos.getY() + lv.nextDouble(), (double)pos.getZ() + lv.nextDouble(), v, w, x);
                }
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_COBWEB_PLACE, SoundCategory.BLOCKS, 1.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 1505: {
                BoneMealItem.createParticles(this.world, pos, data);
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 2011: {
                ParticleUtil.spawnParticlesAround(this.world, pos, data, ParticleTypes.HAPPY_VILLAGER);
                break;
            }
            case 2012: {
                ParticleUtil.spawnParticlesAround(this.world, pos, data, ParticleTypes.HAPPY_VILLAGER);
                break;
            }
            case 3009: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.EGG_CRACK, UniformIntProvider.create(3, 6));
                break;
            }
            case 3002: {
                if (data >= 0 && data < Direction.Axis.VALUES.length) {
                    ParticleUtil.spawnParticle(Direction.Axis.VALUES[data], this.world, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(10, 19));
                    break;
                }
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(3, 5));
                break;
            }
            case 2013: {
                ParticleUtil.spawnSmashAttackParticles(this.world, pos, data);
                break;
            }
            case 3006: {
                int u = data >> 6;
                if (u > 0) {
                    if (lv.nextFloat() < 0.3f + (float)u * 0.1f) {
                        float n = 0.15f + 0.02f * (float)u * (float)u * lv.nextFloat();
                        float y = 0.4f + 0.3f * (float)u * lv.nextFloat();
                        this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, n, y, false);
                    }
                    byte b = (byte)(data & 0x3F);
                    UniformIntProvider lv10 = UniformIntProvider.create(0, u);
                    float z = 0.005f;
                    Supplier<Vec3d> supplier = () -> new Vec3d(MathHelper.nextDouble(lv, -0.005f, 0.005f), MathHelper.nextDouble(lv, -0.005f, 0.005f), MathHelper.nextDouble(lv, -0.005f, 0.005f));
                    if (b == 0) {
                        for (Direction lv11 : Direction.values()) {
                            float aa = lv11 == Direction.DOWN ? (float)Math.PI : 0.0f;
                            double r = lv11.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(aa), lv10, lv11, supplier, r);
                        }
                    } else {
                        for (Direction lv12 : MultifaceBlock.flagToDirections(b)) {
                            float ab = lv12 == Direction.UP ? (float)Math.PI : 0.0f;
                            double q = 0.35;
                            ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(ab), lv10, lv12, supplier, 0.35);
                        }
                    }
                } else {
                    this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                    boolean bl = this.world.getBlockState(pos).isFullCube(this.world, pos);
                    int ac = bl ? 40 : 20;
                    float z = bl ? 0.45f : 0.25f;
                    float ad = 0.07f;
                    for (int ae = 0; ae < ac; ++ae) {
                        float af = 2.0f * lv.nextFloat() - 1.0f;
                        float ab = 2.0f * lv.nextFloat() - 1.0f;
                        float ag = 2.0f * lv.nextFloat() - 1.0f;
                        this.world.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)pos.getX() + 0.5 + (double)(af * z), (double)pos.getY() + 0.5 + (double)(ab * z), (double)pos.getZ() + 0.5 + (double)(ag * z), af * 0.07f, ab * 0.07f, ag * 0.07f);
                    }
                }
                break;
            }
            case 3007: {
                boolean bl2;
                for (int ah = 0; ah < 10; ++ah) {
                    this.world.addParticle(new ShriekParticleEffect(ah * 5), (double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                }
                BlockState lv13 = this.world.getBlockState(pos);
                boolean bl = bl2 = lv13.contains(Properties.WATERLOGGED) && lv13.get(Properties.WATERLOGGED) != false;
                if (bl2) break;
                this.world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 2.0f, 0.6f + this.world.random.nextFloat() * 0.4f, false);
                break;
            }
            case 3003: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_ON, UniformIntProvider.create(3, 5));
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3004: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_OFF, UniformIntProvider.create(3, 5));
                break;
            }
            case 3005: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.SCRAPE, UniformIntProvider.create(3, 5));
                break;
            }
            case 2008: {
                this.world.addParticle(ParticleTypes.EXPLOSION, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.playEffects(this.world, pos, data > 0);
                break;
            }
            case 1504: {
                PointedDripstoneBlock.createParticle(this.world, pos, this.world.getBlockState(pos));
                break;
            }
            case 1501: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (lv.nextFloat() - lv.nextFloat()) * 0.8f, false);
                for (int o = 0; o < 8; ++o) {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)pos.getX() + lv.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + lv.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5f, 2.6f + (lv.nextFloat() - lv.nextFloat()) * 0.8f, false);
                for (int o = 0; o < 5; ++o) {
                    double g = (double)pos.getX() + lv.nextDouble() * 0.6 + 0.2;
                    double p = (double)pos.getY() + lv.nextDouble() * 0.6 + 0.2;
                    double q = (double)pos.getZ() + lv.nextDouble() * 0.6 + 0.2;
                    this.world.addParticle(ParticleTypes.SMOKE, g, p, q, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                for (int o = 0; o < 16; ++o) {
                    double g = (double)pos.getX() + (5.0 + lv.nextDouble() * 6.0) / 16.0;
                    double p = (double)pos.getY() + 0.8125;
                    double q = (double)pos.getZ() + (5.0 + lv.nextDouble() * 6.0) / 16.0;
                    this.world.addParticle(ParticleTypes.SMOKE, g, p, q, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int o = 0; o < 200; ++o) {
                    float ad = lv.nextFloat() * 4.0f;
                    float ai = lv.nextFloat() * ((float)Math.PI * 2);
                    double p = MathHelper.cos(ai) * ad;
                    double q = 0.01 + lv.nextDouble() * 0.5;
                    double r = MathHelper.sin(ai) * ad;
                    Particle lv14 = this.worldRenderer.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)pos.getX() + p * 0.1, (double)pos.getY() + 0.3, (double)pos.getZ() + r * 0.1, p, q, r);
                    if (lv14 == null) continue;
                    lv14.move(ad);
                }
                if (data != 1) break;
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0f, lv.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int o = 0; o < 8; ++o) {
                    this.world.addParticle(ParticleTypes.CLOUD, (double)pos.getX() + lv.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + lv.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1009: {
                if (data == 0) {
                    this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (lv.nextFloat() - lv.nextFloat()) * 0.8f, false);
                    break;
                }
                if (data != 1) break;
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.7f, 1.6f + (lv.nextFloat() - lv.nextFloat()) * 0.4f, false);
                break;
            }
            case 1029: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, lv.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, lv.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                this.world.getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG).getEntry(data).ifPresent(song -> this.playJukeboxSong((RegistryEntry<JukeboxSong>)song, pos));
                break;
            }
            case 1011: {
                this.stopJukeboxSongAndUpdate(pos);
                break;
            }
            case 1015: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, true, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.world.random.nextFloat() * 0.3f, false);
                break;
            }
            case 1045: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND, SoundCategory.BLOCKS, 2.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1046: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1047: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1048: {
                this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_SKELETON_CONVERTED_TO_STRAY, SoundCategory.HOSTILE, 2.0f, (lv.nextFloat() - lv.nextFloat()) * 0.2f + 1.0f, false);
            }
        }
    }

    private void shootParticles(int direction, BlockPos pos, Random random, SimpleParticleType particleType) {
        Direction lv = Direction.byId(direction);
        int j = lv.getOffsetX();
        int k = lv.getOffsetY();
        int l = lv.getOffsetZ();
        for (int m = 0; m < 10; ++m) {
            double d = random.nextDouble() * 0.2 + 0.01;
            double e = (double)pos.getX() + (double)j * 0.6 + 0.5 + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)l * 0.5;
            double f = (double)pos.getY() + (double)k * 0.6 + 0.5 + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
            double g = (double)pos.getZ() + (double)l * 0.6 + 0.5 + (double)l * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
            double h = (double)j * d + random.nextGaussian() * 0.01;
            double n = (double)k * d + random.nextGaussian() * 0.01;
            double o = (double)l * d + random.nextGaussian() * 0.01;
            this.worldRenderer.addParticle(particleType, e, f, g, h, n, o);
        }
    }

    private void playJukeboxSong(RegistryEntry<JukeboxSong> song, BlockPos jukeboxPos) {
        this.stopJukeboxSong(jukeboxPos);
        JukeboxSong lv = song.value();
        SoundEvent lv2 = lv.soundEvent().value();
        PositionedSoundInstance lv3 = PositionedSoundInstance.record(lv2, Vec3d.ofCenter(jukeboxPos));
        this.playingSongs.put(jukeboxPos, lv3);
        this.client.getSoundManager().play(lv3);
        this.client.inGameHud.setRecordPlayingOverlay(lv.description());
        this.updateEntitiesForSong(this.world, jukeboxPos, true);
    }

    private void stopJukeboxSong(BlockPos jukeboxPos) {
        SoundInstance lv = this.playingSongs.remove(jukeboxPos);
        if (lv != null) {
            this.client.getSoundManager().stop(lv);
        }
    }

    private void stopJukeboxSongAndUpdate(BlockPos jukeboxPos) {
        this.stopJukeboxSong(jukeboxPos);
        this.updateEntitiesForSong(this.world, jukeboxPos, false);
    }

    private void updateEntitiesForSong(World world, BlockPos pos, boolean playing) {
        List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, new Box(pos).expand(3.0));
        for (LivingEntity lv : list) {
            lv.setNearbySongPlaying(pos, playing);
        }
    }
}

