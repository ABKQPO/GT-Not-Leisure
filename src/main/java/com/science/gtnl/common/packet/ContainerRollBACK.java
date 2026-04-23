package com.science.gtnl.common.packet;

import net.minecraft.inventory.ContainerPlayer;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.utils.RCAEBaseContainer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class ContainerRollBACK implements IMessage, IMessageHandler<ContainerRollBACK, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(ContainerRollBACK message, MessageContext ctx) {
        switch (ctx.side) {
            case SERVER -> {
                var entityPlayerMP = ctx.getServerHandler().playerEntity;
                ServerThreadUtil.addScheduledTask(() -> {
                    var newContainer = entityPlayerMP.openContainer;
                    if (newContainer instanceof RCAEBaseContainer rac) {
                        var gtnl$oldContainer = rac.rc$getOldContainer();
                        if (gtnl$oldContainer != null) {
                            if (gtnl$oldContainer instanceof ContainerPlayer) {
                                entityPlayerMP.closeContainer();
                            } else {
                                entityPlayerMP.getNextWindowId();
                                entityPlayerMP.closeContainer();
                                int windowId = entityPlayerMP.currentWindowId;
                                entityPlayerMP.openContainer = gtnl$oldContainer;
                                entityPlayerMP.openContainer.windowId = windowId;
                            }
                        }
                    }
                    ScienceNotLeisure.network.sendTo(message, entityPlayerMP);
                });
            }
            case CLIENT -> clientRun();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void clientRun() {
        if (GTNLInputHandler.delayMethod != null) {
            GTNLInputHandler.delayMethod.run();
            GTNLInputHandler.delayMethod = null;
            GTNLInputHandler.oldGui = null;
        }
    }
}
