package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import bwcrossmod.galacticgreg.MTEVoidMiners;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@Mixin(value = { MTEVoidMiners.VMUV.class, MTEVoidMiners.VMZPM.class, MTEVoidMiners.VMLUV.class }, remap = false)
public abstract class MixinMTEVoidMiners extends MTEVoidMinerBase<MixinMTEVoidMiners> {

    public MixinMTEVoidMiners(int aID, String aName, String aNameRegional, int tier) {
        super(aID, aName, aNameRegional, tier);
    }

    @ModifyArg(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeast([Lgregtech/api/interfaces/IHatchElement;)Lgregtech/api/util/HatchElementBuilder;"),
        index = 0)
    private static IHatchElement<?>[] modifyAtLeastArgs(IHatchElement<?>[] elements) {
        if (!MainConfig.machine.enableVoidMinerTweak) return elements;
        return new IHatchElement<?>[] { HatchElement.InputHatch, HatchElement.OutputBus, HatchElement.InputBus,
            HatchElement.Maintenance, HatchElement.Energy.or(HatchElement.ExoticEnergy) };
    }

    @Override
    protected void checkHatches(List<StructureError> errors) {
        super.checkHatches(errors);
        if (!MainConfig.machine.enableVoidMinerTweak) return;

        long amp = 0;

        for (MTEHatch tHatch : GTUtility.validMTEList(mEnergyHatches)) {
            amp += tHatch.maxWorkingAmperesIn();
        }
        for (MTEHatch tHatch : GTUtility.validMTEList(mExoticEnergyHatches)) {
            amp += tHatch.maxWorkingAmperesIn();
        }

        if (amp > 256) {
            errors.add(GTNLStructureErrors.energyInputAmperageTooHigh());
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mExoticEnergyHatches.clear();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        int[] structureBlock = switch (TIER_MULTIPLIER) {
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
                    NumberFormatUtil.formatNumber(GTValues.V[this.getMinTier()])))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.5"))
            .addInfo(StatCollector.translateToLocalFormatted("gtnl.machine.void_miner.info.6", TIER_MULTIPLIER * 2))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.7"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.void_miner.info.9"));

        if (TIER_MULTIPLIER == 3) tt.addPerfectOCInfo();

        tt.addSupportAny()
            .beginStructureBlock(structureBlock[0], structureBlock[1], structureBlock[2], false);

        switch (TIER_MULTIPLIER) {
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
            StatCollector.translateToLocalFormatted("gtnl.machine.void_miner.casing.0", GTValues.VN[this.getMinTier()]))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.1"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.2"))
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.3"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.void_miner.casing.1"))
            .toolTipFinisher();
        return tt;
    }
}
