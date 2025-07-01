/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class EntityTrackerEntry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29767 = 1;
    private static final double field_44988 = 7.62939453125E-6;
    public static final int field_44987 = 60;
    private static final int field_44989 = 400;
    private final ServerWorld world;
    private final Entity entity;
    private final int tickInterval;
    private final boolean alwaysUpdateVelocity;
    private final Consumer<Packet<?>> receiver;
    private final TrackedPosition trackedPos = new TrackedPosition();
    private byte lastYaw;
    private byte lastPitch;
    private byte lastHeadYaw;
    private Vec3d velocity;
    private int trackingTick;
    private int updatesWithoutVehicle;
    private List<Entity> lastPassengers = Collections.emptyList();
    private boolean hadVehicle;
    private boolean lastOnGround;
    @Nullable
    private List<DataTracker.SerializedEntry<?>> changedEntries;

    public EntityTrackerEntry(ServerWorld world, Entity entity, int tickInterval, boolean alwaysUpdateVelocity, Consumer<Packet<?>> receiver) {
        this.world = world;
        this.receiver = receiver;
        this.entity = entity;
        this.tickInterval = tickInterval;
        this.alwaysUpdateVelocity = alwaysUpdateVelocity;
        this.trackedPos.setPos(entity.getSyncedPos());
        this.velocity = entity.getVelocity();
        this.lastYaw = MathHelper.packDegrees(entity.getYaw());
        this.lastPitch = MathHelper.packDegrees(entity.getPitch());
        this.lastHeadYaw = MathHelper.packDegrees(entity.getHeadYaw());
        this.lastOnGround = entity.isOnGround();
        this.changedEntries = entity.getDataTracker().getChangedEntries();
    }

    public void tick() {
        Entity entity;
        List<Entity> list = this.entity.getPassengerList();
        if (!list.equals(this.lastPassengers)) {
            this.receiver.accept(new EntityPassengersSetS2CPacket(this.entity));
            EntityTrackerEntry.streamChangedPassengers(list, this.lastPassengers).forEach(passenger -> {
                if (passenger instanceof ServerPlayerEntity) {
                    ServerPlayerEntity lv = (ServerPlayerEntity)passenger;
                    lv.networkHandler.requestTeleport(lv.getX(), lv.getY(), lv.getZ(), lv.getYaw(), lv.getPitch());
                }
            });
            this.lastPassengers = list;
        }
        if ((entity = this.entity) instanceof ItemFrameEntity) {
            ItemFrameEntity lv = (ItemFrameEntity)entity;
            if (this.trackingTick % 10 == 0) {
                MapIdComponent lv3;
                MapState lv4;
                ItemStack lv2 = lv.getHeldItemStack();
                if (lv2.getItem() instanceof FilledMapItem && (lv4 = FilledMapItem.getMapState(lv3 = lv2.get(DataComponentTypes.MAP_ID), (World)this.world)) != null) {
                    for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers()) {
                        lv4.update(serverPlayerEntity, lv2);
                        Packet<?> lv6 = lv4.getPlayerMarkerPacket(lv3, serverPlayerEntity);
                        if (lv6 == null) continue;
                        serverPlayerEntity.networkHandler.sendPacket(lv6);
                    }
                }
                this.syncEntityData();
            }
        }
        if (this.trackingTick % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
            boolean bl;
            byte b = MathHelper.packDegrees(this.entity.getYaw());
            byte c = MathHelper.packDegrees(this.entity.getPitch());
            boolean bl2 = bl = Math.abs(b - this.lastYaw) >= 1 || Math.abs(c - this.lastPitch) >= 1;
            if (this.entity.hasVehicle()) {
                if (bl) {
                    this.receiver.accept(new EntityS2CPacket.Rotate(this.entity.getId(), b, c, this.entity.isOnGround()));
                    this.lastYaw = b;
                    this.lastPitch = c;
                }
                this.trackedPos.setPos(this.entity.getSyncedPos());
                this.syncEntityData();
                this.hadVehicle = true;
            } else {
                AbstractMinecartEntity lv7;
                MinecartController minecartController;
                Entity entity2 = this.entity;
                if (entity2 instanceof AbstractMinecartEntity && (minecartController = (lv7 = (AbstractMinecartEntity)entity2).getController()) instanceof ExperimentalMinecartController) {
                    ExperimentalMinecartController lv8 = (ExperimentalMinecartController)minecartController;
                    this.tickExperimentalMinecart(lv8, b, c, bl);
                } else {
                    Vec3d lv11;
                    double d;
                    boolean bl6;
                    ++this.updatesWithoutVehicle;
                    Vec3d vec3d = this.entity.getSyncedPos();
                    boolean bl22 = this.trackedPos.subtract(vec3d).lengthSquared() >= 7.62939453125E-6;
                    Packet<ClientPlayPacketListener> lv10 = null;
                    boolean bl3 = bl22 || this.trackingTick % 60 == 0;
                    boolean bl4 = false;
                    boolean bl5 = false;
                    long l = this.trackedPos.getDeltaX(vec3d);
                    long m = this.trackedPos.getDeltaY(vec3d);
                    long n = this.trackedPos.getDeltaZ(vec3d);
                    boolean bl7 = bl6 = l < -32768L || l > 32767L || m < -32768L || m > 32767L || n < -32768L || n > 32767L;
                    if (bl6 || this.updatesWithoutVehicle > 400 || this.hadVehicle || this.lastOnGround != this.entity.isOnGround()) {
                        this.lastOnGround = this.entity.isOnGround();
                        this.updatesWithoutVehicle = 0;
                        lv10 = EntityPositionSyncS2CPacket.create(this.entity);
                        bl4 = true;
                        bl5 = true;
                    } else if (bl3 && bl || this.entity instanceof PersistentProjectileEntity) {
                        lv10 = new EntityS2CPacket.RotateAndMoveRelative(this.entity.getId(), (short)l, (short)m, (short)n, b, c, this.entity.isOnGround());
                        bl4 = true;
                        bl5 = true;
                    } else if (bl3) {
                        lv10 = new EntityS2CPacket.MoveRelative(this.entity.getId(), (short)l, (short)m, (short)n, this.entity.isOnGround());
                        bl4 = true;
                    } else if (bl) {
                        lv10 = new EntityS2CPacket.Rotate(this.entity.getId(), b, c, this.entity.isOnGround());
                        bl5 = true;
                    }
                    if ((this.entity.velocityDirty || this.alwaysUpdateVelocity || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isGliding()) && ((d = (lv11 = this.entity.getVelocity()).squaredDistanceTo(this.velocity)) > 1.0E-7 || d > 0.0 && lv11.lengthSquared() == 0.0)) {
                        this.velocity = lv11;
                        Entity entity3 = this.entity;
                        if (entity3 instanceof ExplosiveProjectileEntity) {
                            ExplosiveProjectileEntity lv12 = (ExplosiveProjectileEntity)entity3;
                            this.receiver.accept(new BundleS2CPacket((Iterable<Packet<? super ClientPlayPacketListener>>)List.of(new EntityVelocityUpdateS2CPacket(this.entity.getId(), this.velocity), new ProjectilePowerS2CPacket(lv12.getId(), lv12.accelerationPower))));
                        } else {
                            this.receiver.accept(new EntityVelocityUpdateS2CPacket(this.entity.getId(), this.velocity));
                        }
                    }
                    if (lv10 != null) {
                        this.receiver.accept(lv10);
                    }
                    this.syncEntityData();
                    if (bl4) {
                        this.trackedPos.setPos(vec3d);
                    }
                    if (bl5) {
                        this.lastYaw = b;
                        this.lastPitch = c;
                    }
                    this.hadVehicle = false;
                }
            }
            byte e = MathHelper.packDegrees(this.entity.getHeadYaw());
            if (Math.abs(e - this.lastHeadYaw) >= 1) {
                this.receiver.accept(new EntitySetHeadYawS2CPacket(this.entity, e));
                this.lastHeadYaw = e;
            }
            this.entity.velocityDirty = false;
        }
        ++this.trackingTick;
        if (this.entity.velocityModified) {
            this.entity.velocityModified = false;
            this.sendSyncPacket(new EntityVelocityUpdateS2CPacket(this.entity));
        }
    }

    private void tickExperimentalMinecart(ExperimentalMinecartController controller, byte yaw, byte pitch, boolean changedAngles) {
        this.syncEntityData();
        if (controller.stagingLerpSteps.isEmpty()) {
            boolean bl3;
            Vec3d lv = this.entity.getVelocity();
            double d = lv.squaredDistanceTo(this.velocity);
            Vec3d lv2 = this.entity.getSyncedPos();
            boolean bl2 = this.trackedPos.subtract(lv2).lengthSquared() >= 7.62939453125E-6;
            boolean bl = bl3 = bl2 || this.trackingTick % 60 == 0;
            if (bl3 || changedAngles || d > 1.0E-7) {
                this.receiver.accept(new MoveMinecartAlongTrackS2CPacket(this.entity.getId(), List.of(new ExperimentalMinecartController.Step(this.entity.getPos(), this.entity.getVelocity(), this.entity.getYaw(), this.entity.getPitch(), 1.0f))));
            }
        } else {
            this.receiver.accept(new MoveMinecartAlongTrackS2CPacket(this.entity.getId(), List.copyOf(controller.stagingLerpSteps)));
            controller.stagingLerpSteps.clear();
        }
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.trackedPos.setPos(this.entity.getPos());
    }

    private static Stream<Entity> streamChangedPassengers(List<Entity> passengers, List<Entity> lastPassengers) {
        return lastPassengers.stream().filter(passenger -> !passengers.contains(passenger));
    }

    public void stopTracking(ServerPlayerEntity player) {
        this.entity.onStoppedTrackingBy(player);
        player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(this.entity.getId()));
    }

    public void startTracking(ServerPlayerEntity player) {
        ArrayList<Packet<? super ClientPlayPacketListener>> list = new ArrayList<Packet<? super ClientPlayPacketListener>>();
        this.sendPackets(player, list::add);
        player.networkHandler.sendPacket(new BundleS2CPacket((Iterable<Packet<? super ClientPlayPacketListener>>)list));
        this.entity.onStartedTrackingBy(player);
    }

    public void sendPackets(ServerPlayerEntity player, Consumer<Packet<ClientPlayPacketListener>> sender) {
        Leashable lv5;
        Entity entity;
        if (this.entity.isRemoved()) {
            LOGGER.warn("Fetching packet for removed entity {}", (Object)this.entity);
        }
        Packet<ClientPlayPacketListener> lv = this.entity.createSpawnPacket(this);
        sender.accept(lv);
        if (this.changedEntries != null) {
            sender.accept(new EntityTrackerUpdateS2CPacket(this.entity.getId(), this.changedEntries));
        }
        boolean bl = this.alwaysUpdateVelocity;
        if (this.entity instanceof LivingEntity) {
            Collection<EntityAttributeInstance> collection = ((LivingEntity)this.entity).getAttributes().getAttributesToSend();
            if (!collection.isEmpty()) {
                sender.accept(new EntityAttributesS2CPacket(this.entity.getId(), collection));
            }
            if (((LivingEntity)this.entity).isGliding()) {
                bl = true;
            }
        }
        if (bl && !(this.entity instanceof LivingEntity)) {
            sender.accept(new EntityVelocityUpdateS2CPacket(this.entity.getId(), this.velocity));
        }
        if ((entity = this.entity) instanceof LivingEntity) {
            LivingEntity lv2 = (LivingEntity)entity;
            ArrayList<Pair<EquipmentSlot, ItemStack>> list = Lists.newArrayList();
            for (EquipmentSlot lv3 : EquipmentSlot.VALUES) {
                ItemStack lv4 = lv2.getEquippedStack(lv3);
                if (lv4.isEmpty()) continue;
                list.add(Pair.of(lv3, lv4.copy()));
            }
            if (!list.isEmpty()) {
                sender.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getId(), list));
            }
        }
        if (!this.entity.getPassengerList().isEmpty()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity.hasVehicle()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
        }
        if ((entity = this.entity) instanceof Leashable && (lv5 = (Leashable)((Object)entity)).isLeashed()) {
            sender.accept(new EntityAttachS2CPacket(this.entity, lv5.getLeashHolder()));
        }
    }

    public Vec3d getPos() {
        return this.trackedPos.getPos();
    }

    public Vec3d getVelocity() {
        return this.velocity;
    }

    public float getPitch() {
        return MathHelper.unpackDegrees(this.lastPitch);
    }

    public float getYaw() {
        return MathHelper.unpackDegrees(this.lastYaw);
    }

    public float getHeadYaw() {
        return MathHelper.unpackDegrees(this.lastHeadYaw);
    }

    private void syncEntityData() {
        DataTracker lv = this.entity.getDataTracker();
        List<DataTracker.SerializedEntry<?>> list = lv.getDirtyEntries();
        if (list != null) {
            this.changedEntries = lv.getChangedEntries();
            this.sendSyncPacket(new EntityTrackerUpdateS2CPacket(this.entity.getId(), list));
        }
        if (this.entity instanceof LivingEntity) {
            Set<EntityAttributeInstance> set = ((LivingEntity)this.entity).getAttributes().getTracked();
            if (!set.isEmpty()) {
                this.sendSyncPacket(new EntityAttributesS2CPacket(this.entity.getId(), set));
            }
            set.clear();
        }
    }

    private void sendSyncPacket(Packet<?> packet) {
        this.receiver.accept(packet);
        if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
        }
    }
}

