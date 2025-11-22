package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.recipe.RecipeMaps.*;
import static gregtech.api.util.GTRecipeBuilder.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.*;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class NanoForgeRecipes implements IRecipePool {

    public RecipeMap<?> NFR = RecipeMaps.nanoForgeRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.GarnetRed, 0),
                GGMaterial.shirabon.get(OrePrefixes.block, 8),
                GTNLItemList.HighlyAdvancedSoc.get(64),
                GTNLItemList.HighlyAdvancedSoc.get(64),
                GTNLItemList.HighlyAdvancedSoc.get(64),
                GTNLItemList.HighlyAdvancedSoc.get(64))
            .itemOutputs(GGMaterial.shirabon.get(OrePrefixes.nanite, 1))
            .fluidInputs(
                Materials.UUMatter.getFluid(500_000),
                MaterialsUEVplus.TranscendentMetal.getMolten(9216),
                MaterialsUEVplus.PrimordialMatter.getFluid(32000))
            .metadata(NANO_FORGE_TIER, 3)
            .duration(200 * SECONDS)
            .eut(2_000_000_000)
            .addTo(NFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Topaz, 0),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.block, 8),
                ItemList.Circuit_Chip_SoC.get(64),
                ItemList.Circuit_Chip_SoC.get(64))
            .itemOutputs(GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.nanite, 2))
            .fluidInputs(Materials.UUMatter.getFluid(200_000))
            .metadata(NANO_FORGE_TIER, 1)
            .duration(200 * SECONDS)
            .eut(20_000_000)
            .addTo(NFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                CustomItemList.RadoxPolymerLens.get(0),
                GGMaterial.metastableOganesson.get(OrePrefixes.block, 8),
                ItemList.Circuit_Chip_SoC2.get(64),
                ItemList.Circuit_Chip_SoC2.get(64))
            .itemOutputs(GGMaterial.metastableOganesson.get(OrePrefixes.nanite, 1))
            .fluidInputs(Materials.UUMatter.getFluid(500_000))
            .metadata(NANO_FORGE_TIER, 2)
            .duration(300 * SECONDS)
            .eut(40_000_000)
            .addTo(NFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.Laser_Lens_Special.get(0),
                GGMaterial.preciousMetalAlloy.get(OrePrefixes.block, 8),
                ItemList.Circuit_Chip_NanoCPU.get(64),
                ItemList.Circuit_Chip_NanoCPU.get(64))
            .itemOutputs(GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 1))
            .fluidInputs(Materials.UUMatter.getFluid(300_000))
            .metadata(NANO_FORGE_TIER, 1)
            .duration(400 * SECONDS)
            .eut(40_000_000)
            .addTo(NFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                CustomItemList.ChromaticLens.get(0),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.block, 16),
                ItemList.Circuit_Chip_QPIC.get(64),
                ItemList.Circuit_Chip_QPIC.get(64),
                ItemList.Circuit_Chip_QPIC.get(64))
            .itemOutputs(GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.nanite, 1))
            .fluidInputs(Materials.UUMatter.getFluid(1000_000))
            .metadata(NANO_FORGE_TIER, 2)
            .duration(300 * SECONDS)
            .eut(40_000_000)
            .addTo(NFR);
    }
}
