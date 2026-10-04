package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;

public class PortalToAlfheimRecipes implements IRecipePool {

    public RecipeMap<?> PTAR = GTNLRecipeMaps.PortalToAlfheimRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.bread, 1))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    Integer.MAX_VALUE,
                    ModsItemlist.IC2BlockNuke.get(1)
                        .setStackDisplayName(
                            StatCollector.translateToLocal("gtnl.recipe.portal_to_alfheim.invalid_input"))))
            .duration(1200)
            .eut(0)
            .fake()
            .addTo(PTAR);

        for (RecipeElvenTrade recipe : BotaniaAPI.elvenTradeRecipes) {

            List<Object> originInputs = recipe.getInputs();
            ItemStack output = recipe.getOutput()
                .copy();

            boolean hasOreDict = false;

            List<Object> inputs = new ArrayList<>();

            for (Object input : originInputs) {

                if (input instanceof String oreName) {

                    hasOreDict = true;
                    inputs.add(new OreDictItemStack(oreName, 1));

                } else if (input instanceof ItemStack stack) {

                    ItemStack copy = stack.copy();

                    if (copy.getItemDamage() == Short.MAX_VALUE) {
                        copy.setItemDamage(0);
                    }

                    inputs.add(copy);
                }
            }

            RecipeBuilder builder = RecipeBuilder.builder()
                .itemOutputs(output)
                .duration(20)
                .eut(2048);

            if (hasOreDict) {
                builder.itemInputs(inputs.toArray(new Object[0]));
            } else {
                builder.itemInputs(inputs.toArray(new ItemStack[0]));
            }
            builder.addTo(PTAR);
        }

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(256, ModsItemlist.IC2BlockITNT.get(1)),
                new ItemStack(Blocks.beacon, 0),
                GTNLItemList.ActivatedGaiaPylon.get(0),
                ModsItemlist.BotaniaGaiaSpiritIngot.get(1))
            .itemOutputs(
                ModsItemlist.BotaniaGaiaSpirit.get(16),
                ModsItemlist.BotaniaDice.get(1),
                ModsItemlist.BotaniaBlackLotus.get(1),
                ModsItemlist.BotaniaBlackestLotus.get(1),
                ModsItemlist.BotaniaAncientWill.get(1),
                ModsItemlist.BotaniaWillOfDharok.get(1),
                ModsItemlist.BotaniaWillOfGuthan.get(1),
                ModsItemlist.BotaniaWillOfTorag.get(1),
                ModsItemlist.BotaniaWillOfVerac.get(1),
                ModsItemlist.BotaniaWillOfKaril.get(1),
                ModsItemlist.BotaniaOvergrowthSeedDamage3.get(1),
                ModsItemlist.BotaniaManasteelIngot.get(16),
                ModsItemlist.BotaniaManaPearl.get(8),
                ModsItemlist.BotaniaManaDiamond.get(4),
                ModsItemlist.BotaniaWaterRune.get(2),
                ModsItemlist.BotaniaFireRune.get(2),
                ModsItemlist.BotaniaEarthRune.get(2),
                ModsItemlist.BotaniaAirRune.get(2),
                ModsItemlist.BotaniaSpringRune.get(2),
                ModsItemlist.BotaniaSummerRune.get(2),
                ModsItemlist.BotaniaAutumnRune.get(2),
                ModsItemlist.BotaniaWinterRune.get(2),
                ModsItemlist.BotaniaManaRune.get(2),
                ModsItemlist.BotaniaLustRune.get(2),
                ModsItemlist.BotaniaGluttonyRune.get(2),
                ModsItemlist.BotaniaGreedRune.get(2),
                ModsItemlist.BotaniaSlothRune.get(2),
                ModsItemlist.BotaniaWrathRune.get(2),
                ModsItemlist.BotaniaEnvyRune.get(2),
                ModsItemlist.BotaniaPrideRune.get(2),
                ModsItemlist.BotaniaPinkinator.get(1),
                ModsItemlist.BotaniaRecordGaia2.get(1),
                new ItemStack(Items.record_13, 1),
                new ItemStack(Items.record_wait, 1),
                ModsItemlist.BotaniaGaiaHead.get(1))
            .outputChances(
                10000,
                10000,
                650,
                560,
                930,
                1667,
                1667,
                1667,
                1667,
                1667,
                2500,
                9000,
                7000,
                5000,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                2000,
                5720,
                139,
                139,
                1)
            .duration(1200)
            .eut(122880)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.AvaritiaInfinitySword.get(0), ModsItemlist.BotaniaGaiaSpiritIngot.get(1))
            .itemOutputs(
                ModsItemlist.BotaniaGaiaSpirit.get(16),
                ModsItemlist.BotaniaDice.get(1),
                ModsItemlist.BotaniaBlackLotus.get(1),
                ModsItemlist.BotaniaBlackestLotus.get(1),
                ModsItemlist.BotaniaAncientWill.get(1),
                ModsItemlist.BotaniaWillOfDharok.get(1),
                ModsItemlist.BotaniaWillOfGuthan.get(1),
                ModsItemlist.BotaniaWillOfTorag.get(1),
                ModsItemlist.BotaniaWillOfVerac.get(1),
                ModsItemlist.BotaniaWillOfKaril.get(1),
                ModsItemlist.BotaniaOvergrowthSeedDamage3.get(1),
                ModsItemlist.BotaniaManasteelIngot.get(16),
                ModsItemlist.BotaniaManaPearl.get(8),
                ModsItemlist.BotaniaManaDiamond.get(4),
                ModsItemlist.BotaniaWaterRune.get(2),
                ModsItemlist.BotaniaFireRune.get(2),
                ModsItemlist.BotaniaEarthRune.get(2),
                ModsItemlist.BotaniaAirRune.get(2),
                ModsItemlist.BotaniaSpringRune.get(2),
                ModsItemlist.BotaniaSummerRune.get(2),
                ModsItemlist.BotaniaAutumnRune.get(2),
                ModsItemlist.BotaniaWinterRune.get(2),
                ModsItemlist.BotaniaManaRune.get(2),
                ModsItemlist.BotaniaLustRune.get(2),
                ModsItemlist.BotaniaGluttonyRune.get(2),
                ModsItemlist.BotaniaGreedRune.get(2),
                ModsItemlist.BotaniaSlothRune.get(2),
                ModsItemlist.BotaniaWrathRune.get(2),
                ModsItemlist.BotaniaEnvyRune.get(2),
                ModsItemlist.BotaniaPrideRune.get(2),
                ModsItemlist.BotaniaPinkinator.get(1),
                ModsItemlist.BotaniaRecordGaia2.get(1),
                new ItemStack(Items.record_13, 1),
                new ItemStack(Items.record_wait, 1),
                ModsItemlist.BotaniaGaiaHead.get(1))
            .outputChances(
                10000,
                10000,
                650,
                560,
                930,
                1667,
                1667,
                1667,
                1667,
                1667,
                2500,
                9000,
                7000,
                5000,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                2000,
                5720,
                139,
                139,
                1)
            .duration(100)
            .eut(7864320)
            .addTo(PTAR);
    }
}
