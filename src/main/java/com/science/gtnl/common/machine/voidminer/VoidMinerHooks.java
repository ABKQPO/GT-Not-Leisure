package com.science.gtnl.common.machine.voidminer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import bwcrossmod.galacticgreg.VoidMinerUtility;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.GTValues;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import gtneioreplugin.util.DimensionHelper;

public class VoidMinerHooks {

    public static IVoidMinerHost host(MTEVoidMinerBase<?> miner) {
        return (IVoidMinerHost) miner;
    }

    public static String resolveDimensionKey(MTEVoidMinerBase<?> miner) {
        try {
            return Optional.ofNullable(miner.mInventory[1])
                .filter(s -> s.getItem() instanceof ItemDimensionDisplay)
                .map(ItemDimensionDisplay::getDimension)
                .orElse("None");
        } catch (Exception e) {
            ScienceNotLeisure.LOG.debug("[VMTweakMixin] Failed to read dimension override slot.", e);
            return "None";
        }
    }

    public static Optional<String> resolveDimensionNameOverride(MTEVoidMinerBase<?> miner) {
        String dimensionKey = resolveDimensionKey(miner);
        if ("None".equals(dimensionKey)) return Optional.empty();

        return toInternalDimensionName(dimensionKey).or(() -> {
            Integer dimensionId = VMTweakHelper.DIM_MAPPING.getDimensionId(dimensionKey);
            if (dimensionId == null) return Optional.empty();
            return toInternalDimensionName(VMTweakHelper.DIM_MAPPING.getDimensionName(dimensionId));
        })
            .or(() -> Optional.of(dimensionKey));
    }

    public static Optional<String> toInternalDimensionName(String dimensionName) {
        if (dimensionName == null || dimensionName.isEmpty()) return Optional.empty();

        String internalName = DimensionHelper.ABBR_TO_INTERNAL.get(dimensionName);
        if (internalName != null) return Optional.of(internalName);

        int fullNameIndex = DimensionHelper.ALL_DIM_NAMES.indexOf(dimensionName);
        if (fullNameIndex >= 0) {
            String abbr = DimensionHelper.ALL_DISPLAYED_NAMES.get(fullNameIndex);
            return Optional.ofNullable(DimensionHelper.ABBR_TO_INTERNAL.get(abbr));
        }

        if (VoidMinerUtility.dropMapsByDimName.containsKey(dimensionName)) return Optional.of(dimensionName);
        return Optional.empty();
    }

    public static void refreshDimensionOverride(MTEVoidMinerBase<?> miner) {
        String dimensionKey = resolveDimensionKey(miner);
        if (!host(miner).gtnl$state()
            .isOverride(dimensionKey)) {
            recalculateDropMap(miner);
        }
    }

    public static void recalculateDropMap(MTEVoidMinerBase<?> miner) {
        IVoidMinerHost host = host(miner);
        VoidMinerState state = host.gtnl$state();
        miner.dropMap = null;
        miner.extraDropMap = null;
        host.gtnl$setTotalWeight(0);
        host.gtnl$setCanVoidMine(false);
        state.warning = "";

        state.setLastDimensionOverride(resolveDimensionKey(miner));
        Optional<String> dimensionNameOverride = resolveDimensionNameOverride(miner);
        boolean hasOverride = dimensionNameOverride.isPresent();

        if (miner.getBaseMetaTileEntity() != null) {
            host.gtnl$setDimensionDef(
                DimensionDef.getDefForWorld(
                    miner.getBaseMetaTileEntity()
                        .getWorld()));
        }

        if (hasOverride) {
            String dimensionName = dimensionNameOverride.get();
            if (VoidMinerUtility.dropMapsByDimName.containsKey(dimensionName)) {
                host.gtnl$setCanVoidMine(true);
                setDropMaps(miner, dimensionName);
                resizeSelected(miner);
                return;
            }
            state.warning = "vmtweak.gui.override.error";
        }

        if (host.gtnl$dimensionDef() == null || !host.gtnl$dimensionDef()
            .canBeVoidMined()) {
            miner.dropMap = new VoidMinerUtility.DropMap();
            miner.extraDropMap = new VoidMinerUtility.DropMap();
            if (state.clearDropMapDimension()) miner.selected.setSize(0);
            resizeSelected(miner);
            return;
        }

        host.gtnl$setCanVoidMine(true);
        setDropMaps(
            miner,
            host.gtnl$dimensionDef()
                .getDimensionName());

        if (hasOverride) {
            state.warning = host.gtnl$totalWeight() > 0 ? "vmtweak.gui.override.failed" : "vmtweak.gui.override.error";
        }
        resizeSelected(miner);
    }

