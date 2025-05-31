package com.science.gtnl.common.recipe.GTNL;

import static com.science.gtnl.Utils.enums.GTNLItemList.TrollFace;
import static com.science.gtnl.Utils.item.TextHandler.texter;
import static gregtech.api.util.GTModHandler.getModItem;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class ReFusionReactorRecipes implements IRecipePool {

    final RecipeMap<?> RFRR = RecipeRegister.RecombinationFusionReactorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmountUnsafe(2147483647, getModItem("gregtech", "gt.metaitem.01", 1, 2299)))
            .itemOutputs(
                TrollFace.get(1)
                    .setStackDisplayName(texter("It's just out of reach, isn't it?", "RFRRRecipes.1")))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.magmatter", 2147483647),
                FluidRegistry.getFluidStack("molten.magnetohydrodynamicallyconstrainedstarmatter", 2147483647),
                FluidRegistry.getFluidStack("molten.universium", 2147483647),
                FluidRegistry.getFluidStack("molten.whitedwarfmatter", 2147483647),
                FluidRegistry.getFluidStack("molten.blackdwarfmatter", 2147483647),
                FluidRegistry.getFluidStack("molten.spacetime", 2147483647),
                FluidRegistry.getFluidStack("molten.transcendentmetal", 2147483647),
                FluidRegistry.getFluidStack("molten.eternity", 2147483647),
                FluidRegistry.getFluidStack("primordialmatter", 2147483647),
                FluidRegistry.getFluidStack("spatialfluid", 2147483647),
                FluidRegistry.getFluidStack("temporalfluid", 2147483647),
                FluidRegistry.getFluidStack("molten.sixphasedcopper", 2147483647),
                FluidRegistry.getFluidStack("molten.infinity", 2147483647),
                FluidRegistry.getFluidStack("antimatter", 2147483647))
            .outputChances(1)
            .duration(1)
            .eut(1)
            .addTo(RFRR);
    }
}
