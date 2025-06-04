package com.science.gtnl.common.recipe.BloodMagic;

import static gregtech.api.enums.Mods.IndustrialCraft2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.Utils.enums.GTNLItemList;

import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.util.GTModHandler;

public class MeteorsRecipes {

    public static void registerMeteors() {
        MeteorRegistry.registerMeteor(
            GTNLItemList.StargateTier9.get(1),
            new String[] { "SGCraft:stargateBase:0:10", "SGCraft:stargateRing:0:10", "SGCraft:stargateRing:1:10",
                "bartworks:bw.werkstoffblocks.01:25201:10", "SGCraft:ocInterface:0:10", "SGCraft:rfPowerUnit:0:10",
                "gregtech:gt.blockmachines:21113:10", "gregtech:gt.blockmachines:21008:10",
                "ScienceNotLeisure:MetaCasing:21:10", "ScienceNotLeisure:MetaCasing:22:10", "IC2:blockNuke:0:10" },
            10,
            Integer.MAX_VALUE);

        MeteorRegistry.registerMeteor(
            GTModHandler.getModItem(IndustrialCraft2.ID, "blockNuke", 1),
            new String[] { "miscutils:blockMiningExplosives:0:20", "IC2:blockITNT:0:20", "minecraft:tnt:1:15",
                "HardcoreEnderExpansion:enhanced_tnt:0:15", "BloodArsenal:blood_tnt:0:15",
                "EnderZoo:blockConcussionCharge:0:5", "EnderZoo:blockConfusingCharge:0:5",
                "EnderZoo:blockEnderCharge:0:5", "TConstruct:explosive.slime:0:5", "TConstruct:explosive.slime:2:5",
                "ThaumicHorizons:alchemite:0:15", "IC2:blockNuke:0:10", "minecraft:redstone_block:0:1" },
            100,
            114514);

        MeteorRegistry.registerMeteor(
            GTNLItemList.StargateSingularity.get(1),
            generateAndMergeMetalBlocks(
                new String[] { "minecraft:diamond_block:0:5", "minecraft:emerald_block:0:5", "minecraft:coal_block:0:5",
                    "minecraft:gold_block:0:5", "minecraft:iron_block:0:5", "minecraft:lapis_block:0:5",
                    "minecraft:redstone_block:0:5", "miscutils:blockBlockAbyssalAlloy:0:5",
                    "miscutils:blockBlockAdvancedNitinol:0:5", "miscutils:blockBlockAncientGranite:0:5",
                    "miscutils:blockBlockArcanite:0:5", "miscutils:blockBlockArceusAlloy2B:0:5",
                    "miscutils:blockBlockAstralTitanium:0:5", "miscutils:blockBlockBabbitAlloy:0:5",
                    "miscutils:blockBlockBlackMetal:0:5", "miscutils:blockBlockBlackTitanium:0:5",
                    "miscutils:blockBlockBloodSteel:0:5", "miscutils:blockBlockBotmium:0:5",
                    "miscutils:blockBlockCelestialTungsten:0:5", "miscutils:blockBlockChromaticGlass:0:5",
                    "miscutils:blockBlockCinobiteA243:0:5", "miscutils:blockBlockCurium:0:5",
                    "miscutils:blockBlockDragonblood:0:5", "miscutils:blockBlockEglinSteel:0:5",
                    "miscutils:blockBlockEnergyCrystal:0:5", "miscutils:blockBlockFermium:0:5",
                    "miscutils:blockBlockGermanium:0:5", "miscutils:blockBlockGrisium:0:5",
                    "miscutils:blockBlockHastelloyC276:0:5", "miscutils:blockBlockHastelloyN:0:5",
                    "miscutils:blockBlockHastelloyW:0:5", "miscutils:blockBlockHastelloyX:0:5",
                    "miscutils:blockBlockHeLiCoPtEr:0:5", "miscutils:blockBlockHS188A:0:5",
                    "miscutils:blockBlockHypogen:0:5", "miscutils:blockBlockIncoloy020:0:5",
                    "miscutils:blockBlockIncoloyDS:0:5", "miscutils:blockBlockIncoloyMA956:0:5",
                    "miscutils:blockBlockInconel625:0:5", "miscutils:blockBlockInconel690:0:5",
                    "miscutils:blockBlockInconel792:0:5", "miscutils:blockBlockIndalloy140:0:5",
                    "miscutils:blockBlockIodine:0:5", "miscutils:blockBlockLafiumCompound:0:5",
                    "miscutils:blockBlockLaurenium:0:5", "miscutils:blockBlockLithium7:0:5",
                    "miscutils:blockBlockMaragingSteel250:0:5", "miscutils:blockBlockMaragingSteel300:0:5",
                    "miscutils:blockBlockMaragingSteel350:0:5", "miscutils:blockBlockNeptunium:0:5",
                    "miscutils:blockBlockNiobiumCarbide:0:5", "miscutils:blockBlockNitinol60:0:5",
                    "miscutils:blockBlockOctiron:0:5", "miscutils:blockBlockPikyonium64B:0:5",
                    "miscutils:blockBlockPlutonium238:0:5", "miscutils:blockBlockPolonium:0:5",
                    "miscutils:blockBlockPotin:0:5", "miscutils:blockBlockProtactinium:0:5",
                    "miscutils:blockBlockQuantum:0:5", "miscutils:blockBlockRadium:0:5",
                    "miscutils:blockBlockRhenium:0:5", "miscutils:blockBlockRhugnor:0:5",
                    "miscutils:blockBlockRunite:0:5", "miscutils:blockBlockSelenium:0:5",
                    "miscutils:blockBlockSiliconCarbide:0:5", "miscutils:blockBlockStaballoy:0:5",
                    "miscutils:blockBlockStellite:0:5", "miscutils:blockBlockTalonite:0:5",
                    "miscutils:blockBlockTantalumCarbide:0:5", "miscutils:blockBlockTantalloy60:0:5",
                    "miscutils:blockBlockTantalloy61:0:5", "miscutils:blockBlockThallium:0:5",
                    "miscutils:blockBlockTitansteel:0:5", "miscutils:blockBlockTriniumNaquadahAlloy:0:5",
                    "miscutils:blockBlockTriniumNaquadahCarbonite:0:5", "miscutils:blockBlockTriniumTitaniumAlloy:0:5",
                    "miscutils:blockBlockTumbaga:0:5", "miscutils:blockBlockTungstenTitaniumCarbide:0:5",
                    "miscutils:blockBlockUranium232:0:5", "miscutils:blockBlockUranium233:0:5",
                    "miscutils:blockBlockWatertightSteel:0:5", "miscutils:blockBlockWhiteMetal:0:5",
                    "miscutils:blockBlockZeron100:0:5", "miscutils:blockBlockZirconiumCarbide:0:5" }),
            100,
            Integer.MAX_VALUE);
    }

