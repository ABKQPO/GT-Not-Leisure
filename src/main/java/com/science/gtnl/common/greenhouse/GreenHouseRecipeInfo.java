package com.science.gtnl.common.greenhouse;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class GreenHouseRecipeInfo {

    private GreenHouseRecipeInfo() {}

    public static String describe(int progressTime, int maxProgressTime, ItemStack[] outputItems,
        Set<Map.Entry<ItemStack, Double>> drops) {
        double maxProgressSeconds = safeMaxProgressSeconds(maxProgressTime);
        double progressPercent = safeProgressPercent(progressTime, maxProgressTime);
        StringBuilder ret = new StringBuilder(
            EnumChatFormatting.WHITE + StatCollector.translateToLocal("GT5U.gui.text.progress"))
                .append(String.format("%,.2f", (double) progressTime / 20))
                .append("s / ")
                .append(String.format("%,.2f", maxProgressSeconds))
                .append("s (")
                .append(String.format("%,.1f", progressPercent))
                .append("%)\n");
        Object2IntOpenHashMap<ItemId> outputCounts = outputItemCounts(outputItems);
        ObjectArrayList<Map.Entry<ItemStack, Double>> sortedDrops = new ObjectArrayList<>(drops);
        sortedDrops.sort(
            Comparator.comparing(
                a -> a.getKey()
                    .toString()
                    .toLowerCase()));

        for (Map.Entry<ItemStack, Double> drop : sortedDrops) {
            int outputSize = outputCounts.getInt(ItemId.createNoCopy(drop.getKey()));
            ret.append(EnumChatFormatting.AQUA)
                .append(
                    drop.getKey()
                        .getDisplayName())
                .append(EnumChatFormatting.WHITE)
                .append(": ");
            if (outputSize == 0) {
                ret.append(String.format("%.2f", drop.getValue() * 100))
                    .append("%\n");
            } else {
                ret.append(EnumChatFormatting.GOLD)
                    .append(
                        String.format(
                            "x%d %s(+%.2f/s)\n",
                            outputSize,
                            EnumChatFormatting.WHITE,
                            outputSize / maxProgressSeconds));
            }
        }
        return ret.toString();
    }

    public static double safeMaxProgressSeconds(int maxProgressTime) {
        return Math.max(maxProgressTime / 20.0D, 0.05D);
    }

    public static double safeProgressPercent(int progressTime, int maxProgressTime) {
        if (maxProgressTime <= 0) return 0;
        return (double) progressTime / maxProgressTime * 100;
    }

    public static Object2IntOpenHashMap<ItemId> outputItemCounts(ItemStack[] outputItems) {
        Object2IntOpenHashMap<ItemId> outputCounts = new Object2IntOpenHashMap<>(
            outputItems == null ? 0 : outputItems.length);
        if (outputItems == null) return outputCounts;
        for (ItemStack outputItem : outputItems) {
            if (outputItem != null && outputItem.stackSize > 0) {
                outputCounts.addTo(ItemId.createNoCopy(outputItem), outputItem.stackSize);
            }
        }
        return outputCounts;
    }
}
