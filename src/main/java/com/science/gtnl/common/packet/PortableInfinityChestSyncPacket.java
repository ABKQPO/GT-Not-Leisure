package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.container.portableWorkbench.ContainerPortableInfinityChest;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PortableInfinityChestSyncPacket extends ClientboundPacket {

    private ItemStack itemStack;
    private int slot;
    private int stackSize;

    public PortableInfinityChestSyncPacket() {}

    public PortableInfinityChestSyncPacket(final ItemStack itemStack, final int slot) {
        if (itemStack != null) this.stackSize = (this.itemStack = itemStack).stackSize;
        else this.itemStack = null;
        this.slot = slot;
    }

    @Override
    protected void read(final ByteBuf buf) {
        itemStack = ByteBufUtils.readItemStack(buf);
        slot = ByteBufUtils.readVarShort(buf);
        stackSize = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    protected void write(final ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, itemStack);
        ByteBufUtils.writeVarShort(buf, slot);
        ByteBufUtils.writeVarInt(buf, stackSize, 5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        EntityPlayer entityPlayer = minecraft.thePlayer;
        if (entityPlayer.openContainer instanceof ContainerPortableInfinityChest container)
            container.syncData(itemStack, slot, stackSize);
    }
}
