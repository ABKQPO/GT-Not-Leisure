package com.science.gtnl.common.machine.monitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.mixinHelper.ICostingEUHolder;
import com.science.gtnl.api.mixinHelper.IWirelessMode;
import com.science.gtnl.common.machine.cover.WirelessMultiEnergyCover;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.common.machine.multiblock.FOGAlloyBlastSmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGAlloySmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;
import com.science.gtnl.common.machine.multiblock.FOGSolarMuonCatalystModule;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;
import com.science.gtnl.common.machine.multiblock.NaquadahReactor.AdvancedHyperNaquadahReactor;
import com.science.gtnl.common.machine.multiblock.RealArtificialStar;
import com.science.gtnl.common.machine.multiblock.SingularityDataHub;
import com.science.gtnl.common.machine.multiblock.SuperSpaceElevator;
import com.science.gtnl.common.machine.multiblock.SupercomputingCenter;
import com.science.gtnl.common.machine.multiblock.WhiteNightGenerator;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.ETGWEyeOfHarmonyModule;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshopModule;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.EnergyInfuser;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.KuangBiaoOneGiantNuclearFusionReactor.UEVTier;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEWirelessEnergy;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import kekztech.common.tileentities.MTELapotronicSuperCapacitor;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;

public class EnergyMonitorCollector {

