package com.science.gtnl.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.GameRules;

import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.config.MainConfig;

/**
 * @author Guilherme Chaguri
 */
public class CommandTickrate extends CommandBase {

    public List<String> aliases;
    public List<String> suggestedTickrateValues;

    public CommandTickrate() {
        aliases = Arrays.asList("ticks", "trc", "settickrate");
        suggestedTickrateValues = Arrays.asList("20", "2.5", "5", "10", "15", "25", "35", "50", "100");
    }

    @Override
    public String getCommandName() {
        return "tickrate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.tickrate.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            return null;
        }
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.addAll(suggestedTickrateValues);
            float defaultTickrate = MainConfig.tickrate.defaultTickrate;
            String defTickrate = defaultTickrate + "";
            if (defaultTickrate == (int) defaultTickrate) defTickrate = (int) defaultTickrate + "";
            if (!tab.contains(defTickrate)) {
                tab.addFirst(defTickrate);
            }
            tab.add("setmap");
            tab.add("freeze");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setmap")) {
                tab.addAll(suggestedTickrateValues);
                float defaultTickrate = MainConfig.tickrate.defaultTickrate;
                String defTickrate = defaultTickrate + "";
                if (defaultTickrate == (int) defaultTickrate) defTickrate = (int) defaultTickrate + "";
                if (!tab.contains(defTickrate)) {
                    tab.addFirst(defTickrate);
                }
            } else {
                tab.add("all");
                tab.add("server");
                tab.add("client");
                for (EntityPlayer p : MinecraftServer.getServer()
                    .getConfigurationManager().playerEntityList) {
                    tab.add(p.getCommandSenderName());
                }
            }
        } else if (((args.length == 3) || (args.length == 4)) && (args[0].equalsIgnoreCase("setmap"))) {
            tab.add("--dontupdate");
        }
        return tab;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            send(
                sender,
                t("gtnl.command.tickrate.current_server", 'f', 'l'),
                c(" ", 'f', 'l'),
                t("gtnl.command.tickrate.ticks_per_second", new Object[] { TickrateAPI.getServerTickrate() }, 'a'));
            try {
                GameRules rules = MinecraftServer.getServer()
                    .getEntityWorld()
                    .getGameRules();
                if (rules.hasRule(TickrateAPI.GAME_RULE)) {
                    float tickrate = Float.parseFloat(rules.getGameRuleStringValue(TickrateAPI.GAME_RULE));
                    send(
                        sender,
                        t("gtnl.command.tickrate.current_map", 'f', 'l'),
                        c(" ", 'f', 'l'),
                        t("gtnl.command.tickrate.ticks_per_second", new Object[] { tickrate }, 'a'));
                }
            } catch (Exception ignored) {}
            send(
                sender,
                t("gtnl.command.tickrate.usage_change_prefix", 'b'),
                t("gtnl.command.tickrate.player_name", 'b', 'o'),
                t("gtnl.command.tickrate.usage_change_suffix", 'b'));
            send(sender, t("gtnl.command.tickrate.usage_setmap", 'b'));
            send(sender);
            send(
                sender,
                t("gtnl.command.tickrate.help_prompt_prefix", 'c'),
                c(" ", 'c'),
                c("/tickrate help", 'c', 'n'),
                c(" ", 'c'),
                t("gtnl.command.tickrate.help_prompt_suffix", 'c'));
            send(sender);
            return;
        }
        if (args[0].equalsIgnoreCase("help")) {
            send(
                sender,
                c(" ", '5', 'l'),
                t("gtnl.command.tickrate.help_title", '5', 'l'),
                c(" ", '5', 'l'),
                t("gtnl.command.tickrate.help_by", '7', 'o'),
                c(" ", '7', 'o'),
                t("gtnl.command.tickrate.help_author", 'f', 'o'));
            send(sender, t("gtnl.command.tickrate.help_hover_hint", 'f', 'l'));
            send(
                sender,
                h(
                    "/tickrate 20 ",
                    new IChatComponent[] { t("gtnl.command.tickrate.help_set_prefix", 'a'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_server_and_client", 'f'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_set_to_twenty", 'a') },
                    '7'));
            send(
                sender,
                h(
                    "/tickrate 20 server ",
                    new IChatComponent[] { t("gtnl.command.tickrate.help_set_prefix", 'a'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_server", 'f'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_set_to_twenty", 'a') },
                    '7'));
            send(
                sender,
                h(
                    "/tickrate 20 client ",
                    new IChatComponent[] { t("gtnl.command.tickrate.help_sets", 'a'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_all_clients", 'f'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_set_to_twenty", 'a') },
                    '7'));
            send(
                sender,
                h(
                    "/tickrate 20 Notch ",
                    new IChatComponent[] { t("gtnl.command.tickrate.help_set_prefix", 'a'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_notch_client", 'f'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_set_to_twenty", 'a') },
                    '7'));
            send(
                sender,
                h(
                    "/tickrate setmap 20 ",
                    new IChatComponent[] { t("gtnl.command.tickrate.help_set_prefix", 'a'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_map", 'f'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_set_to_twenty", 'a') },
                    '7'));
            send(
                sender,
                h(
                    "/tickrate setmap 20 --dontupdate ",
                    new IChatComponent[] { t("gtnl.command.tickrate.help_set_prefix", 'a'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_map", 'f'), c(" ", 'a'),
                        t("gtnl.command.tickrate.help_set_without_updating", 'a') },
                    '7'));
            send(sender, t("gtnl.command.tickrate.help_divider", '5', 'l'));
            return;
        } else if ((args[0].equalsIgnoreCase("setmap")) && (args.length > 1)) {
            boolean update = true;
            for (String s : args) {
                if (s.equalsIgnoreCase("--dontupdate")) {
                    update = false;
                    break;
                }
            }
            float ticksPerSecond;
            try {
                ticksPerSecond = Float.parseFloat(args[1]);
            } catch (Exception ex) {
                send(sender, t("gtnl.command.tickrate.error", '4'));
                send(sender, t("gtnl.command.tickrate.usage_setmap", 'c'));
                return;
            }
            if (!TickrateAPI.isValidTickrate(ticksPerSecond)) {
                send(
                    sender,
                    t("gtnl.command.tickrate.invalid_value", 'c'),
                    t("gtnl.command.tickrate.positive_requirement", '7'));
                return;
            }
            TickrateAPI.changeMapTickrate(ticksPerSecond);
            if (update) {
                TickrateAPI.changeTickrate(ticksPerSecond);
            }
            send(
                sender,
                t("gtnl.command.tickrate.map_changed_prefix", 'a'),
                c(" " + ticksPerSecond, 'f'),
                t("gtnl.command.tickrate.period", 'a'));
            return;
        } else if (args[0].equalsIgnoreCase("freeze")) {
            if (TimeStopPocketWatch.isTimeStopped()) {
                TimeStopPocketWatch.setTimeStopped(false);
                sender.addChatMessage(new ChatComponentTranslation("gtnl.command.tickrate.unfreeze"));
            } else {
                TimeStopPocketWatch.setTimeStopped(true);
                sender.addChatMessage(new ChatComponentTranslation("gtnl.command.tickrate.freeze"));
            }
            return;
        }

        float ticksPerSecond;
        try {
            ticksPerSecond = Float.parseFloat(args[0]);
        } catch (Exception ex) {
            send(sender, t("gtnl.command.tickrate.error", '4'));
            send(
                sender,
                t("gtnl.command.tickrate.usage_change_prefix", 'c'),
                t("gtnl.command.tickrate.player_name", 'c', 'o'),
                t("gtnl.command.tickrate.usage_change_suffix", 'c'));
            return;
        }

        if (!TickrateAPI.isValidTickrate(ticksPerSecond)) {
            send(
                sender,
                t("gtnl.command.tickrate.invalid_value", 'c'),
                t("gtnl.command.tickrate.positive_requirement", '7'));
            return;
        }

        if ((args.length < 2) || (args[1].equalsIgnoreCase("all"))) {
            TickrateAPI.changeTickrate(ticksPerSecond);
            send(
                sender,
                t("gtnl.command.tickrate.changed_prefix", 'a'),
                c(" " + ticksPerSecond, 'f'),
                t("gtnl.command.tickrate.period", 'a'));
        } else if (args[1].equalsIgnoreCase("client")) {
            TickrateAPI.changeClientTickrate(ticksPerSecond);
            send(
                sender,
                t("gtnl.command.tickrate.client_changed_prefix", 'a'),
                c(" " + ticksPerSecond, 'f'),
                t("gtnl.command.tickrate.period", 'a'));
        } else if (args[1].equalsIgnoreCase("server")) {
            TickrateAPI.changeServerTickrate(ticksPerSecond);
            send(
                sender,
                t("gtnl.command.tickrate.server_changed_prefix", 'a'),
                c(" " + ticksPerSecond, 'f'),
                t("gtnl.command.tickrate.period", 'a'));
        } else {
            EntityPlayer p = MinecraftServer.getServer()
                .getConfigurationManager()
                .func_152612_a(args[1]);
            if (p == null) {
                send(sender, t("gtnl.command.tickrate.player_not_found", 'c'));
                return;
            }
            TickrateAPI.changeClientTickrate(p, ticksPerSecond);
            send(
                sender,
                t("gtnl.command.tickrate.player_changed_prefix", new Object[] { p.getCommandSenderName() }, 'a'),
                c(" " + ticksPerSecond, 'f'),
                t("gtnl.command.tickrate.period", 'a'));
        }
    }

    public static void chat(ICommandSender sender, ChatComponentText... comps) {
        ChatComponentText top;
        if (comps.length == 1) {
            top = comps[0];
        } else {
            top = new ChatComponentText("");
            for (ChatComponentText c : comps) {
                top.appendSibling(c);
            }
        }
        sender.addChatMessage(top);
    }

    private static void send(ICommandSender sender, IChatComponent... components) {
        IChatComponent message = new ChatComponentText("");
        for (IChatComponent component : components) {
            message.appendSibling(component);
        }
        sender.addChatMessage(message);
    }

    private static ChatComponentText h(String text, IChatComponent[] hover, char... formatting) {
        ChatComponentText component = c(text, formatting);
        IChatComponent hoverComponent = new ChatComponentText("");
        for (IChatComponent part : hover) {
            hoverComponent.appendSibling(part);
        }
        component.setChatStyle(
            component.getChatStyle()
                .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverComponent)));
        return component;
    }

    private static ChatComponentTranslation t(String key, char... formatting) {
        return t(key, new Object[0], formatting);
    }

    private static ChatComponentTranslation t(String key, Object[] arguments, char... formatting) {
        ChatComponentTranslation component = new ChatComponentTranslation(key, arguments);
        ChatStyle style = component.getChatStyle();
        for (EnumChatFormatting formattingValue : resolveFormatting(formatting)) {
            applyFormatting(style, formattingValue);
        }
        component.setChatStyle(style);
        return component;
    }

    public static ChatComponentText c(String s, ChatComponentText[] hover, char... chars) {
        ChatComponentText c = c(s, chars);
        ChatComponentText hoverComp;
        if (hover.length == 1) {
            hoverComp = hover[0];
        } else {
            hoverComp = new ChatComponentText("");
            for (ChatComponentText txt : hover) {
                hoverComp.appendSibling(txt);
            }
        }
        c.setChatStyle(
            c.getChatStyle()
                .setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverComp)));
        return c;
    }

    public static ChatComponentText c(String s, char... chars) {
        return c(s, resolveFormatting(chars));
    }

    public static ChatComponentText c(String s, EnumChatFormatting... formattings) {
        ChatComponentText comp = new ChatComponentText(s);
        ChatStyle style = comp.getChatStyle();
        for (EnumChatFormatting f : formattings) {
            applyFormatting(style, f);
        }
        comp.setChatStyle(style);
        return comp;
    }

    private static EnumChatFormatting[] resolveFormatting(char... formatting) {
        EnumChatFormatting[] values = new EnumChatFormatting[formatting.length];
        for (int i = 0; i < formatting.length; i++) {
            for (EnumChatFormatting value : EnumChatFormatting.values()) {
                if (value.toString()
                    .equals("§" + formatting[i])) {
                    values[i] = value;
                    break;
                }
            }
        }
        return values;
    }

    private static void applyFormatting(ChatStyle style, EnumChatFormatting formatting) {
        if (formatting == EnumChatFormatting.BOLD) {
            style.setBold(true);
        } else if (formatting == EnumChatFormatting.ITALIC) {
            style.setItalic(true);
        } else if (formatting == EnumChatFormatting.UNDERLINE) {
            style.setUnderlined(true);
        } else {
            style.setColor(formatting);
        }
    }
}
