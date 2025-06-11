package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.enums.Materials.getAll;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.science.gtnl.Utils.recipes.EyeOfHarmonyRecipeFactory;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gtPlusPlus.core.material.Material;

public class CustomEyeOfHarmonyRecipe {

    public static List<Pair<ItemStack, Long>> getAllDustsAsPairsFromGregtech() {
        List<Pair<ItemStack, Long>> result = new ArrayList<>();

        for (Materials material : getAll()) {
            if (material == null) continue;
            ItemStack dust = material.getDust(1);
            if (dust != null) {
                result.add(Pair.of(dust, Long.MAX_VALUE));
            }
        }

        for (Werkstoff werkstoff : Werkstoff.werkstoffHashSet) {
            if (werkstoff == null) continue;
            ItemStack dust = werkstoff.get(OrePrefixes.dust, 1);
            if (dust != null) {
                result.add(Pair.of(dust, Long.MAX_VALUE));
            }
        }

        for (Material material : Material.mMaterialMap) {
            if (material == null) continue;
            ItemStack dust = material.getDust(1);
            if (dust != null) {
                result.add(Pair.of(dust.copy(), Long.MAX_VALUE));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void loadRecipes() {
        ArrayList<Pair<ItemStack, Long>> baseList = Lists.newArrayList();
        baseList.addAll(getAllDustsAsPairsFromGregtech());

        EyeOfHarmonyRecipeFactory.addCustomRecipeEntry(
            new ItemStack(Blocks.dirt, 1),
            baseList,
            Lists.newArrayList(
                Pair.of(MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE), (long) Integer.MAX_VALUE),
                Pair.of(MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE), (long) Integer.MAX_VALUE),
                Pair.of(MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE), (long) Integer.MAX_VALUE)),
            1,
            1145141919810L,
            19198101919810L,
            1145141919810L,
            1145141919810L,
            114514,
            0.01);
    }
}
