package com.science.gtnl.common.command;

import static com.science.gtnl.utils.Utils.hasPermission;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gregtech.common.misc.WirelessNetworkManager.getUserEU;
import static gregtech.common.misc.WirelessNetworkManager.setUserEU;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import gregtech.api.util.GTUtility;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class CommandEnergyNetwork extends CommandBase {

    @Override
    public String getCommandName() {
        return "energy_network";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/energy_network <add|set|join|display> [args...]";
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
                if (!hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                if (args.length != 3) {
                    throw new WrongUsageException("/energy_network add <username> <EU>");
                }

                String username = args[1];
                String EU_String = args[2];
                if (username == null) username = sender.getCommandSenderName();
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);

                if (uuid == null) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "User not found: " + username);
                    return;
                }

                BigInteger euAmount;
                try {
                    euAmount = new BigInteger(EU_String);
                } catch (NumberFormatException e) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "Invalid EU value format: " + EU_String);
                    return;
                }

                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                String EU_string_formatted = EnumChatFormatting.RED + GTUtility.formatNumbers(euAmount)
                    + EnumChatFormatting.RESET;

                if (addEUToGlobalEnergyMap(uuid, euAmount)) {
                    sendChatMessage(
                        sender,
                        new ChatComponentText(
                            String.format(
                                "Successfully added %sEU to the global energy network of %s.",
                                EU_string_formatted,
                                formatted_username)));
                } else {
                    sendChatMessage(
                        sender,
                        new ChatComponentText(
                            String.format(
                                "Failed to add %sEU to the global energy map of %s. Insufficient energy in network.",
                                EU_string_formatted,
                                formatted_username)));
                }

                sendChatMessage(
                    sender,
                    new ChatComponentText(
                        String.format(
                            "%s currently has %sEU in their network.",
                            formatted_username,
                            EnumChatFormatting.RED + GTUtility.formatNumbers(new BigInteger(getUserEU(uuid).toString()))
                                + EnumChatFormatting.RESET)));
                break;
            }

            case "set": {
                if (!hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                if (args.length != 3) {
                    throw new WrongUsageException("/energy_network set <username> <EU>");
                }

                String username = args[1];
                String EU_String = args[2];
                if (username == null) username = sender.getCommandSenderName();
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);

                if (uuid == null) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "User not found: " + username);
                    return;
                }

                BigInteger euAmount;
                try {
                    euAmount = new BigInteger(EU_String);
                } catch (NumberFormatException e) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "Invalid EU value format: " + EU_String);
                    return;
                }

                if ((euAmount.compareTo(BigInteger.ZERO)) < 0) {
                    sendChatMessage(
                        sender,
                        new ChatComponentText("Cannot set a user's energy network to a negative value."));
                    break;
                }

                setUserEU(uuid, euAmount);

                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                String EU_string_formatted = EnumChatFormatting.RED + GTUtility.formatNumbers(euAmount)
                    + EnumChatFormatting.RESET;

                sendChatMessage(
                    sender,
                    new ChatComponentText(
                        String.format(
                            "Successfully set %s's global energy network to %sEU.",
                            formatted_username,
                            EU_string_formatted)));
                break;
            }
            case "join": {
                if (args.length != 3) {
                    throw new WrongUsageException("/energy_network join <user joining> <user to join>");
                }

                String usernameSubject = args[1];
                String usernameTeam = args[2];

                if (usernameSubject == null || usernameTeam == null) {
                    sendChatMessage(
                        sender,
                        EnumChatFormatting.RED
                            + "Invalid arguments. Usage: /energy_network join <user joining> <user to join>");
                    break;
                }

                UUID uuidSubject = SpaceProjectManager.getPlayerUUIDFromName(usernameSubject);
                UUID uuidTeam = SpaceProjectManager.getPlayerUUIDFromName(usernameTeam);

                if (uuidSubject == null) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "User not found: " + usernameSubject);
                    return;
                }
                if (uuidTeam == null) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "User not found: " + usernameTeam);
                    return;
                }

                UUID uuidSender = SpaceProjectManager.getPlayerUUIDFromName(sender.getCommandSenderName());
                UUID uuidSenderLeader = SpaceProjectManager.getLeader(uuidSender);
                UUID targetTeamLeader = SpaceProjectManager.getLeader(uuidTeam);

                boolean senderIsLeaderOfTargetTeam = uuidSenderLeader.equals(targetTeamLeader);

                if (!senderIsLeaderOfTargetTeam && !hasPermission(sender, 2)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                String formattedUsernameSubject = EnumChatFormatting.BLUE + usernameSubject + EnumChatFormatting.RESET;
                String formattedUsernameTeam = EnumChatFormatting.BLUE + usernameTeam + EnumChatFormatting.RESET;

                if (uuidSubject.equals(uuidTeam)) {
                    SpaceProjectManager.putInTeam(uuidSubject, uuidSubject);
                    sendChatMessage(
                        sender,
                        new ChatComponentText(
                            String.format(
                                "User %s has rejoined their own global energy network.",
                                formattedUsernameSubject)));
                    break;
                }

                if (SpaceProjectManager.getLeader(uuidSubject)
                    .equals(SpaceProjectManager.getLeader(uuidTeam))) {
                    sendChatMessage(sender, new ChatComponentText("They are already in the same network!"));
                    break;
                }

                SpaceProjectManager.putInTeam(uuidSubject, uuidTeam);

                sendChatMessage(
                    sender,
                    new ChatComponentText(
                        String.format("Success! %s has joined %s.", formattedUsernameSubject, formattedUsernameTeam)));
                sendChatMessage(
                    sender,
                    new ChatComponentText(
                        String.format(
                            "To undo this simply join your own network again with /energy_network join %s %s.",
                            formattedUsernameSubject,
                            usernameSubject)));
                break;
            }
            case "display": {
                if (args.length != 2) {
                    throw new WrongUsageException("/energy_network display <username>");
                }

                String username = args[1];
                if (username == null) username = sender.getCommandSenderName();
                UUID userUUID = SpaceProjectManager.getPlayerUUIDFromName(username);

                if (userUUID == null) {
                    sendChatMessage(sender, EnumChatFormatting.RED + "User not found: " + username);
                    return;
                }

                String formatted_username = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;

                if (!SpaceProjectManager.isInTeam(userUUID)) {
                    sendChatMessage(
                        sender,
                        new ChatComponentText(
                            String.format("User %s has no global energy network.", formatted_username)));
                    break;
                }
                UUID teamUUID = SpaceProjectManager.getLeader(userUUID);

                sendChatMessage(
                    sender,
                    new ChatComponentText(
                        String.format(
                            "User %s has %sEU in their network.",
                            formatted_username,
                            EnumChatFormatting.RED + GTUtility.formatNumbers(getUserEU(userUUID))
                                + EnumChatFormatting.RESET)));
                if (!userUUID.equals(teamUUID)) {
                    sendChatMessage(
                        sender,
                        new ChatComponentText(
                            String.format(
                                "User %s is currently in network of %s.",
                                formatted_username,
                                EnumChatFormatting.BLUE + SpaceProjectManager.getPlayerNameFromUUID(teamUUID)
                                    + EnumChatFormatting.RESET)));
                }
                break;
            }

            default:
                sender
                    .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid command/syntax detected."));
                printHelp(sender);
                break;
        }
    }

    private void sendChatMessage(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(message));
    }

    private void sendChatMessage(ICommandSender sender, ChatComponentText message) {
        sender.addChatMessage(message);
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
        sender.addChatMessage(new ChatComponentText("Usage: /energy_network <subcommand> [args...]"));
        sender.addChatMessage(new ChatComponentText(" Subcommands:"));
        sender.addChatMessage(
            new ChatComponentText(
                "  add <username> <EU> - Add EU to user's energy network (requires permission level 2)"));
        sender.addChatMessage(
            new ChatComponentText(
                "  set <username> <EU> - Set user's energy network EU (requires permission level 2)"));
        sender.addChatMessage(
            new ChatComponentText(
                "  join <user joining> <user to join> - Join energy network (requires permission level 0)"));
        sender.addChatMessage(
            new ChatComponentText(
                "  display <username> - Display user's energy network EU (requires permission level 0)"));
    }
}
