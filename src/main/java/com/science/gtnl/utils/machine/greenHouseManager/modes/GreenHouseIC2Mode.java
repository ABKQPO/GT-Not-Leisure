package com.science.gtnl.utils.machine.greenHouseManager.modes;

import net.minecraft.util.EnumChatFormatting;

import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;

import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GreenHouseIC2Mode extends GreenHouseMode {

    public static final GreenHouseIC2Mode instance = new GreenHouseIC2Mode();

    @Override
    public int getUIIndex() {
        return 1;
    }

    @Override
    public String getName() {
        return "IC2";
    }

    @Override
    public int getMinVoltageTier() {
        return EIG_BALANCE_IC2_ACCELERATOR_TIER;
    }

    @Override
    public int getMinGlassTier() {
        return EIG_BALANCE_IC2_ACCELERATOR_TIER;
    }

    @Override
    public int getStartingSlotCount() {
        return 4;
    }

    @Override
    public int getSlotPerTierMultiplier() {
        return 4;
    }

    @Override
    public int getSeedCapacityPerSlot() {
        return 1;
    }

    @Override
    public int getWeedEXMultiplier() {
        return 5;
    }

    @Override
    public int getMaxFertilizerUsagePerSeed() {
        return 40;
    }

    @Override
    public double getFertilizerBoost() {
        return 0.1d;
    }

    @Override
    public MultiblockTooltipBuilder addTooltipInfo(MultiblockTooltipBuilder builder) {
        String minVoltageTier = GTUtility.getColoredTierNameFromTier((byte) this.EIG_BALANCE_IC2_ACCELERATOR_TIER);

        int acceleration = (1 << EIG_BALANCE_IC2_ACCELERATOR_TIER);

        double fertilizerBonusMultiplier = this.getFertilizerBoost() * 100;
        String fertilizerBonus = String.format("%.0f%%", fertilizerBonusMultiplier);

        return builder.addSeparator()
            .addInfo(EnumChatFormatting.GOLD + "IC2 Crops:")
            .addInfo("Minimal voltage tier: " + minVoltageTier)
            .addInfo("Starting with " + this.getStartingSlotCount() + " slot")
            .addInfo(
                "Every tier past " + minVoltageTier + ", slots are multiplied by " + this.getSlotPerTierMultiplier())
            .addInfo("Every slot adds " + this.getSeedCapacityPerSlot() + " seed to the total seed capacity")
            .addInfo("Process time: 5 sec")
            .addInfo("All crops are accelerated by x" + acceleration + " times")
            .addInfo("Can consume up to " + this.getMaxFertilizerUsagePerSeed() + " fertilizer per seed per cycle")
            .addInfo("Boost per fertilizer: " + fertilizerBonus)
            .addInfo("Weed-EX 9000 consumption is multiplied by " + this.getWeedEXMultiplier());
    }

    @Override
    public int getSlotCount(int machineTier) {
        int tierAboveMin = machineTier - this.EIG_BALANCE_IC2_ACCELERATOR_TIER;
        if (tierAboveMin < 0) return 0;
        return 4 << (2 * (tierAboveMin));
    }

}
