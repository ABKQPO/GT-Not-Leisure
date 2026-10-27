package com.science.gtnl.client.me;

import com.science.gtnl.common.me.processingPattern.ProcessingPatternCapacity;

import appeng.client.gui.slots.VirtualMEPhantomSlot;
import appeng.container.implementations.ContainerPatternTermEx;

public class ProcessingPatternSlotLayout {

    private ProcessingPatternSlotLayout() {}

    // Returns false when the opened capacity is off and AE2 lays the slots out itself.
    public static boolean update(ContainerPatternTermEx container, VirtualMEPhantomSlot[] craftingSlots,
        VirtualMEPhantomSlot[] outputSlots, int top, int inputX, int inputY, int outputX, int outputY) {
        if (!ProcessingPatternCapacity.isEnabled()) return false;
        int inputPages = container.getPatternInputPages();
        int outputPages = container.getPatternOutputPages();
        int activePage = Math.max(0, Math.min(inputPages - 1, container.activePageSync.get()));
        boolean inverted = container.invertedSync.get();
        layout(craftingSlots, !inverted, inputPages, activePage, inputX, top + inputY);
        layout(outputSlots, inverted, outputPages, activePage, inverted ? outputX : 112, top + outputY);
        return true;
    }

    public static void layout(VirtualMEPhantomSlot[] slots, boolean largeSide, int pages, int activePage, int offsetX,
        int offsetY) {
        int slotsPerPage = largeSide ? ProcessingPatternCapacity.SLOTS_PER_PAGE
            : ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE;
        int firstSlot = activePage * slotsPerPage;
        int endSlot = Math.min(firstSlot + slotsPerPage, pages * slotsPerPage);
        for (int index = 0; index < slots.length; index++) {
            VirtualMEPhantomSlot slot = slots[index];
            boolean hidden = index < firstSlot || index >= endSlot;
            slot.setHidden(hidden);
            if (!hidden) {
                int pageSlot = index - firstSlot;
                slot.setX(offsetX + (largeSide ? pageSlot % 4 : 0) * 18);
                slot.setY(offsetY + (largeSide ? pageSlot / 4 : pageSlot) * 18);
            }
        }
    }
}
