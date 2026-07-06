package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.api.implementations.IUpgradeableHost;
import appeng.api.implementations.guiobjects.INetworkTool;
import appeng.container.implementations.ContainerUpgradeable;

@Mixin(value = ContainerUpgradeable.class, remap = false)
public interface AccessorContainerUpgradeable {

    @Accessor("upgradeable")
    IUpgradeableHost getUpgradeable();

    @Accessor("tbSlot")
    int getTbSlot();

    @Accessor("tbInventory")
    INetworkTool getTbInventory();
}
