package com.science.gtnl.common.machine.hatch;

import static gregtech.common.misc.WirelessNetworkManager.number_of_energy_additions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableSet;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class WirelessSteamEnergyHatch extends CustomFluidHatch {

    public UUID ownerUUID;
    public UUID teamUUID;
    public boolean isInTeam;
    public BigInteger steamDisplay;

    public WirelessSteamEnergyHatch(final int aID, final String aName, final String aNameRegional, int aTier) {
        super(
            ImmutableSet.of(
                Materials.Steam.getGas(1)
                    .getFluid()),
            aTier == 0 ? 8000000 : Integer.MAX_VALUE,
            aID,
            aName,
            aNameRegional,
            aTier);
    }

    public WirelessSteamEnergyHatch(final String aName, final ITexture[][][] aTextures, int aTier) {
        super(
            ImmutableSet.of(
                Materials.Steam.getGas(1)
                    .getFluid()),
            aTier == 0 ? 8000000 : Integer.MAX_VALUE,
            aName,
            aTier,
            new String[] { "" },
            aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new WirelessSteamEnergyHatch(this.mName, this.mTextures, this.mTier);
    }

    @Override
    public String[] getDescription() {
        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamEnergyHatch_00"));
        desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamEnergyHatch_01"));
        desc.add(StatCollector.translateToLocal("HatchCustomFluid_01") + getCapacity() + "L");
        if (mTier == 0) {
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamHatch_00"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamHatch_01"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessSteamHatch_02"));
        } else {
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessJetstreamHatch_00"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessJetstreamHatch_01"));
            desc.add(StatCollector.translateToLocal("Tooltip_PipelessJetstreamHatch_02"));
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
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public ITexture getBaseTexture(int colorIndex) {
        if (mTier == 0) {
            return TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZE_SIDE);
        }
        return TextureFactory.of(Textures.BlockIcons.MACHINE_STEEL_SIDE);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        ownerUUID = aBaseMetaTileEntity.getOwnerUuid();

        SpaceProjectManager.checkOrCreateTeam(ownerUUID);

        isInTeam = SpaceProjectManager.isInTeam(ownerUUID);

        if (isInTeam) {
            teamUUID = SpaceProjectManager.getLeader(ownerUUID);
            steamDisplay = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
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
                    steamDisplay = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
                }
            }
        }
    }

    private void tryFetchingSteam() {
        BigInteger networkSteam = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
        int steamForUse;

        if (networkSteam.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            steamForUse = Integer.MAX_VALUE;
        } else {
            steamForUse = networkSteam.intValue();
        }

        FluidStack currentSteamStack = getFillableStack();

        if (currentSteamStack == null) {
            currentSteamStack = Materials.Steam.getGas(0);
        }

        if (currentSteamStack.amount < mFluidCapacity) {

            int currentSteam = currentSteamStack.amount;
            int maxSteam = mFluidCapacity;

            int steamToTransfer = Math.min(maxSteam - currentSteam, steamForUse);

            if (steamToTransfer <= 0) return;

            if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, -steamToTransfer)) return;
            fill(Materials.Steam.getGas(steamToTransfer), true);
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
        try {
            ownerUUID = UUID.fromString(aNBT.getString("OwnerUUID"));
        } catch (IllegalArgumentException e) {
            ScienceNotLeisure.LOG
                .warn("[WirelessSteamEnergyHatch] Invalid OwnerUUID in NBT: {}", aNBT.getString("OwnerUUID"), e);
        }
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
