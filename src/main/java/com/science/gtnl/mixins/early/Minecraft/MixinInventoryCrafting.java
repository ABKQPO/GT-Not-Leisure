package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.science.gtnl.utils.LargeInventoryCrafting;

@Mixin(value = InventoryCrafting.class, remap = true)
public class MixinInventoryCrafting implements LargeInventoryCrafting {

    @Unique
    private long gtnl$assembler;

    @Intrinsic
    public void setAssemblerSize(long value) {
        gtnl$assembler = value;
    }

    @Intrinsic
    public long getAssemblerSize() {
        return gtnl$assembler;
    }
}
