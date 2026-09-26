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
import gregtech.api.util.GTItemTransfer;
import gregtech.common.gui.modularui.hatch.MTEHatchInputBusGui;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;

public class MTEBreelSteamInputBus extends MTEHatchSteamBusInput {

    public MTEBreelSteamInputBus(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName, tier);
        initializeInventory();
    }

    public MTEBreelSteamInputBus(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
        initializeInventory();
    }

    public void initializeInventory() {
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[26]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                MTEBreelSteamInputBus.this.onContentsChanged(slot);
            }
        });
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEBreelSteamInputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getCircuitSlot() {
        return 25;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new MTEHatchInputBusGui(this) {

            @Override
            protected int getDimension() {
                return 5;
            }
        }.build(data, syncManager, uiSettings);
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long timer) {
        super.onPostTick(baseMetaTileEntity, timer);
        if (!baseMetaTileEntity.isServerSide()) return;
        if ((timer & 0x7) != 0) return;

        GTItemTransfer transfer = new GTItemTransfer();
        transfer.pull(baseMetaTileEntity, baseMetaTileEntity.getFrontFacing());
        transfer.setStacksToTransfer(25);
        transfer.setMaxItemsPerTransfer(getStackSizeLimit(-1, null));
        transfer.transfer();
    }

    @Override
    public String[] getDescription() {
        return new String[] { "%%%" + 25 + "%%%" };
    }
}
