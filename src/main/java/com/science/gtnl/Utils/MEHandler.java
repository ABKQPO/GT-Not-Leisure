package com.science.gtnl.Utils;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.features.IWirelessTermRegistry;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.tile.misc.TileSecurity;
import appeng.util.Platform;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class MEHandler {

    @Optional.Method(modid = "appliedenergistics2")
    public static WirelessTerminalGuiObject getTerminalGuiObject(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler t && t.canHandle(item)) {
                return getTerminalGuiObject(item,player,i,0);
            }
        }

        if (Loader.isModLoaded("Baubles")) {
            return readBaubles(player);
        }
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    public static WirelessTerminalGuiObject getTerminalGuiObject(InventoryPlayer player) {
        for (int i = 0; i < player.getSizeInventory(); i++) {
            ItemStack item = player.getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler t && t.canHandle(item)) {
                return getTerminalGuiObject(item,player.player,i,0);
            }
        }

        if (Loader.isModLoaded("Baubles")) {
            return readBaubles(player.player);
        }
        return null;
    }

    @Optional.Method(modid = "Baubles")
    public static WirelessTerminalGuiObject readBaubles(EntityPlayer player) {
        for (int i = 0; i < BaublesApi.getBaubles(player).getSizeInventory(); i++) {
            ItemStack item = BaublesApi.getBaubles(player).getStackInSlot(i);
            if (item.getItem() instanceof IWirelessTermHandler t && t.canHandle(item)) {
                return getTerminalGuiObject(item,player,i,1);
            }
        }
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    public static WirelessTerminalGuiObject getTerminalGuiObject(ItemStack item, EntityPlayer player, int x,int y) {
        if (Platform.isClient() || item == null)return null;
        if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
            IWirelessTermRegistry registry = AEApi.instance().registries().wireless();
            if (!registry.isWirelessTerminal(item)) {
                player.addChatMessage(PlayerMessages.DeviceNotWirelessTerminal.toChat());
                return null;
            }
            IWirelessTermHandler handler = registry.getWirelessTerminalHandler(item);
            String unparsedKey = handler.getEncryptionKey(item);
            if (unparsedKey.isEmpty()) {
                player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                return null;
            }
            long parsedKey = Long.parseLong(unparsedKey);
            ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
            if (securityStation instanceof TileSecurity t) {
                if (!handler.hasPower(player, 1000F, item)) {
                    player.addChatMessage(PlayerMessages.DeviceNotPowered.toChat());
                    return null;
                }
                return new WirelessTerminalGuiObject(wt, item, player, player.worldObj, x,y,Integer.MIN_VALUE);
            }
        }
        return null;
    }
}
