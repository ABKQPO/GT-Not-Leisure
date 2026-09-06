package com.science.gtnl.container;

import net.minecraft.entity.player.InventoryPlayer;

import com.science.gtnl.common.part.PartMECellDock;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotRestrictedInput;

public class ContainerMECellDock extends AEBaseContainer {

    public ContainerMECellDock(InventoryPlayer playerInventory, PartMECellDock dock) {
        super(playerInventory, dock);
        addSlotToContainer(
            new SlotRestrictedInput(
                SlotRestrictedInput.PlacableItemType.STORAGE_CELLS,
                dock.getCellInventory(),
                0,
                80,
                37,
                getInventoryPlayer()));
        bindPlayerInventory(playerInventory, 0, 84);
    }
}
