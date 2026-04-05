package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import gregtech.api.covers.CoverRegistry;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

@Mixin(value = MTEBasicMachine.class, remap = false)
public abstract class MixinMTEBasicMachineFacing extends MTEBasicTank {

    @Shadow
    public boolean mAllowInputFromOutputSide;

    @Shadow
    @Final
    public int mInputSlotCount;

    @Shadow
    @Final
    public ItemStack[] mOutputItems;

    @Shadow
    public boolean mDisableMultiStack;

    @Shadow
    public boolean mDisableFilter;

    @Shadow
    protected abstract boolean allowPutStackValidated(IGregTechTileEntity aBaseMetaTileEntity, int aIndex,
        ForgeDirection side, ItemStack aStack);

    public MixinMTEBasicMachineFacing(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount,
        String aDescription, ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean isLiquidInput(ForgeDirection side) {
        return mAllowInputFromOutputSide || side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isLiquidOutput(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean allowCoverOnSide(ForgeDirection side, ItemStack coverItem) {
        return CoverRegistry.getCoverPlacer(coverItem)
            .isGuiClickable();
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return aIndex >= getOutputSlot() && aIndex < getOutputSlot() + mOutputItems.length;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        if (aIndex < getInputSlot()) return false;
        if (aIndex >= getInputSlot() + mInputSlotCount) return false;
        if (!mAllowInputFromOutputSide && side == aBaseMetaTileEntity.getFrontFacing()) return false;

        for (int i = getInputSlot(), j = i + mInputSlotCount; i < j; i++) {
            if (GTUtility.areStacksEqual(GTOreDictUnificator.get(aStack), mInventory[i]) && mDisableMultiStack) {
                return i == aIndex;
            }
        }

        return mDisableFilter || allowPutStackValidated(aBaseMetaTileEntity, aIndex, side, aStack);
    }
}
