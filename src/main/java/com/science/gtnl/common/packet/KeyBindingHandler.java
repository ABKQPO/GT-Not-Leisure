package com.science.gtnl.common.packet;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.common.item.ItemWirelessUltraTerminal;
import com.glodblock.github.inventory.InventoryHandler;
import com.glodblock.github.inventory.item.IWirelessTerminal;
import com.glodblock.github.util.BlockPos;
import com.glodblock.github.util.Util;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.PreviousContainerPrimaryGui;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.utils.MEHandler;
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
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.container.AEBaseContainer;
import appeng.container.PrimaryGui;
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
import baubles.api.BaublesApi;
import cpw.mods.fml.common.network.ByteBufUtils;
import gregtech.api.enums.Mods;
import io.netty.buffer.ByteBuf;

public class KeyBindingHandler extends ServerboundPacket {

    public IAEStack<?> stack;
    public String key;
    public boolean isAE = false;

    public KeyBindingHandler() {

    }

    public KeyBindingHandler(String key) {
        this.key = key;
    }

    public KeyBindingHandler(String key, IAEStack<?> stack, boolean isAE) {
        this.key = key;
        this.stack = stack;
        this.isAE = isAE;
    }

    @Override
    protected void read(ByteBuf buf) {
        this.stack = Platform.readStackByte(buf);
        this.key = ByteBufUtils.readUTF8String(buf);
        this.isAE = buf.readBoolean();
    }

    @Override
    protected void write(ByteBuf buf) {
        Platform.writeStackByte(this.stack, buf);
        ByteBufUtils.writeUTF8String(buf, this.key);
        buf.writeBoolean(this.isAE);
    }

    public static Map<UUID, Long> map = new ConcurrentHashMap<>();

    @Override
    public void handleServer(EntityPlayerMP player) {
        var container = player.openContainer;
        var requestedStack = stack;
        if (requestedStack == null) return;
        switch (key) {
            case "gui.ae_retrieve_item" -> ServerThreadUtil
                .addScheduledTask(() -> retrieveStack(player, container, requestedStack, isAE));
            case "gui.ae_start_craft" -> ServerThreadUtil
                .addScheduledTask(() -> startCraft(player, container, requestedStack, isAE));
        }
    }

