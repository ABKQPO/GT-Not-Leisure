package com.science.gtnl.common.packet;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.tile.misc.TileSecurity;
import com.glodblock.github.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.utils.MEHandler;

import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class WirelessPickBlock implements IMessage, IMessageHandler<WirelessPickBlock, IMessage> {

    private int slot;
    private ItemStack stack = null;

    public WirelessPickBlock() {

    }

    public WirelessPickBlock(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.stack);
        buf.writeInt(this.slot);
    }

    @Override
    public IMessage onMessage(WirelessPickBlock message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ItemStack handItem = player.inventory.getStackInSlot(message.slot);
        ItemStack needItem = message.stack.copy();
        if (handItem != null) {
            if (handItem.stackSize >= handItem.getMaxStackSize()) return null;
            else needItem.stackSize = handItem.getMaxStackSize();
        }

        ServerThreadUtil.addScheduledTask(() -> readPlayer(player, needItem, message));

        if (needItem != null && needItem.stackSize != 0 && Loader.isModLoaded("Baubles")) {
            ServerThreadUtil.addScheduledTask(() -> readBaubles(player, needItem, message.slot));
        }

        return null;
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

    @Optional.Method(modid = "Baubles")
    public void readBaubles(EntityPlayerMP player, ItemStack exitem, int slot) {
        for (int i = 0; i < BaublesApi.getBaubles(player)
            .getSizeInventory(); i++) {
            ItemStack item = BaublesApi.getBaubles(player)
                .getStackInSlot(i);
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
        int handItemConnt = 0;
        if (player.inventory.getStackInSlot(slot) != null) {
            ItemStack vitem = player.inventory.getStackInSlot(slot);
            if (exItem.getItem() != vitem.getItem() || exItem.getItemDamage() != vitem.getItemDamage()
                || exItem.getTagCompound() != vitem.getTagCompound()) return true;
            handItemConnt = player.inventory.getStackInSlot(slot).stackSize;
        }

        WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, x, y);

        if (obj == null) {
            return false;
        }

        if (!obj.rangeCheck() && !Util.hasInfinityBoosterCard(item)) {
            player.addChatMessage(PlayerMessages.OutOfRange.toChat());
        } else {
            IGridNode gridNode = obj.getActionableNode();
            if (Util.hasInfinityBoosterCard(item)) {
                IWirelessTermHandler handler = AEApi.instance()
                    .registries()
                    .wireless()
                    .getWirelessTerminalHandler(item);
                String unparsedKey = handler.getEncryptionKey(item);
                long parsedKey = Long.parseLong(unparsedKey);
                ILocatable securityStation = AEApi.instance()
                    .registries()
                    .locatable()
                    .getLocatableBy(parsedKey);
                if (securityStation instanceof TileSecurity t) {
                    gridNode = t.getActionableNode();
                    if (gridNode == null) {
                        player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                    }
                }
            }
            if (gridNode == null) {
                return false;
            }
            IGrid grid = gridNode.getGrid();
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

                    player.inventory.setInventorySlotContents(
                        slot,
                        aeitem.setStackSize(aeitem.getStackSize() + handItemConnt)
                            .getItemStack());
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
