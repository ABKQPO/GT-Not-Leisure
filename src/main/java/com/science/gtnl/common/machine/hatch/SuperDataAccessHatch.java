package com.science.gtnl.common.machine.hatch;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class SuperDataAccessHatch extends MTEHatchDataAccess implements IAddGregtechLogo {

    private List<GTRecipe.RecipeAssemblyLine> cachedRecipes = null;

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
    public int getGUIWidth() {
        return super.getGUIWidth() + 72;
    }

    @Override
    public int getGUIHeight() {
        return super.getGUIHeight() + 112;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
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
    public List<GTRecipe.RecipeAssemblyLine> getAssemblyLineRecipes() {
        if (cachedRecipes == null || cachedRecipes.isEmpty()) {
            cachedRecipes = new ArrayList<>();

            for (int i = 0; i < getSizeInventory(); i++) {
                cachedRecipes.addAll(AssemblyLineUtils.findALRecipeFromDataStick(getStackInSlot(i)));
            }
        }

        return cachedRecipes;
    }

    @Override
    public void onContentsChanged(int slot) {
        cachedRecipes = null;
    }

    @Override
    public int getSizeInventory() {
        return 81;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return true;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= 0 && slot < mInventory.length) {
            return mInventory[slot];
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int aIndex, ItemStack aStack) {
        if (this instanceof IConfigurationCircuitSupport ccs) {
            if (ccs.allowSelectCircuit() && aIndex == ccs.getCircuitSlot() && aStack != null) {
                mInventory[aIndex] = GTUtility.copyAmount(0, aStack);
                markDirty();
                return;
            }
        }
        if (aIndex >= 0 && aIndex < mInventory.length) {
            mInventory[aIndex] = aStack;
        }
        markDirty();
        onContentsChanged(aIndex);
    }

    @Override
    public ItemStack getFirstStack() {
        for (int index = 0; index < mInventory.length; index++) {
            ItemStack stackInSlot = getStackInSlot(index);
            if (stackInSlot != null) {
                return stackInSlot;
            }
        }
        return null;
    }

    @Override
    public ItemStack[] getRealInventory() {
        return mInventory;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStack, int ordinalSide) {
        return isValidSlot(index) && itemStack != null
            && index < mInventory.length
            && (mInventory[index] == null || GTUtility.areStacksEqual(itemStack, mInventory[index]))
            && allowPutStack(getBaseMetaTileEntity(), index, ForgeDirection.getOrientation(ordinalSide), itemStack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack itemStack, int ordinalSide) {
        return isValidSlot(index) && itemStack != null
            && index < mInventory.length
            && allowPullStack(getBaseMetaTileEntity(), index, ForgeDirection.getOrientation(ordinalSide), itemStack);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(210, 162));
    }

    private void initializeInventory() {
        try {
            Field mInventoryField = MetaTileEntity.class.getField("mInventory");
            Field inventoryHandlerField = MetaTileEntity.class.getField("inventoryHandler");
            mInventoryField.setAccessible(true);
            inventoryHandlerField.setAccessible(true);
            ItemStack[] mInventory = new ItemStack[81];
            ItemStackHandler inventoryHandler = new ItemStackHandler(mInventory) {

                @Override
                public void onContentsChanged(int slot) {
                    SuperDataAccessHatch.this.onContentsChanged(slot);
                }
            };
            mInventoryField.set(this, mInventory);
            inventoryHandlerField.set(this, inventoryHandler);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize mInventory", e);
        }
    }
}
