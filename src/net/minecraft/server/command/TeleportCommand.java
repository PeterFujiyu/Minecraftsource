/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LookTarget;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TeleportCommand {
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("teleport").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), Collections.singleton(((ServerCommandSource)context.getSource()).getEntityOrThrow()), ((ServerCommandSource)context.getSource()).getWorld(), Vec3ArgumentType.getPosArgument(context, "location"), null, null)))).then(CommandManager.argument("destination", EntityArgumentType.entity()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), Collections.singleton(((ServerCommandSource)context.getSource()).getEntityOrThrow()), EntityArgumentType.getEntity(context, "destination"))))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), ((ServerCommandSource)context.getSource()).getWorld(), Vec3ArgumentType.getPosArgument(context, "location"), null, null))).then(CommandManager.argument("rotation", RotationArgumentType.rotation()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), ((ServerCommandSource)context.getSource()).getWorld(), Vec3ArgumentType.getPosArgument(context, "location"), RotationArgumentType.getRotation(context, "rotation"), null)))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("facingEntity", EntityArgumentType.entity()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), ((ServerCommandSource)context.getSource()).getWorld(), Vec3ArgumentType.getPosArgument(context, "location"), null, new LookTarget.LookAtEntity(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET)))).then(CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), ((ServerCommandSource)context.getSource()).getWorld(), Vec3ArgumentType.getPosArgument(context, "location"), null, new LookTarget.LookAtEntity(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor(context, "facingAnchor")))))))).then(CommandManager.argument("facingLocation", Vec3ArgumentType.vec3()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), ((ServerCommandSource)context.getSource()).getWorld(), Vec3ArgumentType.getPosArgument(context, "location"), null, new LookTarget.LookAtPosition(Vec3ArgumentType.getVec3(context, "facingLocation")))))))).then(CommandManager.argument("destination", EntityArgumentType.entity()).executes(context -> TeleportCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), EntityArgumentType.getEntity(context, "destination"))))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tp").requires(source -> source.hasPermissionLevel(2))).redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
        for (Entity entity : targets) {
            TeleportCommand.teleport(source, entity, (ServerWorld)destination.getWorld(), destination.getX(), destination.getY(), destination.getZ(), EnumSet.noneOf(PositionFlag.class), destination.getYaw(), destination.getPitch(), null);
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.single", ((Entity)targets.iterator().next()).getDisplayName(), destination.getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
        }
        return targets.size();
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, ServerWorld world, PosArgument location, @Nullable PosArgument rotation, @Nullable LookTarget facingLocation) throws CommandSyntaxException {
        Vec3d lv = location.getPos(source);
        Vec2f lv2 = rotation == null ? null : rotation.getRotation(source);
        for (Entity entity : targets) {
            Set<PositionFlag> set = TeleportCommand.getFlags(location, rotation, entity.getWorld().getRegistryKey() == world.getRegistryKey());
            if (lv2 == null) {
                TeleportCommand.teleport(source, entity, world, lv.x, lv.y, lv.z, set, entity.getYaw(), entity.getPitch(), facingLocation);
                continue;
            }
            TeleportCommand.teleport(source, entity, world, lv.x, lv.y, lv.z, set, lv2.y, lv2.x, facingLocation);
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.teleport.success.location.single", ((Entity)targets.iterator().next()).getDisplayName(), TeleportCommand.formatFloat(arg.x), TeleportCommand.formatFloat(arg.y), TeleportCommand.formatFloat(arg.z)), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.teleport.success.location.multiple", targets.size(), TeleportCommand.formatFloat(arg.x), TeleportCommand.formatFloat(arg.y), TeleportCommand.formatFloat(arg.z)), true);
        }
        return targets.size();
    }

    private static Set<PositionFlag> getFlags(PosArgument pos, @Nullable PosArgument rotation, boolean sameDimension) {
        EnumSet<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
        if (pos.isXRelative()) {
            set.add(PositionFlag.DELTA_X);
            if (sameDimension) {
                set.add(PositionFlag.X);
            }
        }
        if (pos.isYRelative()) {
            set.add(PositionFlag.DELTA_Y);
            if (sameDimension) {
                set.add(PositionFlag.Y);
            }
        }
        if (pos.isZRelative()) {
            set.add(PositionFlag.DELTA_Z);
            if (sameDimension) {
                set.add(PositionFlag.Z);
            }
        }
        if (rotation == null || rotation.isXRelative()) {
            set.add(PositionFlag.X_ROT);
        }
        if (rotation == null || rotation.isYRelative()) {
            set.add(PositionFlag.Y_ROT);
        }
        return set;
    }

    private static String formatFloat(double d) {
        return String.format(Locale.ROOT, "%f", d);
    }

    private static void teleport(ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z, Set<PositionFlag> movementFlags, float yaw, float pitch, @Nullable LookTarget facingLocation) throws CommandSyntaxException {
        LivingEntity lv2;
        float o;
        BlockPos lv = BlockPos.ofFloored(x, y, z);
        if (!World.isValid(lv)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        double i = movementFlags.contains((Object)PositionFlag.X) ? x - target.getX() : x;
        double j = movementFlags.contains((Object)PositionFlag.Y) ? y - target.getY() : y;
        double k = movementFlags.contains((Object)PositionFlag.Z) ? z - target.getZ() : z;
        float l = movementFlags.contains((Object)PositionFlag.Y_ROT) ? yaw - target.getYaw() : yaw;
        float m = movementFlags.contains((Object)PositionFlag.X_ROT) ? pitch - target.getPitch() : pitch;
        float n = MathHelper.wrapDegrees(l);
        if (!target.teleport(world, i, j, k, movementFlags, n, o = MathHelper.wrapDegrees(m), true)) {
            return;
        }
        if (facingLocation != null) {
            facingLocation.look(source, target);
        }
        if (!(target instanceof LivingEntity) || !(lv2 = (LivingEntity)target).isGliding()) {
            target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
            target.setOnGround(true);
        }
        if (target instanceof PathAwareEntity) {
            PathAwareEntity lv3 = (PathAwareEntity)target;
            lv3.getNavigation().stop();
        }
    }
}

