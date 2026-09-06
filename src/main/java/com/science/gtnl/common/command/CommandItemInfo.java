package com.science.gtnl.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandItemInfo extends CommandBase {

    @Override
    public String getCommandName() {
        return "getiteminfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "gtnl.command.item_info.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer player)) {
            sender.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.player_only"));
            return;
        }

        ItemStack stack = player.getHeldItem();

        if (stack == null) {
            player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.no_item"));
            return;
        }

        Item item = stack.getItem();

        if (item == null) {
            player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.no_item"));
            return;
        }

        player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.header"));
        player.addChatMessage(new ChatComponentText(stack.toString()));
        player.addChatMessage(
            new ChatComponentTranslation("gtnl.command.item_info.display_name", stack.getDisplayName()));
        player.addChatMessage(
            new ChatComponentTranslation(
                "gtnl.command.item_info.item_stack_display_name",
                item.getItemStackDisplayName(stack)));
        player.addChatMessage(
            new ChatComponentTranslation(
                "gtnl.command.item_info.unlocalized_inefficient_name",
                item.getUnlocalizedNameInefficiently(stack)));
        player.addChatMessage(
            new ChatComponentTranslation("gtnl.command.item_info.unlocalized_name", stack.getUnlocalizedName()));
        player.addChatMessage(
            new ChatComponentTranslation(
                "gtnl.command.item_info.item_stack_unlocalized_name",
                item.getUnlocalizedName(stack)));
        player.addChatMessage(
            new ChatComponentTranslation(
                "gtnl.command.item_info.class",
                item.getClass()
                    .getName()));
        player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.id", Item.getIdFromItem(item)));
        player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.stack_size", stack.stackSize));
        player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.damage", stack.getItemDamage()));
        player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.max_damage", stack.getMaxDamage()));

        NBTTagCompound nbt = stack.stackTagCompound;
        if (nbt != null) {
            player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.nbt_data"));
            for (String keyObj : nbt.func_150296_c()) {
                player.addChatMessage(
                    new ChatComponentTranslation("gtnl.command.item_info.nbt_entry", keyObj, nbt.getTag(keyObj)));
            }
        } else {
            player.addChatMessage(new ChatComponentTranslation("gtnl.command.item_info.no_nbt_data"));
        }
    }

}
