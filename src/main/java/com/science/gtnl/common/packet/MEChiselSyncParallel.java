package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class MEChiselSyncParallel implements IMessage, IMessageHandler<MEChiselSyncParallel, IMessage> {

    private int x;
    private int y;
    private int z;
    private int parallel;

    public MEChiselSyncParallel() {

    }

    public MEChiselSyncParallel(TileEntityMEChisel te) {
        x = te.xCoord;
        y = te.yCoord;
        z = te.zCoord;
        parallel = te.getParallel();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        parallel = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(parallel);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(MEChiselSyncParallel message, MessageContext ctx) {
        switch (ctx.side) {
            case SERVER -> {
                var world = ctx.getServerHandler().playerEntity.worldObj;
                ServerThreadUtil.addScheduledTask(() -> {
                    if (world.getTileEntity(message.x, message.y, message.z) instanceof TileEntityMEChisel te) {
                        te.setParallel(message.parallel);
                    }
                });
            }
            case CLIENT -> onClient(message, ctx);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void onClient(MEChiselSyncParallel message, MessageContext ctx) {
        var world = Minecraft.getMinecraft().thePlayer.worldObj;
        if (world.getTileEntity(message.x, message.y, message.z) instanceof TileEntityMEChisel te) {
            te.setParallel(message.parallel);
        }
    }
}
