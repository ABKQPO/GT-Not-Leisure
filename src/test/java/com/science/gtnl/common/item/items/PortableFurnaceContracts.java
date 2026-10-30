package com.science.gtnl.common.item.items;

import static com.gtnltest.ContractAssertions.check;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PortableFurnaceContracts {

    public static void run() {
        NBTTagCompound idle = idleTag();
        check(PortableItem.isIdleFurnace(idle), "a cold furnace without input skips the tick");

        ItemStack stack = new ItemStack((Item) null, 1, 0);
        stack.setTagCompound((NBTTagCompound) idle.copy());
        PortableItem.saveFurnaceInventory.save(stack, PortableItem.getFurnaceInventory(stack));
        check(idle.equals(stack.getTagCompound()), "the skipped tick would have written back the NBT it read");

        check(!PortableItem.isIdleFurnace(null), "a furnace without NBT takes the full tick to create it");
        NBTTagCompound noItems = idleTag();
        noItems.removeTag("Items");
        check(!PortableItem.isIdleFurnace(noItems), "a missing item list is written by the full tick");
        for (String key : new String[] { "BurnTime", "CookTime", "CurrentItemBurnTime" }) {
            NBTTagCompound burning = idleTag();
            burning.setInteger(key, 1);
            check(!PortableItem.isIdleFurnace(burning), key + " above zero takes the full tick");
            NBTTagCompound missing = idleTag();
            missing.removeTag(key);
            check(!PortableItem.isIdleFurnace(missing), "a missing " + key + " takes the full tick");
        }
        NBTTagCompound withInput = idleTag();
        NBTTagCompound input = new NBTTagCompound();
        input.setByte("Slot", (byte) 0);
        withInput.getTagList("Items", 10)
            .appendTag(input);
        check(!PortableItem.isIdleFurnace(withInput), "anything in the input slot takes the full tick");
    }

    private static NBTTagCompound idleTag() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("BurnTime", 0);
        tag.setInteger("CookTime", 0);
        tag.setInteger("CurrentItemBurnTime", 0);
        tag.setTag("Items", new NBTTagList());
        return tag;
    }
}
