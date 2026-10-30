package com.science.gtnl.compat.bloodmagic;

import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;

public class BloodMagicRecipes {

    public static void assemblyLine() {
        if (!Mods.BloodMagic.isModLoaded()) return;
        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ModsItemlist.BloodMagicAltar.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ModsItemlist.BloodMagicMasterStone.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32 },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16 },
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Trinium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 64),
                ItemList.Field_Generator_ZPM.get(16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadah, 5),
                ItemList.Electric_Pump_ZPM.get(32),
                ItemList.Emitter_ZPM.get(32),
                ModsItemlist.BloodArsenalLpMaterializer.get(1),
                ModsItemlist.BloodArsenalLifeInfuser.get(1),
                ModsItemlist.BloodMagicBlockWritingTable.get(1),
                ModsItemlist.BloodMagicAwakenedActivationCrystal.get(1),
                ModsItemlist.BloodMagicRitualDiviner.get(1))
            .fluidInputs(
                Materials.Grade4PurifiedWater.getFluid(64000),
                Materials.Americium.getMolten(4608),
                Materials.Neutronium.getMolten(2304),
                Materials.NaquadahEnriched.getMolten(1152))
            .itemOutputs(GTNLItemList.BloodSoulSacrificialArray.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(120 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);
    }
}
