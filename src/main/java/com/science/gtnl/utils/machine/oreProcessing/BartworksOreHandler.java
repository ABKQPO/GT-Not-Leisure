package com.science.gtnl.utils.machine.oreProcessing;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTUtility;

public class BartworksOreHandler {

    public void processBWOreRecipes() {
        for (Werkstoff werkstoff : Werkstoff.werkstoffHashSet) {
            if (!werkstoff.hasItemType(OrePrefixes.ore)) continue;
            ArrayList<ItemStack> outputs = new ArrayList<>();
            // basic output
            outputs.add(werkstoff.get(OrePrefixes.dust, Integer.MAX_VALUE));

            // OrePrefixes.gem output
            if (werkstoff.hasItemType(OrePrefixes.gem)) {
                if (werkstoff.hasItemType(OrePrefixes.gemExquisite)) {
                    outputs.add(werkstoff.get(OrePrefixes.gemExquisite, Integer.MAX_VALUE));
                    outputs.add(werkstoff.get(OrePrefixes.gemFlawless, Integer.MAX_VALUE));
                    outputs.add(werkstoff.get(OrePrefixes.gem, Integer.MAX_VALUE));
                } else {
                    outputs.add(werkstoff.get(OrePrefixes.gem, Integer.MAX_VALUE));
                }
            }

            // byproducts
            if (werkstoff.getNoOfByProducts() >= 1) {
                if (werkstoff.getNoOfByProducts() == 1) {
                    outputs.add(
                        GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, werkstoff.getOreByProduct(0, OrePrefixes.dust)));
                } else {
                    for (int i = 0; i < werkstoff.getNoOfByProducts(); i++) {
                        outputs.add(
                            GTUtility
                                .copyAmountUnsafe(Integer.MAX_VALUE, werkstoff.getOreByProduct(i, OrePrefixes.dust)));
                    }
                }
            } else {
                outputs.add(werkstoff.get(OrePrefixes.dust, Integer.MAX_VALUE));
            }

            if (werkstoff.hasItemType(OrePrefixes.rawOre)) {
                ArrayList<ItemStack> rawOreOutputs = new ArrayList<>();
                rawOreOutputs.add(werkstoff.get(OrePrefixes.dust, Integer.MAX_VALUE));

                if (werkstoff.hasItemType(OrePrefixes.gem)) {
                    if (werkstoff.hasItemType(OrePrefixes.gemExquisite)) {
                        rawOreOutputs.add(werkstoff.get(OrePrefixes.gemExquisite, Integer.MAX_VALUE));
                        rawOreOutputs.add(werkstoff.get(OrePrefixes.gemFlawless, Integer.MAX_VALUE));
                        rawOreOutputs.add(werkstoff.get(OrePrefixes.gem, Integer.MAX_VALUE));
                    } else {
                        rawOreOutputs.add(werkstoff.get(OrePrefixes.gem, Integer.MAX_VALUE));
                    }
                }

                if (werkstoff.getNoOfByProducts() >= 1) {
                    if (werkstoff.getNoOfByProducts() == 1) {
                        rawOreOutputs.add(
                            GTUtility
                                .copyAmountUnsafe(Integer.MAX_VALUE, werkstoff.getOreByProduct(0, OrePrefixes.dust)));
                    } else {
                        for (int i = 0; i < werkstoff.getNoOfByProducts(); i++) {
                            rawOreOutputs.add(
                                GTUtility.copyAmountUnsafe(
                                    Integer.MAX_VALUE,
                                    werkstoff.getOreByProduct(i, OrePrefixes.dust)));
                        }
                    }
                } else {
                    rawOreOutputs.add(werkstoff.get(OrePrefixes.dust, Integer.MAX_VALUE));
                }

                RecipeBuilder.builder()
                    .itemInputs(werkstoff.get(OrePrefixes.rawOre, 1))
                    .itemOutputs(rawOreOutputs.toArray(new ItemStack[] {}))
                    .eut(0)
                    .duration(1)
                    .addTo(GTNLRecipeMaps.CheatOreProcessingRecipes);
            }

            RecipeBuilder.builder()
                .itemInputs(werkstoff.get(OrePrefixes.ore, 1))
                .itemOutputs(outputs.toArray(new ItemStack[] {}))
                .eut(0)
                .duration(1)
                .addTo(GTNLRecipeMaps.CheatOreProcessingRecipes);
        }
    }
}
