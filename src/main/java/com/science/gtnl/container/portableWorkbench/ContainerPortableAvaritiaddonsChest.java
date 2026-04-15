package com.science.gtnl.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.bogosorter.api.IPosSetter;
import com.cleanroommc.bogosorter.api.ISortableContainer;
import com.cleanroommc.bogosorter.api.ISortingContextBuilder;
import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.utils.SlotInfinity;

import cpw.mods.fml.common.Optional;

@Optional.Interface(iface = "com.cleanroommc.bogosorter.api.ISortableContainer", modid = "bogosorter")
public class ContainerPortableAvaritiaddonsChest extends Container implements ISortableContainer {

    public final PortableItem.PortableType type;
    public IInventory chestInventory;
    public ItemStack itemStack;
    public final String portableID;
    public boolean isInfinity;

    public ContainerPortableAvaritiaddonsChest(ItemStack stack, InventoryPlayer playerInv, boolean isInfinity) {
        type = PortableItem.getPortableType(stack);
        chestInventory = type.getInventory(stack);
        itemStack = stack;
        this.isInfinity = isInfinity;

        this.portableID = PortableItem.ensurePortableID(stack);
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 27; x++) {
                addSlotToContainer(getSlot(isInfinity, y, x));
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(playerInv, 9 + y * 9 + x, 170 + (18 * x), 194 + (18 * y)));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(playerInv, i, 170 + (18 * i), 252));
        }
    }

    public @NotNull Slot getSlot(boolean isInfinity, int y, int x) {
        if (isInfinity) {
            return new SlotInfinity(chestInventory, y * 27 + x, 8 + (18 * x), 18 + (18 * y)) {

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack != null && stack.getItem() instanceof PortableItem
                        && (stack.getItemDamage() >= 6 && stack.getItemDamage() <= 17)) {
                        return false;
                    }
                    return super.isItemValid(stack);
                }
            };
        } else {
            return new Slot(chestInventory, y * 27 + x, 8 + (18 * x), 18 + (18 * y)) {

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack != null && stack.getItem() instanceof PortableItem
                        && (stack.getItemDamage() >= 6 && stack.getItemDamage() <= 17)) {
                        return false;
                    }
                    return super.isItemValid(stack);
                }
            };
        }
    }

    @Override
    public void buildSortingContext(ISortingContextBuilder builder) {
        builder.addSlotGroup(0, 243, 27)
            .buttonPosSetter(IPosSetter.TOP_RIGHT_VERTICAL);
    }

    @Override
    public @Nullable IPosSetter getPlayerButtonPosSetter() {
        return IPosSetter.TOP_RIGHT_VERTICAL;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (isMatchingPortableItem(itemstack, portableID)) {
                return null;
            }

            if (index < 243) {
                if (!this.mergeItemStack(stackInSlot, 243, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, 243, false)) {
                return null;
            }

            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof PortableItem) {
            if (PortableItem.getPortableType(held) == type) {
                type.saveInventory(held, this.chestInventory);
                itemStack = held;
            }
        }
        return itemstack;
    }

    public boolean isMatchingPortableItem(ItemStack itemStack, String portableID) {
        return itemStack != null && itemStack.getItem() instanceof PortableItem
            && (itemStack.getItemDamage() == 6 || itemStack.getItemDamage() == 7);
    }

}
