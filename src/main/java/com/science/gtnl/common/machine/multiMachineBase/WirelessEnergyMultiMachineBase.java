package com.science.gtnl.common.machine.multiMachineBase;

import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.api.IWirelessEnergy;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.mixins.early.gregtech.AccessorProcessingLogic;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.utils.structure.MultiblockTooltipReader;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.tooltip.TooltipHelper;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class WirelessEnergyMultiMachineBase<T extends WirelessEnergyMultiMachineBase<T>>
    extends MultiMachineBase<T> implements IWirelessEnergy {

    // Why: GT ProcessingLogic's default overclock ratio, which enablePerfectOverclock overwrites for good.
    private static final double NORMAL_DURATION_DECREASE = 2.0, NORMAL_POWER_INCREASE = 4.0;
    private static final String WIRELESS_MODE_LINE_KEY = "gtnl.machine.common.wireless.mode";
    private static final String WIRELESS_MODE_NAME_KEY = "gtnl.structure.tooltip.wireless_mode";
    private static final int WIRELESS_VOLTAGE_TIER_OFFSET = 1;
    private static final long WIRELESS_BASE_AMPERAGE = 8;
    private static final long WIRELESS_AMPERAGE_RESERVE = 2;
    // Why: four tiers tell a steady step apart from other curves, as for the other tooltip probes.
    private static final int WIRELESS_POWER_PROBES = 4;

    public UUID ownerUUID;
    @Getter
    public boolean wirelessMode;
    @Getter
    @Setter
    public boolean wirelessUpgrade = false;
    public int totalOverclockedDuration = 0;
    public int maxParallelStored = -1;
    public boolean isRecipeProcessing = false;
    public BigInteger costingEU = BigInteger.ZERO;
    public String costingEUText = Utils.ZERO_STRING;
    public int cycleNum = 100_000;
    public int cycleNow = 0;

    public WirelessEnergyMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public WirelessEnergyMultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        wirelessMode = false;
    }

    @Override
    public void startRecipeProcessing() {
        isRecipeProcessing = true;
        super.startRecipeProcessing();
    }

    @Override
    public void endRecipeProcessing() {
        super.endRecipeProcessing();
        isRecipeProcessing = false;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        resetParallelTier();
        setWirelessMode(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty());
    }

    @Override
    public void resetParallelTier() {
        super.resetParallelTier();
        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            break;
        }
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > wirelessModeVoltage() * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        maxParallelStored = -1;
        resetParallelTier();
        costingEU = BigInteger.ZERO;
        costingEUText = Utils.ZERO_STRING;
        totalOverclockedDuration = 0;
        cycleNow = 0;
        if (!wirelessMode) return super.checkProcessing();

        maxParallelStored = getTrueParallel();
        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (cycleNow = 0; cycleNow < cycleNum; cycleNow++) {
            CheckRecipeResult result = wirelessModeProcessOnce(null);
            if (!result.wasSuccessful()) {
                finalResult = result;
                break;
            }
            succeeded = true;
            if (maxParallelStored <= -1) {
                finalResult = result;
                break;
            }
        }

        if (!succeeded) {
            maxParallelStored = -1;
            return finalResult;
        }
        updateSlots();
        costingEUText = NumberFormatUtil.formatNumber(costingEU);
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = totalOverclockedDuration;
        maxParallelStored = -1;
        lEUt = 0;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public CheckRecipeResult wirelessModeProcessOnce(ItemStack stack) {
        if (!isRecipeProcessing) startRecipeProcessing();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        if (!result.wasSuccessful()) {
            return result;
        }

        BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf(processingLogic.getDuration()));
        if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(Utils.NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
        }

        costingEU = costingEU.add(costEU);
        mOutputItems = Utils.mergeArray(mOutputItems, processingLogic.getOutputItems());
        mOutputFluids = Utils.mergeArray(mOutputFluids, processingLogic.getOutputFluids());
        totalOverclockedDuration += processingLogic.getDuration();
        maxParallelStored = maxParallelStored - processingLogic.getCurrentParallels();

        endRecipeProcessing();
        return result;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        setOverclockRatio(logic);
        if (wirelessMode) {
            logic.setAvailableVoltage(appliesMachineVoltageLimit() ? getMachineVoltageLimit() : wirelessModeVoltage());
            logic.setAvailableAmperage((WIRELESS_BASE_AMPERAGE << (2 * mParallelTier)) - WIRELESS_AMPERAGE_RESERVE);
            logic.setAmperageOC(true);
            return;
        }
        logic.setAvailableVoltage(appliesMachineVoltageLimit() ? getLimitedInputEu() : getMaxInputEu());
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(true);
    }

    // Why: enablePerfectOverclock stays on the logic, so leaving wireless mode has to set the normal ratio back.
    protected void setOverclockRatio(ProcessingLogic logic) {
        if (wirelessMode || getPerfectOC()) logic.enablePerfectOverclock();
        else logic.setOverclock(NORMAL_DURATION_DECREASE, NORMAL_POWER_INCREASE);
    }

    @Override
    public int getMaxTierSkip() {
        return UNLIMITED_TIER_SKIPS;
    }

    protected boolean appliesMachineVoltageLimit() {
        return false;
    }

    private long getLimitedInputEu() {
        long limit = getMachineVoltageLimit();
        return Math.max(limitedEu(mEnergyHatches, limit), limitedEu(mExoticEnergyHatches, limit));
    }

    private static long limitedEu(Collection<? extends MTEHatch> hatches, long voltageLimit) {
        long eu = 0;
        for (MTEHatch hatch : GTUtility.validMTEList(hatches)) {
            long voltage = Math.min(
                hatch.getBaseMetaTileEntity()
                    .getInputVoltage(),
                voltageLimit);
            eu = GTUtility.addSafe(eu, GTUtility.mulSafe(voltage, hatch.maxWorkingAmperesIn()));
        }
        return eu;
    }

    @Override
    public String[] getInfoData() {
        List<String> infoData = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode) {
            infoData.add(
                IGregTechDeviceInformation.encode("gtnl.waila.wireless.mode.format", EnumChatFormatting.LIGHT_PURPLE));
            infoData.add(
                IGregTechDeviceInformation.encode(
                    "gtnl.waila.wireless.current_eu_cost.format",
                    EnumChatFormatting.AQUA,
                    EnumChatFormatting.RESET,
                    EnumChatFormatting.GOLD,
                    costingEUText,
                    EnumChatFormatting.RESET));
        }
        return infoData.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessUpgrade")) {
            currentTip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("gtnl.waila.wireless.upgrade"));
        }
        if (tag.getBoolean("wirelessMode")) {
            currentTip
                .add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("gtnl.waila.wireless.mode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.wireless.current_eu_cost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("wirelessUpgrade", wirelessUpgrade);
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) {
                tag.setString("costingEUText", costingEUText);
            }
        }
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        if (wirelessUpgrade) {
            aNBT.setBoolean("wirelessUpgrade", true);
        }
        super.setItemNBT(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessUpgrade", wirelessUpgrade);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setInteger("parallelTier", mParallelTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        wirelessUpgrade = aNBT.getBoolean("wirelessUpgrade");
        wirelessMode = aNBT.getBoolean("wirelessMode");
        mParallelTier = aNBT.getInteger("parallelTier");
    }

    @Override
    public List<String> getTooltipWirelessModeLines() {
        String mode = GTNLMultiblockTooltipBuilder.mechanicName(WIRELESS_MODE_NAME_KEY);
        String hatch = GTNLMultiblockTooltipBuilder.hatchName(CustomHatchElement.ParallelCon.getDescriptionLangKey());
        List<String> lines = new ArrayList<>(
            Arrays.asList(
                StatCollector.translateToLocalFormatted(
                    WIRELESS_MODE_LINE_KEY + ".auto",
                    GTNLMultiblockTooltipBuilder.mechanicName(WIRELESS_UPGRADE_TOOLTIP_KEY),
                    mode),
                wirelessPowerLine(mode, hatch),
                StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".recipes", mode)));
        lines.removeIf(Objects::isNull);
        if (!getPerfectOC() && overclocksPerfectlyInWirelessMode()) {
            lines.add(StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".perfect_oc", mode));
        }
        return lines;
    }

    // Why: machines can replace the wireless mode power, so the line reads it back from the processing logic.
    private String wirelessPowerLine(String mode, String hatch) {
        if (appliesMachineVoltageLimit()) {
            return powerLine(mode, perTierVoltage(hatch, WIRELESS_VOLTAGE_TIER_OFFSET), perTierCurrent());
        }
        int[] tiers = new int[WIRELESS_POWER_PROBES];
        long[] amperages = new long[WIRELESS_POWER_PROBES];
        boolean wireless = wirelessMode;
        int parallelTier = mParallelTier;
        try {
            wirelessMode = true;
            for (int index = 0; index < tiers.length; index++) {
                mParallelTier = MultiblockTooltipReader.FIRST_PARALLEL_CONTROLLER_PROBE_TIER + index;
                ProcessingLogic logic = new GTNLProcessingLogic();
                setProcessingLogicPower(logic);
                AccessorProcessingLogic power = (AccessorProcessingLogic) logic;
                tiers[index] = GTUtility.getTier(power.getAvailableVoltage());
                amperages[index] = power.getAvailableAmperage();
            }
        } finally {
            wirelessMode = wireless;
            mParallelTier = parallelTier;
        }
        String voltage = voltagePhrase(hatch, tiers);
        String current = currentPhrase(amperages);
        return voltage == null || current == null ? null : powerLine(mode, voltage, current);
    }

    private static String powerLine(String mode, String voltage, String current) {
        return StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".power", mode, voltage, current);
    }

    private static String voltagePhrase(String hatch, int[] tiers) {
        int firstTier = MultiblockTooltipReader.FIRST_PARALLEL_CONTROLLER_PROBE_TIER;
        boolean fixed = true, perTier = true;
        for (int index = 1; index < tiers.length; index++) {
            fixed &= tiers[index] == tiers[0];
            perTier &= tiers[index] - (firstTier + index) == tiers[0] - firstTier;
        }
        if (perTier) return perTierVoltage(hatch, tiers[0] - firstTier);
        if (!fixed) return null;
        String tier = tiers[0] <= VoltageIndex.MAX ? TooltipHelper.voltageTierText(tiers[0], false)
            : EnumChatFormatting.WHITE + GTValues.VN[tiers[0]] + EnumChatFormatting.GRAY;
        return StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".voltage.fixed", tier);
    }

    private static String perTierVoltage(String hatch, int offset) {
        return StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".voltage.per_tier", hatch, offset);
    }

    // Why: wireless amperage grows by a power of four per parallel controller tier, less a reserve.
    private static String currentPhrase(long[] amperages) {
        int firstTier = MultiblockTooltipReader.FIRST_PARALLEL_CONTROLLER_PROBE_TIER;
        long step = (1L << (2 * (firstTier + 1))) - (1L << (2 * firstTier));
        long difference = amperages[1] - amperages[0];
        if (difference % step != 0) return null;
        long scale = difference / step;
        long offset = amperages[0] - (scale << (2 * firstTier));
        for (int index = 0; index < amperages.length; index++) {
            if (amperages[index] != (scale << (2 * (firstTier + index))) + offset) return null;
        }
        if (scale == 0) {
            return StatCollector.translateToLocalFormatted(
                WIRELESS_MODE_LINE_KEY + ".current.fixed",
                NumberFormatUtil.formatNumber(offset));
        }
        if (scale < 0 || offset > 0) return null;
        if (offset == 0)
            return StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".current.scaled", scale);
        return StatCollector.translateToLocalFormatted(WIRELESS_MODE_LINE_KEY + ".current.per_tier", scale, -offset);
    }

    private static String perTierCurrent() {
        return StatCollector.translateToLocalFormatted(
            WIRELESS_MODE_LINE_KEY + ".current.per_tier",
            WIRELESS_BASE_AMPERAGE,
            WIRELESS_AMPERAGE_RESERVE);
    }

    protected boolean overclocksPerfectlyInWirelessMode() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return (wirelessUpgrade ? 0.4 : 0.6) - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / (wirelessUpgrade ? 10.0 : 5.0) * Math.pow(0.75, mParallelTier);
    }

    @Override
    public long getMaxInputVoltage() {
        if (wirelessMode) {
            return wirelessModeVoltage();
        }
        return super.getMaxInputVoltage();
    }

    private long wirelessModeVoltage() {
        return GTValues.V[Math.min(mParallelTier + WIRELESS_VOLTAGE_TIER_OFFSET, VoltageIndex.MAX)];
    }

    @Override
    public int getMaxParallelRecipes() {
        if (wirelessMode && maxParallelStored >= 0) {
            return maxParallelStored;
        }
        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            return module.getParallel() << 4;
        }
        if (mParallelTier <= 1) {
            return 8;
        }
        return 1 << (2 * (mParallelTier - 2) + 4);
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public void setWirelessMode(boolean mode) {
        if (wirelessUpgrade) {
            wirelessMode = mode;
        } else {
            wirelessMode = false;
        }
    }
}
