package com.science.gtnl.common.machine.hatch;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;

import gregtech.api.gui.widgets.PhantomItemButton;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;

public class SteamOutputBus extends MTEHatchOutputBus {

    private final int itemSlots;

    public SteamOutputBus(int id, String name, String nameRegional, int itemSlots) {
        super(id, name, nameRegional, 0, createDescription(itemSlots), itemSlots);
        this.itemSlots = itemSlots;
    }

    public SteamOutputBus(String name, int tier, int itemSlots, String[] description, ITexture[][][] textures) {
        super(name, tier, itemSlots, description, textures);
        this.itemSlots = itemSlots;
    }

    private static String[] createDescription(int itemSlots) {
        return new String[] { StatCollector.translateToLocal("Tooltip_AdvancedSteamOutputBus_00"),
            StatCollector.translateToLocal("Tooltip_AdvancedSteamOutputBus_01"),
            StatCollector.translateToLocalFormatted("Tooltip_AdvancedSteamOutputBus_02", itemSlots),
            StatCollector.translateToLocal("Tooltip_AdvancedSteamOutputBus_03") };
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamOutputBus(mName, mTier, itemSlots, mDescriptionArray, mTextures);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        switch (getSizeInventory()) {
            case 1 -> getBaseMetaTileEntity().add1by1Slot(builder);
            case 4 -> getBaseMetaTileEntity().add2by2Slots(builder);
            case 9 -> getBaseMetaTileEntity().add3by3Slots(builder);
            case 16 -> getBaseMetaTileEntity().add4by4Slots(builder);
            default -> {}
        }

        if (acceptsItemLock()) {
            builder.widget(
                new PhantomItemButton(this).setPos(getGUIWidth() - 25, 40)
                    .setBackground(PhantomItemButton.FILTER_BACKGROUND));
        }
    }
}
