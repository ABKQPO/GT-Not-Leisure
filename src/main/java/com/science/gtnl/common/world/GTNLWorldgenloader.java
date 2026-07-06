package com.science.gtnl.common.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.mixins.late.visualProspecting.AccessorVeinTypeCaching;
import com.science.gtnl.utils.enums.GTNLOreMixer;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.enums.Mods;
import gtneioreplugin.util.GT5OreLayerHelper;

public class GTNLWorldgenloader {

    public static void registry() {

        // GT Veins registration
        for (GTNLOreMixer oreMix : GTNLOreMixer.values()) {
            oreMix.addGTOreLayer();

            GT5OreLayerHelper.getOreVeinsByName()
                .put(oreMix.oreMixBuilder.oreMixName, new GT5OreLayerHelper.OreLayerWrapper(oreMix.oreMixBuilder));
            if (Mods.VisualProspecting.isModLoaded()) {
                AccessorVeinTypeCaching.getVeinTypes()
                    .put(oreMix.oreMixBuilder.oreMixName, new VeinType(oreMix.oreMixBuilder));
            }
        }

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
        yPos = random.nextInt(70) + 8;
        zPos = zChunk + random.nextInt(16) + 8;

        if (random.nextInt(512) == 0)
            new WorldGenLakes(BlockLoader.shimmerFluidBlock).generate(world, random, xPos, yPos, zPos);
    }
}
