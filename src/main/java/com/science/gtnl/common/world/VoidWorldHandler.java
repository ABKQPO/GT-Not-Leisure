package com.science.gtnl.common.world;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VoidWorldHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;

        if (!world.isRemote && world.provider.dimensionId == 0) {
            WorldInfo worldInfo = world.getWorldInfo();

            if (worldInfo.getTerrainType()
                .getWorldTypeName()
                .equalsIgnoreCase("flat")) {
                String generatorOptions = worldInfo.getGeneratorOptions();

                if (generatorOptions != null && generatorOptions.contains("2;0;1;decoration")) {
                    VoidWorldSavedData data = VoidWorldSavedData.get(world);
                    if (!data.isPlatformGenerated()) {
                        generateVoidPlatform(world);
                        data.setPlatformGenerated(true);
                        data.markDirty();
                    }
                }
            }
        }
    }

    private void generateVoidPlatform(World world) {
        final int centerX = 0;
        final int centerY = 64;
        final int centerZ = 0;
        final int radius = 16;

        if (world.getBlock(centerX, centerY, centerZ) != Blocks.cobblestone) {

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && z == 0) {
                        world.setBlock(centerX + x, centerY, centerZ + z, Blocks.cobblestone);
                    } else {
                        world.setBlock(centerX + x, centerY, centerZ + z, Blocks.stone);
                    }
                }
            }

            world.setSpawnLocation(centerX, centerY + 1, centerZ);
        }
    }
}
