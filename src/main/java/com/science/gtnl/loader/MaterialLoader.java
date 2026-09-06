package com.science.gtnl.loader;

import static com.science.gtnl.utils.CardboardBoxUtils.addBoxBlacklist;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.oredict.OreDictionary;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.cleanroommc.bogosorter.BogoSortAPI;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.api.stellar.StellarIrisUpgradeCatalog;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.common.item.steamRocket.SchematicSteamRocket;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAvaritiaddonsChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableChest;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;

import bartworks.API.WerkstoffAdderRegistry;
import bartworks.common.loaders.ItemRegistry;
import cpw.mods.fml.common.Optional;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GlassTier;
import gregtech.common.misc.WirelessNetworkManager;
import micdoodle8.mods.galacticraft.api.recipe.RocketFuels;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;

public class MaterialLoader {

    public static void loadPreInit() {
        EffectLoader.registry();
        EntityLoader.registry();
        if (Mods.BetterQuesting.isModLoaded() && ModList.BetterQuestingAPI.isModLoaded()) {
            QuestLoader.registry();
        }

        BlockLoader.registry();
        BlockLoader.registerTreeBrickuoia();

        ItemLoader.registry();
        WerkstoffAdderRegistry.addWerkstoffAdder(new GTNLMaterials());
    }

    public static void loadInit() {
        GTNLMaterials.init();
        WailaLoader.register();
        TickrateAPI.changeTickrate(MainConfig.tickrate.defaultTickrate);

        GTNLStructureChannels.register();

        for (int i = 0; i < 14; i++) {
            GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING
                .registerAsIndicator(new ItemStack(Loaders.componentAssemblylineCasing, 1, i), i + 1);
        }

        registryOreDictionary();
    }

    public static void loadPostInit() {
        MilledOre.registry();
        GreenHouseBucket.LoadGreenHouseBuckets();
        MachineLoader.registry();
        AchievementsLoader.registry();
        StellarIrisUpgradeCatalog.registerDefaults();

        if (Mods.GalaxySpace.isModLoaded() && Mods.StevesCarts2.isModLoaded()
            && Mods.Railcraft.isModLoaded()
            && Mods.IronTanks.isModLoaded()
            && Mods.GraviSuite.isModLoaded()) {
            SchematicRegistry.registerSchematicRecipe(new SchematicSteamRocket());
            RocketAssemblerRecipes.loadSteamRocketRecipe();
        }
        RocketFuels.addFuel(EntitySteamRocket.class, GTNLMaterials.CompressedSteam.getMolten(1));

        if (Mods.InventoryBogoSorter.isModLoaded()) loadCraftTweak();

        OrePrefixes.nugget.addFamiliarPrefix(OrePrefixes.ingotHot);
        OrePrefixes.ingot.addFamiliarPrefix(OrePrefixes.ingotHot);
        OrePrefixes.ingotHot.addFamiliarPrefix(OrePrefixes.nugget);
        OrePrefixes.ingotHot.addFamiliarPrefix(OrePrefixes.ingot);
    }

    public static void loadCompleteInit() {
        if (Mods.NewHorizonsCoreMod.isModLoaded()) ScriptLoader.registry();

        if (Mods.Nutrition.isModLoaded()) NutrientLoader.registry();

        loadCardBoardBoxBlackList();

        if (MainConfig.item.stick.enableStickItem) {
            RecipeLoader.loadVillageTrade();
        }

        WirelessNetworkManager.number_of_energy_additions = 4L;

        LoadCompleteRecipeScheduler.schedule();
    }

    @Optional.Method(modid = "bogosorter")
    public static void loadCraftTweak() {
        SimpleTweakProvider provider = CraftingTweaksAPI
            .registerSimpleProvider(ModList.ScienceNotLeisure.ID, ContainerPortableAdvancedWorkbench.class);
        provider.setTweakRotate(true, true, 0, 0);
        provider.setTweakBalance(true, true, 0, 0);
        provider.setTweakClear(true, true, 0, 0);
        provider.setAlignToGrid(EnumFacing.WEST);
        BogoSortAPI.INSTANCE.addGenericCompat(ContainerPortableChest.class);
        BogoSortAPI.INSTANCE.addGenericCompat(ContainerPortableAvaritiaddonsChest.class);
    }

