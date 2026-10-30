package com.gtnllib.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnllib.GTNLLib;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public abstract class ServerboundPacket implements IMessage {

    private boolean invalid;

    @Override
    public final void fromBytes(ByteBuf buf) {
        try {
            this.read(buf);
        } catch (RuntimeException e) {
            invalidateMalformed(buf, e);
        }
    }

    @Override
    public final void toBytes(ByteBuf buf) {
        this.write(buf);
    }

    protected void read(ByteBuf buf) {}

    protected void write(ByteBuf buf) {}

    protected final void invalidateMalformed(ByteBuf buf, RuntimeException exception) {
        this.invalid = true;
        GTNLLib.LOG.warn(
            "Discarding malformed serverbound packet {} with {} readable bytes",
            getClass().getName(),
            buf.readableBytes(),
            exception);
    }

    public final boolean isInvalid() {
        return this.invalid;
    }

    public abstract void handleServer(EntityPlayerMP player);
}
