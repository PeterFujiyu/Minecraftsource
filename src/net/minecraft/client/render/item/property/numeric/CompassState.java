/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NeedleAngleState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompassState
extends NeedleAngleState {
    public static final MapCodec<CompassState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleAngleState::hasWobble), ((MapCodec)Target.CODEC.fieldOf("target")).forGetter(CompassState::getTarget)).apply((Applicative<CompassState, ?>)instance, CompassState::new));
    private final NeedleAngleState.Angler aimedAngler;
    private final NeedleAngleState.Angler aimlessAngler;
    private final Target target;
    private final Random random = Random.create();

    public CompassState(boolean wobble, Target target) {
        super(wobble);
        this.aimedAngler = this.createAngler(0.8f);
        this.aimlessAngler = this.createAngler(0.8f);
        this.target = target;
    }

    @Override
    protected float getAngle(ItemStack stack, ClientWorld world, int seed, Entity user) {
        GlobalPos lv = this.target.getPosition(world, stack, user);
        long l = world.getTime();
        if (!CompassState.canPointTo(user, lv)) {
            return this.getAimlessAngle(seed, l);
        }
        return this.getAngleTo(user, l, lv.pos());
    }

    private float getAimlessAngle(int seed, long time) {
        if (this.aimlessAngler.shouldUpdate(time)) {
            this.aimlessAngler.update(time, this.random.nextFloat());
        }
        float f = this.aimlessAngler.getAngle() + (float)CompassState.scatter(seed) / 2.1474836E9f;
        return MathHelper.floorMod(f, 1.0f);
    }

    private float getAngleTo(Entity entity, long time, BlockPos pos) {
        float h;
        PlayerEntity lv;
        float f = (float)CompassState.getAngleTo(entity, pos);
        float g = CompassState.getBodyYaw(entity);
        if (entity instanceof PlayerEntity && (lv = (PlayerEntity)entity).isMainPlayer() && lv.getWorld().getTickManager().shouldTick()) {
            if (this.aimedAngler.shouldUpdate(time)) {
                this.aimedAngler.update(time, 0.5f - (g - 0.25f));
            }
            h = f + this.aimedAngler.getAngle();
        } else {
            h = 0.5f - (g - 0.25f - f);
        }
        return MathHelper.floorMod(h, 1.0f);
    }

    private static boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
        return pos != null && pos.dimension() == entity.getWorld().getRegistryKey() && !(pos.pos().getSquaredDistance(entity.getPos()) < (double)1.0E-5f);
    }

    private static double getAngleTo(Entity entity, BlockPos pos) {
        Vec3d lv = Vec3d.ofCenter(pos);
        return Math.atan2(lv.getZ() - entity.getZ(), lv.getX() - entity.getX()) / 6.2831854820251465;
    }

    private static float getBodyYaw(Entity entity) {
        return MathHelper.floorMod(entity.getBodyYaw() / 360.0f, 1.0f);
    }

    private static int scatter(int seed) {
        return seed * 1327217883;
    }

    protected Target getTarget() {
        return this.target;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Target implements StringIdentifiable
    {
        NONE("none"){

            @Override
            @Nullable
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                return null;
            }
        }
        ,
        LODESTONE("lodestone"){

            @Override
            @Nullable
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                LodestoneTrackerComponent lv = stack.get(DataComponentTypes.LODESTONE_TRACKER);
                return lv != null ? (GlobalPos)lv.target().orElse(null) : null;
            }
        }
        ,
        SPAWN("spawn"){

            @Override
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                return GlobalPos.create(world.getRegistryKey(), world.getSpawnPos());
            }
        }
        ,
        RECOVERY("recovery"){

            @Override
            @Nullable
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                GlobalPos globalPos;
                if (holder instanceof PlayerEntity) {
                    PlayerEntity lv = (PlayerEntity)holder;
                    globalPos = lv.getLastDeathPos().orElse(null);
                } else {
                    globalPos = null;
                }
                return globalPos;
            }
        };

        public static final Codec<Target> CODEC;
        private final String name;

        Target(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Nullable
        abstract GlobalPos getPosition(ClientWorld var1, ItemStack var2, Entity var3);

        static {
            CODEC = StringIdentifiable.createCodec(Target::values);
        }
    }
}

