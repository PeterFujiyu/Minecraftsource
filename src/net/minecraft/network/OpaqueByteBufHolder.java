/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCounted;

public record OpaqueByteBufHolder(ByteBuf contents) implements ReferenceCounted
{
    public OpaqueByteBufHolder(ByteBuf buf) {
        this.contents = ByteBufUtil.ensureAccessible(buf);
    }

    public static Object pack(Object buf) {
        if (buf instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf)buf;
            return new OpaqueByteBufHolder(byteBuf);
        }
        return buf;
    }

    public static Object unpack(Object holder) {
        if (holder instanceof OpaqueByteBufHolder) {
            OpaqueByteBufHolder lv = (OpaqueByteBufHolder)holder;
            return ByteBufUtil.ensureAccessible(lv.contents);
        }
        return holder;
    }

    @Override
    public int refCnt() {
        return this.contents.refCnt();
    }

    @Override
    public OpaqueByteBufHolder retain() {
        this.contents.retain();
        return this;
    }

    @Override
    public OpaqueByteBufHolder retain(int i) {
        this.contents.retain(i);
        return this;
    }

    @Override
    public OpaqueByteBufHolder touch() {
        this.contents.touch();
        return this;
    }

    @Override
    public OpaqueByteBufHolder touch(Object object) {
        this.contents.touch(object);
        return this;
    }

    @Override
    public boolean release() {
        return this.contents.release();
    }

    @Override
    public boolean release(int count) {
        return this.contents.release(count);
    }

    @Override
    public /* synthetic */ ReferenceCounted touch(Object object) {
        return this.touch(object);
    }

    @Override
    public /* synthetic */ ReferenceCounted touch() {
        return this.touch();
    }

    @Override
    public /* synthetic */ ReferenceCounted retain(int count) {
        return this.retain(count);
    }

    @Override
    public /* synthetic */ ReferenceCounted retain() {
        return this.retain();
    }
}

