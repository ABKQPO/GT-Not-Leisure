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
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.common.item.steamRocket.SchematicSteamRocket;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;
import com.science.gtnl.common.recipe.oreDictionary.LaserEngraverOreRecipes;
import com.science.gtnl.common.recipe.oreDictionary.SteamCarpenterOreRecipe;
import com.science.gtnl.common.recipe.oreDictionary.WoodDistillationRecipes;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAvaritiaddonsChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableChest;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;

import bartworks.API.WerkstoffAdderRegistry;
import cpw.mods.fml.common.Optional;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
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

        loadOreDictionaryRecipes();
    }

    public static void loadInit() {
        MachineLoader.registerGlasses();
        WailaLoader.register();
        TickrateAPI.changeTickrate(MainConfig.tickrate.defaultTickrate);

        GTNLStructureChannels.register();

        for (int i = 0; i < 14; i++) {
            GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING
                .registerAsIndicator(new ItemStack(Loaders.componentAssemblylineCasing, 1, i), i + 1);
        }
    }

    public static void loadPostInit() {
        MilledOre.registry();
        GreenHouseBucket.LoadGreenHouseBuckets();
        MachineLoader.registry();
        AchievementsLoader.registry();

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

    public static void loadOreDictionaryRecipes() {
        ScienceNotLeisure.LOG.info("GTNL: Register Ore Dictionary Recipe.");
        new WoodDistillationRecipes();
        new LaserEngraverOreRecipes();
        new SteamCarpenterOreRecipe();
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
