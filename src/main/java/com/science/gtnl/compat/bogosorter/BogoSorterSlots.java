package com.science.gtnl.compat.bogosorter;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import com.cleanroommc.bogosorter.api.IBogoSortAPI;
import com.cleanroommc.bogosorter.api.ISlotGroup;
import com.cleanroommc.bogosorter.common.sort.GuiSortingContext;
import com.cleanroommc.bogosorter.mixins.early.minecraft.SlotAccessor;
import com.gtnllib.mod.ModList;

import cpw.mods.fml.common.Optional;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class BogoSorterSlots {

    private BogoSorterSlots() {}

    public static List<Slot> slotGroup(EntityPlayerMP player, Container container, Slot source) {
        return ModList.InventoryBogoSorter.isModLoaded() ? groupOf(player, container, source) : List.of();
    }

    public static boolean isPinned(EntityPlayerMP player, Container container, Slot slot) {
        return ModList.InventoryBogoSorter.isModLoaded() && pinned(player, container, slot);
    }

    @Optional.Method(modid = "bogosorter")
    private static List<Slot> groupOf(EntityPlayerMP player, Container container, Slot source) {
        ISlotGroup group = GuiSortingContext.getOrCreate(container)
            .getSlotGroup(source.slotNumber);
        if (group == null) return List.of();
        List<Slot> slots = new ObjectArrayList<>();
        for (SlotAccessor entry : group.getSlots()) {
            int index = entry.getSlotNumber();
            if (index < 0 || index >= container.inventorySlots.size()) continue;
            Slot slot = container.getSlot(index);

            if (slot != source && pinned(player, container, slot)) continue;
            slots.add(slot);
        }
        return slots;
    }

    @Optional.Method(modid = "bogosorter")
    private static boolean pinned(EntityPlayerMP player, Container container, Slot slot) {
        return IBogoSortAPI.getInstance()
            .isPinned(player, container, slot);
    }
}
