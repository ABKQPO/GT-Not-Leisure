package com.science.gtnl.common.machine.hatch;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.api.mixinHelper.ICommonMetaTileEntityInventory;
import com.science.gtnl.api.mixinHelper.IMetaTileEntityInventoryHandler;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.gui.modularui.hatch.MTEHatchOutputBusGui;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;

public class MTEBreelSteamOutputBus extends MTEHatchSteamBusOutput {

    public MTEBreelSteamOutputBus(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName, tier);
        initializeInventory();
    }

    public MTEBreelSteamOutputBus(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
        initializeInventory();
    }

    public void initializeInventory() {
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[25]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                MTEBreelSteamOutputBus.this.onContentsChanged(slot);
            }
        });
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEBreelSteamOutputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new MTEHatchOutputBusGui(this) {

            @Override
            protected int getDimension() {
                return 5;
            }
        }.build(data, syncManager, uiSettings);
    }

    @Override
    protected int getStackTransferAmount() {
        return 25;
    }

    @Override
    public String[] getDescription() {
        return new String[] { "%%%" + 25 + "%%%" };
    }
}
