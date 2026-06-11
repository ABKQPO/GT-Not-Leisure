package com.science.gtnl.mixins.late.Gregtech;

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
        tt.addMachineType(StatCollector.translateToLocal("VoidMinerRecipeType"))
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
                    "Tooltip_VoidMiner_01",
                    NumberFormatUtil.formatNumber(GTValues.V[this.getMinTier()])))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_06"))
            .addInfo(StatCollector.translateToLocalFormatted("Tooltip_VoidMiner_07", TIER_MULTIPLIER * 2))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VoidMiner_10"));

        if (TIER_MULTIPLIER == 3) tt.addPerfectOCInfo();

        tt.addTecTechHatchInfo()
            .beginStructureBlock(structureBlock[0], structureBlock[1], structureBlock[2], false);

        switch (TIER_MULTIPLIER) {
            case 2 -> tt.addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerII_Casing_00"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerII_Casing_01"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerII_Casing_02"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerII_Casing_03"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerII_Casing_04"));

            case 3 -> tt.addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerIII_Casing_00"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerIII_Casing_01"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerIII_Casing_02"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerIII_Casing_03"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerIII_Casing_04"));
            default -> tt.addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerI_Casing_00"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerI_Casing_01"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerI_Casing_02"))
                .addStructureInfo(StatCollector.translateToLocal("Tooltip_VoidMinerI_Casing_03"));
        }

        tt.addEnergyHatch(
            StatCollector.translateToLocalFormatted("Tooltip_VoidMiner_Casing_00", GTValues.VN[this.getMinTier()]))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_VoidMiner_Casing_01"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_VoidMiner_Casing_02"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_VoidMiner_Casing_03"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_VoidMiner_Casing_01"))
            .toolTipFinisher();
        return tt;
    }
}
