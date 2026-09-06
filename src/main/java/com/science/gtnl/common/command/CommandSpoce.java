package com.science.gtnl.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import com.science.gtnl.utils.render.SpoceEffect;

public class CommandSpoce extends CommandBase {

    private static final double DEF_RADIUS = 4.0;
    private static final int DEF_COLOR = 0x66CCFF;
    private static final float DEF_ALPHA = 0.5f;
    private static final boolean DEF_LINES = true;

    @Override
    public String getCommandName() {
        return "spoce";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.spoce.usage";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "test", "start", "stop");
        }
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "gtnl.command.spoce.usage_message", EnumChatFormatting.RED);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "stop" -> {
                SpoceEffect.clearAll();
                sendMessage(sender, "gtnl.command.spoce.cleared", EnumChatFormatting.GREEN);
            }

            case "test" -> {
                if (!(sender instanceof EntityPlayer player)) {
                    sendMessage(sender, "gtnl.command.spoce.player_only", EnumChatFormatting.RED);
                    return;
                }

                SpoceEffect testEffect = new SpoceEffect(player.posX, player.posY, player.posZ, 0.8f, 1.0f);
                testEffect.addLayer(4.0, 0x66CCFF, true);
                testEffect.addLayer(6.0, 0xFF9933, true);
                testEffect.addLayer(8.0, 0x66FF66, true);

                SpoceEffect.addEffect(testEffect);

                sendMessage(sender, "gtnl.command.spoce.test_loaded", EnumChatFormatting.AQUA);
            }

            case "start" -> {
                try {
                    int idx = 1;
                    double x, y, z;

                    if (args.length >= 4 && (args[1].startsWith("~") || args[1].equals("*") || isNumeric(args[1]))) {
                        double bx = (sender instanceof EntityPlayer) ? ((EntityPlayer) sender).posX : 0;
                        double by = (sender instanceof EntityPlayer) ? ((EntityPlayer) sender).posY : 0;
                        double bz = (sender instanceof EntityPlayer) ? ((EntityPlayer) sender).posZ : 0;

                        x = args[1].equals("*") ? bx : parseCoordinate(bx, args[1]);
                        y = args[2].equals("*") ? by : parseCoordinate(by, args[2]);
                        z = args[3].equals("*") ? bz : parseCoordinate(bz, args[3]);
                        idx = 4;
                    } else if (sender instanceof EntityPlayer player) {
                        x = player.posX;
                        y = player.posY;
                        z = player.posZ;
                    } else {
                        sendMessage(sender, "gtnl.command.spoce.non_player_coordinates", EnumChatFormatting.RED);
                        return;
                    }

                    int life = -1;
                    int end = args.length;

                    if (args.length > idx) {
                        String last = args[args.length - 1];

                        if (isNumeric(last)) {
                            life = (int) Double.parseDouble(last);
                            end--;
                        }
                    }

                    SpoceEffect effect = new SpoceEffect(x, y, z, 0.8f, 1.0f);
                    effect.setLife(life);

                    while (idx < end) {
                        String rStr = args[idx++];
                        double radius = rStr.equals("*") ? DEF_RADIUS : Double.parseDouble(rStr);

                        String cStr = (idx < end) ? args[idx++] : "*";
                        int color = cStr.equals("*") ? DEF_COLOR : parseColor(cStr);

                        String aStr = (idx < end) ? args[idx++] : "*";
                        float alpha = aStr.equals("*") ? DEF_ALPHA : Float.parseFloat(aStr);

                        String lStr = (idx < end) ? args[idx++] : "*";
                        boolean lines = lStr.equals("*") ? DEF_LINES : Boolean.parseBoolean(lStr);

                        SpoceEffect.Layer layer = new SpoceEffect.Layer(radius, color, lines);
                        layer.a = alpha;
                        effect.layers.add(layer);
                    }

                    if (effect.layers.isEmpty()) {
                        sendMessage(sender, "gtnl.command.spoce.no_layers", EnumChatFormatting.YELLOW);
                        effect.addLayer(DEF_RADIUS, DEF_COLOR, DEF_LINES);
                    }

                    if (life == 0) {
                        effect.remove();
                    }

                    SpoceEffect.addEffect(effect);

                    sendMessage(
                        sender,
                        "gtnl.command.spoce.started",
                        EnumChatFormatting.AQUA,
                        x,
                        y,
                        z,
                        effect.layers.size(),
                        life);

                } catch (Exception e) {
                    sendMessage(sender, "gtnl.command.spoce.parse_error", EnumChatFormatting.RED, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(ICommandSender sender, String key, EnumChatFormatting color, Object... arguments) {
        ChatComponentTranslation message = new ChatComponentTranslation(key, arguments);
        message.getChatStyle()
            .setColor(color);
        sender.addChatMessage(message);
    }

    public int parseColor(String s) {
        if (s.startsWith("#")) return (int) Long.parseLong(s.substring(1), 16);
        if (s.startsWith("0x")) return (int) Long.parseLong(s.substring(2), 16);
        return (int) Long.parseLong(s, 16);
    }

    public boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double parseCoordinate(double base, String arg) {
        if (arg.startsWith("~")) {
            return arg.length() == 1 ? base : base + Double.parseDouble(arg.substring(1));
        }
        return Double.parseDouble(arg);
    }
}
