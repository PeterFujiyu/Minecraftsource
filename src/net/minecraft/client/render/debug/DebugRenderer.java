/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.render.debug.BreezeDebugRenderer;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.client.render.debug.ChunkDebugRenderer;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.CollisionDebugRenderer;
import net.minecraft.client.render.debug.GameEventDebugRenderer;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.LightDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.OctreeDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.RedstoneUpdateOrderDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.SupportingBlockDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.render.debug.WaterDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DebugRenderer {
    public final PathfindingDebugRenderer pathfindingDebugRenderer = new PathfindingDebugRenderer();
    public final Renderer waterDebugRenderer;
    public final Renderer chunkBorderDebugRenderer;
    public final Renderer heightmapDebugRenderer;
    public final Renderer collisionDebugRenderer;
    public final Renderer supportingBlockDebugRenderer;
    public final NeighborUpdateDebugRenderer neighborUpdateDebugRenderer;
    public final RedstoneUpdateOrderDebugRenderer redstoneUpdateOrderDebugRenderer;
    public final StructureDebugRenderer structureDebugRenderer;
    public final Renderer skyLightDebugRenderer;
    public final Renderer worldGenAttemptDebugRenderer;
    public final Renderer blockOutlineDebugRenderer;
    public final Renderer chunkLoadingDebugRenderer;
    public final VillageDebugRenderer villageDebugRenderer;
    public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    public final BeeDebugRenderer beeDebugRenderer;
    public final RaidCenterDebugRenderer raidCenterDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    public final GameTestDebugRenderer gameTestDebugRenderer;
    public final GameEventDebugRenderer gameEventDebugRenderer;
    public final LightDebugRenderer lightDebugRenderer;
    public final BreezeDebugRenderer breezeDebugRenderer;
    public final ChunkDebugRenderer chunkDebugRenderer;
    public final OctreeDebugRenderer octreeDebugRenderer;
    private boolean showChunkBorder;
    private boolean showOctree;

    public DebugRenderer(MinecraftClient client) {
        this.waterDebugRenderer = new WaterDebugRenderer(client);
        this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(client);
        this.heightmapDebugRenderer = new HeightmapDebugRenderer(client);
        this.collisionDebugRenderer = new CollisionDebugRenderer(client);
        this.supportingBlockDebugRenderer = new SupportingBlockDebugRenderer(client);
        this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(client);
        this.redstoneUpdateOrderDebugRenderer = new RedstoneUpdateOrderDebugRenderer(client);
        this.structureDebugRenderer = new StructureDebugRenderer(client);
        this.skyLightDebugRenderer = new SkyLightDebugRenderer(client);
        this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer();
        this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(client);
        this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(client);
        this.villageDebugRenderer = new VillageDebugRenderer(client);
        this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
        this.beeDebugRenderer = new BeeDebugRenderer(client);
        this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(client);
        this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(client);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
        this.gameEventDebugRenderer = new GameEventDebugRenderer(client);
        this.lightDebugRenderer = new LightDebugRenderer(client, LightType.SKY);
        this.breezeDebugRenderer = new BreezeDebugRenderer(client);
        this.chunkDebugRenderer = new ChunkDebugRenderer(client);
        this.octreeDebugRenderer = new OctreeDebugRenderer(client);
    }

    public void reset() {
        this.pathfindingDebugRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderDebugRenderer.clear();
        this.heightmapDebugRenderer.clear();
        this.collisionDebugRenderer.clear();
        this.supportingBlockDebugRenderer.clear();
        this.neighborUpdateDebugRenderer.clear();
        this.structureDebugRenderer.clear();
        this.skyLightDebugRenderer.clear();
        this.worldGenAttemptDebugRenderer.clear();
        this.blockOutlineDebugRenderer.clear();
        this.chunkLoadingDebugRenderer.clear();
        this.villageDebugRenderer.clear();
        this.villageSectionsDebugRenderer.clear();
        this.beeDebugRenderer.clear();
        this.raidCenterDebugRenderer.clear();
        this.goalSelectorDebugRenderer.clear();
        this.gameTestDebugRenderer.clear();
        this.gameEventDebugRenderer.clear();
        this.lightDebugRenderer.clear();
        this.breezeDebugRenderer.clear();
        this.chunkDebugRenderer.clear();
    }

    public boolean toggleShowChunkBorder() {
        this.showChunkBorder = !this.showChunkBorder;
        return this.showChunkBorder;
    }

    public boolean toggleShowOctree() {
        this.showOctree = !this.showOctree;
        return this.showOctree;
    }

    public void render(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            this.chunkBorderDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        }
        if (this.showOctree) {
            this.octreeDebugRenderer.render(matrices, frustum, vertexConsumers, cameraX, cameraY, cameraZ);
        }
        this.gameTestDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    public void renderLate(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        this.chunkDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity entity, int maxDistance) {
        int j;
        Box lv4;
        Vec3d lv2;
        Vec3d lv3;
        if (entity == null) {
            return Optional.empty();
        }
        Vec3d lv = entity.getEyePos();
        EntityHitResult lv5 = ProjectileUtil.raycast(entity, lv, lv3 = lv.add(lv2 = entity.getRotationVec(1.0f).multiply(maxDistance)), lv4 = entity.getBoundingBox().stretch(lv2).expand(1.0), EntityPredicates.CAN_HIT, j = maxDistance * maxDistance);
        if (lv5 == null) {
            return Optional.empty();
        }
        if (lv.squaredDistanceTo(lv5.getPos()) > (double)j) {
            return Optional.empty();
        }
        return Optional.of(lv5.getEntity());
    }

    public static void drawBlockBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, float red, float green, float blue, float alpha) {
        DebugRenderer.drawBox(matrices, vertexConsumers, pos, pos.add(1, 1, 1), red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos1, BlockPos pos2, float red, float green, float blue, float alpha) {
        Camera lv = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!lv.isReady()) {
            return;
        }
        Vec3d lv2 = lv.getPos().negate();
        Box lv3 = Box.enclosing(pos1, pos2).offset(lv2);
        DebugRenderer.drawBox(matrices, vertexConsumers, lv3, red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, float expand, float red, float green, float blue, float alpha) {
        Camera lv = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!lv.isReady()) {
            return;
        }
        Vec3d lv2 = lv.getPos().negate();
        Box lv3 = new Box(pos).offset(lv2).expand(expand);
        DebugRenderer.drawBox(matrices, vertexConsumers, lv3, red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float red, float green, float blue, float alpha) {
        DebugRenderer.drawBox(matrices, vertexConsumers, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
        VertexRendering.drawFilledBox(matrices, lv, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, int x, int y, int z, int color) {
        DebugRenderer.drawString(matrices, vertexConsumers, string, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, color);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color) {
        DebugRenderer.drawString(matrices, vertexConsumers, string, x, y, z, color, 0.02f);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color, float size) {
        DebugRenderer.drawString(matrices, vertexConsumers, string, x, y, z, color, size, true, 0.0f, false);
    }

    public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color, float size, boolean center, float offset, boolean visibleThroughObjects) {
        MinecraftClient lv = MinecraftClient.getInstance();
        Camera lv2 = lv.gameRenderer.getCamera();
        if (!lv2.isReady() || lv.getEntityRenderDispatcher().gameOptions == null) {
            return;
        }
        TextRenderer lv3 = lv.textRenderer;
        double j = lv2.getPos().x;
        double k = lv2.getPos().y;
        double l = lv2.getPos().z;
        matrices.push();
        matrices.translate((float)(x - j), (float)(y - k) + 0.07f, (float)(z - l));
        matrices.multiply(lv2.getRotation());
        matrices.scale(size, -size, size);
        float m = center ? (float)(-lv3.getWidth(string)) / 2.0f : 0.0f;
        lv3.draw(string, m -= offset / size, 0.0f, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, visibleThroughObjects ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        matrices.pop();
    }

    private static Vec3d hueToRgb(float hue) {
        float g = 5.99999f;
        int i = (int)(MathHelper.clamp(hue, 0.0f, 1.0f) * 5.99999f);
        float h = hue * 5.99999f - (float)i;
        return switch (i) {
            case 0 -> new Vec3d(1.0, h, 0.0);
            case 1 -> new Vec3d(1.0f - h, 1.0, 0.0);
            case 2 -> new Vec3d(0.0, 1.0, h);
            case 3 -> new Vec3d(0.0, 1.0 - (double)h, 1.0);
            case 4 -> new Vec3d(h, 0.0, 1.0);
            case 5 -> new Vec3d(1.0, 0.0, 1.0 - (double)h);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    private static Vec3d shiftHue(float r, float g, float b, float dHue) {
        Vec3d lv = DebugRenderer.hueToRgb(dHue).multiply(r);
        Vec3d lv2 = DebugRenderer.hueToRgb((dHue + 0.33333334f) % 1.0f).multiply(g);
        Vec3d lv3 = DebugRenderer.hueToRgb((dHue + 0.6666667f) % 1.0f).multiply(b);
        Vec3d lv4 = lv.add(lv2).add(lv3);
        double d = Math.max(Math.max(1.0, lv4.x), Math.max(lv4.y, lv4.z));
        return new Vec3d(lv4.x / d, lv4.y / d, lv4.z / d);
    }

    public static void drawVoxelShapeOutlines(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha, boolean bl) {
        List<Box> list = shape.getBoundingBoxes();
        if (list.isEmpty()) {
            return;
        }
        int k = bl ? list.size() : list.size() * 8;
        VertexRendering.drawOutline(matrices, vertexConsumer, VoxelShapes.cuboid(list.get(0)), offsetX, offsetY, offsetZ, ColorHelper.fromFloats(alpha, red, green, blue));
        for (int l = 1; l < list.size(); ++l) {
            Box lv = list.get(l);
            float m = (float)l / (float)k;
            Vec3d lv2 = DebugRenderer.shiftHue(red, green, blue, m);
            VertexRendering.drawOutline(matrices, vertexConsumer, VoxelShapes.cuboid(lv), offsetX, offsetY, offsetZ, ColorHelper.fromFloats(alpha, (float)lv2.x, (float)lv2.y, (float)lv2.z));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Renderer {
        public void render(MatrixStack var1, VertexConsumerProvider var2, double var3, double var5, double var7);

        default public void clear() {
        }
    }
}

