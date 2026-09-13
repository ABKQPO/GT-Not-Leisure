package com.reavaritia.common.entity;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.mixins.early.minecraft.AccessorTileEntity;
import com.science.gtnl.utils.enums.ModList;

public class EntityChronarchClock extends Entity {

    public int radius;
    public int speedMultiplier;
    public int maxTicks;
    public int age = 0;

    public EntityChronarchClock(World world) {
        super(world);
        this.radius = 0;
        this.speedMultiplier = 0;
        this.maxTicks = 0;
        initInvisible();
    }

    public EntityChronarchClock(World world, double x, double y, double z, int radius, int speedMultiplier,
        int durationTicks) {
        super(world);
        this.setPosition(x, y, z);
        this.noClip = true;
        this.radius = radius;
        this.speedMultiplier = speedMultiplier;
        this.maxTicks = durationTicks;
        initInvisible();
    }

    private void initInvisible() {
        this.setSize(1, 1);
        this.ignoreFrustumCheck = true;
    }

    @Override
    public void entityInit() {}

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {}

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) return;

        final int cx = MathHelper.floor_double(posX);
        final int cy = MathHelper.floor_double(posY);
        final int cz = MathHelper.floor_double(posZ);

        final long tMaxTime = System.nanoTime() + 1000000;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    final int x = cx + dx;
                    final int y = cy + dy;
                    final int z = cz + dz;

                    TileEntity te = worldObj.getTileEntity(x, y, z);
                    if (shouldAccelerate(te)) {

                        if (!ModList.NHUtilities.isModLoaded()) {
                            if (te instanceof ITileEntityTickAcceleration accelerated) {
                                if (accelerated.tickAcceleration(speedMultiplier)) continue;
                            }
                        } else {
                            if (te instanceof com.xir.NHUtilities.common.api.interfaces.ITileEntityTickAcceleration accelerated) {
                                if (accelerated.tickAcceleration(speedMultiplier)) continue;
                            }
                        }
                        safeAccelerateTileEntity(te, tMaxTime);
                    }

                    Block block = worldObj.getBlock(x, y, z);
                    if (shouldAccelerate(block)) {
                        safeAccelerateBlock(block, x, y, z, tMaxTime);
                    }
                }
            }
        }

        if (++age >= maxTicks) {
            setDead();
        }
    }

    private boolean shouldAccelerate(TileEntity te) {
        return te != null && !te.isInvalid() && te.canUpdate() && !isBlacklisted(te);
    }

    private boolean isBlacklisted(TileEntity te) {
        String[] blacklist = MainConfig.re_avaritia.chronarch_clock.chronarchsClockTileEntityBlacklist;
        if (blacklist.length == 0) return false;

        String registryName = getRegistryName(te);
        if (registryName == null) return false;

        for (String entry : blacklist) {
            if (entry != null && registryName.equals(entry.trim())) return true;
        }

        return false;
    }

    private boolean shouldAccelerate(Block block) {
        return block != null && block.getTickRandomly() && worldObj.getTotalWorldTime() % 2 == 0;
    }

    private void safeAccelerateTileEntity(TileEntity te, long tMaxTime) {
        try {
            for (int i = 0; i < speedMultiplier; i++) {
                te.updateEntity();
                if (System.nanoTime() > tMaxTime) break;
            }
        } catch (Throwable t) {
            // log warn if needed
        }
    }

    private void safeAccelerateBlock(Block block, int x, int y, int z, long tMaxTime) {
        try {
            for (int i = 0; i < speedMultiplier; i++) {
                block.updateTick(worldObj, x, y, z, worldObj.rand);
                if (System.nanoTime() > tMaxTime) break;
            }
        } catch (Throwable t) {
            // log warn if needed
        }
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    public static String getRegistryName(TileEntity tileEntity) {
        if (tileEntity == null) return null;

        Map<Class<?>, String> classToNameMap = ((AccessorTileEntity) tileEntity).getClassToNameMap();
        if (classToNameMap == null) return null;

        return classToNameMap.get(tileEntity.getClass());
    }
}
