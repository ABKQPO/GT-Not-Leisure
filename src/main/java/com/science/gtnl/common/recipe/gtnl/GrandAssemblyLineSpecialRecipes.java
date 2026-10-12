package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMap;
import gtnhlanth.common.register.LanthItemList;
import tectech.thing.CustomItemList;

public class GrandAssemblyLineSpecialRecipes implements IRecipePool {

    public RecipeMap<?> GALSR = GTNLRecipeMaps.GrandAssemblyLineSpecialRecipes;

    @Override
    public void loadRecipes() {
        registerSpecialCircuit(ItemList.Casing_Dim_Injector.get(1), 1);
        registerSpecialCircuit(ItemList.Casing_Dim_Trans.get(1), 2);

        registerSpecialCircuit(LanthItemList.LUV_BEAMLINE_INPUT_HATCH, 1);
        registerSpecialCircuit(LanthItemList.LUV_BEAMLINE_OUTPUT_HATCH, 2);

        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier0.get(1), 1);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier1.get(1), 2);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier2.get(1), 3);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier3.get(1), 4);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier4.get(1), 5);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier5.get(1), 6);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier6.get(1), 7);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier7.get(1), 8);
        registerSpecialCircuit(CustomItemList.SpacetimeCompressionFieldGeneratorTier8.get(1), 9);

        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier0.get(1), 1);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier1.get(1), 2);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier2.get(1), 3);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier3.get(1), 4);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier4.get(1), 5);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier5.get(1), 6);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier6.get(1), 7);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier7.get(1), 8);
        registerSpecialCircuit(CustomItemList.TimeAccelerationFieldGeneratorTier8.get(1), 9);

        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier0.get(1), 1);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier1.get(1), 2);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier2.get(1), 3);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier3.get(1), 4);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier4.get(1), 5);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier5.get(1), 6);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier6.get(1), 7);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier7.get(1), 8);
        registerSpecialCircuit(CustomItemList.StabilisationFieldGeneratorTier8.get(1), 9);
    }

    public void registerSpecialCircuit(ItemStack output, int circuit) {
        if (output == null || circuit < 0) return;

        RecipeBuilder.builder()
            .itemInputs()
            .circuit(circuit)
            .itemOutputs(output)
            .fake()
            .duration(0)
            .eut(0)
            .addTo(GALSR);

        GrandAssemblyLine.specialRecipe.put(ItemId.create(output), circuit);
    }
}
