package com.science.gtnl.common.machine.voidminer;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.mixinHelper.IVoidMinerDimensionOverride;

import galacticgreg.api.ModDimensionDef;

/**
 * What GTNL adds to every void miner through its mixin: the tweak's state, and access to the miner's private and
 * protected members for {@link VoidMinerHooks}.
 */
public interface IVoidMinerHost extends IVoidMinerDimensionOverride {

    VoidMinerState gtnl$state();

    byte gtnl$tierMultiplier();

    int gtnl$minTier();

    ItemStack gtnl$nextOre();

    ModDimensionDef gtnl$dimensionDef();

    void gtnl$setDimensionDef(ModDimensionDef dimensionDef);

    void gtnl$setCanVoidMine(boolean canVoidMine);

    int gtnl$multiplier();

    float gtnl$totalWeight();

    void gtnl$setTotalWeight(float totalWeight);
}
