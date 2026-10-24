package com.science.gtnl.common.machine.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.api.mixinHelper.ICommonMetaTileEntityInventory;
import com.science.gtnl.api.mixinHelper.IMetaTileEntityInventoryHandler;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.gui.modularui.hatch.MTEHatchOutputBusGui;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class BreelSteamOutputBus extends MTEHatchSteamBusOutput {

    private static final int ITEM_SLOTS = 25;

    public BreelSteamOutputBus(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName, tier);
        initializeInventory();
    }

    public BreelSteamOutputBus(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
        initializeInventory();
    }

    public void initializeInventory() {
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[ITEM_SLOTS]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                BreelSteamOutputBus.this.onContentsChanged(slot);
            }
        });
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new BreelSteamOutputBus(mName, mTier, mDescriptionArray, mTextures);
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
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.breel.output_bus.steam.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.breel.output_bus.steam.tooltip.0"),
            StatCollector.translateToLocal("gtnl.hatch.breel.output_bus.steam.tooltip.1"),
            StatCollector.translateToLocalFormatted("gtnl.hatch.breel.output_bus.steam.tooltip.2", ITEM_SLOTS),
            StatCollector.translateToLocal("gtnl.hatch.breel.output_bus.steam.tooltip.3") };
    }
}
