package com.science.gtnl.common.packet;

import static com.science.gtnl.utils.event.SubscribeEventUtils.CIRCUIT_NANITES_DATA_LOAD;

import net.minecraft.client.Minecraft;

import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.loader.RecipeLoader;

import io.netty.buffer.ByteBuf;

public class SyncCircuitNanitesPacket extends ClientboundPacket {

    public long worldSeed;

    public SyncCircuitNanitesPacket() {}

    public SyncCircuitNanitesPacket(long worldSeed) {
        this.worldSeed = worldSeed;
    }

    @Override
    protected void read(ByteBuf buf) {
        worldSeed = buf.readLong();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeLong(worldSeed);
    }

    @Override
    public void handleClient(Minecraft minecraft) {
        if (!CIRCUIT_NANITES_DATA_LOAD) {
            RecipeLoader.loadCircuitNanitesData(worldSeed);
        }
        CIRCUIT_NANITES_DATA_LOAD = true;
    }

}
