/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.projectile.thrown;

import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderPearlEntity
extends ThrownItemEntity {
    private long chunkTicketExpiryTicks = 0L;

    public EnderPearlEntity(EntityType<? extends EnderPearlEntity> arg, World arg2) {
        super((EntityType<? extends ThrownItemEntity>)arg, arg2);
    }

    public EnderPearlEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityType.ENDER_PEARL, owner, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void setOwner(UUID uuid) {
        this.removeFromOwner();
        super.setOwner(uuid);
        this.addToOwner();
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        this.removeFromOwner();
        super.setOwner(entity);
        this.addToOwner();
    }

    private void removeFromOwner() {
        Entity entity = this.getOwner();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)entity;
            lv.removeEnderPearl(this);
        }
    }

    private void addToOwner() {
        Entity entity = this.getOwner();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)entity;
            lv.addEnderPearl(this);
        }
    }

    @Override
    @Nullable
    protected Entity getEntity(UUID uuid) {
        World world = this.getWorld();
        if (!(world instanceof ServerWorld)) {
            return null;
        }
        ServerWorld lv = (ServerWorld)world;
        Entity lv2 = super.getEntity(uuid);
        if (lv2 != null) {
            return lv2;
        }
        for (ServerWorld lv3 : lv.getServer().getWorlds()) {
            if (lv3 == lv || (lv2 = lv3.getEntity(uuid)) == null) continue;
            return lv2;
        }
        return null;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.getOwner()), 0.0f);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        ServerWorld lv;
        block15: {
            block14: {
                super.onCollision(hitResult);
                for (int i = 0; i < 32; ++i) {
                    this.getWorld().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
                }
                World world = this.getWorld();
                if (!(world instanceof ServerWorld)) break block14;
                lv = (ServerWorld)world;
                if (!this.isRemoved()) break block15;
            }
            return;
        }
        Entity lv2 = this.getOwner();
        if (lv2 == null || !EnderPearlEntity.canTeleportEntityTo(lv2, lv)) {
            this.discard();
            return;
        }
        if (lv2.hasVehicle()) {
            lv2.detach();
        }
        Vec3d lv3 = this.getLastRenderPos();
        if (lv2 instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv4 = (ServerPlayerEntity)lv2;
            if (lv4.networkHandler.isConnectionOpen()) {
                ServerPlayerEntity lv6;
                EndermiteEntity lv5;
                if (this.random.nextFloat() < 0.05f && lv.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && (lv5 = EntityType.ENDERMITE.create(lv, SpawnReason.TRIGGERED)) != null) {
                    lv5.refreshPositionAndAngles(lv2.getX(), lv2.getY(), lv2.getZ(), lv2.getYaw(), lv2.getPitch());
                    lv.spawnEntity(lv5);
                }
                if (this.hasPortalCooldown()) {
                    lv2.resetPortalCooldown();
                }
                if ((lv6 = lv4.teleportTo(new TeleportTarget(lv, lv3, Vec3d.ZERO, 0.0f, 0.0f, PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA), TeleportTarget.NO_OP))) != null) {
                    lv6.onLanding();
                    lv6.clearCurrentExplosion();
                    lv6.damage(lv4.getServerWorld(), this.getDamageSources().enderPearl(), 5.0f);
                }
                this.playTeleportSound(lv, lv3);
            }
        } else {
            Entity lv7 = lv2.teleportTo(new TeleportTarget(lv, lv3, lv2.getVelocity(), lv2.getYaw(), lv2.getPitch(), TeleportTarget.NO_OP));
            if (lv7 != null) {
                lv7.onLanding();
            }
            this.playTeleportSound(lv, lv3);
        }
        this.discard();
    }

    private static boolean canTeleportEntityTo(Entity entity, World world) {
        if (entity.getWorld().getRegistryKey() == world.getRegistryKey()) {
            if (entity instanceof LivingEntity) {
                LivingEntity lv = (LivingEntity)entity;
                return lv.isAlive() && !lv.isSleeping();
            }
            return entity.isAlive();
        }
        return entity.canUsePortals(true);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void tick() {
        i = ChunkSectionPos.getSectionCoordFloored(this.getPos().getX());
        j = ChunkSectionPos.getSectionCoordFloored(this.getPos().getZ());
        lv = this.getOwner();
        if (!(lv instanceof ServerPlayerEntity)) ** GOTO lbl-1000
        lv2 = (ServerPlayerEntity)lv;
        if (!lv.isAlive() && lv2.getServerWorld().getGameRules().getBoolean(GameRules.ENDER_PEARLS_VANISH_ON_DEATH)) {
            this.discard();
        } else lbl-1000:
        // 2 sources

        {
            super.tick();
        }
        if (!this.isAlive()) {
            return;
        }
        lv3 = BlockPos.ofFloored(this.getPos());
        if ((--this.chunkTicketExpiryTicks <= 0L || i != ChunkSectionPos.getSectionCoord(lv3.getX()) || j != ChunkSectionPos.getSectionCoord(lv3.getZ())) && lv instanceof ServerPlayerEntity) {
            lv4 = (ServerPlayerEntity)lv;
            this.chunkTicketExpiryTicks = lv4.handleThrownEnderPearl(this);
        }
    }

    private void playTeleportSound(World world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
    }

    @Override
    @Nullable
    public Entity teleportTo(TeleportTarget teleportTarget) {
        Entity lv = super.teleportTo(teleportTarget);
        if (lv != null) {
            lv.addPortalChunkTicketAt(BlockPos.ofFloored(lv.getPos()));
        }
        return lv;
    }

    @Override
    public boolean canTeleportBetween(World from, World to) {
        Entity entity;
        if (from.getRegistryKey() == World.END && to.getRegistryKey() == World.OVERWORLD && (entity = this.getOwner()) instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)entity;
            return super.canTeleportBetween(from, to) && lv.seenCredits;
        }
        return super.canTeleportBetween(from, to);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        Entity entity;
        super.onBlockCollision(state);
        if (state.isOf(Blocks.END_GATEWAY) && (entity = this.getOwner()) instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)entity;
            lv.onBlockCollision(state);
        }
    }

    @Override
    public void onRemove(Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            this.removeFromOwner();
        }
        super.onRemove(reason);
    }
}

