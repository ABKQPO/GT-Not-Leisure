package com.science.gtnl.common.topology.probe;

import net.minecraft.item.ItemStack;

import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.PartItemStack;

public class ProbeSupport {

    private ProbeSupport() {}

    public static ItemStack icon(IPart part) {
        return strip(part.getItemStack(PartItemStack.Network));
    }

    public static ItemStack icon(IGridNode node) {
        return strip(
            node.getGridBlock()
                .getMachineRepresentation());
    }

    private static ItemStack strip(ItemStack stack) {
        return stack == null || stack.getItem() == null ? null
            : new ItemStack(stack.getItem(), 1, stack.getItemDamage());
    }
}
