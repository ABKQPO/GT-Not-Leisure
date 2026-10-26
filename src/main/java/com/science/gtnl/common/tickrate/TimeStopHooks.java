package com.science.gtnl.common.tickrate;

import java.util.List;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeModContainer;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;

// While the pocket watch stops time, worlds only tick players. Follows vanilla World.updateEntities.
public class TimeStopHooks {

    private TimeStopHooks() {}

    public static void updatePlayersOnly(World world, List<Entity> unloadedEntityList, IChunkProvider chunkProvider) {
        world.theProfiler.startSection("entities");
        world.theProfiler.startSection("global");
        CrashReport crashreport;
        CrashReportCategory crashreportcategory;
        int i;
        Entity entity;

        world.theProfiler.endStartSection("remove");
        world.loadedEntityList.removeAll(unloadedEntityList);
        int j;
        int l;

        for (i = 0; i < unloadedEntityList.size(); ++i) {
            entity = unloadedEntityList.get(i);
            j = entity.chunkCoordX;
            l = entity.chunkCoordZ;

            if (entity.addedToChunk && (chunkProvider.chunkExists(j, l))) {
                world.getChunkFromChunkCoords(j, l)
                    .removeEntity(entity);
            }
        }

        for (i = 0; i < unloadedEntityList.size(); ++i) {
            world.onEntityRemoved(unloadedEntityList.get(i));
        }

        unloadedEntityList.clear();
        world.theProfiler.endStartSection("regular");

        for (i = 0; i < world.loadedEntityList.size(); ++i) {
            entity = world.loadedEntityList.get(i);
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.ridingEntity != null) {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity) {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            world.theProfiler.startSection("tick");

            if (!entity.isDead) {
                try {
                    world.updateEntity(entity);
                } catch (Throwable throwable1) {
                    crashreport = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                    crashreportcategory = crashreport.makeCategory("Entity being ticked");
                    entity.addEntityCrashInfo(crashreportcategory);

                    if (ForgeModContainer.removeErroringEntities) {
                        FMLLog.getLogger()
                            .log(Level.ERROR, crashreport.getCompleteReport());
                        world.removeEntity(entity);
                    } else {
                        throw new ReportedException(crashreport);
                    }
                }
            }

            world.theProfiler.endSection();
            world.theProfiler.startSection("remove");

            if (entity.isDead) {
                j = entity.chunkCoordX;
                l = entity.chunkCoordZ;

                if (entity.addedToChunk && (chunkProvider.chunkExists(j, l))) {
                    world.getChunkFromChunkCoords(j, l)
                        .removeEntity(entity);
                }

                world.loadedEntityList.remove(i--);
                world.onEntityRemoved(entity);
            }

            world.theProfiler.endSection();
        }

        world.theProfiler.endSection();
        world.theProfiler.endSection();
    }
}