    public static void setDropMaps(MTEVoidMinerBase<?> miner, String dimensionName) {
        IVoidMinerHost host = host(miner);
        VoidMinerState state = host.gtnl$state();
        boolean dimensionChanged = state.switchDropMapDimension(dimensionName);
        miner.dropMap = VoidMinerUtility.dropMapsByDimName.getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        miner.extraDropMap = VoidMinerUtility.extraDropsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        miner.dropMap.isDistributionCached(miner.extraDropMap);
        host.gtnl$setTotalWeight(miner.dropMap.getTotalWeight() + miner.extraDropMap.getTotalWeight());
        if (dimensionChanged) resetSelected(miner);
    }

    public static void resizeSelected(MTEVoidMinerBase<?> miner) {
        if (miner.dropMap == null || miner.selected == null) return;
        var ores = miner.dropMap.getOres();
        if (ores == null) return;
        int oreCount = ores.length;
        if (miner.selected.getSlots() < oreCount) {
            miner.selected.setSize(oreCount);
        }
    }

    public static void resetSelected(MTEVoidMinerBase<?> miner) {
        if (miner.dropMap == null || miner.selected == null) return;
        var ores = miner.dropMap.getOres();
        miner.selected.setSize(ores == null ? 0 : ores.length);
    }

    public static String copyDimension(VoidMinerState state) {
        return state.activeDropMapDimension.isEmpty() ? "unknown" : state.activeDropMapDimension;
    }

    public static NBTTagCompound copiedData(MTEVoidMinerBase<?> miner) {
        recalculateDropMap(miner);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", MTEVoidMinerBase.COPIED_DATA_IDENTIFIER);
        tag.setString("dimension", copyDimension(host(miner).gtnl$state()));
        tag.setTag("selected", miner.selected.serializeNBT());
        tag.setBoolean("blacklist", miner.blacklist);
        return tag;
    }

    public static boolean pasteCopiedData(MTEVoidMinerBase<?> miner, NBTTagCompound nbt) {
        recalculateDropMap(miner);
        if (nbt == null || !MTEVoidMinerBase.COPIED_DATA_IDENTIFIER.equals(nbt.getString("type"))) return false;
        if (!copyDimension(host(miner).gtnl$state()).equals(nbt.getString("dimension"))) return false;

        miner.selected.deserializeNBT(nbt.getCompoundTag("selected"));
        miner.blacklist = nbt.getBoolean("blacklist");
        return true;
    }

    public static boolean drainEnergyInput(long aEU, List<MTEHatchEnergy> energyHatches,
        List<MTEHatch> exoticEnergyHatches) {
        if (aEU <= 0) return true;

        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(energyHatches)) {
            long tDrain = Math.min(
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU(),
                aEU);
            tHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(tDrain, false);
            aEU -= tDrain;

            if (aEU <= 0) return true;
        }
        for (MTEHatch tHatch : GTUtility.validMTEList(exoticEnergyHatches)) {
            long tDrain = Math.min(
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU(),
                aEU);
            tHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(tDrain, false);
            aEU -= tDrain;

            if (aEU <= 0) return true;
        }

