package com.science.gtnl.common.topology;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.item.ItemStackNBT;

/**
 * View preferences that travel with the tool rather than with the session.
 * <p>
 * They live under their own tag, separate from the binding, so clearing the bound network never resets how the viewer
 * is laid out. Both sides read the same stack, so the client can seed its state from the item it is already holding
 * without waiting for a round trip.
 */
public class TopologyPreferences {

    private static final String KEY = "gtnlTopologyView";
    private static final String CONSOLE = "console";

    private TopologyPreferences() {}

    public static boolean consoleOpen(ItemStack stack) {
        NBTTagCompound tag = stack == null ? null : stack.getTagCompound();
        if (tag == null || !tag.hasKey(KEY)) return true;
        NBTTagCompound view = tag.getCompoundTag(KEY);
        return !view.hasKey(CONSOLE) || view.getBoolean(CONSOLE);
    }

    public static void consoleOpen(ItemStack stack, boolean open) {
        if (stack == null) return;
        NBTTagCompound tag = ItemStackNBT.get(stack);
        NBTTagCompound view = tag.hasKey(KEY) ? tag.getCompoundTag(KEY) : new NBTTagCompound();
        view.setBoolean(CONSOLE, open);
        tag.setTag(KEY, view);
    }
}
