package com.science.gtnl.common.item.cellworkbench;

import java.util.UUID;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.Nullable;

import appeng.api.config.CopyMode;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.implementations.tiles.ICellWorkbench;
import appeng.api.storage.ICellWorkbenchItem;
import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEStackType;
import appeng.api.util.IConfigManager;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.helpers.ICellRestriction;
import appeng.helpers.ICellRestriction.CellData;
import appeng.helpers.ICellRestriction.CellRestrictionData;
import appeng.helpers.IPrimaryGuiIconProvider;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEStackInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.ConfigManager;
import appeng.util.Platform;

public class PortableCellWorkbenchHost
    implements ICellWorkbench, IGuiItemObject, IInventorySlotAware, IPrimaryGuiIconProvider {

    public static final String ID_TAG = "PortableCellWorkbenchId";
    public static final String CELL_TAG = "cell";
    public static final String CONFIG_TAG = "config";
    public static final String SETTINGS_TAG = "settings";

    private final IInventory playerInventory;
    private final Item portableWorkbenchItem;
    private final int inventorySlot;
    private final String portableId;
    private final AppEngInternalInventory cell = new AppEngInternalInventory(this, 1);
    private final IAEStackInventory config = new IAEStackInventory(this, 63);
    private final ConfigManager manager = new ConfigManager(this);

    private IInventory cachedUpgrades;
    private IAEStackInventory cachedConfig;
    private boolean updatingCell;
    private long cellRestrictAmount;
    private byte cellRestrictTypes;
    @Nullable
    private IAEStackType<?> stackType;
    @Nullable
    private IAEStackType<?> previousStackType;

    public PortableCellWorkbenchHost(IInventory playerInventory, Item portableWorkbenchItem, int inventorySlot) {
        this.playerInventory = playerInventory;
        this.portableWorkbenchItem = portableWorkbenchItem;
        this.inventorySlot = inventorySlot;

        ItemStack stack = getItemStack();
        if (stack == null || stack.getItem() != portableWorkbenchItem) {
            throw new IllegalArgumentException("Portable cell workbench must be opened from its inventory slot");
        }

        this.portableId = ensurePortableId(stack);
        this.manager.registerSetting(Settings.COPY_MODE, CopyMode.CLEAR_ON_REMOVE);
        this.cell.setEnableClientEvents(true);
        readFromStack(stack);
    }

    @Override
    public ItemStack getItemStack() {
        return playerInventory.getStackInSlot(inventorySlot);
    }

    @Override
    public int getInventorySlot() {
        return inventorySlot;
    }

    public boolean isValid() {
        ItemStack stack = getItemStack();
        return stack != null && stack.getItem() == portableWorkbenchItem && portableId.equals(getPortableId(stack));
    }

    @Override
    public IInventory getInventoryByName(String name) {
        return CELL_TAG.equals(name) ? cell : null;
    }

    @Override
    public ICellWorkbenchItem getCell() {
        ItemStack stack = cell.getStackInSlot(0);
        return stack != null && stack.getItem() instanceof ICellWorkbenchItem cellWorkbenchItem ? cellWorkbenchItem
            : null;
    }

    @Override
    public IInventory getCellUpgradeInventory() {
        if (cachedUpgrades == null) {
            ICellWorkbenchItem cellWorkbenchItem = getCell();
            ItemStack stack = cell.getStackInSlot(0);
            if (cellWorkbenchItem == null || stack == null) {
                return null;
            }

            IInventory upgrades = cellWorkbenchItem.getUpgradesInventory(stack);
            if (upgrades == null) {
                return null;
            }

            cachedUpgrades = new PortableCellWorkbenchUpgradeInventory(this, upgrades);
        }
        return cachedUpgrades;
    }

    @Override
    public int getInstalledUpgrades(Upgrades upgrade) {
        IInventory upgrades = getCellUpgradeInventory();
        if (upgrades == null) {
            return 0;
        }

        for (int slot = 0; slot < upgrades.getSizeInventory(); slot++) {
            ItemStack stack = upgrades.getStackInSlot(slot);
            if (stack != null && stack.getItem() instanceof IUpgradeModule upgradeModule
                && upgradeModule.getType(stack) == upgrade) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public IConfigManager getConfigManager() {
        return manager;
    }

    @Override
    public void updateSetting(IConfigManager configManager, Enum settingName, Enum newValue) {
        saveChanges();
    }

    @Override
    public void saveChanges() {
        if (Platform.isClient()) {
            return;
        }

        ItemStack stack = getItemStack();
        if (stack == null || stack.getItem() != portableWorkbenchItem) {
            return;
        }

        NBTTagCompound data = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        cell.writeToNBT(data, CELL_TAG);
        config.writeToNBT(data, CONFIG_TAG);
        NBTTagCompound settings = new NBTTagCompound();
        manager.writeToNBT(settings);
        data.setTag(SETTINGS_TAG, settings);
        stack.setTagCompound(data);
        playerInventory.markDirty();
    }

    @Override
    public void onChangeInventory(IInventory inventory, int slot, InvOperation operation, ItemStack removedStack,
        ItemStack newStack) {
        if (operation == InvOperation.markDirty) {
            saveChanges();
            return;
        }

        if (inventory != cell || updatingCell) {
            return;
        }

        cachedUpgrades = null;
        cachedConfig = null;
        if (Platform.isClient()) {
            updateStackType();
            return;
        }

        updatingCell = true;
        try {
            ItemStack stack = cell.getStackInSlot(0);
            if (manager.getSetting(Settings.COPY_MODE) == CopyMode.KEEP_ON_REMOVE && stack != null
                && stack.getItem() instanceof ICellRestriction cellRestriction) {
                cellRestriction
                    .setCellRestriction(stack, new CellRestrictionData(cellRestrictTypes, cellRestrictAmount));
            }

            if (updateStackType() && stackType != previousStackType) {
                clearConfig();
            }
            previousStackType = stackType;

            IAEStackInventory cellConfig = getCellConfigInventory();
            if (cellConfig != null) {
                if (hasConfig(cellConfig)) {
                    copyConfig(cellConfig, config);
                } else {
                    copyConfig(config, cellConfig);
                    cellConfig.markDirty();
                }
            } else if (manager.getSetting(Settings.COPY_MODE) == CopyMode.CLEAR_ON_REMOVE) {
                clearConfig();
            }
        } finally {
            updatingCell = false;
        }

        saveChanges();
    }

    @Override
    public void saveAEStackInv() {
        if (updatingCell) {
            return;
        }

        IAEStackInventory cellConfig = getCellConfigInventory();
        if (cellConfig != null) {
            copyConfig(config, cellConfig);
            cellConfig.markDirty();
        }
        saveChanges();
    }

    @Override
    public IAEStackInventory getAEInventoryByName(StorageName name) {
        return config;
    }

    @Override
    public String getFilter() {
        ItemStack stack = cell.getStackInSlot(0);
        return stack != null && stack.getItem() instanceof ICellWorkbenchItem cellWorkbenchItem
            ? cellWorkbenchItem.getOreFilter(stack)
            : "";
    }

    @Override
    public void setFilter(String filter) {
        ItemStack stack = cell.getStackInSlot(0);
        if (stack != null && stack.getItem() instanceof ICellWorkbenchItem cellWorkbenchItem) {
            cellWorkbenchItem.setOreFilter(stack, filter);
            saveChanges();
        }
    }

    @Override
    public CellData getCellData(ItemStack stack) {
        ItemStack cellStack = cell.getStackInSlot(0);
        return cellStack != null && cellStack.getItem() instanceof ICellRestriction cellRestriction
            ? cellRestriction.getCellData(cellStack)
            : null;
    }

    @Override
    public CellRestrictionData getCellRestrictionData(ItemStack stack) {
        ItemStack cellStack = cell.getStackInSlot(0);
        return cellStack != null && cellStack.getItem() instanceof ICellRestriction cellRestriction
            ? cellRestriction.getCellRestrictionData(cellStack)
            : null;
    }

    @Override
    public void setCellRestriction(ItemStack stack, CellRestrictionData newRestriction) {
        if (newRestriction.isReset() || manager.getSetting(Settings.COPY_MODE) == CopyMode.KEEP_ON_REMOVE) {
            cellRestrictTypes = newRestriction.restrictionTypes;
            cellRestrictAmount = newRestriction.restrictionAmount;
        }

        ItemStack cellStack = cell.getStackInSlot(0);
        if (cellStack != null && cellStack.getItem() instanceof ICellRestriction cellRestriction) {
            cellRestriction.setCellRestriction(cellStack, newRestriction);
            saveChanges();
        }
    }

    @Override
    public boolean isSame(ICellWorkbench cellWorkbench) {
        return cellWorkbench == this && isValid();
    }

    @Override
    @Nullable
    public IAEStackType<?> getStackType() {
        return stackType;
    }

    @Override
    public TileEntity getTile() {
        return null;
    }

    @Override
    public ItemStack getPrimaryGuiIcon() {
        return new ItemStack(this.portableWorkbenchItem);
    }

    public void onUpgradeInventoryChanged() {
        if (getInstalledUpgrades(Upgrades.ORE_FILTER) == 0) {
            setFilter("");
        }
        saveChanges();
    }

    private void readFromStack(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return;
        }

        NBTTagCompound data = stack.getTagCompound();
        cell.readFromNBT(data, CELL_TAG);
        config.readFromNBT(data, CONFIG_TAG);
        if (data.hasKey(SETTINGS_TAG)) {
            manager.readFromNBT(data.getCompoundTag(SETTINGS_TAG));
        }
        updateStackType();
        previousStackType = stackType;
    }

    @Nullable
    private IAEStackInventory getCellConfigInventory() {
        if (cachedConfig == null) {
            ICellWorkbenchItem cellWorkbenchItem = getCell();
            ItemStack stack = cell.getStackInSlot(0);
            if (cellWorkbenchItem == null || stack == null) {
                return null;
            }

            cachedConfig = cellWorkbenchItem.getConfigAEInventory(stack);
        }
        return cachedConfig;
    }

    private boolean updateStackType() {
        ItemStack stack = cell.getStackInSlot(0);
        if (stack != null && stack.getItem() instanceof ICellWorkbenchItem cellWorkbenchItem) {
            stackType = cellWorkbenchItem.getStackType();
            return true;
        }

        stackType = null;
        return false;
    }

    private boolean hasConfig(IAEStackInventory inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getAEStackInSlot(slot) != null) {
                return true;
            }
        }
        return false;
    }

    private void clearConfig() {
        for (int slot = 0; slot < config.getSizeInventory(); slot++) {
            config.putAEStackInSlot(slot, null);
        }
    }

    private void copyConfig(IAEStackInventory source, IAEStackInventory destination) {
        for (int slot = 0; slot < destination.getSizeInventory(); slot++) {
            destination.putAEStackInSlot(slot, source.getAEStackInSlot(slot));
        }
    }

    private String ensurePortableId(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey(ID_TAG)) {
            return stack.getTagCompound()
                .getString(ID_TAG);
        }
        if (Platform.isClient()) {
            return "";
        }

        NBTTagCompound data = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        data.setString(
            ID_TAG,
            UUID.randomUUID()
                .toString());
        stack.setTagCompound(data);
        playerInventory.markDirty();
        return data.getString(ID_TAG);
    }

    private String getPortableId(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound()
            .getString(ID_TAG) : "";
    }
}
