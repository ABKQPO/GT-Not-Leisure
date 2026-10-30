package com.gtnllib.utils;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class MetaTooltipUtils {

    public static void appendTooltips(Int2ObjectMap<String[]> tooltipMap, int meta, List<String> tooltips) {
        appendTooltips(tooltipMap.get(meta), tooltips);
    }

    public static void appendTooltips(String[] values, List<String> tooltips) {
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value != null) {
                tooltips.add(value);
            }
        }
    }

    public static void registerIcons(IntSet metas, Int2ObjectMap<IIcon> iconMap, IIconRegister iconRegister,
        String iconPath) {
        iconMap.clear();
        for (int meta : metas) {
            iconMap.put(meta, iconRegister.registerIcon(iconPath + meta));
        }
    }

    public static IIcon getIcon(Int2ObjectMap<IIcon> iconMap, int meta) {
        IIcon icon = iconMap.get(meta);
        return icon != null ? icon : iconMap.get(0);
    }
}
