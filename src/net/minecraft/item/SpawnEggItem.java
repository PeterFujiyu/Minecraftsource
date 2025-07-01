/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SpawnEggItem
extends Item {
    private static final Map<EntityType<? extends MobEntity>, SpawnEggItem> SPAWN_EGGS = Maps.newIdentityHashMap();
    private final EntityType<?> type;

    public SpawnEggItem(EntityType<? extends MobEntity> type, Item.Settings settings) {
        super(settings);
        this.type = type;
        SPAWN_EGGS.put(type, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        EntityType<?> lv7;
        World lv = context.getWorld();
        if (lv.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack lv2 = context.getStack();
        BlockPos lv3 = context.getBlockPos();
        Direction lv4 = context.getSide();
        BlockState lv5 = lv.getBlockState(lv3);
        BlockEntity blockEntity = lv.getBlockEntity(lv3);
        if (blockEntity instanceof Spawner) {
            Spawner lv6 = (Spawner)((Object)blockEntity);
            lv7 = this.getEntityType(lv.getRegistryManager(), lv2);
            lv6.setEntityType(lv7, lv.getRandom());
            lv.updateListeners(lv3, lv5, lv5, Block.NOTIFY_ALL);
            lv.emitGameEvent((Entity)context.getPlayer(), GameEvent.BLOCK_CHANGE, lv3);
            lv2.decrement(1);
            return ActionResult.SUCCESS;
        }
        BlockPos lv8 = lv5.getCollisionShape(lv, lv3).isEmpty() ? lv3 : lv3.offset(lv4);
        lv7 = this.getEntityType(lv.getRegistryManager(), lv2);
        if (lv7.spawnFromItemStack((ServerWorld)lv, lv2, context.getPlayer(), lv8, SpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(lv3, lv8) && lv4 == Direction.UP) != null) {
            lv2.decrement(1);
            lv.emitGameEvent((Entity)context.getPlayer(), GameEvent.ENTITY_PLACE, lv3);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        BlockHitResult lv2 = SpawnEggItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (lv2.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ServerWorld lv3 = (ServerWorld)world;
        BlockHitResult lv4 = lv2;
        BlockPos lv5 = lv4.getBlockPos();
        if (!(world.getBlockState(lv5).getBlock() instanceof FluidBlock)) {
            return ActionResult.PASS;
        }
        if (!world.canPlayerModifyAt(user, lv5) || !user.canPlaceOn(lv5, lv4.getSide(), lv)) {
            return ActionResult.FAIL;
        }
        EntityType<?> lv6 = this.getEntityType(lv3.getRegistryManager(), lv);
        Object lv7 = lv6.spawnFromItemStack(lv3, lv, user, lv5, SpawnReason.SPAWN_ITEM_USE, false, false);
        if (lv7 == null) {
            return ActionResult.PASS;
        }
        lv.decrementUnlessCreative(1, user);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        world.emitGameEvent((Entity)user, GameEvent.ENTITY_PLACE, ((Entity)lv7).getPos());
        return ActionResult.SUCCESS;
    }

    public boolean isOfSameEntityType(RegistryWrapper.WrapperLookup registries, ItemStack stack, EntityType<?> type) {
        return Objects.equals(this.getEntityType(registries, stack), type);
    }

    @Nullable
    public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
        return SPAWN_EGGS.get(type);
    }

    public static Iterable<SpawnEggItem> getAll() {
        return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
    }

    public EntityType<?> getEntityType(RegistryWrapper.WrapperLookup registries, ItemStack stack) {
        EntityType<?> lv2;
        NbtComponent lv = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        if (!lv.isEmpty() && (lv2 = lv.getRegistryValueOfId(registries, RegistryKeys.ENTITY_TYPE)) != null) {
            return lv2;
        }
        return this.type;
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.type.getRequiredFeatures();
    }

    public Optional<MobEntity> spawnBaby(PlayerEntity user, MobEntity entity, EntityType<? extends MobEntity> entityType, ServerWorld world, Vec3d pos, ItemStack stack) {
        if (!this.isOfSameEntityType(world.getRegistryManager(), stack, entityType)) {
            return Optional.empty();
        }
        MobEntity lv = entity instanceof PassiveEntity ? ((PassiveEntity)entity).createChild(world, (PassiveEntity)entity) : entityType.create(world, SpawnReason.SPAWN_ITEM_USE);
        if (lv == null) {
            return Optional.empty();
        }
        lv.setBaby(true);
        if (!lv.isBaby()) {
            return Optional.empty();
        }
        lv.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f);
        world.spawnEntityAndPassengers(lv);
        lv.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));
        stack.decrementUnlessCreative(1, user);
        return Optional.of(lv);
    }

    @Override
    public boolean shouldShowOperatorBlockWarnings(ItemStack stack, @Nullable PlayerEntity player) {
        NbtComponent lv;
        if (player != null && player.getPermissionLevel() >= 2 && (lv = stack.get(DataComponentTypes.ENTITY_DATA)) != null) {
            EntityType<?> lv2 = lv.getRegistryValueOfId(player.getWorld().getRegistryManager(), RegistryKeys.ENTITY_TYPE);
            return lv2 != null && lv2.canPotentiallyExecuteCommands();
        }
        return false;
    }
}