    private void retrieveStack(EntityPlayerMP player, Container container, IAEStack<?> requestedStack, boolean isAE) {
        long targetCount = getTargetCount(requestedStack);
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
                            targetCount = wirelessRetrieve(player, requestedStack, gridNode, targetCount, obj);
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
                    targetCount = wirelessRetrieve(player, requestedStack, gridNode, targetCount, obj);
                    if (targetCount <= 0) {
                        return;
                    }
                }
            }
            if (Mods.Baubles.isModLoaded()) {
                readBaublesR(player, requestedStack, targetCount);
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
                var host = c.getTarget();
                if (host instanceof IActionHost h) {
                    extractAndDeliver(player, storageGrid, requestedStack, targetCount, new PlayerSource(player, h));
                }
            }
        }
    }

    private long wirelessRetrieve(EntityPlayerMP player, IAEStack<?> requestedStack, IGridNode gridNode,
        long targetCount, WirelessTerminalGuiObject obj) {
        IGrid grid = gridNode.getGrid();
        if (securityCheck(player, grid, SecurityPermissions.EXTRACT)) {
            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
            return extractAndDeliver(player, storageGrid, requestedStack, targetCount, new PlayerSource(player, obj));
        }
        return targetCount;
    }

    private long getTargetCount(IAEStack<?> requestedStack) {
        if (requestedStack instanceof IAEItemStack itemStack) {
            return itemStack.getItemStack()
                .getMaxStackSize();
        }
        return Math.max(1, requestedStack.getStackSize());
    }

    private long extractAndDeliver(EntityPlayerMP player, IStorageGrid storageGrid, IAEStack<?> requestedStack,
        long targetCount, PlayerSource source) {
        if (requestedStack instanceof IAEItemStack itemStack) {
            IAEItemStack request = itemStack.copy();
            request.setStackSize(targetCount);
            IAEItemStack extracted = storageGrid.getItemInventory()
                .extractItems(request, Actionable.MODULATE, source);
            if (extracted != null) {
                Utils.placeItemBackInInventory(player, extracted.getItemStack());
                return targetCount - extracted.getStackSize();
            }
            return targetCount;
        }

        if (requestedStack instanceof IAEFluidStack fluidStack) {
            IAEFluidStack request = fluidStack.copy();
            request.setStackSize(targetCount);
            IAEFluidStack extracted = storageGrid.getFluidInventory()
                .extractItems(request, Actionable.MODULATE, source);
            if (extracted != null) {
                ItemStack packet = ItemFluidPacket.newStack(extracted);
                if (packet != null) {
                    Utils.placeItemBackInInventory(player, packet);
                    return targetCount - extracted.getStackSize();
                }
            }
        }

        return targetCount;
    }

    private void startCraft(EntityPlayerMP player, Container container, IAEStack<?> requestedStack, boolean isAE) {
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
                            openWirelessCraft(item, player, requestedStack, gridNode, i, false);
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
                    openWirelessCraft(item, player, requestedStack, gridNode, i, false);
                    return;
                }
            }
            if (Mods.Baubles.isModLoaded()) {
                readBaublesS(player, requestedStack);
            }
        } else if (container instanceof ContainerMEMonitorable) {
            AEBaseContainer aec;
            IGridNode gridNode;
            if (container instanceof ContainerMEMonitorable c) {
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
                IAEStack<?> stackToCraft = requestedStack.copy();
                stackToCraft.setStackSize(1);
                boolean isCraftable = cgc.getCraftingMultiPatterns()
                    .containsKey(stackToCraft);

                if (!isCraftable) {
                    player.addChatMessage(new ChatComponentTranslation("gtnl.nei.bookmark.ae_no_craft"));
                    return;
                }

                var host = aec.getTarget();
                if (host instanceof IActionHost h) {
                    if (aec.getOpenContext() == null) {
                        ScienceNotLeisure.LOG.error(
                            "Cannot create the primary GUI without an open context for player {}",
                            player.getCommandSenderName());
                        return;
                    }
                    PrimaryGui primaryGui = aec.createPrimaryGui();
                    if (Mods.AE2FluidCraft.isModLoaded()) ae2fcCraft(h, player, aec);
                    else Platform.openGUI(
                        player,
                        aec.getOpenContext()
                            .getTile(),
                        aec.getOpenContext()
                            .getSide(),
                        GuiBridge.GUI_CRAFTING_AMOUNT);

                    if (player.openContainer instanceof ContainerCraftAmount cca) {
                        cca.setPrimaryGui(primaryGui);
                        cca.setItemToCraft(stackToCraft);
                        cca.setInitialCraftAmount(requestedStack.getStackSize());
                        cca.detectAndSendChanges();
                    } else {
                        ScienceNotLeisure.LOG.error(
                            "Failed to open AE crafting amount container for player {}; current container is {}",
                            player.getCommandSenderName(),
                            player.openContainer);
                    }
                } else {
                    ScienceNotLeisure.LOG.error(
                        "Cannot start AE crafting from non-action host {} for player {}",
                        host,
                        player.getCommandSenderName());
                }
            }
        }
    }

    private void openWirelessCraft(ItemStack terminal, EntityPlayerMP player, IAEStack<?> requestedStack,
        IGridNode gridNode, int i, boolean isBauble) {
        IGrid grid = gridNode.getGrid();
        if (securityCheck(player, grid, SecurityPermissions.CRAFT)) {
            CraftingGridCache cgc = gridNode.getGrid()
                .getCache(ICraftingGrid.class);
            IAEStack<?> stackToCraft = requestedStack.copy();
            stackToCraft.setStackSize(1);
            boolean isCraftable = cgc.getCraftingMultiPatterns()
                .containsKey(stackToCraft);

            if (!isCraftable) {
                player.addChatMessage(new ChatComponentTranslation("gtnl.nei.bookmark.ae_no_craft"));
                return;
            }

            final Container oldContainer = player.openContainer;
            if (oldContainer == null || oldContainer instanceof AEBaseContainer) {
                ScienceNotLeisure.LOG.error(
                    "Cannot start non-AE wireless crafting from container {} for player {}",
                    oldContainer,
                    player.getCommandSenderName());
                return;
            }
            if (terminal == null) {
                ScienceNotLeisure.LOG.error(
                    "Cannot start non-AE wireless crafting without a terminal for player {}",
                    player.getCommandSenderName());
                return;
            }
            final PrimaryGui primaryGui = new PreviousContainerPrimaryGui(oldContainer, terminal);

            if (terminal.getItem() instanceof ItemWirelessUltraTerminal) {
                var value = Util.GuiHelper.encodeType(0, Util.GuiHelper.GuiType.ITEM);
                InventoryHandler.openGui(
                    player,
                    player.worldObj,
                    new BlockPos(isBauble ? i + value : i, value, 0),
                    ForgeDirection.UNKNOWN,
                    GuiBridge.GUI_CRAFTING_AMOUNT);
            } else {
                player.openGui(
                    AppEng.instance(),
                    GuiBridge.GUI_CRAFTING_AMOUNT.ordinal() << 5 | (1 << 4),
                    player.getEntityWorld(),
                    i,
                    isBauble ? 1 : 0,
                    Integer.MIN_VALUE);
            }

            if (player.openContainer instanceof ContainerCraftAmount cca) {
                cca.setPrimaryGui(primaryGui);
                cca.setItemToCraft(stackToCraft);
                cca.setInitialCraftAmount(requestedStack.getStackSize());
                cca.detectAndSendChanges();
            } else {
                ScienceNotLeisure.LOG.error(
                    "Failed to open wireless crafting amount container for player {}; current container is {}",
                    player.getCommandSenderName(),
                    player.openContainer);
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
                GuiBridge.GUI_CRAFTING_AMOUNT);
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
    private void readBaublesS(EntityPlayerMP player, IAEStack<?> requestedStack) {
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
                        openWirelessCraft(item, player, requestedStack, gridNode, i, true);
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
                openWirelessCraft(item, player, requestedStack, gridNode, i, true);
                return;
            }
        }
    }

    @cpw.mods.fml.common.Optional.Method(modid = "Baubles")
    private void readBaublesR(EntityPlayerMP player, IAEStack<?> requestedStack, long targetCount) {
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
                        targetCount = wirelessRetrieve(player, requestedStack, gridNode, targetCount, obj);
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
                targetCount = wirelessRetrieve(player, requestedStack, gridNode, targetCount, obj);
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
