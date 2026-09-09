package com.science.gtnl.utils.world;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.gtnewhorizons.postea.api.BlockAccessCompat;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.science.gtnl.loader.BlockLoader;

public class TileEntityMigrationHandler {

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;

        register(
            "ArtificialStarRenderTileEntity",
            "artificial_star_render_tile_entity",
            BlockLoader.artificialStarRender);
        register("BeamFormerTileEntity", "beam_former_tile_entity", BlockLoader.beamFormer);
        register("CardboardBoxTileEntity", "cardboard_box_tile_entity", BlockLoader.cardboardBox);
        register(
            "DimensionRespawnAnchorTileEntity",
            "dimension_respawn_anchor_tile_entity",
            BlockLoader.dimensionRespawnAnchor);
        register("DirePatternEncoderTileEntity", "dire_pattern_encoder_tile_entity", BlockLoader.direPatternEncoder);
        register("EnderElevatorTileEntity", "ender_elevator_tile_entity", BlockLoader.enderElevatorBlock);
        register("EssentiaHatchTileEntity", "essentia_hatch_tile_entity", BlockLoader.essentiaHatch);
        register(
            "EternalGregTechWorkshopRenderTileEntity",
            "eternal_greg_tech_workshop_render_tile_entity",
            BlockLoader.eternalGregTechWorkshopRender);
        register("LaserBeaconTileEntity", "laser_beacon_tile_entity", BlockLoader.laserBeacon);
        register("MEChiselTileEntity", "me_chisel_tile_entity", BlockLoader.meChisel);
        register(
            "NanoPhagocytosisPlantRenderTileEntity",
            "nano_phagocytosis_plant_render_tile_entity",
            BlockLoader.nanoPhagocytosisPlantRender);
        register("PlayerDollTileEntity", "player_doll_tile_entity", BlockLoader.playerDoll);
        register(
            "SuperDenseEnergyCellTileEntity",
            "super_dense_energy_cell_tile_entity",
            BlockLoader.superDenseEnergyCell);
        register("SuperDualInterfaceTileEntity", "super_dual_interface_tile_entity", BlockLoader.superDualInterface);
        register("SuperInterfaceTileEntity", "super_interface_tile_entity", BlockLoader.superInterface);
        register("WaterCandleTileEntity", "water_candle_tile_entity", BlockLoader.waterCandle);
    }

    private static void register(String oldId, String newId, Block block) {
        TileEntityReplacementManager
            .tileEntityTransformer(oldId, (tag, world, chunk) -> createBlockInfo(tag, world, chunk, newId, block));
    }

    private static BlockInfo createBlockInfo(NBTTagCompound tag, World world, Chunk chunk, String tileEntityId,
        Block block) {
        return new BlockInfo(block, BlockAccessCompat.getBlockMetaAtTE(tag, chunk), originalTag -> {
            NBTTagCompound migratedTag = (NBTTagCompound) originalTag.copy();
            migratedTag.setString("id", tileEntityId);
            return migratedTag;
        });
    }
}
