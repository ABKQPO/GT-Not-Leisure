package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.config.TickrateConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class TickratePacket extends ClientboundPacket {

    private float tickrate;

    public TickratePacket() {}

    public TickratePacket(float tickrate) {
        this.tickrate = tickrate;
    }

    @Override
    protected void read(ByteBuf buf) {
        tickrate = buf.readFloat();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeFloat(tickrate);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        float tickrate = this.tickrate;
        if (tickrate < TickrateConfig.minTickrate) {
            ScienceNotLeisure.LOG.info(
                "Tickrate forced to change from {} to {}, because the value is too low (You can change the minimum tickrate in the config file)",
                tickrate,
                TickrateConfig.minTickrate);
            tickrate = TickrateConfig.minTickrate;
        } else if (tickrate > TickrateConfig.maxTickrate) {
            ScienceNotLeisure.LOG.info(
                "Tickrate forced to change from {} to {}, because the value is too high (You can change the maximum tickrate in the config file)",
                tickrate,
                TickrateConfig.maxTickrate);
            tickrate = TickrateConfig.maxTickrate;
        }

        TickrateAPI.updateClientTickrate(tickrate);
    }
}
