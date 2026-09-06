package com.science.gtnl.common.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

public class CommandSteamNetwork extends CommandBase {

    @Override
    public String getCommandName() {
        return "steam_network";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.steam_network.usage";
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

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (strings.length < 1) {
            sendMessage(sender, "gtnl.command.steam_network.usage");
            return;
        }
        switch (strings[0]) {
            case "add" -> {
                if (!Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }
                if (strings.length != 3) {
                    sendMessage(sender, "gtnl.command.steam_network.add_usage");
                    break;
                }
                String username = strings[1];
                if (username == null) username = sender.getCommandSenderName();
                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                UUID uuid = TeamNetworkManager.getPlayerId(username);

                String Steam_String = strings[2];

                // Usage is /gt global_energy_add username EU

                String EU_string_formatted = EnumChatFormatting.RED
                    + NumberFormatUtil.formatNumber(new BigInteger(Steam_String))
                    + EnumChatFormatting.RESET;

                if (SteamWirelessNetworkManager.addSteamToGlobalSteamMap(uuid, new BigInteger(Steam_String)))
                    sendMessage(
                        sender,
                        "gtnl.command.steam_network.add_success",
                        EU_string_formatted,
                        formatted_username);
                else sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.steam_network.add_failed",
                        EU_string_formatted,
                        formatted_username));

                sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.steam_network.balance",
                        formatted_username,
                        EnumChatFormatting.RED
                            + NumberFormatUtil.formatNumber(SteamWirelessNetworkManager.getUserSteam(uuid))
                            + EnumChatFormatting.RESET));

            }
            case "set" -> {
                if (!Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }
                if (strings.length != 3) {
                    sendMessage(sender, "gtnl.command.steam_network.set_usage");
                    break;
                }

                // Usage is /gt global_energy_set username EU

                String username = strings[1];
                if (username == null) username = sender.getCommandSenderName();
                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                UUID uuid = TeamNetworkManager.getPlayerId(username);

                String Steam_String_0 = strings[2];

                if ((new BigInteger(Steam_String_0).compareTo(BigInteger.ZERO)) < 0) {
                    sender.addChatMessage(new ChatComponentTranslation("gtnl.command.steam_network.negative_amount"));
                    break;
                }

                SteamWirelessNetworkManager.setUserSteam(uuid, new BigInteger(Steam_String_0));

                sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.steam_network.set_success",
                        formatted_username,
                        EnumChatFormatting.RED + NumberFormatUtil.formatNumber(new BigInteger(Steam_String_0))
                            + EnumChatFormatting.RESET));

            }
            case "join" -> {

                // Usage: /gt global_energy_join username_of_you username_to_join

                String usernameSubject;
                String usernameTeam;

                if (strings.length == 3) {
                    usernameSubject = strings[1];
                    usernameTeam = strings[2];
                } else if (strings.length == 2) {
                    usernameTeam = strings[1];
                    usernameSubject = sender.getCommandSenderName();
                } else {
                    sendMessage(sender, "gtnl.command.steam_network.join_usage");
                    break;
                }

                String formattedUsernameSubject = EnumChatFormatting.BLUE + usernameSubject + EnumChatFormatting.RESET;
                String formattedUsernameTeam = EnumChatFormatting.BLUE + usernameTeam + EnumChatFormatting.RESET;

                UUID uuidSubject = TeamNetworkManager.getPlayerId(usernameSubject);
                UUID uuidTeam = TeamNetworkManager.getPlayerId(usernameTeam);
                UUID uuidSender = TeamNetworkManager.getPlayerId(sender.getCommandSenderName());
                boolean senderIsLeaderOfTeam = TeamNetworkManager.isSameTeam(uuidSender, uuidTeam)
                    && TeamNetworkManager.isTeamOwner(uuidSender);

                if (!senderIsLeaderOfTeam && !Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                if (uuidSubject.equals(uuidTeam)) {
                    // Leave team
                    TeamNetworkManager.createPersonalTeam(uuidSubject);
                    sendMessage(sender, "gtnl.command.steam_network.rejoined", formattedUsernameSubject);
                    break;
                }

                // Already in same team
                if (TeamNetworkManager.isSameTeam(uuidSubject, uuidTeam)) {
                    sendMessage(sender, "gtnl.command.common.already_same_network");
                    break;
                }

                // Join other's team
                TeamNetworkManager.joinTeam(uuidSubject, uuidTeam);

                sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.steam_network.join_success",
                        formattedUsernameSubject,
                        formattedUsernameTeam));
                sendMessage(
                    sender,
                    "gtnl.command.steam_network.join_undo",
                    formattedUsernameSubject,
                    formattedUsernameSubject);
            }
            case "display" -> {
                if (strings.length != 2 && strings.length != 1) {
                    sendMessage(sender, "gtnl.command.steam_network.display_usage");
                    break;
                }

                // Usage is /gt global_energy_display username.

                String username = strings.length == 2 ? strings[1] : sender.getCommandSenderName();
                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                UUID userUUID = TeamNetworkManager.getPlayerId(username);
                UUID teamUUID = TeamNetworkManager.getTeamId(userUUID);

                sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.steam_network.balance",
                        formatted_username,
                        EnumChatFormatting.RED
                            + NumberFormatUtil.formatNumber(SteamWirelessNetworkManager.getUserSteam(userUUID))
                            + EnumChatFormatting.RESET));
                if (!TeamNetworkManager.isTeamOwner(userUUID)) sender.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.command.common.network_owner",
                        formatted_username,
                        EnumChatFormatting.BLUE + TeamNetworkManager.getTeamName(teamUUID) + EnumChatFormatting.RESET));
            }
            default -> sendMessage(sender, "gtnl.command.steam_network.usage");
        }
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

    private void sendMessage(ICommandSender sender, String key, Object... arguments) {
        sender.addChatMessage(new ChatComponentTranslation(key, arguments));
    }

}
