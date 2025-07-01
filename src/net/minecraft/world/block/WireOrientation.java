/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.world.block;

import com.google.common.annotations.VisibleForTesting;
import io.netty.buffer.ByteBuf;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class WireOrientation {
    public static final PacketCodec<ByteBuf, WireOrientation> PACKET_CODEC = PacketCodecs.indexed(WireOrientation::fromOrdinal, WireOrientation::ordinal);
    private static final WireOrientation[] VALUES = Util.make(() -> {
        WireOrientation[] lvs = new WireOrientation[48];
        WireOrientation.initializeValuesArray(new WireOrientation(Direction.UP, Direction.NORTH, SideBias.LEFT), lvs);
        return lvs;
    });
    private final Direction up;
    private final Direction front;
    private final Direction right;
    private final SideBias sideBias;
    private final int ordinal;
    private final List<Direction> directionsByPriority;
    private final List<Direction> horizontalDirections;
    private final List<Direction> verticalDirections;
    private final Map<Direction, WireOrientation> siblingsByFront = new EnumMap<Direction, WireOrientation>(Direction.class);
    private final Map<Direction, WireOrientation> siblingsByUp = new EnumMap<Direction, WireOrientation>(Direction.class);
    private final Map<SideBias, WireOrientation> siblingsBySideBias = new EnumMap<SideBias, WireOrientation>(SideBias.class);

    private WireOrientation(Direction up, Direction front, SideBias sideBias) {
        this.up = up;
        this.front = front;
        this.sideBias = sideBias;
        this.ordinal = WireOrientation.ordinalFromComponents(up, front, sideBias);
        Vec3i lv = front.getVector().crossProduct(up.getVector());
        Direction lv2 = Direction.fromVector(lv, null);
        Objects.requireNonNull(lv2);
        this.right = this.sideBias == SideBias.RIGHT ? lv2 : lv2.getOpposite();
        this.directionsByPriority = List.of(this.front.getOpposite(), this.front, this.right, this.right.getOpposite(), this.up.getOpposite(), this.up);
        this.horizontalDirections = this.directionsByPriority.stream().filter(direction -> direction.getAxis() != this.up.getAxis()).toList();
        this.verticalDirections = this.directionsByPriority.stream().filter(direction -> direction.getAxis() == this.up.getAxis()).toList();
    }

    public static WireOrientation of(Direction up, Direction front, SideBias sideBias) {
        return VALUES[WireOrientation.ordinalFromComponents(up, front, sideBias)];
    }

    public WireOrientation withUp(Direction direction) {
        return this.siblingsByUp.get(direction);
    }

    public WireOrientation withFront(Direction direction) {
        return this.siblingsByFront.get(direction);
    }

    public WireOrientation withFrontIfNotUp(Direction direction) {
        if (direction.getAxis() == this.up.getAxis()) {
            return this;
        }
        return this.siblingsByFront.get(direction);
    }

    public WireOrientation withFrontAndSideBias(Direction direction) {
        WireOrientation lv = this.withFront(direction);
        if (this.front == lv.right) {
            return lv.withOppositeSideBias();
        }
        return lv;
    }

    public WireOrientation withSideBias(SideBias sideBias) {
        return this.siblingsBySideBias.get((Object)sideBias);
    }

    public WireOrientation withOppositeSideBias() {
        return this.withSideBias(this.sideBias.opposite());
    }

    public Direction getFront() {
        return this.front;
    }

    public Direction getUp() {
        return this.up;
    }

    public Direction getRight() {
        return this.right;
    }

    public SideBias getSideBias() {
        return this.sideBias;
    }

    public List<Direction> getDirectionsByPriority() {
        return this.directionsByPriority;
    }

    public List<Direction> getHorizontalDirections() {
        return this.horizontalDirections;
    }

    public List<Direction> getVerticalDirections() {
        return this.verticalDirections;
    }

    public String toString() {
        return "[up=" + String.valueOf(this.up) + ",front=" + String.valueOf(this.front) + ",sideBias=" + String.valueOf((Object)this.sideBias) + "]";
    }

    public int ordinal() {
        return this.ordinal;
    }

    public static WireOrientation fromOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

    public static WireOrientation random(Random random) {
        return Util.getRandom(VALUES, random);
    }

    private static WireOrientation initializeValuesArray(WireOrientation prime, WireOrientation[] valuesOut) {
        Direction lv3;
        if (valuesOut[prime.ordinal()] != null) {
            return valuesOut[prime.ordinal()];
        }
        valuesOut[prime.ordinal()] = prime;
        for (SideBias sideBias : SideBias.values()) {
            prime.siblingsBySideBias.put(sideBias, WireOrientation.initializeValuesArray(new WireOrientation(prime.up, prime.front, sideBias), valuesOut));
        }
        for (Enum enum_ : Direction.values()) {
            lv3 = prime.up;
            if (enum_ == prime.up) {
                lv3 = prime.front.getOpposite();
            }
            if (enum_ == prime.up.getOpposite()) {
                lv3 = prime.front;
            }
            prime.siblingsByFront.put((Direction)enum_, WireOrientation.initializeValuesArray(new WireOrientation(lv3, (Direction)enum_, prime.sideBias), valuesOut));
        }
        for (Enum enum_ : Direction.values()) {
            lv3 = prime.front;
            if (enum_ == prime.front) {
                lv3 = prime.up.getOpposite();
            }
            if (enum_ == prime.front.getOpposite()) {
                lv3 = prime.up;
            }
            prime.siblingsByUp.put((Direction)enum_, WireOrientation.initializeValuesArray(new WireOrientation((Direction)enum_, lv3, prime.sideBias), valuesOut));
        }
        return prime;
    }

    @VisibleForTesting
    protected static int ordinalFromComponents(Direction up, Direction front, SideBias sideBias) {
        if (up.getAxis() == front.getAxis()) {
            throw new IllegalStateException("Up-vector and front-vector can not be on the same axis");
        }
        int i = up.getAxis() == Direction.Axis.Y ? (front.getAxis() == Direction.Axis.X ? 1 : 0) : (front.getAxis() == Direction.Axis.Y ? 1 : 0);
        int j = i << 1 | front.getDirection().ordinal();
        return ((up.ordinal() << 2) + j << 1) + sideBias.ordinal();
    }

    public static enum SideBias {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        private SideBias(String name) {
            this.name = name;
        }

        public SideBias opposite() {
            return this == LEFT ? RIGHT : LEFT;
        }

        public String toString() {
            return this.name;
        }
    }
}

