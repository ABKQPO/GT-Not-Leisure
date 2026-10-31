package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.portalgun.PortalGunData;
import com.science.gtnl.common.portalgun.PortalGunDestination;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class PortalGunFavoritePacket extends ServerboundPacket {

    public enum Change {
        ADD,
        REMOVE,
        EDIT
    }

    private int slot;
    private Change change = Change.ADD;
    private String name = "";
    private ItemId icon;
    private double x, y, z;

    public PortalGunFavoritePacket() {}

    public PortalGunFavoritePacket(int slot, Change change) {
        this(slot, change, "", 0, 0, 0, null);
    }

    public PortalGunFavoritePacket(int slot, Change change, String name, double x, double y, double z, ItemId icon) {
        this.slot = slot;
        this.change = change;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.icon = icon;
    }

    @Override
    protected void read(ByteBuf buffer) {
        slot = buffer.readByte();
        int ordinal = buffer.readByte();
        change = ordinal >= 0 && ordinal < Change.values().length ? Change.values()[ordinal] : null;
        name = PortalGunData.cleanName(ByteBufUtils.readUTF8String(buffer));
        x = buffer.readDouble();
        y = buffer.readDouble();
        z = buffer.readDouble();
        ItemStack shown = ByteBufUtils.readItemStack(buffer);
        icon = shown == null || shown.getItem() == null ? null : ItemId.createWithoutNBT(shown);
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeByte(slot);
        buffer.writeByte(change.ordinal());
        ByteBufUtils.writeUTF8String(buffer, PortalGunData.cleanName(name));
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        ByteBufUtils.writeItemStack(buffer, icon == null ? null : icon.toStack());
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            ItemStack gun = PortalGun.held(player);
            if (gun == null || change == null) return;
            switch (change) {
                case ADD -> {
                    PortalGunDestination here = PortalGunData.addFavorite(gun, slot, player);
                    if (here != null) PortalGun.saved(player, slot, here);
                }
                case REMOVE -> PortalGunData.removeFavorite(gun, slot);
                case EDIT -> PortalGunData.editFavorite(gun, slot, name, x, y, z, icon);
            }
        });
    }
}
