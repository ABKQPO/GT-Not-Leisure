package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.gui.container.ContainerDirePatternEncoder;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class DirePatternHandler extends ServerboundPacket {

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
    protected void read(ByteBuf buf) {
        id = buf.readByte();
        isShift = buf.readBoolean();
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeBoolean(isShift);
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.openContainer instanceof ContainerDirePatternEncoder c) {
                switch (id) {
                    case 0 -> c.encode(isShift);
                    case 1 -> c.clear();
                    case 2 -> c.writeNEINBT(nbt);
                }
            }
        });
    }
}
