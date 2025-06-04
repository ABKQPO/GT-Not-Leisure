package com.science.gtnl.common.machine.hatch;

import java.lang.reflect.Field;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;

public class SuperDataAccessHatch extends MTEHatchDataAccess implements IAddGregtechLogo {

    private ItemStack[] mInventory;

    public SuperDataAccessHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_SuperDataAccessHatch_00");
        initializeInventory(81);
    }

    public SuperDataAccessHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        initializeInventory(81);
    }

    private void initializeInventory(int size) {
        try {
            Field mInventoryField = MetaTileEntity.class.getField("mInventory");
            mInventoryField.setAccessible(true);
            mInventory = new ItemStack[size];
            mInventoryField.set(this, mInventory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize mInventory", e);
        }
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperDataAccessHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col;
                builder.widget(
                    new SlotWidget(new ItemStackHandler(mInventory), slotIndex)
                        .setBackground(ModularUITextures.ITEM_SLOT, GTUITextures.OVERLAY_SLOT_CIRCUIT)
                        .setPos(col * 18 + 43, row * 18 + 18));
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return 81;
    }

    @Override
    public int getGUIWidth() {
        return super.getGUIWidth() + 72;
    }

    @Override
    public int getGUIHeight() {
        return super.getGUIHeight() + 112;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= 0 && slot < mInventory.length) {
            return mInventory[slot];
        }
        return null;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(210, 162));
    }
}
