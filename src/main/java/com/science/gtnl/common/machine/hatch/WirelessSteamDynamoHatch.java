package com.science.gtnl.common.machine.hatch;

import static com.science.gtnl.utils.steam.SteamWirelessNetworkManager.*;
import static gregtech.common.misc.WirelessNetworkManager.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableSet;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.mixins.late.Gregtech.AccessorMTEHatch;
import com.science.gtnl.utils.enums.SteamTypes;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.fluid.IFluidStore;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class WirelessSteamDynamoHatch extends MTEHatchOutput implements IFluidStore, IAddGregtechLogo {

    public UUID ownerUUID;
    public UUID teamUUID;
    public boolean isInTeam;
    public BigInteger steamDisplay;
    public Set<Fluid> mLockedFluids;

    public WirelessSteamDynamoHatch(final int aID, final String aName, final String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        this.mLockedFluids = ImmutableSet.of(
            Materials.Steam.mGas,
            FluidUtils.getSuperHeatedSteam(1)
                .getFluid(),
            Materials.DenseSupercriticalSteam.mGas,
            MaterialPool.CompressedSteam.getMolten(1)
                .getFluid());
    }

    public WirelessSteamDynamoHatch(final String aName, int aTier, final ITexture[][][] aTextures, Set<Fluid> aFluid) {
        super(aName, aTier, 3, new String[] { "" }, aTextures);
        this.mLockedFluids = ImmutableSet.of(
            Materials.Steam.mGas,
            FluidUtils.getSuperHeatedSteam(1)
                .getFluid(),
            Materials.DenseSupercriticalSteam.mGas,
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
    public FluidSlotWidget createFluidSlot() {
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
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS[0] };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS[0] };
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

        ownerUUID = aBaseMetaTileEntity.getOwnerUuid();

        SpaceProjectManager.checkOrCreateTeam(ownerUUID);

        isInTeam = SpaceProjectManager.isInTeam(ownerUUID);

        if (isInTeam) {
            teamUUID = SpaceProjectManager.getLeader(ownerUUID);
            steamDisplay = getUserSteam(ownerUUID);
        }

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

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPostTick(baseMetaTileEntity, tick);
        if (baseMetaTileEntity.isServerSide()) {
            if (tick % 200 == 0L) {
                isInTeam = SpaceProjectManager.isInTeam(ownerUUID);

                if (isInTeam) {
                    teamUUID = SpaceProjectManager.getLeader(ownerUUID);
                    steamDisplay = getUserSteam(ownerUUID);
                }
            }
        }
    }

    private void tryFetchingSteam() {
        FluidStack currentSteamStack = getFillableStack();

        if (currentSteamStack != null && currentSteamStack.amount > 0) {

            int rawAmount = currentSteamStack.amount;
            Fluid fluidType = currentSteamStack.getFluid();

            SteamTypes matchedSteamType = null;
            for (SteamTypes steamType : SteamTypes.VALUES) {
                if (steamType.fluid != null && steamType.fluid.equals(fluidType)) {
                    matchedSteamType = steamType;
                    break;
                }
            }

            if (matchedSteamType != null) {
                int convertedAmount = rawAmount * matchedSteamType.efficiencyFactor;

                if (!addSteamToGlobalSteamMap(ownerUUID, convertedAmount)) {
                    return;
                }

                drain(rawAmount, true);
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setString("OwnerUUID", ownerUUID.toString());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        ownerUUID = UUID.fromString(aNBT.getString("OwnerUUID"));
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        String steamNetworkOwner = tag.getString("SteamNetworkOwner");
        boolean isInTeam = tag.getBoolean("isInSteamNetwork");

        if (!isInTeam) {
            currenttip.add(StatCollector.translateToLocalFormatted("Info_SteamNetwork_00", steamNetworkOwner));
        } else {
            String steamNetworkDisplay = tag.getString("SteamNetworkDisplay");
            currenttip.add(
                StatCollector
                    .translateToLocalFormatted("Info_SteamNetwork_01", steamNetworkOwner, steamNetworkDisplay));
            if (tag.hasKey("SteamNetworkTeam")) {
                currenttip.add(
                    StatCollector.translateToLocalFormatted(
                        "Info_SteamNetwork_02",
                        steamNetworkOwner,
                        tag.getString("SteamNetworkTeam")));
            }
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setString("SteamNetworkOwner", SpaceProjectManager.getPlayerNameFromUUID(ownerUUID));
        tag.setBoolean("isInSteamNetwork", isInTeam);

        if (isInTeam) {
            tag.setString(
                "SteamNetworkDisplay",
                steamDisplay.toString()
                    .length() > 10 ? GTUtility.scientificFormat(steamDisplay) : GTUtility.formatNumbers(steamDisplay));
            if (!ownerUUID.equals(teamUUID)) {
                tag.setString("SteamNetworkTeam", SpaceProjectManager.getPlayerNameFromUUID(teamUUID));
            }
        }
    }
}
