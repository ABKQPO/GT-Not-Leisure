package com.science.gtnl.common.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

import gregtech.common.misc.WirelessNetworkManager;

public class CommandEnergyNetwork extends CommandBase {

    @Override
    public String getCommandName() {
        return "energy_network";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.energy_network.usage";
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            printHelp(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add": {
                if (!Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                if (args.length != 3) {
                    throw new WrongUsageException("gtnl.command.energy_network.add_usage");
                }

                String username = args[1];
                String EU_String = args[2];
                if (username == null) username = sender.getCommandSenderName();
                UUID uuid = TeamNetworkManager.getPlayerId(username);

                if (uuid == null) {
                    sendMessage(sender, "gtnl.command.common.player_not_found", username);
                    return;
                }

                BigInteger euAmount;
                try {
                    euAmount = new BigInteger(EU_String);
                } catch (NumberFormatException e) {
                    sendMessage(sender, "gtnl.command.energy_network.invalid_amount", EU_String);
                    return;
                }

                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                String EU_string_formatted = EnumChatFormatting.RED + NumberFormatUtil.formatNumber(euAmount)
                    + EnumChatFormatting.RESET;

                if (WirelessNetworkManager.addEUToGlobalEnergyMap(uuid, euAmount)) {
                    sendMessage(
                        sender,
                        "gtnl.command.energy_network.add_success",
                        EU_string_formatted,
                        formatted_username);
                } else {
                    sendMessage(
                        sender,
                        "gtnl.command.energy_network.add_failed",
                        EU_string_formatted,
                        formatted_username);
                }

                sendMessage(
                    sender,
                    "gtnl.command.energy_network.balance",
                    formatted_username,
                    EnumChatFormatting.RED + NumberFormatUtil.formatNumber(WirelessNetworkManager.getUserEU(uuid))
                        + EnumChatFormatting.RESET);
                break;
            }

            case "set": {
                if (!Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                if (args.length != 3) {
                    throw new WrongUsageException("gtnl.command.energy_network.set_usage");
                }

                String username = args[1];
                String EU_String = args[2];
                if (username == null) username = sender.getCommandSenderName();
                UUID uuid = TeamNetworkManager.getPlayerId(username);

                if (uuid == null) {
                    sendMessage(sender, "gtnl.command.common.player_not_found", username);
                    return;
                }

                BigInteger euAmount;
                try {
                    euAmount = new BigInteger(EU_String);
                } catch (NumberFormatException e) {
                    sendMessage(sender, "gtnl.command.energy_network.invalid_amount", EU_String);
                    return;
                }

                if ((euAmount.compareTo(BigInteger.ZERO)) < 0) {
                    sendMessage(sender, "gtnl.command.energy_network.negative_amount");
                    break;
                }

                WirelessNetworkManager.setUserEU(uuid, euAmount);

                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                String EU_string_formatted = EnumChatFormatting.RED + NumberFormatUtil.formatNumber(euAmount)
                    + EnumChatFormatting.RESET;

                sendMessage(sender, "gtnl.command.energy_network.set_success", formatted_username, EU_string_formatted);
                break;
            }
            case "join": {
                if (args.length != 3) {
                    throw new WrongUsageException("gtnl.command.energy_network.join_usage");
                }

                String usernameSubject = args[1];
                String usernameTeam = args[2];

                if (usernameSubject == null || usernameTeam == null) {
                    sendMessage(sender, "gtnl.command.energy_network.join_usage");
                    break;
                }

                UUID uuidSubject = TeamNetworkManager.getPlayerId(usernameSubject);
                UUID uuidTeam = TeamNetworkManager.getPlayerId(usernameTeam);

                if (uuidSubject == null) {
                    sendMessage(sender, "gtnl.command.common.player_not_found", usernameSubject);
                    return;
                }
                if (uuidTeam == null) {
                    sendMessage(sender, "gtnl.command.common.player_not_found", usernameTeam);
                    return;
                }

                UUID uuidSender = TeamNetworkManager.getPlayerId(sender.getCommandSenderName());
                boolean senderIsLeaderOfTargetTeam = TeamNetworkManager.isSameTeam(uuidSender, uuidTeam)
                    && TeamNetworkManager.isTeamOwner(uuidSender);

                if (!senderIsLeaderOfTargetTeam && !Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                String formattedUsernameSubject = EnumChatFormatting.BLUE + usernameSubject + EnumChatFormatting.RESET;
                String formattedUsernameTeam = EnumChatFormatting.BLUE + usernameTeam + EnumChatFormatting.RESET;

                if (uuidSubject.equals(uuidTeam)) {
                    TeamNetworkManager.createPersonalTeam(uuidSubject);
                    sendMessage(sender, "gtnl.command.energy_network.rejoined", formattedUsernameSubject);
                    break;
                }

                if (TeamNetworkManager.isSameTeam(uuidSubject, uuidTeam)) {
                    sendMessage(sender, "gtnl.command.common.already_same_network");
                    break;
                }

                TeamNetworkManager.joinTeam(uuidSubject, uuidTeam);

                sendMessage(
                    sender,
                    "gtnl.command.energy_network.join_success",
                    formattedUsernameSubject,
                    formattedUsernameTeam);
                sendMessage(sender, "gtnl.command.energy_network.join_undo", formattedUsernameSubject, usernameSubject);
                break;
            }
            case "display": {
                if (args.length != 2) {
                    throw new WrongUsageException("gtnl.command.energy_network.display_usage");
                }

                String username = args[1];
                if (username == null) username = sender.getCommandSenderName();
                UUID userUUID = TeamNetworkManager.getPlayerId(username);

                if (userUUID == null) {
                    sendMessage(sender, "gtnl.command.common.player_not_found", username);
                    return;
                }

                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;

                UUID teamUUID = TeamNetworkManager.getTeamId(userUUID);

                sendMessage(
                    sender,
                    "gtnl.command.energy_network.balance",
                    formatted_username,
                    EnumChatFormatting.RED + NumberFormatUtil.formatNumber(WirelessNetworkManager.getUserEU(userUUID))
                        + EnumChatFormatting.RESET);
                if (!TeamNetworkManager.isTeamOwner(userUUID)) {
                    sendMessage(
                        sender,
                        "gtnl.command.common.network_owner",
                        formatted_username,
                        EnumChatFormatting.BLUE + TeamNetworkManager.getTeamName(teamUUID) + EnumChatFormatting.RESET);
                }
                break;
            }

            default:
                sendMessage(sender, "gtnl.command.common.invalid_syntax");
                printHelp(sender);
                break;
        }
    }

    private void sendMessage(ICommandSender sender, String key, Object... arguments) {
        sender.addChatMessage(new ChatComponentTranslation(key, arguments));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        String currentArg = args.length == 0 ? "" : args[args.length - 1].trim();

        if (args.length == 1) {
            Stream.of("add", "set", "join", "display")
                .filter(s -> s.startsWith(currentArg))
                .forEach(completions::add);
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if ("add".equals(subCommand) || "set".equals(subCommand)
                || "join".equals(subCommand)
                || "display".equals(subCommand)) {
                List<String> onlinePlayerNames = getListOfStringsMatchingLastWord(
                    args,
                    MinecraftServer.getServer()
                        .getAllUsernames());
                completions.addAll(onlinePlayerNames);
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if ("join".equals(subCommand)) {
                List<String> onlinePlayerNames = getListOfStringsMatchingLastWord(
                    args,
                    MinecraftServer.getServer()
                        .getAllUsernames());
                completions.addAll(onlinePlayerNames);
            }
        }

        return completions;
    }

    private void printHelp(ICommandSender sender) {
        sendMessage(sender, "gtnl.command.energy_network.usage");
        sendMessage(sender, "gtnl.command.energy_network.help_header");
        sendMessage(sender, "gtnl.command.energy_network.help_add");
        sendMessage(sender, "gtnl.command.energy_network.help_set");
        sendMessage(sender, "gtnl.command.energy_network.help_join");
        sendMessage(sender, "gtnl.command.energy_network.help_display");
    }
}
