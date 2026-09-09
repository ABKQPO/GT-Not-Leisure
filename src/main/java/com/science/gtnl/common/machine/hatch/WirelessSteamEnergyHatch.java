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

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.modularui.WirelessSteamEnergyHatchGui;
import com.science.gtnl.utils.enums.SteamTypes;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class WirelessSteamEnergyHatch extends CustomFluidHatch {

    public UUID ownerUUID;
    public UUID teamUUID;
    public boolean isInTeam;
    public BigInteger steamDisplay;
    private SteamTypes selectedSteam = SteamTypes.STEAM;

    public WirelessSteamEnergyHatch(final int aID, final String aName, final String aNameRegional, int aTier) {
        super(
            SteamTypes.getSupportedFluids(),
            aTier == 0 ? 8000000 : Integer.MAX_VALUE,
            aID,
            aName,
            aNameRegional,
            aTier);
    }

    public WirelessSteamEnergyHatch(final String aName, final ITexture[][][] aTextures, int aTier) {
        super(
            SteamTypes.getSupportedFluids(),
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

        desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.tooltip.0"));
        desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.tooltip.1"));
        desc.add(
            StatCollector.translateToLocal("gtnl.hatch.custom_fluid.tooltip.capacity") + " " + getCapacity() + "L");
        if (mTier == 0) {
            desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.steam.tooltip.0"));
            desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.steam.tooltip.1"));
            desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.steam.tooltip.2"));
        } else {
            desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.jetstream.tooltip.0"));
            desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.jetstream.tooltip.1"));
            desc.add(StatCollector.translateToLocal("gtnl.hatch.wireless_steam_energy.jetstream.tooltip.2"));
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
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after WirelessSteamEnergyHatch mui2 parity is verified.
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public boolean usesSteamLogoForMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return new WirelessSteamEnergyHatchGui(this).build(guiData, syncManager, uiSettings);
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

        isInTeam = true;
        teamUUID = TeamNetworkManager.getTeamId(ownerUUID);
        steamDisplay = SteamWirelessNetworkManager.getUserSteam(ownerUUID);

        if (!aBaseMetaTileEntity.isServerSide()) return;
        tryFetchingSteam();
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPreTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && aTick % number_of_energy_additions == 0L) {
            tryFetchingSteam();
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPostTick(baseMetaTileEntity, tick);
        if (baseMetaTileEntity.isServerSide() && tick % 200 == 0L) {
            if (ownerUUID == null) {
                ownerUUID = baseMetaTileEntity.getOwnerUuid();
            }
            isInTeam = true;
            teamUUID = TeamNetworkManager.getTeamId(ownerUUID);
            steamDisplay = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
        }
    }

    private void tryFetchingSteam() {
        if (ownerUUID == null) return;

        BigInteger networkSteam = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
        FluidStack currentSteamStack = getFillableStack();
        SteamTypes steamType = currentSteamStack == null ? selectedSteam
            : SteamTypes.fromFluid(currentSteamStack.getFluid());
        if (steamType == null || !steamType.networkConvertible
            || currentSteamStack != null && currentSteamStack.amount >= mFluidCapacity) return;

        int storedAmount = currentSteamStack == null ? 0 : currentSteamStack.amount;
        int capacity = mFluidCapacity - storedAmount;
        BigInteger availableAmount = networkSteam.divide(steamType.networkSteamPerLiter);
        int amountToFill = availableAmount.min(BigInteger.valueOf(capacity))
            .intValue();
        if (amountToFill <= 0) return;

        FluidStack steamStack = new FluidStack(steamType.fluid, amountToFill);
        int acceptedAmount = fill(steamStack, false);
        if (acceptedAmount <= 0) return;

        long steamCost = (long) acceptedAmount * steamType.efficiencyFactor;
        if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, -steamCost)) return;

        steamStack.amount = acceptedAmount;
        fill(steamStack, true);
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        FluidStack steamStack = getFillableStack();
        SteamTypes steamType = steamStack == null ? null : SteamTypes.fromFluid(steamStack.getFluid());
        if (steamType != null && ownerUUID != null) {
            SteamWirelessNetworkManager
                .addSteamToGlobalSteamMap(ownerUUID, (long) steamStack.amount * steamType.efficiencyFactor);
        }
    }

    public SteamTypes getSteamMode() {
        return selectedSteam;
    }

    public void setSteamMode(SteamTypes steamType) {
        if (steamType == null || !steamType.networkConvertible) return;

        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity == null || !baseMetaTileEntity.isServerSide()) {
            selectedSteam = steamType;
            return;
        }

        FluidStack storedSteam = getFillableStack();
        if (storedSteam == null || storedSteam.amount <= 0 || storedSteam.getFluid() == steamType.fluid) {
            selectedSteam = steamType;
            tryFetchingSteam();
            return;
        }

        SteamTypes storedSteamType = SteamTypes.fromFluid(storedSteam.getFluid());
        if (storedSteamType == null || ownerUUID == null) return;

        long returnedSteam = (long) storedSteam.amount * storedSteamType.efficiencyFactor;
        if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, returnedSteam)) return;

        drain(storedSteam.amount, true);
        selectedSteam = steamType;
        tryFetchingSteam();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (ownerUUID != null) aNBT.setString("OwnerUUID", ownerUUID.toString());
        aNBT.setInteger("SelectedSteam", selectedSteam.ordinal());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        selectedSteam = SteamTypes.fromNetworkTypeId(aNBT.getInteger("SelectedSteam"));
        if (aNBT.hasKey("OwnerUUID")) {
            try {
                ownerUUID = UUID.fromString(aNBT.getString("OwnerUUID"));
            } catch (IllegalArgumentException e) {
                ScienceNotLeisure.LOG
                    .warn("[WirelessSteamEnergyHatch] Invalid OwnerUUID in NBT: {}", aNBT.getString("OwnerUUID"), e);
            }
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
            currenttip
                .add(StatCollector.translateToLocalFormatted("gtnl.waila.steam_network.unlinked", steamNetworkOwner));
        } else {
            String steamNetworkDisplay = tag.getString("SteamNetworkDisplay");
            currenttip.add(
                StatCollector.translateToLocalFormatted(
                    "gtnl.waila.steam_network.balance",
                    steamNetworkOwner,
                    steamNetworkDisplay));
            if (tag.hasKey("SteamNetworkTeam")) {
                currenttip.add(
                    StatCollector.translateToLocalFormatted(
                        "gtnl.waila.steam_network.team",
                        steamNetworkOwner,
                        tag.getString("SteamNetworkTeam")));
            }
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (ownerUUID == null) {
            return;
        }
        tag.setString("SteamNetworkOwner", TeamNetworkManager.getPlayerName(ownerUUID));
        tag.setBoolean("isInSteamNetwork", isInTeam);

        if (isInTeam && steamDisplay != null) {
            tag.setString(
                "SteamNetworkDisplay",
                steamDisplay.toString()
                    .length() > 10 ? GTUtility.scientificFormat(steamDisplay)
                        : NumberFormatUtil.formatNumber(steamDisplay));
            if (teamUUID != null && !TeamNetworkManager.isTeamOwner(ownerUUID)) {
                tag.setString("SteamNetworkTeam", TeamNetworkManager.getTeamName(teamUUID));
            }
        }
    }
}
