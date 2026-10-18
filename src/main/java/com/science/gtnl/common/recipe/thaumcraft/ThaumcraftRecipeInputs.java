package com.science.gtnl.common.recipe.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ThaumcraftRecipeInputs {

    private ThaumcraftRecipeInputs() {}

    public static String findOreDictionaryName(Object input) {
        if (!(input instanceof Iterable<?> candidates)) return null;

        // Thaumcraft stores Forge's live list in place of the original ore name. Match that
        // exact list, not a shared ore tag that could broaden an explicit set of alternatives.
        for (Object candidate : candidates) {
            if (!(candidate instanceof ItemStack stack) || stack.getItem() == null) continue;

            for (int oreId : OreDictionary.getOreIDs(stack)) {
                String oreName = OreDictionary.getOreName(oreId);
                if (OreDictionary.getOres(oreName, false) == input) {
                    return oreName;
                }
            }
            return null;
        }
        return null;
    }
}
