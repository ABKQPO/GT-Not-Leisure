package com.science.gtnl.utils.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.ItemLoader;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class MissingMappingsHandler {

    // spotless:off
    private static final Remapper REMAPPER = new Remapper()
        // Block remappings
        .remapBlock("sciencenotleisure:StargateTier0", BlockLoader.compressedStargateTier0)
        .remapBlock("sciencenotleisure:StargateTier1", BlockLoader.compressedStargateTier1)
        .remapBlock("sciencenotleisure:StargateTier2", BlockLoader.compressedStargateTier2)
        .remapBlock("sciencenotleisure:StargateTier3", BlockLoader.compressedStargateTier3)
        .remapBlock("sciencenotleisure:StargateTier4", BlockLoader.compressedStargateTier4)
        .remapBlock("sciencenotleisure:StargateTier5", BlockLoader.compressedStargateTier5)
        .remapBlock("sciencenotleisure:StargateTier6", BlockLoader.compressedStargateTier6)
        .remapBlock("sciencenotleisure:StargateTier7", BlockLoader.compressedStargateTier7)
        .remapBlock("sciencenotleisure:StargateTier8", BlockLoader.compressedStargateTier8)
        .remapBlock("sciencenotleisure:StargateTier9", BlockLoader.compressedStargateTier9)

        .remapBlock("sciencenotleisure:MetaBlockGlow", BlockLoader.metaBlockGlow)
        .remapBlock("sciencenotleisure:tile.MetaBlockGlow", BlockLoader.metaBlockGlow)
        .remapBlock("sciencenotleisure:MetaBlockGlass", BlockLoader.metaBlockGlass)
        .remapBlock("sciencenotleisure:tile.MetaBlockGlass", BlockLoader.metaBlockGlass)
        .remapBlock("sciencenotleisure:MetaBlockColumn", BlockLoader.metaBlockColumn)
        .remapBlock("sciencenotleisure:tile.MetaBlockColumn", BlockLoader.metaBlockColumn)
        .remapBlock("sciencenotleisure:MetaBlock", BlockLoader.metaBlock)
        .remapBlock("sciencenotleisure:tile.MetaBlock", BlockLoader.metaBlock)
        .remapBlock("sciencenotleisure:MetaCasing02", BlockLoader.metaCasing02)
        .remapBlock("sciencenotleisure:tile.MetaCasing02", BlockLoader.metaCasing02)
        .remapBlock("sciencenotleisure:MetaCasing", BlockLoader.metaCasing)
        .remapBlock("sciencenotleisure:tile.MetaCasing", BlockLoader.metaCasing)
        .remapBlock("sciencenotleisure:PlayerLeash", BlockLoader.playerLeash)
        .remapBlock("sciencenotleisure:CompressedStargateTier0", BlockLoader.compressedStargateTier0)
        .remapBlock("sciencenotleisure:CompressedStargateTier1", BlockLoader.compressedStargateTier1)
        .remapBlock("sciencenotleisure:CompressedStargateTier2", BlockLoader.compressedStargateTier2)
        .remapBlock("sciencenotleisure:CompressedStargateTier3", BlockLoader.compressedStargateTier3)
        .remapBlock("sciencenotleisure:CompressedStargateTier4", BlockLoader.compressedStargateTier4)
        .remapBlock("sciencenotleisure:CompressedStargateTier5", BlockLoader.compressedStargateTier5)
        .remapBlock("sciencenotleisure:CompressedStargateTier6", BlockLoader.compressedStargateTier6)
        .remapBlock("sciencenotleisure:CompressedStargateTier7", BlockLoader.compressedStargateTier7)
        .remapBlock("sciencenotleisure:CompressedStargateTier8", BlockLoader.compressedStargateTier8)
        .remapBlock("sciencenotleisure:CompressedStargateTier9", BlockLoader.compressedStargateTier9)
        .remapBlock("sciencenotleisure:shimmer", BlockLoader.shimmerFluidBlock)
        .remapBlock("sciencenotleisure:honey", BlockLoader.honeyFluidBlock)
        .remapBlock("sciencenotleisure:tile.shimmer", BlockLoader.shimmerFluidBlock)
        .remapBlock("sciencenotleisure:tile.honey", BlockLoader.honeyFluidBlock)
        .remapBlock("sciencenotleisure:tile.playerDoll", BlockLoader.playerDoll)
        .remapBlock("sciencenotleisure:tile.RealArtificialStarRenderer", BlockLoader.artificialStarRender)
        .remapBlock("sciencenotleisure:tile.EternalGregTechWorkshopRenderer", BlockLoader.eternalGregTechWorkshopRender)
        .remapBlock("sciencenotleisure:tile.NanoPhagocytosisPlantRenderer", BlockLoader.nanoPhagocytosisPlantRender)

         // Item remappings
        .remapItem("sciencenotleisure:SteamRocket", ItemLoader.steamRocket)
        .remapItem("sciencenotleisure:InfinityLavaBucket", ItemLoader.infinityLavaBucket)
        .remapItem("sciencenotleisure:InfinityWaterBucket", ItemLoader.infinityWaterBucket)
        .remapItem("sciencenotleisure:RejectionRing", ItemLoader.rejectionRing)
        .remapItem("sciencenotleisure:SlimeSaddle", ItemLoader.slimeSaddle)
        .remapItem("sciencenotleisure:NullPointerException", ItemLoader.nullPointerException)
        .remapItem("sciencenotleisure:CircuitIntegratedPlus", ItemLoader.circuitIntegratedPlus)
        .remapItem("sciencenotleisure:SuperstrongSponge", ItemLoader.superstrongSponge)
        .remapItem("sciencenotleisure:DebugItem", ItemLoader.debugItem)
        .remapItem("sciencenotleisure:InfinityShimmerBucket", ItemLoader.infinityShimmerBucket)
        .remapItem("sciencenotleisure:InfinityTorch", ItemLoader.infinityTorch)
        .remapItem("sciencenotleisure:SatietyRing", ItemLoader.satietyRing)
        .remapItem("sciencenotleisure:InfinityFuelRod", ItemLoader.infinityFuelRod)
        .remapItem("sciencenotleisure:KFCFamily", ItemLoader.KFCFamily)
        .remapItem("sciencenotleisure:FakeItemSiren", ItemLoader.fakeItemSiren)
        .remapItem("sciencenotleisure:InfinityCell", ItemLoader.infinityCell)
        .remapItem("sciencenotleisure:DireCraftPattern", ItemLoader.direCraftPattern)
        .remapItem("sciencenotleisure:LuckyHorseshoe", ItemLoader.luckyHorseshoe)
        .remapItem("sciencenotleisure:PhysicsCape", ItemLoader.physicsCape)
        .remapItem("sciencenotleisure:NetherTeleporter", ItemLoader.netherTeleporter)
        .remapItem("sciencenotleisure:InfinityFuelRodDepleted", ItemLoader.infinityFuelRodDepleted)
        .remapItem("sciencenotleisure:RoyalGel", ItemLoader.royalGel)
        .remapItem("sciencenotleisure:SuperReachRing", ItemLoader.superReachRing)
        .remapItem("sciencenotleisure:InfinityHoneyBucket", ItemLoader.infinityHoneyBucket)
        .remapItem("sciencenotleisure:VeinMiningPickaxe", ItemLoader.veinMiningPickaxe)
        .remapItem("sciencenotleisure:Stick", ItemLoader.stick)
        .remapItem("sciencenotleisure:SuspiciousStew", ItemLoader.suspiciousStew)
        .remapItem("sciencenotleisure:TestItem", ItemLoader.testItem)
        .remapItem("sciencenotleisure:TimeStopPocketWatch", ItemLoader.timeStopPocketWatch)
        .remapItem("sciencenotleisure:TwilightSword", ItemLoader.twilightSword)
        .remapItem("sciencenotleisure:WirelessUpgradeChip", ItemLoader.wirelessUpgradeChip)
        .remapItem("sciencenotleisure:MetaItem", ItemLoader.metaItem)
        .remapItem("sciencenotleisure:HoneyBucket", ItemLoader.honeyBucket)
        .remapItem("sciencenotleisure:ShimmerBucket", ItemLoader.shimmerBucket)

        ;

    // spotless:on

    public static void handleMappings(List<FMLMissingMappingsEvent.MissingMapping> mappings) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : mappings) {
            if (REMAPPER.ignoreMappings.contains(mapping.name)) {
                mapping.ignore();
                continue;
            }

            if (mapping.type == GameRegistry.Type.BLOCK) {
                Block block = REMAPPER.blockRemappings.get(mapping.name);
                if (block == null) {
                    block = GameRegistry.findBlock("sciencenotleisure", normalizeLegacyId(mapping.name));
                }
                if (block != null) {
                    mapping.remap(block);
                }
            } else if (mapping.type == GameRegistry.Type.ITEM) {
                Item item = REMAPPER.itemRemappings.get(mapping.name);
                if (item == null) {
                    item = GameRegistry.findItem("sciencenotleisure", normalizeLegacyId(mapping.name));
                }
                if (item != null) {
                    mapping.remap(item);
                }
            }
        }
    }

    private static String normalizeLegacyId(String legacyId) {
        String prefix = "sciencenotleisure:";
        if (!legacyId.startsWith(prefix)) return legacyId;

        String id = legacyId.substring(prefix.length());
        if (id.startsWith("tile.")) {
            id = id.substring("tile.".length());
        } else if (id.startsWith("item.")) {
            id = id.substring("item.".length());
        }
        return id.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
            .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
            .replace('.', '_')
            .toLowerCase(Locale.ROOT);
    }

    public static class Remapper {

        private final Map<String, Item> itemRemappings = new HashMap<>();
        private final Map<String, Block> blockRemappings = new HashMap<>();
        private final List<String> ignoreMappings = new ArrayList<>();

        public Remapper remapBlock(String oldName, Block newBlock) {
            blockRemappings.put(oldName, newBlock);
            return remapItem(oldName, Item.getItemFromBlock(newBlock));
        }

        public Remapper remapItem(String oldName, Item newItem) {
            itemRemappings.put(oldName, newItem);
            return this;
        }

        public Remapper ignore(String oldName) {
            ignoreMappings.add(oldName);
            return this;
        }
    }
}
