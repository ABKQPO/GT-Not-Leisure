package com.science.gtnl.common.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.util.GTUtility;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class CommandSteamNetwork extends CommandBase {

    @Override
    public String getCommandName() {
        return "steam_network";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/steam_network <add/set/join/display>";
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
            getCommandUsage(sender);
            return;
        }
        switch (strings[0]) {
            case "add" -> {
                if (!Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }
                String username = strings[1];
                if (username == null) username = sender.getCommandSenderName();
                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);

                String Steam_String = strings[2];

                // Usage is /gt global_energy_add username EU

                String EU_string_formatted = EnumChatFormatting.RED
                    + GTUtility.formatNumbers(new BigInteger(Steam_String))
                    + EnumChatFormatting.RESET;

                if (SteamWirelessNetworkManager.addSteamToGlobalSteamMap(uuid, new BigInteger(Steam_String)))
                    sender.addChatMessage(
                        new ChatComponentText(
                            "Successfully added " + EU_string_formatted
                                + "Steam to the global steam network of "
                                + formatted_username
                                + "."));
                else sender.addChatMessage(
                    new ChatComponentText(
                        "Failed to add " + EU_string_formatted
                            + "Steam to the global steam map of "
                            + formatted_username
                            + ". Insufficient steam in network. "));

                sender.addChatMessage(
                    new ChatComponentText(
                        formatted_username + " currently has "
                            + EnumChatFormatting.RED
                            + GTUtility.formatNumbers(
                                new BigInteger(
                                    SteamWirelessNetworkManager.getUserSteam(uuid)
                                        .toString()))
                            + EnumChatFormatting.RESET
                            + " Steam in their network."));

            }
            case "set" -> {
                if (!Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                // Usage is /gt global_energy_set username EU

                String username = strings[1];
                if (username == null) username = sender.getCommandSenderName();
                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);

                String Steam_String_0 = strings[2];

                if ((new BigInteger(Steam_String_0).compareTo(BigInteger.ZERO)) < 0) {
                    sender
                        .addChatMessage(new ChatComponentText("Cannot set a users steam network to a negative value."));
                    break;
                }

                SteamWirelessNetworkManager.setUserSteam(uuid, new BigInteger(Steam_String_0));

                sender.addChatMessage(
                    new ChatComponentText(
                        "Successfully set " + formatted_username
                            + "'s global steam network to "
                            + EnumChatFormatting.RED
                            + GTUtility.formatNumbers(new BigInteger(Steam_String_0))
                            + EnumChatFormatting.RESET
                            + " Steam."));

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
                    sender
                        .addChatMessage(new ChatComponentText("Usage: /steam_network join <your_name> <target_name>"));
                    break;
                }

                String formattedUsernameSubject = EnumChatFormatting.BLUE + usernameSubject + EnumChatFormatting.RESET;
                String formattedUsernameTeam = EnumChatFormatting.BLUE + usernameTeam + EnumChatFormatting.RESET;

                UUID uuidSubject = SpaceProjectManager.getPlayerUUIDFromName(usernameSubject);
                UUID uuidTeam = SpaceProjectManager.getLeader(SpaceProjectManager.getPlayerUUIDFromName(usernameTeam));

                UUID uuidSender = SpaceProjectManager.getPlayerUUIDFromName(sender.getCommandSenderName());
                UUID uuidSenderLeader = SpaceProjectManager.getLeader(uuidSender);

                boolean senderIsLeaderOfTeam = uuidSenderLeader.equals(uuidTeam);

                if (!senderIsLeaderOfTeam && !Utils.hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                if (uuidSubject.equals(uuidTeam)) {
                    // Leave team
                    SpaceProjectManager.putInTeam(uuidSubject, uuidSubject);
                    sender.addChatMessage(
                        new ChatComponentText(
                            "User " + formattedUsernameSubject + " has rejoined their own global steam network."));
                    break;
                }

                // Already in same team
                if (SpaceProjectManager.getLeader(uuidSubject)
                    .equals(SpaceProjectManager.getLeader(uuidTeam))) {
                    sender.addChatMessage(new ChatComponentText("They are already in the same network!"));
                    break;
                }

                // Join other's team
                SpaceProjectManager.putInTeam(uuidSubject, uuidTeam);

                sender.addChatMessage(
                    new ChatComponentText(
                        "Success! " + formattedUsernameSubject + " has joined " + formattedUsernameTeam + "."));
                sender.addChatMessage(
                    new ChatComponentText(
                        "To undo this simply join your own network again with /steam_network join "
                            + formattedUsernameSubject
                            + " "
                            + formattedUsernameSubject
                            + "."));
            }
            case "display" -> {

                // Usage is /gt global_energy_display username.

                String username = strings[1];
                if (username == null) username = sender.getCommandSenderName();
                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                UUID userUUID = SpaceProjectManager.getPlayerUUIDFromName(username);

                if (!SpaceProjectManager.isInTeam(userUUID)) {
                    sender.addChatMessage(
                        new ChatComponentText("User " + formatted_username + " has no global steam network."));
                    break;
                }
                UUID teamUUID = SpaceProjectManager.getLeader(userUUID);

                sender.addChatMessage(
                    new ChatComponentText(
                        "User " + formatted_username
                            + " has "
                            + EnumChatFormatting.RED
                            + GTUtility.formatNumbers(SteamWirelessNetworkManager.getUserSteam(userUUID))
                            + EnumChatFormatting.RESET
                            + "Steam in their network."));
                if (!userUUID.equals(teamUUID)) sender.addChatMessage(
                    new ChatComponentText(
                        "User " + formatted_username
                            + " is currently in network of "
                            + EnumChatFormatting.BLUE
                            + SpaceProjectManager.getPlayerNameFromUUID(teamUUID)
                            + EnumChatFormatting.RESET
                            + "."));

            }
            default -> sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid command/syntax detected."));
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

}
