package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.*;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.api.IWirelessEnergy;
import com.science.gtnl.client.GTNLCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.metatileentity.BaseMetaTileEntity;

public class WirelessUpgradeChip extends Item {

    public WirelessUpgradeChip() {
        super();
        this.setUnlocalizedName("WirelessUpgradeChip");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "WirelessUpgradeChip");
        GTNLItemList.WirelessUpgradeChip.set(new ItemStack(this, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_WirelessUpgradeChip_00"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_WirelessUpgradeChip_01"));
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof BaseMetaTileEntity baseMetaTileEntity
                && baseMetaTileEntity.getMetaTileEntity() instanceof IWirelessEnergy wirelessEnergyMulti) {
                if (!wirelessEnergyMulti.isWirelessUpgrade()) {
                    wirelessEnergyMulti.setWirelessUpgrade(true);
                    stack.splitStack(1);
                    return true;
                }
            }
        }
        return false;
    }
}
