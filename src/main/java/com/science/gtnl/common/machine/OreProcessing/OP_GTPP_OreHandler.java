package com.science.gtnl.common.machine.OreProcessing;

import static com.science.gtnl.common.machine.OreProcessing.OP_Values.OreProcessRecipeDuration;
import static com.science.gtnl.common.machine.OreProcessing.OP_Values.OreProcessRecipeEUt;
import static gtPlusPlus.core.material.MaterialMisc.*;
import static gtPlusPlus.core.material.MaterialsAlloy.KOBOLDITE;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.RecipeRegister;

import gregtech.api.enums.GTValues;
import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.material.MaterialsOres;

public class OP_GTPP_OreHandler {

    public Set<Material> addSpecials(Set<Material> set) {
        set.add(RARE_EARTH_LOW);
        set.add(RARE_EARTH_MID);
        set.add(RARE_EARTH_HIGH);
        set.add(KOBOLDITE);
        return set;
    }

    public Set<Material> getGTPPOreMaterials() {
        Set<Material> gtppOres = new HashSet<>(51);
        for (Field field : MaterialsOres.class.getFields()) {
            if (field.getType() != Material.class) continue;
            try {
                Object object = field.get(null);
                if (!(object instanceof Material)) continue;
                gtppOres.add((Material) object);
            } catch (IllegalAccessException e) {
                ScienceNotLeisure.LOG
                    .info("Catch an IllegalAccessException in OP_GTPP_OreHandler.processGTPPOreRecipes");
            }
        }
        return gtppOres;
    }

    public void processGTPPOreRecipes() {
        for (Material ore : addSpecials(getGTPPOreMaterials())) {
            GTValues.RA.stdBuilder()
                .itemInputs(ore.getOre(1))
                .itemOutputs(ore.getDust(12))
                .eut(OreProcessRecipeEUt)
                .duration(OreProcessRecipeDuration)
                .addTo(RecipeRegister.CheatOreProcessingRecipes);
        }
    }

    public void processGTPPRawOreRecipes() {
        for (Material ore : addSpecials(getGTPPOreMaterials())) {
            GTValues.RA.stdBuilder()
                .itemInputs(ore.getRawOre(1))
                .itemOutputs(ore.getDust(6))
                .eut(OreProcessRecipeEUt)
                .duration(OreProcessRecipeDuration)
                .addTo(RecipeRegister.CheatOreProcessingRecipes);
        }
    }
}
