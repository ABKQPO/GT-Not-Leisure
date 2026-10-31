package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.science.gtnl.common.machine.voidminer.VoidMinerHooks;
import com.science.gtnl.config.MachineConfig;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import bwcrossmod.galacticgreg.MTEVoidMiners;
import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.structure.error.StructureError;
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
        if (!MachineConfig.enableVoidMinerTweak) return elements;
        return new IHatchElement<?>[] { HatchElement.InputHatch, HatchElement.OutputBus, HatchElement.InputBus,
            HatchElement.Maintenance, HatchElement.Energy.or(HatchElement.ExoticEnergy) };
    }

    @Override
    protected void checkHatches(List<StructureError> errors) {
        super.checkHatches(errors);
        if (!MachineConfig.enableVoidMinerTweak) return;
        VoidMinerHooks.checkAmperage(errors, mEnergyHatches, mExoticEnergyHatches);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mExoticEnergyHatches.clear();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return VoidMinerHooks.tooltip(TIER_MULTIPLIER, getMinTier());
    }
}