        return false;
    }

    public static void setElectricityStats(MTEVoidMinerBase<?> miner, boolean batchMode) {
        IVoidMinerHost host = host(miner);
        VoidMinerState state = host.gtnl$state();
        state.lEUt = -Math.abs(Math.toIntExact(GTValues.V[host.gtnl$minTier()]));
        long useEU = miner.getMaxInputEu();

        if (batchMode) {
            miner.mMaxProgresstime = 128;
        } else {
            GTNLOverclockCalculator calculator = new GTNLOverclockCalculator().setEUt(useEU)
                .setRecipeEUt(-state.lEUt)
                .setDuration(10)
                .setParallel(1);
            if (host.gtnl$tierMultiplier() == 3) calculator = calculator.enablePerfectOC();

            calculator = calculator.calculate();
            miner.mMaxProgresstime = calculator.getDuration();
        }

        miner.mOutputItems = GTValues.emptyItemStackArray;
        miner.mProgresstime = 0;
        miner.mEfficiency = miner.getCurrentEfficiency(null);
        miner.mEfficiencyIncrease = 10000;
        state.lEUt = useEU > 0 ? -useEU : useEU;
    }

    public static void handleOutputs(MTEVoidMinerBase<?> miner, boolean batchMode) {
        IVoidMinerHost host = host(miner);
        List<ItemStack> inputOres = miner.getStoredInputs()
            .stream()
            .filter(GTUtility::isOre)
            .toList();
        ItemStack output = host.gtnl$nextOre();

        GTNLOverclockCalculator calculator = new GTNLOverclockCalculator().setEUt(miner.getMaxInputEu())
            .setRecipeEUt(Math.abs(Math.toIntExact(GTValues.V[host.gtnl$minTier()])))
            .setDuration(10 * (batchMode ? 16 : 1))
            .setParallel(1);
        if (host.gtnl$tierMultiplier() == 3) calculator = calculator.enablePerfectOC();
        calculator = calculator.calculate();

        double parallel = calculator.calculateMultiplierUnderOneTick();
        if (batchMode) {
            double multiplierParallel = 128d / calculator.getDuration();
            parallel = (int) Math.max(1, parallel * multiplierParallel);
        }

        long totalCount = (long) (host.gtnl$multiplier() * parallel);
        if (totalCount <= 0) return;

        if (keepsOutput(inputOres, miner.selected.getStacks(), miner.blacklist, output)) {
            while (totalCount > 0) {
                int stackSize = (int) Math.min(Integer.MAX_VALUE, totalCount);
                ItemStack stackPart = output.copy();
                stackPart.stackSize = stackSize;
                miner.addOutputPartial(stackPart);
                totalCount -= stackSize;
            }
        }

        miner.updateSlots();
    }

    public static boolean keepsOutput(List<ItemStack> inputOres, List<ItemStack> selected, boolean blacklist,
        ItemStack output) {
        List<ItemStack> filter = Stream.concat(
            inputOres.stream(),
            selected.stream()
                .filter(Objects::nonNull))
            .toList();
        return filter.isEmpty() || blacklist != filter.stream()
            .anyMatch(is -> GTUtility.areStacksEqual(is, output));
    }

    public static void checkAmperage(List<StructureError> errors, List<MTEHatchEnergy> energyHatches,
        List<MTEHatch> exoticEnergyHatches) {
        long amp = 0;
        for (MTEHatch tHatch : GTUtility.validMTEList(energyHatches)) {
            amp += tHatch.maxWorkingAmperesIn();
        }
        for (MTEHatch tHatch : GTUtility.validMTEList(exoticEnergyHatches)) {
            amp += tHatch.maxWorkingAmperesIn();
        }
        if (amp > 256) {
            errors.add(GTNLStructureErrors.energyInputAmperageTooHigh());
        }
    }

    public static MultiblockTooltipBuilder tooltip(byte tierMultiplier, int minTier) {
        int[] structureBlock = switch (tierMultiplier) {
            case 2 -> new int[] { 9, 13, 8 };
            case 3 -> new int[] { 9, 16, 9 };
            default -> new int[] { 7, 9, 7 };
        };
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.void_miner.recipe_type"))
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo(
                StatCollector.translateToLocalFormatted(
                    "gtnl.machine.void_miner.info.0",
                    NumberFormatUtil.formatNumber(GTValues.V[minTier])))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.5"))
            .addInfo(StatCollector.translateToLocalFormatted("gtnl.machine.void_miner.info.6", tierMultiplier * 2))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.7"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.9"));

        if (tierMultiplier == 3) tt.addPerfectOCInfo();

        tt.addSupportAny()
            .beginStructureBlock(structureBlock[0], structureBlock[1], structureBlock[2], false);

        switch (tierMultiplier) {
            case 2 -> tt.addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.2.casing.0"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.2.casing.1"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.2.casing.2"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.2.casing.3"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.2.casing.4"));
            case 3 -> tt.addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.3.casing.0"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.3.casing.1"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.3.casing.2"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.3.casing.3"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.3.casing.4"));
            default -> tt.addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.1.casing.0"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.1.casing.1"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.1.casing.2"))
                .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.tier.1.casing.3"));
        }

        tt.addEnergyHatch(
            "0+",
            StatCollector.translateToLocalFormatted("gtnl.machine.void_miner.casing.0", GTValues.VN[minTier]))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.1"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.2"))
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.3"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.1"))
            .toolTipFinisher();
        return tt;
    }
}
