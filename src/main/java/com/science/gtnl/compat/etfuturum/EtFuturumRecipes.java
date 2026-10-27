package com.science.gtnl.compat.etfuturum;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.reavaritia.utils.enums.ReAvaItemList;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;

public class EtFuturumRecipes {

    private EtFuturumRecipes() {}

    public static void rockBreaker() {
        if (!Mods.EtFuturumRequiem.isModLoaded()) return;
        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.EtFuturumRequiemBlueIce.get(0), new ItemStack(Blocks.soul_sand, 0))
            .circuit(4)
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stone, Materials.Basalt, 1L))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTNLRecipeMaps.RockBreakerRecipes);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.EtFuturumRequiemMagma.get(0), new ItemStack(Blocks.soul_sand, 0))
            .circuit(5)
            .itemOutputs(ModsItemlist.EtFuturumRequiemCobbledDeepslate.get(1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTNLRecipeMaps.RockBreakerRecipes);
    }

    public static void avaritiaScript() {
        if (!Mods.EtFuturumRequiem.isModLoaded()) return;
        avaritiaScriptRecipes();
    }

    @Optional.Method(modid = "etfuturum")
    private static void avaritiaScriptRecipes() {
        if (ConfigMixins.enableElytra) {
            ExtremeCraftingManager.getInstance()
                .addExtremeShapedOreRecipe(
                    ReAvaItemList.InfinityElytra.get(1),
                    "--AA-AA--",
                    "-BAACAAB-",
                    "BDBAEABDB",
                    "DDDB-BDDD",
                    "DDDD-DDDD",
                    "DDDD-DDDD",
                    "DDDD-DDDD",
                    "DDD---DDD",
                    "-D-----D-",
                    'A',
                    GTOreDictUnificator.get(OrePrefixes.ingot, Materials.CosmicNeutronium, 1),
                    'B',
                    ModsItemlist.AvaritiaCrystalMatrix.get(1),
                    'C',
                    ModsItemlist.AvaritiaEndestPearl.get(1),
                    'D',
                    ModsItemlist.AvaritiaCrystalMatrixIngot.get(1),
                    'E',
                    ModsItemlist.EtFuturumRequiemElytra.get(1));
        }
    }
}
