package com.science.gtnl.common.machine.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;

public class SteamInputBus extends MTEHatchInputBus {

    private final int itemSlots;

    public SteamInputBus(int id, String name, String nameRegional, int itemSlots) {
        super(id, name, nameRegional, 0, itemSlots + 1, createDescription(itemSlots));
        this.itemSlots = itemSlots;
    }

    public SteamInputBus(String name, int tier, int itemSlots, String[] description, ITexture[][][] textures) {
        super(name, tier, itemSlots + 1, description, textures);
        this.itemSlots = itemSlots;
    }

    private static String[] createDescription(int itemSlots) {
        return new String[] { StatCollector.translateToLocal("Tooltip_AdvancedSteamInputBus_00"),
            StatCollector.translateToLocal("Tooltip_AdvancedSteamInputBus_01"),
            StatCollector.translateToLocalFormatted("Tooltip_AdvancedSteamInputBus_02", itemSlots),
            StatCollector.translateToLocal("Tooltip_AdvancedSteamInputBus_03") };
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamInputBus(mName, mTier, itemSlots, mDescriptionArray, mTextures);
    }

    @Override
    public int getCircuitSlot() {
        return itemSlots;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }
}
