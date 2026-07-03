package com.science.gtnl.common.packet;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class TickratePacket implements IMessage, IMessageHandler<TickratePacket, IMessage> {

    private float tickrate;

    public TickratePacket() {}

    public TickratePacket(float tickrate) {
        this.tickrate = tickrate;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tickrate = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(tickrate);
    }

    @Override
    public IMessage onMessage(TickratePacket msg, MessageContext context) {
        float tickrate = msg.tickrate;
        if (tickrate < MainConfig.tickrate.minTickrate) {
            ScienceNotLeisure.LOG.info(
                "Tickrate forced to change from {} to {}, because the value is too low (You can change the minimum tickrate in the config file)",
                tickrate,
                MainConfig.tickrate.minTickrate);
            tickrate = MainConfig.tickrate.minTickrate;
        } else if (tickrate > MainConfig.tickrate.maxTickrate) {
            ScienceNotLeisure.LOG.info(
                "Tickrate forced to change from {} to {}, because the value is too high (You can change the maximum tickrate in the config file)",
                tickrate,
                MainConfig.tickrate.maxTickrate);
            tickrate = MainConfig.tickrate.maxTickrate;
        }

        if (FMLCommonHandler.instance()
            .getSide() != Side.SERVER) {
            TickrateAPI.updateClientTickrate(tickrate);
        }
        return null;
    }
}
