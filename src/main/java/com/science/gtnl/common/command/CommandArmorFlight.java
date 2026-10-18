package com.science.gtnl.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;

import com.science.gtnl.api.armorflight.ArmorFlightAPI;
import com.science.gtnl.api.armorflight.ArmorFlightAnchor;
import com.science.gtnl.api.armorflight.ArmorFlightApproach;
import com.science.gtnl.api.armorflight.ArmorFlightPiece;
import com.science.gtnl.api.armorflight.IArmorFlightService;
import com.science.gtnl.common.armorflight.ArmorFlight;
import com.science.gtnl.common.armorflight.FlightVec;

public class CommandArmorFlight extends CommandBase {

    @Override
    public String getCommandName() {
        return "armorflight";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.armor_flight.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "call", "off");
        if (args.length == 3 && args[0].equals("call")) {
            return getListOfStringsMatchingLastWord(args, "random", "front", "behind");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 3) throw new WrongUsageException(getCommandUsage(sender));
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        double distance = args.length >= 2 ? parseDoubleBounded(sender, args[1], 2, 64) : 12;
        ArmorFlightAnchor anchor = anchorAhead(player, distance);
        IArmorFlightService service = ArmorFlightAPI.service();
        switch (args[0]) {
            case "call" -> {
                ArmorFlightApproach approach = args.length == 3 ? ArmorFlightApproach.byName(args[2])
                    : ArmorFlightApproach.RANDOM;
                if (approach == null) throw new WrongUsageException(getCommandUsage(sender));
                if (service.isDelivering(player)) {
                    sender.addChatMessage(new ChatComponentTranslation("gtnl.command.armor_flight.busy"));
                    return;
                }
                Map<ArmorFlightPiece, ItemStack> suit = takeSuit(player);
                if (suit.isEmpty()) {
                    sender.addChatMessage(new ChatComponentTranslation("gtnl.command.armor_flight.no_armor"));
                    return;
                }
                int count = suit.size();
                int ticks = service.summon(player, suit, anchor, approach);
                sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.armor_flight.summoned",
                        count,
                        String.format("%.1f", ticks / 20.0)));
            }
            case "off" -> {
                List<ArmorFlightPiece> worn = new ArrayList<>();
                for (ArmorFlightPiece p : ArmorFlightPiece.VALUES) if (p.worn(player) != null) worn.add(p);
                int purged = worn.isEmpty() ? 0 : service.purge(player, worn, anchor);
                sender.addChatMessage(
                    purged > 0 ? new ChatComponentTranslation("gtnl.command.armor_flight.purged", purged)
                        : new ChatComponentTranslation("gtnl.command.armor_flight.nothing_worn"));
            }
            default -> throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private static Map<ArmorFlightPiece, ItemStack> takeSuit(EntityPlayerMP player) {
        Map<ArmorFlightPiece, ItemStack> suit = new EnumMap<>(ArmorFlightPiece.class);
        ItemStack[] inv = player.inventory.mainInventory;
        for (ArmorFlightPiece p : ArmorFlightPiece.SEQUENCE) {
            for (int i = 0; i < inv.length; i++) {
                if (!p.accepts(inv[i], player)) continue;
                suit.put(p, inv[i]);
                inv[i] = null;
                break;
            }
        }
        player.inventory.markDirty();
        return suit;
    }

    private static ArmorFlightAnchor anchorAhead(EntityPlayerMP player, double distance) {
        FlightVec ahead = ArmorFlight.forward(player.rotationYaw)
            .scale(distance);
        return new ArmorFlightAnchor(
            player.worldObj.provider.dimensionId,
            MathHelper.floor_double(player.posX + ahead.x()),
            MathHelper.floor_double(player.boundingBox.minY),
            MathHelper.floor_double(player.posZ + ahead.z()),
            0.2);
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("armor_flight");
    }
}
