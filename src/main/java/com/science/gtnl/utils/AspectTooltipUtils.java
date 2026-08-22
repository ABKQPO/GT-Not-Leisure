package com.science.gtnl.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public final class AspectTooltipUtils {

    private AspectTooltipUtils() {}

    @SideOnly(Side.CLIENT)
    public static String getClientAspectDisplay(Aspect aspect, int amount) {
        ItemStack ariAspectStack = new ItemStack(ModItems.itemAspect);
        ItemAspect.setAspect(ariAspectStack, aspect);

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
}
