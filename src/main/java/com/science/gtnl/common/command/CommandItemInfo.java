package com.science.gtnl.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class CommandItemInfo extends CommandBase {

    @Override
    public String getCommandName() {
        return "getiteminfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getiteminfo - Display all info about the item in your hand";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer player)) {
            sender.addChatMessage(new ChatComponentText("This command can only be used by players."));
            return;
        }

        ItemStack stack = player.getHeldItem();

        if (stack == null) {
            player.addChatMessage(new ChatComponentText("You are not holding any item."));
            return;
        }

        Item item = stack.getItem();

        if (item == null) {
            player.addChatMessage(new ChatComponentText("You are not holding any item."));
            return;
        }

        player.addChatMessage(new ChatComponentText("===== Item Info ====="));
        player.addChatMessage(new ChatComponentText(stack.toString()));
        player.addChatMessage(new ChatComponentText("Display Name: " + stack.getDisplayName()));
        player.addChatMessage(new ChatComponentText("ItemStack Display Name: " + item.getItemStackDisplayName(stack)));
        player.addChatMessage(
            new ChatComponentText("Unlocalized Inefficiently Name: " + item.getUnlocalizedNameInefficiently(stack)));
        player.addChatMessage(new ChatComponentText("Unlocalized Name: " + stack.getUnlocalizedName()));
        player.addChatMessage(new ChatComponentText("ItemStack Unlocalized Name: " + item.getUnlocalizedName(stack)));
        player.addChatMessage(
            new ChatComponentText(
                "Class: " + item.getClass()
                    .getName()));
        player.addChatMessage(new ChatComponentText("ID: " + Item.getIdFromItem(item)));
        player.addChatMessage(new ChatComponentText("Stack Size: " + stack.stackSize));
        player.addChatMessage(new ChatComponentText("Damage: " + stack.getItemDamage()));
        player.addChatMessage(new ChatComponentText("Max Damage: " + stack.getMaxDamage()));

        NBTTagCompound nbt = stack.stackTagCompound;
        if (nbt != null) {
            player.addChatMessage(new ChatComponentText("NBT Data:"));
            for (String keyObj : nbt.func_150296_c()) {
                player.addChatMessage(new ChatComponentText("  " + keyObj + " : " + nbt.getTag(keyObj)));
            }
        } else {
            player.addChatMessage(new ChatComponentText("No NBT data."));
        }
    }

}