    private static final Map<String, Boolean> ORE_DICT_CACHE = new HashMap<>();

    public static String[] generateAndMergeMetalBlocks(String[] originalFillers) {
        List<String> metalVariants = new ArrayList<>();

        for (int blockType = 1; blockType <= 9; blockType++) {
            int maxMeta = (blockType == 9) ? 11 : 15;
            for (int meta = 0; meta <= maxMeta; meta++) {
                String blockKey = String.format("gregtech:gt.blockmetal%d:%d", blockType, meta);
                if (isOreDictRegistered(blockKey)) {
                    metalVariants.add(blockKey + ":5");
                }
            }
        }

        for (int meta = 0; meta <= 32767; meta++) {
            String blockKey = String.format("bartworks:bw.werkstoffblocks.01:%d", meta);
            if (isOreDictRegistered(blockKey)) {
                metalVariants.add(blockKey + ":5");
            }
        }
        if (originalFillers != null) {
            metalVariants.addAll(Arrays.asList(originalFillers));
        }
        return metalVariants.toArray(new String[0]);
    }

    private static boolean isOreDictRegistered(String blockKey) {
        return ORE_DICT_CACHE.computeIfAbsent(blockKey, k -> {
            String[] parts = blockKey.split(":");
            if (parts.length < 3) return false;
            String modId = parts[0];
            String itemName = parts[1];
            int meta = Integer.parseInt(parts[2]);
            ItemStack stack = GameRegistry.findItemStack(modId, itemName, 1);
            if (stack == null) return false;
            stack.setItemDamage(meta);
            int[] oreIds = OreDictionary.getOreIDs(stack);
            return oreIds.length > 0;
        });
    }
}
