package com.science.gtnl.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;

public class AspectTooltipUtils {

    public static ItemStack createAspectStack(Aspect aspect, int amount) {
        ItemStack stack = new ItemStack(ModItems.itemAspect, amount, 0);
        ItemAspect.setAspect(stack, aspect);
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public static String getClientAspectDisplay(Aspect aspect, int amount) {
        ItemStack ariAspectStack = createAspectStack(aspect, 1);

        String ariDisplayName = ariAspectStack.getDisplayName();
        String unknownName = StatCollector.translateToLocal("tc.aspect.unknown");

        if (unknownName.equals(ariDisplayName)) {
            return StatCollector.translateToLocalFormatted("gtnl.gui.multi_essentia_jar.aspect_unknown", amount);
        }

        return StatCollector.translateToLocalFormatted(
            "gtnl.gui.multi_essentia_jar.aspect",
            aspect.getLocalizedDescription(),
            ariDisplayName,
            amount);
    }

    public static ChatComponentTranslation createServerAspectDisplay(EntityPlayer player, Aspect aspect, int amount) {
        boolean discovered = aspect != null
            && ThaumcraftApiHelper.hasDiscoveredAspect(player.getCommandSenderName(), aspect);

        if (!discovered) {
            return new ChatComponentTranslation("gtnl.gui.multi_essentia_jar.aspect_unknown", amount);
        }

        return new ChatComponentTranslation(
            "gtnl.gui.multi_essentia_jar.aspect",
            new ChatComponentTranslation("tc.aspect." + aspect.getTag()),
            aspect.getName(),
            amount);
    }

    public static void sendEmptyJarStatus(EntityPlayer player, int capacity) {
        player.addChatMessage(new ChatComponentTranslation("gtnl.chat.multi_essentia_jar.empty", capacity));
    }
}
