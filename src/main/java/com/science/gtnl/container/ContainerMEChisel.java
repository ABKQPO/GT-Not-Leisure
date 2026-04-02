package com.science.gtnl.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotFake;

public class ContainerMEChisel extends AEBaseContainer {

    public TileEntityMEChisel tile;
    public SlotFake slotFake;

    public ContainerMEChisel(InventoryPlayer ip, TileEntityMEChisel myTile) {
        super(ip, myTile, null);
        this.tile = myTile;
        this.addSlotToContainer(slotFake = new SlotFake(myTile.getInternalInventory(), 0, 80, 37) {

            public void putStack(ItemStack is) {
                if (is != null) {
                    is = is.copy();
                    is.stackSize = 1;
                }

                super.putStack(is);
            }
        });
        slotFake.setContainer(this);
        this.bindPlayerInventory(ip, 0, 84);
    }

}
