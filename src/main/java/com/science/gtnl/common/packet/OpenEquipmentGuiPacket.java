package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.equipment.IEquipmentItem;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import io.netty.buffer.ByteBuf;

public class OpenEquipmentGuiPacket extends ServerboundPacket {

    private int slot;

    public OpenEquipmentGuiPacket() {}

    public OpenEquipmentGuiPacket(int slot) {
        this.slot = slot;
    }

    @Override
    protected void read(ByteBuf buffer) {
        slot = buffer.readUnsignedByte();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeByte(slot);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.isDead || player.playerNetServerHandler == null
                || slot >= player.inventory.getSizeInventory()
                || player.isUsingItem()) return;
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack == null || !(stack.getItem() instanceof IEquipmentItem equipment)
                || EquipmentNet.guiThrottled(player)) return;
            equipment.beforeSettingsOpen(player, stack);
            if (player.openContainer != player.inventoryContainer) player.closeScreen();
            GuiFactories.playerInventory()
                .openFromPlayerInventory(player, slot);
        });
    }
}
