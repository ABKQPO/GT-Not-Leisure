package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.Textures.BlockIcons.FLUID_STEAM_IN_SIGN;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PIPE_IN;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;

import gregtech.GTMod;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUIInfos;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

public class CustomFluidHatch extends MTEHatch {

    public Set<Fluid> mLockedFluids;
    public int mFluidCapacity;

    public CustomFluidHatch(Set<Fluid> aFluid, int aAmount, int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            3,
            new String[] { StatCollector.translateToLocal("HatchCustomFluid_00"),
                StatCollector.translateToLocal("HatchCustomFluid_01") + GTUtility.formatNumbers(aAmount) + "L" });
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
    }

    public CustomFluidHatch(Set<Fluid> aFluid, int aAmount, int aID, String aName, String aNameRegional, int aTier,
        String[] aDescription, ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, 3, aDescription, aTextures);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
    }

    public CustomFluidHatch(Set<Fluid> aFluid, int aAmount, int aID, String aName, String aNameRegional, int aTier,
        String[] aDescription) {
        super(aID, aName, aNameRegional, aTier, 3, aDescription);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
    }

    public CustomFluidHatch(Set<Fluid> aFluid, int aAmount, String aName, int aTier, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
    }

    @Override
    public MetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new CustomFluidHatch(
            this.mLockedFluids,
            this.mFluidCapacity,
            this.mName,
            this.mTier,
            this.mDescriptionArray,
            this.mTextures);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {

        int texturePointer = getUpdateData();
        int mTexturePage;

        try {
            Class<?> hatchClass = MTEHatch.class;
            Field texturePageField = hatchClass.getDeclaredField("mTexturePage");
            texturePageField.setAccessible(true);
            mTexturePage = (byte) texturePageField.get(this);
            if (mTexturePage < 0 || mTexturePage >= Textures.BlockIcons.casingTexturePages.length) {
                return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[0][0] };
            }

            int textureIndex = texturePointer | (mTexturePage << 7);

            if (side != aFacing) {
                if (textureIndex > 0 && texturePointer < Textures.BlockIcons.casingTexturePages[mTexturePage].length) {
                    return new ITexture[] { Textures.BlockIcons.casingTexturePages[mTexturePage][texturePointer] };
                } else {
                    return new ITexture[] { getBaseTexture(colorIndex) };
                }
            } else {
                if (textureIndex > 0 && texturePointer < Textures.BlockIcons.casingTexturePages[mTexturePage].length) {
                    if (aActive) {
                        return getTexturesActive(Textures.BlockIcons.casingTexturePages[mTexturePage][texturePointer]);
                    } else {
                        return getTexturesInactive(
                            Textures.BlockIcons.casingTexturePages[mTexturePage][texturePointer]);
                    }
                } else {
                    if (aActive) {
                        return getTexturesActive(getBaseTexture(colorIndex));
                    } else {
                        return getTexturesInactive(getBaseTexture(colorIndex));
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[0][0] };
        }
    }

    protected ITexture getBaseTexture(int colorIndex) {
        return Textures.BlockIcons.MACHINE_CASINGS[mTier][colorIndex + 1];
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, final ForgeDirection side,
        final ItemStack aStack) {
        if (side == aBaseMetaTileEntity.getFrontFacing() && aIndex == 0) {
            FluidStack fs = GTUtility.getFluidForFilledItem(aStack, true);
            return fs != null && mLockedFluids.contains(fs.getFluid());
        }
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == aBaseMetaTileEntity.getFrontFacing() && aIndex == 1;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return GTMod.gregtechproxy.mRenderIndicatorsOnHatch
            ? new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN),
                TextureFactory.of(FLUID_STEAM_IN_SIGN) }
            : new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return GTMod.gregtechproxy.mRenderIndicatorsOnHatch
            ? new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN),
                TextureFactory.of(FLUID_STEAM_IN_SIGN) }
            : new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN) };
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        GTUIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
        return true;
    }

    @Override
    public boolean doesFillContainers() {
        // return true;
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return true;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return true;
    }

    @Override
    public boolean displaysItemStack() {
        return true;
    }

    public void updateSlots() {
        if (mInventory[getInputSlot()] != null && mInventory[getInputSlot()].stackSize <= 0)
            mInventory[getInputSlot()] = null;
    }

    @Override
    public int getTankPressure() {
        return -100;
    }

    @Override
    public int getCapacity() {
        return this.mFluidCapacity;
    }

    @Override
    public String[] getDescription() {
        if (mLockedFluids == null) return new String[] { "INVALID HATCH. ASSIGN LOCKED FLUIDS" };

        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("HatchCustomFluid_00"));
        desc.add(StatCollector.translateToLocal("HatchCustomFluid_01") + getCapacity() + "L");
        desc.add(StatCollector.translateToLocal("HatchCustomFluid_02"));

        for (Fluid allowed : mLockedFluids) {
            desc.add("-" + (allowed.getLocalizedName(new FluidStack(allowed, 1))));
        }

        return desc.toArray(new String[] {});
    }

    @Override
    public boolean isFluidInputAllowed(final FluidStack aFluid) {
        for (Fluid allowed : mLockedFluids) {
            if (allowed.getName()
                .equals(
                    aFluid.getFluid()
                        .getName()))
                return true;
        }
        return false;
    }

    @Override
    protected FluidSlotWidget createFluidSlot() {
        return super.createFluidSlot().setFilter(mLockedFluids::contains);
    }
}
