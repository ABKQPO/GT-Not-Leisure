package com.science.gtnl.common.armorflight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.armorflight.ArmorFlightAnchor;
import com.science.gtnl.api.armorflight.ArmorFlightApproach;
import com.science.gtnl.api.armorflight.ArmorFlightPiece;
import com.science.gtnl.api.armorflight.IArmorFlightHome;
import com.science.gtnl.api.armorflight.IArmorFlightService;
import com.science.gtnl.common.entity.EntityArmorFlightPiece;

public class ArmorFlightService implements IArmorFlightService {

    public static final String SOUND_PREFIX = ScienceNotLeisure.MODID + ":armory.";

    @Override
    public int summon(EntityPlayerMP player, Map<ArmorFlightPiece, ItemStack> suit, ArmorFlightAnchor anchor,
        ArmorFlightApproach approach) {
        List<ArmorFlightPiece> flying = new ArrayList<>();
        for (ArmorFlightPiece p : ArmorFlightPiece.SEQUENCE) {
            ItemStack stack = suit.get(p);
            if (stack == null) continue;
            if (player.isEntityAlive() && p.accepts(stack, player)) {
                flying.add(p);
            } else {
                handBack(anchor, player, p, stack);
            }
        }
        if (flying.isEmpty()) return -1;

        WorldServer world = (WorldServer) player.worldObj;
        boolean sameDimension = anchor.isIn(world);
        FlightVec anchorTop = new FlightVec(anchor.topX(), anchor.topY(), anchor.topZ());
        FlightVec feet = ArmorFlight.feet(player, 1.0F);
        float yaw = player.renderYawOffset;
        int latestArrival = 0;
        int order = 0;
        for (ArmorFlightPiece p : flying) {
            FlightVec dock = ArmorFlight.dockPoint(feet, player.height, p);
            boolean behind = approach.pickBehind(world.rand);
            FlightVec dir = ArmorFlight.forward(yaw + ArmorFlight.sideAngle(p, behind));
            FlightVec hold = ArmorFlight.holdPoint(dock, dir);
            FlightVec start = ArmorFlight.launchPoint(dock, anchorTop, sameDimension, p);
            double length = ArmorFlight.approachLength(start, hold, dir, p);
            int duration = ArmorFlight.ticksFor(length);
            int delay = ArmorFlight.SEQUENCE_LEAD + order++ * ArmorFlight.STAGGER;
            world.spawnEntityInWorld(
                EntityArmorFlightPiece.deliver(player, suit.get(p), p, start, anchor, behind, delay, duration));
            latestArrival = Math.max(latestArrival, delay + ArmorFlight.arrivalTicks(duration, behind));
        }
        purge(player, flying, anchor);

        if (sameDimension) {
            world.playSoundEffect(anchor.topX(), anchor.topY(), anchor.topZ(), SOUND_PREFIX + "alarm", 1.0F, 1.0F);
            world.playSoundEffect(anchor.topX(), anchor.topY(), anchor.topZ(), SOUND_PREFIX + "hatch", 1.0F, 1.0F);
        }
        world.playSoundEffect(player.posX, player.posY, player.posZ, "random.orb", 0.6F, 1.8F);
        return latestArrival;
    }

    @Override
    public int purge(EntityPlayerMP player, Collection<ArmorFlightPiece> pieces, ArmorFlightAnchor anchor) {
        WorldServer world = (WorldServer) player.worldObj;
        int purged = 0;
        for (ArmorFlightPiece p : pieces) {
            ItemStack old = p.worn(player);
            if (old == null) continue;
            p.setWorn(player, null);
            if (sendHome(anchor, player, p, old)) purged++;
        }
        if (purged > 0) {
            double cx = player.posX, cy = player.boundingBox.minY + player.height * 0.55, cz = player.posZ;
            world.playSoundEffect(cx, cy, cz, SOUND_PREFIX + "purge", 0.9F, 0.8F);
            world.func_147487_a("explode", cx, cy, cz, 18, 0.35, 0.5, 0.35, 0.12);
            world.func_147487_a("magicCrit", cx, cy, cz, 14, 0.3, 0.5, 0.3, 0.25);
        }
        return purged;
    }

    @Override
    public boolean isDelivering(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        for (Object o : player.worldObj.loadedEntityList) {
            if (o instanceof EntityArmorFlightPiece piece && !piece.isDead
                && !piece.isReturn()
                && id.equals(piece.owner())) return true;
        }
        return false;
    }

    private static void handBack(ArmorFlightAnchor anchor, EntityPlayerMP player, ArmorFlightPiece piece,
        ItemStack stack) {
        ItemStack rest = storeHome(anchor, player, piece, stack);
        if (rest != null) giveToPlayer(player, rest);
    }

    public static EntityPlayerMP findPlayer(UUID id) {
        MinecraftServer server = MinecraftServer.getServer();
        if (id == null || server == null) return null;
        for (Object o : server.getConfigurationManager().playerEntityList) {
            if (o instanceof EntityPlayerMP player && id.equals(player.getUniqueID())) return player;
        }
        return null;
    }

    public static ItemStack storeHome(ArmorFlightAnchor anchor, EntityPlayer owner, ArmorFlightPiece piece,
        ItemStack stack) {
        if (anchor == null || stack == null) return stack;
        IArmorFlightHome home = anchor.home();
        if (home == null) return stack;
        ItemStack rest = home.storeArmorFlightPiece(owner, piece, stack);
        return rest == null || rest.stackSize <= 0 ? null : rest;
    }

    public static boolean sendHome(ArmorFlightAnchor anchor, EntityPlayerMP player, ArmorFlightPiece piece,
        ItemStack old) {
        boolean hasHome = anchor != null && anchor.home() != null;
        ItemStack rest = storeHome(anchor, player, piece, old.copy());
        if (rest != null) {
            giveToPlayer(player, rest);
            if (hasHome) return false;
        }
        FlightVec at = ArmorFlight.dockPoint(player, 1.0F, piece);
        player.worldObj
            .spawnEntityInWorld(EntityArmorFlightPiece.echo(player, old, piece, at, homeTarget(anchor, player, at)));
        return true;
    }

    public static void giveToPlayer(EntityPlayer player, ItemStack stack) {
        if (stack == null) return;
        if (!player.inventory.addItemStackToInventory(stack) && stack.stackSize > 0) {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
    }

    public static FlightVec homeTarget(ArmorFlightAnchor anchor, EntityPlayer player, FlightVec from) {
        World world = player.worldObj;
        if (anchor == null) return from.add(0, 24, 0);
        FlightVec top = new FlightVec(anchor.topX(), anchor.topY(), anchor.topZ());
        return ArmorFlight.homeTarget(top, anchor.isIn(world), from);
    }
}
