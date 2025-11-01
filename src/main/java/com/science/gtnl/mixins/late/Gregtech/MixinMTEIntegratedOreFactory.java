package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.tileentities.machines.multi.MTEIntegratedOreFactory;

@Mixin(value = MTEIntegratedOreFactory.class, remap = false)
public abstract class MixinMTEIntegratedOreFactory
    extends MTEExtendedPowerMultiBlockBase<MixinMTEIntegratedOreFactory> {

    public MixinMTEIntegratedOreFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @ModifyArg(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeast([Lgregtech/api/interfaces/IHatchElement;)Lgregtech/api/util/HatchElementBuilder;"),
        index = 0)
    private static IHatchElement<?>[] modifyAtLeastArgs(IHatchElement<?>[] elements) {
        for (IHatchElement<?> e : elements) {
            if (e == HatchElement.Energy) {
                return new IHatchElement<?>[] { InputHatch, OutputBus, InputBus, Maintenance, Energy.or(ExoticEnergy) };
            }
        }
        return elements;
    }

    @ModifyConstant(method = "checkProcessing", constant = @Constant(intValue = 1024))
    private int modifyMaxParaConstant(int original) {
        return 65536 * GTUtility.getTier(getMaxInputEu());
    }

    @Override
    public long getMaxInputEu() {
        long exoticEu = ExoticEnergyInputHelper.getTotalEuMulti(mExoticEnergyHatches);
        long normalEu = ExoticEnergyInputHelper.getTotalEuMulti(mEnergyHatches);
        return Math.max(exoticEu, normalEu);
    }

    @Inject(
        method = "checkProcessing",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/OverclockCalculator;calculateMultiplierUnderOneTick()D",
            shift = At.Shift.BEFORE))
    private void injectBeforeCalculateMultiplier(CallbackInfoReturnable<CheckRecipeResult> cir,
        @Local OverclockCalculator calculator) {
        calculator.enablePerfectOC();
    }

    /**
     * @reason Overrides the original tooltip builder method to use translated tooltips
     *         from the lang file using StatCollector.
     *
     * @return the customized MultiblockTooltipBuilder
     * @author GTNotLeisure
     */
    @Overwrite
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IntegratedOreFactoryRecipeType"))
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
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .beginStructureBlock(6, 12, 11, false)
            .addController(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_08"))
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_00"), 128, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_01"), 105, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_02"), 48, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_03"), 30, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_04"), 16, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_05"), 16, false)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_06"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_07"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_08"), 2)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_09"), 3)
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_10"), 3)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_11"), 4)
            .toolTipFinisher();
        return tt;
    }
}
