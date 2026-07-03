package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiAmount;

@Mixin(value = GuiAmount.class, remap = false)
@Deprecated
public abstract class MixinGuiAmount extends AEBaseGui {

    public MixinGuiAmount(Container container) {
        super(container);
    }

    // TODO: Remove after GuiCraftAmount and GuiCraftConfirm return handling fully covers all AE amount screens.
}
