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

public final class AspectTooltipUtils {

    private AspectTooltipUtils() {}

    // 创建一枚携带指定源质（及数量）的 ARI 展示 ItemStack。
    // 供客户端 tooltip 与服务端配方 special 槽共用，避免两处各自 new stack。
    public static ItemStack createAspectStack(Aspect aspect, int amount) {
        ItemStack stack = new ItemStack(ModItems.itemAspect, amount, 1);
        ItemAspect.setAspect(stack, aspect);
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public static String getClientAspectDisplay(Aspect aspect, int amount) {
        ItemStack ariAspectStack = createAspectStack(aspect, 1);

        // 由 ARI 判断并返回要素名或"未知要素"
        String ariDisplayName = ariAspectStack.getDisplayName();
        String unknownName = StatCollector.translateToLocal("tc.aspect.unknown");

        if (unknownName.equals(ariDisplayName)) {
            return StatCollector.translateToLocalFormatted("GTNL.gui.multi_essentia_jar.aspect_unknown", amount);
        }

        return StatCollector.translateToLocalFormatted(
            "GTNL.gui.multi_essentia_jar.aspect",
            aspect.getLocalizedDescription(),
            ariDisplayName,
            amount);
    }

    // 服务端聊天展示：已发现要素 → 本地化名+数量；未发现 → 未知。
    // 供方块与手持物品的状态消息共用，避免两处各自实现（消除 Block → ItemBlock 反向依赖）。
    public static ChatComponentTranslation createServerAspectDisplay(EntityPlayer player, Aspect aspect, int amount) {
        boolean discovered = aspect != null
            && ThaumcraftApiHelper.hasDiscoveredAspect(player.getCommandSenderName(), aspect);

        if (!discovered) {
            return new ChatComponentTranslation("GTNL.gui.multi_essentia_jar.aspect_unknown", amount);
        }

        return new ChatComponentTranslation(
            "GTNL.gui.multi_essentia_jar.aspect",
            new ChatComponentTranslation("tc.aspect." + aspect.getTag()),
            aspect.getName(),
            amount);
    }

    // 空罐聊天提示（方块 / 手持物品 / 拾取三处共用，语言键只保留一处拼装）。
    public static void sendEmptyJarStatus(EntityPlayer player, int capacity) {
        player.addChatMessage(new ChatComponentTranslation("Info_MultiEssentiaJar_Empty", capacity));
    }
}
