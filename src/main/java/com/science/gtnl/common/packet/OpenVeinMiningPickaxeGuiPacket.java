package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.VeinMiningPickaxe;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import gregtech.crossmod.backhand.Backhand;
import io.netty.buffer.ByteBuf;

public class OpenVeinMiningPickaxeGuiPacket extends ServerboundPacket {

    private boolean offhand;

    public OpenVeinMiningPickaxeGuiPacket() {}

    public OpenVeinMiningPickaxeGuiPacket(boolean offhand) {
        this.offhand = offhand;
    }

    @Override
    public void read(ByteBuf buffer) {
        offhand = buffer.readBoolean();
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeBoolean(offhand);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            ItemStack held = offhand ? Backhand.getOffhandItem(player) : player.getCurrentEquippedItem();
            if (held != null && held.getItem() instanceof VeinMiningPickaxe) {
                if (offhand) {
                    GuiFactories.playerInventory()
                        .openFromPlayerInventory(player, Backhand.getOffhandSlot(player));
                } else {
                    GuiFactories.playerInventory()
                        .openFromMainHand(player);
                }
            }
        });
    }
}
