package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.api.mixinHelper.IDualityInterface;
import com.science.gtnl.mixins.late.appliedEnergistics.AccessorTileInterface;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.storage.data.IAEItemStack;
import appeng.parts.automation.StackUpgradeInventory;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.misc.TileInterface;
import appeng.util.inv.WrapperInvSlot;

public class TileEntitySuperInterface extends TileInterface implements ICustomGui {

    public int configSlots = 27;
    public int storageSlots = 27;
    public int patternSlots = 108;
    public int upgradeSlots = 4;

    public TileEntitySuperInterface() {
        super();
        ItemStack upgradeHost = getOriginGuiIcon();
        getProxy().setVisualRepresentation(upgradeHost);
        var duality = (IDualityInterface) ((AccessorTileInterface) this).getDuality();
        duality.setConfigSlots(configSlots);
        duality.setStorageSlots(storageSlots);
        duality.setPatternSlots(patternSlots);
        duality.setUpgradeSlots(upgradeSlots);
        duality.setConfig(new AppEngInternalAEInventory(this, configSlots));
        duality.setStorage(new AppEngInternalInventory(this, storageSlots));
        duality.setPatterns(new AppEngInternalInventory(this, patternSlots));
        duality.setSlotInv(new WrapperInvSlot(duality.getStorage()));
        duality.setUpgrades(new StackUpgradeInventory(upgradeHost, this, upgradeSlots));
        duality.setRequireWork(new IAEItemStack[storageSlots]);
        duality.setHasFuzzyConfig(new boolean[configSlots]);
    }

    @Override
    public int rows() {
        return 12;
    }

    @Override
    public ItemStack getOriginGuiIcon() {
        return GTNLItemList.SuperInterface.get(1);
    }

    @Override
    public ItemStack getSelfRep() {
        return getOriginGuiIcon();
    }

    @Override
    public ItemStack getPrimaryGuiIcon() {
        return getOriginGuiIcon();
    }
}
