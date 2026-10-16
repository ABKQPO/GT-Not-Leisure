package com.science.gtnl.common.recipe.gregtech;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.railgun.RailgunModuleType;
import com.science.gtnl.loader.ItemLoader;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public final class RailgunRecipes {

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
            ItemList.Field_Generator_UV.get(2), ItemList.Field_Generator_UV.get(1), ItemList.Field_Generator_UV.get(2),
            ItemList.Field_Generator_UV.get(4) };
        for (RailgunModuleType type : RailgunModuleType.values()) {
            boolean uhv = type == RailgunModuleType.EHV_BEAM || type == RailgunModuleType.ENERGY_T3;
            List<ItemStack> inputs = new ArrayList<>();
            inputs.add(
                GTOreDictUnificator
                    .get(OrePrefixes.circuit, uhv ? Materials.UHV : Materials.UV, type.energy() ? 4 : 2));
            inputs.add(GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Neutronium, 2));
            inputs.add(parts[type.ordinal()]);
            if (type == RailgunModuleType.ENERGY_T2)
                inputs.add(ItemLoader.railgunModule.stack(RailgunModuleType.ENERGY_T1));
            if (type == RailgunModuleType.ENERGY_T3)
                inputs.add(ItemLoader.railgunModule.stack(RailgunModuleType.ENERGY_T2));
            var recipe = GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray(new ItemStack[0]))
                .fluidInputs(Materials.SolderingAlloy.getMolten(type.energy() ? 1152 : 576))
                .itemOutputs(ItemLoader.railgunModule.stack(type))
                .duration(type.energy() ? 800 : 400)
                .eut(uhv ? TierEU.RECIPE_UHV : TierEU.RECIPE_UV);
            recipe.addTo(RecipeMaps.assemblerRecipes);
        }
    }
}
