package com.science.gtnl.mixins.late.NotEnoughItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import codechicken.nei.ItemList;
import codechicken.nei.ItemPanel;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.RestartableTask;
import codechicken.nei.api.ItemFilter;

@Mixin(targets = "codechicken.nei.ItemList$3", remap = false)
public class MixinItemListUpdateFilter {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void gtnl$runSequentialFilter(CallbackInfo ci) {
        if (!ItemList.loadFinished) {
            ci.cancel();
            return;
        }

        ItemFilter filter = ItemList.getItemListFilter();
        ArrayList<net.minecraft.item.ItemStack> filtered;

        try {
            // NEI worker threads can race legacy class resolution on newer JVMs.
            filtered = ItemList.items.stream()
                .filter(filter::matches)
                .collect(Collectors.toCollection(ArrayList::new));
        } catch (RuntimeException exception) {
            NEIClientConfig.logger.error("Exception filtering item list sequentially", exception);
            ((RestartableTask) (Object) this).stop();
            ci.cancel();
            return;
        }

        if (((RestartableTask) (Object) this).interrupted()) {
            ci.cancel();
            return;
        }

        filtered.sort(Comparator.comparingInt(AccessorItemList.getOrdering()::get));

        if (((RestartableTask) (Object) this).interrupted()) {
            ci.cancel();
            return;
        }

        ItemPanel.updateItemList(filtered);
        ci.cancel();
    }
}
