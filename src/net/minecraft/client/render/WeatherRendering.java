/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Environment(value=EnvType.CLIENT)
public class WeatherRendering {
    private static final int field_53148 = 10;
    private static final int field_53149 = 21;
    private static final Identifier RAIN_TEXTURE = Identifier.ofVanilla("textures/environment/rain.png");
    private static final Identifier SNOW_TEXTURE = Identifier.ofVanilla("textures/environment/snow.png");
    private static final int field_53152 = 32;
    private static final int field_53153 = 16;
    private int soundChance;
    private final float[] NORMAL_LINE_DX = new float[1024];
    private final float[] NORMAL_LINE_DZ = new float[1024];

    public WeatherRendering() {
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float g = i - 16;
                float h = MathHelper.hypot(f, g);
                this.NORMAL_LINE_DX[i * 32 + j] = -g / h;
                this.NORMAL_LINE_DZ[i * 32 + j] = f / h;
            }
        }
    }

    public void renderPrecipitation(World world, VertexConsumerProvider vertexConsumers, int ticks, float delta, Vec3d pos) {
        float g = world.getRainGradient(delta);
        if (g <= 0.0f) {
            return;
        }
        int j = MinecraftClient.isFancyGraphicsOrBetter() ? 10 : 5;
        ArrayList<Piece> list = new ArrayList<Piece>();
        ArrayList<Piece> list2 = new ArrayList<Piece>();
        this.buildPrecipitationPieces(world, ticks, delta, pos, j, list, list2);
        if (!list.isEmpty() || !list2.isEmpty()) {
            this.renderPrecipitation(vertexConsumers, pos, j, g, list, list2);
        }
    }

    private void buildPrecipitationPieces(World world, int ticks, float delta, Vec3d pos, int range, List<Piece> rainOut, List<Piece> snowOut) {
        int k = MathHelper.floor(pos.x);
        int l = MathHelper.floor(pos.y);
        int m = MathHelper.floor(pos.z);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        Random lv2 = Random.create();
        for (int n = m - range; n <= m + range; ++n) {
            for (int o = k - range; o <= k + range; ++o) {
                Biome.Precipitation lv3;
                int p = world.getTopY(Heightmap.Type.MOTION_BLOCKING, o, n);
                int q = Math.max(l - range, p);
                int r = Math.max(l + range, p);
                if (r - q == 0 || (lv3 = this.getPrecipitationAt(world, lv.set(o, l, n))) == Biome.Precipitation.NONE) continue;
                int s = o * o * 3121 + o * 45238971 ^ n * n * 418711 + n * 13761;
                lv2.setSeed(s);
                int t = Math.max(l, p);
                int u = WorldRenderer.getLightmapCoordinates(world, lv.set(o, t, n));
                if (lv3 == Biome.Precipitation.RAIN) {
                    rainOut.add(this.createRainPiece(lv2, ticks, o, q, r, n, u, delta));
                    continue;
                }
                if (lv3 != Biome.Precipitation.SNOW) continue;
                snowOut.add(this.createSnowPiece(lv2, ticks, o, q, r, n, u, delta));
            }
        }
    }

    private void renderPrecipitation(VertexConsumerProvider vertexConsumers, Vec3d pos, int range, float gradient, List<Piece> rainPieces, List<Piece> snowPieces) {
        RenderLayer lv;
        if (!rainPieces.isEmpty()) {
            lv = RenderLayer.getWeather(RAIN_TEXTURE, MinecraftClient.isFabulousGraphicsOrBetter());
            this.renderPieces(vertexConsumers.getBuffer(lv), rainPieces, pos, 1.0f, range, gradient);
        }
        if (!snowPieces.isEmpty()) {
            lv = RenderLayer.getWeather(SNOW_TEXTURE, MinecraftClient.isFabulousGraphicsOrBetter());
            this.renderPieces(vertexConsumers.getBuffer(lv), snowPieces, pos, 0.8f, range, gradient);
        }
    }

    private Piece createRainPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float tickDelta) {
        int o = ticks & 0x1FFFF;
        int p = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
        float g = 3.0f + random.nextFloat();
        float h = -((float)(o + p) + tickDelta) / 32.0f * g;
        float q = h % 32.0f;
        return new Piece(x, z, yMin, yMax, 0.0f, q, light);
    }

    private Piece createSnowPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float tickDelta) {
        float g = (float)ticks + tickDelta;
        float h = (float)(random.nextDouble() + (double)(g * 0.01f * (float)random.nextGaussian()));
        float o = (float)(random.nextDouble() + (double)(g * (float)random.nextGaussian() * 0.001f));
        float p = -((float)(ticks & 0x1FF) + tickDelta) / 512.0f;
        int q = LightmapTextureManager.pack((LightmapTextureManager.getBlockLightCoordinates(light) * 3 + 15) / 4, (LightmapTextureManager.getSkyLightCoordinates(light) * 3 + 15) / 4);
        return new Piece(x, z, yMin, yMax, h, p + o, q);
    }

    private void renderPieces(VertexConsumer vertexConsumer, List<Piece> pieces, Vec3d pos, float intensity, int range, float gradient) {
        for (Piece lv : pieces) {
            float h = (float)((double)lv.x + 0.5 - pos.x);
            float j = (float)((double)lv.z + 0.5 - pos.z);
            float k = (float)MathHelper.squaredHypot(h, j);
            float l = MathHelper.lerp(k / (float)(range * range), intensity, 0.5f) * gradient;
            int m = ColorHelper.getWhite(l);
            int n = (lv.z - MathHelper.floor(pos.z) + 16) * 32 + lv.x - MathHelper.floor(pos.x) + 16;
            float o = this.NORMAL_LINE_DX[n] / 2.0f;
            float p = this.NORMAL_LINE_DZ[n] / 2.0f;
            float q = h - o;
            float r = h + o;
            float s = (float)((double)lv.topY - pos.y);
            float t = (float)((double)lv.bottomY - pos.y);
            float u = j - p;
            float v = j + p;
            float w = lv.uOffset + 0.0f;
            float x = lv.uOffset + 1.0f;
            float y = (float)lv.bottomY * 0.25f + lv.vOffset;
            float z = (float)lv.topY * 0.25f + lv.vOffset;
            vertexConsumer.vertex(q, s, u).texture(w, y).color(m).light(lv.lightCoords);
            vertexConsumer.vertex(r, s, v).texture(x, y).color(m).light(lv.lightCoords);
            vertexConsumer.vertex(r, t, v).texture(x, z).color(m).light(lv.lightCoords);
            vertexConsumer.vertex(q, t, u).texture(w, z).color(m).light(lv.lightCoords);
        }
    }

    public void addParticlesAndSound(ClientWorld world, Camera camera, int ticks, ParticlesMode particlesMode) {
        float f = world.getRainGradient(1.0f) / (MinecraftClient.isFancyGraphicsOrBetter() ? 1.0f : 2.0f);
        if (f <= 0.0f) {
            return;
        }
        Random lv = Random.create((long)ticks * 312987231L);
        BlockPos lv2 = BlockPos.ofFloored(camera.getPos());
        Vec3i lv3 = null;
        int j = (int)(100.0f * f * f) / (particlesMode == ParticlesMode.DECREASED ? 2 : 1);
        for (int k = 0; k < j; ++k) {
            int m;
            int l = lv.nextInt(21) - 10;
            BlockPos lv4 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, lv2.add(l, 0, m = lv.nextInt(21) - 10));
            if (lv4.getY() <= world.getBottomY() || lv4.getY() > lv2.getY() + 10 || lv4.getY() < lv2.getY() - 10 || this.getPrecipitationAt(world, lv4) != Biome.Precipitation.RAIN) continue;
            lv3 = lv4.down();
            if (particlesMode == ParticlesMode.MINIMAL) break;
            double d = lv.nextDouble();
            double e = lv.nextDouble();
            BlockState lv5 = world.getBlockState((BlockPos)lv3);
            FluidState lv6 = world.getFluidState((BlockPos)lv3);
            VoxelShape lv7 = lv5.getCollisionShape(world, (BlockPos)lv3);
            double g = lv7.getEndingCoord(Direction.Axis.Y, d, e);
            double h = lv6.getHeight(world, (BlockPos)lv3);
            double n = Math.max(g, h);
            SimpleParticleType lv8 = lv6.isIn(FluidTags.LAVA) || lv5.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(lv5) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            world.addParticle(lv8, (double)lv3.getX() + d, (double)lv3.getY() + n, (double)lv3.getZ() + e, 0.0, 0.0, 0.0);
        }
        if (lv3 != null && lv.nextInt(3) < this.soundChance++) {
            this.soundChance = 0;
            if (lv3.getY() > lv2.getY() + 1 && world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, lv2).getY() > MathHelper.floor(lv2.getY())) {
                world.playSoundAtBlockCenter((BlockPos)lv3, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                world.playSoundAtBlockCenter((BlockPos)lv3, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    private Biome.Precipitation getPrecipitationAt(World world, BlockPos pos) {
        if (!world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
            return Biome.Precipitation.NONE;
        }
        Biome lv = world.getBiome(pos).value();
        return lv.getPrecipitation(pos, world.getSeaLevel());
    }

    @Environment(value=EnvType.CLIENT)
    record Piece(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
    }
}

