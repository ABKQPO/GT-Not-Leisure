package com.science.gtnl.common.packet;

import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.container.ContainerDirePatternEncoder;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class DirePatternHandler implements IMessage, IMessageHandler<DirePatternHandler, IMessage> {

    private byte id = 0;
    private boolean isShift = false;
    private NBTTagCompound nbt = ContainerDirePatternEncoder.empty;

    public DirePatternHandler() {

    }

    public DirePatternHandler(byte id) {
        this.id = id;
    }

    public DirePatternHandler(byte id, boolean shift) {
        this.id = id;
        isShift = shift;
    }

    public DirePatternHandler(byte id, NBTTagCompound tag) {
        this.id = id;
        nbt = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
        isShift = buf.readBoolean();
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeBoolean(isShift);
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public IMessage onMessage(DirePatternHandler message, MessageContext ctx) {
        var player = ctx.getServerHandler().playerEntity;
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.openContainer instanceof ContainerDirePatternEncoder c) {
                switch (message.id) {
                    case 0 -> c.encode(message.isShift);
                    case 1 -> c.clear();
                    case 2 -> c.writeNEINBT(message.nbt);
                }
            }
        });
        return null;
    }
}
