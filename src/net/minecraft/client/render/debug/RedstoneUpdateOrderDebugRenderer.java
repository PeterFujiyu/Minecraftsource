/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.custom.DebugRedstoneUpdateOrderCustomPayload;
import net.minecraft.world.block.WireOrientation;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class RedstoneUpdateOrderDebugRenderer
implements DebugRenderer.Renderer {
    public static final int field_53174 = 200;
    private final MinecraftClient client;
    private final List<DebugRedstoneUpdateOrderCustomPayload> updateOrders = Lists.newArrayList();

    RedstoneUpdateOrderDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void addUpdateOrder(DebugRedstoneUpdateOrderCustomPayload updateOrder) {
        this.updateOrders.add(updateOrder);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getLines());
        long l = this.client.world.getTime();
        Iterator<DebugRedstoneUpdateOrderCustomPayload> iterator = this.updateOrders.iterator();
        while (iterator.hasNext()) {
            DebugRedstoneUpdateOrderCustomPayload lv2 = iterator.next();
            long m = l - lv2.time();
            if (m > 200L) {
                iterator.remove();
                continue;
            }
            for (DebugRedstoneUpdateOrderCustomPayload.Wire lv3 : lv2.wires()) {
                Vector3f vector3f = lv3.pos().toBottomCenterPos().subtract(cameraX, cameraY - 0.1, cameraZ).toVector3f();
                WireOrientation lv4 = lv3.orientation();
                VertexRendering.drawVector(matrices, lv, vector3f, lv4.getFront().getDoubleVector().multiply(0.5), -16776961);
                VertexRendering.drawVector(matrices, lv, vector3f, lv4.getUp().getDoubleVector().multiply(0.4), -65536);
                VertexRendering.drawVector(matrices, lv, vector3f, lv4.getRight().getDoubleVector().multiply(0.3), -256);
            }
        }
    }
}

