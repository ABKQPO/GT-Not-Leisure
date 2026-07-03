package com.science.gtnl.mixins.late.enhancedLootBags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import eu.usrv.enhancedlootbags.core.LootGroupsHandler;
import eu.usrv.enhancedlootbags.core.items.ItemLootBag;

@Mixin(value = ItemLootBag.class, remap = false)
public interface AccessorItemLootBag {

    @Accessor("_mLGHandler")
    LootGroupsHandler getLGHandler();
}
