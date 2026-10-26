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
import com.science.gtnl.common.gui.modularui.hatch.SuperDataAccessHatchGui;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class SuperDataAccessHatch extends MTEHatchDataAccess {

    public SuperDataAccessHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        initializeInventory();
    }

    public SuperDataAccessHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        initializeInventory();
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.super_data_access.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        return new String[] { super.getDescription()[0],
            StatCollector.translateToLocal("gtnl.hatch.super_data_access.tooltip.0") };
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperDataAccessHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getGUIWidth() {
        return super.getGUIWidth() + 72;
    }

    @Override
    public int getGUIHeight() {
        return super.getGUIHeight() + 112;
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new SuperDataAccessHatchGui(this).build(data, syncManager, uiSettings);
    }

    public void initializeInventory() {
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[81]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                SuperDataAccessHatch.this.onContentsChanged(slot);
            }
        });
    }
}
