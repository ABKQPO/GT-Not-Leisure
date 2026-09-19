package com.science.gtnl.common.machine.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.NuclearItemBusGui;

import gregtech.GTMod;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.ItemRadioactiveCellIC;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.common.items.ItemNeutronReflector;
import ic2.core.item.reactor.ItemReactorReflector;

public class NuclearItemBus extends MTEHatch {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_WORK = 1;
    public static final int SLOT_OUTPUT = 2;

    public NuclearItemBus(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            3,
            new String[] { StatCollector.translateToLocal("gtnl.hatch.nuclear_item_bus.tooltip.0"),
                StatCollector.translateToLocal("gtnl.hatch.nuclear_item_bus.tooltip.1"),
                StatCollector.translateToLocal("gtnl.hatch.nuclear_item_bus.tooltip.2") });
    }

    public NuclearItemBus(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NuclearItemBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return facing == ForgeDirection.UP;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, final ForgeDirection side,
        final ItemStack aStack) {
        if (side != ForgeDirection.UP || aIndex != SLOT_INPUT) return false;
        return isAcceptableItem(aStack);
    }

    public static boolean isHeatPlate(ItemStack aStack) {
        return aStack != null && GTOreDictUnificator.isItemStackInstanceOf(aStack, "plateDenseInvar");
    }

    public static boolean isNeutronReflector(ItemStack aStack) {
        if (aStack == null) return false;
        return aStack.getItem() instanceof ItemReactorReflector || aStack.getItem() instanceof ItemNeutronReflector;
    }

    public boolean isAcceptableItem(ItemStack aStack) {
        return isAcceptableFuelRod(aStack) || isHeatPlate(aStack) || isNeutronReflector(aStack);
    }

    public boolean isAcceptableFuelRod(ItemStack aStack) {
        return aStack != null && aStack.getItem() instanceof ItemRadioactiveCellIC;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == ForgeDirection.UP && (aIndex == SLOT_INPUT || aIndex == SLOT_WORK || aIndex == SLOT_OUTPUT);
    }

    public void updateSlots() {
        for (int i = 0; i < mInventory.length; i++) {
            if (mInventory[i] != null && mInventory[i].stackSize <= 0) {
                mInventory[i] = null;
            }
        }
    }

    public ItemStack getFuelInputStack() {
        return mInventory[SLOT_INPUT];
    }

    public ItemStack getWorkingFuelStack() {
        return mInventory[SLOT_WORK];
    }

    public ItemStack getOutputStack() {
        return mInventory[SLOT_OUTPUT];
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return inputBusTextures(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return inputBusTextures(aBaseTexture);
    }

    private ITexture[] inputBusTextures(ITexture aBaseTexture) {
        byte color = getBaseMetaTileEntity().getColorization();
        ITexture coloredPipeOverlay = TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_COLORS[color + 1]);
        return GTMod.proxy.mRenderIndicatorsOnHatch
            ? new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN), coloredPipeOverlay,
                TextureFactory.of(Textures.BlockIcons.ITEM_IN_SIGN) }
            : new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN),
                coloredPipeOverlay };
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        openGui(aPlayer);
        return true;
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new NuclearItemBusGui(this).build(data, syncManager, uiSettings);
    }
}
