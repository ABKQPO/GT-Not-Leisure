package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.enums.Mods.EternalSingularity;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeConstants.FOG_UPGRADE_NAME_SHORT;

import org.apache.commons.lang3.ArrayUtils;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.EternalGregTechWorkshopUpgrade;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import tectech.thing.CustomItemList;

public class EternalGregTechWorkshopUpgradeRecipes implements IRecipePool {

    final RecipeMap<?> EGTWUR = RecipeRegister.EternalGregTechWorkshopUpgradeRecipes;

    @Override
    public void loadRecipes() {
        EternalGregTechWorkshopUpgrade.START.addExtraCost(
            GTNLItemList.StargateSingularity.get(Integer.MAX_VALUE),
            GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.SuperconductorUIVBase, 64),
            ItemList.SuperconductorComposite.get(32),
            GGMaterial.metastableOganesson.get(OrePrefixes.gearGt, 16),
            getModItem(EternalSingularity.ID, "eternal_singularity", 8L),
            ItemList.Robot_Arm_UIV.get(64L),
            ItemList.Field_Generator_UEV.get(64L));

        EternalGregTechWorkshopUpgrade.FDIM.addExtraCost(
            GregtechItemList.Mega_AlloyBlastSmelter.get(16L),
            ItemList.Casing_Coil_Hypogen.get(64L),
            CustomItemList.Godforge_HarmonicPhononTransmissionConduit.get(32L),
            getModItem(EternalSingularity.ID, "eternal_singularity", 16L),
            ItemRefer.Field_Restriction_Coil_T3.get(48),
            ItemList.Robot_Arm_UIV.get(64L),
            ItemList.Field_Generator_UEV.get(64L));

        EternalGregTechWorkshopUpgrade.GPCI.addExtraCost(
            CustomItemList.Godforge_StellarEnergySiphonCasing.get(8),
            GregtechItemList.FusionComputer_UV3.get(8),
            GregtechItemList.Casing_Fusion_Internal2.get(64),
            ItemList.UHTResistantMesh.get(64),
            MaterialsAlloy.QUANTUM.getPlateDense(48),
            MaterialsElements.STANDALONE.RHUGNOR.getGear(32),
            getModItem(EternalSingularity.ID, "eternal_singularity", 16L),
            ItemList.Robot_Arm_UIV.get(64L),
            ItemList.Field_Generator_UEV.get(64L));

        EternalGregTechWorkshopUpgrade.QGPIU.addExtraCost(
            CustomItemList.Godforge_StellarEnergySiphonCasing.get(16),
            ItemRefer.Compact_Fusion_MK5.get(2),
            ItemRefer.Compact_Fusion_Coil_T4.get(64),
            CustomItemList.Godforge_HarmonicPhononTransmissionConduit.get(16),
            ItemList.Machine_Multi_TranscendentPlasmaMixer.get(4),
            MaterialsElements.STANDALONE.RHUGNOR.getGear(64),
            GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Ichorium, 64),
            getModItem(EternalSingularity.ID, "eternal_singularity", 32L),
            ItemList.Robot_Arm_UIV.get(64L),
            ItemList.Field_Generator_UEV.get(64L));

        EternalGregTechWorkshopUpgrade.CD.addExtraCost(
            GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.SpaceTime, 64),
            GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.SuperconductorUMVBase, 64),
            MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(64),
            MaterialsElements.STANDALONE.DRAGON_METAL.getFrameBox(64),
            CustomItemList.EOH_Reinforced_Spatial_Casing.get(64),
            CustomItemList.EOH_Infinite_Energy_Casing.get(8),
            ItemList.ZPM6.get(2),
            ItemList.Field_Generator_UMV.get(32));

        EternalGregTechWorkshopUpgrade.EE.addExtraCost(
            GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.WhiteDwarfMatter, 64),
            GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.BlackDwarfMatter, 64),
            GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.Eternity, 16),
            GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.Universium, 2),
            CustomItemList.EOH_Infinite_Energy_Casing.get(64),
            CustomItemList.StabilisationFieldGeneratorTier5.get(16),
            ItemList.ZPM6.get(6),
            ItemList.Field_Generator_UMV.get(64));

        EternalGregTechWorkshopUpgrade.END.addExtraCost(
            CustomItemList.Machine_Multi_QuarkGluonPlasmaModule.get(32),
            CustomItemList.Godforge_StellarEnergySiphonCasing.get(64),
            CustomItemList.StabilisationFieldGeneratorTier6.get(48),
            ItemList.Transdimensional_Alignment_Matrix.get(8),
            ItemList.ZPM6.get(16),
            ItemList.Robot_Arm_UMV.get(64),
            ItemList.Conveyor_Module_UMV.get(64));

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.START.getExtraCost()))
            .itemOutputs(
                CustomItemList.Godforge_GravitonFlowModulatorTier1.get(1),
                CustomItemList.Machine_Multi_SmeltingModule.get(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.START.getShortNameText())
            .fake()
            .addTo(EGTWUR);

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.FDIM.getExtraCost()))
            .itemOutputs(CustomItemList.Machine_Multi_MoltenModule.get(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.FDIM.getShortNameText())
            .fake()
            .addTo(EGTWUR);

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.GPCI.getExtraCost()))
            .itemOutputs(CustomItemList.Machine_Multi_PlasmaModule.get(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.GPCI.getShortNameText())
            .fake()
            .addTo(EGTWUR);

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.QGPIU.getExtraCost()))
            .itemOutputs(CustomItemList.Machine_Multi_QuarkGluonPlasmaModule.get(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.QGPIU.getShortNameText())
            .fake()
            .addTo(EGTWUR);

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.CD.getExtraCost()))
            .itemOutputs(CustomItemList.Godforge_GravitonFlowModulatorTier2.get(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.CD.getShortNameText())
            .fake()
            .addTo(EGTWUR);

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.EE.getExtraCost()))
            .itemOutputs(CustomItemList.Godforge_GravitonFlowModulatorTier3.get(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.EE.getShortNameText())
            .fake()
            .addTo(EGTWUR);

        GTValues.RA.stdBuilder()
            .itemInputs(ArrayUtils.addAll(EternalGregTechWorkshopUpgrade.END.getExtraCost()))
            .itemOutputs(MaterialsUEVplus.GravitonShard.getGems(1))
            .duration(1)
            .eut(1)
            .metadata(FOG_UPGRADE_NAME_SHORT, EternalGregTechWorkshopUpgrade.END.getShortNameText())
            .fake()
            .addTo(EGTWUR);
    }
}
