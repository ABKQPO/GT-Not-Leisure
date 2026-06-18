package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop;

import net.minecraft.util.StatCollector;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;

public class EGTWFusionModule extends EternalGregTechWorkshopModule {

    public EGTWFusionModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EGTWFusionModule(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EGTWFusionModule(this.mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.fusionRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EGTWFusionModuleRecipeType"))
            .beginStructureBlock(9, 5, 7, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }
}
