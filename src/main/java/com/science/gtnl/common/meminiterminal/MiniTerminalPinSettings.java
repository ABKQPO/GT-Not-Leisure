package com.science.gtnl.common.meminiterminal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

import appeng.api.config.PinsRows;
import appeng.items.contents.PinList;

/** Exact mini-terminal slot counts backed by AE's fixed nine-slot rows. */
public record MiniTerminalPinSettings(int craftingCount, int playerCount) {

    public static final MiniTerminalPinSettings DISABLED = new MiniTerminalPinSettings(0, 0);
    private static final String TAG = "gtnlMiniTerminalPins";

    public MiniTerminalPinSettings {
        if (!validCounts(craftingCount, playerCount)) throw new IllegalArgumentException("Mini terminal pin counts");
    }

    public static boolean validCounts(int craftingCount, int playerCount) {
        return craftingCount >= 0 && craftingCount <= PinList.CRAFTING_SLOTS
            && playerCount >= 0
            && playerCount <= PinList.PLAYER_SLOTS;
    }

    public static MiniTerminalPinSettings read(ItemStack stack, EntityPlayer player, PinsRows craftingRows,
        PinsRows playerRows) {
        int crafting = craftingRows.getSlotCount();
        int personal = playerRows.getSlotCount();
        if (stack.hasTagCompound()) {
            NBTTagCompound settings = stack.getTagCompound()
                .getCompoundTag(TAG);
            String playerId = player.getPersistentID()
                .toString();
            if (settings.hasKey(playerId, NBT.TAG_COMPOUND)) {
                NBTTagCompound saved = settings.getCompoundTag(playerId);
                crafting = Math.max(0, Math.min(crafting, saved.getInteger("craftingCount")));
                personal = Math.max(0, Math.min(personal, saved.getInteger("playerCount")));
            }
        }
        return new MiniTerminalPinSettings(crafting, personal);
    }

    public void save(ItemStack stack, EntityPlayer player) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound settings = tag.getCompoundTag(TAG);
        NBTTagCompound saved = new NBTTagCompound();
        saved.setInteger("craftingCount", craftingCount);
        saved.setInteger("playerCount", playerCount);
        settings.setTag(
            player.getPersistentID()
                .toString(),
            saved);
        // Keep this outside AE's pins list, which PinsHolder rewrites when a pin changes.
        tag.setTag(TAG, settings);
    }

    public PinsRows craftingRows() {
        return PinsRows.fromOrdinal((craftingCount + 8) / 9);
    }

    public PinsRows playerRows() {
        return PinsRows.fromOrdinal((playerCount + 8) / 9);
    }
}
