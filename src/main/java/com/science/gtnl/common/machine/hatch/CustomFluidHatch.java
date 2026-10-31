package com.science.gtnl.common.machine.hatch;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.gui.modularui.hatch.CustomFluidHatchGui;
import com.science.gtnl.mixins.early.gregtech.AccessorMTEHatch;

import gregtech.GTMod;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class CustomFluidHatch extends MTEHatch {

    public Set<GTUtility.FluidId> mLockedFluids;
    public int mFluidCapacity;
    public boolean steamLogo;
    public final String nameKey;

    public CustomFluidHatch(Set<GTUtility.FluidId> aFluid, int aAmount, int aID, String aName, String aNameKey,
        int aTier) {
        super(
            aID,
            aName,
            aNameKey,
            aTier,
            3,
            new String[] { StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.0"),
                StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.capacity") + " "
                    + NumberFormatUtil.formatNumber(aAmount)
                    + "L" });
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
        this.nameKey = aNameKey;
    }

    public CustomFluidHatch(Set<GTUtility.FluidId> aFluid, int aAmount, int aID, String aName, String aNameKey,
        int aTier, boolean steamLogo) {
        super(
            aID,
            aName,
            aNameKey,
            aTier,
            3,
            new String[] { StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.0"),
                StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.capacity") + " "
                    + NumberFormatUtil.formatNumber(aAmount)
                    + "L" });
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
        this.steamLogo = steamLogo;
        this.nameKey = aNameKey;
    }

    public CustomFluidHatch(Set<GTUtility.FluidId> aFluid, int aAmount, int aID, String aName, String aNameKey,
        int aTier, String[] aDescription, ITexture... aTextures) {
        super(aID, aName, aNameKey, aTier, 3, aDescription, aTextures);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
        this.nameKey = aNameKey;
    }

    public CustomFluidHatch(Set<GTUtility.FluidId> aFluid, int aAmount, int aID, String aName, String aNameKey,
        int aTier, String[] aDescription) {
        super(aID, aName, aNameKey, aTier, 3, aDescription);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
        this.nameKey = aNameKey;
    }

    public CustomFluidHatch(Set<GTUtility.FluidId> aFluid, int aAmount, String aName, int aTier, String[] aDescription,
        ITexture[][][] aTextures, String aNameKey) {
        super(aName, aTier, 3, aDescription, aTextures);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
        this.nameKey = aNameKey;
    }

    public CustomFluidHatch(Set<GTUtility.FluidId> aFluid, int aAmount, String aName, int aTier, String[] aDescription,
        ITexture[][][] aTextures, boolean steamLogo, String aNameKey) {
        super(aName, aTier, 3, aDescription, aTextures);
        this.mLockedFluids = aFluid;
        this.mFluidCapacity = aAmount;
        this.steamLogo = steamLogo;
        this.nameKey = aNameKey;
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return nameKey;
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public MetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new CustomFluidHatch(
            this.mLockedFluids,
            this.mFluidCapacity,
            this.mName,
            this.mTier,
            this.mDescriptionArray,
            this.mTextures,
            this.steamLogo,
            this.nameKey);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int mTexturePage = ((AccessorMTEHatch) this).getTexturePage();

        if (mTexturePage < 0 || mTexturePage >= Textures.BlockIcons.casingTexturePages.length) {
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[0][0] };
        }

        int textureIndex = ((AccessorMTEHatch) this).getTextureIndex();

        if (side != aFacing) {
            if (textureIndex > 0 && textureIndex < Textures.BlockIcons.casingTexturePages[mTexturePage].length) {
                return new ITexture[] { Textures.BlockIcons.casingTexturePages[mTexturePage][textureIndex] };
            } else {
                return new ITexture[] { getBaseTexture(colorIndex) };
            }
        } else {
            if (textureIndex > 0 && textureIndex < Textures.BlockIcons.casingTexturePages[mTexturePage].length) {
                if (aActive) {
                    return getTexturesActive(Textures.BlockIcons.casingTexturePages[mTexturePage][textureIndex]);
                } else {
                    return getTexturesInactive(Textures.BlockIcons.casingTexturePages[mTexturePage][textureIndex]);
                }
            } else {
                if (aActive) {
                    return getTexturesActive(getBaseTexture(colorIndex));
                } else {
                    return getTexturesInactive(getBaseTexture(colorIndex));
                }
            }
        }
    }

    public ITexture getBaseTexture(int colorIndex) {
        return Textures.BlockIcons.MACHINE_CASINGS[mTier][colorIndex + 1];
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return GTMod.proxy.mRenderIndicatorsOnHatch
            ? new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN),
                TextureFactory.of(Textures.BlockIcons.FLUID_STEAM_IN_SIGN) }
            : new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return GTMod.proxy.mRenderIndicatorsOnHatch
            ? new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN),
                TextureFactory.of(Textures.BlockIcons.FLUID_STEAM_IN_SIGN) }
            : new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN) };
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, final ForgeDirection side,
        final ItemStack aStack) {
        if (side == aBaseMetaTileEntity.getFrontFacing() && aIndex == 0) {
            return acceptsFluid(GTUtility.getFluidForFilledItem(aStack, true));
        }
        return false;
    }

    public boolean acceptsFluid(FluidStack fluidStack) {
        return FluidIdUtils.matchesAny(mLockedFluids, fluidStack);
    }

    public boolean acceptsFluid(Fluid fluid) {
        return FluidIdUtils.matchesAny(mLockedFluids, fluid);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == aBaseMetaTileEntity.getFrontFacing() && aIndex == 1;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        openGui(aPlayer);
        return true;
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

    public void updateSlots() {
        if (mInventory[getInputSlot()] != null && mInventory[getInputSlot()].stackSize <= 0)
            mInventory[getInputSlot()] = null;
    }

    @Override
    public int getCapacity() {
        return this.mFluidCapacity;
    }

    @Override
    public String[] getDescription() {
        if (mLockedFluids == null) return new String[] { "INVALID HATCH. ASSIGN LOCKED FLUIDS" };

        ObjectArrayList<String> desc = new ObjectArrayList<>();
        desc.add(StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.0"));
        desc.add(
            StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.capacity") + " " + mFluidCapacity + "L");
        desc.add(StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.allowed_fluids"));

        for (GTUtility.FluidId allowed : mLockedFluids) {
            desc.add(
                "-" + allowed.getFluidStack()
                    .getLocalizedName());
        }

        return desc.toArray(new String[] {});
    }

    @Override
    public boolean isFluidInputAllowed(final FluidStack aFluid) {
        return acceptsFluid(aFluid);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new CustomFluidHatchGui(this).build(data, syncManager, uiSettings);
    }

    public boolean usesSteamLogoForMui2() {
        return steamLogo;
    }
}
