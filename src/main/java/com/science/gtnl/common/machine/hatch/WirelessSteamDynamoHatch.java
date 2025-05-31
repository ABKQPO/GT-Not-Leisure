package com.science.gtnl.common.machine.hatch;

import static com.science.gtnl.Utils.steam.SteamWirelessNetworkManager.addSteamToGlobalSteamMap;
import static gregtech.common.misc.WirelessNetworkManager.number_of_energy_additions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableSet;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;
import com.science.gtnl.common.materials.MaterialPool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.fluid.IFluidStore;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class WirelessSteamDynamoHatch extends MTEHatchOutput implements IFluidStore, IAddGregtechLogo {

    private UUID owner_uuid;
    public Set<Fluid> mLockedFluids;

    public WirelessSteamDynamoHatch(final int aID, final String aName, final String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        this.mLockedFluids = ImmutableSet.of(
            Materials.Steam.getGas(1)
                .getFluid(),
            FluidUtils.getSuperHeatedSteam(1)
                .getFluid(),
            FluidRegistry.getFluid("supercriticalsteam"),
            MaterialPool.CompressedSteam.getMolten(1)
                .getFluid());
    }

    public WirelessSteamDynamoHatch(final String aName, int aTier, final ITexture[][][] aTextures, Set<Fluid> aFluid) {
        super(aName, aTier, 3, new String[] { "" }, aTextures);
        this.mLockedFluids = ImmutableSet.of(
            Materials.Steam.getGas(1)
                .getFluid(),
            FluidUtils.getSuperHeatedSteam(1)
                .getFluid(),
            FluidRegistry.getFluid("supercriticalsteam"),
            MaterialPool.CompressedSteam.getMolten(1)
                .getFluid());
    }

    @Override
    public MetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new WirelessSteamDynamoHatch(this.mName, this.mTier, this.mTextures, this.mLockedFluids);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public int getCapacity() {
        return mTier == 0 ? 128000000 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidLocked() {
        return true;
    }

    @Override
    public boolean acceptsFluidLock(String name) {
        return false;
    }

    @Override
    public void setLockedFluidName(String lockedFluidName) {}

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {}

    @Override
    public boolean isLiquidInput(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean doesEmptyContainers() {
        return true;
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

    @Override
    public String[] getDescription() {
        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("Tooltip_WirelessSteamDynamoHatch_00"));
        desc.add(StatCollector.translateToLocal("Tooltip_WirelessSteamDynamoHatch_01"));
        desc.add(StatCollector.translateToLocal("HatchCustomFluid_01") + getCapacity() + "L");
        if (mTier == 0) {
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamVent_00"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamVent_01"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamVent_02"));
        } else {
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessJetstreamVent_00"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessJetstreamVent_01"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessJetstreamVent_02"));
        }

        return desc.toArray(new String[] {});
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_WIRELESS_ON[0] };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_WIRELESS_ON[0] };
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
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[0][0] };
        }
    }

    public ITexture getBaseTexture(int colorIndex) {
        if (mTier == 0) {
            return TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZE_SIDE);
        }
        return TextureFactory.of(Textures.BlockIcons.MACHINE_STEEL_SIDE);
    }

    @Override
    public boolean isEmptyAndAcceptsAnyFluid() {
        return getFluidAmount() == 0;
    }

    @Override
    public boolean canStoreFluid(@Nonnull FluidStack fluidStack) {
        return mFluid == null || GTUtility.areFluidsEqual(mFluid, fluidStack);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);

        if (!aBaseMetaTileEntity.isServerSide()) return;

        owner_uuid = aBaseMetaTileEntity.getOwnerUuid();

        SpaceProjectManager.checkOrCreateTeam(owner_uuid);

        tryFetchingSteam();
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {

        super.onPreTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTick % number_of_energy_additions == 0L) {
                tryFetchingSteam();
            }
        }
    }

    private void tryFetchingSteam() {
        FluidStack currentSteamStack = getFillableStack();

        if (currentSteamStack != null && currentSteamStack.amount > 0) {

            int rawAmount = currentSteamStack.amount;
            Fluid fluidType = currentSteamStack.getFluid();

            SteamMultiMachineBase.SteamTypes matchedSteamType = null;
            for (SteamMultiMachineBase.SteamTypes steamType : SteamMultiMachineBase.SteamTypes.VALUES) {
                if (steamType.fluid != null && steamType.fluid.equals(fluidType)) {
                    matchedSteamType = steamType;
                    break;
                }
            }

            if (matchedSteamType != null) {
                int convertedAmount = rawAmount * matchedSteamType.efficiencyFactor;

                if (!addSteamToGlobalSteamMap(owner_uuid, convertedAmount)) {
                    return;
                }

                drain(rawAmount, true);
            }
        }
    }
}
