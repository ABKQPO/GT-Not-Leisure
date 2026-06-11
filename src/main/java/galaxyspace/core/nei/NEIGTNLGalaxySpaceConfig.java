package galaxyspace.core.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.Optional;
import galaxyspace.core.register.GSBlocks;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

@SuppressWarnings("unused")
public class NEIGTNLGalaxySpaceConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        if (Mods.StevesCarts2.isModLoaded() && Mods.Railcraft.isModLoaded()
            && Mods.IronTanks.isModLoaded()
            && Mods.GraviSuite.isModLoaded()) {
            CustomRockRecipeHandler rocketRecipeHandlerSteam = new CustomRockRecipeHandler(
                1,
                4,
                122,
                "sciencenotleisure.rocketSteam",
                "sciencenotleisure.nei.rocket.RocketSteam");
            this.addRocketSteamRecipes(rocketRecipeHandlerSteam);
            API.registerRecipeHandler(rocketRecipeHandlerSteam);
            API.registerUsageHandler(rocketRecipeHandlerSteam);
        }
    }

    @Override
    public String getName() {
        return "GTNL:GC/GS NEI Plugin";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    public void addRocketSteamRecipes(RocketRecipeHandler recipeHandler) {
        int x = 4;
        int y = 4 - recipeHandler.y;

        List<PositionedStack> input = new ArrayList<>();
        input.add(
            new PositionedStack(GTModHandler.getModItem(Mods.StevesCarts2.ID, "CartModule", 1, 38), 134 - x, 10 - y));
        input.add(
            new PositionedStack(
                Mods.NewHorizonsCoreMod.isModLoaded() ? getEngineCore() : new ItemStack(Items.arrow),
                134 - x,
                28 - y));
        input.add(new PositionedStack(GTModHandler.getModItem(Mods.IronTanks.ID, "diamondTank", 1), 117 - x, 19 - y));
        input.add(new PositionedStack(GTModHandler.getModItem(Mods.IronTanks.ID, "diamondTank", 1), 152 - x, 19 - y));
        input.add(
            new PositionedStack(
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.BlackSteel, 1),
                53 - x,
                19 - y));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                input.add(
                    new PositionedStack(
                        GTNLMaterials.CompressedSteam.get(OrePrefixes.plateSuperdense, 1),
                        44 + j * 18 - x,
                        37 + i * 18 - y));
            }
        }

        input.add(
            new PositionedStack(GTModHandler.getModItem(Mods.GraviSuite.ID, "itemSimpleItem", 1, 6), 53 - x, 109 - y));
        input
            .add(new PositionedStack(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.beta", 1, 7), 26 - x, 91 - y));
        input
            .add(new PositionedStack(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.beta", 1, 7), 80 - x, 91 - y));
        input.add(
            new PositionedStack(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.beta", 1, 7), 26 - x, 109 - y));
        input.add(
            new PositionedStack(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.beta", 1, 7), 80 - x, 109 - y));

        recipeHandler.addRecipe(input, new PositionedStack(GTNLItemList.SteamRocket.get(1), 134 - x, 73 - y));

        List<PositionedStack> input2 = new ArrayList<>(input);
        input2.add(new PositionedStack(new ItemStack(GSBlocks.ironChest, 1, 3), 134 - x, 46 - y));
        recipeHandler
            .addRecipe(input2, new PositionedStack(GTNLItemList.SteamRocket.getWithMeta(1, 1), 134 - x, 73 - y));

        input2 = new ArrayList<>(input);
        input2.add(new PositionedStack(new ItemStack(GSBlocks.ironChest, 1, 0), 134 - x, 46 - y));
        recipeHandler
            .addRecipe(input2, new PositionedStack(GTNLItemList.SteamRocket.getWithMeta(1, 2), 134 - x, 73 - y));

        input2 = new ArrayList<>(input);
        input2.add(new PositionedStack(new ItemStack(GSBlocks.ironChest, 1, 1), 134 - x, 46 - y));
        recipeHandler
            .addRecipe(input2, new PositionedStack(GTNLItemList.SteamRocket.getWithMeta(1, 3), 134 - x, 73 - y));
    }

    @Optional.Method(modid = "dreamcraft")
    public ItemStack getEngineCore() {
        return NHItemList.EngineCore.get(1);
    }
}
