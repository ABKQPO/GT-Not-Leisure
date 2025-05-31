package com.science.gtnl.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import com.science.gtnl.Utils.enums.Mods;
import com.science.gtnl.config.MainConfig;

@LateMixin
@SuppressWarnings("unused")
public class LateMixinPlugin implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.sciencenotleisure.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        var mixins = new ArrayList<String>();
        mixins.add("Gregtech.AssLineRemover.GTRecipeBuilderHook_Mixin");
        mixins.add("Gregtech.AssLineRemover.ForGTPreLoadHook_Mixin");
        mixins.add("Gregtech.AssLineRemover.TTAssLineBuilderHook_Mixin");
        mixins.add("Gregtech.MTETreeFarm_Mixin");
        mixins.add("Gregtech.MTEIntegratedOreFactory_Mixin");
        mixins.add("Gregtech.MTEBetterJukebox_Mixin");
        mixins.add("Gregtech.MTEDigitalTankBase_Mixin");

        if (MainConfig.enableDebugMode) {
            mixins.add("Gregtech.MachineBlockRenderer_Mixin");
        }

        mixins.add("Bartwork.BartworkLoad_Head_Mixin");
        mixins.add("Bartwork.WerkstoffLoader_Mixin");
        mixins.add("Bartwork.Werkstoff_Mixin");
        mixins.add("Bartwork.MultipleMetalLoader_Mixin");
        mixins.add("Bartwork.SimpleMetalLoader_Mixin");
        mixins.add("Bartwork.CircuitImprintLoader_Mixin");

        mixins.add("DraconicEvolution.DraconicEvolutionEventHandler_Mixin");
        mixins.add("DraconicEvolution.ReactorExplosion_Mixin");

        mixins.add("ThaumicTinkerer.ItemBloodSword_Mixin");

        mixins.add("NHCoreMod.BacteriaRegistry_Mixin");

        mixins.add("BloodMagic.MeteorParadigm_Mixin");

        mixins.add("AppliedEnergistics.EntityTinyTNTPrimed_Mixin");

        if (!Mods.Overpowered.isModLoaded() && MainConfig.enableRecipeOutputChance) {
            mixins.add("Gregtech.VoltageChanceBonus_GT_ParallelHelper_Mixin");
            mixins.add("Gregtech.BehaviourScanner_Mixin");
        }

        if (!Mods.NHUtilities.isModLoaded()) {
            mixins.add("Gregtech.BaseMetaTileEntityAcceleration_Mixin");
            mixins.add("Gregtech.MTEAdvAssLineAcceleration_Mixin");
            mixins.add("Gregtech.ResearchStationAcceleration_Mixin");
            mixins.add("EnderIO.AccelerateEnergyReceive_Mixin");
            mixins.add("EnderIO.AccelerateTileEntity_Mixin");
            mixins.add("EnderIO.Modify_CapBankMaxIO_Mixin");
        }

        if (Mods.TwistSpaceTechnology.isModLoaded()) {
            mixins.add("TwistSpaceTechnology.RecipeLoader_Mixin");
        }

        return mixins;
    }

}
