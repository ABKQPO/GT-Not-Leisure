package com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW;

import net.minecraft.util.StatCollector;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;

public class EGTWFusionModule extends EternalGregTechWorkshopModule {

    public EGTWFusionModule(String aName) {
        super(aName);
    }

    public EGTWFusionModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
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
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 5, 7, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EGTWFusionModule_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }
}
