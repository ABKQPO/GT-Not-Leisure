package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.science.gtnl.utils.LargeInventoryCrafting;

@Mixin(InventoryCrafting.class)
public class MixinInventoryCrafting implements LargeInventoryCrafting {

    @Unique
    private long snl$assembler;

    @Intrinsic
    public void setAssemblerSize(long value) {
        snl$assembler = value;
    }

    @Intrinsic
    public long getAssemblerSize() {
        return snl$assembler;
    }
}
