package com.reavaritia.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.reavaritia.common.BlockLoader;
import com.reavaritia.common.ItemLoader;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class MissingMappingsHandler {

    private static final Map<String, Item> ITEM_MAPPINGS = new HashMap<>();
    private static final Map<String, Block> BLOCK_MAPPINGS = new HashMap<>();

    static {
        String[] names = { "CrystalSword", "CrystalAxe", "CrystalPickaxe", "CrystalShovel", "CrystalHoe", "BlazeSword",
            "BlazeAxe", "BlazePickaxe", "BlazeShovel", "BlazeHoe", "InfinitySword", "InfinityAxe", "InfinityPickaxe",
            "InfinityShovel", "InfinityHoe", "InfinityTotem", "InfinityBucket", "MatterCluster", "ChronarchsClock",
            "InfinityElytra" };
        Item[] items = { ItemLoader.CrystalSword, ItemLoader.CrystalAxe, ItemLoader.CrystalPickaxe,
            ItemLoader.CrystalShovel, ItemLoader.CrystalHoe, ItemLoader.BlazeSword, ItemLoader.BlazeAxe,
            ItemLoader.BlazePickaxe, ItemLoader.BlazeShovel, ItemLoader.BlazeHoe, ItemLoader.InfinitySword,
            ItemLoader.InfinityAxe, ItemLoader.InfinityPickaxe, ItemLoader.InfinityShovel, ItemLoader.InfinityHoe,
            ItemLoader.InfinityTotem, ItemLoader.InfinityBucket, ItemLoader.MatterCluster, ItemLoader.ChronarchsClock,
            ItemLoader.InfinityElytra };
        for (int i = 0; i < names.length; i++) registerAliases(ITEM_MAPPINGS, names[i], items[i]);
        registerAliases(BLOCK_MAPPINGS, "ExtremeAnvil", BlockLoader.ExtremeAnvil);
        registerAliases(BLOCK_MAPPINGS, "BlockSoulFarmland", BlockLoader.BlockSoulFarmland);
        registerAliases(BLOCK_MAPPINGS, "NeutronCollector", BlockLoader.NeutronCollector);
        registerAliases(BLOCK_MAPPINGS, "DenseNeutronCollector", BlockLoader.DenseNeutronCollector);
        registerAliases(BLOCK_MAPPINGS, "DenserNeutronCollector", BlockLoader.DenserNeutronCollector);
        registerAliases(BLOCK_MAPPINGS, "DensestNeutronCollector", BlockLoader.DensestNeutronCollector);
        registerAliases(ITEM_MAPPINGS, "BlockSoulFarmland", Item.getItemFromBlock(BlockLoader.BlockSoulFarmland));
        registerAliases(ITEM_MAPPINGS, "ExtremeAnvil", Item.getItemFromBlock(BlockLoader.ExtremeAnvil));
        registerAliases(ITEM_MAPPINGS, "NeutronCollector", Item.getItemFromBlock(BlockLoader.NeutronCollector));
        registerAliases(
            ITEM_MAPPINGS,
            "DenseNeutronCollector",
            Item.getItemFromBlock(BlockLoader.DenseNeutronCollector));
        registerAliases(
            ITEM_MAPPINGS,
            "DenserNeutronCollector",
            Item.getItemFromBlock(BlockLoader.DenserNeutronCollector));
        registerAliases(
            ITEM_MAPPINGS,
            "DensestNeutronCollector",
            Item.getItemFromBlock(BlockLoader.DensestNeutronCollector));
    }

    public static void handleMappings(List<FMLMissingMappingsEvent.MissingMapping> mappings) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : mappings) {
            if (mapping.type == GameRegistry.Type.ITEM) {
                Item item = ITEM_MAPPINGS.get(mapping.name);
                if (item == null) item = GameRegistry.findItem("reavaritia", normalizeLegacyId(mapping.name));
                if (item != null) mapping.remap(item);
            } else if (mapping.type == GameRegistry.Type.BLOCK) {
                Block block = BLOCK_MAPPINGS.get(mapping.name);
                if (block == null) block = GameRegistry.findBlock("reavaritia", normalizeLegacyId(mapping.name));
                if (block != null) mapping.remap(block);
            }
        }
    }

    private static <T> void registerAliases(Map<String, T> mappings, String legacyName, T value) {
        String base = "reavaritia:" + legacyName;
        mappings.put(base, value);
        mappings.put("reavaritia:item." + legacyName, value);
        mappings.put("reavaritia:tile." + legacyName, value);
    }

    private static String normalizeLegacyId(String legacyId) {
        String prefix = "reavaritia:";
        if (!legacyId.startsWith(prefix)) return legacyId;
        String id = legacyId.substring(prefix.length());
        if (id.startsWith("item.") || id.startsWith("tile.")) id = id.substring(5);
        return id.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
            .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
            .replace('.', '_')
            .toLowerCase(Locale.ROOT);
    }
}
