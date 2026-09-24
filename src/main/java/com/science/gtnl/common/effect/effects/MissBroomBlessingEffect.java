package com.science.gtnl.common.effect.effects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.reavaritia.common.entity.EntityChronarchClock;
import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.common.effect.EffectBase;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MissBroomBlessingEffect extends EffectBase {

    private static final int RADIUS = 5;
    private static final int CLEANSE_INTERVAL_TICKS = 5 * 20;
    private static final long MAX_ACCELERATION_NANOS = 1_000_000L;
    private final Map<World, AccelerationFrame> accelerationFrames = new WeakHashMap<>();

    public MissBroomBlessingEffect(int id) {
        super(id, "miss_broom_blessing", false, 0xB978F2, 6);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (!(entity instanceof EntityPlayer player) || entity.worldObj.isRemote) return;
        World world = entity.worldObj;
        long tick = world.getTotalWorldTime();
        if (tick % CLEANSE_INTERVAL_TICKS == 0L) cleanse(player);
        if ((tick & 1L) != 0L) return;

        AccelerationFrame frame = accelerationFrames.computeIfAbsent(world, ignored -> new AccelerationFrame());
        if (frame.tick != tick) {
            frame.tick = tick;
            frame.positions.clear();
        }

        int cx = MathHelper.floor_double(player.posX);
        int cy = MathHelper.floor_double(player.posY);
        int cz = MathHelper.floor_double(player.posZ);
        long deadline = System.nanoTime() + MAX_ACCELERATION_NANOS;
        boolean nhUtilitiesLoaded = ModList.NHUtilities.isModLoaded();

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (System.nanoTime() > deadline) return;
                    int x = cx + dx;
                    int y = cy + dy;
                    int z = cz + dz;
                    if (y < 0 || y >= world.getActualHeight() || !world.blockExists(x, y, z)) continue;
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile == null || tile.isInvalid() || !tile.canUpdate() || isBlacklisted(tile)) continue;
                    if (!frame.positions.add(new ChunkPosition(x, y, z))) continue;

                    try {
                        if (nhUtilitiesLoaded) {
                            if (tile instanceof com.xir.NHUtilities.common.api.interfaces.ITileEntityTickAcceleration accelerated
                                && accelerated.tickAcceleration(1)) continue;
                        } else if (tile instanceof ITileEntityTickAcceleration accelerated
                            && accelerated.tickAcceleration(1)) continue;
                        tile.updateEntity();
                    } catch (Throwable ignored) {}
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.entity instanceof EntityPlayer player && !player.worldObj.isRemote
            && player.getActivePotionEffect(this) != null) event.setCanceled(true);
    }

    private static void cleanse(EntityPlayer player) {
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            int potionId = effect.getPotionID();
            if (potionId < 0 || potionId >= Potion.potionTypes.length) continue;
            Potion potion = Potion.potionTypes[potionId];
            if (potion != null && potion.isBadEffect()) player.removePotionEffect(potionId);
        }
    }

    private static boolean isBlacklisted(TileEntity tile) {
        String[] blacklist = MainConfig.re_avaritia.chronarch_clock.chronarchsClockTileEntityBlacklist;
        if (blacklist.length == 0) return false;
        String registryName = EntityChronarchClock.getRegistryName(tile);
        if (registryName == null) return false;
        for (String entry : blacklist) {
            if (entry != null && registryName.equals(entry.trim())) return true;
        }
        return false;
    }

    private static class AccelerationFrame {

        private long tick = Long.MIN_VALUE;
        private final Set<ChunkPosition> positions = new HashSet<>();
    }
}
