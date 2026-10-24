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
import gregtech.api.util.GTItemTransfer;
import gregtech.common.gui.modularui.hatch.MTEHatchInputBusGui;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class BreelSteamInputBus extends MTEHatchSteamBusInput {

    private static final int ITEM_SLOTS = 25;

    public BreelSteamInputBus(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName, tier);
        initializeInventory();
    }

    public BreelSteamInputBus(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
        initializeInventory();
    }

    public void initializeInventory() {
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[ITEM_SLOTS + 1]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                BreelSteamInputBus.this.onContentsChanged(slot);
            }
        });
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new BreelSteamInputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getCircuitSlot() {
        return ITEM_SLOTS;
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
        if (transfer.transfer() > 0) {
            updateSlots();
            notifyWatchers();
        }
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.breel.input_bus.steam.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.breel.input_bus.steam.tooltip.0"),
            StatCollector.translateToLocal("gtnl.hatch.breel.input_bus.steam.tooltip.1"),
            StatCollector.translateToLocal("gtnl.hatch.breel.input_bus.steam.tooltip.2"),
            StatCollector.translateToLocalFormatted("gtnl.hatch.breel.input_bus.steam.tooltip.3", ITEM_SLOTS),
            StatCollector.translateToLocal("gtnl.hatch.breel.input_bus.steam.tooltip.4") };
    }
}
