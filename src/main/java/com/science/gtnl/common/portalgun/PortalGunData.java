package com.science.gtnl.common.portalgun;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.common.equipment.EquipmentTag;

public class PortalGunData {

    public static final int MAX_FAVORITES = 12, MAX_NAME_LENGTH = 15, MAX_HORIZONTAL = 30_000_000, MIN_HEIGHT = 0,
        MAX_HEIGHT = 255;
    private static final String ROOT = "PortalGun", FAVORITES = "Favorites", SELECTED = "Selected",
        PREVIOUS = "Previous", SLOT = "Slot", STAY_OPEN = "StayOpen", GUN_ID_MOST = "GunIdMost",
        GUN_ID_LEAST = "GunIdLeast";

    public static NBTTagCompound root(ItemStack stack) {
        return EquipmentTag.child(stack, ROOT);
    }

    public static NBTTagCompound rootForWrite(ItemStack stack) {
        return EquipmentTag.childForWrite(stack, ROOT);
    }

    public static UUID gunId(ItemStack stack) {
        NBTTagCompound root = rootForWrite(stack);
        if (!root.hasKey(GUN_ID_MOST, Constants.NBT.TAG_LONG)) {
            UUID id = UUID.randomUUID();
            root.setLong(GUN_ID_MOST, id.getMostSignificantBits());
            root.setLong(GUN_ID_LEAST, id.getLeastSignificantBits());
        }
        return new UUID(root.getLong(GUN_ID_MOST), root.getLong(GUN_ID_LEAST));
    }

    public static int selectedSlot(ItemStack stack) {
        int slot = root(stack).getByte(SELECTED);
        return validSlot(slot) ? slot : 0;
    }

    public static boolean stayOpen(ItemStack stack) {
        return root(stack).getBoolean(STAY_OPEN);
    }

    public static void select(ItemStack stack, int slot, boolean stayOpen) {
        if (!validSlot(slot)) return;
        NBTTagCompound root = rootForWrite(stack);
        root.setByte(SELECTED, (byte) slot);
        root.setBoolean(STAY_OPEN, stayOpen);
    }

    public static boolean validSlot(int slot) {
        return slot >= 0 && slot < MAX_FAVORITES;
    }

    public static String cleanName(String name) {
        String trimmed = name == null ? "" : name.trim();
        return trimmed.length() > MAX_NAME_LENGTH ? trimmed.substring(0, MAX_NAME_LENGTH) : trimmed;
    }

    public static PortalGunDestination addFavorite(ItemStack stack, int slot, EntityPlayer player) {
        if (!validSlot(slot)) return null;
        PortalGunDestination here = PortalGunDestination.of(player, "");
        setFavorite(stack, slot, here);
        return here;
    }

    public static boolean fillIfEmpty(ItemStack stack, int slot, PortalGunDestination here) {
        if (!validSlot(slot) || favorite(stack, slot) != null) return false;
        setFavorite(stack, slot, here);
        return true;
    }

    public static boolean editFavorite(ItemStack stack, int slot, String name, double x, double y, double z,
        ItemId icon) {
        PortalGunDestination old = validSlot(slot) ? favorite(stack, slot) : null;
        if (old == null) return false;
        setFavorite(
            stack,
            slot,
            old.edited(
                cleanName(name),
                inside(x, old.x(), -MAX_HORIZONTAL, MAX_HORIZONTAL),
                inside(y, old.y(), MIN_HEIGHT, MAX_HEIGHT),
                inside(z, old.z(), -MAX_HORIZONTAL, MAX_HORIZONTAL),
                icon == null || icon.getItem() == null ? null : icon));
        return true;
    }

    private static double inside(double value, double old, double low, double high) {
        return Double.isFinite(value) ? Math.max(low, Math.min(high, value)) : old;
    }

    public static void removeFavorite(ItemStack stack, int slot) {
        if (!validSlot(slot) || !root(stack).hasKey(FAVORITES, Constants.NBT.TAG_LIST)) return;
        NBTTagCompound root = rootForWrite(stack);
        root.setTag(FAVORITES, without(root.getTagList(FAVORITES, Constants.NBT.TAG_COMPOUND), slot));
    }

    public static PortalGunDestination favorite(ItemStack stack, int slot) {
        NBTTagList list = root(stack).getTagList(FAVORITES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            if (entry.getByte(SLOT) == slot) return PortalGunDestination.read(entry);
        }
        return null;
    }

    public static void setFavorite(ItemStack stack, int slot, PortalGunDestination destination) {
        NBTTagCompound root = rootForWrite(stack);
        NBTTagList list = without(root.getTagList(FAVORITES, Constants.NBT.TAG_COMPOUND), slot);
        NBTTagCompound entry = destination.write();
        entry.setByte(SLOT, (byte) slot);
        list.appendTag(entry);
        root.setTag(FAVORITES, list);
    }

    public static PortalGunDestination previous(ItemStack stack) {
        NBTTagCompound root = root(stack);
        return root.hasKey(PREVIOUS, Constants.NBT.TAG_COMPOUND)
            ? PortalGunDestination.read(root.getCompoundTag(PREVIOUS))
            : null;
    }

    public static void setPrevious(ItemStack stack, PortalGunDestination destination) {
        rootForWrite(stack).setTag(PREVIOUS, destination.write());
    }

    private static NBTTagList without(NBTTagList old, int slot) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < old.tagCount(); i++) {
            if (old.getCompoundTagAt(i)
                .getByte(SLOT) != slot) list.appendTag(old.getCompoundTagAt(i));
        }
        return list;
    }
}
