package com.science.gtnl.common.railgun;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;

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
        RailgunBeamService.tick();
        RailgunTerrainService.tick();
        RailgunFireService.CHARGING.entrySet()
            .removeIf(entry -> {
                EntityPlayerMP player = player(entry.getKey());
                if (player == null || player.isDead) return true;
                var session = entry.getValue();
                if (!session.matches(player)) return true;
                long now = player.worldObj.getTotalWorldTime();
                if (player.isUsingItem()) session.touch(now);
                return !session.live(now);
            });
    }

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP player) RailgunBeamService.stop(player);
        RailgunFireService.CHARGING.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP player) RailgunBeamService.stop(player);
        RailgunFireService.CHARGING.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        if (!event.world.isRemote) RailgunTerrainService.unload(event.world.provider.dimensionId);
        if (!event.world.isRemote) RailgunBeamService.unload(event.world.provider.dimensionId);
        if (!event.world.isRemote) RailgunFireService.CHARGING.entrySet()
            .removeIf(
                entry -> entry.getValue()
                    .dimension() == event.world.provider.dimensionId);
    }

    @SubscribeEvent
    public void fall(LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP player && player.getEntityData()
            .hasKey(RailgunFireService.RECOIL_GRACE)
            && player.getEntityData()
                .getLong(RailgunFireService.RECOIL_GRACE) >= player.worldObj.getTotalWorldTime())
            event.setCanceled(true);
    }

    private static boolean holding(EntityPlayer player) {
        return player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof ElectromagneticRailgun;
    }

    @SubscribeEvent
    public void attack(AttackEntityEvent event) {
        if (holding(event.entityPlayer)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void interact(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && holding(event.entityPlayer))
            event.setCanceled(true);
    }
}
