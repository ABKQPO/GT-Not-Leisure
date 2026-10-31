package com.science.gtnl.common.railgun;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@EventBusSubscriber(phase = Phase.PRE)
public class RailgunServerEvents {

    private static final Map<UUID, EntityPlayerMP> ONLINE = new Object2ObjectOpenHashMap<>();
    private static boolean ticking, indexed;

    public static EntityPlayerMP player(UUID id) {
        if (ticking) {
            if (!indexed) {
                for (EntityPlayerMP player : online()) ONLINE.put(player.getUniqueID(), player);
                indexed = true;
            }
            return ONLINE.get(id);
        }
        for (EntityPlayerMP player : online()) if (id.equals(player.getUniqueID())) return player;
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<EntityPlayerMP> online() {
        MinecraftServer server = MinecraftServer.getServer();
        return server == null || server.getConfigurationManager() == null ? Collections.emptyList()
            : server.getConfigurationManager().playerEntityList;
    }

    private static void forget(EntityPlayer player) {
        if (player instanceof EntityPlayerMP serverPlayer) RailgunBeamService.forget(serverPlayer);
        RailgunFireService.forget(player);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ticking = true;
        try {
            RailgunBeamService.tick();
            RailgunTerrainService.tick();
            RailgunFireService.tick();
        } finally {
            ticking = false;
            indexed = false;
            ONLINE.clear();
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        forget(event.player);
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        forget(event.player);
    }

    @SubscribeEvent
    public static void unload(WorldEvent.Unload event) {
        if (event.world.isRemote) return;
        int dimension = event.world.provider.dimensionId;
        RailgunTerrainService.unload(dimension);
        RailgunBeamService.unload(dimension);
        RailgunFireService.unload(dimension);
    }

    @SubscribeEvent
    public static void fall(LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP player && player.getEntityData()
            .hasKey(RailgunFireService.RECOIL_GRACE)
            && player.getEntityData()
                .getLong(RailgunFireService.RECOIL_GRACE) >= player.worldObj.getTotalWorldTime())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void attack(AttackEntityEvent event) {
        if (!RailgunDamage.striking && ElectromagneticRailgun.isHeld(event.entityPlayer)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK
            && ElectromagneticRailgun.isHeld(event.entityPlayer)) event.setCanceled(true);
    }
}
