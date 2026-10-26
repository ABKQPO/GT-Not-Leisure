package com.science.gtnl.common.wireless;

import static com.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnl.common.wireless.WirelessCopiedLinksContracts.allocate;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessCardInventory.TrackedCard;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

public class WirelessAutoConnectContracts {

    private static final WirelessCardBinding BINDING = new WirelessCardBinding(
        new Address(0, 100, 64, 100, ForgeDirection.UNKNOWN),
        UUID.randomUUID(),
        "review");

    public static void run() throws ReflectiveOperationException {
        nativeInventoryMoves();
        liveIdentity();
        invalidReplacements();
    }

    private static ItemStack card() {
        ItemStack card = new ItemStack(new Item());
        card.setTagCompound(new NBTTagCompound());
        BINDING.write(card.getTagCompound());
        WirelessCardBinding.toggleAutomatic(card);
        WirelessCardBinding.identify(card);
        return card;
    }

    private static void nativeInventoryMoves() throws ReflectiveOperationException {
        EntityPlayer player = allocate(EntityPlayerMP.class);
        player.inventory = new InventoryPlayer(player);
        TestContainer container = new TestContainer(player);
        for (int mode : new int[] { 0, 1, 2 }) {
            player.inventory.clearInventory(null, -1);
            ItemStack original = card();
            TrackedCard tracked = new TrackedCard(original, BINDING);
            player.inventory.mainInventory[10] = original;
            container.slotClick(10, 0, mode, player);
            if (mode == 0) container.slotClick(4, 0, 0, player);
            ItemStack moved = player.inventory.mainInventory[mode == 0 ? 4 : mode == 1 ? 35 : 0];
            check(moved != original && moved.stackSize == 1, "vanilla inventory movement replaces the stack: " + mode);
            check(mode == 2 || original.stackSize == 0, "cursor and shift moves empty the old object");
            check(
                tracked.present(WirelessCardInventory.collect(player.inventory.mainInventory, null)),
                "native cursor, shift-click and number-key moves preserve identity: " + mode);
            WirelessCardBinding.toggleAutomatic(moved);
            check(
                !tracked.present(WirelessCardInventory.collect(player.inventory.mainInventory, null)),
                "changes to the adopted live card cancel the task");
        }
    }

    private static void liveIdentity() {
        ItemStack original = card();
        UUID identity = WirelessCardBinding.identity(original.getTagCompound());
        check(identity != null && !WirelessCardBinding.identify(original), "identity is assigned once");
        TrackedCard tracked = new TrackedCard(original, BINDING);
        ItemStack live = original.copy();
        live.setStackDisplayName("renamed");
        check(tracked.present(List.of(live)), "presentation changes do not affect identity");
        InventoryBasic worn = new InventoryBasic("review", false, 4);
        worn.setInventorySlotContents(1, live.copy());
        check(
            tracked.present(WirelessCardInventory.collect(new ItemStack[36], worn)),
            "copies moving into Baubles retain identity");
        WirelessCardBinding.clear(
            worn.getStackInSlot(1)
                .getTagCompound());
        check(!tracked.present(WirelessCardInventory.collect(new ItemStack[36], worn)), "clearing a binding cancels");
        check(
            identity.equals(
                WirelessCardBinding.identity(
                    worn.getStackInSlot(1)
                        .getTagCompound())),
            "rebinding does not change item identity");
        check(
            !identity.equals(WirelessCardBinding.identity(card().getTagCompound())),
            "independent cards have distinct identities");
        check(!new TrackedCard(original, BINDING).present(List.of()), "removed and cursor-only cards do not count");

        ItemStack duplicate = original.copy();
        WirelessCardBinding.toggleAutomatic(original);
        check(
            !new TrackedCard(original, BINDING).present(List.of(duplicate, original)),
            "a carried original takes priority over its enabled duplicate");
    }

    private static void invalidReplacements() {
        ItemStack original = card();
        check(
            !new TrackedCard(original, BINDING).present(List.of(original.copy(), original.copy())),
            "ambiguous duplicated identities cannot replace an absent original");
        for (int variant = 0; variant < 7; variant++) {
            ItemStack replacement = original.copy();
            switch (variant) {
                case 0 -> replacement = card();
                case 1 -> {
                    replacement = new ItemStack(new Item());
                    replacement.setTagCompound(
                        (NBTTagCompound) original.getTagCompound()
                            .copy());
                }
                case 2 -> replacement.setItemDamage(1);
                case 3 -> new WirelessCardBinding(BINDING.source(), UUID.randomUUID(), "other")
                    .write(replacement.getTagCompound());
                case 4 -> new WirelessCardBinding(
                    new Address(0, 200, 64, 100, ForgeDirection.UNKNOWN),
                    BINDING.owner(),
                    "review").write(replacement.getTagCompound());
                case 5 -> WirelessCardBinding.toggleAutomatic(replacement);
                case 6 -> replacement.stackSize = 0;
            }
            check(
                !new TrackedCard(original, BINDING).present(List.of(replacement)),
                "reject changed identity, item, damage, owner, frequency, toggle or empty stack: " + variant);
        }
    }

    private static class TestContainer extends Container {

        TestContainer(EntityPlayer player) {
            for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
                addSlotToContainer(new Slot(player.inventory, slot, 0, 0));
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int index) {
            Slot slot = (Slot) inventorySlots.get(index);
            ItemStack stack = slot.getStack();
            if (stack == null || index < 9) return null;
            ItemStack previous = stack.copy();
            if (!mergeItemStack(stack, 27, 36, true)) return null;
            if (stack.stackSize == 0) slot.putStack(null);
            return previous;
        }
    }
}
