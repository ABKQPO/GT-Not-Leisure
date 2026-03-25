package com.science.gtnl.common.part;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.api.mixinHelper.IDualityInterface;
import com.science.gtnl.mixins.late.AppliedEnergistics.AccessorPartInterface;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.storage.data.IAEItemStack;
import appeng.parts.automation.StackUpgradeInventory;
import appeng.parts.misc.PartInterface;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.WrapperInvSlot;

// TODO:再加个二合一ME接口
public class PartSuperInterface extends PartInterface implements ICustomGui {

    public int configSlots = 27;
    public int storageSlots = 27;
    public int patternSlots = 108;
    public int upgradeSlots = 4;

    public PartSuperInterface(ItemStack is) {
        super(is);
        var duality = (IDualityInterface) ((AccessorPartInterface) this).getDuality();
        duality.setConfigSlots(configSlots);
        duality.setStorageSlots(storageSlots);
        duality.setPatternSlots(patternSlots);
        duality.setUpgradeSlots(upgradeSlots);
        duality.setConfig(new AppEngInternalAEInventory(this, configSlots));
        duality.setStorage(new AppEngInternalInventory(this, storageSlots));
        duality.setPatterns(new AppEngInternalInventory(this, patternSlots));
        duality.setSlotInv(new WrapperInvSlot(duality.getStorage()));
        duality.setUpgrades(
            new StackUpgradeInventory(
                duality.getGridProxy()
                    .getMachineRepresentation(),
                this,
                upgradeSlots));
        duality.setRequireWork(new IAEItemStack[storageSlots]);
        duality.setHasFuzzyConfig(new boolean[configSlots]);
    }

    @Override
    public boolean onPartActivate(final EntityPlayer p, final Vec3 pos) {
        if (p.isSneaking()) {
            return false;
        }
        CommonProxy.openGui(
            p,
            GuiType.SuperInterfaceGUI,
            this.getSide(),
            this.getHost()
                .getTile());
        return true;
    }

    @Override
    public int rows() {
        return 12;
    }

    @Override
    public ItemStack getOriginGuiIcon() {
        return GTNLItemList.PartSuperInterface.get(1);
    }
}