    public static void registryOreDictionary() {
        GlassTier.addCustomGlass(ItemRegistry.bw_realglas2, 6, 13, 1);
        GTOreDictUnificator
            .registerOre("blockGlass" + GTValues.VN[13], GTNLItemList.ShirabonReinforcedBoronSilicateGlass.get(1));

        if (MainConfig.item.player_doll.enableRegisterMAXTierGlass) {
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 1, 14, 1);
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 2, 14, 1);
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 3, 14, 1);
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 4, 14, 1);
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 5, 14, 1);
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 0, 14, 1);
            GTOreDictUnificator.registerOre("blockGlass" + GTValues.VN[14], GTNLItemList.PlayerDoll.get(1));
        }

        GlassTier.addCustomGlass(ItemRegistry.bw_realglas2, 7, 14, 2);
        GTOreDictUnificator.registerOre(
            "blockGlass" + GTValues.VN[14],
            GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.get(1));

        GlassTier.addCustomGlass(BlockLoader.metaBlockGlass, 0, 10, 2);
        GTOreDictUnificator.registerOre("blockGlass" + GTValues.VN[10], GTNLItemList.GaiaGlass.get(1));

        GlassTier.addCustomGlass(BlockLoader.metaBlockGlass, 1, 8, 2);
        GTOreDictUnificator.registerOre("blockGlass" + GTValues.VN[8], GTNLItemList.TerraGlass.get(1));

        GlassTier.addCustomGlass(BlockLoader.metaBlockGlass, 2, 7, 1);
        GTOreDictUnificator.registerOre("blockGlass" + GTValues.VN[7], GTNLItemList.FusionGlass.get(1));

        for (int lampMeta = 1; lampMeta <= 32; lampMeta++) {
            GlassTier.addCustomGlass(BlockLoader.metaBlockGlow, lampMeta, 3, 1);
            GTOreDictUnificator
                .registerOre("blockGlass" + GTValues.VN[3], new ItemStack(BlockLoader.metaBlockGlow, lampMeta));
        }

        for (int lampOffMeta = 3; lampOffMeta <= 34; lampOffMeta++) {
            GlassTier.addCustomGlass(BlockLoader.metaBlock, lampOffMeta, 3, 1);
            GTOreDictUnificator
                .registerOre("blockGlass" + GTValues.VN[3], new ItemStack(BlockLoader.metaBlock, lampOffMeta));
        }

        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNLItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNLItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNLItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNLItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNLItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.IV), GTNLItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.LuV), GTNLItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ZPM), GTNLItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.UV), GTNLItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UHV), GTNLItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UEV), GTNLItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UIV), GTNLItemList.CircuitResonaticUIV.get(1));

        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNLItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNLItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNLItemList.BasicCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNLItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNLItemList.EliteCircuit.get(1));

        GTOreDictUnificator.registerOre("record", GTNLItemList.RecordSus.get(1));

        var shimmerOre = new ItemStack(BlockLoader.shimmerFluidBlock, 1, OreDictionary.WILDCARD_VALUE);
        var shimmerMaterial = GTNLMaterials.Shimmer.getBridgeMaterial();
        GTOreDictUnificator.registerOre(OrePrefixes.ore.get(shimmerMaterial), shimmerOre);
        GTOreDictUnificator.addAssociation(OrePrefixes.ore, shimmerMaterial, shimmerOre, false);
    }

    public static void loadCardBoardBoxBlackList() {
        addBoxBlacklist(Blocks.wooden_door, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(Blocks.iron_door, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(BlockLoader.cardboardBox, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(ModBlocks.reactorCore, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(ModBlocks.chaosCrystal, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockGenerator", 1, 5));
        addBoxBlacklist(
            GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockReactorChamber", 1, OreDictionary.WILDCARD_VALUE));
    }
}
