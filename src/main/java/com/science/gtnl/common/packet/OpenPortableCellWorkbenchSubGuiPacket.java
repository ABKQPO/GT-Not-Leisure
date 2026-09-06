package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.common.item.cellworkbench.PortableCellWorkbenchHost;
import com.science.gtnl.common.item.cellworkbench.PortableCellWorkbenchSubGui;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.Upgrades;
import appeng.container.implementations.ContainerCellWorkbench;
import appeng.helpers.ICellRestriction;
import io.netty.buffer.ByteBuf;

public class OpenPortableCellWorkbenchSubGuiPacket extends ServerboundPacket {

    private PortableCellWorkbenchSubGui subGui;

    public OpenPortableCellWorkbenchSubGuiPacket() {}

    public OpenPortableCellWorkbenchSubGuiPacket(PortableCellWorkbenchSubGui subGui) {
        this.subGui = subGui;
    }

    @Override
    protected void read(ByteBuf buffer) {
        int ordinal = buffer.readUnsignedByte();
        PortableCellWorkbenchSubGui[] values = PortableCellWorkbenchSubGui.values();
        if (ordinal >= values.length) {
            throw new IllegalArgumentException("Unknown portable cell workbench sub-GUI");
        }
        subGui = values[ordinal];
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeByte(subGui.ordinal());
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        if (!(player.openContainer instanceof ContainerCellWorkbench container)
            || !(container.getTarget() instanceof PortableCellWorkbenchHost host)
            || !host.isValid()) {
            return;
        }

        if (subGui == PortableCellWorkbenchSubGui.ORE_FILTER && host.getInstalledUpgrades(Upgrades.ORE_FILTER) == 0) {
            return;
        }
        if (subGui == PortableCellWorkbenchSubGui.CELL_RESTRICTION && !(host.getCell() instanceof ICellRestriction)) {
            return;
        }

        GuiType guiType = subGui == PortableCellWorkbenchSubGui.ORE_FILTER ? GuiType.PortableCellWorkbenchOreFilterGUI
            : GuiType.PortableCellWorkbenchRestrictionGUI;
        CommonProxy.openGui(player, guiType, null, player.worldObj, host.getInventorySlot(), 0, 0);
    }
}
