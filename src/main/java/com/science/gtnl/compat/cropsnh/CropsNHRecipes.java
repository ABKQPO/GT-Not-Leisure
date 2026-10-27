package com.science.gtnl.compat.cropsnh;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.gtnewhorizon.cropsnh.api.CropsNHItemList;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;

public class CropsNHRecipes {

    private CropsNHRecipes() {}

    public static void assembler() {
        if (!Mods.CropsNH.isModLoaded()) return;
        assemblerRecipes();
    }

    @Optional.Method(modid = "cropsnh")
    private static void assemblerRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                CropsNHItemList.CropManager_LV.get(2),
                new ItemStack(Blocks.dirt, 64),
                CropsNHItemList.cropSticks.get(64),
                ModsItemlist.ForestryFarmValve.get(16),
                ModsItemlist.ForestryFarmGearbox.get(16),
                ModsItemlist.ForestryFarmControl.get(4))
            .itemOutputs(GTNLItemList.SteamGreenhouseModule.get(1))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sunshine", 16000))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
