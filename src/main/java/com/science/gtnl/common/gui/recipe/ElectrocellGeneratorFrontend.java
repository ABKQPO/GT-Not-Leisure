package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.science.gtnl.utils.recipes.metadata.ElectrocellGeneratorMetadata;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElectrocellGeneratorFrontend extends GTNLLogoFrontend {

    public ElectrocellGeneratorFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder.neiSpecialInfoFormatter(ElectrocellGeneratorMetadata.INSTANCE));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return ImmutableList.of(new Pos2d(43, 7), new Pos2d(115, 7));
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return ImmutableList.of(new Pos2d(79, 45));
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        return ImmutableList.of(new Pos2d(79, 7));
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return ImmutableList.of(new Pos2d(79, 63));
    }
}
