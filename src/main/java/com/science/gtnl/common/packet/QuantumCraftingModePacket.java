package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingMode;

import appeng.api.networking.IGrid;
import appeng.container.implementations.ContainerCraftAmount;
import io.netty.buffer.ByteBuf;

public class QuantumCraftingModePacket extends ServerboundPacket {

    private boolean enabled;

    public QuantumCraftingModePacket() {}

    public QuantumCraftingModePacket(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void read(ByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeBoolean(this.enabled);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        QuantumCraftingMode.setEnabled(player, this.enabled);
        if (!(player.openContainer instanceof ContainerCraftAmount container)) return;

        IGrid grid;
        try {
            grid = container.getGrid();
        } catch (RuntimeException e) {
            grid = null;
        }
        ScienceNotLeisure.network
            .sendTo(new QuantumCraftingAvailabilityPacket(QuantumCraftingMode.isAvailable(grid)), player);
    }
}
