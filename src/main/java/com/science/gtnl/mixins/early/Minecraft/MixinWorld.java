package com.science.gtnl.mixins.early.Minecraft;

import java.util.Iterator;
import java.util.List;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeModContainer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.TimeStopManager;

import cpw.mods.fml.common.FMLLog;

@Mixin(value = World.class)
public abstract class MixinWorld {

    @Final
    @Shadow
    public Profiler theProfiler;
    @Shadow
    public List<Entity> weatherEffects;
    @Shadow
    public List<Entity> loadedEntityList;
    @Shadow
    public List<Entity> unloadedEntityList;
    @Shadow
    private boolean field_147481_N;
    @Shadow
    public List<TileEntity> loadedTileEntityList;
    @Shadow
    private List field_147483_b;
    @Shadow
    private List addedTileEntityList;
    @Shadow
    public IChunkProvider chunkProvider;

    @Inject(method = "updateEntities", at = @At("HEAD"), cancellable = true, remap = true)
    public void mixin$updateEntities(CallbackInfo ci) {
        boolean isStop = TimeStopManager.isTimeStopped();
        if (!isStop) return;

        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");
        int i;
        Entity entity;
        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        // 时停后不执行部分
        if (!isStop) {
            for (i = 0; i < this.weatherEffects.size(); ++i) {
                entity = this.weatherEffects.get(i);

                try {
                    ++entity.ticksExisted;
                    entity.onUpdate();
                } catch (Throwable throwable2) {
                    crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
                    crashreportcategory = crashreport.makeCategory("Entity being ticked");

                    if (entity == null) {
                        crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                    } else {
                        entity.addEntityCrashInfo(crashreportcategory);
                    }

                    if (ForgeModContainer.removeErroringEntities) {
                        FMLLog.getLogger()
                            .log(org.apache.logging.log4j.Level.ERROR, crashreport.getCompleteReport());
                        ((World) ((Object) this)).removeEntity(entity);
                    } else {
                        throw new ReportedException(crashreport);
                    }
                }

                if (entity.isDead) {
                    this.weatherEffects.remove(i--);
                }
            }
        }

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int j;
        int l;

        for (i = 0; i < this.unloadedEntityList.size(); ++i) {
            entity = this.unloadedEntityList.get(i);
            j = entity.chunkCoordX;
            l = entity.chunkCoordZ;

            if (entity.addedToChunk && (this.chunkProvider.chunkExists(j, l))) {
                ((World) ((Object) this)).getChunkFromChunkCoords(j, l)
                    .removeEntity(entity);
            }
        }

        for (i = 0; i < this.unloadedEntityList.size(); ++i) {
            ((World) ((Object) this)).onEntityRemoved(this.unloadedEntityList.get(i));
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");

        for (i = 0; i < this.loadedEntityList.size(); ++i) {
            entity = this.loadedEntityList.get(i);
            // 跳过不是玩家的更新
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.ridingEntity != null) {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity) {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            this.theProfiler.startSection("tick");

            if (!entity.isDead) {
                try {
                    ((World) ((Object) this)).updateEntity(entity);
                } catch (Throwable throwable1) {
                    crashreport = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                    crashreportcategory = crashreport.makeCategory("Entity being ticked");
                    entity.addEntityCrashInfo(crashreportcategory);

                    if (ForgeModContainer.removeErroringEntities) {
                        FMLLog.getLogger()
                            .log(org.apache.logging.log4j.Level.ERROR, crashreport.getCompleteReport());
                        ((World) ((Object) this)).removeEntity(entity);
                    } else {
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("remove");

            if (entity.isDead) {
                j = entity.chunkCoordX;
                l = entity.chunkCoordZ;

                if (entity.addedToChunk && (this.chunkProvider.chunkExists(j, l))) {
                    ((World) ((Object) this)).getChunkFromChunkCoords(j, l)
                        .removeEntity(entity);
                }

                this.loadedEntityList.remove(i--);
                ((World) ((Object) this)).onEntityRemoved(entity);
            }

            this.theProfiler.endSection();
        }

        if (!isStop) {
            this.theProfiler.endStartSection("blockEntities");
            this.field_147481_N = true;
            Iterator iterator = this.loadedTileEntityList.iterator();

            while (iterator.hasNext()) {
                TileEntity tileentity = (TileEntity) iterator.next();

                if (!tileentity.isInvalid() && tileentity.hasWorldObj()
                    && ((World) ((Object) this)).blockExists(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord)) {
                    try {
                        tileentity.updateEntity();
                    } catch (Throwable throwable) {
                        crashreport = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                        crashreportcategory = crashreport.makeCategory("Block entity being ticked");
                        tileentity.func_145828_a(crashreportcategory);
                        if (ForgeModContainer.removeErroringTileEntities) {
                            FMLLog.getLogger()
                                .log(org.apache.logging.log4j.Level.ERROR, crashreport.getCompleteReport());
                            tileentity.invalidate();
                            ((World) ((Object) this))
                                .setBlockToAir(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
                        } else {
                            throw new ReportedException(crashreport);
                        }
                    }
                }

                if (tileentity.isInvalid()) {
                    iterator.remove();

                    if (this.chunkProvider.chunkExists(tileentity.xCoord >> 4, tileentity.zCoord >> 4)) {
                        Chunk chunk = ((World) ((Object) this))
                            .getChunkFromChunkCoords(tileentity.xCoord >> 4, tileentity.zCoord >> 4);

                        if (chunk != null) {
                            chunk.removeInvalidTileEntity(
                                tileentity.xCoord & 15,
                                tileentity.yCoord,
                                tileentity.zCoord & 15);
                        }
                    }
                }
            }

            if (!this.field_147483_b.isEmpty()) {
                for (Object tile : field_147483_b) {
                    ((TileEntity) tile).onChunkUnload();
                }
                this.loadedTileEntityList.removeAll(this.field_147483_b);
                this.field_147483_b.clear();
            }

            this.field_147481_N = false;

            this.theProfiler.endStartSection("pendingBlockEntities");

            if (!this.addedTileEntityList.isEmpty()) {
                for (int k = 0; k < this.addedTileEntityList.size(); ++k) {
                    TileEntity tileentity1 = (TileEntity) this.addedTileEntityList.get(k);

                    if (!tileentity1.isInvalid()) {
                        if (!this.loadedTileEntityList.contains(tileentity1)) {
                            this.loadedTileEntityList.add(tileentity1);
                        }
                    } else {
                        if (this.chunkProvider.chunkExists(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4)) {
                            Chunk chunk1 = ((World) ((Object) this))
                                .getChunkFromChunkCoords(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4);

                            if (chunk1 != null) {
                                chunk1.removeInvalidTileEntity(
                                    tileentity1.xCoord & 15,
                                    tileentity1.yCoord,
                                    tileentity1.zCoord & 15);
                            }
                        }
                    }
                }

                this.addedTileEntityList.clear();
            }
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();

        if (isStop) ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void mixin$tick(CallbackInfo ci) {
        boolean isStop = TimeStopManager.isTimeStopped();
        if (isStop) {
            ci.cancel();
        }
    }
}
