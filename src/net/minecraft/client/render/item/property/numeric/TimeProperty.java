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
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class TimeProperty
extends NeedleAngleState
implements NumericProperty {
    public static final MapCodec<TimeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleAngleState::hasWobble), ((MapCodec)Source.CODEC.fieldOf("source")).forGetter(property -> property.source)).apply((Applicative<TimeProperty, ?>)instance, TimeProperty::new));
    private final Source source;
    private final Random random = Random.create();
    private final NeedleAngleState.Angler angler;

    public TimeProperty(boolean wobble, Source source) {
        super(wobble);
        this.source = source;
        this.angler = this.createAngler(0.9f);
    }

    @Override
    protected float getAngle(ItemStack stack, ClientWorld world, int seed, Entity user) {
        float f = this.source.getAngle(world, stack, user, this.random);
        long l = world.getTime();
        if (this.angler.shouldUpdate(l)) {
            this.angler.update(l, f);
        }
        return this.angler.getAngle();
    }

    public MapCodec<TimeProperty> getCodec() {
        return CODEC;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Source implements StringIdentifiable
    {
        RANDOM("random"){

            @Override
            public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
                return random.nextFloat();
            }
        }
        ,
        DAYTIME("daytime"){

            @Override
            public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
                return world.getSkyAngle(1.0f);
            }
        }
        ,
        MOON_PHASE("moon_phase"){

            @Override
            public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
                return (float)world.getMoonPhase() / 8.0f;
            }
        };

        public static final Codec<Source> CODEC;
        private final String name;

        Source(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        abstract float getAngle(ClientWorld var1, ItemStack var2, Entity var3, Random var4);

        static {
            CODEC = StringIdentifiable.createCodec(Source::values);
        }
    }
}

