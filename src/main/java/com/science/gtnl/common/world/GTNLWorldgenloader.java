package com.science.gtnl.common.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.compat.visualprospecting.OreVeinTypes;
import com.science.gtnl.loader.BlockLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GTNLWorldgenloader {

    public static void registry() {
        for (GTNLOreMixer oreMix : GTNLOreMixer.values()) {
            oreMix.addGTOreLayer();
        }

        OreVeinTypes.register();

        ScienceNotLeisure.LOG.info("Started Galactic Greg ore gen code");
    }

    @SubscribeEvent
    public void onDecorateBiome(DecorateBiomeEvent.Decorate event) {
        if (event.type != DecorateBiomeEvent.Decorate.EventType.LAKE) return;
        generateFluidLake(event.world, event.rand, event.chunkX, event.chunkZ);
    }

    public void generateFluidLake(World world, Random random, int xChunk, int zChunk) {
        if (random == null) return;
        int xPos, yPos, zPos;

        xPos = xChunk + random.nextInt(16) + 8;
        yPos = random.nextInt(60) + 60;
        zPos = zChunk + random.nextInt(16) + 8;

        if (random.nextInt(128) == 0
            && BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(xPos, zPos), BiomeDictionary.Type.JUNGLE))
            new WorldGenLakes(BlockLoader.honeyFluidBlock).generate(world, random, xPos, yPos, zPos);

        xPos = xChunk + random.nextInt(16) + 8;
        yPos = random.nextInt(62) + 8;
        zPos = zChunk + random.nextInt(16) + 8;

        if (random.nextInt(512) == 0)
            new WorldGenLakes(BlockLoader.shimmerFluidBlock).generate(world, random, xPos, yPos, zPos);
    }
}
