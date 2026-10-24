package com.science.gtnl.common.yogsothoth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import appeng.util.Platform;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public final class YogTerminals {

    private static BiFunction<UUID, Integer, List<ItemStack>> clientStored;
    private static Function<EntityPlayer, ItemStack> clientOpened;

    public static void setClient(BiFunction<UUID, Integer, List<ItemStack>> stored,
        Function<EntityPlayer, ItemStack> opened) {
        clientStored = stored;
        clientOpened = opened;
    }

    public static List<ItemStack> stored(ItemStack yog) {
        UUID id = YogToken.id(yog);
        if (id == null) return Collections.emptyList();
        if (FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient())
            return clientStored == null ? Collections.emptyList() : clientStored.apply(id, YogToken.generation(yog));
        YogRecord record = YogTerminalStorage.get(id);
        if (record == null || record.generation != YogToken.generation(yog)) return Collections.emptyList();
        List<ItemStack> stored = new ArrayList<>(record.size());
        for (int index = 0; index < record.size(); index++) {
            if (index != record.active) stored.add(record.get(index));
        }
        return stored;
    }

    public static List<ItemStack> carried(EntityPlayer player) {
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
        if (player == null || baubles == null) return Collections.emptyList();
        List<ItemStack> carried = new ArrayList<>();
        for (YogBaublesView.Entry entry : YogBaublesView.entries(player, baubles)) {
            ItemStack stack = entry.get();
            if (stack != null) carried.add(stack);
        }
        return carried;
    }

    public static int slotOf(EntityPlayer player, ItemStack terminal) {
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
        if (player == null || baubles == null || terminal == null) return -1;
        List<? extends YogBaublesView.Entry> entries = YogBaublesView.entries(player, baubles);
        for (int index = 0; index < entries.size(); index++) {
            if (entries.get(index)
                .get() == terminal) return Platform.baublesSlotsOffset + baubles.getSizeInventory() + index;
        }
        return -1;
    }

    public static ItemStack opened(EntityPlayer player) {
        if (player == null) return null;
        if (!player.worldObj.isRemote) return YogTerminalService.opened(player);
        return clientOpened == null ? null : clientOpened.apply(player);
    }
}
