package com.science.gtnl.common.world;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.player.PlayerEvents;
import com.science.gtnl.common.render.PlayerDollRenderManager;
import com.science.gtnl.loader.RecipeLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import tectech.thing.casing.TTCasingsContainer;

@EventBusSubscriber
public class WorldEvents {

    private WorldEvents() {}

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.world.isRemote) return;
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;

        Block block = event.block;
        int meta = world.getBlockMetadata(x, y, z);

        if (block == TTCasingsContainer.GodforgeCasings && meta == 8) {

            if (world.rand.nextInt(1000) == 0) {
                world.playSoundEffect(x, y, z, ScienceNotLeisure.RESOURCE_ROOT_ID + ":tidal.wave", 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world.isRemote) return;
        RecipeLoader.loadCircuitNanitesData(world.getSeed());
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            PlayerDollRenderManager.offlineMode = false;
            PlayerDollRenderManager.BLACKLISTED_UUIDS.clear();
            PlayerDollRenderManager.BLACKLISTED_NAMES.clear();
            PlayerDollRenderManager.BLACKLISTED_SKIN_URLS.clear();
            PlayerDollRenderManager.BLACKLISTED_CAPE_URLS.clear();
            PlayerDollRenderManager.UUID_CACHE.clear();
            return;
        }
        // Other dimensions unload during play; only the overworld unloading ends the server session.
        if (event.world.provider.dimensionId != 0) return;
        PlayerEvents.FOOD_TICK_TIMERS.clear();
        RecipeLoader.clearCircuitNanitesData();
    }

    @SubscribeEvent
    public static void onExplosionEvent(ExplosionEvent event) {
        if (tryAbsorbExplosion(
            event.world,
            event.explosion.explosionX,
            event.explosion.explosionY,
            event.explosion.explosionZ,
            event.explosion.explosionSize)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onIC2ExplosionEvent(ic2.api.event.ExplosionEvent event) {
        if (tryAbsorbExplosion(event.world, event.x, event.y, event.z, (float) event.power)) {
            event.setCanceled(true);
        }
    }

    public static boolean tryAbsorbExplosion(World world, double explosionX, double explosionY, double explosionZ,
        float power) {
        int ex = (int) explosionX;
        int ey = (int) explosionY;
        int ez = (int) explosionZ;

        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {

                    if (!world.blockExists(x, y, z)) continue;

                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (!(tile instanceof BaseMetaTileEntity te)) continue;

                    IMetaTileEntity mte = te.getMetaTileEntity();
                    if (!(mte instanceof ExplosionDynamoHatch machine)) continue;

                    double dx = te.getXCoord() + 0.5 - explosionX;
                    double dy = te.getYCoord() + 0.5 - explosionY;
                    double dz = te.getZCoord() + 0.5 - explosionZ;
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (distance > 10.0) continue;

                    double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
                    long addedEU = (long) (power * 2048 * efficiency);

                    long stored = machine.getEUVar();
                    long maxEU = machine.maxEUStore();

                    if (stored + addedEU <= maxEU) {
                        machine.setEUVar(stored + addedEU);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
