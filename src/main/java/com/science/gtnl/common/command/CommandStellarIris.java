package com.science.gtnl.common.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.world.stellar.StellarIrisUpgradeManager;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class CommandStellarIris extends CommandBase {

    @Override
    public String getCommandName() {
        return "stellar_iris";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stellar_iris <points/tier/upgrade> ...";
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
            sendMessage(sender, "Overworld is unavailable / 主世界不可用");
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
            sendMessage(sender, "Usage / 用法: /stellar_iris points <add/set> <player/team> <name> <amount>");
            return;
        }
        UUID targetId = resolveTarget(sender, arguments[2], arguments[3]);
        Integer amount = parseInteger(sender, arguments[4]);
        if (targetId == null || amount == null) {
            return;
        }
        if ("set".equals(arguments[1])) {
            if (amount < 0) {
                sendMessage(sender, "Points cannot be negative / 点数不能为负数");
                return;
            }
            StellarIrisUpgradeManager.setSpendablePoints(world, targetId, amount);
            sendMessage(sender, "Points updated / 点数已更新: " + amount);
            return;
        }
        if (!StellarIrisUpgradeManager.addPoints(world, targetId, amount)) {
            sendMessage(sender, "Point operation would exceed the valid range / 点数操作超出有效范围");
            return;
        }
        sendMessage(sender, "Points adjusted / 点数已调整: " + amount);
    }

    private void handleTier(ICommandSender sender, World world, String[] arguments) {
        if (arguments.length != 5 || !"set".equals(arguments[1])) {
            sendMessage(sender, "Usage / 用法: /stellar_iris tier set <player/team> <name> <tier>");
            return;
        }
        UUID targetId = resolveTarget(sender, arguments[2], arguments[3]);
        Integer tier = parseInteger(sender, arguments[4]);
        if (targetId == null || tier == null) {
            return;
        }
        if (tier < 0) {
            sendMessage(sender, "Tier cannot be negative / 阶级不能为负数");
            return;
        }
        StellarIrisUpgradeManager.setTier(world, targetId, tier);
        sendMessage(sender, "Tier updated / 阶级已更新: " + tier);
    }

    private void handleUpgrade(ICommandSender sender, World world, String[] arguments) {
        if (arguments.length < 5 || arguments.length > 6
            || (!"enable".equals(arguments[1]) && !"disable".equals(arguments[1]))) {
            sendMessage(
                sender,
                "Usage / 用法: /stellar_iris upgrade <enable/disable> <player/team> <name> <upgrade> [level]");
            return;
        }
        UUID targetId = resolveTarget(sender, arguments[2], arguments[3]);
        if (targetId == null) {
            return;
        }
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(arguments[4]);
        if (definition == null) {
            sendMessage(sender, "Unknown upgrade / 未知升级节点: " + arguments[4]);
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
            sendMessage(sender, "Disable does not accept a level / 禁用节点不接受等级参数");
            return;
        }
        if (!StellarIrisUpgradeManager.setUpgradeLevel(world, targetId, definition.getId(), level)) {
            sendMessage(
                sender,
                "Level must be between 0 and " + definition.getMaxLevel() + " / 等级必须在 0 到 " + definition.getMaxLevel());
            return;
        }
        sendMessage(
            sender,
            "Upgrade " + (level == 0 ? "disabled / 已禁用" : "enabled / 已启用") + ": " + definition.getId() + " = " + level);
    }

    private UUID resolveTarget(ICommandSender sender, String targetType, String targetName) {
        if (!"player".equals(targetType) && !"team".equals(targetType)) {
            sendMessage(sender, "Target must be player or team / 目标必须是 player 或 team");
            return null;
        }
        UUID targetId = SpaceProjectManager.getPlayerUUIDFromName(targetName);
        if (targetId == null) {
            sendMessage(sender, "Unknown target / 未知目标: " + targetName);
            return null;
        }
        SpaceProjectManager.checkOrCreateTeam(targetId);
        return "team".equals(targetType) ? SpaceProjectManager.getLeader(targetId) : targetId;
    }

    private Integer parseInteger(ICommandSender sender, String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            sendMessage(sender, "Expected an integer / 需要整数: " + value);
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
        sendMessage(sender, "Stellar Iris command usage / 星体虹膜命令用法: " + getCommandUsage(sender));
    }

    private void sendMessage(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + message));
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
