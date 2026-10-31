package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.portalgun.PortalGunData;
import com.science.gtnl.common.portalgun.PortalGunDestination;

import io.netty.buffer.ByteBuf;

public class PortalGunSelectPacket extends ServerboundPacket {

    private int slot;
    private boolean stayOpen, clicked;

    public PortalGunSelectPacket() {}

    public PortalGunSelectPacket(int slot, boolean stayOpen, boolean clicked) {
        this.slot = slot;
        this.stayOpen = stayOpen;
        this.clicked = clicked;
    }

    @Override
    protected void read(ByteBuf buffer) {
        slot = buffer.readByte();
        stayOpen = buffer.readBoolean();
        clicked = buffer.readBoolean();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeByte(slot);
        buffer.writeBoolean(stayOpen);
        buffer.writeBoolean(clicked);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            ItemStack gun = PortalGun.held(player);
            if (gun == null) return;
            PortalGunData.select(gun, slot, stayOpen);
            if (!clicked || !PortalGunData.validSlot(slot)) return;
            PortalGunDestination here = PortalGunDestination.of(player, "");
            if (PortalGunData.fillIfEmpty(gun, slot, here)) PortalGun.saved(player, slot, here);
            else((PortalGun) gun.getItem()).fire(gun, player.worldObj, player, false);
        });
    }
}
