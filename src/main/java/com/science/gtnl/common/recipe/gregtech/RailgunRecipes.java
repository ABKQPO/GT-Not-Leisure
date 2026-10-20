package com.science.gtnl.common.recipe.gregtech;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.loader.ItemLoader;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class RailgunRecipes {

    private RailgunRecipes() {}

    public static void register() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Neutronium, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2),
                ItemList.Emitter_UV.get(2),
                ItemList.Field_Generator_UV.get(1),
                ItemList.Electric_Motor_UV.get(2))
            .fluidInputs(Materials.SolderingAlloy.getMolten(2304))
            .itemOutputs(new ItemStack(ItemLoader.electromagneticRailgun))
            .duration(1200)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
        ItemStack[] parts = { ItemList.Sensor_UV.get(1), ItemList.Electric_Motor_UV.get(1), ItemList.Emitter_UV.get(1),
            ItemList.Field_Generator_UV.get(2) };
        for (RailgunModule type : RailgunModule.values()) {
            boolean uhv = type == RailgunModule.EHV_BEAM;
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTOreDictUnificator.get(OrePrefixes.circuit, uhv ? Materials.UHV : Materials.UV, 2),
                    GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Neutronium, 2),
                    parts[type.ordinal()])
                .fluidInputs(Materials.SolderingAlloy.getMolten(576))
                .itemOutputs(ItemLoader.railgunModule.stack(type))
                .duration(400)
                .eut(uhv ? TierEU.RECIPE_UHV : TierEU.RECIPE_UV)
                .addTo(RecipeMaps.assemblerRecipes);
        }
        int[] fieldGenerators = { 1, 2, 4 };
        for (int tier = 1; tier <= EnergyModuleItem.TIERS; tier++) {
            boolean uhv = tier == EnergyModuleItem.TIERS;
            List<ItemStack> inputs = new ArrayList<>();
            inputs.add(GTOreDictUnificator.get(OrePrefixes.circuit, uhv ? Materials.UHV : Materials.UV, 4));
            inputs.add(GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Neutronium, 2));
            inputs.add(ItemList.Field_Generator_UV.get(fieldGenerators[tier - 1]));
            if (tier > 1) inputs.add(ItemLoader.energyModule.stack(tier - 1));
            GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray(new ItemStack[0]))
                .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
                .itemOutputs(ItemLoader.energyModule.stack(tier))
                .duration(800)
                .eut(uhv ? TierEU.RECIPE_UHV : TierEU.RECIPE_UV)
                .addTo(RecipeMaps.assemblerRecipes);
        }
    }
}
