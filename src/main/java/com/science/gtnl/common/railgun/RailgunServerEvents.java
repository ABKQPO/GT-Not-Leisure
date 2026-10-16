package com.science.gtnl.common.railgun;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class RailgunServerEvents {

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        RailgunServerEvents handler = new RailgunServerEvents();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }

    public static EntityPlayerMP player(UUID id) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getConfigurationManager() == null) return null;
        for (Object candidate : server.getConfigurationManager().playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP) candidate;
            if (id.equals(player.getUniqueID())) return player;
        }
        return null;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        RailgunFireService.CHARGING.entrySet()
            .removeIf(entry -> {
                EntityPlayerMP player = player(entry.getKey());
                var session = entry.getValue();
                return player == null || player.isDead
                    || !player.isUsingItem()
                    || player.dimension != session.dimension()
                    || player.inventory.currentItem != session.slot()
                    || player.getHeldItem() != session.stack();
            });
    }

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        RailgunFireService.CHARGING.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        RailgunFireService.CHARGING.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        if (!event.world.isRemote) RailgunFireService.CHARGING.entrySet()
            .removeIf(
                entry -> entry.getValue()
                    .dimension() == event.world.provider.dimensionId);
    }

    @SubscribeEvent
    public void fall(LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP player && player.getEntityData()
            .hasKey(RailgunRecoilService.GRACE)
            && player.getEntityData()
                .getLong(RailgunRecoilService.GRACE) >= player.worldObj.getTotalWorldTime())
            event.setCanceled(true);
    }
}
