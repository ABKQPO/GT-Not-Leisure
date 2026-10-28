package com.science.gtnl.client.gui;

import net.minecraft.item.ItemStack;

public interface IBookmarkGroupDropTarget {

    int handleBookmarkGroupDrop(int mouseX, int mouseY, Iterable<ItemStack> stacks);
}
