package com.science.gtnl.loader;

import static com.science.gtnl.utils.CardboardBoxUtils.addBoxBlacklist;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.text.LanguageLoader;

import bartworks.API.WerkstoffAdderRegistry;
import bartworks.common.loaders.ItemRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GlassTier;
import gregtech.common.misc.WirelessNetworkManager;
import micdoodle8.mods.galacticraft.api.recipe.RocketFuels;

public class MaterialLoader implements IContentLoader {

    @Override
    public String name() {
        return "materials";
    }

    @Override
    public void preInit() {
        WerkstoffAdderRegistry.addWerkstoffAdder(new GTNLMaterials());
    }

    @Override
    public void init() {
        GTNLMaterials.init();
        registryOreDictionary();
    }

    @Override
    public void postInit() {
        MilledOre.registry();
        RocketFuels.addFuel(EntitySteamRocket.class, GTNLMaterials.CompressedSteam.getMolten(1));
        OrePrefixes.nugget.addFamiliarPrefix(OrePrefixes.ingotHot);
        OrePrefixes.ingot.addFamiliarPrefix(OrePrefixes.ingotHot);
        OrePrefixes.ingotHot.addFamiliarPrefix(OrePrefixes.nugget);
        OrePrefixes.ingotHot.addFamiliarPrefix(OrePrefixes.ingot);
    }

    @Override
    public void loadComplete() {
        loadCardBoardBoxBlackList();
        WirelessNetworkManager.number_of_energy_additions = 4L;
        LanguageLoader.registry();
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

    }

    public static void loadCardBoardBoxBlackList() {
        addBoxBlacklist(Blocks.wooden_door, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(Blocks.iron_door, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(BlockLoader.cardboardBox, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(ModBlocks.reactorCore, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(ModBlocks.chaosCrystal, OreDictionary.WILDCARD_VALUE);
        addBoxBlacklist(ModsItemlist.IC2NuclearReactor.get(1));
        addBoxBlacklist(ModsItemlist.IC2ReactorChamberAnyDamage.get(1));
    }
}
