package com.science.gtnl.client.celestweave;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.client.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableEnderChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortablePortableCompressedChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortablePortableInfinityChest;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.container.portableWorkbench.IPortableStorageHost;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;

@SideOnly(Side.CLIENT)
public class CelestweaveBackpackClient {

    private CelestweaveBackpackClient() {}

    public static void registerTabs() {
        if (Mods.TinkerConstruct.isModLoaded()) CelestweaveBackpackTicCompat.register();
    }

    public static void addTabs(int left, int top, List<?> buttons) {
        if (Mods.TinkerConstruct.isModLoaded()) CelestweaveBackpackTicCompat.add(left, top, buttons);
    }

    public static Object gui(EntityPlayer player) {
        ItemStack worn = CelestweaveBackpack.wornBackpack(player);
        if (worn == null) return null;
        ItemStack held = worn.copy();
        PortableItem.PortableType type = PortableItem.getPortableType(held);
        IPortableStorageHost host = CelestweaveBackpack.clientHost();
        return switch (type) {
            case ENDERCHEST -> new GuiPortableEnderChest(player.inventory, player.getInventoryEnderChest(), true);
            case COMPRESSEDCHEST -> new GuiPortablePortableCompressedChest(held, player.inventory, host);
            case INFINITYCHEST -> new GuiPortablePortableInfinityChest(held, player.inventory, host);
            default -> new GuiPortableChest(CelestweaveBackpack.chestType(type), player.inventory, held, host);
        };
    }
}
