/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record PlayerInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
    private static final byte FORWARD = 1;
    private static final byte BACKWARD = 2;
    private static final byte LEFT = 4;
    private static final byte RIGHT = 8;
    private static final byte JUMP = 16;
    private static final byte SNEAK = 32;
    private static final byte SPRINT = 64;
    public static final PacketCodec<PacketByteBuf, PlayerInput> PACKET_CODEC = new PacketCodec<PacketByteBuf, PlayerInput>(){

        @Override
        public void encode(PacketByteBuf arg, PlayerInput arg2) {
            byte b = 0;
            b = (byte)(b | (arg2.forward() ? 1 : 0));
            b = (byte)(b | (arg2.backward() ? 2 : 0));
            b = (byte)(b | (arg2.left() ? 4 : 0));
            b = (byte)(b | (arg2.right() ? 8 : 0));
            b = (byte)(b | (arg2.jump() ? 16 : 0));
            b = (byte)(b | (arg2.sneak() ? 32 : 0));
            b = (byte)(b | (arg2.sprint() ? 64 : 0));
            arg.writeByte(b);
        }

        @Override
        public PlayerInput decode(PacketByteBuf arg) {
            byte b = arg.readByte();
            boolean bl = (b & 1) != 0;
            boolean bl2 = (b & 2) != 0;
            boolean bl3 = (b & 4) != 0;
            boolean bl4 = (b & 8) != 0;
            boolean bl5 = (b & 0x10) != 0;
            boolean bl6 = (b & 0x20) != 0;
            boolean bl7 = (b & 0x40) != 0;
            return new PlayerInput(bl, bl2, bl3, bl4, bl5, bl6, bl7);
        }

        @Override
        public /* synthetic */ void encode(Object object, Object object2) {
            this.encode((PacketByteBuf)object, (PlayerInput)object2);
        }

        @Override
        public /* synthetic */ Object decode(Object object) {
            return this.decode((PacketByteBuf)object);
        }
    };
    public static PlayerInput DEFAULT = new PlayerInput(false, false, false, false, false, false, false);
}

