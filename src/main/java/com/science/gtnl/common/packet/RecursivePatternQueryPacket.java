package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.me.cache.CraftingGridCache;
import io.netty.buffer.ByteBuf;

/**
 * Asks for the crafting cycles of the request the player is confirming: by the player, to be shown, or by the screen
 * once the calculation is done, to mark them in the crafting tree.
 */
public class RecursivePatternQueryPacket extends ServerboundPacket {

    private boolean manual;

    public RecursivePatternQueryPacket() {}

    public RecursivePatternQueryPacket(boolean manual) {
        this.manual = manual;
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeBoolean(this.manual);
    }

    @Override
    protected void read(ByteBuf buf) {
        this.manual = buf.readBoolean();
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        if (!(player.openContainer instanceof ContainerCraftConfirm container)) return;
        var request = container.getItemToCraft();
        IGrid grid;
        try {
            grid = container.getGrid();
        } catch (RuntimeException e) {
            grid = null;
        }
        if (request == null || grid == null
            || !(grid.getCache(ICraftingGrid.class) instanceof CraftingGridCache crafting)) {
            return;
        }
        var cycles = RecursivePatternFinder.find(crafting, request, player.worldObj);
        ScienceNotLeisure.network.sendTo(new RecursivePatternResultPacket(request, cycles, this.manual), player);
    }
}
