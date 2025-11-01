package com.science.gtnl.utils.gui.portableWorkbench;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.cleanroommc.bogosorter.api.IPosSetter;
import com.cleanroommc.bogosorter.api.ISortableContainer;
import com.cleanroommc.bogosorter.api.ISortingContextBuilder;
import com.science.gtnl.common.item.items.PortableItem;

import cpw.mods.fml.common.Optional;

@Optional.Interface(iface = "com.cleanroommc.bogosorter.api.ISortableContainer", modid = "bogosorter")
public class ContainerPortableChest extends Container implements ISortableContainer {

    public IInventory chestInventory;
    public int meta;
    public int rows;
    public int cols;
    public ItemStack itemStack;
    public final String portableID;
    private final PortableItem.PortableType type;

    public ContainerPortableChest(InventoryPlayer playerInventory, ItemStack stack, GuiPortableChest.GUI type) {
        this(playerInventory, stack, type.xSize, type.ySize, type.rows, type.cols);
    }

    public ContainerPortableChest(InventoryPlayer playerInventory, ItemStack stack, int xSize, int ySize, int rows,
        int cols) {
        this.rows = rows;
        this.cols = cols;
        this.chestInventory = new InventoryBasic("PortableChest", false, rows * cols);
        this.meta = stack.getItemDamage();
        this.itemStack = stack;

        this.portableID = PortableItem.ensurePortableID(stack);
        this.type = PortableItem.getPortableType(stack);
        IInventory saved = type.getInventory(stack);
        for (int i = 0; i < rows * cols; i++) {
            chestInventory.setInventorySlotContents(i, saved.getStackInSlot(i));
        }

        for (int chestRow = 0; chestRow < cols; chestRow++) {
            for (int chestCol = 0; chestCol < rows; chestCol++) {
                final int index = chestCol + chestRow * rows;
                this.addSlotToContainer(new Slot(chestInventory, index, 12 + chestCol * 18, 8 + chestRow * 18) {

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        if (stack != null && stack.getItem() instanceof PortableItem
                            && (stack.getItemDamage() >= 6 && stack.getItemDamage() <= 17)) {
                            return false;
                        }
                        return super.isItemValid(stack);
                    }
                });
            }
        }

        int leftCol = (xSize - 162) / 2 + 1;
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                this.addSlotToContainer(
                    new Slot(
                        playerInventory,
                        playerInvCol + playerInvRow * 9 + 9,
                        leftCol + playerInvCol * 18,
                        ySize - (4 - playerInvRow) * 18 - 10));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlotToContainer(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
        }
    }

    @Override
    public void buildSortingContext(ISortingContextBuilder builder) {
        builder.addSlotGroup(0, rows * cols, rows)
            .buttonPosSetter(IPosSetter.TOP_RIGHT_VERTICAL);
    }

    @Override
    public @Nullable IPosSetter getPlayerButtonPosSetter() {
        return IPosSetter.TOP_RIGHT_VERTICAL;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        return PortableItem.matchesPortableID(held, portableID);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        ItemStack held = player.getHeldItem();
        if (PortableItem.matchesPortableID(held, portableID)) {
            type.saveInventory(held, chestInventory);
            itemStack = held;
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
        ItemStack result = super.slotClick(slotId, clickedButton, mode, player);
        ItemStack held = player.getHeldItem();
        if (PortableItem.matchesPortableID(held, portableID)) {
            type.saveInventory(held, chestInventory);
            itemStack = held;
        }
        return result;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int chestSize = chestInventory.getSizeInventory();

            if (index < chestSize) {
                if (!this.mergeItemStack(itemstack1, chestSize, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, chestSize, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        ItemStack held = player.getHeldItem();
        if (PortableItem.matchesPortableID(held, portableID)) {
            type.saveInventory(held, chestInventory);
            itemStack = held;
        }
        return itemstack;
    }
}
