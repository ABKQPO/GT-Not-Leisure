package com.science.gtnl.common.packet;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

import com.glodblock.github.client.gui.container.ContainerItemMonitor;
import com.glodblock.github.common.item.ItemWirelessUltraTerminal;
import com.glodblock.github.inventory.InventoryHandler;
import com.glodblock.github.inventory.gui.GuiType;
import com.glodblock.github.inventory.item.IWirelessTerminal;
import com.glodblock.github.util.BlockPos;
import com.glodblock.github.util.Util;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.utils.MEHandler;
import com.science.gtnl.utils.RCAEBaseContainer;
import com.science.gtnl.utils.Utils;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.core.AppEng;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IContainerCraftingPacket;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.cache.CraftingGridCache;
import appeng.tile.misc.TileSecurity;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class KeyBindingHandler implements IMessage, IMessageHandler<KeyBindingHandler, IMessage> {

    public ItemStack stack;
    public String key;
    public boolean isAE = false;

    public KeyBindingHandler() {

    }

    public KeyBindingHandler(String key) {
        this.key = key;
    }

    public KeyBindingHandler(String key, ItemStack item, boolean isAE) {
        this.key = key;
        this.stack = item;
        this.isAE = isAE;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.key = ByteBufUtils.readUTF8String(buf);
        this.isAE = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.stack);
        ByteBufUtils.writeUTF8String(buf, this.key);
        buf.writeBoolean(this.isAE);
    }

    public static Map<UUID, Long> map = new ConcurrentHashMap<>();

    @Override
    public IMessage onMessage(KeyBindingHandler message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        var container = player.openContainer;
        var item = message.stack;
        switch (message.key) {
            case "gui.ae_retrieve_item" -> ServerThreadUtil
                .addScheduledTask(() -> retrieveItem(player, container, item, message.isAE));
            case "gui.ae_start_craft" -> ServerThreadUtil
                .addScheduledTask(() -> startCraft(player, container, item, message.isAE));
        }
        return null;
    }

    private void retrieveItem(EntityPlayerMP player, Container container, ItemStack exItem, boolean isAE) {
        long targetCount = exItem.getMaxStackSize();
        if (!isAE) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack item = player.inventory.getStackInSlot(i);
                WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, i, 0);

                if (obj == null) {
                    continue;
                }

                if (!obj.rangeCheck()) {
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
                            IGridNode gridNode = t.getActionableNode();
                            if (gridNode == null) {
                                player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                                continue;
                            }
                            targetCount = wirelessRetrieve(player, exItem, gridNode, targetCount, obj);
                            if (targetCount <= 0) {
                                return;
                            }
                        }
                        continue;
                    }
                    player.addChatMessage(PlayerMessages.OutOfRange.toChat());
                } else {
                    IGridNode gridNode = obj.getActionableNode();
                    if (gridNode == null) {
                        player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                        continue;
                    }
                    targetCount = wirelessRetrieve(player, exItem, gridNode, targetCount, obj);
                    if (targetCount <= 0) {
                        return;
                    }
                }
            }
            if (Loader.isModLoaded("Baubles")) {
                readBaublesR(player, exItem, targetCount);
            }
        } else if (container instanceof AEBaseContainer c && container instanceof IContainerCraftingPacket t) {
            IGridNode gridNode = t.getNetworkNode();
            if (gridNode == null) {
                player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                return;
            }
            IGrid grid = gridNode.getGrid();
            if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
                IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                var iItemStorageChannel = storageGrid.getItemInventory();
                var host = c.getTarget();
                if (host instanceof IActionHost h) {
                    var aeItem = Optional.ofNullable(
                        iItemStorageChannel.extractItems(
                            AEItemStack.create(exItem)
                                .setStackSize(targetCount),
                            Actionable.SIMULATE,
                            new PlayerSource(player, h)));

                    if (aeItem.isPresent()) {
                        var aeItem0 = aeItem.get();
                        var aeItem1 = iItemStorageChannel.extractItems(
                            AEItemStack.create(exItem)
                                .setStackSize(aeItem0.getStackSize()),
                            Actionable.MODULATE,
                            new PlayerSource(player, h));

                        Utils.placeItemBackInInventory(player, aeItem1.getItemStack());
                    }
                }
            }
        }
    }

    private long wirelessRetrieve(EntityPlayerMP player, ItemStack exItem, IGridNode gridNode, long targetCount,
        WirelessTerminalGuiObject obj) {
        IGrid grid = gridNode.getGrid();
        if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
            var iItemStorageChannel = storageGrid.getItemInventory();
            var aeItem = Optional.ofNullable(
                iItemStorageChannel.extractItems(
                    AEItemStack.create(exItem)
                        .setStackSize(targetCount),
                    Actionable.SIMULATE,
                    new PlayerSource(player, obj)));

            if (aeItem.isPresent()) {
                var aeItem0 = aeItem.get();
                var aeItem1 = iItemStorageChannel.extractItems(
                    AEItemStack.create(exItem)
                        .setStackSize(aeItem0.getStackSize()),
                    Actionable.MODULATE,
                    new PlayerSource(player, obj));

                targetCount -= aeItem1.getStackSize();

                Utils.placeItemBackInInventory(player, aeItem1.getItemStack());
            }
        }
        return targetCount;
    }

    private void startCraft(EntityPlayerMP player, Container container, ItemStack exItem, boolean isAE) {
        UUID playUUID = player.getUniqueID();
        long worldTime = Instant.now()
            .getEpochSecond();
        if (map.containsKey(playUUID)) {
            if (map.get(playUUID) < worldTime) {
                map.put(playUUID, worldTime);
            } else {
                return;
            }
        } else {
            map.put(playUUID, worldTime);
        }
        if (!isAE) {
            if (player.openContainer instanceof ContainerCraftAmount
                || player.openContainer instanceof ContainerCraftConfirm) return;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack item = player.inventory.getStackInSlot(i);
                WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, i, 0);

                if (obj == null) {
                    continue;
                }

                if (!obj.rangeCheck()) {
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
                            IGridNode gridNode = t.getActionableNode();
                            if (gridNode == null) {
                                player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                                continue;
                            }
                            openWirelessCraft(item, player, exItem, gridNode, i, false);
                            return;
                        }
                        continue;
                    }
                    player.addChatMessage(PlayerMessages.OutOfRange.toChat());
                } else {
                    IGridNode gridNode = obj.getActionableNode();
                    if (gridNode == null) {
                        player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                        continue;
                    }
                    openWirelessCraft(item, player, exItem, gridNode, i, false);
                    return;
                }
            }
            if (Loader.isModLoaded("Baubles")) {
                readBaublesS(player, exItem);
            }
        } else if (container instanceof ContainerMEMonitorable || container instanceof ContainerItemMonitor) {
            AEBaseContainer aec;
            IGridNode gridNode;
            if (container instanceof ContainerItemMonitor c) {
                aec = c;
                gridNode = c.getNetworkNode();
            } else {
                return;
            }
            if (gridNode == null) {
                player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                return;
            }
            IGrid grid = gridNode.getGrid();
            if (securityCheck(player, grid, SecurityPermissions.CRAFT)) {
                CraftingGridCache cgc = gridNode.getGrid()
                    .getCache(ICraftingGrid.class);
                IAEItemStack aeItem = AEItemStack.create(exItem)
                    .setStackSize(1);
                boolean isCraftable = cgc.getCraftingPatterns()
                    .containsKey(aeItem);

                if (!isCraftable) {
                    player.addChatMessage(new ChatComponentTranslation("nei.bookmark.ae_no_craft"));
                    return;
                }

                var host = aec.getTarget();
                if (host instanceof IActionHost h) {
                    if (Loader.isModLoaded("ae2fc")) ae2fcCraft(h, player, aec);
                    else Platform.openGUI(
                        player,
                        aec.getOpenContext()
                            .getTile(),
                        aec.getOpenContext()
                            .getSide(),
                        GuiBridge.GUI_CRAFTING_AMOUNT);

                    if (player.openContainer instanceof ContainerCraftAmount cca) {
                        var item0 = aeItem.getItemStack();
                        cca.getCraftingItem()
                            .putStack(item0);
                        cca.setItemToCraft(aeItem);
                        cca.setInitialCraftAmount(exItem.stackSize);
                        cca.detectAndSendChanges();
                    }
                }
            }
        }
    }

    private void openWirelessCraft(ItemStack terminal, EntityPlayerMP player, ItemStack exItem, IGridNode gridNode,
        int i, boolean isBauble) {
        IGrid grid = gridNode.getGrid();
        if (securityCheck(player, grid, SecurityPermissions.CRAFT)) {
            CraftingGridCache cgc = gridNode.getGrid()
                .getCache(ICraftingGrid.class);
            IAEItemStack aeItem = AEItemStack.create(exItem)
                .setStackSize(1);
            boolean isCraftable = cgc.getCraftingPatterns()
                .containsKey(aeItem);

            if (!isCraftable) {
                player.addChatMessage(new ChatComponentTranslation("nei.bookmark.ae_no_craft"));
                return;
            }

            final var oldContainer = player.openContainer;

            if (terminal.getItem() instanceof ItemWirelessUltraTerminal) {
                var value = Util.GuiHelper.encodeType(0, Util.GuiHelper.GuiType.ITEM);
                InventoryHandler.openGui(
                    player,
                    player.worldObj,
                    new BlockPos(isBauble ? i + value : i, value, 0),
                    ForgeDirection.UNKNOWN,
                    GuiType.FLUID_CRAFTING_AMOUNT);
            } else {
                player.openGui(
                    AppEng.instance(),
                    GuiBridge.GUI_CRAFTING_AMOUNT.ordinal() << 5 | (1 << 4),
                    player.getEntityWorld(),
                    i,
                    isBauble ? 1 : 0,
                    Integer.MIN_VALUE);
            }

            var newContainer = player.openContainer;

            if (newContainer instanceof ContainerCraftAmount cca) {
                if (newContainer instanceof RCAEBaseContainer rcc) {
                    rcc.rc$setOldContainer(oldContainer);
                }
                var item0 = aeItem.getItemStack();
                cca.getCraftingItem()
                    .putStack(item0);
                cca.setItemToCraft(aeItem);
                cca.setInitialCraftAmount(exItem.stackSize);
                cca.detectAndSendChanges();
            }
        }
    }

    @cpw.mods.fml.common.Optional.Method(modid = "ae2fc")
    private void ae2fcCraft(IActionHost host, EntityPlayerMP player, AEBaseContainer c) {
        if (host instanceof IWirelessTerminal wt) {
            InventoryHandler.openGui(
                player,
                player.worldObj,
                new BlockPos(wt.getInventorySlot(), Util.GuiHelper.encodeType(0, Util.GuiHelper.GuiType.ITEM), 0),
                ForgeDirection.UNKNOWN,
                GuiType.FLUID_CRAFTING_AMOUNT);
        } else {
            Platform.openGUI(
                player,
                c.getOpenContext()
                    .getTile(),
                c.getOpenContext()
                    .getSide(),
                GuiBridge.GUI_CRAFTING_AMOUNT);
        }
    }

    @cpw.mods.fml.common.Optional.Method(modid = "Baubles")
    private void readBaublesS(EntityPlayerMP player, ItemStack exitem) {
        for (int i = 0; i < BaublesApi.getBaubles(player)
            .getSizeInventory(); i++) {
            ItemStack item = BaublesApi.getBaubles(player)
                .getStackInSlot(i);
            WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, i, 1);

            if (obj == null) {
                continue;
            }

            if (!obj.rangeCheck()) {
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
                        IGridNode gridNode = t.getActionableNode();
                        if (gridNode == null) {
                            player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                            continue;
                        }
                        openWirelessCraft(item, player, exitem, gridNode, i, true);
                        return;
                    }
                    continue;
                }
                player.addChatMessage(PlayerMessages.OutOfRange.toChat());
            } else {
                IGridNode gridNode = obj.getActionableNode();
                if (gridNode == null) {
                    player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                    continue;
                }
                openWirelessCraft(item, player, exitem, gridNode, i, true);
                return;
            }
        }
    }

    @cpw.mods.fml.common.Optional.Method(modid = "Baubles")
    private void readBaublesR(EntityPlayerMP player, ItemStack exitem, long targetCount) {
        var inv = BaublesApi.getBaubles(player);
        if (inv == null) return;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack item = inv.getStackInSlot(i);
            WirelessTerminalGuiObject obj = MEHandler.getTerminalGuiObject(item, player, i, 1);

            if (obj == null) {
                continue;
            }

            if (!obj.rangeCheck()) {
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
                        IGridNode gridNode = t.getActionableNode();
                        if (gridNode == null) {
                            player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                            continue;
                        }
                        targetCount = wirelessRetrieve(player, exitem, gridNode, targetCount, obj);
                        if (targetCount <= 0) {
                            return;
                        }
                    }
                    continue;
                }
                player.addChatMessage(PlayerMessages.OutOfRange.toChat());
            } else {
                IGridNode gridNode = obj.getActionableNode();
                if (gridNode == null) {
                    player.addChatMessage(PlayerMessages.DeviceNotLinked.toChat());
                    continue;
                }
                targetCount = wirelessRetrieve(player, exitem, gridNode, targetCount, obj);
                if (targetCount <= 0) {
                    return;
                }
            }
        }
    }

    private boolean securityCheck(final EntityPlayer player, IGrid gridNode,
        final SecurityPermissions requiredPermission) {
        final ISecurityGrid sg = gridNode.getCache(ISecurityGrid.class);
        return sg.hasPermission(player, requiredPermission);
    }
}
