package com.science.gtnl.compat.mobsinfo;

import static kubatech.api.enums.ItemList.ExtremeEntityCrusher;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;

import goodgenerator.util.ItemRefer;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import tectech.recipe.TTRecipeAdder;

public class MobsInfoRecipes {

    public static void assemblyLine() {
        if (!Mods.MobsInfo.isModLoaded()) return;
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ExtremeEntityCrusher.get(1),
            6660000,
            666,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ExtremeEntityCrusher.get(64), ModsItemlist.EnderIOBlockPoweredSpawner.get(64),
                ItemList.UltraHighStrengthConcrete.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StellarAlloy, 64),
                ModsItemlist.BloodMagicDaggerOfSacrifice.get(1), ItemList.Electric_Motor_UEV.get(64),
                ItemList.Robot_Arm_UEV.get(16), ItemList.Conveyor_Module_UEV.get(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 8), ItemList.NuclearStar.get(48),
                ModsItemlist.EnderIOAttractorCrystal.get(64), ItemRefer.HiC_T5.get(64),
                GTNLItemList.EnhancementCore.get(16), GregtechItemList.Laser_Lens_Special.get(2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 12) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Lubricant.getFluid(128000), GTNLMaterials.Polyetheretherketone.getMolten(36864),
                new FluidStack(GTPPFluids.Pyrotheum, 512000) },
            GTNLItemList.HighwayToHell.get(1),
            60 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UIV);
    }
}
