package com.science.gtnl.common.packet;

import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class TickratePacket implements IMessage {

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

    public static class Handler implements IMessageHandler<TickratePacket, IMessage> {

        @Override
        public IMessage onMessage(TickratePacket msg, MessageContext context) {
            float tickrate = msg.getTickrate();
            if (tickrate < MainConfig.minTickrate) {
                GTNLEarlyCoreMod.LOGGER.info(
                    "Tickrate forced to change from " + tickrate
                        + " to "
                        + MainConfig.minTickrate
                        + ", because the value is too low"
                        + " (You can change the minimum tickrate in the config file)");
                tickrate = MainConfig.minTickrate;
            } else if (tickrate > MainConfig.maxTickrate) {
                GTNLEarlyCoreMod.LOGGER.info(
                    "Tickrate forced to change from " + tickrate
                        + " to "
                        + MainConfig.maxTickrate
                        + ", because the value is too high"
                        + " (You can change the maximum tickrate in the config file)");
                tickrate = MainConfig.maxTickrate;
            }

            if (FMLCommonHandler.instance()
                .getSide() != Side.SERVER) {
                GTNLEarlyCoreMod.INSTANCE.updateClientTickrate(tickrate);
            }
            return null;
        }
    }
}
