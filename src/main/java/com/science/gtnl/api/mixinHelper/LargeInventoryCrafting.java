package com.science.gtnl.api.mixinHelper;

/**
 * Carries an exact long-sized dispatch multiplier alongside Minecraft's int-sized crafting inventory stacks.
 */
public interface LargeInventoryCrafting {

    /**
     * Stores the multiplier selected before a compatible medium receives the crafting inventory.
     *
     * @param value positive dispatch craft count
     */
    void setAssemblerSize(long value);

    /**
     * Returns the exact multiplier that medium output logic must use instead of ItemStack.stackSize.
     *
     * @return positive dispatch craft count; ordinary crafting inventories default to one
     */
    long getAssemblerSize();
}
