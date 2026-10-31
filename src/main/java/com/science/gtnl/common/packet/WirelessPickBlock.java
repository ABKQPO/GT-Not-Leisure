package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import com.glodblock.github.util.Util;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.me.MEHandler;
import com.science.gtnl.compat.baubles.BaublesInventory;

import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class WirelessPickBlock extends ServerboundPacket {

    private int slot;
    private ItemStack stack = null;

    public WirelessPickBlock() {}

    public WirelessPickBlock(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    protected void read(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.slot = buf.readInt();
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.stack);
        buf.writeInt(this.slot);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        if (stack == null || slot < 0 || slot >= player.inventory.mainInventory.length) return;

        ItemStack handItem = player.inventory.getStackInSlot(slot);
        ItemStack needItem = stack.copy();
        if (handItem != null) {
            if (handItem.stackSize > handItem.getMaxStackSize()) return;
            else needItem.stackSize = handItem.getMaxStackSize();
        }

        ServerThreadUtil.addScheduledTask(() -> {
            readPlayer(player, needItem, this);
            if (needItem != null && needItem.stackSize != 0) {
                readBaubles(player, needItem, slot);
            }
        });
    }

    public void readPlayer(EntityPlayerMP player, ItemStack needItem, WirelessPickBlock message) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (item == null) continue;
            if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                if (work(item, player, needItem, message.slot, i, 0, Integer.MIN_VALUE)) {
                    needItem.stackSize = 0;
                    return;
                }
            }
        }
    }

    public static int getFirstEmptySlot(EntityPlayerMP player) {
        for (int i = 9; i < player.inventory.mainInventory.length; i++) {
            if (player.inventory.mainInventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void readBaubles(EntityPlayerMP player, ItemStack exitem, int slot) {
        IInventory baubles = BaublesInventory.of(player);
        if (baubles == null) return;
        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack item = baubles.getStackInSlot(i);
            if (item == null) continue;
            if (item.getItem() instanceof IWirelessTermHandler wt && wt.canHandle(item)) {
                if (work(item, player, exitem, slot, i, 1, Integer.MIN_VALUE)) {
                    return;
                }
            }
        }
    }

    private boolean work(ItemStack item, EntityPlayerMP player, ItemStack exItem, int slot, int x, int y, int z) {
        if (Platform.isClient()) return true;
        // int handItemConnt = 0;
        // if (player.inventory.getStackInSlot(slot) != null) {
        // ItemStack vitem = player.inventory.getStackInSlot(slot);
        // if (exItem.getItem() != vitem.getItem() || exItem.getItemDamage() != vitem.getItemDamage()
        // || exItem.getTagCompound() != vitem.getTagCompound()) return true;
        // handItemConnt = player.inventory.getStackInSlot(slot).stackSize;
        // }

        WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, x, y);

        if (obj == null) {
            return false;
        }

        if (!obj.rangeCheck() && !Util.hasInfinityBoosterCard(item)) {
            player.addChatMessage(PlayerMessages.OutOfRange.toChat());
        } else {
            IGrid grid = obj.getGrid();
            if (grid == null) return false;
            if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {

                IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                var iItemStorageChannel = storageGrid.getItemInventory();
                var aeItem = iItemStorageChannel.extractItems(
                    AEItemStack.create(exItem)
                        .setStackSize(exItem.stackSize),
                    Actionable.SIMULATE,
                    new PlayerSource(player, obj));
                if (aeItem != null && aeItem.getStackSize() > 0) {
                    var aeitem = iItemStorageChannel.extractItems(
                        AEItemStack.create(exItem)
                            .setStackSize(aeItem.getStackSize()),
                        Actionable.MODULATE,
                        new PlayerSource(player, obj));

                    ItemStack from = player.inventory.getStackInSlot(slot);
                    int emptySlot = getFirstEmptySlot(player);
                    if (emptySlot != -1 && from != null) {
                        player.inventory.setInventorySlotContents(emptySlot, from);
                    }

                    player.inventory.setInventorySlotContents(
                        slot,
                        aeitem.setStackSize(aeitem.getStackSize()) // +handItemConnt)
                            .getItemStack());

                    player.updateHeldItem();
                    if (emptySlot != -1 && from != null) {
                        player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, emptySlot, from));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean securityCheck(final EntityPlayer player, IGrid gridNode,
        final SecurityPermissions requiredPermission) {
        final ISecurityGrid sg = gridNode.getCache(ISecurityGrid.class);
        return sg.hasPermission(player, requiredPermission);
    }
}
