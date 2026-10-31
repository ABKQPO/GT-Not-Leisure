package com.science.gtnl.compat.nei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import com.science.gtnl.mixins.late.notEnoughItems.AccessorItemList;

import codechicken.nei.ItemList;
import codechicken.nei.ItemPanel;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.RestartableTask;
import codechicken.nei.api.ItemFilter;

public class NEIItemListFilter {

    public static void filter(RestartableTask task) {
        if (!ItemList.loadFinished) return;

        ItemFilter filter = ItemList.getItemListFilter();
        ArrayList<ItemStack> filtered;

        try {
            // NEI worker threads can race legacy class resolution on newer JVMs.
            filtered = ItemList.items.stream()
                .filter(filter::matches)
                .collect(Collectors.toCollection(ArrayList::new));
        } catch (RuntimeException exception) {
            NEIClientConfig.logger.error("Exception filtering item list sequentially", exception);
            task.stop();
            return;
        }

        if (task.interrupted()) {
            return;
        }

        filtered.sort(
            Comparator.comparingInt(
                a -> AccessorItemList.getItems()
                    .indexOf(a)));

        if (task.interrupted()) {
            return;
        }

        ItemPanel.updateItemList(filtered);
    }
}
