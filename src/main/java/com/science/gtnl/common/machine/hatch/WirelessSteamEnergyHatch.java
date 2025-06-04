package com.science.gtnl.common.machine.hatch;

import static com.science.gtnl.Utils.steam.SteamWirelessNetworkManager.addSteamToGlobalSteamMap;
import static com.science.gtnl.Utils.steam.SteamWirelessNetworkManager.getUserSteam;
import static gregtech.common.misc.WirelessNetworkManager.number_of_energy_additions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableSet;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class WirelessSteamEnergyHatch extends CustomFluidHatch {

    private UUID owner_uuid;

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

        desc.add(StatCollector.translateToLocal("Tooltip_WirelessSteamEnergyHatch_00"));
        desc.add(StatCollector.translateToLocal("Tooltip_WirelessSteamEnergyHatch_01"));
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
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_WIRELESS_ON[0] };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_WIRELESS_ON[0] };
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
        BigInteger networkSteam = getUserSteam(owner_uuid);
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

            if (steamToTransfer <= 0) return; // nothing to transfer

            if (!addSteamToGlobalSteamMap(owner_uuid, -steamToTransfer)) return;
            fill(Materials.Steam.getGas(steamToTransfer), true);
        }
    }
}
