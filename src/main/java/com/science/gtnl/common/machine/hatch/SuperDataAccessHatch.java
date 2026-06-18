package com.science.gtnl.common.machine.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.science.gtnl.common.gui.modularui.SuperDataAccessHatchGui;
import com.science.gtnl.mixins.early.Gregtech.AccessorCommonMetaTileEntity;
import com.science.gtnl.mixins.early.Gregtech.AccessorMetaTileEntity;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;

public class SuperDataAccessHatch extends MTEHatchDataAccess implements IAddGregtechLogo {

    public SuperDataAccessHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_SuperDataAccessHatch_00");
        initializeInventory();
    }

    public SuperDataAccessHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        initializeInventory();
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperDataAccessHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    protected boolean useMui2() {
        return true;
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

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after SuperDataAccessHatch mui2 rollout is complete.
        IItemHandlerModifiable inventoryHandler = getInventoryHandler();
        if (inventoryHandler == null) return;

        builder.widget(
            SlotGroup.ofItemHandler(inventoryHandler, 9)
                .startFromSlot(0)
                .endAtSlot(80)
                .background(getGUITextureSet().getItemSlot(), GTUITextures.OVERLAY_SLOT_CIRCUIT)
                .build()
                .setPos(43, 18));
    }

    @Override
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after SuperDataAccessHatch mui2 rollout is complete.
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(210, 162));
    }

    public void initializeInventory() {
        ((AccessorCommonMetaTileEntity) this).setInventory(new ItemStack[81]);
        ((AccessorMetaTileEntity) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                SuperDataAccessHatch.this.onContentsChanged(slot);
            }
        });
    }
}
