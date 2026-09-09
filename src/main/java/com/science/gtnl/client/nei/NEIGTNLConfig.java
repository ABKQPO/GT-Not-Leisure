package com.science.gtnl.client.nei;

import com.science.gtnl.client.gui.GuiDirePatternEncoder;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableAdvancedWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableBasicWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableFurnace;
import com.science.gtnl.common.gui.recipe.RocketAssemblerHandler;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;

import bartworks.system.material.Werkstoff;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;

@SuppressWarnings("unused")
public class NEIGTNLConfig implements IConfigureNEI {

    public static boolean IS_ADDED = true;

    @Override
    public String getName() {
        return "GTNL NEI Plugin";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void loadConfig() {

        IS_ADDED = false;
        new RocketAssemblerHandler(GTNLRecipeMaps.RocketAssemblerRecipes.getDefaultRecipeCategory());
        IS_ADDED = true;

        API.registerGuiOverlay(GuiPortableAdvancedWorkbench.class, "crafting");
        API.registerGuiOverlay(GuiPortableBasicWorkbench.class, "crafting");
        API.registerGuiOverlay(GuiPortableFurnace.class, "smelting");
        API.registerGuiOverlay(GuiPortableFurnace.class, "fuel");
        API.registerGuiOverlay(GuiDirePatternEncoder.class, "extreme");

        API.registerGuiOverlayHandler(GuiDirePatternEncoder.class, new DirePatternOverlayHandler(), "extreme");
        API.registerGuiOverlayHandler(GuiPortableAdvancedWorkbench.class, new DefaultOverlayHandler(), "crafting");
        API.registerGuiOverlayHandler(GuiPortableBasicWorkbench.class, new DefaultOverlayHandler(), "crafting");

        API.addRecipeCatalyst(GTNLItemList.ShimmerBucket.get(1), GTNLRecipeMaps.ShimmerRecipes.unlocalizedName);
        API.addRecipeCatalyst(GTNLItemList.InfinityShimmerBucket.get(1), GTNLRecipeMaps.ShimmerRecipes.unlocalizedName);
        API.addRecipeCatalyst(GTNLItemList.ShimmerFluidBlock.get(1), GTNLRecipeMaps.ShimmerRecipes.unlocalizedName);

        API.addRecipeCatalyst(GTNLItemList.ReactionFurnace.get(1), "smelting");
        API.addRecipeCatalyst(GTNLItemList.LargeSteamFurnace.get(1), "smelting");
        API.addRecipeCatalyst(GTNLItemList.PortableFurnace.get(1), "smelting");
        API.addRecipeCatalyst(GTNLItemList.FurnaceArray.get(1), "smelting");
        API.addRecipeCatalyst(GTNLItemList.PortableBasicWorkBench.get(1), "crafting");
        API.addRecipeCatalyst(GTNLItemList.PortableAdvancedWorkBench.get(1), "crafting");
        API.addRecipeCatalyst(GTNLItemList.AssemblerMatrix.get(1), "crafting");
        API.addRecipeCatalyst(GTNLItemList.DirePatternEncoder.get(1), "extreme");
        API.addRecipeCatalyst(GTNLItemList.SteamOilDrillModuleI.get(1), "GTOrePluginUndergroundFluid");
        API.addRecipeCatalyst(GTNLItemList.SteamOilDrillModuleII.get(1), "GTOrePluginUndergroundFluid");
        API.addRecipeCatalyst(GTNLItemList.SteamOilDrillModuleIII.get(1), "GTOrePluginUndergroundFluid");

        if (Mods.EnhancedLootBags.isModLoaded()) {
            API.addRecipeCatalyst(GTNLItemList.LootBagRedemption.get(1), Mods.EnhancedLootBags.ID);
        }

        Werkstoff[] hiddenMaterials = { GTNLMaterials.Polyimide, GTNLMaterials.AcrylonitrileButadieneStyrene,
            GTNLMaterials.Polyetheretherketone, GTNLMaterials.HSLASteel, GTNLMaterials.Actinium,
            GTNLMaterials.Rutherfordium, GTNLMaterials.Dubnium, GTNLMaterials.Seaborgium, GTNLMaterials.Technetium,
            GTNLMaterials.Bohrium, GTNLMaterials.Hassium, GTNLMaterials.Meitnerium, GTNLMaterials.Darmstadtium,
            GTNLMaterials.Roentgenium, GTNLMaterials.Copernicium, GTNLMaterials.Moscovium, GTNLMaterials.Livermorium,
            GTNLMaterials.Astatine, GTNLMaterials.Tennessine, GTNLMaterials.Francium, GTNLMaterials.Berkelium,
            GTNLMaterials.Einsteinium, GTNLMaterials.Mendelevium, GTNLMaterials.Nobelium, GTNLMaterials.Lawrencium,
            GTNLMaterials.Nihonium, GTNLMaterials.CompressedSteam, GTNLMaterials.Breel, GTNLMaterials.Stronze,
            GTNLMaterials.Periodicium, GTNLMaterials.Stargate };

        OrePrefixes[] orePrefixes = { OrePrefixes.ingotHot, OrePrefixes.toolHeadSaw, OrePrefixes.toolHeadWrench,
            OrePrefixes.toolHeadHammer };

        for (OrePrefixes ore : orePrefixes) {
            for (Werkstoff mat : hiddenMaterials) {
                API.hideItem(mat.get(ore));
            }
        }

        API.hideItem(GTNLItemList.EternalGregTechWorkshopRender.get(1));
        API.hideItem(GTNLItemList.NanoPhagocytosisPlantRender.get(1));
        API.hideItem(GTNLItemList.ArtificialStarRender.get(1));
        API.hideItem(GTNLItemList.NullPointerException.get(1));
        API.hideItem(GTNLItemList.TwilightSword.get(1));
        API.hideItem(GTNLItemList.FakeItemSiren.get(1));
        API.hideItem(ModList.ScienceNotLeisure.ID + ":stick");
        API.hideItem(ModList.ScienceNotLeisure.ID + ":player_doll tag.HideNEI=1b");
    }
}
