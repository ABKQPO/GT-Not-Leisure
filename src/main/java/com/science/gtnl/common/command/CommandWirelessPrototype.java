package com.science.gtnl.common.command;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelPrototype;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Entrance;
import com.science.gtnl.common.wireless.WirelessClusterManager;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.tile.networking.TileController;

/** Operator-only debug controls. Explicit link can add entrances; unlink removes the associated cluster binding. */
public final class CommandWirelessPrototype extends CommandBase {

    private final Map<UUID, Address> sources = new HashMap<>();

    @Override
    public String getCommandName() {
        return "gtnlwireless";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gtnlwireless bind <dim> <x> <y> <z> | link <dim> <x> <y> <z> [side] | unlink <id> | status | clear";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP player)) throw new CommandException("Player command only.");
        if (args.length == 0) {
            message(sender, getCommandUsage(sender));
            return;
        }
        try {
            switch (args[0]) {
                case "bind" -> {
                    if (args.length != 5) throw new IllegalArgumentException(getCommandUsage(sender));
                    Address source = address(args);
                    if (source.tile() == null || source.tile()
                        .getClass() != TileController.class) {
                        throw new IllegalArgumentException("Source must be a loaded ordinary ME controller.");
                    }
                    sources.put(player.getUniqueID(), source);
                    message(sender, "Bound wireless source: " + source);
                }
                case "link" -> {
                    if (args.length != 5 && args.length != 6)
                        throw new IllegalArgumentException(getCommandUsage(sender));
                    Address source = sources.get(player.getUniqueID());
                    if (source == null) throw new IllegalArgumentException("Bind a source first.");
                    int id = WirelessClusterManager.connectCluster(source, address(args));
                    message(sender, "Created entrance " + id + ". Wait for AE2 pathing, then run status.");
                }
                case "unlink" -> {
                    if (args.length != 2) throw new IllegalArgumentException(getCommandUsage(sender));
                    int id = Integer.parseInt(args[1]);
                    boolean removed = false;
                    for (Entrance entrance : WirelessChannelPrototype.entrances()) {
                        if (entrance.id() == id) {
                            removed = WirelessClusterManager
                                .disconnectCluster(entrance.source(), entrance.targetNode());
                            break;
                        }
                    }
                    message(
                        sender,
                        removed ? "Frequency disconnected from the entire physical cluster." : "Unknown entrance ID.");
                }
                case "clear" -> {
                    if (args.length != 1) throw new IllegalArgumentException(getCommandUsage(sender));
                    com.science.gtnl.common.wireless.WirelessLinkPersistence.clearLoadedClaims();
                    WirelessChannelPrototype.clear();
                    sources.clear();
                    message(
                        sender,
                        "Loaded wireless links, entrances and command bindings cleared; unloaded chunks are unchanged.");
                }
                case "status" -> {
                    if (args.length != 1) throw new IllegalArgumentException(getCommandUsage(sender));
                    WirelessClusterManager.refresh();
                    message(
                        sender,
                        "Channels enabled: " + WirelessChannelPrototype.channelsEnabled()
                            + "; runtime-only entrances (lost on unload/restart):");
                    for (Entrance entrance : WirelessChannelPrototype.entrances()) {
                        message(
                            sender,
                            "#" + entrance.id()
                                + " live="
                                + entrance.isLive()
                                + " edgeChannels="
                                + entrance.connection()
                                    .getUsedChannels()
                                + " target="
                                + entrance.target());
                    }
                    Address source = sources.get(player.getUniqueID());
                    if (source != null) message(sender, "Cluster records: " + WirelessClusterManager.summary(source));
                    IGridNode node = source == null ? null : source.node();
                    if (node != null) {
                        IPathingGrid path = node.getGrid()
                            .getCache(IPathingGrid.class);
                        var allocation = WirelessChannelPrototype.allocation(node.getGrid());
                        message(
                            sender,
                            "controller=" + path.getControllerState()
                                + " booting="
                                + path.isNetworkBooting()
                                + " managed="
                                + WirelessChannelPrototype.manages(node.getGrid())
                                + " geometricCapacity="
                                + WirelessChannelPrototype.capacity(node.getGrid())
                                + " lastPathingAllocation="
                                + allocation);
                    }
                }
                default -> message(sender, getCommandUsage(sender));
            }
        } catch (IllegalArgumentException | IllegalStateException | FailedConnection failure) {
            throw new CommandException(
                "Wireless network: " + failure.getClass()
                    .getSimpleName() + ": " + failure.getMessage());
        }
    }

    private static Address address(String[] args) {
        ForgeDirection side = args.length == 6 ? ForgeDirection.valueOf(args[5].toUpperCase(Locale.ROOT))
            : ForgeDirection.UNKNOWN;
        return new Address(
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2]),
            Integer.parseInt(args[3]),
            Integer.parseInt(args[4]),
            side);
    }

    private static void message(ICommandSender sender, String text) {
        sender.addChatMessage(new ChatComponentText(text));
    }
}