    private static final Comparator<EnergyMonitorRowSnapshot> ROW_DISPLAY_ORDER = Comparator
        .comparing(
            (EnergyMonitorRowSnapshot row) -> row.getEut()
                .abs(),
            Comparator.reverseOrder())
        .thenComparing(EnergyMonitorRowSnapshot::getDisplayName)
        .thenComparing(EnergyMonitorRowSnapshot::getOwnerName)
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getDimensionId())
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getX())
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getY())
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getZ());

    public static EnergyMonitorSnapshot collect(UUID monitorOwnerUuid) {
        if (monitorOwnerUuid == null) {
            return EnergyMonitorSnapshot.empty();
        }

        WirelessTeam.TeamContext teamContext = WirelessTeam.resolveContext(monitorOwnerUuid);
        UUID leader = teamContext.getLeader();
        Set<UUID> teamMembers = teamContext.getMembers();
        if (leader == null || teamMembers.isEmpty()) {
            return EnergyMonitorSnapshot.empty();
        }

        BigInteger wiredEnergy = BigInteger.ZERO;
        BigInteger wiredCapacity = BigInteger.ZERO;
        List<EnergyMonitorRowSnapshot> rows = new ArrayList<>();

        for (MetaTileEntity metaTileEntity : EnergyMonitorRegistry.snapshot()) {
            IGregTechTileEntity base = metaTileEntity.getBaseMetaTileEntity();
            if (base == null || base.isDead() || EnergyMonitorRegistry.isInvalid(metaTileEntity)) {
                EnergyMonitorRegistry.unregister(metaTileEntity);
                continue;
            }
            UUID ownerUuid = base.getOwnerUuid();
            if (ownerUuid == null || !teamMembers.contains(ownerUuid)) {
                continue;
            }

            World world = base.getWorld();
            if (world == null || world.isRemote
                || !world.blockExists(base.getXCoord(), base.getYCoord(), base.getZCoord())) {
                continue;
            }

            if (metaTileEntity instanceof MTELapotronicSuperCapacitor capacitor) {
                wiredEnergy = wiredEnergy.add(capacitor.getStored());
                wiredCapacity = wiredCapacity.add(capacitor.getEnergyCapacity());
            }

            for (EnergyMonitorRowSnapshot machineRow : createMachineRows(metaTileEntity, base)) {
                if (machineRow == null) {
                    continue;
                }
                rows.add(machineRow);
            }

            for (EnergyMonitorRowSnapshot coverRow : createCoverRows(metaTileEntity, base)) {
                rows.add(coverRow);
            }
        }

        BigInteger wirelessEnergy = WirelessNetworkManager.getUserEU(leader);
        rows.sort(ROW_DISPLAY_ORDER);
        return new EnergyMonitorSnapshot(rows, wiredEnergy, wiredCapacity, wirelessEnergy);
    }

    public static EnergyMonitorSummarySnapshot createSummary(EnergyMonitorSnapshot snapshot,
        EnergyMonitorMode totalMode, EnergyMonitorMode statsMode) {
        EnergyMonitorSummarySnapshot summary = EnergyMonitorSummarySnapshot.empty();
        BigInteger wiredStored = snapshot == null ? BigInteger.ZERO : snapshot.getWiredStored();
        BigInteger wiredCapacity = snapshot == null ? BigInteger.ZERO : snapshot.getWiredCapacity();
        BigInteger wirelessEnergy = snapshot == null ? BigInteger.ZERO : snapshot.getWirelessStored();
        BigInteger statisticsTotal = calculateStatisticsTotal(
            snapshot == null ? Collections.emptyList() : snapshot.getRows(),
            statsMode);
        BigInteger totalEnergy = switch (totalMode) {
            case WIRED -> wiredStored;
            case WIRELESS -> wirelessEnergy;
            case ALL -> wiredStored.add(wirelessEnergy);
        };
        summary.setTotalEnergyText(
            formatTotalEnergyText(totalMode, totalEnergy, wiredStored, wiredCapacity, wirelessEnergy));

        BigInteger magnitude = statisticsTotal.abs();
        boolean outputMode = statisticsTotal.signum() < 0;
        int voltageTier = magnitude.signum() == 0 ? 0 : EnergyMonitorFormatter.getVoltageTier(magnitude);
        summary.setAverageEuText(EnergyMonitorFormatter.formatBigInteger(magnitude));
        summary.setAmpText(EnergyMonitorFormatter.formatAmps(magnitude, voltageTier));
        summary.setVoltageTier(voltageTier);
        summary.setOutputMode(outputMode);
        summary.setEstimatedEmpty(outputMode);

        if (magnitude.signum() == 0) {
            summary.setEstimatedTimeText("gtnl.energy_monitor.never_fill");
            return summary;
        }

        if (!outputMode) {
            if (totalMode == EnergyMonitorMode.WIRED) {
                BigInteger remaining = wiredCapacity.subtract(wiredStored)
                    .max(BigInteger.ZERO);
                BigInteger ticks = remaining.divide(magnitude.max(BigInteger.ONE));
                summary.setEstimatedTimeText(EnergyMonitorFormatter.formatDuration(ticks));
            } else {
                summary.setEstimatedTimeText("gtnl.energy_monitor.never_fill");
            }
            return summary;
        }

        BigInteger sourceEnergy = totalMode == EnergyMonitorMode.WIRED ? wiredStored : totalEnergy;
        BigInteger ticks = sourceEnergy.divide(magnitude.max(BigInteger.ONE));
        summary.setEstimatedTimeText(EnergyMonitorFormatter.formatDuration(ticks));
        return summary;
    }

    public static List<EnergyMonitorRowSnapshot> getVisibleRows(List<EnergyMonitorRowSnapshot> rows,
        EnergyMonitorMode statsMode, int visibleRowCount) {
        return getVisibleRowsResult(rows, statsMode, visibleRowCount).getRows();
    }

    public static VisibleRowsResult getVisibleRowsResult(List<EnergyMonitorRowSnapshot> rows,
        EnergyMonitorMode statsMode, int visibleRowCount) {
        if (rows == null || rows.isEmpty()) {
            return new VisibleRowsResult(Collections.emptyList(), false);
        }
        int clampedVisible = Math.max(visibleRowCount, 40);
        List<EnergyMonitorRowSnapshot> visibleRows = new ArrayList<>(clampedVisible);
        boolean hasMoreRows = false;
        for (EnergyMonitorRowSnapshot row : rows) {
            if (!matchesMode(row, statsMode)) {
                continue;
            }
            if (visibleRows.size() >= clampedVisible) {
                hasMoreRows = true;
                break;
            }
            visibleRows.add(row);
        }
        return new VisibleRowsResult(visibleRows, hasMoreRows);
    }

    public static boolean hasMoreRows(List<EnergyMonitorRowSnapshot> rows, EnergyMonitorMode statsMode,
        int visibleRowCount) {
        return getVisibleRowsResult(rows, statsMode, visibleRowCount).hasMoreRows();
    }

    public static BigInteger calculateStatisticsTotal(List<EnergyMonitorRowSnapshot> rows,
        EnergyMonitorMode statsMode) {
        if (rows == null || rows.isEmpty()) {
            return BigInteger.ZERO;
        }
        BigInteger statisticsTotal = BigInteger.ZERO;
        for (EnergyMonitorRowSnapshot row : rows) {
            if (matchesMode(row, statsMode)) {
                statisticsTotal = statisticsTotal.add(row.getEut());
            }
        }
        return statisticsTotal;
    }

    private static String formatTotalEnergyText(EnergyMonitorMode totalMode, BigInteger totalEnergy,
        BigInteger wiredStored, BigInteger wiredCapacity, BigInteger wirelessEnergy) {
        return switch (totalMode) {
            case WIRED -> EnergyMonitorFormatter.formatCompactBigInteger(wiredStored) + " / "
                + EnergyMonitorFormatter.formatCompactBigInteger(wiredCapacity)
                + " EU ("
                + EnergyMonitorFormatter.formatPercentage(wiredStored, wiredCapacity)
                + ")";
            case WIRELESS -> EnergyMonitorFormatter.formatCompactBigInteger(wirelessEnergy) + " EU";
            case ALL -> EnergyMonitorFormatter.formatCompactBigInteger(wiredStored) + " + "
                + EnergyMonitorFormatter.formatCompactBigInteger(wirelessEnergy)
                + " EU";
        };
    }

    private static List<EnergyMonitorRowSnapshot> createMachineRows(MetaTileEntity metaTileEntity,
        IGregTechTileEntity base) {
        if (metaTileEntity instanceof MTEBasicMachine basicMachine) {
            EnergyMonitorRowSnapshot row = buildRow(
                metaTileEntity,
                base,
                BigInteger.valueOf(-basicMachine.mEUt),
                EnergyMonitorCategory.BASIC_MACHINE,
                false,
                getMachineDisplayStack(metaTileEntity));
            return row == null ? Collections.emptyList() : List.of(row);
        }
        if (metaTileEntity instanceof MTELapotronicSuperCapacitor capacitor) {
            BigInteger eut = resolveLapotronicSuperCapacitorEut(base, capacitor);
            EnergyMonitorRowSnapshot row = buildRow(
                metaTileEntity,
                base,
                eut,
                EnergyMonitorCategory.MULTIBLOCK,
                capacitor.isWireless_mode(),
                getMachineDisplayStack(metaTileEntity));
            return row == null ? Collections.emptyList() : List.of(row);
        }
        if (metaTileEntity instanceof MTEMultiBlockBase multiblock) {
            List<EnergyMonitorRowSnapshot> rows = new ArrayList<>();
            boolean wireless = isWirelessMachine(metaTileEntity);
            ItemStack displayStack = getMachineDisplayStack(metaTileEntity);
            EnergyMonitorRowSnapshot machineRow = buildRow(
                metaTileEntity,
                base,
                resolveMultiblockMachineEut(multiblock),
                EnergyMonitorCategory.MULTIBLOCK,
                wireless,
                displayStack);
            if (machineRow != null) {
                rows.add(machineRow);
            }
            if (metaTileEntity instanceof MTEExtendedPowerMultiBlockBase<?>extendedPowerMultiBlockBase) {
                EnergyMonitorRowSnapshot hatchRow = buildRow(
                    metaTileEntity,
                    base,
                    BigInteger.valueOf(extendedPowerMultiBlockBase.lEUt),
                    EnergyMonitorCategory.HATCH,
                    wireless,
                    displayStack);
                if (hatchRow != null && !isDuplicateMultiblockRow(machineRow, hatchRow)) {
                    rows.add(hatchRow);
                }
            }
            return rows;
        }
        if (metaTileEntity instanceof MTEHatchWirelessMulti wirelessInput) {
            BigInteger eut = BigInteger.valueOf(-1L)
                .multiply(BigInteger.valueOf((long) wirelessInput.getAmperes() * GTValues.V[wirelessInput.mTier]));
            EnergyMonitorRowSnapshot row = buildRow(
                metaTileEntity,
                base,
                eut,
                EnergyMonitorCategory.HATCH,
                true,
                getMachineDisplayStack(metaTileEntity));
            return row == null ? Collections.emptyList() : List.of(row);
        }
        if (metaTileEntity instanceof MTEHatchWirelessDynamoMulti wirelessDynamo) {
            BigInteger eut = BigInteger.valueOf((long) wirelessDynamo.Amperes * GTValues.V[wirelessDynamo.mTier]);
            EnergyMonitorRowSnapshot row = buildRow(
                metaTileEntity,
                base,
                eut,
                EnergyMonitorCategory.HATCH,
                true,
                getMachineDisplayStack(metaTileEntity));
            return row == null ? Collections.emptyList() : List.of(row);
        }
        if (metaTileEntity instanceof MTEWirelessEnergy wirelessEnergy) {
            BigInteger eut = BigInteger.valueOf(-2L * GTValues.V[wirelessEnergy.mTier]);
            EnergyMonitorRowSnapshot row = buildRow(
                metaTileEntity,
                base,
                eut,
                EnergyMonitorCategory.HATCH,
                true,
                getMachineDisplayStack(metaTileEntity));
            return row == null ? Collections.emptyList() : List.of(row);
        }
        if (metaTileEntity instanceof MTEHatch) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    private static BigInteger resolveLapotronicSuperCapacitorEut(IGregTechTileEntity base,
        MTELapotronicSuperCapacitor capacitor) {
        if (base == null || !base.isActive()) {
            return BigInteger.ZERO;
        }
        return BigInteger.valueOf(
            capacitor.getEnergyInputValues()
                .avgLong())
            .subtract(
                BigInteger.valueOf(
                    capacitor.getEnergyOutputValues()
                        .avgLong()));
    }

    private static List<EnergyMonitorRowSnapshot> createCoverRows(MetaTileEntity metaTileEntity,
        IGregTechTileEntity base) {
        if (!(base instanceof ICoverable coverable)) {
            return Collections.emptyList();
        }
        List<EnergyMonitorRowSnapshot> rows = new ArrayList<>();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (!(coverable.getCoverAtSide(side) instanceof WirelessMultiEnergyCover wirelessCover)) {
                continue;
            }
            BigInteger eut = BigInteger.valueOf(
                -wirelessCover.transferredEnergyPerOperation / WirelessNetworkManager.ticks_between_energy_addition);
            ItemStack coverStack = coverable.getCoverItemAtSide(side);
            EnergyMonitorRowSnapshot row = buildRow(
                metaTileEntity,
                base,
                eut,
                EnergyMonitorCategory.COVER,
                true,
                coverStack == null ? null : coverStack.copy());
            if (row == null) {
                continue;
            }
            if (coverStack != null && coverStack.getDisplayName() != null) {
                row.setDisplayName(coverStack.getDisplayName());
            }
            rows.add(row);
        }
        return rows;
    }

    private static EnergyMonitorRowSnapshot buildRow(MetaTileEntity metaTileEntity, IGregTechTileEntity base,
        BigInteger eut, EnergyMonitorCategory category, boolean wireless, ItemStack iconStack) {
        if (eut == null || eut.signum() == 0) {
            return null;
        }
        EnergyMonitorRowSnapshot row = new EnergyMonitorRowSnapshot();
        row.setIconStack(iconStack);
        row.setDisplayName(resolveDisplayName(metaTileEntity, iconStack));
        row.setOwnerName(resolveOwnerName(base));
        row.setEut(eut);
        row.setCategory(category);
        row.setWireless(wireless);
        row.setHighlightTarget(
            new EnergyMonitorHighlightTarget(
                base.getWorld().provider.dimensionId,
                base.getXCoord(),
                base.getYCoord(),
                base.getZCoord()));
        return row;
    }

    private static boolean isDuplicateMultiblockRow(EnergyMonitorRowSnapshot machineRow,
        EnergyMonitorRowSnapshot hatchRow) {
        return machineRow != null && hatchRow != null && machineRow.sameAs(hatchRow);
    }

    private static String resolveDisplayName(MetaTileEntity metaTileEntity, ItemStack iconStack) {
        if (iconStack != null && iconStack.getDisplayName() != null) {
            return iconStack.getDisplayName();
        }
        ItemStack machineStack = getMachineDisplayStack(metaTileEntity);
        if (machineStack != null && machineStack.getDisplayName() != null) {
            return machineStack.getDisplayName();
        }
        return metaTileEntity.getLocalName();
    }

    private static ItemStack getMachineDisplayStack(MetaTileEntity metaTileEntity) {
        try {
            return metaTileEntity.getStackForm(1L);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static String resolveOwnerName(IGregTechTileEntity base) {
        String ownerName = base.getOwnerName();
        if (ownerName != null && !ownerName.isEmpty()) {
            return ownerName;
        }
        UUID ownerUuid = base.getOwnerUuid();
        return ownerUuid == null ? "" : ownerUuid.toString();
    }

    private static BigInteger resolveMultiblockMachineEut(MTEMultiBlockBase multiblock) {
        BigInteger specialEut = resolveSpecialMultiblockMachineEut(multiblock);
        if (specialEut != null) {
            return specialEut;
        }
        return BigInteger.valueOf(multiblock.mEUt);
    }

    private static BigInteger resolveSpecialMultiblockMachineEut(MTEMultiBlockBase multiblock) {
        if (multiblock instanceof EnergyMonitorCustomWirelessEutProvider customWirelessEutProvider) {
            return customWirelessEutProvider.getEnergyMonitorWirelessEut();
        }
        if (multiblock instanceof WirelessEnergyMultiMachineBase<?>wirelessMultiBlock
            && wirelessMultiBlock.wirelessMode) {
            return resolveWirelessCycleEut(wirelessMultiBlock.costingEU, wirelessMultiBlock.totalOverclockedDuration);
        }
        if (multiblock instanceof GrandAssemblyLine grandAssemblyLine && grandAssemblyLine.wirelessMode) {
            return resolveWirelessCycleEut(
                parseFormattedBigInteger(grandAssemblyLine.costingEUText),
                grandAssemblyLine.maxProgresstime());
        }
        if (multiblock instanceof AssemblerMatrix assemblerMatrix && assemblerMatrix.wirelessMode) {
            return resolveWirelessCycleEut(
                parseFormattedBigInteger(assemblerMatrix.costingEUText),
                assemblerMatrix.maxProgresstime());
        }
        if (multiblock instanceof SuperSpaceElevator superSpaceElevator && superSpaceElevator.wirelessMode) {
            return resolveWirelessCycleEut(
                parseFormattedBigInteger(superSpaceElevator.costingEUText),
                superSpaceElevator.maxProgresstime());
        }
        if (multiblock instanceof SupercomputingCenter supercomputingCenter
            && supercomputingCenter.energyWirelessMode) {
            return resolveSupercomputingCenterWirelessEut(supercomputingCenter);
        }
        if (multiblock instanceof EternalGregTechWorkshop eternalGregTechWorkshop) {
            return resolveEternalGregTechWorkshopEut(eternalGregTechWorkshop);
        }
        if (multiblock instanceof ETGWEyeOfHarmonyModule eyeOfHarmonyModule) {
            return resolveEyeOfHarmonyModuleEut(eyeOfHarmonyModule);
        }
        if (multiblock instanceof EternalGregTechWorkshopModule eternalGregTechWorkshopModule) {
            return resolveEternalGregTechWorkshopModuleEut(eternalGregTechWorkshopModule);
        }
        if (multiblock instanceof FOGAlloySmelterModule alloySmelterModule) {
            return BigInteger.valueOf(-alloySmelterModule.EUt);
        }
        if (multiblock instanceof FOGAlloyBlastSmelterModule alloyBlastSmelterModule) {
            return BigInteger.valueOf(-alloyBlastSmelterModule.EUt);
        }
        if (multiblock instanceof FOGExtractorModule extractorModule) {
            return BigInteger.valueOf(-extractorModule.EUt);
        }
        if (multiblock instanceof FOGSolarMuonCatalystModule solarMuonCatalystModule) {
            return BigInteger.valueOf(-solarMuonCatalystModule.EUt);
        }
        if (multiblock instanceof SingularityDataHub singularityDataHub && singularityDataHub.wirelessMode) {
            return BigInteger.valueOf(-singularityDataHub.getActualEnergyUsage());
        }
        if (multiblock instanceof WhiteNightGenerator whiteNightGenerator) {
            BigInteger totalGenerated = BigInteger.valueOf(whiteNightGenerator.currentOutputEU)
                .multiply(BigInteger.valueOf(Integer.MAX_VALUE));
            return resolveGeneratedCycleEut(totalGenerated, whiteNightGenerator.maxProgresstime());
        }
        if (multiblock instanceof RealArtificialStar realArtificialStar) {
            return resolveRealArtificialStarEut(realArtificialStar);
        }
        if (multiblock instanceof AdvancedHyperNaquadahReactor advancedHyperNaquadahReactor
            && advancedHyperNaquadahReactor.wirelessMode) {
            return resolveGeneratedCycleEut(
                advancedHyperNaquadahReactor.bigEUt,
                advancedHyperNaquadahReactor.maxProgresstime());
        }
        if (multiblock instanceof UEVTier fusionReactor && fusionReactor.wirelessMode) {
            return resolveWirelessCycleEut(fusionReactor.costingEU, fusionReactor.totalOverclockedDuration);
        }
        if (multiblock instanceof EnergyInfuser energyInfuser && energyInfuser.wirelessMode) {
            return BigInteger.ZERO;
        }
        if (multiblock instanceof MTEPurificationPlant purificationPlant
            && purificationPlant instanceof IWirelessMode wirelessMode
            && wirelessMode.isGtnl$wirelessMode()) {
            return resolveWirelessCycleEut(
                ((ICostingEUHolder) purificationPlant).getGtnl$costingEU(),
                purificationPlant.maxProgresstime());
        }
        return null;
    }

    private static BigInteger resolveWirelessCycleEut(BigInteger totalCost, int durationTicks) {
        if (totalCost == null || totalCost.signum() <= 0 || durationTicks <= 0) {
            return BigInteger.ZERO;
        }
        return totalCost.divide(BigInteger.valueOf(durationTicks))
            .negate();
    }

    private static BigInteger resolveGeneratedCycleEut(BigInteger totalGenerated, int durationTicks) {
        if (totalGenerated == null || totalGenerated.signum() <= 0 || durationTicks <= 0) {
            return BigInteger.ZERO;
        }
        return totalGenerated.divide(BigInteger.valueOf(durationTicks));
    }

    private static BigInteger resolveSupercomputingCenterWirelessEut(SupercomputingCenter supercomputingCenter) {
        if (supercomputingCenter.overclock == null || supercomputingCenter.overvolt == null) {
            return BigInteger.ZERO;
        }
        double overclock = supercomputingCenter.overclock.get();
        double overvolt = supercomputingCenter.overvolt.get();
        if (!Double.isFinite(overclock) || !Double.isFinite(overvolt)) {
            return BigInteger.ZERO;
        }
        long eut = (long) Math.max(GTValues.V[7], GTValues.V[7] * overclock * overvolt);
        return BigInteger.valueOf(-eut);
    }

    private static BigInteger resolveRealArtificialStarEut(RealArtificialStar realArtificialStar) {
        if (realArtificialStar.currentOutputEU == null || realArtificialStar.currentOutputEU.signum() <= 0) {
            return BigInteger.ZERO;
        }
        BigDecimal totalGenerated = new BigDecimal(realArtificialStar.currentOutputEU)
            .multiply(BigDecimal.valueOf(realArtificialStar.outputMultiplier))
            .multiply(BigDecimal.valueOf((realArtificialStar.rewardContinuous + 100D) / 100D))
            .multiply(BigDecimal.valueOf(Integer.MAX_VALUE));
        return resolveGeneratedCycleEut(totalGenerated.toBigInteger(), realArtificialStar.maxProgresstime());
    }

    private static BigInteger resolveEternalGregTechWorkshopEut(EternalGregTechWorkshop eternalGregTechWorkshop) {
        if (eternalGregTechWorkshop.mMaxProgresstime <= 0 || eternalGregTechWorkshop.moduleHatches == null
            || eternalGregTechWorkshop.moduleHatches.isEmpty()) {
            return BigInteger.ZERO;
        }
        return BigInteger.valueOf((long) eternalGregTechWorkshop.moduleHatches.size() * Integer.MAX_VALUE)
            .divide(BigInteger.valueOf(eternalGregTechWorkshop.mMaxProgresstime))
            .negate();
    }

    private static BigInteger resolveEternalGregTechWorkshopModuleEut(
        EternalGregTechWorkshopModule eternalGregTechWorkshopModule) {
        return BigInteger.valueOf(-eternalGregTechWorkshopModule.EUt);
    }

    private static BigInteger resolveEyeOfHarmonyModuleEut(ETGWEyeOfHarmonyModule eyeOfHarmonyModule) {
        if (eyeOfHarmonyModule.mMaxProgresstime <= 0) {
            return BigInteger.ZERO;
        }
        return eyeOfHarmonyModule.outputEU_BigInt.subtract(eyeOfHarmonyModule.usedEU.abs())
            .divide(BigInteger.valueOf(eyeOfHarmonyModule.mMaxProgresstime));
    }

    private static BigInteger parseFormattedBigInteger(String formattedValue) {
        if (formattedValue == null || formattedValue.isEmpty()) {
            return BigInteger.ZERO;
        }
        try {
            return new BigInteger(formattedValue.replace(",", ""));
        } catch (NumberFormatException ignored) {
            return BigInteger.ZERO;
        }
    }

    private static boolean isWirelessMachine(MetaTileEntity metaTileEntity) {
        if (metaTileEntity instanceof WirelessEnergyMultiMachineBase<?>wirelessMultiBlock) {
            return wirelessMultiBlock.wirelessMode;
        }
        if (metaTileEntity instanceof GrandAssemblyLine grandAssemblyLine) {
            return grandAssemblyLine.wirelessMode;
        }
        if (metaTileEntity instanceof AssemblerMatrix assemblerMatrix) {
            return assemblerMatrix.wirelessMode;
        }
        if (metaTileEntity instanceof SingularityDataHub singularityDataHub) {
            return singularityDataHub.wirelessMode;
        }
        if (metaTileEntity instanceof SupercomputingCenter supercomputingCenter) {
            return supercomputingCenter.energyWirelessMode;
        }
        if (metaTileEntity instanceof SuperSpaceElevator superSpaceElevator) {
            return superSpaceElevator.wirelessMode;
        }
        if (metaTileEntity instanceof EternalGregTechWorkshop || metaTileEntity instanceof EternalGregTechWorkshopModule
            || metaTileEntity instanceof MTEBaseModule) {
            return true;
        }
        if (metaTileEntity instanceof WhiteNightGenerator whiteNightGenerator) {
            return whiteNightGenerator.wirelessMode;
        }
        if (metaTileEntity instanceof RealArtificialStar) {
            return true;
        }
        if (metaTileEntity instanceof AdvancedHyperNaquadahReactor advancedHyperNaquadahReactor) {
            return advancedHyperNaquadahReactor.wirelessMode;
        }
        if (metaTileEntity instanceof UEVTier fusionReactor) {
            return fusionReactor.wirelessMode;
        }
        if (metaTileEntity instanceof EnergyInfuser energyInfuser) {
            return energyInfuser.wirelessMode;
        }
        if (metaTileEntity instanceof MTEPurificationPlant purificationPlant
            && purificationPlant instanceof IWirelessMode wirelessMode) {
            return wirelessMode.isGtnl$wirelessMode();
        }
        return metaTileEntity instanceof MTEHatchWirelessMulti || metaTileEntity instanceof MTEHatchWirelessDynamoMulti
            || metaTileEntity instanceof MTEWirelessEnergy;
    }

    private static boolean matchesMode(EnergyMonitorRowSnapshot row, EnergyMonitorMode mode) {
        return switch (mode) {
            case WIRED -> !row.isWireless();
            case WIRELESS -> row.isWireless();
            case ALL -> true;
        };
    }

    public static class VisibleRowsResult {

        private final List<EnergyMonitorRowSnapshot> rows;
        private final boolean hasMoreRows;

        public VisibleRowsResult(List<EnergyMonitorRowSnapshot> rows, boolean hasMoreRows) {
            this.rows = rows == null ? Collections.emptyList() : rows;
            this.hasMoreRows = hasMoreRows;
        }

        public List<EnergyMonitorRowSnapshot> getRows() {
            return rows;
        }

        public boolean hasMoreRows() {
            return hasMoreRows;
        }
    }
}
