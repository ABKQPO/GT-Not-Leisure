package com.science.gtnl.common.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.world.stellar.StellarIrisUpgradeManager;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

public class CommandStellarIris extends CommandBase {

    @Override
    public String getCommandName() {
        return "stellar_iris";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.stellar_iris.usage";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] arguments) {
        List<String> completions = new ArrayList<>();
        String currentArgument = arguments.length == 0 ? "" : arguments[arguments.length - 1];
        if (arguments.length == 1) {
            addMatchingCompletions(completions, currentArgument, "points", "tier", "upgrade");
        } else if (arguments.length == 2) {
            if ("points".equals(arguments[0])) {
                addMatchingCompletions(completions, currentArgument, "add", "set");
            } else if ("tier".equals(arguments[0])) {
                addMatchingCompletions(completions, currentArgument, "set");
            } else if ("upgrade".equals(arguments[0])) {
                addMatchingCompletions(completions, currentArgument, "enable", "disable");
            }
        } else if (arguments.length == 3) {
            addMatchingCompletions(completions, currentArgument, "player", "team");
        } else if (arguments.length == 4) {
            completions.addAll(
                getListOfStringsMatchingLastWord(
                    arguments,
                    MinecraftServer.getServer()
                        .getAllUsernames()));
        } else if (arguments.length == 5 && "upgrade".equals(arguments[0])) {
            for (StellarIrisUpgradeDefinition definition : StellarIrisUpgradeRegistry.getUpgrades()) {
                if (definition.getId()
                    .startsWith(currentArgument)) {
                    completions.add(definition.getId());
                }
            }
        }
        return completions;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arguments) {
        if (!Utils.hasPermission(sender, 2)) {
            sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
            return;
        }
        if (arguments.length == 0) {
            sendUsage(sender);
            return;
        }
        World world = MinecraftServer.getServer()
            .worldServerForDimension(0);
        if (world == null) {
            sendMessage(sender, "gtnl.command.stellar_iris.overworld_unavailable");
            return;
        }
        if ("points".equals(arguments[0])) {
            handlePoints(sender, world, arguments);
        } else if ("tier".equals(arguments[0])) {
            handleTier(sender, world, arguments);
        } else if ("upgrade".equals(arguments[0])) {
            handleUpgrade(sender, world, arguments);
        } else {
            sendUsage(sender);
        }
    }

    private void handlePoints(ICommandSender sender, World world, String[] arguments) {
        if (arguments.length != 5 || (!"add".equals(arguments[1]) && !"set".equals(arguments[1]))) {
            sendMessage(sender, "gtnl.command.stellar_iris.points_usage");
            return;
        }
        UUID targetId = resolveTarget(sender, arguments[2], arguments[3]);
        Integer amount = parseInteger(sender, arguments[4]);
        if (targetId == null || amount == null) {
            return;
        }
        if ("set".equals(arguments[1])) {
            if (amount < 0) {
                sendMessage(sender, "gtnl.command.stellar_iris.points_negative");
                return;
            }
            StellarIrisUpgradeManager.setSpendablePoints(world, targetId, amount);
            sendMessage(sender, "gtnl.command.stellar_iris.points_updated", amount);
            return;
        }
        if (!StellarIrisUpgradeManager.addPoints(world, targetId, amount)) {
            sendMessage(sender, "gtnl.command.stellar_iris.points_range");
            return;
        }
        sendMessage(sender, "gtnl.command.stellar_iris.points_adjusted", amount);
    }

    private void handleTier(ICommandSender sender, World world, String[] arguments) {
        if (arguments.length != 5 || !"set".equals(arguments[1])) {
            sendMessage(sender, "gtnl.command.stellar_iris.tier_usage");
            return;
        }
        UUID targetId = resolveTarget(sender, arguments[2], arguments[3]);
        Integer tier = parseInteger(sender, arguments[4]);
        if (targetId == null || tier == null) {
            return;
        }
        if (tier < 0) {
            sendMessage(sender, "gtnl.command.stellar_iris.tier_negative");
            return;
        }
        StellarIrisUpgradeManager.setTier(world, targetId, tier);
        sendMessage(sender, "gtnl.command.stellar_iris.tier_updated", tier);
    }

    private void handleUpgrade(ICommandSender sender, World world, String[] arguments) {
        if (arguments.length < 5 || arguments.length > 6
            || (!"enable".equals(arguments[1]) && !"disable".equals(arguments[1]))) {
            sendMessage(sender, "gtnl.command.stellar_iris.upgrade_usage");
            return;
        }
        UUID targetId = resolveTarget(sender, arguments[2], arguments[3]);
        if (targetId == null) {
            return;
        }
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(arguments[4]);
        if (definition == null) {
            sendMessage(sender, "gtnl.command.stellar_iris.upgrade_unknown", arguments[4]);
            return;
        }
        int level = 0;
        if ("enable".equals(arguments[1])) {
            level = 1;
            if (arguments.length == 6) {
                Integer requestedLevel = parseInteger(sender, arguments[5]);
                if (requestedLevel == null) {
                    return;
                }
                level = requestedLevel;
            }
        } else if (arguments.length == 6) {
            sendMessage(sender, "gtnl.command.stellar_iris.disable_level");
            return;
        }
        if (!StellarIrisUpgradeManager.setUpgradeLevel(world, targetId, definition.getId(), level)) {
            sendMessage(sender, "gtnl.command.stellar_iris.level_range", definition.getMaxLevel());
            return;
        }
        sendMessage(
            sender,
            level == 0 ? "gtnl.command.stellar_iris.upgrade_disabled" : "gtnl.command.stellar_iris.upgrade_enabled",
            definition.getId(),
            level);
    }

    private UUID resolveTarget(ICommandSender sender, String targetType, String targetName) {
        if (!"player".equals(targetType) && !"team".equals(targetType)) {
            sendMessage(sender, "gtnl.command.stellar_iris.target_type");
            return null;
        }
        UUID targetId = TeamNetworkManager.getPlayerId(targetName);
        if (targetId == null) {
            sendMessage(sender, "gtnl.command.stellar_iris.target_unknown", targetName);
            return null;
        }
        return "team".equals(targetType) ? TeamNetworkManager.getTeamId(targetId) : targetId;
    }

    private Integer parseInteger(ICommandSender sender, String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            sendMessage(sender, "gtnl.command.stellar_iris.integer_expected", value);
            return null;
        }
    }

    private void addMatchingCompletions(List<String> completions, String currentArgument, String... values) {
        for (String value : values) {
            if (value.startsWith(currentArgument)) {
                completions.add(value);
            }
        }
    }

    private void sendUsage(ICommandSender sender) {
        sendMessage(sender, "gtnl.command.stellar_iris.usage");
    }

    private void sendMessage(ICommandSender sender, String key, Object... arguments) {
        ChatComponentTranslation message = new ChatComponentTranslation(key, arguments);
        message.getChatStyle()
            .setColor(EnumChatFormatting.AQUA);
        sender.addChatMessage(message);
    }

    @Override
    public int compareTo(Object object) {
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
