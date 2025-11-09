package com.science.gtnl.common.recipe.gtnl;

import static com.science.gtnl.utils.enums.GTNLItemList.TrollFace;
import static gregtech.api.util.GTModHandler.getModItem;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.loader.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class ReFusionReactorRecipes implements IRecipePool {

    public RecipeMap<?> RFRR = RecipePool.RecombinationFusionReactorRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, getModItem("gregtech", "gt.metaitem.01", 1, 2299)))
            .itemOutputs(
                TrollFace.get(1)
                    .setStackDisplayName(StatCollector.translateToLocal("RFRRRecipes.1")))
            .fluidOutputs(
                MaterialsUEVplus.MagMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.Universium.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.WhiteDwarfMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.TranscendentMetal.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.Eternity.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.PrimordialMatter.getFluid(Integer.MAX_VALUE),
                MaterialsUEVplus.Space.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.Time.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.SixPhasedCopper.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.StargateCrystalSlurry.getFluid(Integer.MAX_VALUE),
                MaterialsUEVplus.Antimatter.getFluid(Integer.MAX_VALUE))
            .outputChances(1)
            .duration(1)
            .eut(0)
            .addTo(RFRR);
    }
}
