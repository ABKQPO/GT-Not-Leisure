package com.gtnllib.network;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public abstract class ClientboundPacket implements IMessage {

    @Override
    public final void fromBytes(ByteBuf buf) {
        this.read(buf);
    }

    @Override
    public final void toBytes(ByteBuf buf) {
        this.write(buf);
    }

    protected void read(ByteBuf buf) {}

    protected void write(ByteBuf buf) {}

    @SideOnly(Side.CLIENT)
    public abstract void handleClient(Minecraft minecraft);

    @SideOnly(Side.CLIENT)
    protected static void runInWorld(Minecraft minecraft, Runnable task) {
        var world = minecraft.theWorld;
        if (world != null) minecraft.func_152344_a(() -> { if (minecraft.theWorld == world) task.run(); });
    }
}
