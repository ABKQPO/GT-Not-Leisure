package com.science.gtnl.utils.machine.oreProcessing;

import static gtPlusPlus.core.material.MaterialsAlloy.KOBOLDITE;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsOres;

public class GTPPOreHandler {

    public Set<Material> addSpecials(Set<Material> set) {
        set.add(MaterialMisc.RARE_EARTH_LOW);
        set.add(MaterialMisc.RARE_EARTH_MID);
        set.add(MaterialMisc.RARE_EARTH_HIGH);
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
                ScienceNotLeisure.LOG.info("Catch an IllegalAccessException in GTPPOreHandler.processGTPPOreRecipes");
            }
        }
        return gtppOres;
    }

    public void processGTPPOreRecipes() {
        for (Material ore : addSpecials(getGTPPOreMaterials())) {
            RecipeBuilder.builder()
                .itemInputs(ore.getOre(1))
                .itemOutputs(ore.getDust(Integer.MAX_VALUE))
                .eut(0)
                .duration(1)
                .addTo(GTNLRecipeMaps.CheatOreProcessingRecipes);
        }
    }

    public void processGTPPRawOreRecipes() {
        for (Material ore : addSpecials(getGTPPOreMaterials())) {
            RecipeBuilder.builder()
                .itemInputs(ore.getRawOre(1))
                .itemOutputs(ore.getDust(Integer.MAX_VALUE))
                .eut(0)
                .duration(1)
                .addTo(GTNLRecipeMaps.CheatOreProcessingRecipes);
        }
    }
}
