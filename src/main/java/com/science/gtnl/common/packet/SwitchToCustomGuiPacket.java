package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.utils.enums.GuiType;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import io.netty.buffer.ByteBuf;

public class SwitchToCustomGuiPacket extends ServerboundPacket {

    private int guiID;
    private int sideOrdinal;

    public SwitchToCustomGuiPacket() {}

    public SwitchToCustomGuiPacket(GuiType guiType, ForgeDirection side) {
        this.guiID = guiType.ordinal();
        this.sideOrdinal = side.ordinal();
    }

    @Override
    protected void read(ByteBuf buf) {
        this.guiID = buf.readInt();
        this.sideOrdinal = buf.readInt();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(this.guiID);
        buf.writeInt(this.sideOrdinal);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        GuiType[] guiTypes = GuiType.values();
        if (guiID < 0 || guiID >= guiTypes.length) {
            return;
        }

        ForgeDirection[] directions = ForgeDirection.values();
        if (sideOrdinal < 0 || sideOrdinal >= directions.length) {
            return;
        }

        Container c = player.openContainer;

        if (c instanceof AEBaseContainer bc) {
            ContainerOpenContext context = bc.getOpenContext();
            if (context != null) {
                TileEntity te = context.getTile();
                if (te != null) {
                    CommonProxy.openGui(
                        player,
                        guiTypes[guiID],
                        directions[sideOrdinal],
                        te.getWorldObj(),
                        te.xCoord,
                        te.yCoord,
                        te.zCoord);
                }
            }
        }
    }
}
