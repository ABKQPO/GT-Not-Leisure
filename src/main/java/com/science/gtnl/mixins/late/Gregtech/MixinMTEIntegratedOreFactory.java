package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEIntegratedOreFactory;

@Mixin(value = MTEIntegratedOreFactory.class, remap = false)
public abstract class MixinMTEIntegratedOreFactory {

    @ModifyArg(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeast([Lgregtech/api/interfaces/IHatchElement;)Lgregtech/api/util/HatchElementBuilder;"),
        index = 0)
    private static IHatchElement<?>[] modifyAtLeastArgs(IHatchElement<?>[] elements) {
        for (IHatchElement<?> e : elements) {
            if (e == HatchElement.Energy) {
                IHatchElement<?>[] modified = new IHatchElement<?>[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    if (elements[i] == HatchElement.Energy) {
                        modified[i] = HatchElement.Energy.or(HatchElement.ExoticEnergy);
                    } else {
                        modified[i] = elements[i];
                    }
                }
                return modified;
            }
        }
        return elements;
    }

    @ModifyConstant(method = "checkProcessing", constant = @Constant(intValue = 1024))
    private int modifyMaxParaConstant(int original) {
        return 65536;
    }

    /**
     * @reason Overrides the original tooltip builder method to use translated tooltips
     *         from the lang file using StatCollector.
     *
     * @return the customized MultiblockTooltipBuilder
     * @author GTNotLeisure
     */
    @Overwrite
    protected MultiblockTooltipBuilder createTooltip() {
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
